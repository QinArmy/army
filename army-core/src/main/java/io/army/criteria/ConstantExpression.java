package io.army.criteria;

import io.army.lang.NonNull;

public interface ConstantExpression<E> extends ValueExpression<E> {

    @NonNull
    @Override
    Object value();
}
