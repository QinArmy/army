package io.army.criteria.impl;

import io.army.criteria.Query;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.mysql.MySQL57Query;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;

abstract class MySQLPartQuery<C, Q extends MySQLQuery> extends PartQuery<C, Q
        , MySQL57Query.Union57Spec<Q, C> // U
        , MySQL57Query.MySQLSelectPartSpec<C, MySQL57Query.Into57Spec<Q, C>> // S
        , MySQL57Query.OrderBy57Spec<Q, C> // O
        , MySQL57Query.Limit57Spec<Q, C> // L
        >
        implements _PartQuery, Query, Query.QuerySpec<Q> {

    public MySQLPartQuery(@Nullable C criteria) {
        super(criteria);
    }


}
