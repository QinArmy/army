package io.army.meta;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.domain.IDomain;

import java.sql.JDBCType;

/**
 * 代表 表的一个字段
 * created  on 2018/10/8.
 * F 表示字段的 java 类型
 * T 表对应的 domain 类型
 */
public interface Field<T extends IDomain, F> extends Expression<F>, Selection<F> {

    Selection<F> as(String tableAlias, String alias);


    boolean isPrimary();

    boolean isUnique();

    boolean isIndex();


    TableMeta<T> table();


    /**
     * @return 字段对应的 java 类型
     */
    Class<F> javaType();


    /**
     * @return 字段对应的 jdbc 类型
     */
    JDBCType jdbcType();

    boolean isNullable();

    boolean isInsertalbe();

    boolean isUpdatable();

    String comment();


    String defaultValue();

    /**
     * @return 长度, 如:varchar 的长度
     */
    int length();

    /**
     * (Optional) The precision for a decimal (exact numeric)
     * column. (Applies only if a decimal column is used.)
     * Value must be set by developer if used when generating
     * the DDL for the column.
     */
    int precision();

    /**
     * (Optional) The scale for a decimal (exact numeric) column.
     * (Applies only if a decimal column is used.)
     */
    int scale();


    String fieldName();

    String propertyName();


}
