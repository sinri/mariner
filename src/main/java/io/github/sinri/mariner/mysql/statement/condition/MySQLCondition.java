package io.github.sinri.mariner.mysql.statement.condition;


/**
 * @since 2.8 became interface
 */
public interface MySQLCondition {
    /**
     * 生成SQL的条件表达式文本。
     *
     * @return The generated SQL component as String
     */
    String toString();
}