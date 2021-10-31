package io.army.criteria;

import io.army.stmt.ParamValue;

public interface NamedParamExpression<E> extends Expression<E>, ParamValue {

    String name();


    /**
     * @throws UnsupportedOperationException always throws.
     */
    @Override
    Object value() throws UnsupportedOperationException;
}
