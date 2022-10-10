package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.impl.Postgres;

import java.util.function.BooleanSupplier;


/**
 * <p>
 * This interface representing postgre SELECT statement.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
 * @since 1.0
 */
public interface PostgreQuery extends Query, DialectStatement {

    interface _PostgreDynamicWithClause<SR>
            extends _DynamicWithCteClause<PostgreCteBuilder, SR> {

    }


    interface _LockSpec<I extends Item> {

    }

    interface _OffsetSpec<I extends Item> {

    }

    interface _LimitSpec<I extends Item> extends _RowCountLimitClause<_OffsetSpec<I>> {

        _OffsetSpec<I> limitAll();

        _OffsetSpec<I> ifLimitAll(BooleanSupplier supplier);

    }


    interface _OrderBySpec<I extends Item> extends _OrderByClause<_LimitSpec<I>>, _LimitSpec<I> {

    }


    interface _HavingSpec<I extends Item> extends _HavingClause<_OrderBySpec<I>>, _OrderBySpec<I> {

    }


    interface _GroupBySpec<I extends Item> extends _GroupClause<_HavingSpec<I>>
            , _OrderBySpec<I> {

    }


    interface _WhereSpec<I extends Item> {

    }


    interface _TableSampleOnSpec<I extends Item> {

    }

    interface _TabularOnColumnAlias<I extends Item> {

    }

    interface _TableSampleJoinSpec<I extends Item> {

    }

    interface _TabularJoinColumnAlias<I extends Item> {

    }


    interface _JoinSpec<I extends Item> extends _JoinModifierClause<_TableSampleOnSpec<I>, _TabularOnColumnAlias<I>>
            , _CrossJoinModifierClause<_TableSampleJoinSpec<I>, _TabularJoinColumnAlias<I>>
            , _JoinCteClause<_TabularOnColumnAlias<I>>, _CrossJoinCteClause<_TabularJoinColumnAlias<I>>
            , _WhereSpec<I> {

    }


    interface _PostgreFromClause<FT, FS> extends _FromModifierClause<FT, FS>
            , _FromModifierCteClause<FS> {

    }


    interface _FromSpec<I extends Item> extends _PostgreFromClause<_TableSampleJoinSpec<I>, _TabularJoinColumnAlias<I>> {

        //TODO function Tabular item
    }


    interface _PostgreSelectClause<I extends Item> extends _SelectClause<_FromSpec<I>>
            , _DynamicModifierSelectClause<Postgres.SelectModifier, _FromSpec<I>> {


    }

    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreSelectClause<I> {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<I extends Item> extends _PostgreSelectClause<I> {

    }

    interface _StaticCteAsClause<I extends Item>
            extends Statement._StaticAsClaus<_StaticCteComplexCommandSpec<I>> {

    }

    interface _StaticCteLeftParenSpec<I extends Item>
            extends Statement._LeftParenStringQuadraSpec<_StaticCteAsClause<I>>
            , _StaticCteAsClause<I> {

    }


    /**
     * <p>
     * primary-statement syntax support static WITH clause,it's simple and clear and free
     * </p>
     *
     * @since 1.0
     */
    interface _WithCteSpec<I extends Item> extends _PostgreDynamicWithClause<_PostgreSelectClause<I>>
            , _StaticWithCteClause<_StaticCteLeftParenSpec<_CteComma<I>>>
            , _PostgreSelectClause<I> {

    }


    /**
     * <p>
     * sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     *
     * @since 1.0
     */
    interface _SubWithCteSpec<I extends Item> extends _PostgreDynamicWithClause<_PostgreSelectClause<I>>
            , _PostgreSelectClause<I> {

    }


}
