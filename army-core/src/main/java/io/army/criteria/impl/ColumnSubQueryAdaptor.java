package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerStandardSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class ColumnSubQueryAdaptor<E, C> implements ColumnSubQuery<E>, ColumnSubQuery.ColumnSubQuerySelectionAble<E, C>
        , ColumnSubQuery.ColumnSubQueryFromAble<E, C>, ColumnSubQuery.TableRouteOnAble<E, C>
        , ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C>, ColumnSubQuery.TableRouteJoinAble<E, C>
        , ColumnSubQuery.ColumnSubQueryHavingAble<E, C>, ColumnSubQuery.ColumnSubQueryUnionAble<E, C>
        , InnerStandardSubQuery {

    static <E, C> ColumnSubQueryAdaptor<E, C> build(Class<E> javaType, C criteria) {
        return new ColumnSubQueryAdaptor<>(javaType, criteria);
    }

    private final SubQuerySelect<C> actualSelect;


    private ColumnSubQueryAdaptor(Class<E> javaType, C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    @Override
    public final String toString() {
        return "#ColumnSubQuery@" + System.identityHashCode(this);
    }

    @Override
    public final List<SelectPart> selectPartList() {
        return this.actualSelect.selectPartList();
    }

    @Override
    public final Selection selection() {
        List<SelectPart> selectionList = this.actualSelect.selectPartList();
        Assert.state(selectionList.size() == 1, "ColumnSubQuery select clause error,selection count isn't 1 .");
        return (Selection) selectionList.get(0);
    }


    @Override
    public final Selection selection(String derivedFieldName) {
        return this.actualSelect.selection(derivedFieldName);
    }


    @Override
    public final void appendSQL(SQLContext context) {
        this.actualSelect.appendSQL(context);
    }

    /*################################## blow ColumnSubQuerySelectionAble method ##################################*/

    @Override
    public final ColumnSubQueryFromAble<E, C> select(Distinct distinct, Selection selection) {
        this.actualSelect.select(distinct, Collections.singletonList(selection));
        return this;
    }

    @Override
    public final ColumnSubQueryFromAble<E, C> select(Selection selection) {
        this.actualSelect.select(Collections.singletonList(selection));
        return this;
    }

    /*################################## blow ColumnSubQueryFromAble method ##################################*/

    @Override
    public final TableRouteJoinAble<E, C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final ColumnSubQueryJoinAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
        return this;
    }

    @Override
    public final ColumnSubQueryJoinAble<E, C> fromRoute(int databaseIndex, int tableIndex) {
        this.actualSelect.fromRoute(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final ColumnSubQueryJoinAble<E, C> fromRoute(int tableIndex) {
        this.actualSelect.fromRoute(tableIndex);
        return this;
    }

    /*################################## blow ColumnSubQueryOnAble method ##################################*/

    @Override
    public final ColumnSubQuery.ColumnSubQueryJoinAble<E, C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryJoinAble<E, C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow ColumnSubQueryJoinAble method ##################################*/

    @Override
    public final TableRouteOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.leftJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final ColumnSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.leftJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public final TableRouteOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.join(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final ColumnSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.join(function, subQueryAlia);
        return this;
    }

    @Override
    public final TableRouteOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.rightJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final ColumnSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.rightJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public final ColumnSubQueryOnAble<E, C> route(int databaseIndex, int tableIndex) {
        this.actualSelect.route(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final ColumnSubQueryOnAble<E, C> route(int tableIndex) {
        this.actualSelect.route(tableIndex);
        return this;
    }

    /*################################## blow ColumnSubQueryWhereAble method ##################################*/

    @Override
    public final ColumnSubQuery.ColumnSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow ColumnSubQueryWhereAndAble method ##################################*/

    @Override
    public final ColumnSubQueryWhereAndAble<E, C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> ifAnd(@Nullable IPredicate predicate) {
        this.actualSelect.ifAnd(predicate);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryWhereAndAble<E, C> ifAnd(Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(function);
        return this;
    }


    /*################################## blow ColumnSubQueryGroupByAble method ##################################*/

    @Override
    public final ColumnSubQueryHavingAble<E, C> groupBy(SortPart sortPart) {
        this.actualSelect.groupBy(sortPart);
        return this;
    }

    @Override
    public final ColumnSubQueryHavingAble<E, C> groupBy(List<SortPart> sortPartList) {
        this.actualSelect.groupBy(sortPartList);
        return this;
    }

    @Override
    public final ColumnSubQueryHavingAble<E, C> groupBy(Function<C, List<SortPart>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    /*################################## blow ColumnSubQueryHavingAble method ##################################*/


    @Override
    public final ColumnSubQuery.ColumnSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public final ColumnSubQuery.ColumnSubQueryOrderByAble<E, C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public final ColumnSubQueryOrderByAble<E, C> having(List<IPredicate> predicateList) {
        this.actualSelect.having(predicateList);
        return this;
    }

    /*################################## blow ColumnSubQueryOrderByAble method ##################################*/

    @Override
    public final ColumnSubQueryLimitAble<E, C> orderBy(SortPart sortPart) {
        this.actualSelect.orderBy(sortPart);
        return this;
    }

    @Override
    public final ColumnSubQueryLimitAble<E, C> orderBy(List<SortPart> sortPartList) {
        this.actualSelect.orderBy(sortPartList);
        return this;
    }

    @Override
    public final ColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<SortPart>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    /*################################## blow ColumnSubQueryLimitAble method ##################################*/

    @Override
    public final ColumnSubQueryUnionAble<E, C> limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return this;
    }

    @Override
    public final ColumnSubQueryUnionAble<E, C> limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return this;
    }

    @Override
    public final ColumnSubQueryUnionAble<E, C> ifLimit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(function);
        return this;
    }

    @Override
    public final ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return this;
    }

    @Override
    public final ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return this;
    }
    /*################################## blow ColumnSubQueryUnionAble method ##################################*/

    @Override
    public final ColumnSubQueryUnionAble<E, C> brackets() {
        return ComposeColumnSubQueries.brackets(this.actualSelect.criteria(), thisSubQuery());
    }

    @Override
    public final <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> union(Function<C, S> function) {
        return ComposeColumnSubQueries.compose(this.actualSelect.criteria(), thisSubQuery(), UnionType.UNION, function);
    }

    @Override
    public final <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionAll(Function<C, S> function) {
        return ComposeColumnSubQueries.compose(this.actualSelect.criteria(), thisSubQuery()
                , UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionDistinct(Function<C, S> function) {
        return ComposeColumnSubQueries.compose(this.actualSelect.criteria(), thisSubQuery()
                , UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow ColumnSubQueryAble method ##################################*/

    @Override
    public final ColumnSubQuery<E> asSubQuery() {
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


    /*################################## blow private method ##################################*/

    private ColumnSubQuery<E> thisSubQuery() {
        return this.asSubQuery();
    }
}
