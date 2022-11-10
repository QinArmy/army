package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.ItemExpression;
import io.army.criteria.ItemPredicate;
import io.army.criteria.dialect.SubQuery;
import io.army.function.BiAsExpFunction;
import io.army.function.BiAsFunction;

import java.util.function.Supplier;


public interface _ItemExpression<I extends Item> extends ItemExpression<I> {


    ItemPredicate<I> equal(Supplier<Expression> supplier);

    <R> R equal(BiAsFunction<ItemPredicate<I>, I, R> function);

    <R> R equal(BiAsExpFunction<ItemPredicate<I>, I, R> function);

    <R> R equal(BiAsExpFunction<ItemPredicate<I>, I, R> function, Expression operand);

    ItemPredicate<I> equalAny(Supplier<SubQuery> supplier);


    ItemPredicate<I> equalSome(Supplier<SubQuery> supplier);


}
