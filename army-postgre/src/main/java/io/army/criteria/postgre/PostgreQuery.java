package io.army.criteria.postgre;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;

import java.util.function.*;


/**
 * <p>
 * This interface representing postgre SELECT statement.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
 * @since 1.0
 */
public interface PostgreQuery extends Query, PostgreStatement {


    interface _PostgreOrderByClause<OR> extends _StaticOrderByClause<OR> {
        //TODO add dialect method
    }

    interface _PostgreLockClause<R> extends _MinLockOptionClause<R> {

        R forNoKeyUpdate();

        R forKeyShare();

        R ifForNoKeyUpdate(BooleanSupplier predicate);

        R ifForKeyShare(BooleanSupplier predicate);

    }


    interface _FrameExclusionSpec extends Item {

        Item excludeCurrentRow();

        Item excludeGroup();

        Item excludeTies();

        Item excludeNoOthers();

        Item ifExcludeCurrentRow(BooleanSupplier predicate);

        Item ifExcludeGroup(BooleanSupplier predicate);

        Item ifExcludeTies(BooleanSupplier predicate);

        Item ifExcludeNoOthers(BooleanSupplier predicate);

    }

    interface _PostgreFrameEndNonExpBoundClause extends Window._FrameNonExpBoundClause {

        @Override
        _FrameExclusionSpec currentRow();

        @Override
        _FrameExclusionSpec unboundedPreceding();

        @Override
        _FrameExclusionSpec unboundedFollowing();
    }

    interface _PostgreFrameEndExpBoundClause extends Window._FrameExpBoundClause {

        @Override
        _FrameExclusionSpec preceding();

        @Override
        _FrameExclusionSpec following();
    }

    interface _PostgreFrameBetweenAndClause
            extends Window._FrameBetweenAndExpClause<_PostgreFrameEndExpBoundClause>
            , _StaticAndClause<_PostgreFrameEndNonExpBoundClause> {

    }


    interface _PostgreFrameStartNonExpBoundClause extends Window._FrameNonExpBoundClause {

        @Override
        _PostgreFrameBetweenAndClause currentRow();

        @Override
        _PostgreFrameBetweenAndClause unboundedPreceding();

        @Override
        _PostgreFrameBetweenAndClause unboundedFollowing();
    }

    interface _PostgreFrameStartExpBoundClause extends Window._FrameExpBoundClause {

        @Override
        _PostgreFrameBetweenAndClause preceding();

        @Override
        _PostgreFrameBetweenAndClause following();

    }

    interface _PostgreFrameBetweenSpec
            extends Window._FrameBetweenExpClause<_PostgreFrameStartExpBoundClause>,
            _StaticBetweenClause<_PostgreFrameStartNonExpBoundClause>,
            _PostgreFrameStartNonExpBoundClause {

    }

