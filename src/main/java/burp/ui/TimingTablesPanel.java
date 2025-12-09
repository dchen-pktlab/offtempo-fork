package burp.ui;

import burp.model.TimingTable;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class TimingTablesPanel extends JSplitPane {
    private final TimingTable existingModel;
    private final TimingTable nonExistingModel;

    public TimingTablesPanel(TimingTable existing, TimingTable nonExisting) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.existingModel = existing;
        this.nonExistingModel = nonExisting;

        JTable existingTable = new JTable(existingModel);
        JTable nonExistingTable = new JTable(nonExistingModel);
        existingTable.setRowSorter(new TableRowSorter<>(existingModel));
        nonExistingTable.setRowSorter(new TableRowSorter<>(nonExistingModel));

        JScrollPane leftScroll = new JScrollPane(existingTable);
        JScrollPane rightScroll = new JScrollPane(nonExistingTable);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftScroll, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(0,300));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Existing"));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(rightScroll, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(0,300));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Non-existing"));

        setLeftComponent(leftPanel);
        setRightComponent(rightPanel);
        setResizeWeight(0.5);
    }

}
