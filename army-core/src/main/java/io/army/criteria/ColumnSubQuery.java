package io.army.criteria;


/**
 * @see ScalarSubQuery
 */
public interface ColumnSubQuery extends SubQuery {


    interface StandardColumnSubQuerySpec<C>
            extends StandardQuery.StandardSelectClauseSpec<C, ColumnSubQuery> {

    }


}
