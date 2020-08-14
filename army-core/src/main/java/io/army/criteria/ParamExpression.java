package io.army.criteria;

import io.army.wrapper.ParamWrapper;

/**
 * extends {@link ParamWrapper} to avoid new instance of {@link ParamWrapper}
 *
 * @since 1.0
 */
public interface ParamExpression<E> extends ValueExpression<E>, ParamWrapper {


}
