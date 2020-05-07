package io.army.meta;

import io.army.criteria.AliasField;
import io.army.criteria.Expression;
import io.army.criteria.FieldSelection;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingMeta;

import java.sql.JDBCType;

/**
 * @param <T>
 * @param <F>
 * @see FieldMeta
 * @see AliasField
 */
public interface FieldExp<T extends IDomain, F> extends Expression<F>, FieldSelection {


    boolean primary();

    boolean unique();

    boolean index();

    boolean nullable();


    TableMeta<T> tableMeta();

    /**
     * <p>
     * if this field representing {@link TableMeta#ID}
     * then field's tableMeta is {@link MappingMode#CHILD},always return null.
     * </p>
     */
    @Nullable
    GeneratorMeta generator();


    JDBCType jdbcType();

    /**
     * @return 字段对应的 java 类型
     */
    Class<F> javaType();


    MappingMeta mappingMeta();

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

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
