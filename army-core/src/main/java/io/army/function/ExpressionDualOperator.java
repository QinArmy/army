package io.army.function;

import io.army.criteria.Expression;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ExpressionDualOperator<T, E extends Expression> {

    E apply(BiFunction<Expression, T, Expression> valueOperator, T value);

}
