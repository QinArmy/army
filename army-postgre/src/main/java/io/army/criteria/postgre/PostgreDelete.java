package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

public interface PostgreDelete extends DialectStatement {


    interface _PostgreUsingClause<FT, FS> extends _UsingModifierClause<FT, FS>
            , _UsingCteClause<FS> {

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
            extends PostgreQuery._RepeatableClause<_OnClause<_JoinSpec<I, Q>>>
            , _OnClause<_JoinSpec<I, Q>> {

    }

    interface _TableSampleOnSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_RepeatableOnClause<I, Q>>
            , _OnClause<_JoinSpec<I, Q>> {

    }


    interface _JoinSpec<I extends Item, Q extends Item>
            extends _JoinModifierClause<_TableSampleOnSpec<I, Q>, _OnClause<_JoinSpec<I, Q>>>
            , _CrossJoinModifierClause<_TableSampleJoinSpec<I, Q>, _JoinSpec<I, Q>>
            , _JoinCteClause<_OnClause<_JoinSpec<I, Q>>>, _CrossJoinCteClause<_JoinSpec<I, Q>>
            , _SingleWhereClause<I, Q> {

        //TODO add dialect function tabular

    }

    interface _RepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_JoinSpec<I, Q>>, _JoinSpec<I, Q> {

    }


    interface _TableSampleJoinSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_RepeatableJoinClause<I, Q>>, _JoinSpec<I, Q> {

    }


    interface _SingleFromClause<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreFromClause<_TableSampleJoinSpec<I, Q>, _JoinSpec<I, Q>> {
        //TODO add dialect function tabular
    }


    interface _SingleUsingSpec<I extends Item, Q extends Item, T>
            extends _PostgreUsingClause<_TableSampleJoinSpec<I, Q>, _JoinSpec<I, Q>>
            , _UsingNestedClause<_JoinSpec<I, Q>> {


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
            extends Query._StaticWithCommaClause<PostgreQuery._StaticCteLeftParenSpec<_CteComma<I, Q>>>
            , _SingleUpdateClause<I, Q> {

    }

    interface _SingleWithSpec<I extends Item, Q extends Item> extends _SingleMinWithSpec<I, Q>
            , Query._StaticWithClause<PostgreQuery._StaticCteLeftParenSpec<_CteComma<I, Q>>> {

    }

    /*-------------------below batch syntax -------------------*/


    interface _BatchStaticReturningCommaSpec<Q extends Item>
            extends _StaticReturningCommaClause<_StaticReturningCommaSpec<Q>>
            , _DqlUpdateSpec<Q> {

    }

    interface _BatchReturningSpec<I extends Item, Q extends Item>
            extends _StaticReturningClause<_BatchStaticReturningCommaSpec<Q>>
            , _DynamicReturningClause<_DqlUpdateSpec<Q>>
            , _DmlUpdateSpec<I> {

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
            extends PostgreQuery._RepeatableClause<_OnClause<_BatchJoinSpec<I, Q>>>
            , _OnClause<_BatchJoinSpec<I, Q>> {

    }

    interface _BatchTableSampleOnSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_BatchRepeatableOnClause<I, Q>>
            , _OnClause<_BatchJoinSpec<I, Q>> {

    }


    interface _BatchJoinSpec<I extends Item, Q extends Item>
            extends _JoinModifierClause<_BatchTableSampleOnSpec<I, Q>, _OnClause<_BatchJoinSpec<I, Q>>>
            , _CrossJoinModifierClause<_BatchTableSampleJoinSpec<I, Q>, _BatchJoinSpec<I, Q>>
            , _JoinCteClause<_OnClause<_BatchJoinSpec<I, Q>>>, _CrossJoinCteClause<_BatchJoinSpec<I, Q>>
            , _BatchSingleWhereClause<I, Q> {

        //TODO add dialect function tabular

    }

    interface _BatchRepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_BatchJoinSpec<I, Q>>, _BatchJoinSpec<I, Q> {

    }


    interface _BatchTableSampleJoinSpec<I extends Item, Q extends Item>
            extends PostgreQuery._TableSampleClause<_BatchRepeatableJoinClause<I, Q>>, _BatchJoinSpec<I, Q> {

    }


    interface _BatchSingleFromClause<I extends Item, Q extends Item>
            extends PostgreQuery._PostgreFromClause<_BatchTableSampleJoinSpec<I, Q>, _BatchJoinSpec<I, Q>> {
        //TODO add dialect function tabular
    }

    interface _BatchSingleSetClause<I extends Item, Q extends Item, T>
            extends Update._StaticRowSetClause<FieldMeta<T>, _BatchSingleFromSpec<I, Q, T>>
            , Update._DynamicSetClause<RowPairs<FieldMeta<T>>, _BatchSingleFromClause<I, Q>> {

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
            extends Query._StaticWithCommaClause<PostgreQuery._StaticCteLeftParenSpec<_BatchCteComma<I, Q>>>
            , _BatchSingleUpdateClause<I, Q> {

    }

    interface _BatchSingleWithSpec<I extends Item, Q extends Item> extends _BatchSingleMinWithSpec<I, Q>
            , Query._StaticWithClause<PostgreQuery._StaticCteLeftParenSpec<_BatchCteComma<I, Q>>> {

    }


}
