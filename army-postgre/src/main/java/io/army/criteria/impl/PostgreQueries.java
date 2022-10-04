package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.postgre.PostgreQuery;
import io.army.lang.Nullable;

import java.util.function.Function;

abstract class PostgreQueries {


    static <C> PostgreQuery._WithCteSpec<C, Select> primaryQuery(@Nullable C criteria) {
        throw new UnsupportedOperationException();
    }

    static <C, Q extends Item> PostgreQuery._SubWithCteSpec<C, Q> subQuery(@Nullable C criteria
            , CriteriaContext outerContext, Function<SubQuery, Q> function) {
        return null;
    }


    private PostgreQueries(CriteriaContext context) {


    }


}
