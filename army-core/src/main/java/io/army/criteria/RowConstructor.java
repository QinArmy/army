package io.army.criteria;

/**
 * <p>
 * This interface representing ROW constructor of VALUES statement.
 * </p>
 *
 * @since 1.0
 */
public interface RowConstructor {

    RowConstructor add(Object value);

    RowConstructor row();

}
