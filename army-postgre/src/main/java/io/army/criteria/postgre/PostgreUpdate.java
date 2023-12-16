package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.UpdateStatement;
import io.army.criteria.impl.SQLs;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing Postgre UPDATE syntax.
 * * @see <a href="https://www.postgresql.org/docs/current/sql-update.html">Postgre UPDATE syntax</a>
 *
 * @since 0.6.0
 */
public interface PostgreUpdate extends PostgreStatement {


    interface _StaticReturningCommaSpec<Q extends Item>
            extends _StaticDmlReturningCommaClause<_StaticReturningCommaSpec<Q>>,
            _DqlUpdateSpec<Q> {

    }

    interface _ReturningSpec<I extends Item, Q extends Item>
            extends _StaticDmlReturningClause<_StaticReturningCommaSpec<Q>>,
            _DynamicReturningClause<_DqlUpdateSpec<Q>>,
            _DmlUpdateSpec<I> {

    }


    interface _SingleWhereAndSpec<I extends Item, Q extends Item>
            extends UpdateStatement._UpdateWhereAndClause<_SingleWhereAndSpec<I, Q>>, _ReturningSpec<I, Q> {

    }


    interface _SingleWhereClause<I extends Item, Q extends Item>
            extends _WhereClause<_ReturningSpec<I, Q>, _SingleWhereAndSpec<I, Q>>,
            _WhereCurrentOfClause<_ReturningSpec<I, Q>> {

    }


    interface _RepeatableOnClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>> {

    }

    interface _TableSampleOnSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_RepeatableOnClause<I, Q>>, _OnClause<_SingleJoinSpec<I, Q>> {

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

    interface _RepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q> {

    }


    interface _TableSampleJoinSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_RepeatableJoinClause<I, Q>>, _SingleJoinSpec<I, Q> {

    }


    interface _SingleSetClause<I extends Item, Q extends Item, T>
            extends UpdateStatement._StaticBatchRowSetClause<FieldMeta<T>, _SingleSetFromSpec<I, Q, T>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._BatchRowPairs<FieldMeta<T>>, _SingleFromSpec<I, Q>> {

    }

    interface _ParensJoinSpec<I extends Item, Q extends Item> extends _OptionalParensStringClause<_SingleJoinSpec<I, Q>>,
            _SingleJoinSpec<I, Q> {

    }


    interface _SingleFromSpec<I extends Item, Q extends Item>
            extends _PostgreFromClause<_TableSampleJoinSpec<I, Q>, _ParensJoinSpec<I, Q>>,
            _PostgreFromUndoneFuncClause<_SingleJoinSpec<I, Q>>,
            _FromCteClause<_SingleJoinSpec<I, Q>>,
            _PostgreFromNestedClause<_SingleJoinSpec<I, Q>>,
            _SingleWhereClause<I, Q> {

    }

    interface _SingleSetFromSpec<I extends Item, Q extends Item, T> extends _SingleFromSpec<I, Q>,
            _SingleSetClause<I, Q, T> {

    }


    interface _SingleUpdateClause<I extends Item, Q extends Item> extends Item {

        <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _SingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table, SQLs.WordAs as,
                                             String tableAlias);

        <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);


    }


    interface _SingleWithSpec<I extends Item, Q extends Item>
            extends _PostgreDynamicWithClause<_SingleUpdateClause<I, Q>>,
            PostgreQuery._PostgreStaticWithClause<_SingleUpdateClause<I, Q>>,
            _SingleUpdateClause<I, Q> {

    }


    interface _UpdateDynamicCteAsClause
            extends _PostgreDynamicCteAsClause<_SingleWithSpec<_CommaClause<PostgreCtes>, _CommaClause<PostgreCtes>>,
            _CommaClause<PostgreCtes>> {

    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_UpdateDynamicCteAsClause>, _UpdateDynamicCteAsClause {

    }



}
