package io.github.sinri.mariner.mysql.exception;

public class MarinerMySQLTransactionRollbackOccurred extends Exception {
    public MarinerMySQLTransactionRollbackOccurred(Exception cause) {
        super("MySQL Transaction Rollback Occurred, caused by: (" + cause.getClass() + ") " + cause.getMessage(), cause);
    }
}
