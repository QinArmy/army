package io.army.criteria.impl;

import io.army.criteria.Predicate;
import io.army.criteria.SubQuery;

/**
 * created  on 2018/11/25.
 */
abstract class AbstractPredicate extends AbstractExpression<Boolean> implements Predicate {



    @Override
    public SubQuery getSubQuery() {
        return null;
    }


    @Override
    public Predicate and(Predicate predicate) {
        return null;
    }

    @Override
    public Predicate or(Predicate... predicates) {
        return null;
    }
}
