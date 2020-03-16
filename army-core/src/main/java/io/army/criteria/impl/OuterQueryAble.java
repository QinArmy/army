package io.army.criteria.impl;

import io.army.criteria.QueryAble;
import io.army.criteria.SubQuery;

interface OuterQueryAble extends SubQuery {

     void outerQuery(QueryAble outerQuery);
}
