package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.inner.InnerStandardDomainUpdate;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class StandardContextualDomainUpdate<T extends IDomain, C> extends AbstractSQLDebug implements
        InnerStandardDomainUpdate, Update, Update.UpdateAble, Update.DomainWhereAble<T, C>
        , Update.WhereAndAble<T, C>, Update.DomainUpdateAble<T, C> {


    private final TableMeta<?> tableMeta;

    private final Object primaryKeyValue;

    private final C criteria;

    private final CriteriaContext criteriaContext;

    private String tableAlias;

    private List<FieldMeta<?, ?>> targetFieldList = new ArrayList<>();

    private List<Expression<?>> valueExpList = new ArrayList<>();

    private List<IPredicate> predicateList = new ArrayList<>();

    private boolean prepared;

    StandardContextualDomainUpdate(TableMeta<?> tableMeta, Object primaryKeyValue, C criteria) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(criteria, "criteria required");

        Assert.isInstanceOf(tableMeta.primaryKey().javaType(), primaryKeyValue);

        this.tableMeta = tableMeta;
        this.primaryKeyValue = primaryKeyValue;
        this.criteria = criteria;

        this.criteriaContext = new AbstractStandardSelect.CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    /*################################## blow DomainUpdateAble method ##################################*/

    @Override
    public final DomainSetAble<T, C> update(TableMeta<T> tableMeta, String tableAlias) {
        Assert.isTrue(this.tableMeta == tableMeta, "tableMeta not match.");
        Assert.hasText(tableAlias, "tableAlias required");
        this.tableAlias = tableAlias;
        return this;
    }

    /*################################## blow DomainSetAble method ##################################*/

    @Override
    public final <F> DomainWhereAble<T, C> set(FieldMeta<? super T, F> target, F value) {
        this.targetFieldList.add(target);
        this.valueExpList.add(SQLS.param(value));
        return this;
    }

    @Override
    public final <F> DomainWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp) {
        this.targetFieldList.add(target);
        this.valueExpList.add(valueExp);
        return this;
    }

    @Override
    public final <F> DomainWhereAble<T, C> set(FieldMeta<? super T, F> target, Function<C, Expression<F>> function) {
        this.targetFieldList.add(target);
        this.valueExpList.add(function.apply(this.criteria));
        return this;
    }

    /*################################## blow DomainWhereAble method ##################################*/

    @Override
    public final <F> DomainWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value) {
        if (predicate.test(this.criteria)) {
            set(target, value);
        }
        return this;
    }

    @Override
    public final <F> DomainWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target
            , Expression<F> valueExp) {
        if (predicate.test(this.criteria)) {
            set(target, valueExp);
        }
        return this;
    }

    @Override
    public final <F> DomainWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target
            , Function<C, Expression<F>> valueExpFunction) {
        if (predicate.test(this.criteria)) {
            set(target, valueExpFunction);
        }
        return this;
    }

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

    /*################################## blow private method ##################################*/

    @Override
    public final Update asUpdate() {
        if (this.prepared) {
            return this;
        }
        Assert.hasText(this.tableAlias, "tableAlias has no text,state error.");

        CriteriaContextHolder.clearContext(this.criteriaContext);
        this.criteriaContext.clear();

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
    public final Object primaryKeyValue() {
        return this.primaryKeyValue;
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
        Assert.state(this.prepared, "Update not invoke asUpdate() method.");

        this.targetFieldList = null;
        this.valueExpList = null;
        this.predicateList = null;
    }
}

