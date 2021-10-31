package io.army.criteria;

import io.army.stmt.ParamValue;

/**
 * extends {@link ParamValue} to avoid new instance of {@link ParamValue}
 *
 * @since 1.0
 */
public interface ParamExpression<E> extends ValueExpression<E>, ParamValue {


}
