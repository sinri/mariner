package io.github.sinri.mariner.mysql.statement;

import io.github.sinri.mariner.mysql.MySQLConnectionWrapper;
import io.github.sinri.mariner.mysql.dao.MarinerQueriedResult;

public abstract class AbstractCallStatement extends AbstractStatement {
    public static AbstractCallStatement buildWithRawSQL(String sql) {
        return new AbstractCallStatement() {
            @Override
            public String toString() {
                return sql;
            }
        };
    }

    public MarinerQueriedResult run(MySQLConnectionWrapper connectionWrapper) {
        return connectionWrapper.call(this.toString());
    }
}
