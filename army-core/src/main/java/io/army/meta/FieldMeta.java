package io.army.meta;

import io.army.criteria.GenericField;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.modelgen._MetaBridge;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.</p>
 *
 * @param <T> representing Domain Java Type
 * @param <F> representing Domain property Java Type
 */
public interface FieldMeta<T extends IDomain, F> extends GenericField<T, F> {


    boolean primary();

    boolean unique();

    boolean index();


    /**
     * <p>
     * if this field representing {@link _MetaBridge#ID}
     * then field's tableMeta is {@link MappingMode#CHILD},always return null.
     * </p>
     */
    @Nullable
    GeneratorMeta generator();

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


    boolean insertable();

    String comment();

    String defaultValue();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();


}
