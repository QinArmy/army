package io.army.criteria;

/**
 * <p>
 * This interface representing simple(non-batch) primary DQL statement that return result set,
 * this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningUpdate}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningDelete}</li>
 * </ul>
 *
 * @see SimpleDmlStatement
 * @since 0.6.0
 */
public interface SimpleDqlStatement extends DqlStatement {


}
