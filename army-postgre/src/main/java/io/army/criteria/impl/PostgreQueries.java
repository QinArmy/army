package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SubQuery;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreStatement;

import java.util.function.Function;

abstract class PostgreQueries implements PostgreStatement {


    static <Q extends Item> PostgreQuery._WithSpec<Q> primaryQuery() {
        throw new UnsupportedOperationException();
    }

    static <Q extends Item> PostgreQuery._WithSpec<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> Function<String, _StaticCteLeftParenSpec<I>> complexCte(CriteriaContext outerContext
            , I comma) {
        throw new UnsupportedOperationException();

    }

    static <I extends Item> PostgreQuery._DynamicSubMaterializedSpec<I> dynamicCteQuery(CriteriaContext outerContext
            , Function<SubQuery, I> function) {
        throw new UnsupportedOperationException();
    }


    private PostgreQueries(CriteriaContext context) {


    }


}
