package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;

/**
 * <p>
 * This interface representing below methods:
 *     <ul>
 *         <li>{@link Expression#in(TeNamedOperator, String, int)}</li>
 *         <li>{@link Expression#notIn(TeNamedOperator, String, int)}</li>
 *         <li>other custom method</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
@FunctionalInterface
public interface InNamedOperator {


    IPredicate apply(TeNamedOperator<Expression> namedOperator, String paramName, int size);


}
