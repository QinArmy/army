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
    public final IPredicate equal(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equal(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equal(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equal(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalSome(Supplier<? extends SubQuery> subQuery) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate less(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate less(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate less(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate less(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate less(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessAll(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate lessEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate great(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate great(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate great(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate great(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate great(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatAll(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate greatEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAny(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualSome(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, ? extends SubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAll(Supplier<? extends SubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Expression first, Expression second) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Object first, Object second) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Supplier<?> firstSupplier, Supplier<?> secondSupplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Supplier<ExpressionPair> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Function<Expression, ExpressionPair> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate between(BiFunction<C, Expression, ExpressionPair> function) {
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
    public final IPredicate in(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate in(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName) {
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
    public final IPredicate notIn(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notIn(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<? super Expression, O, ? extends Expression> operator, O operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName) {
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
    public final IPredicate like(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate like(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate like(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate like(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Expression pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notLike(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        throw unsupportedOperation();
    }


    @Override
    public final Expression mod(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression mod(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression mod(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression mod(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression mod(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression modNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression times(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression times(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression times(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression times(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression timesNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression plus(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression plus(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression plus(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plus(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression plusNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Expression minuend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression minus(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression minus(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression minus(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minus(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression minusNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Expression divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression divide(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression divide(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression divide(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divide(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression divideNamed(String paramName) {
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
    public final Expression bitwiseAnd(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(Function<Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression bitwiseAnd(BiFunction<C, Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<Expression, Collection<?>, Expression> operator, Collection<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<Expression, Collection<?>, Expression> operator, Supplier<Collection<?>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseAndNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression bitwiseOr(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOr(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression bitwiseOrNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Expression operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression xor(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression xor(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression xor(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xor(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression xorNamed(String paramName) {
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
    public final Expression rightShift(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression rightShift(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression rightShift(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression rightShift(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShift(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression rightShiftNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Expression bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Supplier<? extends Expression> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(Function<? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> Expression leftShift(BiFunction<C, ? super Expression, ? extends Expression> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression leftShift(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        throw unsupportedOperation();
    }

    @Override
    public final <T> Expression leftShift(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShift(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression leftShiftNamed(String paramName) {
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
