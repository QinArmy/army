package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SQLs.DefaultWord}</li>
 *         <li>{@link SQLs.NullWord}</li>
 *     </ul>
 * </p>
 */
abstract class NonOperationExpression implements ArmyExpression {


    NonOperationExpression() {
    }


    @Override
    public final Selection as(String alias) {
        if (!(this instanceof SQLParam || this instanceof SQLs.NullWord)) {
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
        if (!(this instanceof SQLs.NullWord)) {
            throw unsupportedOperation();
        }
        return CastExpression.cast(this, paramMeta);
    }

    @Override
    public final IPredicate isNull() {
        if (!(this instanceof SQLs.NullWord)) {
            throw unsupportedOperation();
        }
        return UnaryPredicate.create(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        if (!(this instanceof SQLs.NullWord)) {
            throw unsupportedOperation();
        }
        return UnaryPredicate.create(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final IPredicate equal(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate equal(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalSome(Supplier<SubQuery> subQuery) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate less(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate less(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate lessEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate great(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate great(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate greatEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notEqual(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAny(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualSome(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAll(Supplier<SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Expression first, Expression second) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(BiFunction<Expression, Object, Expression> operator, Object first, Object second) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Supplier<ExpressionPair> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate betweenNamed(String firstParamName, String secondParamName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<Expression, O, Expression> operator, O operand) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate inNamed(String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<Expression, O, Expression> operator, O operand) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate notInNamed(String paramName, int size) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate like(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression mod(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression mod(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression times(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression times(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression plus(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression plus(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression minus(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression minus(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression divide(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression divide(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression bitwiseAnd(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression bitwiseOr(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression xor(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression xor(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression rightShift(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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
    public final <C> Expression leftShift(BiFunction<C, Expression, Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
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

    @Override
    public final SortItem asc() {
        throw unsupportedOperation();
    }

    @Override
    public final SortItem desc() {
        throw unsupportedOperation();
    }


    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
