package io.github.sinri.mariner.mysql.dao;

import io.github.sinri.mariner.helper.MarinerHelper;
import io.github.sinri.mariner.mysql.MySQLConnectionWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于快速根据数据库中的表结构生成AbstractTableRow的实现类的代码生成工具。
 * 为安全起见，默认不打开覆盖开关。
 *
 * @since 2.8
 */
public class MarinerTableRowClassGenerator {

    private static final Pattern patternForLooseEnum;

    static {
        patternForLooseEnum = Pattern.compile("Enum\\{([A-Za-z0-9_, ]+)}");
    }

    private final MySQLConnectionWrapper sqlConnection;
    private final Set<String> tableSet;
    private String schema;

    /**
     * Generate an Enum in the class and let the getter return the enum.
     * Loose Enum means that, you use a String field in table, but you defined some values in Java as Enum.
     * Values other than the enum defined ones may be treated as null in JAVA.
     */

    public MarinerTableRowClassGenerator(MySQLConnectionWrapper sqlConnection) {
        this.sqlConnection = sqlConnection;
        this.schema = null;
        this.tableSet = new HashSet<>();
    }

    public MarinerTableRowClassGenerator forSchema(String schema) {
        if (schema == null || schema.isEmpty() || schema.isBlank()) {
            this.schema = null;
        } else {
            this.schema = schema;
        }
        return this;
    }

    public MarinerTableRowClassGenerator forTables(Collection<String> tables) {
        this.tableSet.addAll(tables);
        return this;
    }

    public MarinerTableRowClassGenerator forTable(String table) {
        this.tableSet.add(table);
        return this;
    }


    public void generate(String packageName, String packagePath) {
        Set<String> tables = this.confirmTablesToGenerate();
        generateForTables(packageName, packagePath, tables);
    }

    private Set<String> confirmTablesToGenerate() {
        Set<String> tables = new HashSet<>();
        if (this.tableSet.isEmpty()) {
            MarinerQueriedResult queriedResult;
            if (schema == null || schema.isEmpty() || schema.isBlank()) {
                queriedResult = this.sqlConnection.query("show tables");
            } else {
                queriedResult = this.sqlConnection.query("show tables in `" + this.schema + "`");
            }
            queriedResult.getRowList().forEach(row -> {
                tables.add(row.getValueAt(0).toString());
            });
        } else {
            tables.addAll(this.tableSet);
        }
        return tables;
    }

