package io.army.meta;

import io.army.criteria.AliasTableFieldMeta;
import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;

import java.sql.JDBCType;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.</p>
 *
 * @param <T> representing Entity Java class
 * @param <F> representing Entity property Java class
 */
public interface FieldMeta<T extends IDomain, F> extends Expression<F> ,Selection{

    Selection as();

    boolean primary();

    boolean unique();

    boolean index();

    boolean nullable();


    TableMeta<T> tableMeta();

    AliasTableFieldMeta<T,F> table(String tableAlias);

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

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
