package io.github.sinri.mariner.mysql;

import io.github.sinri.mariner.mysql.dao.MarinerQueriedResult;
import io.github.sinri.mariner.mysql.dao.MarinerQueriedRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLConnectionWrapper {
    private final Connection connection;

    public MySQLConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    public MarinerQueriedResult query(String sql) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery(sql)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                // maybe need : metaData.getColumnType(index)
                // for result set
                int columnCount = metaData.getColumnCount();
                List<String> columnNames = new ArrayList<>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    String columnLabel = metaData.getColumnLabel(columnIndex);
                    columnNames.add(columnLabel);
                }

                MarinerQueriedResult queriedResult = new MarinerQueriedResult(columnNames);

                while (resultSet.next()) {
                    List<Object> columnValues = new ArrayList<>();
                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        var x = resultSet.getObject(columnIndex);
                        columnValues.add(x);
                    }
                    MarinerQueriedRow queriedRow = new MarinerQueriedRow(columnNames, columnValues);
                    queriedResult.addRow(queriedRow);
                }
                return queriedResult;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MarinerQueriedResult execute(String sql) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int afx = preparedStatement.executeUpdate();
            Long lastInsertId = null;
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    lastInsertId = resultSet.getLong(1);
                }
            }
            return new MarinerQueriedResult(afx, lastInsertId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MarinerQueriedResult call(String sql) {
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            boolean successfullyCalled = callableStatement.execute();
            try (ResultSet resultSet = callableStatement.getResultSet()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                List<String> columnNames = new ArrayList<>();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    String columnLabel = resultSet.getMetaData().getColumnLabel(columnIndex);
                    columnNames.add(columnLabel);
                }

                MarinerQueriedResult queriedResult = new MarinerQueriedResult(successfullyCalled, columnNames);

                while (resultSet.next()) {
                    List<Object> columnValues = new ArrayList<>();
                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        var x = resultSet.getObject(columnIndex);
                        columnValues.add(x);
                    }
                    MarinerQueriedRow queriedRow = new MarinerQueriedRow(columnNames, columnValues);
                    queriedResult.addRow(queriedRow);
                }

                return queriedResult;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
