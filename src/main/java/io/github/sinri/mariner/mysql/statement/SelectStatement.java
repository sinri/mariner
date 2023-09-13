package io.github.sinri.mariner.mysql.statement;

import io.github.sinri.mariner.helper.MarinerHelper;
import io.github.sinri.mariner.mysql.exception.MarinerMySQLStatementRenderException;
import io.github.sinri.mariner.mysql.statement.condition.CompareCondition;
import io.github.sinri.mariner.mysql.statement.condition.GroupCondition;
import io.github.sinri.mariner.mysql.statement.condition.MySQLCondition;
import io.github.sinri.mariner.mysql.statement.condition.RawCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SelectStatement extends AbstractReadStatement {
    //    private final List<KeelMySQLCondition> whereConditions = new ArrayList<>();
    final ConditionsComponent whereConditionsComponent = new ConditionsComponent();
    //    private final List<KeelMySQLCondition> havingConditions = new ArrayList<>();
    final ConditionsComponent havingConditionsComponent = new ConditionsComponent();
    private final List<String> tables = new ArrayList<>();
    private final List<String> columns = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final List<String> sortRules = new ArrayList<>();
    private long offset = 0;
    private long limit = 0;
    private String lockMode = "";

    public SelectStatement from(String tableExpression) {
        return from(tableExpression, null);
    }

    public SelectStatement from(String tableExpression, String alias) {
        if (tableExpression == null || tableExpression.trim().equals("")) {
            throw new MarinerMySQLStatementRenderException("Select from null");
        }
        String x = tableExpression;
        if (alias != null) {
            x += " AS " + alias;
        }
        if (tables.isEmpty()) {
            tables.add(x);
        } else {
            tables.set(0, x);
        }
        return this;
    }

    /**
     * @since 2.8
     */
    public SelectStatement from(AbstractReadStatement subQuery, String alias) {
        if (alias == null) {
            throw new MarinerMySQLStatementRenderException("Sub Query without alias");
        }
        return this.from("(" + subQuery.toString() + ")", alias);
    }

    public SelectStatement leftJoin(Function<JoinComponent, JoinComponent> joinFunction) {
        JoinComponent join = new JoinComponent("LEFT JOIN");
        tables.add(joinFunction.apply(join).toString());
        return this;
    }

    public SelectStatement rightJoin(Function<JoinComponent, JoinComponent> joinFunction) {
        JoinComponent join = new JoinComponent("RIGHT JOIN");
        tables.add(joinFunction.apply(join).toString());
        return this;
    }

    public SelectStatement innerJoin(Function<JoinComponent, JoinComponent> joinFunction) {
        JoinComponent join = new JoinComponent("INNER JOIN");
        tables.add(joinFunction.apply(join).toString());
        return this;
    }

    public SelectStatement straightJoin(Function<JoinComponent, JoinComponent> joinFunction) {
        JoinComponent join = new JoinComponent("STRAIGHT_JOIN");
        tables.add(joinFunction.apply(join).toString());
        return this;
    }

    public SelectStatement column(Function<ColumnComponent, ColumnComponent> func) {
        columns.add(func.apply(new ColumnComponent()).toString());
        return this;
    }

    public SelectStatement columnWithAlias(String columnExpression, String alias) {
        columns.add(columnExpression + " as `" + alias + "`");
        return this;
    }

    public SelectStatement columnAsExpression(String fieldName) {
        columns.add(fieldName);
        return this;
    }

    /**
     * @param function ConditionsComponent → ConditionsComponent it self
     * @return this
     * @since 1.4
     */
    public SelectStatement where(Function<ConditionsComponent, ConditionsComponent> function) {
        function.apply(whereConditionsComponent);
        return this;
    }

    public SelectStatement groupBy(String x) {
        categories.add(x);
        return this;
    }

    public SelectStatement groupBy(List<String> x) {
        categories.addAll(x);
        return this;
    }

    public SelectStatement having(Function<ConditionsComponent, ConditionsComponent> function) {
        function.apply(havingConditionsComponent);
        return this;
    }

    public SelectStatement orderByAsc(String x) {
        sortRules.add(x);
        return this;
    }

    public SelectStatement orderByDesc(String x) {
        sortRules.add(x + " DESC");
        return this;
    }

    public SelectStatement limit(long limit) {
        this.offset = 0;
        this.limit = limit;
        return this;
    }

    public SelectStatement limit(long limit, long offset) {
        this.offset = offset;
        this.limit = limit;
        return this;
    }

    public SelectStatement setLockMode(@NotNull String lockMode) {
        Objects.requireNonNull(lockMode);
        this.lockMode = lockMode;
        return this;
    }

    public String toString() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if (columns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append((new MarinerHelper()).joinStringArray(columns, ","));
        }
        if (!tables.isEmpty()) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append("FROM ").append((new MarinerHelper()).joinStringArray(tables, AbstractStatement.SQL_COMPONENT_SEPARATOR));
        }
        if (!whereConditionsComponent.isEmpty()) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append("WHERE ").append(whereConditionsComponent);
        }
        if (!categories.isEmpty()) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append("GROUP BY ").append((new MarinerHelper()).joinStringArray(categories, ","));
        }
        if (!havingConditionsComponent.isEmpty()) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append("HAVING ").append(havingConditionsComponent);
        }
        if (!sortRules.isEmpty()) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append("ORDER BY ").append((new MarinerHelper()).joinStringArray(sortRules, ","));
        }
        if (limit > 0) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append("LIMIT ").append(limit).append(" OFFSET ").append(offset);
        }
        if (!"".equals(lockMode)) {
            sql.append(AbstractStatement.SQL_COMPONENT_SEPARATOR).append(lockMode);
        }
