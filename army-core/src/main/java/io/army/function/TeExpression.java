package io.army.function;

import io.army.criteria.Expression;

@FunctionalInterface
public interface TeExpression<F, S, T> {

    Expression apply(F f, S s, T t);
}
