package io.army.criteria;

import io.army.domain.IDomain;

/**
 * <p>
 * This interface representing qualified field , output format: tableAlias.column .
 * You don't need a {@link QualifiedField},if no self-join in statement.
 * </p>
 *
 * @param <T> java type domain.
 * @param <F> java type field.
 */
public interface QualifiedField<T extends IDomain> extends GenericField<T> {

    String tableAlias();

}
