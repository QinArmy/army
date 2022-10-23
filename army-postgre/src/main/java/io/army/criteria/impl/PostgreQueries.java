package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SubQuery;
import io.army.criteria.postgre.PostgreQuery;

import java.util.function.Function;

abstract class PostgreQueries {


    static <Q extends Item> PostgreQuery._WithSpec<Q> primaryQuery() {
        throw new UnsupportedOperationException();
    }

    static <Q extends Item> PostgreQuery._SubWithCteSpec<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        return null;
    }


    private PostgreQueries(CriteriaContext context) {


    }


}
