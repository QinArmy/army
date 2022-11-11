package io.army.criteria.impl;

import io.army.criteria.AliasExpression;
import io.army.criteria.AliasPredicate;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.dialect.SubQuery;
import io.army.function.BiAsExpFunction;
import io.army.function.BiAsFunction;

import java.util.function.Supplier;


public interface _AliasExpression<I extends Item> extends AliasExpression<I> {


    AliasPredicate<I> equal(Supplier<Expression> supplier);

    <R> R equal(BiAsFunction<AliasPredicate<I>, I, R> function);

    <R> R equal(BiAsExpFunction<AliasPredicate<I>, I, R> function);

    <R> R equal(BiAsExpFunction<AliasPredicate<I>, I, R> function, Expression operand);

    AliasPredicate<I> equalAny(Supplier<SubQuery> supplier);


    AliasPredicate<I> equalSome(Supplier<SubQuery> supplier);


    @Override
    I as(String alias);


}
