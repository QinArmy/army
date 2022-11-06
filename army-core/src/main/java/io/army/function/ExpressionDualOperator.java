package io.army.function;

import io.army.criteria.Expression;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ExpressionDualOperator<T, E extends Expression>
        extends BiFunction<BiFunction<Expression, T, Expression>, T, E> {

}
