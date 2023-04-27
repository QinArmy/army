package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.UpdateStatement;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing Postgre DELETE syntax.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-delete.html">Postgre DELETE syntax</a>
 * @since 1.0
 */
public interface PostgreDelete extends PostgreStatement {


    interface _PostgreDeleteClause<R> extends Item {

        R deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias);

        R deleteFrom(@Nullable SQLs.WordOnly only, TableMeta<?> table, SQLs.WordAs as, String tableAlias);

        R deleteFrom(TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

        R deleteFrom(@Nullable SQLs.WordOnly only, TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

    }


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


    interface _SingleDeleteClause<I extends Item, Q extends Item> extends _PostgreDeleteClause<_SingleUsingSpec<I, Q>> {

    }


    interface _SingleWithSpec<I extends Item, Q extends Item> extends _SingleDeleteClause<I, Q>,
            _PostgreDynamicWithClause<_SingleDeleteClause<I, Q>>,
            PostgreQuery._PostgreStaticWithClause<_SingleDeleteClause<I, Q>> {

    }


    interface _DeleteDynamicCteAsClause
            extends _PostgreDynamicCteAsClause<_SingleWithSpec<_CommaClause<PostgreCtes>, _CommaClause<PostgreCtes>>,
            _CommaClause<PostgreCtes>> {


    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_DeleteDynamicCteAsClause>, _DeleteDynamicCteAsClause {

    }


    /*-------------------below batch syntax -------------------*/


    interface _BatchStaticReturningCommaSpec<Q extends Item>
            extends _StaticDmlReturningCommaClause<_BatchStaticReturningCommaSpec<Q>>,
            _BatchParamClause<_DqlDeleteSpec<Q>> {

    }

    interface _BatchReturningSpec<I extends Item, Q extends Item>
            extends _StaticDmlReturningClause<_BatchStaticReturningCommaSpec<Q>>,
            _DynamicReturningClause<_BatchParamClause<_DqlDeleteSpec<Q>>>,
            _BatchParamClause<_DmlDeleteSpec<I>> {

    }


    interface _BatchSingleWhereAndSpec<I extends Item, Q extends Item>
            extends UpdateStatement._UpdateWhereAndClause<_BatchSingleWhereAndSpec<I, Q>>,
            _BatchReturningSpec<I, Q> {

    }


    interface _BatchSingleWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_BatchReturningSpec<I, Q>, _BatchSingleWhereAndSpec<I, Q>>,
            _WhereCurrentOfClause<_BatchReturningSpec<I, Q>> {


    }


    interface _BatchRepeatableOnClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>,
            _OnClause<_BatchSingleJoinSpec<I, Q>> {

    }

    interface _BatchTableSampleOnSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_BatchRepeatableOnClause<I, Q>>,
            _OnClause<_BatchSingleJoinSpec<I, Q>> {

    }


    interface _BatchSingleJoinSpec<I extends Item, Q extends Item>
            extends _JoinModifierClause<_BatchTableSampleOnSpec<I, Q>, _AsParensOnClause<_BatchSingleJoinSpec<I, Q>>>,
            _PostgreJoinUndoneFuncClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>,
            _PostgreCrossClause<_BatchTableSampleJoinSpec<I, Q>, _BatchParensJoinSpec<I, Q>>,
            _PostgreCrossUndoneFuncClause<_BatchSingleJoinSpec<I, Q>>,
            _JoinCteClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>,
            _CrossJoinCteClause<_BatchSingleJoinSpec<I, Q>>,
            _PostgreJoinNestedClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>,
            _PostgreCrossNestedClause<_BatchSingleJoinSpec<I, Q>>,
            _PostgreDynamicJoinCrossClause<_BatchSingleJoinSpec<I, Q>>,
            _BatchSingleWhereClause<I, Q> {

    }

    interface _BatchParensJoinSpec<I extends Item, Q extends Item>
            extends _OptionalParensStringClause<_BatchSingleJoinSpec<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }

    interface _BatchRepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_BatchSingleJoinSpec<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }


    interface _BatchTableSampleJoinSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_BatchRepeatableJoinClause<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }


    interface _BatchSingleUsingSpec<I extends Item, Q extends Item>
            extends _PostgreUsingClause<_BatchTableSampleJoinSpec<I, Q>, _BatchParensJoinSpec<I, Q>>,
            _PostgreUsingUndoneFuncClause<_BatchSingleJoinSpec<I, Q>>,
            _UsingCteClause<_BatchSingleJoinSpec<I, Q>>,
            _PostgreUsingNestedClause<_BatchSingleJoinSpec<I, Q>>,
            _BatchSingleWhereClause<I, Q> {
    }


    interface _BatchSingleDeleteClause<I extends Item, Q extends Item>
            extends _PostgreDeleteClause<_BatchSingleUsingSpec<I, Q>> {

    }

    interface _BatchSingleWithSpec<I extends Item, Q extends Item> extends _BatchSingleDeleteClause<I, Q>,
            PostgreQuery._PostgreDynamicWithClause<_BatchSingleDeleteClause<I, Q>>,
            PostgreQuery._PostgreStaticWithClause<_BatchSingleDeleteClause<I, Q>> {

    }


}
