package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

final class ColumnSubQueryAdaptor<E, C> implements ColumnSubQuery<E>, ColumnSubQuery.ColumnSubQuerySelectionAble<E, C>
        , ColumnSubQuery.ColumnSubQueryFromAble<E, C>, ColumnSubQuery.ColumnSubQueryOnAble<E, C>
        , ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C>, ColumnSubQuery.ColumnSubQueryJoinAble<E, C>
        , ColumnSubQuery.ColumnSubQueryHavingAble<E, C>, InnerSubQuery {

    private final SubQuerySelect<C> actualSelect;


    ColumnSubQueryAdaptor(Class<E> javaType, C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    @Override
    public String toString() {
        return "#ColumnSubQuery@" + System.identityHashCode(this);
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
    public Selection selection(String derivedFieldName) {
        return this.actualSelect.selection(derivedFieldName);
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
    public ColumnSubQueryJoinAble<E, C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ColumnSubQueryJoinAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
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
    public ColumnSubQueryOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.leftJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ColumnSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.leftJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public ColumnSubQueryOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.join(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ColumnSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.join(function, subQueryAlia);
        return this;
    }

    @Override
    public ColumnSubQueryOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.rightJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ColumnSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.rightJoin(function, subQueryAlia);
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
    public ColumnSubQueryUnionAble<E, C> limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryUnionAble<E, C> limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryUnionAble<E, C> limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return this;
    }

    @Override
    public ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(predicate, function);
        return this;
    }

    /*################################## blow ColumnSubQueryUnionAble method ##################################*/

    @Override
    public ColumnSubQueryUnionAble<E, C> brackets() {
        return ComposeColumnSubQueries.brackets(this.actualSelect.criteria(), thisSubQuery());
    }

    @Override
    public <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> union(Function<C, S> function) {
        return ComposeColumnSubQueries.compose(this.actualSelect.criteria(), thisSubQuery(), UnionType.UNION, function);
    }

    @Override
    public <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionAll(Function<C, S> function) {
        return ComposeColumnSubQueries.compose(this.actualSelect.criteria(), thisSubQuery()
                , UnionType.UNION_ALL, function);
    }

    @Override
    public <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionDistinct(Function<C, S> function) {
        return ComposeColumnSubQueries.compose(this.actualSelect.criteria(), thisSubQuery()
                , UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow ColumnSubQueryAble method ##################################*/

    @Override
    public final ColumnSubQuery<E> asSubQuery() {
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

    @Override
    public Map<TableMeta<?>, Integer> tablePresentCountMap() {
        return this.actualSelect.tablePresentCountMap();
    }


    /*################################## blow private method ##################################*/

    private ColumnSubQuery<E> thisSubQuery() {
        return this.asSubQuery();
    }
}