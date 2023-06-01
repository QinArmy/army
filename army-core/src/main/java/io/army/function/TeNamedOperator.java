package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.RowExpression;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;

/**
 * <ul>
 *     <li>{@link SQLs#namedMultiParam(TypeInfer, String, int)}</li>
 *     <li>{@link SQLs#namedMultiLiteral(TypeInfer, String, int)}</li>
 *     <li>other custom method</li>
 * </ul>
 *
 * @since 1.0
 */
@FunctionalInterface
public interface TeNamedOperator<E extends Expression> {

    RowExpression apply(E exp, String paramName, int size);


}
