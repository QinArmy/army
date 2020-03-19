package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSubQueryAble;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.util.Assert;
import io.army.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class ColumnSubQueryAdaptor<E, C> implements ColumnSubQuery<E>, ColumnSubQuery.ColumnSubQuerySelectionAble<E, C>
        , ColumnSubQuery.ColumnSubQueryFromAble<E, C>, ColumnSubQuery.ColumnSubQueryOnAble<E, C>
        , ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C>, ColumnSubQuery.ColumnSubQueryJoinAble<E, C>
        , ColumnSubQuery.ColumnSubQueryHavingAble<E, C>, OuterQueryAble, InnerSubQueryAble {

    private final SubQuerySelect<C> actualSelect;


    ColumnSubQueryAdaptor(Class<E> javaType, C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    @Override
    public List<SelectPart> selectPartList() {
        return this.actualSelect.selectPartList();
    }

    @Override
    public Selection selection() {
        List<SelectPart> selectionList = this.actualSelect.selectPartList();
        Assert.state(selectionList.size() == 1, "ColumnSubQuery select clause error,selection count isn't 1 .");
        return (Selection) selectionList.get(0);
    }

    @Override
    public SubQuery subordinateSubQuery(String subordinateSubQueryAlias) {
        return this.actualSelect.subordinateSubQuery(subordinateSubQueryAlias);
    }

    @Override
    public Selection selection(String derivedFieldName) {
        return this.actualSelect.selection(derivedFieldName);
    }

    @Override
    public void outerQuery(QueryAble outerQuery) {
        this.actualSelect.outerQuery(outerQuery);
    }

    @Override
    public QueryAble outerQuery() {
        return this.actualSelect.outerQuery();
    }

    @Override
    public void appendSQL(SQLContext context) {
        this.actualSelect.appendSQL(context);
    }

    /*################################## blow ColumnSubQuerySelectionAble method ##################################*/

    @Override
    public ColumnSubQueryFromAble<E, C> select(Distinct distinct, Selection selection) {
        this.actualSelect.select(distinct, Collections.singletonList(selection));
        return this;
    }

    @Override
    public ColumnSubQueryFromAble<E, C> select(Selection selection) {
        this.actualSelect.select(Collections.singletonList(selection));
        return this;
    }

    /*################################## blow ColumnSubQueryFromAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryJoinAble<E, C> from(TableAble tableAble, String tableAlias) {
        this.actualSelect.from(tableAble, tableAlias);
        return this;
    }

    /*################################## blow ColumnSubQueryOnAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryJoinAble<E, C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryJoinAble<E, C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow ColumnSubQueryJoinAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryOnAble<E, C> leftJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.leftJoin(tableAble, tableAlias);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryOnAble<E, C> join(TableAble tableAble, String tableAlias) {
        this.actualSelect.join(tableAble, tableAlias);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryOnAble<E, C> rightJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.rightJoin(tableAble, tableAlias);
        return this;
    }


    /*################################## blow ColumnSubQueryWhereAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow ColumnSubQueryWhereAndAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function) {
        this.actualSelect.and(function);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow ColumnSubQueryGroupByAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp) {
        this.actualSelect.groupBy(groupExp);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifGroupBy(predicate, groupExp);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryHavingAble<E, C> ifGroupBy(
            Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow ColumnSubQueryHavingAble method ##################################*/


    @Override
    public ColumnSubQuery.ColumnSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryOrderByAble<E, C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryOrderByAble<E, C> ifHaving(
            Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryOrderByAble<E, C> ifHaving(
            Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow ColumnSubQueryOrderByAble method ##################################*/

    @Override
    public ColumnSubQuery.ColumnSubQueryLimitAble<E, C> orderBy(Expression<?> groupExp) {
        this.actualSelect.orderBy(groupExp);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifOrderBy(predicate, groupExp);
        return this;
    }

    @Override
    public ColumnSubQuery.ColumnSubQueryLimitAble<E, C> ifOrderBy(
            Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifOrderBy(predicate, expFunction);
        return this;
    }

    /*################################## blow ColumnSubQueryLimitAble method ##################################*/

    @Override
    public ColumnSubQuery<E> limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return asColumnSubQuery();
    }

    @Override
    public ColumnSubQuery<E> limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return asColumnSubQuery();
    }

    @Override
    public ColumnSubQuery<E> limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return asColumnSubQuery();
    }

    @Override
    public ColumnSubQuery<E> ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return asColumnSubQuery();
    }

    @Override
    public ColumnSubQuery<E> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return asColumnSubQuery();
    }

    @Override
    public ColumnSubQuery<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(predicate, function);
        return asColumnSubQuery();
    }

    /*################################## blow ColumnSubQueryAble method ##################################*/

    @Override
    public final ColumnSubQuery<E> asColumnSubQuery() {
        this.actualSelect.asSelect();
        return this;
    }

    /*################################## blow InnerSubQueryAble method ##################################*/

    @Override
    public List<SQLModifier> modifierList() {
        return this.actualSelect.modifierList();
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
    public void clear() {
        this.actualSelect.clear();
    }
}
