package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interface representing the sql expression, eg: column,function.
 *
 * @see FieldMeta
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression extends SelectionSpec, TypeInfer, SortItem, SetRightItem {


    /**
     * relational operate with {@code =}
     * <p>
     * Operand will be wrapped with optimizing param
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     */
    IPredicate equal(Expression operand);

    IPredicate equal(Supplier<? extends Expression> supplier);

    IPredicate equal(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate equal(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate equal(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate equalNamed(String paramName);

    /**
     * relational operate with {@code = ANY}
     */
    <C> IPredicate equalAny(Function<C, ? extends SubQuery> function);

    /**
     * relational operate with {@code = ANY}
     */
    IPredicate equalAny(Supplier<? extends SubQuery> supplier);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, ? extends SubQuery> function);

    /**
     * relational operate with {@code = SOME}
     */
    IPredicate equalSome(Supplier<? extends SubQuery> subQuery);


    IPredicate less(Expression operand);

    IPredicate less(Supplier<? extends Expression> supplier);

    IPredicate less(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate less(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate less(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate lessNamed(String paramName);

    <C> IPredicate lessAny(Function<C, ? extends SubQuery> function);

    IPredicate lessAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessSome(Function<C, ? extends SubQuery> function);

    IPredicate lessSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessAll(Function<C, ? extends SubQuery> function);

    IPredicate lessAll(Supplier<? extends SubQuery> supplier);


    IPredicate lessEqual(Expression operand);

    IPredicate lessEqual(Supplier<? extends Expression> supplier);

    IPredicate lessEqual(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate lessEqual(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate lessEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate lessEqualNamed(String paramName);


    <C> IPredicate lessEqualAny(Function<C, ? extends SubQuery> function);

    IPredicate lessEqualAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessEqualSome(Function<C, ? extends SubQuery> function);

    IPredicate lessEqualSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessEqualAll(Function<C, ? extends SubQuery> function);

    IPredicate lessEqualAll(Supplier<? extends SubQuery> supplier);


    IPredicate great(Expression operand);


    IPredicate great(Supplier<? extends Expression> supplier);

    IPredicate great(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate great(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate great(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate greatNamed(String paramName);

    <C> IPredicate greatAny(Function<C, ? extends SubQuery> function);

    IPredicate greatAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatSome(Function<C, ? extends SubQuery> function);

    IPredicate greatSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatAll(Function<C, ? extends SubQuery> function);

    IPredicate greatAll(Supplier<? extends SubQuery> supplier);

    IPredicate greatEqual(Expression operand);

    IPredicate greatEqual(Supplier<? extends Expression> supplier);

    IPredicate greatEqual(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate greatEqual(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate greatEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate greatEqualNamed(String paramName);

    <C> IPredicate greatEqualAny(Function<C, ? extends SubQuery> function);

    IPredicate greatEqualAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatEqualSome(Function<C, ? extends SubQuery> function);

    IPredicate greatEqualSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatEqualAll(Function<C, ? extends SubQuery> function);

    IPredicate greatEqualAll(Supplier<? extends SubQuery> supplier);


    IPredicate notEqual(Expression operand);

    IPredicate notEqual(Supplier<? extends Expression> supplier);

    IPredicate notEqual(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate notEqual(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate notEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate notEqualNamed(String paramName);

    <C> IPredicate notEqualAny(Function<C, ? extends SubQuery> function);

    IPredicate notEqualAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, ? extends SubQuery> function);

    IPredicate notEqualSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, ? extends SubQuery> function);

    IPredicate notEqualAll(Supplier<? extends SubQuery> supplier);

    IPredicate between(Expression first, Expression second);

    IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Object first, Object second);

    IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Supplier<?> firstSupplier, Supplier<?> secondSupplier);

    IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    IPredicate between(Supplier<ExpressionPair> supplier);

    IPredicate between(Function<Expression, ExpressionPair> function);

    <C> IPredicate between(BiFunction<C, Expression, ExpressionPair> function);

    IPredicate betweenNamed(String firstParamName, String secondParamName);


    IPredicate isNull();

    IPredicate isNotNull();

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#preferLiteralParams(TypeMeta, Collection)}.
     * </p>
     */
    IPredicate in(Expression operand);

    IPredicate in(Supplier<? extends Expression> supplier);

    IPredicate in(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate in(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, O operand);

    <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier);

    IPredicate in(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName);


    IPredicate inNamed(String paramName, int size);

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#preferLiteralParams(TypeMeta, Collection)}.
     * </p>
     */
    IPredicate notIn(Expression operand);

    IPredicate notIn(Supplier<? extends Expression> supplier);

    IPredicate notIn(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate notIn(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T, O extends Collection<T>> IPredicate notIn(BiFunction<? super Expression, O, ? extends Expression> operator, O operand);

    <T, O extends Collection<T>> IPredicate notIn(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier);

    IPredicate notIn(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName);

    IPredicate notInNamed(String paramName, int size);

    IPredicate like(Expression pattern);

    IPredicate like(Supplier<? extends Expression> supplier);

    IPredicate like(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate like(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate like(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate like(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate like(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate likeNamed(String paramName);


    IPredicate notLike(Expression pattern);

    IPredicate notLike(Supplier<? extends Expression> supplier);

    IPredicate notLike(Function<? super Expression, ? extends Expression> function);

    <C> IPredicate notLike(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> IPredicate notLike(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> IPredicate notLike(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    IPredicate notLike(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    IPredicate notLikeNamed(String paramName);


    Expression mod(Expression operand);

    Expression mod(Supplier<? extends Expression> supplier);

    Expression mod(Function<? super Expression, ? extends Expression> function);

    <C> Expression mod(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression mod(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression mod(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression mod(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression modNamed(String paramName);


    Expression times(Expression operand);

    Expression times(Supplier<? extends Expression> supplier);

    Expression times(Function<? super Expression, ? extends Expression> function);

    <C> Expression times(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression times(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression times(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression times(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression timesNamed(String paramName);


    Expression plus(Expression operand);

    Expression plus(Supplier<? extends Expression> supplier);

    Expression plus(Function<? super Expression, ? extends Expression> function);

    <C> Expression plus(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression plus(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression plus(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression plus(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression plusNamed(String paramName);

    Expression minus(Expression minuend);

    Expression minus(Supplier<? extends Expression> supplier);

    Expression minus(Function<? super Expression, ? extends Expression> function);

    <C> Expression minus(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression minus(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression minus(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression minus(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression minusNamed(String paramName);

    Expression divide(Expression divisor);

    Expression divide(Supplier<? extends Expression> supplier);

    Expression divide(Function<? super Expression, ? extends Expression> function);

    <C> Expression divide(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression divide(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression divide(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression divide(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression divideNamed(String paramName);

    Expression negate();

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseAnd(Expression operand);

    Expression bitwiseAnd(Supplier<? extends Expression> supplier);

    Expression bitwiseAnd(Function<? super Expression, ? extends Expression> function);

    <C> Expression bitwiseAnd(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression bitwiseAnd(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression bitwiseAnd(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression bitwiseAnd(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression bitwiseAndNamed(String paramName);


    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseOr(Expression operand);

    Expression bitwiseOr(Supplier<? extends Expression> supplier);

    Expression bitwiseOr(Function<? super Expression, ? extends Expression> function);

    <C> Expression bitwiseOr(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression bitwiseOr(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression bitwiseOr(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression bitwiseOr(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression bitwiseOrNamed(String paramName);
    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression xor(Expression operand);

    Expression xor(Supplier<? extends Expression> supplier);

    Expression xor(Function<? super Expression, ? extends Expression> function);

    <C> Expression xor(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression xor(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression xor(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression xor(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression xorNamed(String paramName);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression invert();

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression rightShift(Expression bitNumber);

    Expression rightShift(Supplier<? extends Expression> supplier);

    Expression rightShift(Function<? super Expression, ? extends Expression> function);

    <C> Expression rightShift(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression rightShift(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression rightShift(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression rightShift(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression rightShiftNamed(String paramName);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression leftShift(Expression bitNumber);

    Expression leftShift(Supplier<? extends Expression> supplier);

    Expression leftShift(Function<? super Expression, ? extends Expression> function);

    <C> Expression leftShift(BiFunction<C, ? super Expression, ? extends Expression> function);

    <T> Expression leftShift(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand);

    <T> Expression leftShift(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier);

    Expression leftShift(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

    Expression leftShiftNamed(String paramName);

    @Override
    Expression asType(TypeMeta paramMeta);

    Expression bracket();

    SortItem asc();

    SortItem desc();


}
