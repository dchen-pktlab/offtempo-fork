package burp;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class BurpExtender implements BurpExtension, ExtensionUnloadingHandler, ContextMenuItemsProvider, HttpHandler {
    public static final String EXTENSION_VERSION = "0.1.0";
    public static final String TAB_NAME = "Miao";
    public static final String EXTENSION_NAME = "Miao";

    private final Map<Integer, HttpRequestWithTimestamp> existingRequestMap = new ConcurrentHashMap<>(); // NEW
    private final Map<Integer, HttpRequestWithTimestamp> nonExistingRequestMap = new ConcurrentHashMap<>(); // NEW
    private TimingTableModel existingResourceModel;
    private TimingTableModel nonExistingResourceModel;

    private MontoyaApi api;

    private static PrintWriter out;
    private static PrintWriter err;

    private JCheckBox enableCheckbox;
    private JComboBox<String> typeSelector;

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;

        api.extension().setName(EXTENSION_NAME);
        out = new PrintWriter(api.logging().output(), true);
        err = new PrintWriter(api.logging().error(), true);

        SwingUtilities.invokeLater(() -> {
            setupUI(api.userInterface());
            api.extension().registerUnloadingHandler(BurpExtender.this);
            api.http().registerHttpHandler(BurpExtender.this);
        });
    }

    private void setupUI(UserInterface ui) {

        existingResourceModel = new TimingTableModel();
        nonExistingResourceModel = new TimingTableModel();

        JTable existingTable = new JTable(existingResourceModel);
        JTable nonExistingTable = new JTable(nonExistingResourceModel);

        existingTable.setRowSorter(new TableRowSorter<>(existingResourceModel));
        nonExistingTable.setRowSorter(new TableRowSorter<>(nonExistingResourceModel));

        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        enableCheckbox = new JCheckBox("Enable timing capture");
        enableCheckbox.setSelected(false);

        typeSelector = new JComboBox<>(new String[]{
                "Existing resource",
                "Non-existing resource"
        });

        // --- NEW: Help button and label ---
        JLabel helpLabel = new JLabel("Why is the time different than what I see in Intruder?");
        JButton helpButton = new JButton("?");
        helpButton.setMargin(new Insets(2, 5, 2, 5));
        helpButton.addActionListener(e -> {
            HelpPanel helpPanel = new HelpPanel();
            JOptionPane.showMessageDialog(controlPanel, helpPanel, "Timing Info Help", JOptionPane.INFORMATION_MESSAGE);
        });

        controlPanel.add(enableCheckbox);
        controlPanel.add(new JLabel("→ Add results to: "));
        controlPanel.add(typeSelector);
        controlPanel.add(helpLabel);
        controlPanel.add(helpButton);

        // --- Create scroll panes with padding ---
        JScrollPane existingScroll = new JScrollPane(existingTable);
        existingScroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane nonExistingScroll = new JScrollPane(nonExistingTable);
        nonExistingScroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Panels to control preferred height ---
        JPanel existingPanel = new JPanel(new BorderLayout());
        existingPanel.add(existingScroll, BorderLayout.CENTER);
        existingPanel.setPreferredSize(new Dimension(0, 300)); // ~50% of typical vertical space

        JPanel nonExistingPanel = new JPanel(new BorderLayout());
        nonExistingPanel.add(nonExistingScroll, BorderLayout.CENTER);
        nonExistingPanel.setPreferredSize(new Dimension(0, 300));

        // --- Split pane ---
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                existingPanel,
                nonExistingPanel
        );
        split.setResizeWeight(0.5);

        // --- Panel below the tables with label and button ---
        JPanel mannWhitneyPanel = new JPanel();
        mannWhitneyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel("Mann–Whitney U test");
        JButton button = new JButton("Run Test");
        button.addActionListener(e -> performMannWhitneyUTest()); // NEW: calls new function
        mannWhitneyPanel.add(label);
        mannWhitneyPanel.add(button);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(split, BorderLayout.CENTER);
        mainPanel.add(mannWhitneyPanel, BorderLayout.SOUTH);

        ui.registerSuiteTab(TAB_NAME, mainPanel);
    }

    public static void info(final String msg) {
        out.println(msg);
    }

    public static void debug(final String msg) {
        out.println(msg);
    }

    public static void error(final String msg) {
        err.println(msg);
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        if (requestToBeSent.toolSource().isFromTool(ToolType.INTRUDER) && enableCheckbox.isSelected()) {
            String selected = (String) typeSelector.getSelectedItem();
            if ("Existing resource".equals(selected)) {
                existingRequestMap.put(requestToBeSent.messageId(),
                        new HttpRequestWithTimestamp(requestToBeSent.messageId(), System.currentTimeMillis()));
            } else {
                nonExistingRequestMap.put(requestToBeSent.messageId(),
                        new HttpRequestWithTimestamp(requestToBeSent.messageId(), System.currentTimeMillis()));
            }
        }
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        if (responseReceived.toolSource().isFromTool(ToolType.INTRUDER) && enableCheckbox.isSelected()) {
            String selected = (String) typeSelector.getSelectedItem();
            HttpRequestWithTimestamp r;
            if ("Existing resource".equals(selected)) {
                r = existingRequestMap.get(responseReceived.messageId());
            } else {
                r = nonExistingRequestMap.get(responseReceived.messageId());
            }
            if (r == null) {
                return ResponseReceivedAction.continueWith(responseReceived);
            } else {
                final long elapsed = System.currentTimeMillis() - r.sendTimestamp;
                info(String.format("[%d] Elapsed %d millis", r.burpMessageId, elapsed));

                SwingUtilities.invokeLater(() -> {
                    if ("Existing resource".equals(selected)) {
                        existingResourceModel.addTiming(r, elapsed);
                    } else {
                        nonExistingResourceModel.addTiming(r, elapsed);
                    }
                });
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }

    @Override
    public void extensionUnloaded() {

    }

    private void performMannWhitneyUTest() {

    }
}