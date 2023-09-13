package io.github.sinri.mariner.mysql;

import io.github.sinri.mariner.helper.MarinerPropertiesFileReader;
import io.github.sinri.mariner.mysql.exception.MarinerMySQLDataSourceException;
import io.github.sinri.mariner.mysql.exception.MarinerMySQLTransactionRollbackOccurred;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * It is not a version with POOLING.
 */
public class MarinerMySQLDataSource {
    private final String connectUrl;
    private final String username;
    private final String password;

    public MarinerMySQLDataSource(
            String connectUrl,
            String username,
            String password
    ) {
        this.connectUrl = connectUrl;
        this.username = username;
        this.password = password;

        // for mysql 8... seems superfluous
        //Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public static MarinerMySQLDataSource buildFromConfigProperties(MarinerPropertiesFileReader propertiesFileReader, String code) {
        String host = propertiesFileReader.read("mariner.mysql." + code + ".host");
        String port = propertiesFileReader.read("mariner.mysql." + code + ".port");
        String schema = propertiesFileReader.read("mariner.mysql." + code + ".schema");
        String arguments = propertiesFileReader.read("mariner.mysql." + code + ".arguments");
        String username = propertiesFileReader.read("mariner.mysql." + code + ".username");
        String password = propertiesFileReader.read("mariner.mysql." + code + ".password");
        int poolSize = Integer.parseInt(propertiesFileReader.read("mariner.mysql." + code + ".poolSize", "16"));

        return new MarinerMySQLDataSource(
                "jdbc:mysql://" + host + ":" + port + "/" + schema + "?" + arguments,
                username,
                password
        );
    }

    private Connection makeNewConnection() throws MarinerMySQLDataSourceException {
        try {
            return DriverManager.getConnection(connectUrl, username, password);
        } catch (SQLException e) {
            throw new MarinerMySQLDataSourceException(e);
        }
    }

    public void transaction(Consumer<MySQLConnectionWrapper> handler) throws MarinerMySQLDataSourceException, MarinerMySQLTransactionRollbackOccurred {
        transaction(mySQLConnectionWrapper -> {
            handler.accept(mySQLConnectionWrapper);
            return null;
        });
    }

    public <T> T transaction(Function<MySQLConnectionWrapper, T> func) throws MarinerMySQLTransactionRollbackOccurred, MarinerMySQLDataSourceException {
        try (Connection connection = this.makeNewConnection()) {
            try {
                connection.setAutoCommit(false);
                MySQLConnectionWrapper mySQLConnectionWrapper = new MySQLConnectionWrapper(connection);
                T t = func.apply(mySQLConnectionWrapper);
                connection.commit();
                return t;
            } catch (Exception e) {
                connection.rollback();
                throw new MarinerMySQLTransactionRollbackOccurred(e);
            }
        } catch (SQLException ex) {
            throw new MarinerMySQLDataSourceException(ex);
        }
    }

    public void connection(Consumer<MySQLConnectionWrapper> handler) throws MarinerMySQLDataSourceException {
        connection(mySQLConnectionWrapper -> {
            handler.accept(mySQLConnectionWrapper);
            return null;
        });
    }

    public <T> T connection(Function<MySQLConnectionWrapper, T> func) throws MarinerMySQLDataSourceException {
        try (Connection connection = this.makeNewConnection()) {
            connection.setAutoCommit(true);
            MySQLConnectionWrapper mySQLConnectionWrapper = new MySQLConnectionWrapper(connection);
            return func.apply(mySQLConnectionWrapper);
        } catch (Exception exception) {
            throw new MarinerMySQLDataSourceException(exception);
        }
    }

}
