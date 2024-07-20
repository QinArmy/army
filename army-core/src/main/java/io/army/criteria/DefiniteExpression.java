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
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.optional.NoCastTextType;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static io.army.dialect.Database.H2;
import static io.army.dialect.Database.PostgreSQL;

/**
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link TableField}</li>
 *     <li>{@link ValueExpression}</li>
 * </ul>
 */
public interface DefiniteExpression extends SimpleExpression {


    /**
     * <p><strong>=</strong> operator. This method is similar to {@link #equal(Expression)},except that the operand
     * of {@link #equal(Expression)} is returned by funcRef.
     *
     * <p><strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate equal(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>!=</strong> . This method is similar to {@link #notEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate notEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    /**
     * <p>
     * <strong>=</strong> operator. This method is similar to {@link #equal(Expression)},except that the operand
     * of {@link #equal(Expression)} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html#operator_equal-to">NULL-safe equal.</a>
     */
    <T> CompoundPredicate nullSafeEqual(BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);


    /**
     * <p>
     * Operator <strong>&lt;</strong> . This method is similar to {@link #less(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate less(BiFunction<SimpleExpression, T, Expression> funcRef, T value);


    /**
     * <p>
     * Operator <strong>&lt;=</strong> . This method is similar to {@link #lessEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate lessEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>></strong> . This method is similar to {@link #greater(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate greater(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>>=</strong> . This method is similar to {@link #greaterEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate greaterEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);


    /**
     * <p>
     * Operator <strong>BETWEEN AND</strong> . This method is similar to {@link #between(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param first   non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and     {@link SQLs#AND}
     * @param second  non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate between(BiFunction<SimpleExpression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);


    /**
     * <p>
     * Operator <strong>BETWEEN AND</strong> . This method is similar to {@link #between(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by firstFuncRef and secondFuncRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of firstFuncRef and secondFuncRef always is <strong>this</strong>.
     *
     * @param firstFuncRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of firstFuncRef always is <strong>this</strong>.
     * @param first         non-null,it will be passed to firstFuncRef as the second argument of firstFuncRef
     * @param and           {@link SQLs#AND}
     * @param secondFuncRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of secondFuncRef always is <strong>this</strong>.
     * @param second        non-null,it will be passed to secondFuncRef as the second argument of secondFuncRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T, U> CompoundPredicate between(BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef, U second);


    /**
     * <p>
     * Operator <strong>NOT BETWEEN AND</strong> . This method is similar to {@link #notBetween(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param first   non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and     {@link SQLs#AND}
     * @param second  non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T> CompoundPredicate notBetween(BiFunction<SimpleExpression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);

    /**
     * <p>
     * Operator <strong>BETWEEN AND</strong> . This method is similar to {@link #between(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by firstFuncRef and secondFuncRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of firstFuncRef and secondFuncRef always is <strong>this</strong>.
     *
     * @param firstFuncRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of firstFuncRef always is <strong>this</strong>.
     * @param first         non-null,it will be passed to firstFuncRef as the second argument of firstFuncRef
     * @param and           {@link SQLs#AND}
     * @param secondFuncRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of secondFuncRef always is <strong>this</strong>.
     * @param second        non-null,it will be passed to secondFuncRef as the second argument of secondFuncRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    <T, U> CompoundPredicate notBetween(BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef, U second);


    /**
     * <p>
     * Operator <strong>BETWEEN [SYMMETRIC/ASYMMETRIC] AND</strong> . This method is similar to {@link #between(SQLs.BetweenModifier, Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param modifier {@link SQLs#SYMMETRIC} or {@link SQLs#ASYMMETRIC}
     * @param funcRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                 <ul>
     *                     <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>developer custom method</li>
     *                 </ul>.
     *                 The first argument of funcRef always is <strong>this</strong>.
     * @param first    non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and      {@link SQLs#AND}
     * @param second   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T> CompoundPredicate between(@Nullable SQLs.BetweenModifier modifier, BiFunction<SimpleExpression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);

    /**
     * <p>
     * Operator <strong>BETWEEN AND</strong> . This method is similar to {@link #between(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by firstFuncRef and secondFuncRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of firstFuncRef and secondFuncRef always is <strong>this</strong>.
     *
     * @param firstFuncRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of firstFuncRef always is <strong>this</strong>.
     * @param first         non-null,it will be passed to firstFuncRef as the second argument of firstFuncRef
     * @param and           {@link SQLs#AND}
     * @param secondFuncRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of secondFuncRef always is <strong>this</strong>.
     * @param second        non-null,it will be passed to secondFuncRef as the second argument of secondFuncRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T, U> CompoundPredicate between(@Nullable SQLs.BetweenModifier modifier, BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef, U second);


    /**
     * <p>
     * Operator <strong>NOT BETWEEN [SYMMETRIC/ASYMMETRIC] AND</strong> . This method is similar to {@link #notBetween(SQLs.BetweenModifier, Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param modifier {@link SQLs#SYMMETRIC} or {@link SQLs#ASYMMETRIC}
     * @param funcRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                 <ul>
     *                     <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>developer custom method</li>
     *                 </ul>.
     *                 The first argument of funcRef always is <strong>this</strong>.
     * @param first    non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and      {@link SQLs#AND}
     * @param second   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T> CompoundPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, BiFunction<SimpleExpression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);

    /**
     * <p>
     * Operator <strong>BETWEEN AND</strong> . This method is similar to {@link #between(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by firstFuncRef and secondFuncRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of firstFuncRef and secondFuncRef always is <strong>this</strong>.
     *
     * @param firstFuncRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of firstFuncRef always is <strong>this</strong>.
     * @param first         non-null,it will be passed to firstFuncRef as the second argument of firstFuncRef
     * @param and           {@link SQLs#AND}
     * @param secondFuncRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                      <ul>
     *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                          <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                          <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete ) syntax</li>
     *                          <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                          and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                          <li>developer custom method</li>
     *                      </ul>.
     *                      The first argument of secondFuncRef always is <strong>this</strong>.
     * @param second        non-null,it will be passed to secondFuncRef as the second argument of secondFuncRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T, U> CompoundPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef, U second);


    /**
     * <p>
     * Operator <strong>IS </strong> . This method is similar to {@link #notEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param operator see <ul>
     *                 <li>{@link SQLs#DISTINCT_FROM}</li>
     *                 </ul>
     * @param funcRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                 <ul>
     *                     <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>developer custom method</li>
     *                 </ul>.
     *                 The first argument of funcRef always is <strong>this</strong>.
     * @param value    non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T> CompoundPredicate is(SQLs.IsComparisonWord operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);

    /**
     * <p>
     * Operator <strong>IS </strong> . This method is similar to {@link #notEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param operator see <ul>
     *                 <li>{@link SQLs#DISTINCT_FROM}</li>
     *                 </ul>
     * @param funcRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                 <ul>
     *                     <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>developer custom method</li>
     *                 </ul>.
     *                 The first argument of funcRef always is <strong>this</strong>.
     * @param value    non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#rowParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T> CompoundPredicate isNot(SQLs.IsComparisonWord operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);

    CompoundPredicate in(BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value);

    CompoundPredicate notIn(BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value);

    CompoundPredicate in(TeNamedOperator<SimpleExpression> funcRef, String paramName, int size);

    CompoundPredicate notIn(TeNamedOperator<SimpleExpression> funcRef, String paramName, int size);


    <T> CompoundPredicate like(BiFunction<NoCastTextType, T, Expression> funcRef, T value);

    <T> CompoundPredicate like(BiFunction<NoCastTextType, T, Expression> funcRef, T value, SQLs.WordEscape escape, char escapeChar);

    <T> CompoundPredicate notLike(BiFunction<NoCastTextType, T, Expression> funcRef, T value);

    <T> CompoundPredicate notLike(BiFunction<NoCastTextType, T, Expression> funcRef, T value, SQLs.WordEscape escape, char escapeChar);

    <T> CompoundExpression mod(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression times(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression plus(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression minus(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression divide(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression bitwiseAnd(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression bitwiseOr(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression bitwiseXor(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression rightShift(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> CompoundExpression leftShift(BiFunction<SimpleExpression, T, Expression> funcRef, T value);


    /*-------------------below dialect operator method-------------------*/


