package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.TeExpression;
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
public interface Expression extends SelectionSpec, TypeInfer, SortItem, RightOperand {


    /**
     * relational operate with {@code =}
     * <p>
     * Operand will be wrapped with optimizing param
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     */
    IPredicate equal(Expression operand);

    /**
     * <p>
     * operator detail:
     *     <ul>
     *         <li>the first argument of operator is this</li>
     *         li>the second argument of operator is operand</li>
     *     </ul>
     * </p>
     * <p>
     *     operator possibly is the reference of below method:
     *     <ul>
     *         <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *         <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *         <li>{@link SQLs#namedParam(TypeInfer, String)}</li>
     *         <li>{@link SQLs#namedLiteral(TypeInfer, String)},only used in insert syntax</li>
     *         <li>custom method</li>
     *     </ul>
     * </p>
     *
     * @param operator the reference of method,Note: it's the reference of method,not lambda.
     * @param operand  non-null,it will pass to operator as the second argument of operator
     */
    <T> IPredicate equal(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * relational operate with {@code = ANY}
     */
    IPredicate equalAny(SubQuery subQuery);

    /**
     * relational operate with {@code = SOME}
     */
    IPredicate equalSome(SubQuery subQuery);


    IPredicate less(Expression operand);

    <T> IPredicate less(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate lessAny(SubQuery subQuery);

    IPredicate lessSome(SubQuery subQuery);

    IPredicate lessAll(SubQuery subQuery);


    IPredicate lessEqual(Expression operand);

    <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate lessEqualAny(SubQuery subQuery);


    IPredicate lessEqualSome(SubQuery subQuery);

    IPredicate lessEqualAll(SubQuery subQuery);

    IPredicate great(Expression operand);

    <T> IPredicate great(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate greatAny(SubQuery subQuery);

    IPredicate greatSome(SubQuery subQuery);

    IPredicate greatAll(SubQuery subQuery);

    IPredicate greatEqual(Expression operand);

    <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate greatEqualAny(SubQuery subQuery);

    IPredicate greatEqualSome(SubQuery subQuery);

    IPredicate greatEqualAll(SubQuery subQuery);


    IPredicate notEqual(Expression operand);

    <T> IPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate notEqualAny(SubQuery subQuery);

    IPredicate notEqualSome(SubQuery subQuery);

    IPredicate notEqualAll(SubQuery subQuery);

    IPredicate between(Expression first, Expression second);

    <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first, T second);

    IPredicate between(Supplier<ExpressionPair> supplier);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Expression operand);

    IPredicate in(SubQuery subQuery);

    <T, O extends Collection<T>> IPredicate in(BiFunction<Expression, O, Expression> operator, O operand);

    IPredicate in(TeExpression<Expression, String, Integer> namedOperator, String paramName, int size);


    IPredicate notIn(Expression operand);

    IPredicate notIn(SubQuery subQuery);

    <T, O extends Collection<T>> IPredicate notIn(BiFunction<Expression, O, Expression> operator, O operand);

    IPredicate notIn(TeExpression<Expression, String, Integer> namedOperator, String paramName, int size);

    IPredicate like(Expression pattern);

    <T> IPredicate like(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate notLike(Expression pattern);

    <T> IPredicate notLike(BiFunction<Expression, T, Expression> operator, T operand);

    Expression mod(Expression operand);

    Expression mod(Supplier<Expression> supplier);

    Expression mod(Function<Expression, Expression> function);

    <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression mod(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression mod(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);


    Expression times(Expression operand);

    Expression times(Supplier<Expression> supplier);

    Expression times(Function<Expression, Expression> function);


    <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression times(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression times(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);


    Expression plus(Expression operand);

    Expression plus(Supplier<Expression> supplier);

    Expression plus(Function<Expression, Expression> function);

    <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression plus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression plus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    Expression minus(Expression minuend);

    Expression minus(Supplier<Expression> supplier);

    Expression minus(Function<Expression, Expression> function);

    <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression minus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression minus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    Expression divide(Expression divisor);

    Expression divide(Supplier<Expression> supplier);

    Expression divide(Function<Expression, Expression> function);

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


    <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    Expression leftShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    @Override
    Expression asType(TypeMeta paramMeta);

    Expression bracket();



}
