package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.WithElement;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.List;

abstract class SingleDelete<C, WR, WA> extends DmlWhereClause<C, WR, WA>
        implements Delete, Delete.DeleteSpec, _SingleDelete {

    final CriteriaContext criteriaContext;

    SingleDelete(@Nullable C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        if (this instanceof WithElement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }


    @Override
    public final Delete asDelete() {
        _Assert.nonPrepared(this.prepared);

        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }

        final List<_Predicate> predicates = this.predicateList;

        if (CollectionUtils.isEmpty(predicates)) {
            throw _Exceptions.dmlNoWhereClause();
        }

        this.predicateList = CollectionUtils.unmodifiableList(predicates);

        this.onAsDelete();
        this.prepared = true;
        return this;
    }


    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);

        this.predicateList = null;
        this.onClear();

        this.prepared = false;
    }

    void onAsDelete() {

    }


}
