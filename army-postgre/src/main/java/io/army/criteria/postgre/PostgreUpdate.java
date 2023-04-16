package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.UpdateStatement;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.function.Function;

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
            _PostgreCrossClause<_TableSampleJoinSpec<I, Q>, _ParensJoinSpec<I, Q>>,
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
            extends UpdateStatement._StaticRowSetClause<FieldMeta<T>, _SingleSetFromSpec<I, Q, T>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._RowPairs<FieldMeta<T>>, _SingleFromSpec<I, Q>> {

    }

    interface _ParensJoinSpec<I extends Item, Q extends Item> extends _OptionalParensStringClause<_SingleJoinSpec<I, Q>>,
            _SingleJoinSpec<I, Q> {

    }


    interface _SingleFromSpec<I extends Item, Q extends Item>
            extends _PostgreFromClause<_TableSampleJoinSpec<I, Q>, _ParensJoinSpec<I, Q>>,
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

        <T> _SingleSetClause<I, Q, T> update(TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

        <T> _SingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

    }


    interface _SingleWithSpec<I extends Item, Q extends Item>
            extends _PostgreDynamicWithClause<_SingleUpdateClause<I, Q>>,
            PostgreQuery._PostgreStaticWithClause<_SingleUpdateClause<I, Q>>,
            _SingleUpdateClause<I, Q> {

    }


    interface _DynamicCteAsClause {

        DialectStatement._CommaClause<PostgreCtes> as(Function<_SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function);

        DialectStatement._CommaClause<PostgreCtes> as(@Nullable Postgres.WordMaterialized modifier,
                                                      Function<_SingleWithSpec<DialectStatement._CommaClause<PostgreCtes>, DialectStatement._CommaClause<PostgreCtes>>, DialectStatement._CommaClause<PostgreCtes>> function);

    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_DynamicCteAsClause>, _DynamicCteAsClause {

    }


    /*-------------------below batch syntax -------------------*/


    interface _BatchStaticReturningCommaSpec<Q extends Item>
            extends _StaticDmlReturningCommaClause<_BatchStaticReturningCommaSpec<Q>>,
            _BatchParamClause<_DqlUpdateSpec<Q>> {

    }

    interface _BatchReturningSpec<I extends Item, Q extends Item>
            extends _StaticDmlReturningClause<_BatchStaticReturningCommaSpec<Q>>,
            _DynamicReturningClause<_BatchParamClause<_DqlUpdateSpec<Q>>>,
            _BatchParamClause<_DmlUpdateSpec<I>> {

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
            _PostgreCrossClause<_BatchTableSampleJoinSpec<I, Q>, _BatchParensJoinSpec<I, Q>>,
            _JoinCteClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>,
            _CrossJoinCteClause<_BatchSingleJoinSpec<I, Q>>,
            _PostgreJoinNestedClause<_OnClause<_BatchSingleJoinSpec<I, Q>>>,
            _PostgreCrossNestedClause<_BatchSingleJoinSpec<I, Q>>,
            _PostgreDynamicJoinCrossClause<_BatchSingleJoinSpec<I, Q>>,
            _BatchSingleWhereClause<I, Q> {

    }

    interface _BatchRepeatableJoinClause<I extends Item, Q extends Item>
            extends PostgreQuery._RepeatableClause<_BatchSingleJoinSpec<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }


    interface _BatchTableSampleJoinSpec<I extends Item, Q extends Item>
            extends _StaticTableSampleClause<_BatchRepeatableJoinClause<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }

    interface _BatchParensJoinSpec<I extends Item, Q extends Item>
            extends _OptionalParensStringClause<_BatchSingleJoinSpec<I, Q>>, _BatchSingleJoinSpec<I, Q> {

    }


    interface _BatchSingleFromClause<I extends Item, Q extends Item>
            extends _PostgreFromClause<_BatchTableSampleJoinSpec<I, Q>, _BatchParensJoinSpec<I, Q>>,
            _PostgreFromNestedClause<_BatchSingleJoinSpec<I, Q>>,
            _BatchSingleWhereClause<I, Q> {

    }

    interface _BatchSingleSetClause<I extends Item, Q extends Item, T>
            extends UpdateStatement._StaticRowSetClause<FieldMeta<T>, _BatchSingleSetFromSpec<I, Q, T>>,
            UpdateStatement._StaticBatchSetClause<FieldMeta<T>, _BatchSingleSetFromSpec<I, Q, T>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._BatchRowPairs<FieldMeta<T>>, _BatchSingleFromClause<I, Q>> {

    }


    interface _BatchSingleSetFromSpec<I extends Item, Q extends Item, T> extends _BatchSingleFromClause<I, Q>,
            _BatchSingleSetClause<I, Q, T> {

    }

    interface _BatchSingleUpdateClause<I extends Item, Q extends Item> extends Item {

        <T> _BatchSingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _BatchSingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table, SQLs.WordAs as,
                                                  String tableAlias);

        <T> _BatchSingleSetClause<I, Q, T> update(TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

        <T> _BatchSingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<?> table, @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as, String tableAlias);

    }


    interface _BatchSingleWithSpec<I extends Item, Q extends Item>
            extends _PostgreDynamicWithClause<_BatchSingleUpdateClause<I, Q>>,
            PostgreQuery._PostgreStaticWithClause<_BatchSingleUpdateClause<I, Q>>,
            _BatchSingleUpdateClause<I, Q> {

    }


}
