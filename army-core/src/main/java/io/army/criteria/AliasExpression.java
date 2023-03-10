package io.army.criteria;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.criteria.impl._ItemExpression;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;
@Deprecated
public interface AliasExpression<I extends Item> extends _ItemExpression<I>, Statement._AsClause<I> {


    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> equal(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> equalAny(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> equalSome(SubQuery subQuery);


    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> less(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessAny(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessSome(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessAll(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessEqual(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessEqualAny(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessEqualSome(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> lessEqualAll(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> great(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatAny(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatSome(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatAll(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatEqual(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatEqualAny(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatEqualSome(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> greatEqualAll(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notEqual(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notEqualAny(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notEqualSome(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notEqualAll(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notBetween(Expression first, SQLs.WordAnd and, Expression second);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> notBetween(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> is(SQLs.BooleanTestOperand operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> isNot(SQLs.BooleanTestOperand operand);


    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> isNull();

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> isNotNull();

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> in(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> in(SubQuery subQuery);


    /**
     * {@inheritDoc}
     */
    @Override
    <T extends Collection<?>> AliasPredicate<I> in(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notIn(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notIn(SubQuery subQuery);

    /**
     * {@inheritDoc}
     */
    @Override
    <T extends Collection<?>> AliasPredicate<I> notIn(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> like(Expression pattern);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    AliasPredicate<I> notLike(Expression pattern);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> AliasPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> mod(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> times(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> plus(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> minus(Expression minuend);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> divide(Expression divisor);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> negate();

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> bitwiseAnd(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> bitwiseOr(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> xor(Expression operand);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> invert();

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> rightShift(Expression bitNumber);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> leftShift(Expression bitNumber);

    /**
     * {@inheritDoc}
     */
    @Override
    <T> _AliasExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> mapTo(TypeMeta typeMeta);

    /**
     * {@inheritDoc}
     */
    @Override
    _AliasExpression<I> bracket();


}
