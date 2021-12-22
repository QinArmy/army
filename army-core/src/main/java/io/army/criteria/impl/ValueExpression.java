package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.lang.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link ParamExpression}</li>
 *         <li>{@link LiteralExpression}</li>
 *     </ul>
 * </p>
 *
 * @param <E> java type of expression
 */
interface ValueExpression<E> extends Expression<E> {

    @Nullable
    E value();

}
