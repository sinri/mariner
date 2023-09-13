package io.github.sinri.mariner.mysql.statement;

import io.github.sinri.mariner.helper.MarinerHelper;
import io.github.sinri.mariner.mysql.statement.condition.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConditionsComponent {
    protected final List<MySQLCondition> conditions;

    public ConditionsComponent() {
        conditions = new ArrayList<>();
    }

    public boolean isEmpty() {
        return this.conditions.isEmpty();
    }

    public ConditionsComponent comparison(Function<CompareCondition, CompareCondition> function) {
        CompareCondition condition = function.apply(new CompareCondition());
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public ConditionsComponent comparison(String operator, Function<CompareCondition, CompareCondition> function) {
        CompareCondition condition = function.apply(new CompareCondition(operator));
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public ConditionsComponent among(Function<AmongstCondition, AmongstCondition> function) {
        AmongstCondition condition = function.apply(new AmongstCondition());
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public ConditionsComponent intersection(Function<GroupCondition, GroupCondition> function) {
        GroupCondition condition = function.apply(new GroupCondition(GroupCondition.JUNCTION_FOR_AND));
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public ConditionsComponent union(Function<GroupCondition, GroupCondition> function) {
        GroupCondition condition = function.apply(new GroupCondition(GroupCondition.JUNCTION_FOR_OR));
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public ConditionsComponent raw(String raw) {
        if (raw != null && !raw.isEmpty()) {
            conditions.add(new RawCondition(raw));
        }
        return this;
    }

    @Override
    public String toString() {
        if (conditions.isEmpty()) return "";
        return (new MarinerHelper()).joinStringArray(conditions, " and ");
    }

}
