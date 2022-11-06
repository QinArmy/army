package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.function.TeNamedOperator;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
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
abstract class NonOperationExpression implements ArmyExpression {


    NonOperationExpression() {
    }


    @Override
    public final Selection as(String alias) {
        if (!(this instanceof SQLParam || this == SQLs.NULL)) {
            throw unsupportedOperation();
        }
        return Selections.forExp(this, alias);
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
    public final Expression asType(TypeMeta paramMeta) {
        if (this != SQLs.NULL) {
            throw unsupportedOperation();
        }
        return CastExpression.cast(this, paramMeta);
    }

    @Override
    public final IPredicate isNull() {
        if (this != SQLs.NULL) {
            throw unsupportedOperation();
        }
        return UnaryPredicate.create(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        if (this != SQLs.NULL) {
            throw unsupportedOperation();
        }
        return UnaryPredicate.create(UnaryOperator.IS_NOT_NULL, this);
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
    public final IPredicate equalAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate less(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate less(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate lessEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate great(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate great(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate greatEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first
            , SQLs.WordAnd and, T second) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<Expression, O, Expression> operator, O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(SubQuery subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<Expression, O, Expression> operator, O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate like(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate like(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }


    @Override
    public final Expression mod(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression mod(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression times(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }
    @Override
    public final <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression plus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Expression minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression minus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Expression divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }
    @Override
    public final <T> Expression divide(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression divide(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression negate() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }


    @Override
    public final Expression bitwiseAnd(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }
    @Override
    public final <T> Expression xor(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression xor(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression invert() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Supplier<Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Function<Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bracket() {
        throw unsupportedOperation();
    }


    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
