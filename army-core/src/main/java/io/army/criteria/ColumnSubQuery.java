package io.army.criteria;


public interface ColumnSubQuery<E> extends SubQuery {


    interface StandardColumnSubQuerySpec<C, E>
            extends StandardQuery.StandardSelectClauseSpec<C, ColumnSubQuery<E>> {

    }




}
