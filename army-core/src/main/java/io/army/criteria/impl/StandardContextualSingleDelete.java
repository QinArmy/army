package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner.InnerStandardDomainDelete;
import io.army.criteria.impl.inner.InnerStandardSingleDelete;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

class StandardContextualSingleDelete<C> extends AbstractSQLDebug implements Delete
        , Delete.SingleDeleteAble<C>, Delete.SingleDeleteWhereAble<C>, Delete.SingleDeleteWhereAndAble<C>
        , InnerStandardSingleDelete {

    static <C> StandardContextualSingleDelete<C> buildDelete(C criteria) {
        return new StandardContextualSingleDelete<>(criteria);
    }

    static <C> StandardContextualSingleDelete<C> buildDomainDelete(Object primaryKeyValue, C criteria) {
        return new StandardContextualDomainDelete<>(primaryKeyValue, criteria);
    }

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> tableMeta;

    private List<IPredicate> predicateList = new ArrayList<>();

    private boolean prepared;

    private StandardContextualSingleDelete(C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new AbstractSQL.CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(criteriaContext);
    }

    /*################################## blow SingleDeleteAble method ##################################*/

    @Override
    public final SingleDeleteWhereAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta) {
        this.tableMeta = tableMeta;
        return this;
    }

    /*################################## blow SingleWhereAble method ##################################*/

    @Override
    public final DeleteAble where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final DeleteAble where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final SingleDeleteWhereAndAble<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow private method ##################################*/

    @Override
    public final SingleDeleteWhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final SingleDeleteWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final SingleDeleteWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow DeleteAble method ##################################*/

    @Override
    public final Delete asDelete() {
        if (this.prepared) {
            return this;
        }

        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

        Assert.state(this.tableMeta != null, "Delete must specify TableMeta.");
        Assert.state(!CollectionUtils.isEmpty(this.predicateList), "Delete must have where clause.");
        assertPrimaryKeyValue(this.tableMeta);

        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardSingleDelete method ##################################*/

    @Override
    public final TableMeta<?> tableMeta() {
        Assert.state(this.prepared, "Criteria state error.");
        return this.tableMeta;
    }

    @Override
    public final List<IPredicate> predicateList() {
        return this.predicateList;
    }

    void assertPrimaryKeyValue(TableMeta<?> tableMeta) {

    }

    /*################################## blow static inner class ##################################*/

    private static final class StandardContextualDomainDelete<C> extends StandardContextualSingleDelete<C>
            implements InnerStandardDomainDelete {

        private final Object primaryKeyValue;

        private StandardContextualDomainDelete(Object primaryKeyValue, C criteria) {
            super(criteria);
            Assert.notNull(primaryKeyValue, "primaryKeyValue required");
            this.primaryKeyValue = primaryKeyValue;
        }

        @Override
        public final Object primaryKeyValue() {
            return this.primaryKeyValue;
        }

        @Override
        void assertPrimaryKeyValue(TableMeta<?> tableMeta) {
            Assert.isInstanceOf(tableMeta.primaryKey().javaType(), this.primaryKeyValue);
        }


    }
}
