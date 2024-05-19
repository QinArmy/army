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


import io.army.criteria.standard.SQLs;
import io.army.function.ExpressionOperator;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing primary update statement.This interface is base interface of below:
 * <ul>
 *     <li>{@link Update}</li>
 *     <li>{@link BatchUpdate}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningUpdate}</li>
 *     <li>{@link io.army.criteria.dialect.BatchReturningUpdate}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface UpdateStatement extends NarrowDmlStatement {

    @Deprecated
    interface _UpdateSpec extends DmlStatement._DmlUpdateSpec<UpdateStatement> {

    }


    interface _ItemPairBuilder {

    }

    interface _DynamicSetClause<B extends _ItemPairBuilder, SR> {
        SR sets(Consumer<B> consumer);

    }

    /**
     * @param <SR> java type of next clause.
     */
    interface _StaticSetClause<F extends SqlField, SR> extends Item {

        SR set(F field, Expression value);

        <R extends AssignmentItem> SR set(F field, Supplier<R> supplier);

        <R extends AssignmentItem> SR set(F field, Function<F, R> function);

        <E, R extends AssignmentItem> SR set(F field, BiFunction<F, E, R> valueOperator, @Nullable E value);

        <K, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> valueOperator, Function<K, V> function, K key);

        <E, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> fieldOperator,
                                                BiFunction<F, E, V> valueOperator, E value);

        <K, V, U, R extends AssignmentItem> SR set(F field, BiFunction<F, U, R> fieldOperator,
                                                   BiFunction<F, V, U> valueOperator, Function<K, V> function, K key);

        <R extends AssignmentItem> SR ifSet(F field, Supplier<R> supplier);

        <R extends AssignmentItem> SR ifSet(F field, Function<F, R> function);

        <E, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, E, R> valueOperator, Supplier<E> supplier);

        <K, V, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, V, R> valueOperator,
                                                  Function<K, V> function, K key);

        <E, V, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, V, R> fieldOperator,
                                                  BiFunction<F, E, V> valueOperator, Supplier<E> getter);

        <K, V, U, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, U, R> fieldOperator,
                                                     BiFunction<F, V, U> valueOperator, Function<K, V> function, K key);

    }


    /**
     * @param <SR> java type of next clause.
     */
    @Deprecated
    interface _SimpleSetClause<F extends SqlField, SR> extends _StaticSetClause<F, SR> {

    }


    /**
     * @param <SR> java type of next clause.
     */
    interface _StaticBatchSetClause<F extends SqlField, SR> extends _StaticSetClause<F, SR> {


        SR setSpace(F field, BiFunction<F, String, Expression> valueOperator);

        <R extends AssignmentItem> SR setSpace(F field, BiFunction<F, Expression, R> fieldOperator, BiFunction<F, String, Expression> valueOperator);

    }


    interface _StaticRowSetClause<F extends SqlField, SR> extends _StaticSetClause<F, SR> {

        SR setRow(F field1, F field2, Supplier<SubQuery> supplier);

        SR setRow(F field1, F field2, F field3, Supplier<SubQuery> supplier);

        SR setRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier);

        SR setRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier);

        SR ifSetRow(F field1, F field2, Supplier<SubQuery> supplier);

        SR ifSetRow(F field1, F field2, F field3, Supplier<SubQuery> supplier);

        SR ifSetRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier);

        SR ifSetRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier);

    }


    interface _StaticBatchRowSetClause<F extends SqlField, SR> extends _StaticRowSetClause<F, SR>,
            _StaticBatchSetClause<F, SR> {

    }


    interface _ItemPairs<F extends SqlField> extends _ItemPairBuilder,
            _StaticSetClause<F, _ItemPairs<F>> {


    }

    interface _BatchItemPairs<F extends SqlField> extends _ItemPairBuilder,
            _StaticBatchSetClause<F, _BatchItemPairs<F>> {


    }

    interface _RowPairs<F extends SqlField> extends _ItemPairBuilder,
            _StaticRowSetClause<F, _RowPairs<F>> {


    }

    interface _BatchRowPairs<F extends SqlField> extends _ItemPairBuilder,
            _StaticRowSetClause<F, _BatchRowPairs<F>>,
            _StaticBatchSetClause<F, _BatchRowPairs<F>> {


    }

    interface _UpdateWhereAndClause<WA> extends Statement._WhereAndClause<WA> {

        /**
         * @param numberOperand see <ul>
         *                      <li>{@link SQLs#LITERAL_0}</li>
         *                      <li>{@link SQLs#LITERAL_DECIMAL_0}</li>
         *                      <li>{@link SQLs#PARAM_0}</li>
         *                      <li>{@link SQLs#PARAM_DECIMAL_0}</li>
         *                      </ul>
         */
        <T> WA and(ExpressionOperator<SimpleExpression, T, Expression> expOperator1,
                   BiFunction<SimpleExpression, T, Expression> operator, T operand1,
                   BiFunction<Expression, Expression, IPredicate> expOperator2, ValueExpression numberOperand);

        /**
         * @param numberOperand see <ul>
         *                      <li>{@link SQLs#LITERAL_0}</li>
         *                      <li>{@link SQLs#LITERAL_DECIMAL_0}</li>
         *                      <li>{@link SQLs#PARAM_0}</li>
         *                      <li>{@link SQLs#PARAM_DECIMAL_0}</li>
         *                      </ul>
         */
        <T> WA ifAnd(ExpressionOperator<SimpleExpression, T, Expression> expOperator1,
                     BiFunction<SimpleExpression, T, Expression> operator, @Nullable T operand1,
                     BiFunction<Expression, Expression, IPredicate> expOperator2, ValueExpression numberOperand);

        /**
         * @param numberOperand see <ul>
         *                      <li>{@link SQLs#LITERAL_0}</li>
         *                      <li>{@link SQLs#LITERAL_DECIMAL_0}</li>
         *                      <li>{@link SQLs#PARAM_0}</li>
         *                      <li>{@link SQLs#PARAM_DECIMAL_0}</li>
         *                      </ul>
         */
        WA and(Function<BiFunction<SqlField, String, Expression>, Expression> fieldOperator,
               BiFunction<SqlField, String, Expression> operator,
               BiFunction<Expression, Expression, IPredicate> expOperator2, ValueExpression numberOperand);

    } // _UpdateWhereAndClause


}
