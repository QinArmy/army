package io.army.function;

import io.army.criteria.Item;
import io.army.criteria.ItemExpression;
import io.army.criteria.Selection;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface BiAsFunction<T extends Item, U extends Item, R>
        extends BiFunction<Function<ItemExpression<U>, T>, Function<Selection, U>, R> {

}
