package burp.ui;

import burp.logic.PlotService;
import burp.logic.StatsService;
import burp.model.TimingAnalysisResult;
import burp.model.TimingTable;
import burp.model.HttpRequestWithTimestamp;
import burp.logic.Winsorizer;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainPanel {

    private final JPanel root;
    private final TimingTable existingModel;
    private final TimingTable nonExistingModel;

    private final ControlPanel controlPanel;
    private final TimingTablesPanel tablesPanel;
    private final AnalysisPanel analysisPanel;
    private final PlotContainerPanel plotPanel;

    private final StatsService statsService;
    private final PlotService plotService;

    public MainPanel(StatsService statsService, PlotService plotService) {
        this.statsService = statsService;
        this.plotService = plotService;

        existingModel = new TimingTable();
        nonExistingModel = new TimingTable();

        root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        controlPanel = new ControlPanel(e -> showHelp(), e -> clearTables());
        tablesPanel = new TimingTablesPanel(existingModel, nonExistingModel);
        plotPanel = new PlotContainerPanel();
        analysisPanel = new AnalysisPanel(e -> runAnalysis(), e -> showPlot(), e -> savePlot());

        root.add(controlPanel, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(analysisPanel, BorderLayout.NORTH);
        bottom.add(plotPanel, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tablesPanel,
                bottom
        );

        split.setResizeWeight(0.5);
        split.setContinuousLayout(true);
        split.setDividerSize(8);

        root.add(split, BorderLayout.CENTER);
    }

    public JPanel getRootPanel() { return root; }

    private void showHelp() {
        HelpPanel hp = new HelpPanel();
        JOptionPane.showMessageDialog(root, hp, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void runAnalysis() {
        List<Long> existingTimes = existingModel.getAllTimings();
        List<Long> nonExistingTimes = nonExistingModel.getAllTimings();
        if (existingTimes.isEmpty() || nonExistingTimes.isEmpty()) {
            JOptionPane.showMessageDialog(root, "No data available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double[] existingTimesArray = existingTimes.stream().mapToDouble(Long::doubleValue).toArray();
        double[] nonExistingTimesArray = nonExistingTimes.stream().mapToDouble(Long::doubleValue).toArray();

        TimingAnalysisResult result = statsService.computeStats(existingTimesArray, nonExistingTimesArray);
        analysisPanel.showResult(result);
    }

    private void showPlot() {
        try {
            if (existingModel.getAllTimings().isEmpty() || nonExistingModel.getAllTimings().isEmpty()) {
                return;
            }
            var existing = Winsorizer.winsorize(existingModel.getAllTimings().stream().map(Long::doubleValue).toList(), 0.95);
            var nonExisting = Winsorizer.winsorize(nonExistingModel.getAllTimings().stream().map(Long::doubleValue).toList(), 0.95);
            XChartPanel<XYChart> panel = plotService.buildPairedScatter(existing, nonExisting);
            plotPanel.showPlot(panel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(root, "Error creating plot: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePlot() {
        try {
            if (plotPanel.getLastPanel() == null) {
                JOptionPane.showMessageDialog(root, "No plot available. Generate a plot first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            plotService.saveLastPlot(root);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(root, "Error saving plot: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCaptureEnabled() { return controlPanel.isCaptureEnabled(); }
    public String getSelectedType() { return controlPanel.getSelectedType(); }
    public void addTiming(String targetType, int burpMessageId, long elapsedMs) {
        SwingUtilities.invokeLater(() -> {
            HttpRequestWithTimestamp r = new HttpRequestWithTimestamp(burpMessageId, System.currentTimeMillis());
            if ("Pool A".equals(targetType)) existingModel.addTiming(r, elapsedMs);
            else nonExistingModel.addTiming(r, elapsedMs);
            controlPanel.updateCounts(existingModel.getRowCount(), nonExistingModel.getRowCount());
        });
    }

    private void clearTables() {
        existingModel.clear();
        nonExistingModel.clear();
        plotPanel.clearPlot();
        analysisPanel.clearResult();
        controlPanel.updateCounts(0, 0);
    }
}
