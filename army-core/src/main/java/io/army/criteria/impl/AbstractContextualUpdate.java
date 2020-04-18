package io.army.criteria.impl;

import io.army.criteria.*;
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

abstract class AbstractContextualUpdate<T extends IDomain, C> extends AbstractSQL implements InnerUpdate
        , Update.UpdateAble, Update.WhereAble<T, C>, Update.WhereAndAble<T, C> {

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

        this.criteriaContext = new AbstractStandardSelect.CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow SetAble method ##################################*/

    @Override
    public final <F> WhereAble<T, C> set(FieldMeta<T, F> target, F value) {
        this.targetFieldList.add(target);
        this.valueExpList.add(SQLS.param(value));
        return this;
    }

    @Override
    public final <F> WhereAble<T, C> set(FieldMeta<T, F> target, Expression<F> valueExp) {
        this.targetFieldList.add(target);
        this.valueExpList.add(valueExp);
        return this;
    }

    @Override
    public final <F> WhereAble<T, C> set(FieldMeta<T, F> target, Function<C, Expression<F>> function) {
        this.targetFieldList.add(target);
        this.valueExpList.add(function.apply(this.criteria));
        return this;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public final <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<T, F> target, F value) {
        if (predicate.test(this.criteria)) {
            set(target, value);
        }
        return this;
    }

    @Override
    public final <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<T, F> target, Expression<F> valueExp) {
        if (predicate.test(this.criteria)) {
            set(target, valueExp);
        }
        return this;
    }

    @Override
    public final <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<T, F> target
            , Function<C, Expression<F>> valueExpFunction) {
        if (predicate.test(this.criteria)) {
            set(target, valueExpFunction);
        }
        return this;
    }

    @Override
    public final Update.UpdateAble where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final Update.UpdateAble where(Function<C, List<IPredicate>> function) {
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
    public final WhereAndAble<T, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final WhereAndAble<T, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
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
        Assert.notEmpty(this.predicateList, "update statement must have where.");

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
    public List<SQLModifier> modifierList() {
        return Collections.emptyList();
    }

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
