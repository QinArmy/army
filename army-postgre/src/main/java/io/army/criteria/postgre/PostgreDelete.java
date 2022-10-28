package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
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


    interface _PostgreUsingClause<FT, FS> extends _UsingModifierClause<FT, FS>
            , _UsingCteClause<FS> {

    }

    interface _PostgreDeleteClause<R> {

        R delete(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        R delete(SQLs.WordOnly wordOnly, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);
    }


    interface _StaticReturningCommaSpec<Q extends Item>
            extends _StaticReturningCommaClause<_StaticReturningCommaSpec<Q>>
            , _DqlDeleteSpec<Q> {

    }

    interface _ReturningSpec<I extends Item, Q extends Item>
            extends _StaticReturningClause<_StaticReturningCommaSpec<Q>>
            , _DynamicReturningClause<_DqlDeleteSpec<Q>>
            , _DmlDeleteSpec<I> {

    }


    interface _SingleWhereAndSpec<I extends Item, Q extends Item>
            extends _WhereAndClause<_SingleWhereAndSpec<I, Q>>
            , _ReturningSpec<I, Q> {

    }


    interface _SingleWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_ReturningSpec<I, Q>, _SingleWhereAndSpec<I, Q>>
            , _WhereCurrentOfClause<_ReturningSpec<I, Q>> {

    }


    interface _RepeatableOnClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_OnClause<_SingleJoinSpec<I, Q>>>
            , _OnClause<_SingleJoinSpec<I, Q>> {

    }

    interface _TableSampleOnSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_RepeatableOnClause<I, Q>>
            , _OnClause<_SingleJoinSpec<I, Q>> {

    }


    interface _SingleJoinSpec<I extends Item, Q extends Item>
            extends _PostgreJoinClause<_TableSampleOnSpec<I, Q>, _OnClause<_SingleJoinSpec<I, Q>>>
            , _PostgreCrossJoinClause<_TableSampleJoinSpec<I, Q>, _SingleJoinSpec<I, Q>>
            , _JoinNestedClause<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_SingleJoinSpec<I, Q>>>
            , _PostgreDynamicJoinClause<_SingleJoinSpec<I, Q>>
            , _PostgreDynamicCrossJoinClause<_SingleJoinSpec<I, Q>>
            , _SingleWhereClause<I, Q> {

        //TODO add dialect function tabular
    }

    interface _RepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q> {

    }


    interface _TableSampleJoinSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_RepeatableJoinClause<I, Q>>, _SingleJoinSpec<I, Q> {

    }


    interface _SingleUsingSpec<I extends Item, Q extends Item>
            extends _PostgreUsingClause<_TableSampleJoinSpec<I, Q>, _SingleJoinSpec<I, Q>>
            , _UsingNestedClause<PostgreQuery._NestedLeftParenSpec<_SingleJoinSpec<I, Q>>>
            , _SingleWhereClause<I, Q> {
        //TODO add dialect function tabular
    }


    interface _SingleDeleteClause<I extends Item, Q extends Item>
            extends _PostgreDeleteClause<_SingleUsingSpec<I, Q>> {

    }


    interface _SingleMinWithSpec<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreDynamicWithClause<_SingleDeleteClause<I, Q>>
            , _SingleDeleteClause<I, Q> {

    }


    interface _CteComma<I extends Item, Q extends Item>
            extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I, Q>>>
            , _SingleDeleteClause<I, Q> {

    }

    interface _SingleWithSpec<I extends Item, Q extends Item> extends _SingleMinWithSpec<I, Q>
            , _StaticWithClause<PostgreQuery._StaticCteLeftParenSpec<_CteComma<I, Q>>> {

    }

    interface _StaticSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_SingleDeleteClause<I, I>>
            , _SingleDeleteClause<I, I> {

    }

    interface _DynamicSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_SingleMinWithSpec<I, I>>
            , _SingleMinWithSpec<I, I> {

    }


    interface _DynamicCteDeleteSpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_AsCteClause<PostgreCteBuilder>>> {

    }

    /*-------------------below batch syntax -------------------*/


    interface _BatchStaticReturningCommaSpec<Q extends Item>
            extends _StaticReturningCommaClause<_BatchStaticReturningCommaSpec<Q>>
            , _BatchParamClause<_DqlDeleteSpec<Q>> {

    }

    interface _BatchReturningSpec<I extends Item, Q extends Item>
            extends _StaticReturningClause<_BatchStaticReturningCommaSpec<Q>>
            , _DynamicReturningClause<_BatchParamClause<_DqlDeleteSpec<Q>>>
            , _BatchParamClause<_DmlDeleteSpec<I>> {

    }


    interface _BatchSingleWhereAndSpec<I extends Item, Q extends Item>
            extends Update._UpdateWhereAndClause<_BatchSingleWhereAndSpec<I, Q>>
            , _BatchReturningSpec<I, Q> {

    }


    interface _BatchSingleWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_BatchReturningSpec<I, Q>, _BatchSingleWhereAndSpec<I, Q>>
            , _WhereCurrentOfClause<_BatchReturningSpec<I, Q>> {


    }


    interface _BatchRepeatableOnClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>
            , _OnClause<_BatchSingleJoinSpec<I, Q>> {

    }

    interface _BatchTableSampleOnSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_BatchRepeatableOnClause<I, Q>>
            , _OnClause<_BatchSingleJoinSpec<I, Q>> {

    }


    interface _BatchSingleJoinSpec<I extends Item, Q extends Item>
            extends _PostgreJoinClause<_BatchTableSampleOnSpec<I, Q>, _OnClause<_BatchSingleJoinSpec<I, Q>>>
            , _PostgreCrossJoinClause<_BatchTableSampleJoinSpec<I, Q>, _BatchSingleJoinSpec<I, Q>>
            , _JoinNestedClause<_NestedLeftParenSpec<_OnClause<_BatchSingleJoinSpec<I, Q>>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_BatchSingleJoinSpec<I, Q>>>
            , _PostgreDynamicJoinClause<_BatchSingleJoinSpec<I, Q>>
            , _PostgreDynamicCrossJoinClause<_BatchSingleJoinSpec<I, Q>>
            , _BatchSingleWhereClause<I, Q> {

        //TODO add dialect function tabular
    }

    interface _BatchRepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_BatchSingleJoinSpec<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }


    interface _BatchTableSampleJoinSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_BatchRepeatableJoinClause<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }


    interface _BatchSingleUsingSpec<I extends Item, Q extends Item>
            extends _PostgreUsingClause<_BatchTableSampleJoinSpec<I, Q>, _BatchSingleJoinSpec<I, Q>>
            , _UsingNestedClause<_NestedLeftParenSpec<_BatchSingleJoinSpec<I, Q>>>
            , _BatchSingleWhereClause<I, Q> {
        //TODO add dialect function tabular
    }


    interface _BatchSingleDeleteClause<I extends Item, Q extends Item>
            extends _PostgreDeleteClause<_BatchSingleUsingSpec<I, Q>> {

    }


    interface _BatchSingleMinWithSpec<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreDynamicWithClause<_BatchSingleDeleteClause<I, Q>>
            , _BatchSingleDeleteClause<I, Q> {

    }


    interface _BatchCteComma<I extends Item, Q extends Item>
            extends _StaticWithCommaClause<PostgreQuery._StaticCteLeftParenSpec<_BatchCteComma<I, Q>>>
            , _BatchSingleDeleteClause<I, Q> {

    }

    interface _BatchSingleWithSpec<I extends Item, Q extends Item> extends _BatchSingleMinWithSpec<I, Q>
            , _StaticWithClause<PostgreQuery._StaticCteLeftParenSpec<_BatchCteComma<I, Q>>> {

    }


}
