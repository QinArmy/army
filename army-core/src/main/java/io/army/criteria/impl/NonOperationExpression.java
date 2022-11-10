package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.ItemPredicate;
import io.army.criteria.Selection;
import io.army.criteria.SqlValueParam;
import io.army.criteria.dialect.SubQuery;
import io.army.function.BiAsExpFunction;
import io.army.function.BiAsFunction;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@code  SQLs.DefaultWord}</li>
 *         <li>{@code SQLs.NullWord}</li>
 *     </ul>
 * </p>
 */
abstract class NonOperationExpression implements ArmyExpression, _ItemExpression<Selection> {


    NonOperationExpression() {
    }


    @Override
    public final boolean isNullValue() {
        final boolean nullable;
        if (this instanceof SqlValueParam.SingleNonNamedValue) {
            nullable = ((SqlValueParam.SingleNonNamedValue) this).value() == null;
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final ItemPredicate<Selection> equal(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> equal(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> equal(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <R> R equal(BiAsFunction<ItemPredicate<Selection>, Selection, R> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <R> R equal(BiAsExpFunction<ItemPredicate<Selection>, Selection, R> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <R> R equal(BiAsExpFunction<ItemPredicate<Selection>, Selection, R> function, Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> equalAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> equalSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> equalAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> equalSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> less(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> less(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> great(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> great(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> greatEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> between(Expression first, StandardSyntax.WordAnd and, Expression second) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> between(BiFunction<Expression, T, Expression> operator, T first, StandardSyntax.WordAnd and, T second) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> isNull() {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> isNotNull() {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> in(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> in(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> ItemPredicate<Selection> in(BiFunction<Expression, O, Expression> operator
            , O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notIn(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notIn(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> ItemPredicate<Selection> notIn(BiFunction<Expression, O, Expression> operator
            , O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> like(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> like(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final ItemPredicate<Selection> notLike(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> ItemPredicate<Selection> notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> mod(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> mod(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> times(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> times(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> plus(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> plus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> minus(Expression minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> minus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> divide(Expression divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> divide(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> negate() {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> bitwiseAnd(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> bitwiseOr(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> xor(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> xor(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> invert() {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> rightShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> leftShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> _ItemExpression<Selection> leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> asType(TypeMeta paramMeta) {
        throw unsupportedOperation();
    }

    @Override
    public final _ItemExpression<Selection> bracket() {
        throw unsupportedOperation();
    }

    @Override
    public final Selection as(String alias) {
        if (this instanceof NonSelectionExpression) {
            throw unsupportedOperation();
        }
        return ArmySelections.forExp(this, alias);
    }

    static abstract class NonSelectionExpression extends NonOperationExpression {

        NonSelectionExpression() {
        }

    }//NonSelectionExpression

    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
