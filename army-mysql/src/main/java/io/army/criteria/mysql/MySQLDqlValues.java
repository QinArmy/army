package io.army.criteria.mysql;

import io.army.criteria.*;

/**
 * <p>
 * This interface representing MySQL Values statement,this interface is base interface of below:
 *     <ul>
 *         <li>MySQL {@link  io.army.criteria.Values}</li>
 *         <li>MySQL {@link io.army.criteria.SubValues}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface MySQLDqlValues extends DialectStatement, RowSet.DqlValues {


    interface _UnionSpec<C, U extends DqlValues>
            extends Query._UnionClause<C, _UnionOrderBySpec<C, U>>, Values._ValuesSpec<U> {

    }

    interface _UnionLimitSpec<C, U extends DqlValues> extends Statement._RowCountLimitClause<C, _UnionSpec<C, U>>
            , _UnionSpec<C, U> {

    }

    interface _UnionOrderBySpec<C, U extends DqlValues> extends Statement._OrderByClause<C, _UnionLimitSpec<C, U>>
            , _UnionLimitSpec<C, U> {

    }


    interface _LimitSpec<C, U extends DqlValues>
            extends Statement._RowCountLimitClause<C, _UnionSpec<C, U>>, _UnionSpec<C, U> {
    }


    interface _OrderBySpec<C, U extends DqlValues>
            extends Statement._OrderByClause<C, _LimitSpec<C, U>>, _LimitSpec<C, U> {

    }


    interface _StaticRowClause<C, U extends DqlValues> {

        Values._StaticValueLeftParenClause<_StaticRowSpec<C, U>> row();

    }

    interface _StaticRowSpec<C, U extends DqlValues> extends _StaticRowClause<C, U>, _OrderBySpec<C, U> {

    }


    interface _ValuesStmtValuesClause<C, U extends DqlValues>
            extends Values._ValuesDynamicClause<C, _OrderBySpec<C, U>> {

        _StaticRowClause<C, U> values();


    }


}
