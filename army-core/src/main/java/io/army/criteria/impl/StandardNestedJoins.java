package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.NestedItems;
import io.army.criteria.standard.StandardStatement;

import java.util.function.BiFunction;

abstract class StandardNestedJoins {

    static <I extends Item> StandardStatement._NestedLeftParenSpec<I> nestedItem(CriteriaContext context
            , _JoinType joinType, BiFunction<_JoinType, NestedItems, I> function) {
        throw new UnsupportedOperationException();
    }

}
