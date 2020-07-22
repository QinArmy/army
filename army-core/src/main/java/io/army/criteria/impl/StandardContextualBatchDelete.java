package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner.InnerBatchDML;
import io.army.criteria.impl.inner.InnerStandardDelete;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

final class StandardContextualBatchDelete<C> implements Delete,
        Delete.BatchDeleteAble<C>, Delete.BatchWhereAble<C>, Delete.BatchWhereAndAble<C>
        , Delete.BatchNamedParamAble<C>, Delete.DeleteAble, InnerStandardDelete, InnerBatchDML {

    static <C> StandardContextualBatchDelete<C> build(C criteria) {
        return new StandardContextualBatchDelete<>(criteria);
    }

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> tableMeta;

    private String tableAlias;

    private List<IPredicate> predicateList = new ArrayList<>();

    private List<Object> namedParamList = new ArrayList<>();

    private boolean prepared;

    private StandardContextualBatchDelete(C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow BatchDeleteAble method ##################################*/

    @Override
    public BatchWhereAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias) {
        Assert.hasText(this.tableAlias, "tableAlias required");
        this.tableMeta = tableMeta;
        this.tableAlias = tableAlias;
        return this;
    }

    /*################################## blow BatchWhereAble method ##################################*/

    @Override
    public BatchNamedParamAble<C> where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public BatchNamedParamAble<C> where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public BatchWhereAndAble<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow BatchWhereAndAble method ##################################*/

    @Override
    public BatchWhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public BatchWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public BatchWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow BatchNamedParamAble method ##################################*/

    @Override
    public DeleteAble namedParamMaps(Collection<Map<String, Object>> mapCollection) {
        this.namedParamList.addAll(mapCollection);
        return this;
    }

    @Override
    public DeleteAble namedParamMaps(Function<C, Collection<Map<String, Object>>> function) {
        this.namedParamList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public DeleteAble namedParamBeans(Collection<Object> beanCollection) {
        this.namedParamList.addAll(beanCollection);
        return this;
    }

    @Override
    public DeleteAble namedParamBeans(Function<C, Collection<Object>> function) {
        this.namedParamList.addAll(function.apply(this.criteria));
        return this;
    }

    /*################################## blow DeleteAble method ##################################*/

    @Override
    public Delete asDelete() {
        if (this.prepared) {
            return this;
        }

        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.state(this.tableMeta != null, "TableMeta must not null");
        Assert.state(StringUtils.hasText(this.tableAlias), "Table alias must have text.");
        Assert.state(!this.predicateList.isEmpty(), "delete no where clause.");
        Assert.state(!this.namedParamList.isEmpty(), "delete no named params");

        this.predicateList = Collections.unmodifiableList(this.predicateList);
        this.namedParamList = Collections.unmodifiableList(this.namedParamList);
        this.prepared = true;
        return this;
    }

    /*################################## blow Delete method ##################################*/

    @Override
    public boolean prepared() {
        return this.prepared;
    }

    /*################################## blow InnerStandardBatchDelete method ##################################*/

    @Override
    public List<Object> namedParamList() {
        return this.namedParamList;
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public List<IPredicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public void clear() {
        this.tableMeta = null;
        this.tableAlias = null;
        this.predicateList = null;
        this.namedParamList = null;

        this.prepared = false;
    }
}
