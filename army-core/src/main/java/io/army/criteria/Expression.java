package io.army.criteria;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.function.OptionalClauseOperator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.BiFunction;

import static io.army.dialect.Database.H2;
import static io.army.dialect.Database.PostgreSQL;

/**
 * Interface representing the sql expression, eg: column,function.
 *
 * @see FieldMeta
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression extends ExpressionElement, TypeInfer, TypeInfer.TypeUpdateSpec, SortItem, RightOperand,
        AssignmentItem, SelectionSpec, ArraySubscript {


    /**
     * <p>
     * <strong>=</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    IPredicate equal(Expression operand);


    /**
     * <p>
     * <strong>= ANY</strong> operator
     * </p>
     */
    IPredicate equalAny(SubQuery subQuery);

    /**
     * <p>
     * <strong>= SOME</strong> operator
     * </p>
     */
    IPredicate equalSome(SubQuery subQuery);

    /**
     * <p>
     * <strong>&lt;</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    IPredicate less(Expression operand);

    IPredicate lessAny(SubQuery subQuery);

    IPredicate lessSome(SubQuery subQuery);

    IPredicate lessAll(SubQuery subQuery);


    IPredicate lessEqual(Expression operand);


    IPredicate lessEqualAny(SubQuery subQuery);


    IPredicate lessEqualSome(SubQuery subQuery);

    IPredicate lessEqualAll(SubQuery subQuery);

    IPredicate greater(Expression operand);


    IPredicate greaterAny(SubQuery subQuery);

    IPredicate greaterSome(SubQuery subQuery);

    IPredicate greaterAll(SubQuery subQuery);

    IPredicate greaterEqual(Expression operand);

    IPredicate greaterEqualAny(SubQuery subQuery);

    IPredicate greaterEqualSome(SubQuery subQuery);

    IPredicate greaterEqualAll(SubQuery subQuery);

    IPredicate notEqual(Expression operand);

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
    IPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    @Support({PostgreSQL, H2})
    IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    @Support({PostgreSQL, H2})
    IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second);

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

    IPredicate in(Expression operand);

    IPredicate in(SubQuery operand);

    IPredicate notIn(Expression operand);

    IPredicate notIn(SubQuery subQuery);


    IPredicate like(Expression pattern);

    IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar);

    IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar);

    IPredicate notLike(Expression pattern);

    IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar);

    IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar);

    Expression mod(Expression operand);

    Expression times(Expression operand);

    Expression plus(Expression operand);

    Expression minus(Expression minuend);

    Expression divide(Expression divisor);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseAnd(Expression operand);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     * @see #bitwiseAnd(Expression)
     * @see SQLs#bitwiseNot(Expression)
     */
    Expression bitwiseOr(Expression operand);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseXor(Expression operand);


    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression rightShift(Expression bitNumber);


    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression leftShift(Expression bitNumber);


    @Override
    Expression mapTo(TypeMeta typeMeta);


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


    <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, Expression optionalExp);

    <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, char escapeChar);

    IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, Expression operand);


    <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, Expression optionalExp);

    <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, char escapeChar);


}
