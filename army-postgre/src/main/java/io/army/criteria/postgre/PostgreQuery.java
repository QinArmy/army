package io.army.criteria.postgre;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

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
            extends Window._FrameBetweenExpClause<_PostgreFrameStartExpBoundClause>
            , _StaticBetweenClause<_PostgreFrameStartNonExpBoundClause>
            , _PostgreFrameStartNonExpBoundClause {

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

        <E> _PostgreFrameEndExpBoundClause ifGroups(Function<E, Expression> valueOperator, @Nullable E value);

        <E> _PostgreFrameEndExpBoundClause ifGroups(Function<E, Expression> valueOperator, Supplier<E> supplier);

        _PostgreFrameEndExpBoundClause ifGroups(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }

    interface _WindowOrderBySpec extends _PostgreOrderByClause<_PostgreFrameUnitSpec>
            , _PostgreFrameUnitSpec {

    }

    interface _WindowPartitionBySpec extends Window._PartitionByExpClause<_WindowOrderBySpec>
            , _WindowOrderBySpec {

    }

    @Deprecated
    interface _WindowLeftParenClause<I extends Item> {

    }

    @Deprecated
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
            extends Window._StaticWindowCommaClause<_WindowAsClause<_WindowCommaSpec<I>>>, _OrderBySpec<I> {

        _WindowCommaSpec<I> comma(String name, SQLs.WordAs as, Consumer<_WindowPartitionBySpec> consumer);

        _WindowCommaSpec<I> comma(String name, SQLs.WordAs as, @Nullable String existingWindowName, Consumer<_WindowPartitionBySpec> consumer);
    }

    interface _WindowSpec<I extends Item> extends Window._DynamicWindowClause<PostgreWindows, _OrderBySpec<I>>
            , _OrderBySpec<I> {

        _WindowCommaSpec<I> window(String name, SQLs.WordAs as, Consumer<_WindowPartitionBySpec> consumer);

        _WindowCommaSpec<I> window(String name, SQLs.WordAs as, @Nullable String existingWindowName, Consumer<_WindowPartitionBySpec> consumer);


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

    interface _TableSampleOnSpec<I extends Item> extends _StaticTableSampleClause<_RepeatableOnClause<I>>
            , _OnClause<_JoinSpec<I>> {

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


    interface _TableSampleJoinSpec<I extends Item> extends _StaticTableSampleClause<_RepeatableJoinClause<I>>, _JoinSpec<I> {

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

    interface _CteComma<I extends Item> extends _PostgreStaticCteCommaClause<_CteComma<I>>,
            _StaticSpaceClause<_SelectSpec<I>> {

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
            _PostgreStaticWithClause<_CteComma<I>>,
            _SelectSpec<I> {

    }


    interface _DynamicSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_MinWithSpec<I>>, _MinWithSpec<I> {

    }

    interface _DynamicCteQuerySpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_CteSearchSpec<PostgreCtes>>> {

    }

    interface _QueryComplexSpec<I extends Item> extends _PostgreSelectClause<I>,
            PostgreValues._PostgreValuesClause<I>,
            _LeftParenClause<_QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _QueryWithComplexSpec<I extends Item> extends _QueryComplexSpec<I>
            , _PostgreDynamicWithClause<_QueryComplexSpec<I>> {

    }


}
