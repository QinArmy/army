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
 */
interface ValueExpression extends Expression {

    @Nullable
    Object value();

}
