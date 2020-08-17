package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner.InnerStandardBatchDelete;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

final class StandardContextualBatchDelete<C> implements Delete,
        Delete.BatchSingleDeleteSpec<C>, Delete.BatchSingleDeleteWhereSpec<C>, Delete.BatchSingleDeleteTableRouteSpec<C>
        , Delete.BatchSingleDeleteWhereAndSpec<C>, Delete.BatchSingleDeleteNamedParamSpec<C>, Delete.DeleteSpec, InnerStandardBatchDelete {

    static <C> StandardContextualBatchDelete<C> build(C criteria) {
        return new StandardContextualBatchDelete<>(criteria);
    }

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> tableMeta;

    private String tableAlias;

    private List<IPredicate> predicateList = new ArrayList<>();

    private List<ReadonlyWrapper> namedParamList = new ArrayList<>();

    private int databaseIndex = -1;

    private int tableIndex = -1;

    private boolean prepared;

    private StandardContextualBatchDelete(C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow BatchSingleDeleteSpec method ##################################*/

    @Override
    public final BatchSingleDeleteTableRouteSpec<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias) {
        Assert.hasText(this.tableAlias, "tableAlias required");
        this.tableMeta = tableMeta;
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public final BatchSingleDeleteWhereSpec<C> route(int databaseIndex, int tableIndex) {
        this.databaseIndex = databaseIndex;
        this.tableIndex = tableIndex;
        return this;
    }

    @Override
    public final BatchSingleDeleteWhereSpec<C> route(int tableIndex) {
        this.tableIndex = tableIndex;
        return this;
    }

    /*################################## blow BatchWhereSpec method ##################################*/

    @Override
    public final BatchSingleDeleteNamedParamSpec<C> where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final BatchSingleDeleteNamedParamSpec<C> where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final BatchSingleDeleteWhereAndSpec<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow BatchWhereAndSpec method ##################################*/

    @Override
    public final BatchSingleDeleteWhereAndSpec<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final BatchSingleDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final BatchSingleDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    /*################################## blow BatchNamedParamSpec method ##################################*/

    @Override
    public final DeleteSpec namedParamMaps(List<Map<String, Object>> mapList) {
        List<ReadonlyWrapper> namedParamList = this.namedParamList;
        for (Map<String, Object> map : mapList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(map));
        }
        return this;
    }

    @Override
    public final DeleteSpec namedParamMaps(Function<C, List<Map<String, Object>>> function) {
        return namedParamMaps(function.apply(this.criteria));
    }

    @Override
    public final DeleteSpec namedParamBeans(List<Object> beanList) {
        List<ReadonlyWrapper> namedParamList = this.namedParamList;
        for (Object bean : beanList) {
            namedParamList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
        }
        return this;
    }

    @Override
    public final DeleteSpec namedParamBeans(Function<C, List<Object>> function) {
        return namedParamBeans(function.apply(this.criteria));
    }

    /*################################## blow DeleteSpec method ##################################*/

    @Override
    public final Delete asDelete() {
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
    public final boolean prepared() {
        return this.prepared;
    }

    /*################################## blow InnerStandardBatchDelete method ##################################*/

    @Override
    public final List<ReadonlyWrapper> wrapperList() {
        return this.namedParamList;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final int databaseIndex() {
        return this.databaseIndex;
    }

    @Override
    public final int tableIndex() {
        return this.tableIndex;
    }

    @Override
    public final List<IPredicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public final void clear() {
        this.tableMeta = null;
        this.tableAlias = null;
        this.predicateList = null;
        this.namedParamList = null;

        this.prepared = false;
    }
}
