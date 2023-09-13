package io.github.sinri.mariner.mysql.statement.condition;

import io.github.sinri.mariner.helper.MarinerHelper;
import io.github.sinri.mariner.mysql.Quoter;
import io.github.sinri.mariner.mysql.exception.MarinerMySQLStatementRenderException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AmongstCondition implements MySQLCondition {
    public static final String OP_IN = "IN";
    protected final List<String> targetSet;
    protected String element;
    protected boolean inverseOperator;

    public AmongstCondition() {
        this.inverseOperator = false;
        this.targetSet = new ArrayList<>();
    }

    public AmongstCondition not() {
        this.inverseOperator = true;
        return this;
    }

    /**
     * @param element     expression or value
     * @param needQuoting TRUE for VALUE, FALSE for EXPRESSION
     * @return AmongstCondition
     * @since 1.4
     */
    public AmongstCondition element(Object element, Boolean needQuoting) {
        if (needQuoting) {
            if (element instanceof Number) {
                return elementAsValue((Number) element);
            } else {
                return elementAsValue(String.valueOf(element));
            }
        } else {
            return elementAsExpression(String.valueOf(element));
        }
    }

    /**
     * @param element expression (would not be quoted)
     * @return AmongstCondition
     * @since 1.4
     */
    public AmongstCondition element(Object element) {
        return element(element, false);
    }

    public AmongstCondition elementAsExpression(String element) {
        this.element = element;
        return this;
    }

    public AmongstCondition elementAsValue(String element) {
        this.element = new Quoter(element).toString();
        return this;
    }

    public AmongstCondition elementAsValue(Number element) {
        this.element = new Quoter(element).toString();
        return this;
    }

    /**
     * @since 1.4
     */
    public AmongstCondition amongst(Collection<?> targetSet, boolean needQuoting) {
        if (needQuoting) {
            return amongstValueList(targetSet);
        } else {
            List<String> x = new ArrayList<>();
            for (var y : targetSet) {
                x.add(y.toString());
            }
            return amongstExpression(x);
        }
    }

    /**
     * @since 1.4
     */
    public AmongstCondition amongst(Collection<?> targetSet) {
        return amongst(targetSet, true);
    }

    public AmongstCondition amongstValueList(Collection<?> targetSet) {
        for (Object next : targetSet) {
            this.targetSet.add(new Quoter(String.valueOf(next)).toString());
        }
        return this;
    }

    public AmongstCondition amongstValueArray(Object[] targetSet) {
        for (Object next : targetSet) {
            this.targetSet.add(new Quoter(String.valueOf(next)).toString());
        }
        return this;
    }

    public AmongstCondition amongstValue(String value) {
        this.targetSet.add(new Quoter(value).toString());
        return this;
    }

    public AmongstCondition amongstValue(Number value) {
        this.targetSet.add(new Quoter(value).toString());
        return this;
    }

    public AmongstCondition amongstExpression(String value) {
        this.targetSet.add(value);
        return this;
    }

    public AmongstCondition amongstExpression(List<String> value) {
        this.targetSet.addAll(value);
        return this;
    }

    /**
     * 生成SQL的比较条件表达式文本。如果出错，则抛出 KeelSQLGenerateError 异常。
     */
    @Override
    public String toString() {
        if (targetSet.isEmpty()) {
            throw new MarinerMySQLStatementRenderException("AmongstCondition Target Set Empty");
        }

        String s = element;
        if (inverseOperator) {
            s += " NOT";
        }
        s += " " + OP_IN + " (" + (new MarinerHelper()).joinStringArray(targetSet, ",") + ")";
        return s;
    }
}
