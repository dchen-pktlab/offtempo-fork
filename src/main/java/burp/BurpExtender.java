package burp;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class BurpExtender implements BurpExtension, ExtensionUnloadingHandler, ContextMenuItemsProvider, HttpHandler
{
    public static final String EXTENSION_VERSION = "0.1.0";
    public static final String TAB_NAME = "Miao";
    public static final String EXTENSION_NAME = "Miao";

    public Map<Integer, HttpRequestWithTimestamp> requestMap = new HashMap<>();

    private MontoyaApi api;

    private static PrintWriter out;
    private static PrintWriter err;

    // UI elements
    private JScrollPane outerScrollPane;
    private JCheckBox proxySigningEnabledCheckBox;
    private JCheckBox useSuiteScopeCheckBox;
    private JCheckBox repeaterSigningEnabledCheckBox;
    private JTextField pathTextField;
    private JTextField apiSecretKeyTextField;
    private JTextField apiKeyIdTextField;
    private JTextField timestampTextField;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;

        api.extension().setName(EXTENSION_NAME);
        out = new PrintWriter(api.logging().output(), true);
        err = new PrintWriter(api.logging().error(), true);

        SwingUtilities.invokeLater(() -> {
            buildMainTab();
            api.extension().registerUnloadingHandler(BurpExtender.this);
            api.http().registerHttpHandler(BurpExtender.this);
            api.userInterface().registerSuiteTab(TAB_NAME, outerScrollPane);
        });
    }

    public static void info(final String msg)
    {
        out.println(msg);
    }

    public static void debug(final String msg)
    {
        out.println(msg);
    }

    public static void error(final String msg)
    {
        err.println(msg);
    }

    @Override
    public void extensionUnloaded() {
        // save extension version directly in Burp in case json settings fail
        api.persistence().extensionData().setString("ExtensionVersion", EXTENSION_VERSION);
        final Settings savedSettings = Settings.builder()
                .proxySigningEnabled(proxySigningEnabledCheckBox.isSelected())
                .useSuiteScope(useSuiteScopeCheckBox.isSelected())
                .repeaterSigningEnabled(repeaterSigningEnabledCheckBox.isSelected())
                .extensionVersion(EXTENSION_VERSION)
                .build();
        final String settingsJson = new GsonBuilder()
                //.setPrettyPrinting()
                .setVersion(savedSettings.version())
                .create()
                .toJson(savedSettings, Settings.class);
        api.persistence().extensionData().setString("ExtensionSettings", settingsJson);
        api.persistence().extensionData().setString("SettingsVersion", Float.toString(savedSettings.version()));
    }

    private static GridBagConstraints newConstraint(int gridx, int gridy, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = gridy;
        c.gridx = gridx;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = anchor;
        c.insets = new Insets(3, 3, 3, 3);
        return c;
    }

    private void buildMainTab() {

        // when a text field loses focus, strip whitespace
        FocusListener textFieldFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                JTextField textField = (JTextField)focusEvent.getSource();
                textField.setText(textField.getText().trim());
            }
        };

        final int TEXT_FIELD_WIDTH = 40;

        // settings
        JPanel settingsPanel = new JPanel(new GridBagLayout());

        proxySigningEnabledCheckBox = new JCheckBox("Proxy signing");
        useSuiteScopeCheckBox = new JCheckBox("Use suite scope");
        repeaterSigningEnabledCheckBox = new JCheckBox("Repeater signing");

        pathTextField = new JTextField(TEXT_FIELD_WIDTH);
        pathTextField.addFocusListener(textFieldFocusListener);
        apiKeyIdTextField = new JTextField(TEXT_FIELD_WIDTH);
        apiKeyIdTextField.addFocusListener(textFieldFocusListener);
        apiSecretKeyTextField = new JTextField(TEXT_FIELD_WIDTH);
        apiSecretKeyTextField.addFocusListener(textFieldFocusListener);
        timestampTextField = new JTextField(TEXT_FIELD_WIDTH);
        timestampTextField.addFocusListener(textFieldFocusListener);

        int outerPanelY = 0;
        JPanel outerPanel = new JPanel(new GridBagLayout());

        // add a title
        var titleLabel =  new JLabel(EXTENSION_NAME);
        titleLabel.setFont(new Font(titleLabel.getFont().getFamily(), Font.BOLD, titleLabel.getFont().getSize()));
        outerPanel.add(titleLabel, newConstraint(0, outerPanelY++, GridBagConstraints.LINE_START));
        GridBagConstraints c000 = newConstraint(0, outerPanelY++, GridBagConstraints.CENTER);
        c000.fill = GridBagConstraints.HORIZONTAL;
        outerPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c000);

        // top level settings panel. check boxes laid out left to right with wrap enabled.
        JPanel globalSettingsPanel = new JPanel(new FlowLayout());
        globalSettingsPanel.add(proxySigningEnabledCheckBox);
        globalSettingsPanel.add(useSuiteScopeCheckBox);
        globalSettingsPanel.add(repeaterSigningEnabledCheckBox);
        outerPanel.add(globalSettingsPanel, newConstraint(0, outerPanelY++, GridBagConstraints.LINE_START));
        GridBagConstraints c001 = newConstraint(0, outerPanelY++, GridBagConstraints.CENTER);
        c001.fill = GridBagConstraints.HORIZONTAL;
        outerPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c001);

        // construct text fields panel
        int settingsPanelY = 0;

        settingsPanel.add(new JLabel("Path"), newConstraint(0, settingsPanelY, GridBagConstraints.LINE_START));
        settingsPanel.add(pathTextField, newConstraint(1, settingsPanelY++, GridBagConstraints.LINE_START));

        settingsPanel.add(new JLabel("KeyId"), newConstraint(0, settingsPanelY, GridBagConstraints.LINE_START));
        settingsPanel.add(apiKeyIdTextField, newConstraint(1, settingsPanelY++, GridBagConstraints.LINE_START));

        settingsPanel.add(new JLabel("Secret"), newConstraint(0, settingsPanelY, GridBagConstraints.LINE_START));
        settingsPanel.add(apiSecretKeyTextField, newConstraint(1, settingsPanelY++, GridBagConstraints.LINE_START));

        settingsPanel.add(new JLabel("Timestamp"), newConstraint(0, settingsPanelY, GridBagConstraints.LINE_START));
        settingsPanel.add(timestampTextField, newConstraint(1, settingsPanelY++, GridBagConstraints.LINE_START));

        outerPanel.add(settingsPanel, newConstraint(0, outerPanelY++, GridBagConstraints.FIRST_LINE_START));

        // add help text
        GridBagConstraints c002 = newConstraint(0, outerPanelY++, GridBagConstraints.CENTER);
        c002.fill = GridBagConstraints.HORIZONTAL;
        outerPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c002);

        // add help text
        JPanel helpPanel = new HelpPanel();
        helpPanel.setBorder(new TitledBorder(""));
        outerPanel.add(helpPanel, newConstraint(0, outerPanelY, GridBagConstraints.FIRST_LINE_START));

        // get content into upper left of panel
        JPanel outerNorthPanel = new JPanel(new BorderLayout());
        JPanel outerWestPanel = new JPanel(new BorderLayout());
        outerNorthPanel.add(outerPanel, BorderLayout.PAGE_START);
        outerWestPanel.add(outerNorthPanel, BorderLayout.LINE_START);
        outerScrollPane = new JScrollPane(outerWestPanel);
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        if (requestToBeSent.toolSource().isFromTool(ToolType.INTRUDER)) {
            requestMap.put(requestToBeSent.messageId(), new HttpRequestWithTimestamp(
                    requestToBeSent.messageId(), System.currentTimeMillis()
            ));
        }
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (responseReceived.toolSource().isFromTool(ToolType.INTRUDER)) {
            HttpRequestWithTimestamp r = requestMap.get(responseReceived.messageId());
            if (r != null) {
                final long elapsed = System.currentTimeMillis() - r.sendTimestamp;
                info(String.format("[%d] Elapsed %d millis", r.burpMessageId, elapsed));
            }
            return null;
        }
        return null;
    }

}