//        if (!getRemarkAsComment().isEmpty()) {
//            sql.append("\n-- ").append(getRemarkAsComment()).append("\n");
//        }
        return String.valueOf(sql);
    }

    public static class JoinComponent {
        final String joinType;
        final List<MySQLCondition> onConditions = new ArrayList<>();
        String tableExpression;
        String alias;

        public JoinComponent(String joinType) {
            this.joinType = joinType;
        }

        public JoinComponent table(String tableExpression) {
            this.tableExpression = tableExpression;
            return this;
        }

        public JoinComponent alias(String alias) {
            this.alias = alias;
            return this;
        }

        public JoinComponent onForRaw(Function<RawCondition, RawCondition> func) {
            this.onConditions.add(func.apply(new RawCondition()));
            return this;
        }

        public JoinComponent onForAndGroup(Function<GroupCondition, GroupCondition> func) {
            this.onConditions.add(func.apply(new GroupCondition(GroupCondition.JUNCTION_FOR_AND)));
            return this;
        }

        public JoinComponent onForOrGroup(Function<GroupCondition, GroupCondition> func) {
            this.onConditions.add(func.apply(new GroupCondition(GroupCondition.JUNCTION_FOR_OR)));
            return this;
        }

        public JoinComponent onForCompare(Function<CompareCondition, CompareCondition> func) {
            this.onConditions.add(func.apply(new CompareCondition()));
            return this;
        }

        public String toString() {
            String s = joinType + " " + tableExpression;
            if (alias != null) {
                s += " AS " + alias;
            }
            if (!onConditions.isEmpty()) {
                s += " ON ";
                s += (new MarinerHelper()).joinStringArray(onConditions, " AND ");
            }
            return s;
        }
    }

    public static class ColumnComponent {
        String schema;
        String field;
        String expression;
        String alias;

        public ColumnComponent field(String field) {
            this.field = field;
            return this;
        }

        public ColumnComponent field(String schema, String field) {
            this.schema = schema;
            this.field = field;
            return this;
        }

        public ColumnComponent expression(String expression) {
            this.expression = expression;
            return this;
        }

        public ColumnComponent alias(String alias) {
            this.alias = alias;
            return this;
        }

        public String toString() {
            StringBuilder column = new StringBuilder();
            if (expression == null) {
                if (schema == null) {
                    column.append("`").append(field).append("`");
                } else {
                    column.append("`").append(schema).append("`.`").append(field).append("`");
                }
            } else {
                column.append(expression);
            }

            if (alias != null) {
                column.append(" AS `").append(alias).append("`");
            }
            return String.valueOf(column);
        }
    }
}
