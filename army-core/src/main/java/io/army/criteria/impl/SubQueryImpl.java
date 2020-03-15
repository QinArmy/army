package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSubQueryAble;

final class SubQueryImpl<C> extends AbstractSelect<C> implements
        InnerSubQueryAble, SelfDescribed, TableAble, SubQuery {

    private final Select outerQuery;

    SubQueryImpl(C criteria, Select outerQuery) {
        super(criteria);
        this.outerQuery = outerQuery;
    }

    /*################################## blow SubQuery method ##################################*/

    @Override
    public final Select outerQuery() {
        return this.outerQuery;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.dml().subQuery(this);
    }


}
