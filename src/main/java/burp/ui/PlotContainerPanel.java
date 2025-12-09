package burp.ui;

import lombok.Getter;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import javax.swing.*;
import java.awt.*;

@Getter
public class PlotContainerPanel extends JPanel {
    private XChartPanel<XYChart> lastPanel = null;

    public PlotContainerPanel() {
        super(new GridLayout(1,1));
        setPreferredSize(new Dimension(0, 350));
    }

    public void showPlot(XChartPanel<XYChart> panel) {
        lastPanel = panel;
        removeAll();
        setLayout(new GridLayout(1,1));
        add(panel);
        revalidate();
        repaint();
    }

    public void clearPlot() {
        this.removeAll();
        this.revalidate();
        this.repaint();
        lastPanel = null;
    }

}
