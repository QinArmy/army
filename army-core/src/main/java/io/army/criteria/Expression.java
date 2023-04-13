package io.army.criteria;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.BiFunction;

/**
 * Interface representing the sql expression, eg: column,function.
 *
 * @see FieldMeta
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression extends TypeInfer, TypeInfer.TypeUpdateSpec, SortItem, RightOperand, AssignmentItem,
        SelectionSpec {


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

    /**
     * @param and {@link SQLs#AND}
     */
    IPredicate between(Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    /**
     * @param and {@link SQLs#AND}
     */
    IPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate notBetween(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    /**
     * @param and {@link SQLs#AND}
     */
    IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate between(@Nullable SQLs.BetweenModifier modifier, BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    /**
     * @param and {@link SQLs#AND}
     */
    IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    /**
     * @param operand <ul>
     *                <li>{@link SQLs#TRUE}</li>
     *                <li>{@link SQLs#FALSE}</li>
     *                <li>{@link SQLs#UNKNOWN}</li>
     *                <li>{@link SQLs#NULL}</li>
     *                <li>other</li>
     *                </ul>
     */
    IPredicate is(SQLs.BooleanTestWord operand);

    /**
     * @param operand <ul>
     *                <li>{@link SQLs#TRUE}</li>
     *                <li>{@link SQLs#FALSE}</li>
     *                <li>{@link SQLs#UNKNOWN}</li>
     *                <li>{@link SQLs#NULL}</li>
     *                <li>other</li>
     *                </ul>
     */
    IPredicate isNot(SQLs.BooleanTestWord operand);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate is(SQLs.IsComparisonWord operator, Expression operand);

    IPredicate isNot(SQLs.IsComparisonWord operator, Expression operand);

    <T> IPredicate is(SQLs.IsComparisonWord operator, BiFunction<Expression, T, Expression> valueOperator, @Nullable T value);

    <T> IPredicate isNot(SQLs.IsComparisonWord operator, BiFunction<Expression, T, Expression> valueOperator, @Nullable T value);

    IPredicate in(Expression operand);

    IPredicate in(SubQuery operand);

    <T extends Collection<?>> IPredicate in(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate in(TeNamedOperator<Expression> namedOperator, String paramName, int size);


    IPredicate notIn(Expression operand);

    IPredicate notIn(SubQuery subQuery);

    <T extends Collection<?>> IPredicate notIn(BiFunction<Expression, T, Expression> operator, T operand);

    IPredicate notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size);

    IPredicate like(Expression pattern);

    <T> IPredicate like(BiFunction<MappingType, T, Expression> operator, T operand);

    IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar);

    IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar);

    <T> IPredicate like(BiFunction<MappingType, T, Expression> operator, T operand, SQLs.WordEscape escape, char escapeChar);

    IPredicate notLike(Expression pattern);

    <T> IPredicate notLike(BiFunction<MappingType, T, Expression> operator, T operand);

    IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar);

    IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar);

    <T> IPredicate notLike(BiFunction<MappingType, T, Expression> operator, T operand, SQLs.WordEscape escape, char escapeChar);

    Expression mod(Expression operand);

    <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand);

    Expression times(Expression operand);

    <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand);

    Expression plus(Expression operand);

    <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand);

    Expression minus(Expression minuend);


    <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand);

    Expression divide(Expression divisor);

    <T> Expression divide(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseAnd(Expression operand);

    <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     * @see #bitwiseAnd(Expression)
     * @see SQLs#bitwiseNot(Expression)
     */
    Expression bitwiseOr(Expression operand);

    <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseXor(Expression operand);

    <T> Expression bitwiseXor(BiFunction<Expression, T, Expression> operator, T operand);


    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression rightShift(Expression bitNumber);

    <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression leftShift(Expression bitNumber);

    <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand);


    @Override
    Expression mapTo(TypeMeta typeMeta);

    @Deprecated
    Expression bracket();


    /**
     * @return this
     */
    @Override
    SortItem asSortItem();

    SortItem asc();

    SortItem desc();

    SortItem ascSpace(@Nullable Statement.NullsFirstLast firstLast);


    SortItem descSpace(@Nullable Statement.NullsFirstLast firstLast);

    /*-------------------below dialect operator method -------------------*/


    Expression apply(BiFunction<Expression, Expression, Expression> operator, Expression operand);

    <T> Expression apply(BiFunction<Expression, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator, T value);


    <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, Expression optionalExp);

    <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, char escapeChar);

    <M extends SQLWords, T> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator, T value, M modifier, Expression optionalExp);

    <M extends SQLWords, T> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator, T value, M modifier, char escapeChar);

    IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, Expression operand);

    <T> IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, BiFunction<Expression, T, Expression> valueOperator, T value);


    <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, Expression optionalExp);

    <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, char escapeChar);

    <M extends SQLWords, T> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, BiFunction<MappingType, T, Expression> valueOperator, T value, M modifier, Expression optionalExp);

    <M extends SQLWords, T> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, BiFunction<MappingType, T, Expression> valueOperator, T value, M modifier, char escapeChar);


}
