package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner.InnerStandardDelete;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

final class StandardContextualDelete<C> extends AbstractSQLDebug implements Delete
        , Delete.SingleDeleteAble<C>, Delete.SingleDeleteTableRouteAble<C>, Delete.SingleWhereAndAble<C>
        , InnerStandardDelete {

    static <C> StandardContextualDelete<C> buildDelete(C criteria) {
        return new StandardContextualDelete<>(criteria);
    }


    private final C criteria;

    private final CriteriaContext criteriaContext;

    private TableMeta<?> tableMeta;

    private String tableAlias;

    private List<IPredicate> predicateList = new ArrayList<>();

    private int databaseIndex = -1;

    private int tableIndex = -1;

    private boolean prepared;

    private StandardContextualDelete(C criteria) {
        this.criteria = criteria;
        this.criteriaContext = new CriteriaContextImpl<>(this.criteria);
        CriteriaContextHolder.setContext(criteriaContext);
    }

    /*################################## blow SingleDeleteAble method ##################################*/


    @Override
    public final SingleDeleteTableRouteAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias) {
        this.tableMeta = tableMeta;
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public final SingleDeleteTableRouteAble<C> route(int databaseIndex, int tableIndex) {
        this.databaseIndex = databaseIndex;
        this.tableIndex = tableIndex;
        return this;
    }

    @Override
    public final SingleDeleteTableRouteAble<C> route(int tableIndex) {
        this.tableIndex = tableIndex;
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
    public final SingleWhereAndAble<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow private method ##################################*/

    @Override
    public final SingleWhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final SingleWhereAndAble<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final SingleWhereAndAble<C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }

    /*################################## blow DeleteAble method ##################################*/

    @Override
    public final Delete asDelete() {
        if (this.prepared) {
            return this;
        }

        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.state(this.tableMeta != null, "Delete must specify TableMeta.");
        Assert.state(StringUtils.hasText(this.tableAlias), "Delete must specify table alias.");
        Assert.state(!CollectionUtils.isEmpty(this.predicateList), "Delete must have where clause.");

        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.prepared = true;
        return this;
    }

    @Override
    public final void clear() {
        this.tableMeta = null;
        this.tableAlias = null;
        this.predicateList = null;
        this.prepared = false;
    }

    /*################################## blow InnerStandardSingleDelete method ##################################*/

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
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
    public final String tableAlias() {
        return this.tableAlias;
    }


    /*################################## blow static inner class ##################################*/

}
