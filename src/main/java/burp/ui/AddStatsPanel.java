package burp.ui;

import burp.model.PoolStats;
import burp.model.TimingAnalysisResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddStatsPanel extends JPanel {

    public AddStatsPanel() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    public void showResult(TimingAnalysisResult result) {
        removeAll();
        if (result == null) {
            add(new JLabel("No data available"), BorderLayout.CENTER);
        } else {
            JPanel horizontalPanel = new JPanel();
            horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
            horizontalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            horizontalPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            horizontalPanel.add(createPlotStatsPanel("Pool A", result.getPlotAStats()));
            horizontalPanel.add(Box.createRigidArea(new Dimension(30, 0)));
            horizontalPanel.add(createPlotStatsPanel("Pool B", result.getPlotBStats()));
            horizontalPanel.add(Box.createRigidArea(new Dimension(30, 0)));
            horizontalPanel.add(createGlobalStatsPanel("Global statistics", result));

            add(horizontalPanel, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    private JPanel createPlotStatsPanel(String title, PoolStats stats) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(title));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(new JLabel("Count: " + stats.getCount()));
        panel.add(new JLabel("Mean: " + String.format("%.2f", stats.getMean())));
        panel.add(new JLabel("Median: " + String.format("%.2f", stats.getMedian())));
        panel.add(new JLabel("Std Dev: " + String.format("%.2f", stats.getStdDev())));
        panel.add(new JLabel("Min: " + String.format("%.2f", stats.getMin())));
        panel.add(new JLabel("Max: " + String.format("%.2f", stats.getMax())));

        return panel;
    }

    private JPanel createGlobalStatsPanel(String title, TimingAnalysisResult result) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(title));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(new JLabel("U statistic: " + String.format("%.2f", result.getUStatistic())));
        panel.add(new JLabel("Cohen's d (effect size): " + String.format("%.2f", result.getCohensD())));
        panel.add(new JLabel("p-value: " + formatPValue(result.getPValue())));

        return panel;
    }

    private String formatPValue(double p) {
        return p < 0.0001 ? String.format("%.2e", p) : String.format("%.4f", p);
    }

    public void clear() {
        removeAll();
        revalidate();
        repaint();
    }
}
