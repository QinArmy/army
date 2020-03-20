package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerMySQLSingleDelete;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class MySQLContextualSingleDelete<C> extends StandardContextualSingleDelete<C>
        implements InnerMySQLSingleDelete, MySQLDelete.MySQLSingleDeleteAble<C>
        , MySQLDelete.MySQLNoJoinFromAble<C>, MySQLDelete.MySQLWhereAble<C>
        , MySQLDelete.MySQLWhereAndAble<C>, MySQLDelete.MySQLOrderByAble<C>, MySQLDelete.MySQLLimitAble<C> {

    private List<SQLModifier> sqlModifierList = new ArrayList<>(2);

    private List<Expression<?>> sortExpList = new ArrayList<>(3);

    private int rowCount;

    MySQLContextualSingleDelete(C criteria) {
        super(criteria);
    }

    /*################################## blow MySQLSingleDeleteAble method ##################################*/

    @Override
    public MySQLNoJoinFromAble<C> delete(List<MySQLModifier> modifierList) {
        this.sqlModifierList.addAll(modifierList);
        return this;
    }

    @Override
    public MySQLNoJoinFromAble<C> delete() {
        super.delete();
        return this;
    }

    /*################################## blow MySQLNoJoinFromAble method ##################################*/

    @Override
    public MySQLWhereAble<C> from(TableMeta<?> tableMeta) {
        super.from(tableMeta);
        return this;
    }

    /*################################## blow MySQLWhereAble method ##################################*/

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

    @Override
    public MySQLWhereAndAble<C> where(IPredicate predicate) {
        super.where(predicate);
        return this;
    }

    /*################################## blow MySQLWhereAndAble method ##################################*/

    @Override
    public MySQLWhereAndAble<C> and(IPredicate predicate) {
        super.and(predicate);
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> and(Function<C, IPredicate> function) {
        super.and(function);
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
    public DeleteAble limit(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public DeleteAble limit(Function<C, Integer> function) {
        this.rowCount = function.apply(this.criteria);
        return this;
    }

    @Override
    public DeleteAble ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            this.rowCount = rowCount;
        }
        return this;
    }

    @Override
    public DeleteAble ifLimit(Predicate<C> predicate, Function<C, Integer> function) {
        if (predicate.test(this.criteria)) {
            this.rowCount = function.apply(this.criteria);
        }
        return this;
    }

    /*################################## blow InnerMySQLSingleDelete method ##################################*/

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
    final void afterDoAsDelete() {
        super.afterDoAsDelete();

        this.sqlModifierList = Collections.unmodifiableList(this.sqlModifierList);
        this.sortExpList = Collections.unmodifiableList(this.sortExpList);
    }

    @Override
    void doClear() {
        super.doClear();

        this.sqlModifierList = null;
        this.sortExpList = null;
    }
}
