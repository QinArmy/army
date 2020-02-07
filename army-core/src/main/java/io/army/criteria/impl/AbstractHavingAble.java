package io.army.criteria.impl;

import io.army.criteria.HavingAble;
import io.army.criteria.OrderAble;
import io.army.criteria.IPredicate;
import io.army.util.ArrayUtils;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2019-01-31.
 */
abstract class AbstractHavingAble extends AbstractOrderAble implements HavingAble {

    private List<IPredicate> havingIPredicateList = Collections.emptyList();


    @NonNull
    @Override
    public final OrderAble having(IPredicate... IPredicates) {
        havingIPredicateList = ArrayUtils.asUnmodifiableList(IPredicates);
        return this;
    }
}
