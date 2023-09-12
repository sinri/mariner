package io.github.sinri.mariner.mysql.matrix;

import java.util.List;

public class QueriedRow {
    private final List<Object> columnValues;
    private final List<String> columnNames;

    public QueriedRow(List<String> columnNames, List<Object> columnValues) {
        this.columnValues = columnValues;
        this.columnNames = columnNames;
    }

    public Object getValueAt(int i) {
        return columnValues.get(i);
    }

    public Object getValueNamed(String name) {
        return columnValues.get(columnNames.indexOf(name));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.columnValues.size(); i++) {
            s.append("<").append(i).append(":").append(this.columnNames.get(i)).append(":").append(this.columnValues.get(i)).append(">");
        }
        return s.toString();
    }
}
