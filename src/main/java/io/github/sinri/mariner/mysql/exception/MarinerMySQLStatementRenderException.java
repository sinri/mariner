package io.github.sinri.mariner.mysql.exception;

public class MarinerMySQLStatementRenderException extends RuntimeException {
    public MarinerMySQLStatementRenderException(String sql) {
        super("MySQL Statement Render Exception caused by SQL: " + sql);
    }
}
