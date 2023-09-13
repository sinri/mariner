package io.github.sinri.mariner.mysql.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarinerQueriedResult {
    private Boolean successfullyCalled;
    private Integer affectedRows;
    private Long lastInsertedId;
    private List<MarinerQueriedRow> rowList;
    private List<String> columnNames;

    public MarinerQueriedResult() {
        this.successfullyCalled = null;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.rowList = null;
        this.columnNames = null;
    }

    public MarinerQueriedResult(int affectedRows) {
        this.affectedRows = affectedRows;
        this.lastInsertedId = null;
        this.rowList = null;
        this.columnNames = null;
        this.successfullyCalled = null;
    }

    public MarinerQueriedResult(int affectedRows, Long lastInsertedId) {
        this.affectedRows = affectedRows;
        this.lastInsertedId = lastInsertedId;
        this.rowList = List.of();
        this.columnNames = List.of();
        this.successfullyCalled = null;
    }

    public MarinerQueriedResult(boolean successfullyCalled) {
        this.successfullyCalled = successfullyCalled;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.rowList = List.of();
        this.columnNames = List.of();
    }

    public MarinerQueriedResult(boolean successfullyCalled, List<String> columnNames, List<MarinerQueriedRow> rowList) {
        this.successfullyCalled = successfullyCalled;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.columnNames = columnNames;
        this.rowList = rowList;
    }

    public MarinerQueriedResult(boolean successfullyCalled, List<String> columnNames) {
        this.successfullyCalled = successfullyCalled;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.columnNames = columnNames;
        this.rowList = new ArrayList<>();
    }

    public MarinerQueriedResult(List<String> columnNames) {
        this.columnNames = columnNames;
        this.rowList = new ArrayList<>();
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.successfullyCalled = null;
    }

    public MarinerQueriedResult(List<String> columnNames, List<MarinerQueriedRow> rowList) {
        this.columnNames = columnNames;
        this.rowList = rowList;
    }

    public MarinerQueriedResult setSuccessfullyCalled(Boolean successfullyCalled) {
        this.successfullyCalled = successfullyCalled;
        return this;
    }

    public MarinerQueriedResult setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
        return this;
    }

    public MarinerQueriedResult setLastInsertedId(Long lastInsertedId) {
        this.lastInsertedId = lastInsertedId;
        return this;
    }

    public MarinerQueriedResult setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public MarinerQueriedResult addRow(MarinerQueriedRow row) {
        this.rowList.add(row);
        return this;
    }

    public List<MarinerQueriedRow> getRowList() {
        return rowList;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public Long getLastInsertedId() {
        return lastInsertedId;
    }

    public MarinerQueriedResult setRowList(List<MarinerQueriedRow> rowList) {
        this.rowList = rowList;
        return this;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Map<String, Object>> toMaps() {
        List<Map<String, Object>> list = new ArrayList<>();
        rowList.forEach(row -> {
            list.add(row.toMap());
        });
        return list;
    }


    public Boolean isSuccessfullyCalled() {
        return successfullyCalled;
    }
}
