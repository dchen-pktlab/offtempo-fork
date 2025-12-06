package burp;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TimingTableModel extends AbstractTableModel {

    private static class Row {
        final HttpRequestWithTimestamp req;
        final long elapsed;

        Row(HttpRequestWithTimestamp req, long elapsed) {
            this.req = req;
            this.elapsed = elapsed;
        }
    }

    private final List<Row> rows = new ArrayList<>();

    private final String[] columns = {
            "Message ID",
            "Elapsed (ms)"
    };

    public void addTiming(HttpRequestWithTimestamp req, long elapsed) {
        rows.add(new Row(req, elapsed));
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int col) {
        Row row = rows.get(rowIndex);
        return switch (col) {
            case 0 -> row.req.burpMessageId;
            case 1 -> row.elapsed;
            default -> "";
        };
    }
}
