package burp.ui;

import burp.model.TimingAnalysisResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnalysisPanel extends JPanel {

    private final AucPanel aucPanel;
    private final AddStatsPanel statsPanel;

    private JButton saveBtn;

    public AnalysisPanel(ActionListener runAction, ActionListener plotAction, ActionListener saveAction) {
        super(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton runBtn = new JButton("Run");
        runBtn.addActionListener(e -> {
            runAction.actionPerformed(e);
            plotAction.actionPerformed(e);
            saveBtn.setVisible(true);
        });

        runBtn.setBackground(new Color(0xDD5D33));
        runBtn.setForeground(Color.WHITE);
        runBtn.setFont(runBtn.getFont().deriveFont(Font.BOLD));
        runBtn.setOpaque(true);
        runBtn.setBorderPainted(false);
        buttonPanel.add(runBtn);

        buttonPanel.add(Box.createHorizontalStrut(4));

        saveBtn = new JButton("Save Plot");
        saveBtn.addActionListener(saveAction);
        saveBtn.setVisible(false);
        buttonPanel.add(saveBtn);

        add(buttonPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        aucPanel = new AucPanel();
        statsPanel = new AddStatsPanel();

        tabbedPane.addTab("AUC", aucPanel);
        tabbedPane.addTab("Additional statistics", statsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void showResult(TimingAnalysisResult result) {
        aucPanel.showResult(result);
        statsPanel.showResult(result);
    }

    public void clearResult() {
        aucPanel.clear();
        statsPanel.clear();
        saveBtn.setVisible(false);
    }
}
