package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadWrapper;
import io.army.criteria.Delete;
import io.army.criteria.Statement;
import io.army.criteria.WithElement;
import io.army.criteria.impl.inner._BatchSingleDelete;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class BatchSingleDelete<C, WR, WA, BR> extends DmlWhereClause<C, WR, WA>
        implements Delete, Delete.DeleteSpec, Statement.BatchParamClause<C, BR>, _BatchSingleDelete {

    final CriteriaContext criteriaContext;

    private List<ReadWrapper> paramList = new ArrayList<>();

    BatchSingleDelete(@Nullable C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        if (this instanceof WithElement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }


    @Override
    public final BR paramMaps(List<Map<String, Object>> mapList) {
        if (mapList.size() == 0) {
            throw _Exceptions.batchParamEmpty();
        }
        final List<ReadWrapper> namedParamList = this.paramList;
        for (Map<String, Object> map : mapList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return (BR) this;
    }

    @Override
    public final BR paramMaps(Supplier<List<Map<String, Object>>> supplier) {
        return this.paramMaps(supplier.get());
    }

    @Override
    public final BR paramMaps(Function<C, List<Map<String, Object>>> function) {
        return this.paramMaps(function.apply(this.criteria));
    }

    @Override
    public final BR paramBeans(List<Object> beanList) {
        if (beanList.size() == 0) {
            throw _Exceptions.batchParamEmpty();
        }
        final List<ReadWrapper> namedParamList = this.paramList;
        for (Object bean : beanList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return (BR) this;
    }

    @Override
    public final BR paramBeans(Supplier<List<Object>> supplier) {
        return this.paramBeans(supplier.get());
    }

    @Override
    public final BR paramBeans(Function<C, List<Object>> function) {
        return this.paramBeans(function.apply(this.criteria));
    }

    @Override
    public final List<ReadWrapper> wrapperList() {
        _Assert.prepared(this.prepared);
        return this.paramList;
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
        this.predicateList = CollectionUtils.asUnmodifiableList(predicates);

        final List<ReadWrapper> paramList = this.paramList;
        if (CollectionUtils.isEmpty(paramList)) {
            throw _Exceptions.batchParamEmpty();
        }
        this.paramList = CollectionUtils.unmodifiableList(paramList);

        this.onAsDelete();

        this.prepared = true;
        return this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;
        this.predicateList = null;
        this.paramList = null;
    }

    void onAsDelete() {

    }


}
