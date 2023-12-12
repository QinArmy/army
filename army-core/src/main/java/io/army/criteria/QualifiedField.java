package io.army.criteria;


/**
 * <p>
 * This interface representing qualified field , output format: tableAlias.column .
 * You don't need a {@link QualifiedField},if no self-join in statement.
 *
 * @param <T> java type domain.
 */
public interface QualifiedField<T> extends TypeTableField<T> {


    String tableAlias();

}
