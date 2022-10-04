package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.impl.Postgres;


/**
 * <p>
 * This interface representing postgre SELECT statement.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
 * @since 1.0
 */
public interface PostgreQuery extends Query, DialectStatement {

    interface _PostgreDynamicWithClause<C, SR>
            extends DialectStatement._DynamicWithCteClause<C, PostgreCteBuilder, SR> {

    }


    interface _SelectCommaSpec<C, Q extends Item> {

    }


    interface _SelectSpec<C, Q extends Item> extends _SelectClause<C, Postgres.Modifier, _SelectCommaSpec<C, Q>> {

    }

    interface _CteComma<C, Q extends Item> extends _StaticWithCommaClause<_StaticCteLeftParenSpec<C, _CteComma<C, Q>>>
            , DialectStatement._StaticSpaceClause<_SelectSpec<C, Q>>, Item {

    }


    /**
     * <p>
     * static sub-statement syntax forbid the WITH clause ,because it destroy the Readability of code.
     * </p>
     *
     * @since 1.0
     */
    interface _StaticCteComplexCommandSpec<C, Q extends Item> extends _SelectSpec<C, Q> {

    }

    interface _StaticCteAsClause<C, Q extends Item>
            extends Statement._StaticAsClaus<_StaticCteComplexCommandSpec<C, Q>> {

    }

    interface _StaticCteLeftParenSpec<C, Q extends Item>
            extends Statement._LeftParenStringQuadraSpec<C, _StaticCteAsClause<C, Q>>
            , _StaticCteAsClause<C, Q> {

    }


    /**
     * <p>
     * primary-statement syntax support static WITH clause,it's simple and clear and free
     * </p>
     *
     * @since 1.0
     */
    interface _WithCteSpec<C, Q extends Item> extends _PostgreDynamicWithClause<C, _SelectSpec<C, Q>>
            , DialectStatement._StaticWithCteClause<_StaticCteLeftParenSpec<C, _CteComma<C, Q>>>
            , _SelectSpec<C, Q> {

    }


    /**
     * <p>
     * sub-statement syntax forbid static WITH syntax,because it destroy the simpleness of SQL.
     * </p>
     *
     * @since 1.0
     */
    interface _SubWithCteSpec<C, Q extends Item> extends _PostgreDynamicWithClause<C, _SelectSpec<C, Q>>
            , _SelectSpec<C, Q> {

    }


}
