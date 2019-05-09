package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.GroupAble;
import org.qinarmy.army.criteria.Predicate;
import org.qinarmy.army.criteria.WhereAble;
import org.qinarmy.army.util.ArrayUtils;
import org.qinarmy.army.util.Assert;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2018-12-24.
 */
abstract class AbstractWhereAble extends AbstractGroupAble implements WhereAble {

    private List<Predicate> predicateList = Collections.emptyList();


    @NonNull
    @Override
    public GroupAble where(Predicate... predicates) {
        this.predicateList = ArrayUtils.asUnmodifiableList(predicates);
        return this;
    }

    @NonNull
    @Override
    public GroupAble where(@NonNull List<Predicate> predicateList) {
        Assert.assertNotNull(predicateList, "predicateList required");
        this.predicateList = Collections.unmodifiableList(predicateList);
        return this;
    }


}
