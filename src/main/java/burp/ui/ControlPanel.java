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

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        enableCheckbox = new JCheckBox("Enable timing capture");
        enableCheckbox.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel addLabel = new JLabel("→ Add results to: ");
        addLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        typeSelector = new JComboBox<>(new String[]{"Pool A", "Pool B"});
        typeSelector.setAlignmentY(Component.CENTER_ALIGNMENT);

        JButton helpButton = new JButton("?");
        helpButton.setMargin(new Insets(2, 5, 2, 5));
        helpButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        helpButton.addActionListener(helpAction);

        leftPanel.add(enableCheckbox);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(addLabel);
        leftPanel.add(typeSelector);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(helpButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(clearAction);

        add(leftPanel, BorderLayout.WEST);
        add(clearButton, BorderLayout.EAST);
    }

    public boolean isCaptureEnabled() { return enableCheckbox.isSelected(); }
    public String getSelectedType() { return (String) typeSelector.getSelectedItem(); }
}
