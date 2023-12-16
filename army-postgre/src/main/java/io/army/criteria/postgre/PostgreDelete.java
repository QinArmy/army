package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.impl.SQLs;

import javax.annotation.Nullable;

import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing Postgre DELETE syntax.
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-delete.html">Postgre DELETE syntax</a>
 * @since 0.6.0
 */
public interface PostgreDelete extends PostgreStatement {



    interface _StaticReturningCommaSpec<Q extends Item>
            extends _StaticDmlReturningCommaClause<_StaticReturningCommaSpec<Q>>,
            _DqlDeleteSpec<Q> {

    }

    interface _ReturningSpec<I extends Item, Q extends Item>
            extends _StaticDmlReturningClause<_StaticReturningCommaSpec<Q>>,
            _DynamicReturningClause<_DqlDeleteSpec<Q>>,
            _DmlDeleteSpec<I> {

    }


    interface _SingleWhereAndSpec<I extends Item, Q extends Item>
            extends _WhereAndClause<_SingleWhereAndSpec<I, Q>>,
            _ReturningSpec<I, Q> {

    }


    interface _SingleWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_ReturningSpec<I, Q>, _SingleWhereAndSpec<I, Q>>,
            _WhereCurrentOfClause<_ReturningSpec<I, Q>> {

    }


    interface _RepeatableOnClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_OnClause<_SingleJoinSpec<I, Q>>>,
            _OnClause<_SingleJoinSpec<I, Q>> {

    }

    interface _TableSampleOnSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_RepeatableOnClause<I, Q>>,
            _OnClause<_SingleJoinSpec<I, Q>> {

    }


    interface _SingleJoinSpec<I extends Item, Q extends Item>
            extends _JoinModifierClause<_TableSampleOnSpec<I, Q>, _AsParensOnClause<_SingleJoinSpec<I, Q>>>,
            _PostgreJoinUndoneFuncClause<_OnClause<_SingleJoinSpec<I, Q>>>,
            _PostgreCrossClause<_TableSampleJoinSpec<I, Q>, _ParensJoinSpec<I, Q>>,
            _PostgreCrossUndoneFuncClause<_SingleJoinSpec<I, Q>>,
            _JoinCteClause<_OnClause<_SingleJoinSpec<I, Q>>>,
            _CrossJoinCteClause<_SingleJoinSpec<I, Q>>,
            _PostgreJoinNestedClause<_OnClause<_SingleJoinSpec<I, Q>>>,
            _PostgreCrossNestedClause<_SingleJoinSpec<I, Q>>,
            _PostgreDynamicJoinCrossClause<_SingleJoinSpec<I, Q>>,
            _SingleWhereClause<I, Q> {

    }

    interface _ParensJoinSpec<I extends Item, Q extends Item> extends _OptionalParensStringClause<_SingleJoinSpec<I, Q>>,
            _SingleJoinSpec<I, Q> {

    }

    interface _RepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q> {

    }


    interface _TableSampleJoinSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_RepeatableJoinClause<I, Q>>, _SingleJoinSpec<I, Q> {

    }


    interface _SingleUsingSpec<I extends Item, Q extends Item>
            extends _PostgreUsingClause<_TableSampleJoinSpec<I, Q>, _ParensJoinSpec<I, Q>>,
            _PostgreUsingUndoneFuncClause<_SingleJoinSpec<I, Q>>,
            _UsingCteClause<_SingleJoinSpec<I, Q>>,
            _PostgreUsingNestedClause<_SingleJoinSpec<I, Q>>,
            _SingleWhereClause<I, Q> {
    }


    interface _PostgreDeleteClause<I extends Item, Q extends Item> extends Item {

        _SingleUsingSpec<I, Q> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias);

        _SingleUsingSpec<I, Q> deleteFrom(@Nullable SQLs.WordOnly only, TableMeta<?> table, SQLs.WordAs as, String tableAlias);

        _SingleUsingSpec<I, Q> deleteFrom(TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

    }


    interface _SingleWithSpec<I extends Item, Q extends Item> extends _PostgreDeleteClause<I, Q>,
            _PostgreDynamicWithClause<_PostgreDeleteClause<I, Q>>,
            PostgreQuery._PostgreStaticWithClause<_PostgreDeleteClause<I, Q>> {

    }


    interface _DeleteDynamicCteAsClause
            extends _PostgreDynamicCteAsClause<_SingleWithSpec<_CommaClause<PostgreCtes>, _CommaClause<PostgreCtes>>,
            _CommaClause<PostgreCtes>> {


    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_DeleteDynamicCteAsClause>, _DeleteDynamicCteAsClause {

    }




}
