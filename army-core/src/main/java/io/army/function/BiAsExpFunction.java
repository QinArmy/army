package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Selection;
import io.army.criteria.impl._AliasExpression;

import java.util.function.Function;

@FunctionalInterface
public interface BiAsExpFunction<T extends Item, U extends Item, R> {

    R apply(Expression exp, Function<_AliasExpression<U>, T> endFunc, Function<Selection, U> asFunc);

}
