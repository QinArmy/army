package io.army.criteria.postgre;

import io.army.criteria.BatchRowPairs;
import io.army.criteria.Item;
import io.army.criteria.RowPairs;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing Postgre UPDATE syntax.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-update.html">Postgre UPDATE syntax</a>
 * @since 1.0
 */
public interface PostgreUpdate extends PostgreStatement {


    interface _StaticReturningCommaSpec<Q extends Item>
            extends _StaticReturningCommaClause<_StaticReturningCommaSpec<Q>>
            , _DqlUpdateSpec<Q> {

    }

    interface _ReturningSpec<I extends Item, Q extends Item>
            extends _StaticReturningClause<_StaticReturningCommaSpec<Q>>
            , _DynamicReturningClause<_DqlUpdateSpec<Q>>
            , _DmlUpdateSpec<I> {

    }


    interface _SingleWhereAndSpec<I extends Item, Q extends Item>
            extends Update._UpdateWhereAndClause<_SingleWhereAndSpec<I, Q>>
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


    interface _SingleSetClause<I extends Item, Q extends Item, T>
            extends Update._StaticRowSetClause<FieldMeta<T>, _SingleFromSpec<I, Q, T>>
            , Update._DynamicSetClause<RowPairs<FieldMeta<T>>, _SingleFromSpec<I, Q, T>> {

    }


    interface _SingleFromSpec<I extends Item, Q extends Item, T>
            extends _PostgreFromClause<_TableSampleJoinSpec<I, Q>, _SingleJoinSpec<I, Q>>
            , _FromNestedClause<_NestedLeftParenSpec<_SingleJoinSpec<I, Q>>>
            , _SingleWhereClause<I, Q>
            , _SingleSetClause<I, Q, T> {
        //TODO add dialect function tabular

    }

    interface _SingleUpdateClause<I extends Item, Q extends Item> {

        <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias);

        <T> _SingleSetClause<I, Q, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias);

    }


    interface _SingleMinWithSpec<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreDynamicWithClause<_SingleUpdateClause<I, Q>>
            , _SingleUpdateClause<I, Q> {

    }


    interface _CteComma<I extends Item, Q extends Item>
            extends _StaticWithCommaClause<PostgreQuery._StaticCteLeftParenSpec<_CteComma<I, Q>>>
            , _SingleUpdateClause<I, Q> {

    }

    interface _SingleWithSpec<I extends Item, Q extends Item> extends _SingleMinWithSpec<I, Q>
            , _StaticWithClause<PostgreQuery._StaticCteLeftParenSpec<_CteComma<I, Q>>> {

    }

    interface _StaticSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_SingleUpdateClause<I, I>>
            , _SingleUpdateClause<I, I> {

    }


    interface _DynamicSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_SingleMinWithSpec<I, I>>
            , _SingleMinWithSpec<I, I> {

    }


    interface _DynamicCteUpdateSpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_AsCteClause<PostgreCteBuilder>>> {

    }

    /*-------------------below batch syntax -------------------*/


    interface _BatchStaticReturningCommaSpec<Q extends Item>
            extends _StaticReturningCommaClause<_BatchStaticReturningCommaSpec<Q>>
            , _BatchParamClause<_DqlUpdateSpec<Q>> {

    }

    interface _BatchReturningSpec<I extends Item, Q extends Item>
            extends _StaticReturningClause<_BatchStaticReturningCommaSpec<Q>>
            , _DynamicReturningClause<_BatchParamClause<_DqlUpdateSpec<Q>>>
            , _BatchParamClause<_DmlUpdateSpec<I>> {

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


    interface _BatchSingleFromClause<I extends Item, Q extends Item>
            extends _PostgreFromClause<_BatchTableSampleJoinSpec<I, Q>, _BatchSingleJoinSpec<I, Q>>
            , _FromNestedClause<PostgreQuery._NestedLeftParenSpec<_BatchSingleJoinSpec<I, Q>>>
            , _BatchSingleWhereClause<I, Q> {
        //TODO add dialect function tabular
    }

    interface _BatchSingleSetClause<I extends Item, Q extends Item, T>
            extends Update._StaticRowSetClause<FieldMeta<T>, _BatchSingleFromSpec<I, Q, T>>
            , Update._DynamicSetClause<BatchRowPairs<FieldMeta<T>>, _BatchSingleFromClause<I, Q>> {

    }


    interface _BatchSingleFromSpec<I extends Item, Q extends Item, T> extends _BatchSingleFromClause<I, Q>
            , _BatchSingleSetClause<I, Q, T> {


    }

    interface _BatchSingleUpdateClause<I extends Item, Q extends Item> {

        <T> _BatchSingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias);

        <T> _BatchSingleSetClause<I, Q, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias);

    }


    interface _BatchSingleMinWithSpec<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreDynamicWithClause<_BatchSingleUpdateClause<I, Q>>
            , _BatchSingleUpdateClause<I, Q> {

    }


    interface _BatchCteComma<I extends Item, Q extends Item>
            extends _StaticWithCommaClause<PostgreQuery._StaticCteLeftParenSpec<_BatchCteComma<I, Q>>>
            , _BatchSingleUpdateClause<I, Q> {

    }

    interface _BatchSingleWithSpec<I extends Item, Q extends Item> extends _BatchSingleMinWithSpec<I, Q>
            , _StaticWithClause<PostgreQuery._StaticCteLeftParenSpec<_BatchCteComma<I, Q>>> {

    }


}