    interface _PostgreFrameUnitSpec
            extends Window._FrameUnitExpClause<_PostgreFrameEndExpBoundClause>,
            Window._FrameUnitNoExpClause<_PostgreFrameBetweenSpec> {

        _PostgreFrameBetweenSpec groups();

        _PostgreFrameBetweenSpec ifGroups(BooleanSupplier predicate);

        _PostgreFrameEndExpBoundClause groups(Expression expression);

        _PostgreFrameEndExpBoundClause groups(Supplier<Expression> supplier);

        _PostgreFrameEndExpBoundClause groups(Function<Object, Expression> valueOperator, @Nullable Object value);

        <E> _PostgreFrameEndExpBoundClause groups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause groups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        _PostgreFrameEndExpBoundClause ifGroups(Supplier<Expression> supplier);

        <E> _PostgreFrameEndExpBoundClause ifGroups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause ifGroups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _WindowOrderBySpec extends _PostgreOrderByClause<_PostgreFrameUnitSpec>, _PostgreFrameUnitSpec {
        //TODO _PostgreOrderByClause return order by comma
    }

    interface _WindowPartitionBySpec extends Window._PartitionByExpClause<_WindowOrderBySpec>,
            _WindowOrderBySpec {

    }


    interface _UnionSpec<I extends Item> extends _QueryUnionClause<_QueryWithComplexSpec<I>>,
            _QueryIntersectClause<_QueryWithComplexSpec<I>>,
            _QueryExceptClause<_QueryWithComplexSpec<I>>,
            _AsQueryClause<I> {

    }


    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {


    }

    interface _UnionLimitSpec<I extends Item> extends _RowCountLimitAllClause<_UnionOffsetSpec<I>>, _UnionOffsetSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _PostgreOrderByClause<_UnionLimitSpec<I>>,
            _UnionLimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_LockSpec<I>>, _LockSpec<I> {//TODO validate multi-lock clause


    }


    interface _LockOfTableSpec<I extends Item> extends _LockOfTableAliasClause<_LockWaitOptionSpec<I>>,
            _LockWaitOptionSpec<I> {

    }


    interface _LockSpec<I extends Item> extends _PostgreLockClause<_LockOfTableSpec<I>>, _AsQueryClause<I> {


    }


    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_LockSpec<I>>, _LockSpec<I> {


    }


    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>, _LockSpec<I> {


    }


    interface _LimitSpec<I extends Item> extends _RowCountLimitAllClause<_OffsetSpec<I>>, _OffsetSpec<I> {

    }


    interface _OrderBySpec<I extends Item> extends _PostgreOrderByClause<_LimitSpec<I>>, _LimitSpec<I>, _UnionSpec<I> {

    }


    interface _WindowAsClause<I extends Item> extends Window._WindowAsClause<_WindowCommaSpec<I>> {

        _WindowCommaSpec<I> as(@Nullable String existingWindowName, Consumer<_WindowPartitionBySpec> consumer);

        _WindowCommaSpec<I> as(Consumer<_WindowPartitionBySpec> consumer);

    }

    interface _WindowCommaSpec<I extends Item> extends _OrderBySpec<I> {

        _WindowAsClause<I> comma(String name);

    }

    interface _WindowSpec<I extends Item> extends Window._DynamicWindowClause<PostgreWindows, _OrderBySpec<I>>,
            _OrderBySpec<I> {

        _WindowAsClause<I> window(String name);

    }


    interface _HavingSpec<I extends Item> extends _HavingClause<_WindowSpec<I>>, _WindowSpec<I> {

    }


    interface _GroupBySpec<I extends Item> extends _GroupByClause<_HavingSpec<I>>, _WindowSpec<I> {
        //TODO add dialect method
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
            _PostgreCrossClause<_TableSampleJoinSpec<I>, _ParensJoinSpec<I>>,
            _JoinCteClause<_OnClause<_JoinSpec<I>>>,
            _CrossJoinCteClause<_JoinSpec<I>>,
            _PostgreJoinNestedClause<_OnClause<_JoinSpec<I>>>,
            _PostgreCrossNestedClause<_JoinSpec<I>>,
            _PostgreDynamicJoinCrossClause<_JoinSpec<I>>,
            _WhereSpec<I> {

    }

    interface _ParensJoinSpec<I extends Item> extends _ParensStringClause<_JoinSpec<I>>, _JoinSpec<I> {

    }

    interface _RepeatableJoinClause<I extends Item> extends _RepeatableClause<_JoinSpec<I>>, _JoinSpec<I> {

    }


    interface _TableSampleJoinSpec<I extends Item> extends _StaticTableSampleClause<_RepeatableJoinClause<I>>,
            _JoinSpec<I> {

    }


    interface _FromSpec<I extends Item>
            extends _PostgreFromClause<_TableSampleJoinSpec<I>, _ParensJoinSpec<I>>,
            _FromCteClause<_JoinSpec<I>>,
            _PostgreFromNestedClause<_JoinSpec<I>>,
            _UnionSpec<I> {

    }

    interface _PostgreSelectCommaSpec<I extends Item> extends _StaticSelectCommaClause<_PostgreSelectCommaSpec<I>>,
            _FromSpec<I> {

    }


    interface _PostgreSelectClause<I extends Item>
            extends _ModifierSelectClause<Postgres.Modifier, _PostgreSelectCommaSpec<I>>,
            _DynamicModifierSelectClause<Postgres.Modifier, _FromSpec<I>> {

    }


    interface _CyclePathColumnClause<I extends Item> {

        I using(String cyclePathColumnName);

        I using(Supplier<String> supplier);

    }

    interface _CycleToMarkValueSpec<I extends Item> extends _CyclePathColumnClause<I> {

        _CyclePathColumnClause<I> to(Expression cycleMarkValue, SQLs.WordDefault wordDefault, Expression cycleMarkDefault);

        _CyclePathColumnClause<I> to(Consumer<BiConsumer<Expression, Expression>> consumer);

        _CyclePathColumnClause<I> ifTo(Consumer<BiConsumer<Expression, Expression>> consumer);

    }

    interface _SetCycleMarkColumnClause<I extends Item> {

        _CycleToMarkValueSpec<I> set(String cycleMarkColumnName);

        _CycleToMarkValueSpec<I> set(Supplier<String> supplier);

    }

    interface _CteCycleClause<I extends Item> extends Item {

        _SetCycleMarkColumnClause<I> cycle(String firstColumnName, String... rest);

        _SetCycleMarkColumnClause<I> cycle(Consumer<Consumer<String>> consumer);

        _SetCycleMarkColumnClause<I> ifCycle(Consumer<Consumer<String>> consumer);


    }


    interface _SetSearchSeqColumnClause<I extends Item> {

        I set(String searchSeqColumnName);

        I set(Supplier<String> supplier);

    }

    interface _SearchFirstByClause<I extends Item> {

        _SetSearchSeqColumnClause<I> firstBy(String firstColumnName, String... rest);

        _SetSearchSeqColumnClause<I> firstBy(Consumer<Consumer<String>> consumer);

    }

    interface _CteSearchClause<I extends Item> {

        _SearchFirstByClause<I> searchBreadth();

        _SearchFirstByClause<I> searchDepth();

        _SearchFirstByClause<I> ifSearchBreadth(BooleanSupplier predicate);

        _SearchFirstByClause<I> ifSearchDepth(BooleanSupplier predicate);

    }


    interface _StaticCteAsClause<I extends Item> {

        <R extends _CteComma<I>> R as(Function<PostgreQuery._StaticCteComplexCommandSpec<I>, R> function);

        <R extends _CteComma<I>> R as(@Nullable Postgres.WordMaterialized modifier,
                                      Function<PostgreQuery._StaticCteComplexCommandSpec<I>, R> function);

    }

    interface _StaticCteParensSpec<I extends Item>
            extends Statement._ParensStringClause<_StaticCteAsClause<I>>,
            _StaticCteAsClause<I> {

    }

    interface _PostgreStaticWithClause<I extends Item> extends _StaticWithClause<_StaticCteParensSpec<I>> {

    }


    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteParensSpec<I>>,
            _StaticSpaceClause<I> {

    }

    interface _StaticCteCycleSpec<I extends Item> extends _CteCycleClause<_CteComma<I>>, _CteComma<I> {

    }

    interface _StaticCteSearchSpec<I extends Item> extends _CteSearchClause<_StaticCteCycleSpec<I>>,
            _StaticCteCycleSpec<I> {

    }


    interface _StaticCteSelectSpec<I extends Item> extends PostgreQuery._PostgreSelectClause<I>,
            _LeftParenClause<_StaticCteSelectSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item>
            extends _StaticCteSelectSpec<_StaticCteSearchSpec<I>>,
            PostgreValues._PostgreValuesClause<_CteComma<I>>,
            PostgreInsert._StaticSubOptionSpec<_CteComma<I>>,
            PostgreUpdate._SingleUpdateClause<_CteComma<I>, _CteComma<I>>,
            PostgreDelete._SingleDeleteClause<_CteComma<I>, _CteComma<I>> {

    }



    interface _SelectSpec<I extends Item> extends _PostgreSelectClause<I>,
            _LeftParenClause<_WithSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    @Deprecated
    interface _MinWithSpec<I extends Item> extends _PostgreDynamicWithClause<_SelectSpec<I>>,
            _SelectSpec<I> {

    }

    /**
     * <p>
     * primary-statement syntax support static WITH clause,it's simple and clear and free
     * </p>
     *
     * @since 1.0
     */
    interface _WithSpec<I extends Item> extends _PostgreDynamicWithClause<_SelectSpec<I>>,
            _PostgreStaticWithClause<_SelectSpec<I>>,
            _SelectSpec<I> {

    }


    interface _DynamicCteCycleSpec extends _CteCycleClause<_CommaClause<PostgreCtes>>,
            _CommaClause<PostgreCtes> {

    }

    interface _DynamicCteSearchSpec extends _CteSearchClause<_DynamicCteCycleSpec>, _DynamicCteCycleSpec {

    }

    interface _DynamicCteAsClause {

        _DynamicCteSearchSpec as(Function<_WithSpec<_DynamicCteSearchSpec>, _DynamicCteSearchSpec> function);

        _DynamicCteSearchSpec as(@Nullable Postgres.WordMaterialized materialized,
                                 Function<_WithSpec<_DynamicCteSearchSpec>, _DynamicCteSearchSpec> function);

    }

    interface _DynamicCteParensSpec extends _ParensStringClause<_DynamicCteAsClause>, _DynamicCteAsClause {

    }



    interface _QueryComplexSpec<I extends Item> extends _PostgreSelectClause<I>,
            PostgreValues._PostgreValuesClause<I>,
            _LeftParenClause<_QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>,
            _PostgreDynamicWithClause<_QueryComplexSpec<I>>,
            _PostgreStaticWithClause<_QueryComplexSpec<I>>,
            _DynamicParensRowSetClause<_UnionOrderBySpec<I>> {

    }


}
