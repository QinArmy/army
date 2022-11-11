package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._SpacePredicateExp;
import io.army.function.*;
import io.army.lang.Nullable;

import java.util.function.*;

/**
 * <p>
 * This interface extends {@link  IPredicate} and provide {@link #space()}
 * </p>
 *
 * @since 1.0
 */
public interface SpacePredicate<I extends Item> extends _SpacePredicateExp<I>, IPredicate {


    @Override
    SpacePredicate<I> bracket();


    @Override
    SpacePredicate<I> or(IPredicate predicate);

    @Override
    SpacePredicate<I> or(Supplier<IPredicate> supplier);

    @Override
    SpacePredicate<I> or(Function<Expression, IPredicate> expOperator, Expression operand);

    @Override
    <E extends RightOperand> SpacePredicate<I> or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> SpacePredicate<I> or(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    SpacePredicate<I> or(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    <T> SpacePredicate<I> or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    SpacePredicate<I> or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    SpacePredicate<I> or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    SpacePredicate<I> or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    SpacePredicate<I> or(Consumer<Consumer<IPredicate>> consumer);

    @Override
    SpacePredicate<I> ifOr(Supplier<IPredicate> supplier);

    @Override
    <E> SpacePredicate<I> ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> SpacePredicate<I> ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    SpacePredicate<I> ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    <T> SpacePredicate<I> ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    SpacePredicate<I> ifOr(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    SpacePredicate<I> ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);

    @Override
    SpacePredicate<I> ifOr(Consumer<Consumer<IPredicate>> consumer);


    @Override
    SpacePredicate<I> and(IPredicate predicate);

    @Override
    SpacePredicate<I> and(Supplier<IPredicate> supplier);

    @Override
    SpacePredicate<I> ifAnd(Supplier<IPredicate> supplier);

    @Override
    SpacePredicate<I> and(Function<Expression, IPredicate> expOperator, Expression operand);

    @Override
    SpacePredicate<I> and(UnaryOperator<IPredicate> expOperator, IPredicate operand);

    @Override
    <E extends RightOperand> SpacePredicate<I> and(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    SpacePredicate<I> and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator, BiFunction<DataField, String, Expression> namedOperator);

    @Override
    <T> SpacePredicate<I> and(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    SpacePredicate<I> and(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    SpacePredicate<I> and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    SpacePredicate<I> and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    SpacePredicate<I> and(UnaryOperator<IPredicate> predicateOperator, BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    <T> SpacePredicate<I> and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    SpacePredicate<I> and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    <T> SpacePredicate<I> and(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    <E extends RightOperand> IPredicate ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> SpacePredicate<I> ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    SpacePredicate<I> ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    <T> SpacePredicate<I> ifAnd(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    SpacePredicate<I> ifAnd(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    SpacePredicate<I> ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);

    @Override
    SpacePredicate<I> and(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    SpacePredicate<I> not();

    @Override
    SpacePredicate<I> ifNot(BooleanSupplier predicate);


    I space();


}
