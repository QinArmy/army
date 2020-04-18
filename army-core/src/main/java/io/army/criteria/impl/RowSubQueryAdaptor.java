package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

final class RowSubQueryAdaptor<C> implements RowSubQuery
        , RowSubQuery.RowSubQuerySelectPartAble<C>, RowSubQuery.RowSubQueryFromAble<C>
        , RowSubQuery.RowSubQueryOnAble<C>, RowSubQuery.RowSubQueryWhereAndAble<C>
        , RowSubQuery.RowSubQueryJoinAble<C>, RowSubQuery.RowSubQueryHavingAble<C>, InnerSubQuery {


    private final SubQuerySelect<C> actualSelect;

    RowSubQueryAdaptor(C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    @Override
    public String toString() {
        return "#RowSubQuery@" + System.identityHashCode(this);
    }

    /*################################## blow RowSubQuery method ##################################*/

    @Override
    public List<SelectPart> selectPartList() {
        return this.actualSelect.selectPartList();
    }


    @Override
    public Selection selection(String derivedFieldName) {
        return this.actualSelect.selection(derivedFieldName);
    }

    @Override
    public final void appendSQL(SQLContext context) {
        this.actualSelect.appendSQL(context);
    }


    /*################################## blow RowSubQuerySelectPartAble method ##################################*/

    @Override
    public <S extends SelectPart> RowSubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    @Override
    public final RowSubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart) {
        this.actualSelect.select(distinct, selectPart);
        return this;
    }

    @Override
    public final RowSubQueryFromAble<C> select(SelectPart selectPart) {
        this.actualSelect.select(selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> RowSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList) {
        this.actualSelect.select(distinct, selectPartList);
        return this;
    }

    @Override
    public <S extends SelectPart> RowSubQueryFromAble<C> select(List<S> selectPartList) {
        this.actualSelect.select(selectPartList);
        return this;
    }

    /*################################## blow RowSubQueryFromAble method ##################################*/

    @Override
    public final RowSubQueryFromAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final RowSubQueryFromAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
        return this;
    }

    /*################################## blow RowSubQueryOnAble method ##################################*/

    @Override
    public final RowSubQueryJoinAble<C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow RowSubQueryJoinAble method ##################################*/

    @Override
    public final RowSubQueryJoinAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this;
    }

    /*################################## blow RowSubQueryWhereAble method ##################################*/

    @Override
    public final RowSubQueryGroupByAble<C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public final RowSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public final RowSubQueryWhereAndAble<C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow RowSubQueryWhereAndAble method ##################################*/

    @Override
    public final RowSubQueryWhereAndAble<C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public final RowSubQueryWhereAndAble<C> and(Function<C, IPredicate> function) {
        this.actualSelect.and(function);
        return this;
    }

    @Override
    public final RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public final RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow RowSubQueryGroupByAble method ##################################*/

    @Override
    public final RowSubQueryHavingAble<C> groupBy(SortPart sortPart) {
        this.actualSelect.groupBy(sortPart);
        return this;
    }

    @Override
    public final RowSubQueryHavingAble<C> groupBy(List<SortPart> sortPartList) {
        this.actualSelect.groupBy(sortPartList);
        return this;
    }

    @Override
    public final RowSubQueryHavingAble<C> groupBy(Function<C, List<SortPart>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public final RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, SortPart sortPart) {
        this.actualSelect.ifGroupBy(predicate, sortPart);
        return this;
    }

    @Override
    public final RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortPart>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow RowSubQueryHavingAble method ##################################*/

    @Override
    public final RowSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public final RowSubQueryOrderByAble<C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public final RowSubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public final RowSubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow RowSubQueryOrderByAble method ##################################*/

    @Override
    public final RowSubQueryLimitAble<C> orderBy(SortPart sortPart) {
        this.actualSelect.orderBy(sortPart);
        return this;
    }

    @Override
    public final RowSubQueryLimitAble<C> orderBy(List<SortPart> sortPartList) {
        this.actualSelect.orderBy(sortPartList);
        return this;
    }

    @Override
    public final RowSubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public final RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, SortPart sortPart) {
        this.actualSelect.ifOrderBy(predicate, sortPart);
        return this;
    }

    @Override
    public final RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<SortPart>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow RowSubQueryLimitAble method ##################################*/

    @Override
    public final RowSubQueryAble limitOne() {
        this.actualSelect.limit(1);
        return this;
    }

    /*################################## blow SubQueryAble method ##################################*/

    @Override
    public final RowSubQuery asSubQuery() {
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
    public List<SortPart> groupPartList() {
        return this.actualSelect.groupPartList();
    }

    @Override
    public List<IPredicate> havingList() {
        return this.actualSelect.havingList();
    }

    @Override
    public List<SortPart> orderPartList() {
        return this.actualSelect.orderPartList();
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
}
