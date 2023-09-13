package io.github.sinri.mariner.mysql.dao;


import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface TableRowInterface {

    MarinerQueriedRow getEmbeddedQueriedRow();

    /**
     * @return default null
     */
    default String sourceSchemaName() {
        return null;
    }

    /**
     * @return table name
     */
    String sourceTableName();

    default String readDateTime(String field) {
        String s = readString(field);
        if (s == null) return null;
        return LocalDateTime.parse(s)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    default String readDate(String field) {
        return readString(field);
    }

    default String readTime(String field) {
        var s = readString(field);
        if (s == null) return null;
        return s
                .replaceAll("[PTS]+", "")
                .replaceAll("[HM]", ":");
    }

    default String readTimestamp(String field) {
        return readDateTime(field);
    }

    default @Nullable String readString(String key) {
        return String.valueOf(this.getEmbeddedQueriedRow().getValueNamed(key));
    }

    default @Nullable Number readNumber(String key) {
        Object v = this.getEmbeddedQueriedRow().getValueNamed(key);
        return (Number) v;
    }

    default @Nullable Long readLong(String key) {
        Number number = readNumber(key);
        if (number == null) return null;
        return number.longValue();
    }

    default @Nullable Integer readInteger(String key) {
        Number number = readNumber(key);
        if (number == null) return null;
        return number.intValue();
    }

    default @Nullable Float readFloat(String key) {
        Number number = readNumber(key);
        if (number == null) return null;
        return number.floatValue();
    }

    default @Nullable Double readDouble(String key) {
        Number number = readNumber(key);
        if (number == null) return null;
        return number.doubleValue();
    }

    default @Nullable Boolean readBoolean(String key) {
        Object v = getEmbeddedQueriedRow().getValueNamed(key);
        return (Boolean) v;
    }
}
