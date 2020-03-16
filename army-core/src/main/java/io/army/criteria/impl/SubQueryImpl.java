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

final class SubQueryImpl<C> implements
        InnerSubQueryAble, OuterQueryAble, SubQuery.SubQuerySelectionAble<C>
        , SubQuery.SubQueryFromAble<C>, SubQuery.SubQueryOnAble<C>, SubQuery.SubQueryJoinAble<C>
        , SubQuery.SubQueryWhereAndAble<C>, SubQuery.SubQueryHavingAble<C> {

    private final SelectImpl<C> actualSelect;

    private QueryAble outerQuery;

    SubQueryImpl(C criteria) {
        this.actualSelect = new SelectImpl<>(criteria);
    }

    /*################################## blow SelfDescribed method ##################################*/

    @Override
    public final void appendSQL(SQLContext context) {
        context.dml().subQuery(this, context);
    }

    /*################################## blow SubQuery method ##################################*/

    @Override
    public final QueryAble outerQuery() {
        Assert.state(this.outerQuery != null, "outerQuery is null,criteria state error.");
        return this.outerQuery;
    }


    /*################################## blow SubQuerySelectionAble method ##################################*/

    @Override
    public <T extends IDomain> SubQueryFromAble<C> select(Distinct distinct, TableMeta<T> tableMeta) {
        this.actualSelect.select(distinct, tableMeta);
        return this;
    }

    @Override
    public <T extends IDomain> SubQueryFromAble<C> select(TableMeta<T> tableMeta) {
        this.actualSelect.select(tableMeta);
        return this;
    }

    @Override
    public SubQueryFromAble<C> select(String subQueryAlias) {
        this.actualSelect.select(subQueryAlias);
        return this;
    }

    @Override
    public SubQueryFromAble<C> select(Distinct distinct, String subQueryAlias) {
        this.actualSelect.select(distinct, subQueryAlias);
        return this;
    }

    @Override
    public SubQueryFromAble<C> select(List<Selection> selectionList) {
        this.actualSelect.select(selectionList);
        return this;
    }

    @Override
    public SubQueryFromAble<C> select(Distinct distinct, List<Selection> selectionList) {
        this.actualSelect.select(distinct, selectionList);
        return this;
    }

    @Override
    public SubQueryFromAble<C> select(Function<C, List<Selection>> function) {
        this.actualSelect.select(function);
        return this;
    }

    @Override
    public SubQueryFromAble<C> select(Distinct distinct, Function<C, List<Selection>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    /*################################## blow SubQueryFromAble method ##################################*/

    @Override
    public SubQueryOnAble<C> from(TableAble tableAble, String tableAlias) {
        this.actualSelect.from(tableAble, tableAlias);
        return this;
    }

    /*################################## blow SubQueryOnAble method ##################################*/

    @Override
    public SubQueryJoinAble<C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public SubQueryJoinAble<C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow SubQueryJoinAble method ##################################*/

    @Override
    public SubQueryOnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.leftJoin(tableAble, tableAlias);
        return this;
    }

    @Override
    public SubQueryOnAble<C> join(TableAble tableAble, String tableAlias) {
        this.actualSelect.join(tableAble, tableAlias);
        return this;
    }

    @Override
    public SubQueryOnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.rightJoin(tableAble, tableAlias);
        return this;
    }


    /*################################## blow SubQueryWhereAble method ##################################*/

    @Override
    public SubQueryGroupByAble<C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public SubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public SubQueryWhereAndAble<C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow SubQueryWhereAndAble method ##################################*/

    @Override
    public SubQueryWhereAndAble<C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public SubQueryWhereAndAble<C> and(Function<C, IPredicate> function) {
        this.actualSelect.and(function);
        return this;
    }

    @Override
    public SubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public SubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow SubQueryGroupByAble method ##################################*/

    @Override
    public SubQueryHavingAble<C> groupBy(Expression<?> groupExp) {
        this.actualSelect.groupBy(groupExp);
        return this;
    }

    @Override
    public SubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifGroupBy(predicate, groupExp);
        return this;
    }

    @Override
    public SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow SubQueryHavingAble method ##################################*/

    @Override
    public SubQueryOrderByAble<C> having(List<IPredicate> predicateList) {
        this.actualSelect.having(predicateList);
        return this;
    }

    @Override
    public SubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public SubQueryOrderByAble<C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public SubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList) {
        this.actualSelect.ifHaving(predicate, predicateList);
        return this;
    }

    @Override
    public SubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public SubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow SubQueryOrderByAble method ##################################*/

    @Override
    public SubQueryLimitAble<C> orderBy(Expression<?> groupExp) {
        this.actualSelect.orderBy(groupExp);
        return this;
    }

    @Override
    public SubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifOrderBy(predicate, groupExp);
        return this;
    }

    @Override
    public SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifOrderBy(predicate, expFunction);
        return this;
    }

    /*################################## blow SubQueryLimitAble method ##################################*/

    @Override
    public SubQuery limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return this;
    }

    @Override
    public SubQuery limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return this;
    }

    @Override
    public SubQuery limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return this;
    }

    @Override
    public SubQuery ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return this;
    }

    @Override
    public SubQuery ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public SubQuery ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
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
    public final SubQuery asSubQuery() {
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
