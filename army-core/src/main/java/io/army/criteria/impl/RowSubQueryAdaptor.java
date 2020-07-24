package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerStandardSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

final class RowSubQueryAdaptor<C> implements RowSubQuery
        , RowSubQuery.RowSubQuerySelectPartAble<C>, RowSubQuery.RowSubQueryFromAble<C>
        , RowSubQuery.RowSubQueryOnAble<C>, RowSubQuery.RowSubQueryWhereAndAble<C>
        , RowSubQuery.RowSubQueryJoinAble<C>, RowSubQuery.RowSubQueryHavingAble<C>
        , RowSubQuery.TableRouteJoinAble<C>, RowSubQuery.TableRouteOnAble<C>, InnerStandardSubQuery {

    static <C> RowSubQueryAdaptor<C> build(C criteria) {
        return new RowSubQueryAdaptor<>(criteria);
    }

    private final SubQuerySelect<C> actualSelect;

    private RowSubQueryAdaptor(C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    @Override
    public final String toString() {
        return "#RowSubQuery@" + System.identityHashCode(this);
    }

    /*################################## blow RowSubQuery method ##################################*/

    @Override
    public final List<SelectPart> selectPartList() {
        return this.actualSelect.selectPartList();
    }


    @Override
    public final Selection selection(String derivedFieldName) {
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
    public final RowSubQueryFromAble<C> select(Distinct distinct, SelectionGroup selectionGroup) {
        this.actualSelect.select(distinct, selectionGroup);
        return this;
    }

    @Override
    public final RowSubQueryFromAble<C> select(SelectionGroup selectionGroup) {
        this.actualSelect.select(selectionGroup);
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
    public final TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final RowSubQueryFromAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> fromRoute(int databaseIndex, int tableIndex) {
        this.actualSelect.fromRoute(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> fromRoute(int tableIndex) {
        this.actualSelect.fromRoute(tableIndex);
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
    public final TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.leftJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.leftJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public final TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.join(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.join(function, subQueryAlia);
        return this;
    }

    @Override
    public final TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.rightJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final RowSubQueryJoinAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.rightJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public final RowSubQueryOnAble<C> route(int databaseIndex, int tableIndex) {
        this.actualSelect.route(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final RowSubQueryOnAble<C> route(int tableIndex) {
        this.actualSelect.route(tableIndex);
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
    public final RowSubQueryWhereAndAble<C> and(@Nullable IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public final RowSubQueryWhereAndAble<C> ifAnd(Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(function);
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
    public final RowSubQueryOrderByAble<C> having(List<IPredicate> predicateList) {
        this.actualSelect.having(predicateList);
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

    /*################################## blow SQLStatement method ##################################*/

    @Override
    public final boolean prepared() {
        return this.actualSelect.prepared();
    }

    /*################################## blow InnerSubQueryAble method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        return this.actualSelect.modifierList();
    }

    @Override
    public final List<? extends TableWrapper> tableWrapperList() {
        return this.actualSelect.tableWrapperList();
    }

    @Override
    public final List<IPredicate> predicateList() {
        return this.actualSelect.predicateList();
    }

    @Override
    public final List<SortPart> groupPartList() {
        return this.actualSelect.groupPartList();
    }

    @Override
    public final List<IPredicate> havingList() {
        return this.actualSelect.havingList();
    }

    @Override
    public final List<SortPart> orderPartList() {
        return this.actualSelect.orderPartList();
    }

    @Override
    public final int offset() {
        return this.actualSelect.offset();
    }

    @Override
    public final int rowCount() {
        return this.actualSelect.rowCount();
    }

    @Override
    public final void clear() {
        this.actualSelect.clear();
    }


}
