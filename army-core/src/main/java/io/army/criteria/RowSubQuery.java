package io.army.criteria;

public interface RowSubQuery extends SubQuery, SetRightItem {


    interface StandardRowSubQuerySpec<C> extends StandardQuery.StandardSelectSpec<C, RowSubQuery> {


    }


}
