package burp.ui;

import burp.model.PoolStats;
import burp.model.TimingAnalysisResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
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
            PoolStats a = result.getPlotAStats();
            PoolStats b = result.getPlotBStats();

            String[][] leftRows = {
                { "Count",   String.valueOf(a.getCount()), String.valueOf(b.getCount()) },
                { "Mean",    fmt(a.getMean()) + " ms",     fmt(b.getMean()) + " ms" },
                { "Median",  fmt(a.getMedian()) + " ms",   fmt(b.getMedian()) + " ms" },
                { "Std Dev", fmt(a.getStdDev()) + " ms",   fmt(b.getStdDev()) + " ms" },
            };
            String[][] rightRows = {
                { "p95", fmt(a.getP95()) + " ms", fmt(b.getP95()) + " ms" },
                { "p99", fmt(a.getP99()) + " ms", fmt(b.getP99()) + " ms" },
                { "Min", fmt(a.getMin()) + " ms", fmt(b.getMin()) + " ms" },
                { "Max", fmt(a.getMax()) + " ms", fmt(b.getMax()) + " ms" },
            };

            JPanel content = new JPanel(new GridLayout(1, 3, 0, 0));
            content.add(buildTable(leftRows));
            content.add(buildTable(rightRows));
            content.add(buildGlobalStats(result));
            add(content, BorderLayout.NORTH);
        }
        revalidate();
        repaint();
    }

    private JPanel buildTable(String[][] rows) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 8, 2, 8);
        c.weightx = 0;

        addHeaderCell(panel, "",       c, 0, 0);
        addHeaderCell(panel, "Pool A", c, 1, 0);
        addHeaderCell(panel, "Pool B", c, 2, 0);

        for (int i = 0; i < rows.length; i++) {
            addCell(panel, rows[i][0], c, 0, i + 1, true);
            addCell(panel, rows[i][1], c, 1, i + 1, false);
            addCell(panel, rows[i][2], c, 2, i + 1, false);
        }

        // filler to prevent GridBagLayout from centering content
        c.gridx = 3; c.gridy = 0; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(), c);

        return panel;
    }

    private void addHeaderCell(JPanel panel, String text, GridBagConstraints c, int col, int row) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        c.gridx = col; c.gridy = row; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(label, c);
    }

    private void addCell(JPanel panel, String text, GridBagConstraints c, int col, int row, boolean bold) {
        JLabel label = new JLabel(text);
        if (bold) label.setFont(label.getFont().deriveFont(Font.BOLD));
        c.gridx = col; c.gridy = row; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        panel.add(label, c);
    }

    private JPanel buildGlobalStats(TimingAnalysisResult result) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));

        panel.add(Box.createVerticalGlue());
        panel.add(createStatCard("U statistic",               String.format("%.2f", result.getUStatistic())));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(createStatCard("Cohen's d (effect size)",   String.format("%.2f", result.getCohensD())));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(createStatCard("p-value",                   formatPValue(result.getPValue())));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(0, 20, 0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setForeground(Color.GRAY);
        labelLbl.setFont(labelLbl.getFont().deriveFont(12f));
        labelLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(valueLbl.getFont().deriveFont(Font.BOLD, 16f));
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(labelLbl);
        card.add(valueLbl);
        return card;
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
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
