package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SubQuery;
import io.army.criteria.Update;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractContextualUpdate<C> extends AbstractSQL implements InnerUpdate
        , Update.UpdateAble, Update.WhereAble<C>, Update.WhereAndAble<C> {

    static final String NOT_PREPARED_MSG = "update criteria don't haven invoke asUpdate() method.";

    final C criteria;

    private final CriteriaContext criteriaContext;

    private List<FieldMeta<?, ?>> targetFieldList = new ArrayList<>();

    private List<Expression<?>> valueExpList = new ArrayList<>();

    private List<IPredicate> predicateList = new ArrayList<>();

    private boolean prepared;

    AbstractContextualUpdate(C criteria) {
        Assert.notNull(criteria, "criteria required");
        this.criteria = criteria;

        this.criteriaContext = new AbstractSelect.CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow SetAble method ##################################*/

    @Override
    public final <F> WhereAble<C> set(FieldMeta<? extends IDomain, F> target, F value) {
        this.targetFieldList.add(target);
        this.valueExpList.add(SQLS.param(value, target.mappingType()));
        return this;
    }

    @Override
    public final <F> WhereAble<C> set(FieldMeta<? extends IDomain, F> target, Expression<F> valueExp) {
        this.targetFieldList.add(target);
        this.valueExpList.add(valueExp);
        return this;
    }

    @Override
    public final <F> WhereAble<C> set(FieldMeta<? extends IDomain, F> target, Function<C, Expression<?>> function) {
        this.targetFieldList.add(target);
        this.valueExpList.add(function.apply(this.criteria));
        return this;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public final <F> WhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, F value) {
        if (predicate.test(this.criteria)) {
            set(target, value);
        }
        return this;
    }

    @Override
    public final <F> WhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
            , Expression<F> valueExp) {
        if (predicate.test(this.criteria)) {
            set(target, valueExp);
        }
        return this;
    }

    @Override
    public final <F> WhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
            , Function<C, Expression<?>> valueExpFunction) {
        if (predicate.test(this.criteria)) {
            set(target, valueExpFunction);
        }
        return this;
    }

    @Override
    public final Update where(List<IPredicate> predicateList) {
        Assert.state(this.predicateList.isEmpty(), "where clause ended.");
        this.predicateList.addAll(predicateList);
        return asUpdate();
    }

    @Override
    public final Update where(Function<C, List<IPredicate>> function) {
        Assert.state(this.predicateList.isEmpty(), "where clause ended.");
        this.predicateList.addAll(function.apply(this.criteria));
        return asUpdate();
    }

    @Override
    public final WhereAndAble<C> where(IPredicate predicate) {
        Assert.state(this.predicateList.isEmpty(), "where clause ended.");
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public final WhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow UpdateAble method ##################################*/

    @Override
    public final Update asUpdate() {
        if (this.prepared) {
            return this;
        }
        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

        this.asSQL();
        this.targetFieldList = Collections.unmodifiableList(this.targetFieldList);
        this.valueExpList = Collections.unmodifiableList(this.valueExpList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        doAsUpdate();
        this.prepared = true;
        return this;
    }

    /*################################## blow InnerUpdate method ##################################*/

    @Override
    public final List<FieldMeta<?, ?>> targetFieldList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.targetFieldList;
    }

    @Override
    public final List<Expression<?>> valueExpList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.valueExpList;
    }

    @Override
    public final List<IPredicate> predicateList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.predicateList;
    }

    @Override
    public final void clear() {
        super.beforeClear(NOT_PREPARED_MSG);

        this.targetFieldList = null;
        this.valueExpList = null;
        this.predicateList = null;

        this.doClear();
    }

    /*################################## blow package method ##################################*/

    @Override
    final boolean prepared() {
        return this.prepared;
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    void doClear() {

    }

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }


    /*################################## blow package template method ##################################*/

    abstract void doAsUpdate();

}
