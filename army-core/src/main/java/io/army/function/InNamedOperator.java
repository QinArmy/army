package io.army.function;

import io.army.criteria.IPredicate;
import io.army.criteria.SimpleExpression;

/**
 * <p>
 * This interface representing below methods:
 * <ul>
 *     <li>{@link SimpleExpression#in(TeNamedOperator, String, int)}</li>
 *     <li>{@link SimpleExpression#notIn(TeNamedOperator, String, int)}</li>
 *     <li>other custom method</li>
 * </ul>
 *
 * @since 0.6.0
 */
@FunctionalInterface
public interface InNamedOperator {


    IPredicate apply(TeNamedOperator<SimpleExpression> namedOperator, String paramName, int size);


}
