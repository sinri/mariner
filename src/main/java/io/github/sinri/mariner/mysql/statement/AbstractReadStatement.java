package io.github.sinri.mariner.mysql.statement;

import io.github.sinri.mariner.mysql.MySQLConnectionWrapper;
import io.github.sinri.mariner.mysql.dao.MarinerQueriedResult;

public abstract class AbstractReadStatement extends AbstractStatement {
    public static AbstractReadStatement buildWithRawSQL(String sql) {
        return new AbstractReadStatement() {
            @Override
            public String toString() {
                return sql;
            }
        };
    }

    public MarinerQueriedResult run(MySQLConnectionWrapper connectionWrapper) {
        return connectionWrapper.query(this.toString());
    }
}
