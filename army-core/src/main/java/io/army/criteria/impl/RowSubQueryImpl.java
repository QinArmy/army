package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSubQueryAble;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class RowSubQueryImpl<C> implements RowSubQuery, InnerSubQueryAble, OuterQueryAble
        , RowSubQuery.RowSubQuerySelectionAble<C>, RowSubQuery.RowSubQueryFromAble<C>
        , RowSubQuery.RowSubQueryOnAble<C>, RowSubQuery.RowSubQueryWhereAndAble<C>
        , RowSubQuery.RowSubQueryJoinAble<C>, RowSubQuery.RowSubQueryHavingAble<C> {


    private final SelectImpl<C> actualSelect;

    private QueryAble outerQuery;


    RowSubQueryImpl(C criteria) {
        this.actualSelect = new SelectImpl<>(criteria);
    }


    /*################################## blow SelfDescribed method ##################################*/

    @Override
    public final void appendSQL(SQLContext context) {
        context.dml().subQuery(this, context);
    }

    /*################################## blow RowSubQuery method ##################################*/

    @Override
    public final QueryAble outerQuery() {
        Assert.state(this.outerQuery != null, "outerQuery is null,criteria state error.");
        return this.outerQuery;
    }


    /*################################## blow RowSubQuerySelectionAble method ##################################*/

    @Override
    public <T extends IDomain> RowSubQueryFromAble<C> select(Distinct distinct, TableMeta<T> tableMeta) {
        this.actualSelect.select(distinct, tableMeta);
        return this;
    }

    @Override
    public <T extends IDomain> RowSubQueryFromAble<C> select(TableMeta<T> tableMeta) {
        this.actualSelect.select(tableMeta);
        return this;
    }

    @Override
    public RowSubQueryFromAble<C> select(String subQueryAlias) {
        this.actualSelect.select(subQueryAlias);
        return this;
    }

    @Override
    public RowSubQueryFromAble<C> select(Distinct distinct, String RowSubQueryAlias) {
        this.actualSelect.select(distinct, RowSubQueryAlias);
        return this;
    }

    @Override
    public RowSubQueryFromAble<C> select(List<Selection> selectionList) {
        this.actualSelect.select(selectionList);
        return this;
    }

    @Override
    public RowSubQueryFromAble<C> select(Distinct distinct, List<Selection> selectionList) {
        this.actualSelect.select(distinct, selectionList);
        return this;
    }

    @Override
    public RowSubQueryFromAble<C> select(Function<C, List<Selection>> function) {
        this.actualSelect.select(function);
        return this;
    }

    @Override
    public RowSubQueryFromAble<C> select(Distinct distinct, Function<C, List<Selection>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    /*################################## blow RowSubQueryFromAble method ##################################*/

    @Override
    public RowSubQueryOnAble<C> from(TableAble tableAble, String tableAlias) {
        this.actualSelect.from(tableAble, tableAlias);
        return this;
    }

    /*################################## blow RowSubQueryOnAble method ##################################*/

    @Override
    public RowSubQueryJoinAble<C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public RowSubQueryJoinAble<C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public RowSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow RowSubQueryJoinAble method ##################################*/

    @Override
    public RowSubQueryOnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.leftJoin(tableAble, tableAlias);
        return this;
    }

    @Override
    public RowSubQueryOnAble<C> join(TableAble tableAble, String tableAlias) {
        this.actualSelect.join(tableAble, tableAlias);
        return this;
    }

    @Override
    public RowSubQueryOnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.rightJoin(tableAble, tableAlias);
        return this;
    }


    /*################################## blow RowSubQueryWhereAble method ##################################*/

    @Override
    public RowSubQueryGroupByAble<C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public RowSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public RowSubQueryWhereAndAble<C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow RowSubQueryWhereAndAble method ##################################*/

    @Override
    public RowSubQueryWhereAndAble<C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public RowSubQueryWhereAndAble<C> and(Function<C, IPredicate> function) {
        this.actualSelect.and(function);
        return this;
    }

    @Override
    public RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow RowSubQueryGroupByAble method ##################################*/

    @Override
    public RowSubQueryHavingAble<C> groupBy(Expression<?> groupExp) {
        this.actualSelect.groupBy(groupExp);
        return this;
    }

    @Override
    public RowSubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifGroupBy(predicate, groupExp);
        return this;
    }

    @Override
    public RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow RowSubQueryHavingAble method ##################################*/

    @Override
    public RowSubQueryOrderByAble<C> having(List<IPredicate> predicateList) {
        this.actualSelect.having(predicateList);
        return this;
    }

    @Override
    public RowSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public RowSubQueryOrderByAble<C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public RowSubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList) {
        this.actualSelect.ifHaving(predicate, predicateList);
        return this;
    }

    @Override
    public RowSubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public RowSubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow RowSubQueryOrderByAble method ##################################*/

    @Override
    public RowSubQueryLimitAble<C> orderBy(Expression<?> groupExp) {
        this.actualSelect.orderBy(groupExp);
        return this;
    }

    @Override
    public RowSubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifOrderBy(predicate, groupExp);
        return this;
    }

    @Override
    public RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifOrderBy(predicate, expFunction);
        return this;
    }

    /*################################## blow RowSubQueryLimitAble method ##################################*/

    @Override
    public RowSubQuery limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return this;
    }

    @Override
    public RowSubQuery limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return this;
    }

    @Override
    public RowSubQuery limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return this;
    }

    @Override
    public RowSubQuery ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return this;
    }

    @Override
    public RowSubQuery ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public RowSubQuery ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(predicate, function);
        return this;
    }

    /*################################## blow OuterQueryAble method ##################################*/


    @Override
    public void outerQuery(QueryAble outerQuery) {
        Assert.state(this.outerQuery == null, "outerQuery only update once.");
        Assert.notNull(outerQuery, "outerQuery required.");
        this.outerQuery = outerQuery;
    }

    /*################################## blow TableSubQueryAble method ##################################*/

    @Override
    public final RowSubQuery asRowSubQuery() {
        return this;
    }

    /*################################## blow InnerSubQueryAble method ##################################*/

    @Override
    public List<SQLModifier> modifierList() {
        return this.actualSelect.modifierList();
    }

    @Override
    public List<Selection> selectionList() {
        return actualSelect.selectionList();
    }

    @Override
    public List<TableWrapper> tableWrapperList() {
        return this.actualSelect.tableWrapperList();
    }

    @Override
    public List<IPredicate> predicateList() {
        return this.actualSelect.predicateList();
    }

    @Override
    public List<Expression<?>> groupExpList() {
        return this.actualSelect.groupExpList();
    }

    @Override
    public List<IPredicate> havingList() {
        return this.actualSelect.havingList();
    }

    @Override
    public List<Expression<?>> sortExpList() {
        return this.actualSelect.sortExpList();
    }

    @Override
    public int offset() {
        return this.actualSelect.offset();
    }

    @Override
    public int rowCount() {
        return this.actualSelect.rowCount();
    }

    @Override
    public void prepare() {
        Assert.state(this.outerQuery != null, "outerQuery is null,criteria error.");
        this.actualSelect.prepare();
    }
}
