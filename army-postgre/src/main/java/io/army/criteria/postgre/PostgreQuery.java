package io.army.criteria.postgre;

import io.army.criteria.*;
import io.army.criteria.impl.Postgres;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;


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


    interface _FrameExclusionSpec<I extends Item> extends _RightParenClause<I> {

        _RightParenClause<I> excludeCurrentRow();

        _RightParenClause<I> excludeGroup();

        _RightParenClause<I> excludeTies();

        _RightParenClause<I> excludeNoOthers();

        _RightParenClause<I> ifExcludeCurrentRow(BooleanSupplier predicate);

        _RightParenClause<I> ifExcludeGroup(BooleanSupplier predicate);

        _RightParenClause<I> ifExcludeTies(BooleanSupplier predicate);

        _RightParenClause<I> ifExcludeNoOthers(BooleanSupplier predicate);

    }

    interface _PostgreFrameEndNonExpBoundClause<I extends Item> extends Window._FrameNonExpBoundClause {

        @Override
        _FrameExclusionSpec<I> currentRow();

        @Override
        _FrameExclusionSpec<I> unboundedPreceding();

        @Override
        _FrameExclusionSpec<I> unboundedFollowing();
    }

    interface _PostgreFrameEndExpBoundClause<I extends Item> extends Window._FrameExpBoundClause {

        @Override
        _FrameExclusionSpec<I> preceding();

        @Override
        _FrameExclusionSpec<I> following();
    }

    interface _PostgreFrameBetweenAndClause<I extends Item>
            extends Window._FrameBetweenAndExpClause<_PostgreFrameEndExpBoundClause<I>>
            , _StaticAndClause<_PostgreFrameEndNonExpBoundClause<I>> {

    }


    interface _PostgreFrameStartNonExpBoundClause<I extends Item> extends Window._FrameNonExpBoundClause {

        @Override
        _PostgreFrameBetweenAndClause<I> currentRow();

        @Override
        _PostgreFrameBetweenAndClause<I> unboundedPreceding();

        @Override
        _PostgreFrameBetweenAndClause<I> unboundedFollowing();
    }

    interface _PostgreFrameStartExpBoundClause<I extends Item> extends Window._FrameExpBoundClause {

        @Override
        _PostgreFrameBetweenAndClause<I> preceding();

        @Override
        _PostgreFrameBetweenAndClause<I> following();

    }

    interface _PostgreFrameBetweenSpec<I extends Item>
            extends Window._FrameBetweenExpClause<_PostgreFrameStartExpBoundClause<I>>
            , _StaticBetweenClause<_PostgreFrameStartNonExpBoundClause<I>>
            , _PostgreFrameStartNonExpBoundClause<I> {

    }

    interface _PostgreFrameUnitSpec<I extends Item>
            extends Window._FrameUnitExpClause<_PostgreFrameEndExpBoundClause<I>>
            , Window._FrameUnitNoExpClause<_PostgreFrameBetweenSpec<I>>
            , Statement._RightParenClause<I> {

        _PostgreFrameBetweenSpec<I> groups();

        _PostgreFrameBetweenSpec<I> ifGroups(BooleanSupplier predicate);

        _PostgreFrameEndExpBoundClause<I> groups(Expression expression);

        _PostgreFrameEndExpBoundClause<I> groups(Supplier<Expression> supplier);

        <E> _PostgreFrameEndExpBoundClause<I> groups(Function<E, Expression> valueOperator, @Nullable E value);

        <E> _PostgreFrameEndExpBoundClause<I> groups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause<I> groups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        _PostgreFrameEndExpBoundClause<I> ifGroups(Supplier<Expression> supplier);

        <E> _PostgreFrameEndExpBoundClause<I> ifGroups(Function<E, Expression> valueOperator, @Nullable E value);

        <E> _PostgreFrameEndExpBoundClause<I> ifGroups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause<I> ifGroups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _WindowOrderBySpec<I extends Item> extends _PostgreOrderByClause<_PostgreFrameUnitSpec<I>>
            , _PostgreFrameUnitSpec<I> {

    }

    interface _WindowPartitionBySpec<I extends Item> extends Window._PartitionByExpClause<_WindowOrderBySpec<I>>
            , _WindowOrderBySpec<I> {

    }

    interface _WindowLeftParenClause<I extends Item>
            extends Window._LeftParenNameClause<_WindowPartitionBySpec<I>> {

    }

    interface _WindowAsClause<I extends Item> extends Statement._StaticAsClaus<_WindowLeftParenClause<I>> {

    }


    interface _UnionSpec<I extends Item> extends _QueryUnionClause<_QueryWithComplexSpec<I>>
            , _QueryIntersectClause<_QueryWithComplexSpec<I>>
            , _QueryExceptClause<_QueryWithComplexSpec<I>>
            , _AsQueryClause<I> {

    }


    interface _UnionFetchSpec<I extends Item> extends _QueryFetchClause<_AsQueryClause<I>>, _AsQueryClause<I> {

    }

    interface _UnionOffsetSpec<I extends Item> extends _QueryOffsetClause<_UnionFetchSpec<I>>, _UnionFetchSpec<I> {


    }

    interface _UnionLimitSpec<I extends Item> extends _RowCountLimitAllClause<_UnionOffsetSpec<I>>
            , _UnionOffsetSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _PostgreOrderByClause<_UnionLimitSpec<I>>
            , _UnionLimitSpec<I>, _UnionSpec<I> {

    }


    interface _LockWaitOptionSpec<I extends Item> extends _MinLockWaitOptionClause<_LockSpec<I>>, _LockSpec<I> {//TODO validate multi-lock clause


    }


    interface _LockOfTableSpec<I extends Item> extends _LockOfTableClause<_LockWaitOptionSpec<I>>
            , _LockWaitOptionSpec<I> {


    }


    interface _LockSpec<I extends Item> extends _PostgreLockClause<_LockOfTableSpec<I>>, _AsQueryClause<I> {


    }


    interface _FetchSpec<I extends Item> extends _QueryFetchClause<_LockSpec<I>>, _LockSpec<I> {


    }


    interface _OffsetSpec<I extends Item> extends _QueryOffsetClause<_FetchSpec<I>>, _LockSpec<I> {


    }


    interface _LimitSpec<I extends Item> extends _RowCountLimitAllClause<_OffsetSpec<I>>, _OffsetSpec<I> {

        @Override
        _FetchSpec<I> offset(Expression start, FetchRow row);

        @Override
        _FetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator, long start, FetchRow row);

        @Override
        <N extends Number> _FetchSpec<I> offset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier, FetchRow row);

        @Override
        _FetchSpec<I> offset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function, String keyName, FetchRow row);

        @Override
        _FetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start, FetchRow row);

        @Override
        <N extends Number> _FetchSpec<I> ifOffset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier, FetchRow row);

        @Override
        _FetchSpec<I> ifOffset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function, String keyName, FetchRow row);
    }


    interface _OrderBySpec<I extends Item> extends _PostgreOrderByClause<_LimitSpec<I>>, _LimitSpec<I>, _UnionSpec<I> {

    }

    interface _WindowCommaSpec<I extends Item>
            extends Window._StaticWindowCommaClause<_WindowAsClause<_WindowCommaSpec<I>>>
            , _OrderBySpec<I> {

    }

    interface _WindowSpec<I extends Item> extends Window._DynamicWindowClause<PostgreWindows, _OrderBySpec<I>>
            , Window._StaticWindowClause<_WindowAsClause<_WindowCommaSpec<I>>>
            , _OrderBySpec<I> {

    }


    interface _HavingSpec<I extends Item> extends _HavingClause<_WindowSpec<I>>, _WindowSpec<I> {

    }


    interface _GroupBySpec<I extends Item> extends _GroupByClause<_HavingSpec<I>>
            , _WindowSpec<I> {
        //TODO add dialect method
    }

    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>, _GroupBySpec<I> {

    }

    interface _WhereSpec<I extends Item> extends _QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>
            , _GroupBySpec<I> {

    }


    interface _RepeatableOnClause<I extends Item> extends _RepeatableClause<_OnClause<_JoinSpec<I>>>
            , _OnClause<_JoinSpec<I>> {

    }

    interface _TableSampleOnSpec<I extends Item> extends _TableSampleClause<_RepeatableOnClause<I>>
            , _OnClause<_JoinSpec<I>> {

    }


    interface _JoinSpec<I extends Item>
            extends _PostgreJoinClause<_TableSampleOnSpec<I>, _OnClause<_JoinSpec<I>>>
            , _PostgreCrossJoinClause<_TableSampleJoinSpec<I>, _JoinSpec<I>>
            , _JoinNestedClause<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_JoinSpec<I>>>
            , _PostgreDynamicJoinClause<_JoinSpec<I>>
            , _PostgreDynamicCrossJoinClause<_JoinSpec<I>>
            , _WhereSpec<I> {

        //TODO add dialect function tabular
    }

    interface _RepeatableJoinClause<I extends Item> extends _RepeatableClause<_JoinSpec<I>>, _JoinSpec<I> {

    }


    interface _TableSampleJoinSpec<I extends Item> extends _TableSampleClause<_RepeatableJoinClause<I>>, _JoinSpec<I> {

    }


    interface _FromSpec<I extends Item>
            extends _PostgreFromClause<_TableSampleJoinSpec<I>, _JoinSpec<I>>
            , _FromNestedClause<_NestedLeftParenSpec<_JoinSpec<I>>>
            , _UnionSpec<I> {

    }

    interface _PostgreSelectCommaSpec<I extends Item> extends _StaticSelectCommaClause<_PostgreSelectCommaSpec<I>>
            , _FromSpec<I> {

    }


    interface _PostgreSelectClause<I extends Item>
            extends _ModifierSelectClause<Postgres.Modifier, _PostgreSelectCommaSpec<I>>
            , _DynamicModifierSelectClause<Postgres.Modifier, _FromSpec<I>> {

    }

    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreSelectClause<I> {

    }


    interface _SelectSpec<I extends Item> extends _PostgreSelectClause<I>
            , _LeftParenClause<_MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _MinWithSpec<I extends Item> extends _PostgreDynamicWithClause<_SelectSpec<I>>
            , _SelectSpec<I> {

    }

    /**
     * <p>
     * primary-statement syntax support static WITH clause,it's simple and clear and free
     * </p>
     *
     * @since 1.0
     */
    interface _WithSpec<I extends Item> extends _MinWithSpec<I>
            , _StaticWithClause<_StaticCteLeftParenSpec<_CteComma<I>>> {

    }


    interface _DynamicSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_MinWithSpec<I>>
            , _MinWithSpec<I> {

    }

    interface _DynamicCteQuerySpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_CteSearchSpec<PostgreCtes>>> {

    }

    interface _QueryComplexSpec<I extends Item> extends _PostgreSelectClause<I>
            , PostgreValues._PostgreValuesClause<I>
            , _LeftParenClause<_QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>
            , _PostgreDynamicWithClause<_QueryComplexSpec<I>> {

    }


}
