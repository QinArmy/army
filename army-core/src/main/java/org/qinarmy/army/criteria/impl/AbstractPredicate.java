package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.Predicate;

/**
 * created  on 2018/11/25.
 */
class AbstractPredicate extends AbstractExpression<Boolean> implements Predicate {


    @Override
    public Predicate and(Predicate predicate) {
        return null;
    }

    @Override
    public Predicate or(Predicate... predicates) {
        return null;
    }
}
