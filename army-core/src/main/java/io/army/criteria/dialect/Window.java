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

package io.army.criteria.dialect;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SimpleExpression;
import io.army.criteria.Statement;
import io.army.criteria.impl.SQLs;
import io.army.mapping.IntegerType;
import io.army.mapping.LongType;

import io.army.lang.Nullable;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * <p>
 * This interface representing window that is defined by application developer in query statement.
 * * @since 0.6.0
 */
public interface Window extends Item {


    interface _WindowAsClause<T extends Item, R extends Item> extends Item {

        R as();

        R as(@Nullable String existingWindowName);

        R as(Consumer<T> consumer);

        R as(@Nullable String existingWindowName, Consumer<T> consumer);

    }


    @FunctionalInterface
    interface Builder<T extends Item> extends Item {

        _WindowAsClause<T, Item> window(String windowName);

    }


    /**
     * <p>
     * This interface representing static comma clause in WINDOW clause.
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @param <CR> next clause java type
     * @since 0.6.0
     */
    interface _StaticWindowCommaClause<CR> {

        CR comma(String windowName);
    }


    /**
     * <p>
     * This interface representing dynamic WINDOW clause.
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @param <T> sub interface of {@link Builder}
     * @param <R> next clause java type
     * @since 0.6.0
     */
    interface _DynamicWindowClause<T extends Item, R extends Item> {


        R windows(Consumer<Builder<T>> consumer);

        R ifWindows(Consumer<Builder<T>> consumer);

    }


    interface _WindowSpec {

    }

    interface _PartitionByCommaClause<R> {

        R comma(Expression exp);

        R comma(Expression exp1, Expression exp2);

        R comma(Expression exp1, Expression exp2, Expression exp3);

        R comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    }

    /**
     * <p>
     * This interface representing PARTITION BY clause in WINDOW clause.
     * * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * *
     *
     * @param <R> next clause java type
     * @since 0.6.0
     */
    interface _PartitionByExpClause<R> extends _WindowSpec {

        R partitionBy(Expression exp);

        R partitionBy(Expression exp1, Expression exp2);

        R partitionBy(Expression exp1, Expression exp2, Expression exp3);

        R partitionBy(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

        R partitionBy(Consumer<Consumer<Expression>> consumer);

        R ifPartitionBy(Consumer<Consumer<Expression>> consumer);

    }


