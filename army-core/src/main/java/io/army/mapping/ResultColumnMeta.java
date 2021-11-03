package io.army.mapping;

import java.sql.JDBCType;

public interface ResultColumnMeta {

    /**
     * @see java.sql.ResultSetMetaData#getColumnType(int)
     */
    JDBCType jdbcType();

    /**
     * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
     */
    String sqlType();

    /**
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     */
    String javaClassName();
}
