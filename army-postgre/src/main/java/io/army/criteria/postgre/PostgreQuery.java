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

package io.army.criteria.postgre;

import io.army.criteria.GroupByItem;
import io.army.criteria.Item;
import io.army.criteria.LiteralExpression;
import io.army.criteria.Query;
import io.army.criteria.dialect.Window;
import io.army.criteria.standard.SQLs;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing postgre SELECT statement.
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
 * @since 0.6.0
 */
public interface PostgreQuery extends Query, PostgreStatement {


    interface _UnionSpec<I extends Item> extends _StaticUnionClause<_QueryWithComplexSpec<I>>,
            _StaticIntersectClause<_QueryWithComplexSpec<I>>,
            _StaticExceptClause<_QueryWithComplexSpec<I>>,
            _AsQueryClause<I> {

    }


    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {


    }

    interface _UnionLimitSpec<I extends Item> extends _RowCountLimitAllClause<_UnionOffsetSpec<I>>, _UnionOffsetSpec<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _StaticOrderByClause<_UnionOrderByCommaSpec<I>>,
            _DynamicOrderByClause<_UnionLimitSpec<I>>,
            _UnionLimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _PostgreStaticLockStrengthClause<R> extends _MinLockStrengthClause<R> {

        R forNoKeyUpdate();

        R forKeyShare();

    }


    interface _DynamicLockOfTableSpec extends _LockOfTableAliasClause<_MinLockWaitOptionClause<Item>>,
            _MinLockWaitOptionClause<Item> {

    }

    interface _PostgreDynamicLockStrengthClause extends Item {

        _DynamicLockOfTableSpec update();

        _DynamicLockOfTableSpec share();

        _DynamicLockOfTableSpec noKeyUpdate();

        _DynamicLockOfTableSpec keyShare();

    }


    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_LockSpec<I>>, _LockSpec<I> {//TODO validate multi-lock clause


    }


    interface _LockOfTableSpec<I extends Item> extends _LockOfTableAliasClause<_LockWaitOptionSpec<I>>,
            _LockWaitOptionSpec<I> {

    }


    interface _LockSpec<I extends Item> extends _PostgreStaticLockStrengthClause<_LockOfTableSpec<I>>,
            _DynamicLockClause<_PostgreDynamicLockStrengthClause, _LockSpec<I>>,
            _AsQueryClause<I> {

    }


    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_LockSpec<I>>, _LockSpec<I> {


    }


    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>, _LockSpec<I> {


    }


