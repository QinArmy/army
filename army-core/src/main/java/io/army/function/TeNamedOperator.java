package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.RowExpression;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;

/**
 * <ul>
 *     <li>{@link SQLs#namedRowParam(TypeInfer, String, int)}</li>
 *     <li>{@link SQLs#namedRowLiteral(TypeInfer, String, int)}</li>
 *     <li>other custom method</li>
 * </ul>
 *
 * @since 0.6.0
 */
@FunctionalInterface
public interface TeNamedOperator<E extends Expression> {

    RowExpression apply(E exp, String paramName, int size);


}
