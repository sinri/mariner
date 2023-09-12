package io.github.sinri.mariner.mysql;

import io.github.sinri.mariner.helper.PropertiesFileReader;
import io.github.sinri.mariner.mysql.matrix.QueriedResult;
import io.github.sinri.mariner.mysql.matrix.QueriedRow;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MySQLDataSource {
    private final String connectUrl;
    private final String username;
    private final String password;
    private final Map<String, Boolean> connectionFreeMap;
    private final Map<String, Connection> connectionMap;

    public static MySQLDataSource buildFromConfigProperties(PropertiesFileReader propertiesFileReader, String code) throws SQLException, ClassNotFoundException {
        String host = propertiesFileReader.read("mariner.mysql." + code + ".host");
        String port = propertiesFileReader.read("mariner.mysql." + code + ".port");
        String schema = propertiesFileReader.read("mariner.mysql." + code + ".schema");
        String arguments = propertiesFileReader.read("mariner.mysql." + code + ".arguments");
        String username = propertiesFileReader.read("mariner.mysql." + code + ".username");
        String password = propertiesFileReader.read("mariner.mysql." + code + ".password");
        int poolSize = Integer.parseInt(propertiesFileReader.read("mariner.mysql." + code + ".poolSize", "16"));

        return new MySQLDataSource(
                "jdbc:mysql://" + host + ":" + port + "/" + schema + "?" + arguments,
                username,
                password,
                poolSize
        );
    }

    /**
     * @param connectUrl conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_demo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC","root","password");
     */
    public MySQLDataSource(
            String connectUrl,
            String username,
            String password,
            int poolSize
    ) throws ClassNotFoundException, SQLException {
        this.connectUrl = connectUrl;
        this.username = username;
        this.password = password;

        // for mysql 8
        Class.forName("com.mysql.cj.jdbc.Driver");

        this.connectionFreeMap = new ConcurrentHashMap<>();
        this.connectionMap = new ConcurrentHashMap<>();

        for (int i = 0; i < poolSize; i++) {
            this.makeNewConnection();
        }
    }

    private synchronized void makeNewConnection() throws SQLException {
        String connectionSN = UUID.randomUUID().toString();
        var connection = DriverManager.getConnection(connectUrl, username, password);
        this.connectionMap.put(connectionSN, connection);
        this.connectionFreeMap.put(connectionSN, true);
    }

    private synchronized String acquireSnForConnection() {
        Set<String> connectionSNs = this.connectionFreeMap.keySet();
        for (String connectionSN : connectionSNs) {
            Boolean free = this.connectionFreeMap.get(connectionSN);
            if (free) {
                this.connectionFreeMap.put(connectionSN, false);
                return connectionSN;
            }
        }
        throw new RuntimeException("Mariner MySQL Data Source Error: all connections in use.");
    }

    private synchronized void freeSnForConnection(String connectionSN) {
        this.connectionFreeMap.replace(connectionSN, true);
    }

    protected <T> T acquireConnectionToUse(Function<Connection, T> useConnection) {
        String connectionSN = this.acquireSnForConnection();
        var connection = this.connectionMap.get(connectionSN);
        Objects.requireNonNull(connection);
        try {
            return useConnection.apply(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.freeSnForConnection(connectionSN);
        }
    }


    protected <T> T execute(String sql, Function<PreparedStatement, T> preparedStatementHandler) {
        return this.acquireConnectionToUse(connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                return preparedStatementHandler.apply(preparedStatement);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public QueriedResult executeForMatrix(String sql) {
        return this.execute(sql, preparedStatement -> {
            try {
                try (ResultSet resultSet = preparedStatement.executeQuery(sql)) {
                    // for result set
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    List<String> columnNames = new ArrayList<>();
                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        String columnLabel = resultSet.getMetaData().getColumnLabel(columnIndex);
                        columnNames.add(columnLabel);
                    }

                    QueriedResult queriedResult = new QueriedResult(columnNames);

                    while (resultSet.next()) {
                        List<Object> columnValues = new ArrayList<>();
                        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                            var x = resultSet.getObject(columnIndex);
                            columnValues.add(x);
                        }
                        QueriedRow queriedRow = new QueriedRow(columnNames, columnValues);
                        queriedResult.addRow(queriedRow);
                    }
                    return queriedResult;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public QueriedResult executeForModification(String sql) {
        return this.execute(sql, preparedStatement -> {
            try {
                int afx = preparedStatement.executeUpdate();
                Long lastInsertId = null;
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        lastInsertId = resultSet.getLong(1);
                    }
                }
                return new QueriedResult(afx, lastInsertId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Nullable
    @Deprecated
    public QueriedResult executeToWriteInto(String sql) {
        return this.execute(sql, preparedStatement -> {
            try {
                int afx = preparedStatement.executeUpdate();
                Long lastInsertId = null;
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        lastInsertId = resultSet.getLong(1);
                    }
                }
                return new QueriedResult(afx, lastInsertId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Deprecated
    public QueriedResult executeToModify(String sql) {
        return this.execute(sql, preparedStatement -> {
            try {
                int afx = preparedStatement.executeUpdate();
                return new QueriedResult(afx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public QueriedResult executeToCall(String sql) {
        return this.acquireConnectionToUse(connection -> {
            try (CallableStatement callableStatement = connection.prepareCall(sql)) {
                boolean successfullyCalled = callableStatement.execute();
                try (ResultSet resultSet = callableStatement.getResultSet()) {
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    List<String> columnNames = new ArrayList<>();
                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        String columnLabel = resultSet.getMetaData().getColumnLabel(columnIndex);
                        columnNames.add(columnLabel);
                    }

                    QueriedResult queriedResult = new QueriedResult(successfullyCalled, columnNames);

                    while (resultSet.next()) {
                        List<Object> columnValues = new ArrayList<>();
                        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                            var x = resultSet.getObject(columnIndex);
                            columnValues.add(x);
                        }
                        QueriedRow queriedRow = new QueriedRow(columnNames, columnValues);
                        queriedResult.addRow(queriedRow);
                    }

                    return queriedResult;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
