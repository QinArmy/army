package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class StandardContextualUpdate<T extends IDomain, C> extends AbstractSQLDebug implements
        Update, Update.UpdateAble, Update.SingleWhereAble<T, C>, Update.SingleUpdateTableRouteAble<T, C>
        , Update.WhereAndAble<T, C>, Update.SingleUpdateAble<T, C>, InnerStandardUpdate {

    static <T extends IDomain, C> StandardContextualUpdate<T, C> build(TableMeta<T> tableMeta, C criteria) {
        Assert.isTrue(!tableMeta.immutable(), () -> String.format("TableMeta[%s] immutable", tableMeta));
        return new StandardContextualUpdate<>(tableMeta, criteria);
    }

    private final TableMeta<T> tableMeta;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private String tableAlias;

    private List<FieldMeta<?, ?>> targetFieldList = new ArrayList<>();

    private List<Expression<?>> valueExpList = new ArrayList<>();

    private List<IPredicate> predicateList = new ArrayList<>();

    private int databaseIndex = -1;

    private int tableIndex = -1;

    private boolean prepared;

    private StandardContextualUpdate(TableMeta<T> tableMeta, C criteria) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(criteria, "criteria required");

        this.tableMeta = tableMeta;
        this.criteria = criteria;

        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow DomainUpdateAble method ##################################*/

    @Override
    public final SingleUpdateTableRouteAble<T, C> update(TableMeta<T> tableMeta, String tableAlias) {
        Assert.isTrue(this.tableMeta == tableMeta, "tableMeta not match.");
        Assert.hasText(tableAlias, "tableAlias required");
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public final SingleSetAble<T, C> route(int databaseIndex, int tableIndex) {
        this.databaseIndex = databaseIndex;
        this.tableIndex = tableIndex;
        return this;
    }

    @Override
    public final SingleSetAble<T, C> route(int tableIndex) {
        this.tableIndex = tableIndex;
        return this;
    }

    /*################################## blow DomainSetAble method ##################################*/

    @Override
    public final <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, F value) {
        this.targetFieldList.add(target);
        this.valueExpList.add(SQLS.paramWithExp(value, target));
        return this;
    }

    @Override
    public final <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp) {
        this.targetFieldList.add(target);
        this.valueExpList.add(valueExp);
        return this;
    }

    @Override
    public final <F> SingleWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value) {
        if (predicate.test(this.criteria)) {
            set(target, value);
        }
        return this;
    }

    @Override
    public final <F> SingleWhereAble<T, C> nonNullSet(FieldMeta<? super T, F> target
            , Function<C, Expression<F>> function) {
        Expression<F> expression = function.apply(this.criteria);
        if (expression != null) {
            set(target, expression);
        }
        return this;
    }

    /*################################## blow DomainWhereAble method ##################################*/


    @Override
    public final UpdateAble where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final UpdateAble where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final WhereAndAble<T, C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public final WhereAndAble<T, C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final WhereAndAble<T, C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final WhereAndAble<T, C> ifAnd(Function<C, IPredicate> function) {
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

    /*################################## blow UpdateAble method ##################################*/

    @Override
    public final Update asUpdate() {
        if (this.prepared) {
            return this;
        }
        CriteriaContextHolder.clearContext(this.criteriaContext);

        Assert.state(!this.targetFieldList.isEmpty(), "update no set clause.");
        Assert.state(this.valueExpList.size() == this.targetFieldList.size()
                , "update target field and value exp size not match");
        Assert.state(!this.predicateList.isEmpty(), "update no where clause.");
        Assert.state(this.tableAlias != null, "no tableAlias");

        this.targetFieldList = Collections.unmodifiableList(this.targetFieldList);
        this.valueExpList = Collections.unmodifiableList(this.valueExpList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.prepared = true;
        return this;
    }

    /*################################## blow InnerStandardDomainUpdate method ##################################*/

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
    public final List<FieldMeta<?, ?>> targetFieldList() {
        return this.targetFieldList;
    }

    @Override
    public final List<Expression<?>> valueExpList() {
        return this.valueExpList;
    }

    @Override
    public final void clear() {

        this.targetFieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
        this.prepared = false;
    }
}

