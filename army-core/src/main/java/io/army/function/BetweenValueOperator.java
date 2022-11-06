package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.SQLs;

import java.util.function.BiFunction;


public interface BetweenValueOperator<T> {

    IPredicate apply(BiFunction<Expression, T, Expression> valueOperator, T first, SQLs.WordAnd and, T second);

}
