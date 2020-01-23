package io.army.meta;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.meta.mapping.MappingType;

/**
 * 代表 表的一个字段
 * created  on 2018/10/8.
 * F 表示字段的 java 类型
 * T 表对应的 domain 类型
 */
public interface FieldMeta<T extends IDomain, F> extends Expression<F>, Selection {

    Selection as(String tableAlias, String alias);


    boolean isPrimary();

    boolean isUnique();

    boolean isIndex();

    boolean isNullable();


    TableMeta<T> table();


    /**
     * @return 字段对应的 java 类型
     */
    Class<F> javaType();

    MappingType mappingType();

    boolean isInsertalbe();

    boolean isUpdatable();

    String comment();

    String defaultValue();

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
