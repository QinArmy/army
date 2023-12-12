package io.army.criteria;


import io.army.criteria.dialect.BatchDqlStatement;

/**
 * <p>
 * This interface representing  primary DQL statement that return result set,
 * this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningUpdate}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningDelete}</li>
 *     <li>{@link BatchDqlStatement}</li>
 * </ul>
*
 * @since 1.0
 */
public interface DqlStatement extends PrimaryStatement {

}
