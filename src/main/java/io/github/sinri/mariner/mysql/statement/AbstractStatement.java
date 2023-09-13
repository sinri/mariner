package io.github.sinri.mariner.mysql.statement;

import io.github.sinri.mariner.mysql.MySQLConnectionWrapper;
import io.github.sinri.mariner.mysql.dao.MarinerQueriedResult;

import java.util.UUID;

abstract public class AbstractStatement {
    protected static String SQL_COMPONENT_SEPARATOR = " ";//"\n";

    public static void setSqlComponentSeparator(String sqlComponentSeparator) {
        SQL_COMPONENT_SEPARATOR = sqlComponentSeparator;
    }

    protected final String statementUuid;

    public AbstractStatement() {
        this.statementUuid = UUID.randomUUID().toString();
    }

    /**
     * @return The SQL Generated
     */
    public abstract String toString();

    public abstract MarinerQueriedResult run(MySQLConnectionWrapper connectionWrapper);
}
