package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@code  SQLs.DefaultWord}</li>
 *         <li>{@code SQLs.NullWord}</li>
 *     </ul>
 * </p>
 */
abstract class NonOperationExpression implements ArmyExpression {


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
    public final IPredicate equal(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate equal(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate equalAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate equalSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate less(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate less(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate great(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate great(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate greatEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate between(Expression first, SQLsSyntax.WordAnd and, Expression second) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate between(BiFunction<Expression, T, Expression> operator, T first, SQLsSyntax.WordAnd and, T second) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate isNull() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate isNotNull() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate in(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate in(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> AliasPredicate<Selection> in(BiFunction<Expression, O, Expression> operator
            , O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notIn(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notIn(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> OperationPredicate notIn(BiFunction<Expression, O, Expression> operator
            , O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate like(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate like(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationPredicate notLike(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationPredicate notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression mod(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression mod(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression times(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression times(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression plus(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression plus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression minus(Expression minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression minus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression divide(Expression divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression divide(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression negate() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression bitwiseAnd(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression bitwiseOr(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression xor(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression xor(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression invert() {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression rightShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression leftShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> OperationExpression leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression mapTo(TypeMeta typeMeta) {
        throw unsupportedOperation();
    }

    @Override
    public final OperationExpression bracket() {
        throw unsupportedOperation();
    }


    static abstract class NonSelectionExpression extends NonOperationExpression {

        NonSelectionExpression() {
        }

    }//NonSelectionExpression

    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
