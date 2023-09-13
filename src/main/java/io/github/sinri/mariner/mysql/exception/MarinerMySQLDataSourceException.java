package io.github.sinri.mariner.mysql.exception;

public class MarinerMySQLDataSourceException extends Exception {
    public MarinerMySQLDataSourceException(Exception e) {
        super("MySQL Data Source Exception: (" + e.getClass() + ") " + e.getMessage(), e);
    }
}
