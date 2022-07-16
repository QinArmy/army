package io.army.criteria;

import io.army.lang.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@code  ParamExpression}</li>
 *         <li>{@code  LiteralExpression}</li>
 *     </ul>
 * </p>
 */
public interface ValueExpression extends Expression {


    @Nullable
    Object value();

}