    private void generateForTables(String packageName, String packagePath, Collection<String> tables) {
        MarinerHelper marinerHelper = new MarinerHelper();
        tables.forEach(table -> {
            String className = marinerHelper.fromUnderScoreCaseToCamelCase(table) + "TableRow";
            String classFile = packagePath + "/" + className + ".java";

            String generatedClassCodeForOneTable = generateClassCodeForOneTable(schema, table, packageName, className);
            // rewrite
            try (var f = new FileWriter(classFile)) {
                f.write(generatedClassCodeForOneTable);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getCommentOfTable(String table, String schema) {
        String sql_for_table_comment = "SELECT TABLE_COMMENT " +
                "FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_NAME = '" + table + "' " +
                (schema == null ? "" : ("AND TABLE_SCHEMA = '" + schema + "' "));

        MarinerQueriedResult queriedResult = sqlConnection.query(sql_for_table_comment);
        MarinerQueriedRow queriedRow = queriedResult.getRowList().get(0);
        Object tableComment = queriedRow.getValueNamed("TABLE_COMMENT");
        return tableComment.toString();
    }

    private String buildFieldGetter(String field, String type, String comment) {
        MarinerHelper marinerHelper = new MarinerHelper();
        String getter = "get" + marinerHelper.fromUnderScoreCaseToCamelCase(field);
        String returnType = "Object";
        String readMethod = "readValue";

        if (type.contains("bigint")) {
            returnType = "Long";
            readMethod = "readLong";
        } else if (type.contains("int")) {
            // tinyint smallint mediumint
            returnType = "Integer";
            readMethod = "readInteger";
        } else if (type.contains("float")) {
            returnType = "Float";
            readMethod = "readFloat";
        } else if (type.contains("double")) {
            returnType = "Double";
            readMethod = "readDouble";
        } else if (type.contains("decimal")) {
            returnType = "Number";
            readMethod = "readNumber";
        } else if (type.contains("datetime") || type.contains("timestamp")) {
            returnType = "String";
            readMethod = "readDateTime";
        } else if (type.contains("date")) {
            returnType = "String";
            readMethod = "readDate";
        } else if (type.contains("time")) {
            returnType = "String";
            readMethod = "readTime";
        } else if (type.contains("text") || type.contains("char")) {
            // mediumtext, varchar, etc.
            returnType = "String";
            readMethod = "readString";
        }

        StringBuilder getter_string = new StringBuilder();

        String enum_name = null;

        if (type.contains("char") && comment != null) {
            Matcher matcher = patternForLooseEnum.matcher(comment);
            if (matcher.find()) {
                String enumValuesString = matcher.group(1);
                String[] enumValueArray = enumValuesString.split("[, ]+");
                if (enumValueArray.length > 0) {
                    // to build enum
                    enum_name = marinerHelper.fromUnderScoreCaseToCamelCase(field) + "Enum";

                    getter_string
                            .append("\t/**\n")
                            .append("\t * Enum for Field `").append(field).append("` \n")
                            .append("\t */\n")
                            .append("\tpublic enum ").append(enum_name).append(" {\n");
                    for (var enumValue : enumValueArray) {
                        getter_string.append("\t\t").append(enumValue).append(",\n");
                    }
                    getter_string.append("\t}\n");
                }
            }
        }

        if (enum_name == null) {
            getter_string.append("\t/*\n");
            if (comment != null) {
                getter_string.append("\t * ").append(comment).append("\n\t * \n");
            }
            getter_string.append("\t * Field `").append(field).append("` of type `").append(type).append("`.\n")
                    .append("\t */\n")
                    .append("\tpublic ").append(returnType).append(" ").append(getter).append("() {\n")
                    .append("\t\treturn ").append(readMethod).append("(\"").append(field).append("\");\n")
                    .append("\t}\n");
        } else {
            getter_string.append("\t/*\n")
                    .append("\t * ").append(comment).append("\n\t * \n")
                    .append("\t * Loose Enum of Field `").append(field).append("` of type `").append(type).append("`.\n")
                    .append("\t */\n")
                    .append("\tpublic ").append(enum_name).append(" ").append(getter).append("() {\n")
                    .append("\t\treturn ").append(enum_name).append(".valueOf(\n")
                    .append("\t\t\t").append(readMethod).append("(\"").append(field).append("\")\n")
                    .append("\t\t);\n")
                    .append("\t}\n");

            getter_string.append("\t/*\n")
                    .append("\t * ").append(comment).append("\n\t * \n")
                    .append("\t * Raw value of Field `").append(field).append("` of type `").append(type).append("`.\n")
                    .append("\t */\n")
                    .append("\tpublic ").append(returnType).append(" ").append(getter).append("AsRawString() {\n")
                    .append("\t\treturn ").append(readMethod).append("(\"").append(field).append("\");\n")
                    .append("\t}\n");
        }

        return getter_string.toString();
    }

    private String buildAllFieldGetters(String table, String schema) {
        String sql_for_columns = "show full columns in ";
        if (schema != null && !schema.isEmpty() && !schema.isBlank()) {
            sql_for_columns += "`" + schema + "`.";
        }
        sql_for_columns += "`" + table + "`;";

        StringBuilder getters = new StringBuilder();

        MarinerQueriedResult queriedResult = sqlConnection.query(sql_for_columns);
        queriedResult.getRowList().forEach(row -> {
            String field = String.valueOf(row.getValueNamed("Field"));
            String type = String.valueOf(row.getValueNamed("Type"));
            String comment = String.valueOf(row.getValueNamed("Comment"));
            if (comment == null || comment.isEmpty() || comment.isBlank()) {
                comment = null;
            }
            getters.append(this.buildFieldGetter(field, type, comment)).append("\n");
        });
        return getters.toString();
    }

    private String getCreationOfTable(String table, String schema) {
        String sql_sct = "show create table ";
        if (schema != null) {
            sql_sct += "`" + schema + "`.";
        }
        sql_sct += "`" + table + "`;";
        MarinerQueriedResult queriedResult = sqlConnection.query(sql_sct);
        MarinerQueriedRow queriedRow = queriedResult.getRowList().get(0);
        Object x = queriedRow.getValueAt(1);
        return x.toString();
    }

    private String generateClassCodeForOneTable(String schema, String table, String packageName, String className) {
        String table_comment = this.getCommentOfTable(table, schema);// comment of table
        String getters = this.buildAllFieldGetters(table, schema); // getters
        String creation = this.getCreationOfTable(table, schema);// creation

        StringBuilder classContent = new StringBuilder();

        classContent
                .append("package ").append(packageName).append(";").append("\n")
                .append("import io.github.sinri.mariner.mysql.dao.TableRowInterface;\n")
                .append("import io.github.sinri.mariner.mysql.dao.QueriedRow;\n")
                .append("\n")
                .append("/**\n");
        if (table_comment == null || table_comment.isEmpty() || table_comment.isBlank()) {
            classContent.append(" * Table ").append(table).append(" has no table comment.\n");
        } else {
            classContent.append(" * ").append(table_comment).append("\n");
        }
        classContent.append(" * (´^ω^`)\n");
        if (schema != null && !schema.isEmpty() && !schema.isBlank()) {
            classContent.append(" * SCHEMA: ").append(schema).append("\n");
        }
        classContent
                .append(" * TABLE: ").append(table).append("\n")
                .append(" * (*￣∇￣*)\n")
                .append(" * NOTICE BY KEEL:\n")
                .append(" * \tTo avoid being rewritten, do not modify this file manually, unless editable confirmed.\n")
                .append(" * \tIt was auto-generated on ").append(new Date()).append(".\n")
                .append(" * @see ").append(this.getClass().getName()).append("\n")
                .append(" */\n")
                .append("public class ").append(className).append(" extends QueriedRow implements TableRowInterface {").append("\n");
        if (this.schema != null && !this.schema.isEmpty() && !this.schema.isBlank()) {
            classContent.append("\tpublic static final String SCHEMA = \"").append(schema).append("\";\n");
        }
        classContent.append("\n")
                .append("\tpublic static final String TABLE = \"").append(table).append("\";\n")
                .append("\n")
                .append("\t").append("public ").append(className).append("(QueriedRow queriedRow) {\n")
                .append("\t\tsuper(queriedRow);\n")
                .append("\t}\n")
                .append("\n")
                .append("\tpublic QueriedRow getEmbeddedQueriedRow(){\n")
                .append("\t\treturn this;\n")
                .append("\t}\n")
                .append("\n")
                .append("\t@Override\n")
                .append("\tpublic String sourceTableName() {\n")
                .append("\t\treturn TABLE;\n")
                .append("\t}\n")
                .append("\n");
        if (this.schema != null) {
            classContent.append("\tpublic String sourceSchemaName(){\n")
                    .append("\t\treturn SCHEMA;\n")
                    .append("\t}\n");
        }

        classContent.append(getters);
        classContent.append("\n}\n");
        if (creation != null) {
            classContent.append("\n/*\n").append(creation).append("\n */\n");
        }

        return classContent.toString();
    }
}
