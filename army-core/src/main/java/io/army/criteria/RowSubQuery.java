package io.army.criteria;

public interface RowSubQuery extends SubQuery, SetValueItem {


    interface StandardRowSubQuerySpec<C> extends StandardQuery.StandardSelectSpec<C, RowSubQuery> {


    }


}
