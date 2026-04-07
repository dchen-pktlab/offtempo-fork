package burp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private final JCheckBox enableCheckbox;
    private final JComboBox<String> typeSelector;
    private final JLabel sampleCountLabel;

    public ControlPanel(ActionListener helpAction, ActionListener clearAction) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        enableCheckbox = new JCheckBox("Enable timing capture");
        JLabel addLabel = new JLabel("→ Add results to: ");
        typeSelector = new JComboBox<>(new String[]{"Pool A", "Pool B"});

        JButton helpButton = new JButton("?");
        helpButton.setMargin(new Insets(2, 5, 2, 5));
        helpButton.addActionListener(helpAction);

        sampleCountLabel = new JLabel("Pool A: 0  |  Pool B: 0");
        sampleCountLabel.setForeground(Color.GRAY);

        leftPanel.add(enableCheckbox);
        leftPanel.add(addLabel);
        leftPanel.add(typeSelector);
        leftPanel.add(helpButton);
        leftPanel.add(Box.createHorizontalStrut(12));
        leftPanel.add(sampleCountLabel);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(clearAction);

        add(leftPanel, BorderLayout.WEST);
        add(clearButton, BorderLayout.EAST);
    }

    public boolean isCaptureEnabled() { return enableCheckbox.isSelected(); }
    public String getSelectedType() { return (String) typeSelector.getSelectedItem(); }

    public void updateCounts(int poolA, int poolB) {
        sampleCountLabel.setText("Pool A: " + poolA + "  |  Pool B: " + poolB);
    }
}
