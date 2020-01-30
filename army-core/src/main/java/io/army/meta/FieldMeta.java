package io.army.meta;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

/**
 * 代表 表的一个字段
 * created  on 2018/10/8.
 * F 表示字段的 java 类型
 * T 表对应的 domain 类型
 */
public interface FieldMeta<T extends IDomain, F> extends Expression<F>, Selection {

    Selection as(String tableAlias, String alias);


    boolean primary();

    boolean unique();

    boolean index();

    boolean nullable();


    TableMeta<T> table();

    @Nullable
    GeneratorMeta generator();


    /**
     * @return 字段对应的 java 类型
     */
    @NonNull
    Class<F> javaType();

    MappingType mappingType();

    boolean insertalbe();

    boolean updatable();

    String comment();

    String defaultValue();

    /**
     * (Optional) The columnSize for a decimal (exact numeric)
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
