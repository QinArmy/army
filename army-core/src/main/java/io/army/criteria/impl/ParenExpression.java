package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.ParenPredicate;
import io.army.criteria.dialect.SubQuery;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

public interface ParenExpression<I extends Item> extends _ItemExpression<I> {


    @Override
    ParenPredicate<I> equal(Expression operand);

    @Override
    <T> ParenPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> equalAny(SubQuery subQuery);

    @Override
    ParenPredicate<I> equalSome(SubQuery subQuery);


    @Override
    ParenPredicate<I> less(Expression operand);

    @Override
    <T> ParenPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> lessAny(SubQuery subQuery);

    @Override
    ParenPredicate<I> lessSome(SubQuery subQuery);

    @Override
    ParenPredicate<I> lessAll(SubQuery subQuery);

    @Override
    ParenPredicate<I> lessEqual(Expression operand);

    @Override
    <T> ParenPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> lessEqualAny(SubQuery subQuery);

    @Override
    ParenPredicate<I> lessEqualSome(SubQuery subQuery);

    @Override
    ParenPredicate<I> lessEqualAll(SubQuery subQuery);

    @Override
    ParenPredicate<I> great(Expression operand);

    @Override
    <T> ParenPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> greatAny(SubQuery subQuery);

    @Override
    ParenPredicate<I> greatSome(SubQuery subQuery);

    @Override
    ParenPredicate<I> greatAll(SubQuery subQuery);

    @Override
    ParenPredicate<I> greatEqual(Expression operand);

    @Override
    <T> ParenPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> greatEqualAny(SubQuery subQuery);

    @Override
    ParenPredicate<I> greatEqualSome(SubQuery subQuery);

    @Override
    ParenPredicate<I> greatEqualAll(SubQuery subQuery);

    @Override
    ParenPredicate<I> notEqual(Expression operand);

    @Override
    <T> ParenPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> notEqualAny(SubQuery subQuery);

    @Override
    ParenPredicate<I> notEqualSome(SubQuery subQuery);

    @Override
    ParenPredicate<I> notEqualAll(SubQuery subQuery);

    @Override
    ParenPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second);

    @Override
    <T> ParenPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    @Override
    ParenPredicate<I> isNull();

    @Override
    ParenPredicate<I> isNotNull();

    @Override
    ParenPredicate<I> in(Expression operand);

    @Override
    ParenPredicate<I> in(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> ParenPredicate<I> in(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    ParenPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    ParenPredicate<I> notIn(Expression operand);

    @Override
    ParenPredicate<I> notIn(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> ParenPredicate<I> notIn(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    ParenPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    ParenPredicate<I> like(Expression pattern);

    @Override
    <T> ParenPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ParenPredicate<I> notLike(Expression pattern);

    @Override
    <T> ParenPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> mod(Expression operand);

    @Override
    <T> _ParenExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> times(Expression operand);

    @Override
    <T> _ParenExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> plus(Expression operand);

    @Override
    <T> _ParenExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> minus(Expression minuend);

    @Override
    <T> _ParenExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> divide(Expression divisor);

    @Override
    <T> _ParenExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> negate();

    @Override
    _ParenExpression<I> bitwiseAnd(Expression operand);

    @Override
    <T> _ParenExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> bitwiseOr(Expression operand);

    @Override
    <T> _ParenExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> xor(Expression operand);

    @Override
    <T> _ParenExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> invert();

    @Override
    _ParenExpression<I> rightShift(Expression bitNumber);

    @Override
    <T> _ParenExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> leftShift(Expression bitNumber);

    @Override
    <T> _ParenExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ParenExpression<I> mapTo(TypeMeta typeMeta);

    @Override
    _ParenExpression<I> bracket();


}