    /**
     * <p>see :
     * <ul>
     *     <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
     *     <li>{@link SQLs#CURRENT_ROW}</li>
     *      <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
     * </ul>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface RowModifier {

    }

    /**
     * <p>see
     * <ul>
     *     <li>{@link SQLs#PRECEDING}</li>
     *     <li>{@link SQLs#FOLLOWING}</li>
     * </ul>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface ExpModifier {

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRowsClause<RS, RB> {

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                     <li>{@link SQLs#CURRENT_ROW}</li>
         *                      <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                 </ul>
         */
        RS rows(RowModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        RS rows(Expression exp, ExpModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        <T> RS rows(BiFunction<LongType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB rows();


    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRangeClause<RS, RB> {

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                     <li>{@link SQLs#CURRENT_ROW}</li>
         *                      <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                 </ul>
         */
        RS range(RowModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        RS range(Expression exp, ExpModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        <T> RS range(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB range();


    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRowsRangeSpec<RS, RB> extends _StaticFrameUnitRowsClause<RS, RB>,
            _StaticFrameUnitRangeClause<RS, RB> {

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitGroupsClause<RS, RB> {

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                     <li>{@link SQLs#CURRENT_ROW}</li>
         *                      <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                 </ul>
         */
        RS groups(RowModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        RS groups(Expression exp, ExpModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        <T> RS groups(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB groups();


    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRowsRangeGroupsSpec<RS, RB> extends _StaticFrameUnitRowsRangeSpec<RS, RB>,
            _StaticFrameUnitGroupsClause<RS, RB> {

    }

    interface _FrameUnitSpaceClause<RS, RB> {

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                     <li>{@link SQLs#CURRENT_ROW}</li>
         *                      <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                 </ul>
         */
        RS space(RowModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        RS space(Expression exp, ExpModifier modifier);

        /**
         * @param modifier see :
         *                 <ul>
         *                     <li>{@link SQLs#PRECEDING}</li>
         *                     <li>{@link SQLs#FOLLOWING}</li>
         *                 </ul>
         */
        <T> RS space(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB space();
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRowsClause<T, R> {


        R ifRows(Consumer<T> consumer);

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRangeClause<T, R> {


        R ifRange(Consumer<T> consumer);

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRowsRangeClause<T, R> extends _DynamicFrameUnitRowsClause<T, R>,
            _DynamicFrameUnitRangeClause<T, R> {


    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitGroupsClause<T, R> {


        R ifGroups(Consumer<T> consumer);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRowsRangeGroupsClause<T, R> extends _DynamicFrameUnitRowsRangeClause<T, R>,
            _DynamicFrameUnitGroupsClause<T, R> {


    }


    interface _FrameBetweenAndClause<R> {

        /**
         * @param endModifier see :
         *                    <ul>
         *                        <li>{@link SQLs#PRECEDING}</li>
         *                        <li>{@link SQLs#FOLLOWING}</li>
         *                    </ul>
         */
        R and(Expression endExp, ExpModifier endModifier);

        /**
         * @param endModifier see :
         *                    <ul>
         *                        <li>{@link SQLs#PRECEDING}</li>
         *                        <li>{@link SQLs#FOLLOWING}</li>
         *                    </ul>
         */
        <T> R and(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier);

        /**
         * @param frameEnd see :
         *                 <ul>
         *                     <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                     <li>{@link SQLs#CURRENT_ROW}</li>
         *                      <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                 </ul>
         */
        R and(RowModifier frameEnd);

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _FrameBetweenClause<R> {

        /**
         * @param frameStart see :
         *                   <ul>
         *                       <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                       <li>{@link SQLs#CURRENT_ROW}</li>
         *                        <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                   </ul>
         * @param frameEnd   see :
         *                   <ul>
         *                       <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                       <li>{@link SQLs#CURRENT_ROW}</li>
         *                        <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                   </ul>
         */
        R between(RowModifier frameStart, SQLs.WordAnd and, RowModifier frameEnd);

        /**
         * @param startModifier see :
         *                      <ul>
         *                          <li>{@link SQLs#PRECEDING}</li>
         *                          <li>{@link SQLs#FOLLOWING}</li>
         *                      </ul>
         * @param and           see {@link SQLs#AND}
         * @param endModifier   see :
         *                      <ul>
         *                          <li>{@link SQLs#PRECEDING}</li>
         *                          <li>{@link SQLs#FOLLOWING}</li>
         *                      </ul>
         */
        R between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and, Expression endExp, ExpModifier endModifier);

        /**
         * @param frameStart  see :
         *                    <ul>
         *                        <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                        <li>{@link SQLs#CURRENT_ROW}</li>
         *                         <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                    </ul>
         * @param and         see {@link SQLs#AND}
         * @param endModifier see :
         *                    <ul>
         *                        <li>{@link SQLs#PRECEDING}</li>
         *                        <li>{@link SQLs#FOLLOWING}</li>
         *                    </ul>
         */
        R between(RowModifier frameStart, SQLs.WordAnd and, Expression endExp, ExpModifier endModifier);

        /**
         * @param startModifier see :
         *                      <ul>
         *                          <li>{@link SQLs#PRECEDING}</li>
         *                          <li>{@link SQLs#FOLLOWING}</li>
         *                      </ul>
         * @param and           see {@link SQLs#AND}
         * @param frameEnd      see :
         *                      <ul>
         *                          <li>{@link SQLs#UNBOUNDED_PRECEDING}</li>
         *                          <li>{@link SQLs#CURRENT_ROW}</li>
         *                           <li>{@link SQLs#UNBOUNDED_FOLLOWING}</li>
         *                      </ul>
         */
        R between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and, RowModifier frameEnd);

        /**
         * @param startModifier see :
         *                      <ul>
         *                          <li>{@link SQLs#PRECEDING}</li>
         *                          <li>{@link SQLs#FOLLOWING}</li>
         *                      </ul>
         */
        _FrameBetweenAndClause<R> between(Expression startExp, ExpModifier startModifier);

        <T> R between(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier startModifier, SQLs.WordAnd and, RowModifier frameEnd);

        <T> R between(RowModifier frameStart, SQLs.WordAnd and, BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier);

        <T> R between(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier startModifier, SQLs.WordAnd and, Expression endExp, ExpModifier endModifier);

        <T> R between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and, BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier);

        <T, U> R between(BiFunction<IntegerType, T, Expression> funcRefForStart, T startValue, ExpModifier startModifier, SQLs.WordAnd and, BiFunction<IntegerType, U, Expression> funcRefForEnd, U endValue, ExpModifier endModifier);

    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _FrameExclusionClause<R> {

        R excludeCurrentRow();

        R excludeGroup();

        R excludeTies();

        R excludeNoOthers();

        R ifExcludeCurrentRow(BooleanSupplier predicate);

        R ifExcludeGroup(BooleanSupplier predicate);

        R ifExcludeTies(BooleanSupplier predicate);

        R ifExcludeNoOthers(BooleanSupplier predicate);
    }


    /*-------------------below standard window -------------------*/

    interface _StandardFrameBetweenClause extends _FrameBetweenClause<Item> {

    }

    interface _StandardFrameUnitSpaceSpec extends _FrameUnitSpaceClause<Item, _StandardFrameBetweenClause> {

    }

    interface _StandardFrameExtentSpec extends _StaticFrameUnitRowsRangeSpec<Item, _StandardFrameBetweenClause>,
            _DynamicFrameUnitRowsRangeClause<_StandardFrameUnitSpaceSpec, Item> {

    }

    interface _StandardOrderByCommaSpec extends Statement._OrderByCommaClause<_StandardOrderByCommaSpec>,
            _StandardFrameExtentSpec {

    }

    interface _StandardOrderBySpec extends Statement._StaticOrderByClause<_StandardOrderByCommaSpec>,
            Statement._DynamicOrderByClause<_StandardFrameExtentSpec>,
            _StandardFrameExtentSpec {

    }

    interface _StandardPartitionByCommaSpec extends _PartitionByCommaClause<_StandardPartitionByCommaSpec>,
            _StandardOrderBySpec {

    }

    interface _StandardPartitionBySpec extends _PartitionByExpClause<_StandardPartitionByCommaSpec>,
            _StandardOrderBySpec {

    }


    /*-------------------below over clause -------------------*/

    interface _OverWindowClause<T extends _WindowSpec> extends Item {

        SimpleExpression over();

        SimpleExpression over(@Nullable String existingWindowName);

        SimpleExpression over(Consumer<T> consumer);

        SimpleExpression over(@Nullable String existingWindowName, Consumer<T> consumer);

    }


}
