package io.army.criteria;

import io.army.wrapper.ParamWrapper;

/**
 * extends {@link ParamWrapper} to avoid new instance of {@link ParamWrapper}
 * created  on 2018/12/4.
 */
public interface ParamExpression<E> extends Expression<E>, ParamWrapper {

    String MSG = "operation isn'field supported by ParamExpression";

    E value();

}
