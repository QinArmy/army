package io.army.meta;

import io.army.criteria.TableField;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.modelgen._MetaBridge;

import java.util.List;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.</p>
 *
 * @param <T> representing Domain Java Type
 */
public interface FieldMeta<T extends IDomain> extends TableField {


    @Override
    TableMeta<T> tableMeta();

    boolean primary();

    boolean unique();

    boolean index();


    /**
     * <p>
     * if this field representing {@link _MetaBridge#ID}
     * </p>
     */
    @Nullable
    GeneratorMeta generator();

    @Nullable
    FieldMeta<?> dependField();


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


    /**
     * <p>
     * If {@link #javaType()} is below type then return element java type,
     * <ul>
     *     <li>{@link java.util.Collection}</li>
     *     <li>{@link java.util.Set }</li>
     *     <li>{@link java.util.List }</li>
     *     <li>{@link java.util.Map}</li>
     * </ul>
     * else return {@code void.class}.
     * </p>
     */
    List<Class<?>> elementTypes();


}
