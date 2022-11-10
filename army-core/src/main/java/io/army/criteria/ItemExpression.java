package io.army.criteria;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._ItemExpression;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

public interface ItemExpression<I extends Item> extends Expression, Statement._AsClause<I> {


    @Override
    ItemPredicate<I> equal(Expression operand);

    @Override
    <T> ItemPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> equalAny(SubQuery subQuery);

    @Override
    ItemPredicate<I> equalSome(SubQuery subQuery);


    @Override
    ItemPredicate<I> less(Expression operand);

    @Override
    <T> ItemPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> lessAny(SubQuery subQuery);

    @Override
    ItemPredicate<I> lessSome(SubQuery subQuery);

    @Override
    ItemPredicate<I> lessAll(SubQuery subQuery);

    @Override
    ItemPredicate<I> lessEqual(Expression operand);

    @Override
    <T> ItemPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> lessEqualAny(SubQuery subQuery);

    @Override
    ItemPredicate<I> lessEqualSome(SubQuery subQuery);

    @Override
    ItemPredicate<I> lessEqualAll(SubQuery subQuery);

    @Override
    ItemPredicate<I> great(Expression operand);

    @Override
    <T> ItemPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> greatAny(SubQuery subQuery);

    @Override
    ItemPredicate<I> greatSome(SubQuery subQuery);

    @Override
    ItemPredicate<I> greatAll(SubQuery subQuery);

    @Override
    ItemPredicate<I> greatEqual(Expression operand);

    @Override
    <T> ItemPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> greatEqualAny(SubQuery subQuery);

    @Override
    ItemPredicate<I> greatEqualSome(SubQuery subQuery);

    @Override
    ItemPredicate<I> greatEqualAll(SubQuery subQuery);

    @Override
    ItemPredicate<I> notEqual(Expression operand);

    @Override
    <T> ItemPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> notEqualAny(SubQuery subQuery);

    @Override
    ItemPredicate<I> notEqualSome(SubQuery subQuery);

    @Override
    ItemPredicate<I> notEqualAll(SubQuery subQuery);

    @Override
    ItemPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second);

    @Override
    <T> ItemPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    @Override
    ItemPredicate<I> isNull();

    @Override
    ItemPredicate<I> isNotNull();

    @Override
    ItemPredicate<I> in(Expression operand);

    @Override
    ItemPredicate<I> in(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> ItemPredicate<I> in(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    ItemPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    ItemPredicate<I> notIn(Expression operand);

    @Override
    ItemPredicate<I> notIn(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> ItemPredicate<I> notIn(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    ItemPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    ItemPredicate<I> like(Expression pattern);

    @Override
    <T> ItemPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    ItemPredicate<I> notLike(Expression pattern);

    @Override
    <T> ItemPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> mod(Expression operand);

    @Override
    <T> _ItemExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> times(Expression operand);

    @Override
    <T> _ItemExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> plus(Expression operand);

    @Override
    <T> _ItemExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> minus(Expression minuend);

    @Override
    <T> _ItemExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> divide(Expression divisor);

    @Override
    <T> _ItemExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> negate();

    @Override
    _ItemExpression<I> bitwiseAnd(Expression operand);

    @Override
    <T> _ItemExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> bitwiseOr(Expression operand);

    @Override
    <T> _ItemExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> xor(Expression operand);

    @Override
    <T> _ItemExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> invert();

    @Override
    _ItemExpression<I> rightShift(Expression bitNumber);

    @Override
    <T> _ItemExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> leftShift(Expression bitNumber);

    @Override
    <T> _ItemExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _ItemExpression<I> asType(TypeMeta paramMeta);

    @Override
    _ItemExpression<I> bracket();


    @Override
    I as(String alias);


}
