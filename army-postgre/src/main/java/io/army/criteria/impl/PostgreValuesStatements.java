package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SubStatement;
import io.army.criteria.postgre.PostgreValues;

import java.util.function.Function;

abstract class PostgreValuesStatements {

    static <I extends Item> PostgreValues._DynamicSubMaterializedSpec<I> dynamicCteValues(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }
}
