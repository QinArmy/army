package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.postgre.PostgreQuery;
import io.army.lang.Nullable;

import java.util.function.Function;

abstract class PostgreQueries {


    static <C, Q extends Item> PostgreQuery._WithCteSpec<C, Q> primaryQuery(@Nullable C criteria
            , Function<Select, Q> function) {
        throw new UnsupportedOperationException();
    }

    static <C, Q extends Item> PostgreQuery._SubWithCteSpec<C, Q> subQuery(@Nullable C criteria
            , CriteriaContext outerContext, Function<SubQuery, Q> function) {
        return null;
    }


    private PostgreQueries(CriteriaContext context) {


    }


}
