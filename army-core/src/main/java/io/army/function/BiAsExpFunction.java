package io.army.function;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.ItemExpression;
import io.army.criteria.Selection;

import java.util.function.Function;

public interface BiAsExpFunction<T extends Item, U extends Item, R> {

    R apply(Expression exp, Function<ItemExpression<U>, T> endFunc, Function<Selection, U> asFunc);

}