    interface _LimitSpec<I extends Item> extends _RowCountLimitAllClause<_OffsetSpec<I>>, _OffsetSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }


    interface _OrderBySpec<I extends Item> extends _StaticOrderByClause<_OrderByCommaSpec<I>>,
            _DynamicOrderByClause<_LimitSpec<I>>,
            _LimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _WindowCommaSpec<I extends Item> extends _OrderBySpec<I> {

        Window._WindowAsClause<PostgreWindow._PartitionBySpec, _WindowCommaSpec<I>> comma(String name);

    }

    interface _WindowSpec<I extends Item> extends Window._DynamicWindowClause<PostgreWindow._PartitionBySpec, _OrderBySpec<I>>,
            _OrderBySpec<I> {

        Window._WindowAsClause<PostgreWindow._PartitionBySpec, _WindowCommaSpec<I>> window(String name);

    }

    interface _HavingAndSpec<I extends Item> extends _HavingAndClause<_HavingAndSpec<I>>, _WindowSpec<I> {

    }


    interface _HavingSpec<I extends Item> extends _StaticHavingClause<_HavingAndSpec<I>>,
            _DynamicHavingClause<_WindowSpec<I>>,
            _WindowSpec<I> {

    }

    interface _GroupByCommaSpec<I extends Item> extends _GroupByCommaClause<_GroupByCommaSpec<I>>, _HavingSpec<I> {

    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html#SQL-GROUPBY">GROUP BY Clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-GROUP">The GROUP BY and HAVING Clauses</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-GROUPING-SETS">GROUPING SETS, CUBE, and ROLLUP</a>
     */
    interface _GroupBySpec<I extends Item> extends _StaticGroupByClause<_GroupByCommaSpec<I>>,
            _DynamicGroupByClause<_HavingSpec<I>>,
            _WindowSpec<I> {

        _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item);

        _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item1, GroupByItem item2);

        _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item1, GroupByItem item2, GroupByItem item3);

        _GroupByCommaSpec<I> groupBy(@Nullable SQLs.Modifier modifier, GroupByItem item1, GroupByItem item2, GroupByItem item3, GroupByItem item4);

        _HavingSpec<I> groupBy(@Nullable SQLs.Modifier modifier, Consumer<Consumer<GroupByItem>> consumer);

        _HavingSpec<I> ifGroupBy(@Nullable SQLs.Modifier modifier, Consumer<Consumer<GroupByItem>> consumer);

    }


    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>, _GroupBySpec<I> {

    }

    interface _WhereSpec<I extends Item> extends _QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>,
            _GroupBySpec<I> {

    }


    interface _RepeatableOnClause<I extends Item> extends _RepeatableClause<_OnClause<_JoinSpec<I>>>,
            _OnClause<_JoinSpec<I>> {

    }

    interface _TableSampleOnSpec<I extends Item> extends _StaticTableSampleClause<_RepeatableOnClause<I>>,
            _OnClause<_JoinSpec<I>> {

    }


    interface _JoinSpec<I extends Item>
            extends _JoinModifierClause<_TableSampleOnSpec<I>, _AsParensOnClause<_JoinSpec<I>>>,
            _PostgreJoinUndoneFuncClause<_OnClause<_JoinSpec<I>>>,
            _PostgreCrossClause<_TableSampleJoinSpec<I>, _ParensJoinSpec<I>>,
            _PostgreCrossUndoneFuncClause<_JoinSpec<I>>,
            _JoinCteClause<_OnClause<_JoinSpec<I>>>,
            _CrossJoinCteClause<_JoinSpec<I>>,
            _PostgreJoinNestedClause<_OnClause<_JoinSpec<I>>>,
            _PostgreCrossNestedClause<_JoinSpec<I>>,
            _PostgreDynamicJoinCrossClause<_JoinSpec<I>>,
            _WhereSpec<I> {

    }

    interface _ParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_JoinSpec<I>>, _JoinSpec<I> {

    }

    interface _RepeatableJoinClause<I extends Item> extends _RepeatableClause<_JoinSpec<I>>, _JoinSpec<I> {

    }


    interface _TableSampleJoinSpec<I extends Item> extends _StaticTableSampleClause<_RepeatableJoinClause<I>>,
            _JoinSpec<I> {

    }


    interface _FromSpec<I extends Item>
            extends _PostgreFromClause<_TableSampleJoinSpec<I>, _ParensJoinSpec<I>>,
            _PostgreFromUndoneFuncClause<_JoinSpec<I>>,
            _FromCteClause<_JoinSpec<I>>,
            _PostgreFromNestedClause<_JoinSpec<I>>,
            _UnionSpec<I> {

    }

    interface _PostgreSelectCommaSpec<I extends Item> extends _StaticSelectCommaClause<_PostgreSelectCommaSpec<I>>,
            _FromSpec<I> {

    }


    interface _PostgreSelectClause<I extends Item>
            extends _ModifierSelectClause<_PostgreSelectCommaSpec<I>>,
            _DynamicModifierSelectClause<Postgres.Modifier, _FromSpec<I>>,
            _DynamicDistinctOnExpClause<_PostgreSelectCommaSpec<I>>,
            _DynamicDistinctOnAndSelectsClause<_FromSpec<I>> {


    }


    interface _CyclePathColumnClause {

        void using(String cyclePathColumnName);

        void using(Supplier<String> supplier);
    }

    interface _CycleToMarkValueSpec extends _CyclePathColumnClause {

        /**
         * @param wordDefault see {@link SQLs#DEFAULT}
         */
        _CyclePathColumnClause to(LiteralExpression cycleMarkValue, SQLs.WordDefault wordDefault, LiteralExpression cycleMarkDefault);

        _CyclePathColumnClause to(Consumer<BiConsumer<LiteralExpression, LiteralExpression>> consumer);

        _CyclePathColumnClause ifTo(Consumer<BiConsumer<LiteralExpression, LiteralExpression>> consumer);

    }

    interface _SetCycleMarkColumnClause {

        _CycleToMarkValueSpec set(String cycleMarkColumnName);

        _CycleToMarkValueSpec set(Supplier<String> supplier);

    }

    interface _CteCycleColumnNameSpace {

        _SetCycleMarkColumnClause space(String firstColumnName, String... rest);


        _SetCycleMarkColumnClause space(Consumer<Consumer<String>> consumer);

        _SetCycleMarkColumnClause ifSpace(Consumer<Consumer<String>> consumer);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-CYCLE">Cycle Detection</a>
     */
    interface _CteCycleClause<I extends Item> {

        I cycle(Consumer<_CteCycleColumnNameSpace> consumer);

        I ifCycle(Consumer<_CteCycleColumnNameSpace> consumer);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-SEARCH">Search Order</a>
     */
    interface _SearchBreadthDepthClause {

        _SetSearchSeqColumnClause breadthFirstBy(String firstColumnName, String... rest);

        _SetSearchSeqColumnClause breadthFirstBy(Consumer<Consumer<String>> consumer);

        _SetSearchSeqColumnClause depthFirstBy(String firstColumnName, String... rest);

        _SetSearchSeqColumnClause depthFirstBy(Consumer<Consumer<String>> consumer);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-SEARCH">Search Order</a>
     */
    interface _CteSearchClause<I extends Item> {

        I search(Consumer<_SearchBreadthDepthClause> consumer);

        I ifSearch(Consumer<_SearchBreadthDepthClause> consumer);
    }

    interface _SetSearchSeqColumnClause {

        void set(String searchSeqColumnName);

        void set(Supplier<String> supplier);

    }




    interface _StaticCteCycleSpec<I extends Item> extends _CteCycleClause<_CteComma<I>>, _CteComma<I> {

    }


    interface _StaticCteSearchSpec<I extends Item> extends _CteSearchClause<_StaticCteCycleSpec<I>>, _StaticCteCycleSpec<I> {


    }


    interface _StaticCteAsClause<I extends Item> {

        <R extends _CteComma<I>> R as(Function<PostgreQuery._StaticCteComplexCommandSpec<I>, R> function);

        <R extends _CteComma<I>> R as(@Nullable SQLs.WordMaterialized modifier,
                                      Function<PostgreQuery._StaticCteComplexCommandSpec<I>, R> function);

    }

    interface _StaticCteParensSpec<I extends Item>
            extends _OptionalParensStringClause<_StaticCteAsClause<I>>,
            _StaticCteAsClause<I> {

    }

    interface _PostgreStaticWithClause<I extends Item> extends _StaticWithClause<_StaticCteParensSpec<I>> {

    }


    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteParensSpec<I>>,
            _StaticSpaceClause<I> {

    }


    interface _StaticCteSelectSpec<I extends Item> extends PostgreQuery._PostgreSelectClause<I>,
            _DynamicParensRowSetClause<_StaticCteSelectSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     *
     * @since 0.6.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item>
            extends _StaticCteSelectSpec<_StaticCteSearchSpec<I>>,
            PostgreValues._PostgreValuesClause<_CteComma<I>>,
            PostgreInsert._StaticSubOptionSpec<_CteComma<I>>,
            PostgreUpdate._SingleUpdateClause<_CteComma<I>, _CteComma<I>>,
            PostgreDelete._PostgreDeleteClause<_CteComma<I>, _CteComma<I>> {

    }


    interface _SelectSpec<I extends Item> extends _PostgreSelectClause<I>,
            _DynamicParensRowSetClause<WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    /**
     * <p>primary-statement syntax support static WITH clause,it's simple and clear and free
     * <p>This interface is public interface that developer can directly use.
     *
     * @since 0.6.0
     */
    interface WithSpec<I extends Item> extends _PostgreDynamicWithClause<_SelectSpec<I>>,
            _PostgreStaticWithClause<_SelectSpec<I>>,
            _SelectSpec<I> {

    }


    interface _DynamicCteCycleSpec extends _CteCycleClause<_CommaClause<PostgreCtes>>, _CommaClause<PostgreCtes> {

    }


    interface _DynamicCteSearchSpec extends _CteSearchClause<_DynamicCteCycleSpec>, _DynamicCteCycleSpec {


    }


    interface _QueryDynamicCteAsClause
            extends _PostgreDynamicCteAsClause<WithSpec<_DynamicCteSearchSpec>, _DynamicCteSearchSpec> {

    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_QueryDynamicCteAsClause>, _QueryDynamicCteAsClause {

    }


    interface _QueryComplexSpec<I extends Item> extends _PostgreSelectClause<I>,
            PostgreValues._PostgreValuesClause<I>,
            _DynamicParensRowSetClause<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>,
            _PostgreDynamicWithClause<_QueryComplexSpec<I>>,
            _PostgreStaticWithClause<_QueryComplexSpec<I>> {

    }


}
