package io.army.criteria;

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

    <T> IPredicate equal(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * relational operate with {@code = ANY}
     */
    <C> IPredicate equalAny(Function<C, SubQuery> function);

    /**
     * relational operate with {@code = ANY}
     */
    IPredicate equalAny(Supplier<SubQuery> supplier);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, SubQuery> function);

    /**
     * relational operate with {@code = SOME}
     */
    IPredicate equalSome(Supplier<SubQuery> subQuery);


    IPredicate less(Expression operand);

    <T> IPredicate less(BiFunction<Expression, T, Expression> operator, T operand);

    <C> IPredicate lessAny(Function<C, SubQuery> function);

    IPredicate lessAny(Supplier<SubQuery> supplier);

    <C> IPredicate lessSome(Function<C, SubQuery> function);

    IPredicate lessSome(Supplier<SubQuery> supplier);

    <C> IPredicate lessAll(Function<C, SubQuery> function);

    IPredicate lessAll(Supplier<SubQuery> supplier);


    IPredicate lessEqual(Expression operand);

    <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    <C> IPredicate lessEqualAny(Function<C, SubQuery> function);

    IPredicate lessEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate lessEqualSome(Function<C, SubQuery> function);

    IPredicate lessEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate lessEqualAll(Function<C, SubQuery> function);

    IPredicate lessEqualAll(Supplier<SubQuery> supplier);

    IPredicate great(Expression operand);

    <T> IPredicate great(BiFunction<Expression, T, Expression> operator, T operand);

    <C> IPredicate greatAny(Function<C, SubQuery> function);

    IPredicate greatAny(Supplier<SubQuery> supplier);

    <C> IPredicate greatSome(Function<C, SubQuery> function);

    IPredicate greatSome(Supplier<SubQuery> supplier);

    <C> IPredicate greatAll(Function<C, SubQuery> function);

    IPredicate greatAll(Supplier<SubQuery> supplier);

    IPredicate greatEqual(Expression operand);

    <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    <C> IPredicate greatEqualAny(Function<C, SubQuery> function);

    IPredicate greatEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate greatEqualSome(Function<C, SubQuery> function);

    IPredicate greatEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate greatEqualAll(Function<C, SubQuery> function);

    IPredicate greatEqualAll(Supplier<SubQuery> supplier);


    IPredicate notEqual(Expression operand);

    <T> IPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    <C> IPredicate notEqualAny(Function<C, SubQuery> function);

    IPredicate notEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, SubQuery> function);

    IPredicate notEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, SubQuery> function);

    IPredicate notEqualAll(Supplier<SubQuery> supplier);

    IPredicate between(Expression first, Expression second);

    <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first, T second);

    IPredicate between(Supplier<ExpressionPair> supplier);

    IPredicate betweenNamed(String firstParamName, String secondParamName);


    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Expression operand);

    <T, O extends Collection<T>> IPredicate in(BiFunction<Expression, O, Expression> operator, O operand);


    IPredicate inNamed(String paramName, int size);


    IPredicate notIn(Expression operand);

    <T, O extends Collection<T>> IPredicate notIn(BiFunction<Expression, O, Expression> operator, O operand);

    IPredicate notInNamed(String paramName, int size);

    IPredicate like(Expression pattern);

    <T> IPredicate like(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate notLike(Expression pattern);

    <T> IPredicate notLike(BiFunction<Expression, T, Expression> operator, T operand);

    Expression mod(Expression operand);

    Expression mod(Supplier<Expression> supplier);

    Expression mod(Function<Expression, Expression> function);

    <C> Expression mod(BiFunction<C, Expression, Expression> function);

    <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression mod(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression mod(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);


    Expression times(Expression operand);

    Expression times(Supplier<Expression> supplier);

    Expression times(Function<Expression, Expression> function);


    <C> Expression times(BiFunction<C, Expression, Expression> function);

    <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression times(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression times(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);


    Expression plus(Expression operand);

    Expression plus(Supplier<Expression> supplier);

    Expression plus(Function<Expression, Expression> function);

    <C> Expression plus(BiFunction<C, Expression, Expression> function);

    <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression plus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression plus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    Expression minus(Expression minuend);

    Expression minus(Supplier<Expression> supplier);

    Expression minus(Function<Expression, Expression> function);

    <C> Expression minus(BiFunction<C, Expression, Expression> function);

    <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression minus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression minus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    Expression divide(Expression divisor);

    Expression divide(Supplier<Expression> supplier);

    Expression divide(Function<Expression, Expression> function);

    <C> Expression divide(BiFunction<C, Expression, Expression> function);

    <T> Expression divide(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression divide(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression divide(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    Expression negate();

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseAnd(Expression operand);

    Expression bitwiseAnd(Supplier<Expression> supplier);

    Expression bitwiseAnd(Function<Expression, Expression> function);

    <C> Expression bitwiseAnd(BiFunction<C, Expression, Expression> function);

    <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression bitwiseAnd(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseOr(Expression operand);

    Expression bitwiseOr(Supplier<Expression> supplier);

    Expression bitwiseOr(Function<Expression, Expression> function);

    <C> Expression bitwiseOr(BiFunction<C, Expression, Expression> function);

    <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression bitwiseOr(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression xor(Expression operand);

    Expression xor(Supplier<Expression> supplier);

    Expression xor(Function<Expression, Expression> function);

    <C> Expression xor(BiFunction<C, Expression, Expression> function);

    <T> Expression xor(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression xor(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression xor(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

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

    Expression rightShift(Supplier<Expression> supplier);

    Expression rightShift(Function<Expression, Expression> function);

    <C> Expression rightShift(BiFunction<C, Expression, Expression> function);

    <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression rightShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression leftShift(Expression bitNumber);

    Expression leftShift(Supplier<Expression> supplier);

    Expression leftShift(Function<Expression, Expression> function);

    <C> Expression leftShift(BiFunction<C, Expression, Expression> function);

    <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression leftShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    Expression asType(TypeMeta paramMeta);

    Expression bracket();

    SortItem asc();

    SortItem desc();


}
