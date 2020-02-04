package io.army.criteria;

import io.army.criteria.Expression;
import io.army.lang.Nullable;

public interface ConstantExpression<E> extends Expression<E> {

    E constant();
}
