package io.army.criteria;


public interface SubQuery extends DerivedTable, Query {

    interface StandardSubQuerySpec<C> extends StandardSelectClauseSpec<C, SubQuery> {


    }

}
