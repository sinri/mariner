package io.github.sinri.mariner.mysql.matrix;

import java.util.ArrayList;
import java.util.List;

public class QueriedResult {
    private Boolean successfullyCalled;
    private Integer affectedRows;
    private Long lastInsertedId;
    private  List<QueriedRow> rowList;
    private  List<String> columnNames;

    public QueriedResult(){
        this.successfullyCalled=null;
        this.affectedRows=null;
        this.lastInsertedId=null;
        this.rowList=null;
        this.columnNames=null;
    }

    public QueriedResult setSuccessfullyCalled(Boolean successfullyCalled) {
        this.successfullyCalled = successfullyCalled;
        return this;
    }

    public QueriedResult setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
        return this;
    }

    public QueriedResult setLastInsertedId(Long lastInsertedId) {
        this.lastInsertedId = lastInsertedId;
        return this;
    }

    public QueriedResult setRowList(List<QueriedRow> rowList) {
        this.rowList = rowList;
        return this;
    }

    public QueriedResult setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public QueriedResult(int affectedRows) {
        this.affectedRows = affectedRows;
        this.lastInsertedId = null;
        this.rowList = null;
        this.columnNames = null;
        this.successfullyCalled = null;
    }

    public QueriedResult(int affectedRows, Long lastInsertedId) {
        this.affectedRows = affectedRows;
        this.lastInsertedId = lastInsertedId;
        this.rowList = List.of();
        this.columnNames = List.of();
        this.successfullyCalled = null;
    }



    public QueriedResult(boolean successfullyCalled) {
        this.successfullyCalled = successfullyCalled;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.rowList = List.of();
        this.columnNames = List.of();
    }

    public QueriedResult(boolean successfullyCalled, List<String> columnNames, List<QueriedRow> rowList) {
        this.successfullyCalled = successfullyCalled;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.columnNames = columnNames;
        this.rowList = rowList;
    }

    public QueriedResult(boolean successfullyCalled, List<String> columnNames) {
        this.successfullyCalled = successfullyCalled;
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.columnNames = columnNames;
        this.rowList = new ArrayList<>();
    }

    public QueriedResult(List<String> columnNames) {
        this.columnNames = columnNames;
        this.rowList = new ArrayList<>();
        this.affectedRows = null;
        this.lastInsertedId = null;
        this.successfullyCalled = null;
    }

    public QueriedResult(List<String> columnNames, List<QueriedRow> rowList) {
        this.columnNames = columnNames;
        this.rowList = rowList;
    }

    public QueriedResult addRow(QueriedRow row) {
        this.rowList.add(row);
        return this;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public Long getLastInsertedId() {
        return lastInsertedId;
    }

    public List<QueriedRow> getRowList() {
        return rowList;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

}
