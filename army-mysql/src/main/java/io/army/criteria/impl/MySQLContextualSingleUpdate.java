package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerMySQLSingleUpdate;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class MySQLContextualSingleUpdate<C> extends StandardContextualSingleUpdate<C>
        implements InnerMySQLSingleUpdate, MySQLUpdate.MySQLSingleUpdateAble<C>
        , MySQLUpdate.MySQLSetAble<C>, MySQLUpdate.MySQLWhereAble<C>
        , MySQLUpdate.MySQLWhereAndAble<C>, MySQLUpdate.MySQLOrderByAble<C>
        , MySQLUpdate.MySQLLimitAble<C> {

    private List<SQLModifier> sqlModifierList = new ArrayList<>(2);

    private List<Expression<?>> sortExpList = new ArrayList<>(3);

    private int rowCount;

    MySQLContextualSingleUpdate(C criteria) {
        super(criteria);
    }

    /*################################## blow MySQLSingleUpdateAble method ##################################*/

    @Override
    public MySQLSetAble<C> update(TableMeta<?> tableMeta, String tableAlias) {
        super.update(tableMeta, tableAlias);
        return this;
    }

    @Override
    public MySQLSetAble<C> update(List<MySQLModifier> modifierList, TableMeta<?> tableMeta, String tableAlias) {
        this.sqlModifierList.addAll(modifierList);
        super.update(tableMeta, tableAlias);
        return this;
    }

    /*################################## blow MySQLSetAble method ##################################*/

    @Override
    public <F> MySQLWhereAble<C> set(FieldMeta<? extends IDomain, F> target, F value) {
        super.set(target, value);
        return this;
    }

    @Override
    public <F> MySQLWhereAble<C> set(FieldMeta<? extends IDomain, F> target, Expression<F> valueExp) {
        super.set(target, valueExp);
        return this;
    }

    @Override
    public <F> MySQLWhereAble<C> set(FieldMeta<? extends IDomain, F> target, Function<C, Expression<?>> function) {
        super.set(target, function);
        return this;
    }

    /*################################## blow MySQLWhereAble method ##################################*/

    @Override
    public <F> MySQLWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, F value) {
        super.ifSet(predicate, target, value);
        return this;
    }

    @Override
    public <F> MySQLWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
            , Expression<F> valueExp) {
        super.ifSet(predicate, target, valueExp);
        return this;
    }

    @Override
    public <F> MySQLWhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
            , Function<C, Expression<?>> valueExpFunction) {
        super.ifSet(predicate, target, valueExpFunction);
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> where(IPredicate predicate) {
        super.where(predicate);
        return this;
    }

    @Override
    public MySQLOrderByAble<C> where(List<IPredicate> predicateList) {
        super.where(predicateList);
        return this;
    }

    @Override
    public MySQLOrderByAble<C> where(Function<C, List<IPredicate>> function) {
        super.where(function);
        return this;
    }

    /*################################## blow MySQLWhereAndAble method ##################################*/

    @Override
    public MySQLWhereAndAble<C> and(IPredicate predicate) {
        super.and(predicate);
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        super.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        super.ifAnd(testPredicate, function);
        return this;
    }


    /*################################## blow MySQLOrderByAble method ##################################*/

    @Override
    public MySQLLimitAble<C> orderBy(Expression<?> orderExp) {
        this.sortExpList.add(orderExp);
        return this;
    }

    @Override
    public MySQLLimitAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        this.sortExpList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public MySQLLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp) {
        if (predicate.test(this.criteria)) {
            this.sortExpList.add(orderExp);
        }
        return this;
    }

    @Override
    public MySQLLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            this.sortExpList.addAll(expFunction.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow MySQLLimitAble method ##################################*/

    @Override
    public UpdateAble limit(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public UpdateAble limit(Function<C, Integer> function) {
        this.rowCount = function.apply(this.criteria);
        return this;
    }

    @Override
    public UpdateAble ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            this.rowCount = rowCount;
        }
        return this;
    }

    @Override
    public UpdateAble ifLimit(Predicate<C> predicate, Function<C, Integer> function) {
        if (predicate.test(this.criteria)) {
            this.rowCount = function.apply(this.criteria);
        }
        return this;
    }



    /*################################## blow InnerMySQLUpdate method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.sqlModifierList;
    }

    @Override
    public final List<Expression<?>> sortExpList() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.sortExpList;
    }

    @Override
    public final int rowCount() {
        Assert.state(prepared(), NOT_PREPARED_MSG);
        return this.rowCount;
    }

    /*################################## blow package  method ##################################*/

    @Override
    final void afterDoAsUpdate() {
        this.sqlModifierList = Collections.unmodifiableList(this.sqlModifierList);
        this.sortExpList = Collections.unmodifiableList(this.sortExpList);
    }

    @Override
    void doClear() {
        this.sqlModifierList = null;
        this.sortExpList = null;
    }
}
