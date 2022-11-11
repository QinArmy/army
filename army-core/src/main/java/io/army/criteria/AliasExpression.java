package io.army.criteria;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.criteria.impl._ItemExpression;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

public interface AliasExpression<I extends Item> extends _ItemExpression<I>, Statement._AsClause<I> {


    @Override
    AliasPredicate<I> equal(Expression operand);

    @Override
    <T> AliasPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> equalAny(SubQuery subQuery);

    @Override
    AliasPredicate<I> equalSome(SubQuery subQuery);


    @Override
    AliasPredicate<I> less(Expression operand);

    @Override
    <T> AliasPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> lessAny(SubQuery subQuery);

    @Override
    AliasPredicate<I> lessSome(SubQuery subQuery);

    @Override
    AliasPredicate<I> lessAll(SubQuery subQuery);

    @Override
    AliasPredicate<I> lessEqual(Expression operand);

    @Override
    <T> AliasPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> lessEqualAny(SubQuery subQuery);

    @Override
    AliasPredicate<I> lessEqualSome(SubQuery subQuery);

    @Override
    AliasPredicate<I> lessEqualAll(SubQuery subQuery);

    @Override
    AliasPredicate<I> great(Expression operand);

    @Override
    <T> AliasPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> greatAny(SubQuery subQuery);

    @Override
    AliasPredicate<I> greatSome(SubQuery subQuery);

    @Override
    AliasPredicate<I> greatAll(SubQuery subQuery);

    @Override
    AliasPredicate<I> greatEqual(Expression operand);

    @Override
    <T> AliasPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> greatEqualAny(SubQuery subQuery);

    @Override
    AliasPredicate<I> greatEqualSome(SubQuery subQuery);

    @Override
    AliasPredicate<I> greatEqualAll(SubQuery subQuery);

    @Override
    AliasPredicate<I> notEqual(Expression operand);

    @Override
    <T> AliasPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> notEqualAny(SubQuery subQuery);

    @Override
    AliasPredicate<I> notEqualSome(SubQuery subQuery);

    @Override
    AliasPredicate<I> notEqualAll(SubQuery subQuery);

    @Override
    AliasPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second);

    @Override
    <T> AliasPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    @Override
    AliasPredicate<I> isNull();

    @Override
    AliasPredicate<I> isNotNull();

    @Override
    AliasPredicate<I> in(Expression operand);

    @Override
    AliasPredicate<I> in(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> AliasPredicate<I> in(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    AliasPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    AliasPredicate<I> notIn(Expression operand);

    @Override
    AliasPredicate<I> notIn(SubQuery subQuery);

    @Override
    <T, O extends Collection<T>> AliasPredicate<I> notIn(BiFunction<Expression, O, Expression> operator, O operand);

    @Override
    AliasPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    @Override
    AliasPredicate<I> like(Expression pattern);

    @Override
    <T> AliasPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    AliasPredicate<I> notLike(Expression pattern);

    @Override
    <T> AliasPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> mod(Expression operand);

    @Override
    <T> _AliasExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> times(Expression operand);

    @Override
    <T> _AliasExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> plus(Expression operand);

    @Override
    <T> _AliasExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> minus(Expression minuend);

    @Override
    <T> _AliasExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> divide(Expression divisor);

    @Override
    <T> _AliasExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> negate();

    @Override
    _AliasExpression<I> bitwiseAnd(Expression operand);

    @Override
    <T> _AliasExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> bitwiseOr(Expression operand);

    @Override
    <T> _AliasExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> xor(Expression operand);

    @Override
    <T> _AliasExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> invert();

    @Override
    _AliasExpression<I> rightShift(Expression bitNumber);

    @Override
    <T> _AliasExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> leftShift(Expression bitNumber);

    @Override
    <T> _AliasExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    @Override
    _AliasExpression<I> mapTo(TypeMeta typeMeta);

    @Override
    _AliasExpression<I> bracket();


}
