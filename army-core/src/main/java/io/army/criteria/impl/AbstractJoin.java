package io.army.criteria.impl;

import io.army.criteria.CriteriaContext;
import io.army.criteria.Join;
import io.army.criteria.Predicate;
import org.springframework.lang.NonNull;

/**
 * created  on 2018-12-24.
 */
abstract class AbstractJoin<X> extends AbstractWhereAble implements InnerJoin<X> {


    protected final InnerSelectList selectList;

    private String joinAbleAlias = "";

    AbstractJoin(InnerSelectList selectList) {
        this.selectList = selectList;
    }

    @NonNull
    @Override
    public CriteriaContext getCriteriaContext() {
        return selectList.getCriteriaContext();
    }

    @Override
    public Join<X> as(@NonNull String alias) {
        if (alias != null) {
            this.joinAbleAlias = alias;
        }
        return this;
    }

    @Override
    public <Y> Join<Y> join(Class<Y> tableClass) {
        return null;
    }


    @Override
    public Join<X> on(Predicate... predicate) {
        return null;
    }

    /*####################### sub class method start #################################*/


    protected abstract String alias();


}
