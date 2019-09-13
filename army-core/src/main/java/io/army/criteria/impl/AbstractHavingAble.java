package io.army.criteria.impl;

import io.army.criteria.HavingAble;
import io.army.criteria.OrderAble;
import io.army.criteria.Predicate;
import io.army.util.ArrayUtils;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2019-01-31.
 */
abstract class AbstractHavingAble extends AbstractOrderAble implements HavingAble {

    private List<Predicate> havingPredicateList = Collections.emptyList();


    @NonNull
    @Override
    public final OrderAble having(Predicate... predicates) {
        havingPredicateList = ArrayUtils.asUnmodifiableList(predicates);
        return this;
    }
}
