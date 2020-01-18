package io.army.schema.extract;

import java.sql.JDBCType;

public interface ColumnInfo {

   TableInfo getTable();

    String getName();

    String getSqlType();

    JDBCType getJdbcType();

    boolean isNotNull();

    String getComment();

    String getDefaultValue();

    /**
     * (Optional) The precision for a decimal (exact numeric)
     * column. (Applies only if a decimal column is used.)
     * Value must be set by developer if used when generating
     * the DDL for the column.
     */
    int getPrecision();

    /**
     * (Optional) The scale for a decimal (exact numeric) column.
     * (Applies only if a decimal column is used.)
     */
    int getScale();


}
