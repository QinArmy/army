package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SimpleExpression;
import io.army.criteria.impl.SQLs;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BetweenDualOperator<T, U> {

    IPredicate apply(BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef, U second);

}
