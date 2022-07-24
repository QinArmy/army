package io.army.criteria;


import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing qualified field , output format: tableAlias.column .
 * You don't need a {@link QualifiedField},if no self-join in statement.
 * </p>
 *
 * @param <T> java type domain.
 */
public interface QualifiedField<T> extends TableField {

    @Override
    TableMeta<T> tableMeta();

    String tableAlias();

}
