package io.army.criteria.impl;

import io.army.criteria.Query;
import io.army.criteria.mysql.MySQL80Query;

abstract class MySQL80UnionQuery implements MySQL80Query {

    static <C, Q extends Query> UnionOrderBy80Spec<C, Q> bracketQuery(Q query) {
        return null;
    }

    static <C, Q extends Query> UnionOrderBy80Spec<C, Q> unionQuery(Q left, UnionType unionType, Q right) {
        return null;
    }

}
