package io.army.criteria;

/**
 * <p>
 * This interface representing SELECT statement.
 * This interface is only base interface of following : <ul>
 * <li>{@link Select}</li>
 * <li>{@link BatchSelect}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SelectStatement extends Query, DqlStatement {

}
