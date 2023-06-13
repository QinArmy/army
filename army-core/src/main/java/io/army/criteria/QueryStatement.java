package io.army.criteria;

/**
 * <p>
 * This interface representing SELECT statement.
 * This interface is only base interface of following : <ul>
 * <li>{@link Select}</li>
 * <li>{@link BatchSelect}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface QueryStatement extends Query, DqlStatement {

}
