package io.army.function;

import io.army.criteria.CompoundPredicate;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;

import javax.annotation.Nullable;

import java.util.function.BiFunction;

@FunctionalInterface
public interface DialectBooleanOperator<T> {


    CompoundPredicate apply(BiFunction<SimpleExpression, Expression, CompoundPredicate> operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);

}
