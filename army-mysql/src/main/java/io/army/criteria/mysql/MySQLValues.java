package io.army.criteria.mysql;

import io.army.criteria.Item;
import io.army.criteria.RowSet;
import io.army.criteria.Statement;
import io.army.criteria.Values;

/**
 * <p>
 * This interface representing MySQL Values statement,this interface is base interface of below:
 *     <ul>
 *         <li>MySQL {@link  io.army.criteria.Values}</li>
 *         <li>MySQL {@link io.army.criteria.SubValues}</li>
 *     </ul>
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/values.html">VALUES Statement</a>
 * @since 1.0
 */
public interface MySQLValues extends MySQLStatement, RowSet {


    interface _UnionSpec<I extends Item>
            extends _StaticUnionClause<_ValueWithComplexSpec<I>>, _AsValuesClause<I> {

    }

    interface _UnionLimitSpec<I extends Item> extends Statement._RowCountLimitClause<_AsValuesClause<I>>,
            _AsValuesClause<I> {

    }

    interface _UnionOrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I> {

    }

    interface _UnionOrderBySpec<I extends Item> extends _OrderByClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _LimitSpec<I extends Item> extends Statement._RowCountLimitClause<_AsValuesClause<I>>,
            _AsValuesClause<I> {
    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>,
            _LimitSpec<I> {

    }

    interface _OrderBySpec<I extends Item> extends _OrderByClause<_OrderByCommaSpec<I>>,
            _LimitSpec<I>,
            _UnionSpec<I> {

    }


    interface _ValuesLeftParenClause<I extends Item>
            extends Values._StaticValueLeftParenClause<_ValuesLeftParenSpec<I>> {

    }

    interface _ValuesLeftParenSpec<I extends Item> extends _ValuesLeftParenClause<I>,
            _OrderBySpec<I> {

    }

    interface _MySQLValuesClause<I extends Item>
            extends Values._StaticValuesClause<_ValuesLeftParenClause<I>>,
            Values._DynamicValuesClause<_OrderBySpec<I>> {

    }


    interface _ValueSpec<I extends Item> extends _MySQLValuesClause<I>,
            _DynamicParensRowSetClause<_ValueSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    /**
     * <p>
     * VALUES statement don't support WITH clause.
     * </p>
     */
    interface _SelectComplexCommandSpec<I extends Item> extends MySQLQuery._MySQLSelectClause<I>,
            _DynamicParensRowSetClause<_ValueWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    interface _ValueWithComplexSpec<I extends Item>
            extends _MySQLDynamicWithClause<_SelectComplexCommandSpec<I>>,
            _MySQLStaticWithClause<_SelectComplexCommandSpec<I>>,
            _SelectComplexCommandSpec<I>,
            _MySQLValuesClause<I> {

    }


}
