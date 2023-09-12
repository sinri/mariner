package io.github.sinri.mariner.mysql.statement;

import java.util.UUID;

abstract public class AbstractStatement {
    protected final String statementUuid;

    public AbstractStatement() {
        this.statementUuid = UUID.randomUUID().toString();
    }

    /**
     * @return The SQL Generated
     */
    public abstract String toString();

    /**
     * @param sql
     * @return
     * @since 3.0.0
     */
    public static AbstractStatement buildWithRawSQL(String sql) {
        return new AbstractStatement() {
            @Override
            public String toString() {
                return sql;
            }
        };
    }
}
