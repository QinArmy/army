package io.army.criteria.impl;

import io.army.criteria.AliasPredicate;
import io.army.criteria.Expression;
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
abstract class NonOperationExpression implements ArmyExpression, _AliasExpression<Selection> {


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
    public final OperationPredicate<Selection> equal(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> equal(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> equal(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <R> R equal(BiAsFunction<AliasPredicate<Selection>, Selection, R> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <R> R equal(BiAsExpFunction<AliasPredicate<Selection>, Selection, R> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <R> R equal(BiAsExpFunction<AliasPredicate<Selection>, Selection, R> function, Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> equalAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> equalSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> equalAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> equalSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> less(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> less(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> great(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> great(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> greatEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> between(Expression first, SQLsSyntax.WordAnd and, Expression second) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> between(BiFunction<Expression, T, Expression> operator, T first, SQLsSyntax.WordAnd and, T second) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> isNull() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> isNotNull() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> in(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> in(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> AliasPredicate<Selection> in(BiFunction<Expression, O, Expression> operator
            , O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notIn(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notIn(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> OperationPredicate<Selection> notIn(BiFunction<Expression, O, Expression> operator
            , O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> like(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> like(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate<Selection> notLike(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate<Selection> notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> mod(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> mod(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> times(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> times(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> plus(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> plus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> minus(Expression minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> minus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> divide(Expression divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> divide(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> negate() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> bitwiseAnd(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> bitwiseOr(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> xor(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> xor(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> invert() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> rightShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> leftShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression<Selection> leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> asType(TypeMeta paramMeta) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression<Selection> bracket() {
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
