package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SimpleExpression;
import io.army.criteria.impl.SQLs;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BetweenValueOperator<T> {

    IPredicate apply(BiFunction<SimpleExpression, T, Expression> valueOperator, T first, SQLs.WordAnd and, T second);

}