    /**
     * <p>
     * This method is designed for dialect operator.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param value   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */

    <T, R extends ResultExpression> R space(BiFunction<SimpleExpression, Expression, R> operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);

    /**
     * <p>
     * This method is designed for dialect operator.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param value   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <M extends SQLWords, T, R extends ResultExpression> R space(OptionalClauseOperator<M, Expression, R> operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value, M modifier, Expression optionalExp);

    /**
     * <p>
     * This method is designed for dialect operator.
     *
     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param value   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <M extends SQLWords, T, R extends ResultExpression> R space(OptionalClauseOperator<M, Expression, R> operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value, M modifier, char escapeChar);

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
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param value   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <T> CompoundPredicate whiteSpace(BiFunction<SimpleExpression, Expression, CompoundPredicate> operator, BiFunction<SimpleExpression, T, Expression> funcRef, @Nullable T value);

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
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param value   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <M extends SQLWords, T> CompoundPredicate whiteSpace(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, BiFunction<MappingType, T, Expression> funcRef, @Nullable T value, M modifier, Expression optionalExp);

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
     * @param funcRef the reference of the method of dialect operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code Postgres.pound(Expression,Expression)}
     * @param value   the right operand of dialect operator.  It will be passed to funcRef as the second argument of funcRef
     */
    <M extends SQLWords, T> CompoundPredicate whiteSpace(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, BiFunction<MappingType, T, Expression> funcRef, @Nullable T value, M modifier, char escapeChar);


}
