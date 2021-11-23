package io.army.meta;

import io.army.annotation.UpdateMode;
import io.army.criteria.SetTargetPart;
import io.army.criteria._SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.modelgen._MetaBridge;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.</p>
 *
 * @param <T> representing Domain Java Type
 * @param <F> representing Domain property Java Type
 */
public interface FieldMeta<T extends IDomain, F> extends GenericField<T, F>, ParamMeta, SetTargetPart {


    /**
     * @throws UnsupportedOperationException throw always
     */
    @Override
    void appendSortPart(_SqlContext context);

    /**
     * @throws UnsupportedOperationException throw always
     */
    @Override
    void appendSQL(_SqlContext context);


    boolean primary();

    boolean unique();

    boolean index();

    boolean nullable();

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


    String columnName();

    boolean insertable();

    UpdateMode updateMode();

    String comment();

    String defaultValue();

    boolean codec();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();


}
