package io.army.criteria;


public interface SubQuery extends DerivedTable, Query {

    interface StandardSubQuerySpec<C> extends StandardQuery.StandardSelectSpec<C, SubQuery> {


    }

}
