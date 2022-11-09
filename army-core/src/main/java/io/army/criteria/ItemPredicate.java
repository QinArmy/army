package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.*;
import io.army.lang.Nullable;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ItemPredicate<I extends Item> extends IPredicate, ItemExpression<I>, Statement._AsClause<I> {

    @Override
    ItemPredicate<I> bracket();

    @Override
    ItemPredicate<I> or(IPredicate predicate);

    @Override
    ItemPredicate<I> or(Supplier<IPredicate> supplier);

    @Override
    ItemPredicate<I> or(Function<Expression, IPredicate> expOperator, Expression operand);

    @Override
    <E extends RightOperand> IPredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> ItemPredicate<I> or(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    ItemPredicate<I> or(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    <T> ItemPredicate<I> or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    ItemPredicate<I> or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    ItemPredicate<I> or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    ItemPredicate<I> or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    ItemPredicate<I> ifOr(Supplier<IPredicate> supplier);

    @Override
    <E> IPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> ItemPredicate<I> ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    ItemPredicate<I> ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    <T> ItemPredicate<I> ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    ItemPredicate<I> ifOr(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    ItemPredicate<I> ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);

    @Override
    ItemPredicate<I> and(IPredicate predicate);

    @Override
    ItemPredicate<I> and(Supplier<IPredicate> supplier);

    @Override
    ItemPredicate<I> ifAnd(Supplier<IPredicate> supplier);

    @Override
    ItemPredicate<I> and(Function<Expression, IPredicate> expOperator, Expression operand);

    @Override
    <E extends RightOperand> IPredicate and(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    ItemPredicate<I> and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator, BiFunction<DataField, String, Expression> namedOperator);

    @Override
    <T> ItemPredicate<I> and(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    ItemPredicate<I> and(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    ItemPredicate<I> and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    ItemPredicate<I> and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    <T> ItemPredicate<I> and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    ItemPredicate<I> and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    <E extends RightOperand> IPredicate ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> ItemPredicate<I> ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    ItemPredicate<I> ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    <T> ItemPredicate<I> ifAnd(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    ItemPredicate<I> ifAnd(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    ItemPredicate<I> ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);


    @Override
    ItemPredicate<I> not();

    @Override
    ItemPredicate<I> ifNot(BooleanSupplier predicate);


}
