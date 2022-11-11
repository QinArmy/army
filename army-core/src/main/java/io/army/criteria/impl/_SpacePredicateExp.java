package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SpacePredicate;
import io.army.criteria.dialect.SubQuery;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * <p>
 * This interface is base interface of {@link SpacePredicate}
 * </p>
 *
 * @param <I>
 */
public interface _SpacePredicateExp<I extends Item> extends _ItemExpression<I> {


    @Override
    SpacePredicate<I> equal(Expression operand);

    @Override
    <T> SpacePredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> equalAny(SubQuery subQuery);

    @Override
    SpacePredicate<I> equalSome(SubQuery subQuery);


    @Override
    SpacePredicate<I> less(Expression operand);

    @Override
    <T> SpacePredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> lessAny(SubQuery subQuery);

    @Override
    SpacePredicate<I> lessSome(SubQuery subQuery);

    @Override
    SpacePredicate<I> lessAll(SubQuery subQuery);

    @Override
    SpacePredicate<I> lessEqual(Expression operand);

    @Override
    <T> SpacePredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> lessEqualAny(SubQuery subQuery);

    @Override
    SpacePredicate<I> lessEqualSome(SubQuery subQuery);

    @Override
    SpacePredicate<I> lessEqualAll(SubQuery subQuery);

    @Override
    SpacePredicate<I> great(Expression operand);

    @Override
    <T> SpacePredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> greatAny(SubQuery subQuery);

    @Override
    SpacePredicate<I> greatSome(SubQuery subQuery);

    @Override
    SpacePredicate<I> greatAll(SubQuery subQuery);

    @Override
    SpacePredicate<I> greatEqual(Expression operand);

    @Override
    <T> SpacePredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> greatEqualAny(SubQuery subQuery);

    @Override
    SpacePredicate<I> greatEqualSome(SubQuery subQuery);

    @Override
    SpacePredicate<I> greatEqualAll(SubQuery subQuery);

    @Override
    SpacePredicate<I> notEqual(Expression operand);

    @Override
    <T> SpacePredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> notEqualAny(SubQuery subQuery);

    @Override
    SpacePredicate<I> notEqualSome(SubQuery subQuery);

    @Override
    SpacePredicate<I> notEqualAll(SubQuery subQuery);

    @Override
    SpacePredicate<I> between(Expression first, SQLs.WordAnd and, Expression second);

    @Override
    <T> SpacePredicate<I> between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    @Override
    SpacePredicate<I> isNull();

    @Override
    SpacePredicate<I> isNotNull();

    @Override
    SpacePredicate<I> in(Expression operand);

    @Override
    SpacePredicate<I> in(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> SpacePredicate<I> in(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    SpacePredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    SpacePredicate<I> notIn(Expression operand);

    @Override
    SpacePredicate<I> notIn(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> SpacePredicate<I> notIn(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    SpacePredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    SpacePredicate<I> like(Expression pattern);

    @Override
    <T> SpacePredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    SpacePredicate<I> notLike(Expression pattern);

    @Override
    <T> SpacePredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> mod(Expression operand);

    @Override
    <T> _SpacePredicateExp<I> mod(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> times(Expression operand);

    @Override
    <T> _SpacePredicateExp<I> times(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> plus(Expression operand);

    @Override
    <T> _SpacePredicateExp<I> plus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> minus(Expression minuend);

    @Override
    <T> _SpacePredicateExp<I> minus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> divide(Expression divisor);

    @Override
    <T> _SpacePredicateExp<I> divide(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> negate();

    @Override
    _SpacePredicateExp<I> bitwiseAnd(Expression operand);

    @Override
    <T> _SpacePredicateExp<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> bitwiseOr(Expression operand);

    @Override
    <T> _SpacePredicateExp<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> xor(Expression operand);

    @Override
    <T> _SpacePredicateExp<I> xor(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> invert();

    @Override
    _SpacePredicateExp<I> rightShift(Expression bitNumber);

    @Override
    <T> _SpacePredicateExp<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> leftShift(Expression bitNumber);

    @Override
    <T> _SpacePredicateExp<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _SpacePredicateExp<I> mapTo(TypeMeta typeMeta);

    @Override
    _SpacePredicateExp<I> bracket();


}
