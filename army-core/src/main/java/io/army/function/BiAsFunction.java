package io.army.function;

import io.army.criteria.Item;
import io.army.criteria.Selection;
import io.army.criteria.impl._ItemExpression;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface BiAsFunction<T extends Item, U extends Item, R>
        extends BiFunction<Function<_ItemExpression<U>, T>, Function<Selection, U>, R> {

}
