/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeFunction;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.army.dialect.Database.*;

/**
 * Interface representing the sql expression, eg: column,function.
 * <p> This interface is the base interface of below"
 * <ul>
 *     <li>{@link SimpleExpression}</li>
 *     <li>{@link CompoundExpression}</li>
 *     <li>{@link IPredicate}</li>
 * </ul>
 *
 * @see FieldMeta
 * @since 0.6.0
 */
@SuppressWarnings("unused")
public interface Expression extends SQLExpression, TypeInfer, TypeInfer.TypeUpdateSpec, SortItem,
        GroupByItem.ExpressionItem, RightOperand, AssignmentItem, SelectionSpec, ArraySubscript {


    /**
     * <p>
     * <strong>=</strong> operator
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see DefiniteExpression#equal(BiFunction, Object)
     * @see SqlField#equal(BiFunction)
     */
    CompoundPredicate equal(Expression operand);

    CompoundPredicate notEqual(Expression operand);

    @Support({MySQL, PostgreSQL, H2})
    CompoundPredicate nullSafeEqual(Expression operand);

    /**
     * <p>
     * <strong>&lt;</strong> operator
     *
     *
     * @param operand non-null
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    CompoundPredicate less(Expression operand);


    CompoundPredicate lessEqual(Expression operand);


    CompoundPredicate greater(Expression operand);

    CompoundPredicate greaterEqual(Expression operand);


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

    /**
     * @param operator see <ul>
     *                 <li>{@link SQLs#DISTINCT_FROM}</li>
     *                 </ul>
     */
    @Support({PostgreSQL, H2})
    CompoundPredicate is(SQLs.IsComparisonWord operator, Expression operand);

    /**
     * @param operator see <ul>
     *                 <li>{@link SQLs#DISTINCT_FROM}</li>
     *                 </ul>
     */
    @Support({PostgreSQL, H2})
    CompoundPredicate isNot(SQLs.IsComparisonWord operator, Expression operand);

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
     * @return always this
     */
    @Override
    SortItem asSortItem();

    SortItem asc();

    SortItem desc();

    SortItem ascSpace(@Nullable SQLs.NullsFirstLast firstLast);


    SortItem descSpace(@Nullable SQLs.NullsFirstLast firstLast);

    /*-------------------below dialect operator method -------------------*/

    /**
     * <p>
     * This method is designed for dialect key word syntax. For example : postgre using key word
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.using(Expression)}
     */
    <R extends UnaryResult> R space(Function<Expression, R> funcRef);


    /**
     * <p>This method is designed for dialect operator.
     *
     * <p><strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <T, R extends ResultExpression> R space(BiFunction<Expression, T, R> funcRef, T right);


    /**
     * <p>
     * This method is designed for dialect operator.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */

    <M extends SQLWords, R extends ResultExpression> R space(OptionalClauseOperator<M, Expression, R> funcRef, Expression right, M modifier, Expression optionalExp);

    /**
     * <p>
     * This method is designed for dialect operator.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <M extends SQLWords, R extends ResultExpression> R space(OptionalClauseOperator<M, Expression, R> funcRef, Expression right, M modifier, char escapeChar);


    /**
     * <p>
     * This method is designed for dialect operator that produce boolean type expression.
     * This method name is 'whiteSpace' not 'space' ,because of {@link Statement._WhereAndClause#and(UnaryOperator, IPredicate)} type infer.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * <p>
     *
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <T> CompoundPredicate whiteSpace(BiFunction<Expression, T, CompoundPredicate> funcRef, T right);


    /**
     * <p>
     * This method is designed for dialect operator that produce boolean type expression.
     * This method name is 'whiteSpace' not 'space' ,because of {@link Statement._WhereAndClause#and(UnaryOperator, IPredicate)} type infer.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * <p>
     *
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <M extends SQLWords, T extends RightOperand> CompoundPredicate whiteSpace(TeFunction<Expression, M, T, CompoundPredicate> funcRef, final M modifier, T right);

    /**
     * <p>
     * This method is designed for dialect operator that produce boolean type expression.
     * This method name is 'whiteSpace' not 'space' ,because of {@link Statement._WhereAndClause#and(UnaryOperator, IPredicate)} type infer.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * <p>
     *
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */

    <M extends SQLWords> CompoundPredicate whiteSpace(OptionalClauseOperator<M, Expression, CompoundPredicate> funcRef, Expression right, M modifier, Expression optionalExp);

    /**
     * <p>
     * This method is designed for dialect operator that produce boolean type expression.
     * This method name is 'whiteSpace' not 'space' ,because of {@link Statement._WhereAndClause#and(UnaryOperator, IPredicate)} type infer.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * <p>
     *
     *
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param right   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */

    <M extends SQLWords> CompoundPredicate whiteSpace(OptionalClauseOperator<M, Expression, CompoundPredicate> funcRef, Expression right, M modifier, char escapeChar);


}
