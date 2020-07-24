package io.army.criteria;

import io.army.wrapper.ParamWrapper;

public interface NamedParamExpression<E> extends Expression<E>, ParamWrapper {

    String name();


    /**
     * @throws UnsupportedOperationException always throws.
     */
    @Override
    Object value() throws UnsupportedOperationException;
}
