package io.army.criteria.impl;

import io.army.criteria.CriteriaContext;
import io.army.criteria.SubQuery;
import org.springframework.lang.NonNull;

/**
 * created  on 2019-01-30.
 */
class SubQueryJoin<X> extends AbstractJoin<X> implements InnerJoin<X> {

    private final InnerSelectList selectList;

    private final SubQuery<X> subQuery;


    public SubQueryJoin(InnerSelectList selectList, InnerSelectList selectList1, SubQuery<X> subQuery) {
        super(selectList);
        this.selectList = selectList1;
        this.subQuery = subQuery;
    }

    @Override
    protected String alias() {
        return null;
    }

    @NonNull
    @Override
    public CriteriaContext getCriteriaContext() {
        return selectList.getCriteriaContext();
    }


}
