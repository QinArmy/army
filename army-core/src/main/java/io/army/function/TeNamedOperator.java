package io.army.function;

import io.army.criteria.Expression;

@FunctionalInterface
public interface TeNamedOperator<E extends Expression> {

    Expression apply(E exp, String paramName, int size);


}
