package burp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private final JCheckBox enableCheckbox;
    private final JComboBox<String> typeSelector;

    public ControlPanel(ActionListener helpAction, ActionListener clearAction) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enableCheckbox = new JCheckBox("Enable timing capture");
        enableCheckbox.setSelected(false);

        typeSelector = new JComboBox<>(new String[]{"Existing resource", "Non-existing resource"});

        JButton helpButton = new JButton("?");
        helpButton.setMargin(new Insets(2, 5, 2, 5));
        helpButton.addActionListener(helpAction);

        leftPanel.add(enableCheckbox);
        leftPanel.add(new JLabel("→ Add results to: "));
        leftPanel.add(typeSelector);
        leftPanel.add(helpButton);

        JButton clearButton = new JButton("Clear Results");
        clearButton.addActionListener(clearAction);

        add(leftPanel, BorderLayout.WEST);
        add(clearButton, BorderLayout.EAST);
    }

    public boolean isCaptureEnabled() { return enableCheckbox.isSelected(); }
    public String getSelectedType() { return (String) typeSelector.getSelectedItem(); }
}
