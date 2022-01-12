package io.army.criteria;

public interface RowSubQuery extends SubQuery, SetValuePart {


    interface StandardRowSubQuerySpec<C> extends StandardQuery.StandardSelectSpec<C, RowSubQuery> {


    }


}
