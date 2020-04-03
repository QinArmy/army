package io.army.criteria.impl;

import io.army.criteria.SQLModifier;
import io.army.criteria.SubQuery;
import io.army.criteria.inner.postgre.PostgreInnerSelect;
import io.army.criteria.postgre.PostgreSelect;
import io.army.criteria.postgre.PostgreWithQuery;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class PostgreContextualMultiSelect<C> extends AbstractPostgreMultiSelect<C>
        implements PostgreSelect.PostgreWithAble<C>, PostgreInnerSelect {

    private final CriteriaContext criteriaContext;

    private SQLModifier recursive;

    private List<PostgreWithQuery> withQueryList;

    PostgreContextualMultiSelect(C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow PostgreWithAble method ##################################*/

    @Override
    public PostgreSelectPartAble<C> with(Function<C, List<PostgreWithQuery>> function) {
        List<PostgreWithQuery> list = function.apply(this.criteria);
        if (this.withQueryList == null) {
            this.withQueryList = new ArrayList<>(list.size());
        }
        this.withQueryList.addAll(list);
        return this;
    }

    @Override
    public PostgreSelectPartAble<C> withRecursive(Function<C, List<PostgreWithQuery>> function) {
        with(function);
        this.recursive = PostgreModifier.RECURSIVE;
        return this;
    }

    /*################################## blow PostgreInnerSelect method ##################################*/

    @Override
    public final SQLModifier recursive() {
        return this.recursive;
    }

    @Override
    public final List<PostgreWithQuery> withQueryList() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.withQueryList;
    }


    /*################################## blow InnerSQL method ##################################*/

    @Override
    public void clear() {
        super.clear();

        if (this.withQueryList != null) {
            this.withQueryList = null;
        }
    }

    /*################################## blow package method implementation ##################################*/

    @Override
    void doAsSelect() {
        if (this.withQueryList == null) {
            this.withQueryList = Collections.emptyList();
        } else {
            this.withQueryList = Collections.unmodifiableList(this.withQueryList);
        }
    }

    @Override
    final void beforeAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);
    }

    /*################################## blow static inner  class ##################################*/


}
