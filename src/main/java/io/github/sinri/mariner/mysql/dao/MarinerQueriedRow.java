package io.github.sinri.mariner.mysql.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MarinerQueriedRow {
    private final List<Object> columnValues;
    private final List<String> columnNames;

    protected MarinerQueriedRow(MarinerQueriedRow queriedRow) {
        this.columnValues = queriedRow.columnValues;
        this.columnNames = queriedRow.columnNames;
    }

    public MarinerQueriedRow(List<String> columnNames, List<Object> columnValues) {
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new TreeMap<>();
        for (int i = 0; i < this.columnValues.size(); i++) {
            map.put(this.columnNames.get(i), this.columnValues.get(i));
        }
        return map;
    }
}
