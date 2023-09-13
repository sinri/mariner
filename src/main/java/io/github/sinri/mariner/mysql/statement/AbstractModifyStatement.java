package io.github.sinri.mariner.mysql.statement;

import io.github.sinri.mariner.mysql.MySQLConnectionWrapper;
import io.github.sinri.mariner.mysql.dao.MarinerQueriedResult;

public abstract class AbstractModifyStatement extends AbstractStatement {

    public static AbstractModifyStatement buildWithRawSQL(String sql) {
        return new AbstractModifyStatement() {
            @Override
            public String toString() {
                return sql;
            }
        };
    }

    public MarinerQueriedResult run(MySQLConnectionWrapper connectionWrapper) {
        return connectionWrapper.execute(this.toString());
    }
}
