package io.army.criteria.impl;

import io.army.criteria.GroupAble;
import io.army.criteria.IPredicate;
import io.army.criteria.WhereAble;
import io.army.util.ArrayUtils;
import io.army.util.Assert;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2018-12-24.
 */
abstract class AbstractWhereAble extends AbstractGroupAble implements WhereAble {

    private List<IPredicate> IPredicateList = Collections.emptyList();


    @NonNull
    @Override
    public GroupAble where(IPredicate... IPredicates) {
        this.IPredicateList = ArrayUtils.asUnmodifiableList(IPredicates);
        return this;
    }

    @NonNull
    @Override
    public GroupAble where(@NonNull List<IPredicate> IPredicateList) {
        Assert.assertNotNull(IPredicateList, "predicateList required");
        this.IPredicateList = Collections.unmodifiableList(IPredicateList);
        return this;
    }


}
