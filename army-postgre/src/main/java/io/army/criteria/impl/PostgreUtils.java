package io.army.criteria.impl;

import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._ParensRowSet;
import io.army.criteria.impl.inner._RowSet;

abstract class PostgreUtils extends CriteriaUtils {

    private PostgreUtils() {
    }


    static boolean isUnionQuery(final SubQuery query) {
        _RowSet rowSet = (_RowSet) query;
        while (rowSet instanceof _ParensRowSet) {
            rowSet = ((_ParensRowSet) rowSet).innerRowSet();
        }
        return rowSet instanceof SimpleQueries.UnionSubQuery;
    }


}
