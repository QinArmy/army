package io.army.schema.migration;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.JDBCType;
import java.util.StringJoiner;

public class ColumnInfo {

    private final TableInfo table;

    private final String name;

    private String sqlType;

    private JDBCType jdbcType;

    private boolean notNull;

    private String comment;

    private int precision;

    private int scale;

    private String defaultValue;

    ColumnInfo(TableInfo table, String name) {
        this.table = table;
        this.name = name;
    }

    public TableInfo table() {
        return table;
    }


    public String name() {
        return name;
    }



    public String sqlType() {
        return sqlType;
    }

    ColumnInfo sqlType(String sqlType) {
        this.sqlType = sqlType;
        return this;
    }


    public JDBCType jdbcType() {
        return jdbcType;
    }

    ColumnInfo jdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }


    public boolean nonNull() {
        return notNull;
    }

    ColumnInfo nonNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }



    public String comment() {
        return comment;
    }

    ColumnInfo comment(String comment) {
        this.comment = comment;
        return this;
    }


    public int precision() {
        return precision;
    }

    ColumnInfo precision(int precision) {
        this.precision = precision;
        return this;
    }


    public int scale() {
        return scale;
    }

    ColumnInfo scale(int scale) {
        this.scale = scale;
        return this;
    }


    public String defaultValue() {
        return defaultValue;
    }

    ColumnInfo defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("table=" + table.name())
                .add("name='" + name + "'")
                .add("sqlType='" + sqlType + "'")
                .add("jdbcType=" + jdbcType)
                .add("nonNull=" + notNull)
                .add("comment='" + comment + "'")
                .add("precision=" + precision)
                .add("scale=" + scale)
                .add("defaultValue='" + defaultValue + "'")
                .toString();
    }
}