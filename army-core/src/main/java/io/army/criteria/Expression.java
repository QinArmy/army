package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.OptionalClauseOperator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.BiFunction;

import static io.army.dialect.Database.*;

/**
 * Interface representing the sql expression, eg: column,function.
 * <p> This interface is the base interface of below"
 *     <ul>
 *         <li>{@link SimpleExpression}</li>
 *         <li>{@link CompoundExpression}</li>
 *         <li>{@link IPredicate}</li>
 *     </ul>
 * </p>
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
    CompoundPredicate equal(Expression operand);

    /**
     * MySQL
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_equal-to">NULL-safe equal.</a>
     */
    @Support({MySQL})
    CompoundPredicate nullSafeEqual(Expression operand);


    /**
     * <p>
     * <strong>= ANY</strong> operator
     * </p>
     */
    CompoundPredicate equalAny(SubQuery subQuery);

    /**
     * <p>
     * <strong>= SOME</strong> operator
     * </p>
     */
    CompoundPredicate equalSome(SubQuery subQuery);

    /**
     * <p>
     * <strong>&lt;</strong> operator
     * </p>
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    CompoundPredicate less(Expression operand);

    CompoundPredicate lessAny(SubQuery subQuery);

    CompoundPredicate lessSome(SubQuery subQuery);

    CompoundPredicate lessAll(SubQuery subQuery);


    CompoundPredicate lessEqual(Expression operand);


    CompoundPredicate lessEqualAny(SubQuery subQuery);


    CompoundPredicate lessEqualSome(SubQuery subQuery);

    CompoundPredicate lessEqualAll(SubQuery subQuery);

    CompoundPredicate greater(Expression operand);


    CompoundPredicate greaterAny(SubQuery subQuery);

    CompoundPredicate greaterSome(SubQuery subQuery);

    CompoundPredicate greaterAll(SubQuery subQuery);

    CompoundPredicate greaterEqual(Expression operand);

    CompoundPredicate greaterEqualAny(SubQuery subQuery);

    CompoundPredicate greaterEqualSome(SubQuery subQuery);

    CompoundPredicate greaterEqualAll(SubQuery subQuery);

    CompoundPredicate notEqual(Expression operand);

    CompoundPredicate notEqualAny(SubQuery subQuery);

    CompoundPredicate notEqualSome(SubQuery subQuery);

    CompoundPredicate notEqualAll(SubQuery subQuery);

    /**
     * @param and {@link SQLs#AND}
     */
    CompoundPredicate between(Expression first, SQLs.WordAnd and, Expression second);


    /**
     * @param and {@link SQLs#AND}
     */
    CompoundPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    @Support({PostgreSQL, H2})
    CompoundPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param and {@link SQLs#AND}
     */
    @Support({PostgreSQL, H2})
    CompoundPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second);

    /**
     * @param operand <ul>
     *                <li>{@link SQLs#TRUE}</li>
     *                <li>{@link SQLs#FALSE}</li>
     *                <li>{@link SQLs#UNKNOWN}</li>
     *                <li>{@link SQLs#NULL}</li>
     *                <li>other</li>
     *                </ul>
     */
    CompoundPredicate is(SQLs.BooleanTestWord operand);

    /**
     * @param operand <ul>
     *                <li>{@link SQLs#TRUE}</li>
     *                <li>{@link SQLs#FALSE}</li>
     *                <li>{@link SQLs#UNKNOWN}</li>
     *                <li>{@link SQLs#NULL}</li>
     *                <li>other</li>
     *                </ul>
     */
    CompoundPredicate isNot(SQLs.BooleanTestWord operand);

    CompoundPredicate isNull();

    CompoundPredicate isNotNull();

    CompoundPredicate is(SQLs.IsComparisonWord operator, Expression operand);

    CompoundPredicate isNot(SQLs.IsComparisonWord operator, Expression operand);

    CompoundPredicate in(RowElement row);

    CompoundPredicate notIn(RowElement row);

    CompoundPredicate like(Expression pattern);

    CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar);

    CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar);

    CompoundPredicate notLike(Expression pattern);

    CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar);

    CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar);

    CompoundExpression mod(Expression operand);

    CompoundExpression times(Expression operand);

    CompoundExpression plus(Expression operand);

    CompoundExpression minus(Expression minuend);

    CompoundExpression divide(Expression divisor);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    CompoundExpression bitwiseAnd(Expression operand);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     * @see #bitwiseAnd(Expression)
     * @see SQLs#bitwiseNot(Expression)
     */
    CompoundExpression bitwiseOr(Expression operand);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    CompoundExpression bitwiseXor(Expression operand);


    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    CompoundExpression rightShift(Expression bitNumber);


    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    CompoundExpression leftShift(Expression bitNumber);


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


    CompoundExpression apply(BiFunction<Expression, Expression, CompoundExpression> operator, Expression operand);


    <M extends SQLWords> CompoundExpression apply(OptionalClauseOperator<M, Expression, CompoundExpression> operator, Expression right, M modifier, Expression optionalExp);

    <M extends SQLWords> CompoundExpression apply(OptionalClauseOperator<M, Expression, CompoundExpression> operator, Expression right, M modifier, char escapeChar);

    CompoundPredicate test(BiFunction<Expression, Expression, CompoundPredicate> operator, Expression operand);


    <M extends SQLWords> CompoundPredicate test(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier, Expression optionalExp);

    <M extends SQLWords> CompoundPredicate test(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier, char escapeChar);


}
