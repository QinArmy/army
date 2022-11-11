package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;

/**
 * <ul>
 *     <li>{@link SQLs#namedMultiParams(TypeInfer, String, int)}</li>
 *     <li>{@link SQLs#namedMultiLiterals(TypeInfer, String, int)}</li>
 *     <li>other custom method</li>
 * </ul>
 *
 * @since 1.0
 */
@FunctionalInterface
public interface TeNamedOperator<E extends Expression> {

    Expression apply(E exp, String paramName, int size);


}
