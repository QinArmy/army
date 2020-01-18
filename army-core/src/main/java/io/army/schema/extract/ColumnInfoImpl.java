package io.army.schema.extract;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.JDBCType;
import java.util.StringJoiner;

class ColumnInfoImpl implements ColumnInfo {

    @JsonIgnore
    private final TableInfo table;

    private final String name;

    private String sqlType;

    private JDBCType jdbcType;

    private boolean notNull;

    private String comment;

    private int precision;

    private int scale;

    private String defaultValue;

    ColumnInfoImpl(TableInfo table, String name) {
        this.table = table;
        this.name = name;
    }

    @Override
    public TableInfo getTable() {
        return table;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getSqlType() {
        return sqlType;
    }

    ColumnInfoImpl setSqlType(String sqlType) {
        this.sqlType = sqlType;
        return this;
    }

    @Override
    public JDBCType getJdbcType() {
        return jdbcType;
    }

    ColumnInfoImpl setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    @Override
    public boolean isNotNull() {
        return notNull;
    }

    ColumnInfoImpl setNotNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }


    @Override
    public String getComment() {
        return comment;
    }

    ColumnInfoImpl setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    ColumnInfoImpl setPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    @Override
    public int getScale() {
        return scale;
    }

    ColumnInfoImpl setScale(int scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    ColumnInfoImpl setDefaultValue(String defaultValue) {
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
                .add("notNull=" + notNull)
                .add("comment='" + comment + "'")
                .add("precision=" + precision)
                .add("scale=" + scale)
                .add("defaultValue='" + defaultValue + "'")
                .toString();
    }
}
