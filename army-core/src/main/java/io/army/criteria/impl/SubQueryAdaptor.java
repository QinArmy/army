package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class SubQueryAdaptor<C> implements SubQuery.SubQuerySelectPartAble<C>
        , SubQuery.SubQueryFromAble<C>, SubQuery.SubQueryOnAble<C>, SubQuery.SubQueryJoinAble<C>
        , SubQuery.SubQueryWhereAndAble<C>, SubQuery.SubQueryHavingAble<C>, SubQuery.SubQueryUnionAble<C>
        , SubQuery.TableRouteJoinAble<C>, SubQuery.TableRouteOnAble<C> {

    private final SubQuerySelect<C> actualSelect;

    SubQueryAdaptor(C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    /*################################## blow SelfDescribed method ##################################*/

    /*################################## blow SubQuerySelectPartAble method ##################################*/

    @Override
    public final <S extends SelectPart> SubQuery.SubQueryFromAble<C> select(Distinct distinct
            , Function<C, List<S>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    @Override
    public final SubQuery.SubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart) {
        this.actualSelect.select(distinct, selectPart);
        return this;
    }

    @Override
    public final SubQuery.SubQueryFromAble<C> select(SelectPart selectPart) {
        this.actualSelect.select(selectPart);
        return this;
    }

    @Override
    public final <S extends SelectPart> SubQuery.SubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList) {
        this.actualSelect.select(distinct, selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> SubQuery.SubQueryFromAble<C> select(List<S> selectPartList) {
        this.actualSelect.select(selectPartList);
        return this;
    }

    /*################################## blow SubQueryFromAble method ##################################*/

    @Override
    public final SubQuery.TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final SubQuery.SubQueryJoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
        return this;
    }

    @Override
    public final SubQuery.SubQueryJoinAble<C> fromRoute(int databaseIndex, int tableIndex) {
        this.actualSelect.fromRoute(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final SubQuery.SubQueryJoinAble<C> fromRoute(int tableIndex) {
        this.actualSelect.fromRoute(-1, tableIndex);
        return this;
    }

    /*################################## blow SubQueryOnAble method ##################################*/

    @Override
    public final SubQuery.SubQueryJoinAble<C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public final SubQuery.SubQueryJoinAble<C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public final SubQuery.SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow SubQueryJoinAble method ##################################*/

    @Override
    public final SubQuery.TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.leftJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.leftJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public final SubQuery.TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.join(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.join(function, subQueryAlia);
        return this;
    }

    @Override
    public final SubQuery.TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.rightJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.rightJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOnAble<C> route(int databaseIndex, int tableIndex) {
        this.actualSelect.route(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOnAble<C> route(int tableIndex) {
        this.actualSelect.route(-1, tableIndex);
        return this;
    }

    /*################################## blow SubQueryWhereAble method ##################################*/

    @Override
    public final SubQuery.SubQueryGroupByAble<C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public final SubQuery.SubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public final SubQuery.SubQueryWhereAndAble<C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow SubQueryWhereAndAble method ##################################*/

    @Override
    public final SubQuery.SubQueryWhereAndAble<C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public final SubQuery.SubQueryWhereAndAble<C> ifAnd(@Nullable IPredicate predicate) {
        this.actualSelect.ifAnd(predicate);
        return this;
    }

    @Override
    public final SubQuery.SubQueryWhereAndAble<C> ifAnd(Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(function);
        return this;
    }

    /*################################## blow SubQueryGroupByAble method ##################################*/

    @Override
    public final SubQuery.SubQueryHavingAble<C> groupBy(SortPart sortPart) {
        this.actualSelect.groupBy(sortPart);
        return this;
    }

    @Override
    public final SubQuery.SubQueryHavingAble<C> groupBy(List<SortPart> sortPartList) {
        this.actualSelect.groupBy(sortPartList);
        return this;
    }

    @Override
    public final SubQuery.SubQueryHavingAble<C> groupBy(Function<C, List<SortPart>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    /*################################## blow SubQueryHavingAble method ##################################*/

    @Override
    public final SubQuery.SubQueryOrderByAble<C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOrderByAble<C> having(List<IPredicate> predicateList) {
        this.actualSelect.having(predicateList);
        return this;
    }

    @Override
    public final SubQuery.SubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    /*################################## blow SubQueryOrderByAble method ##################################*/

    @Override
    public final SubQuery.SubQueryLimitAble<C> orderBy(SortPart sortPart) {
        this.actualSelect.orderBy(sortPart);
        return this;
    }

    @Override
    public final SubQuery.SubQueryLimitAble<C> orderBy(List<SortPart> sortPartList) {
        this.actualSelect.orderBy(sortPartList);
        return this;
    }

    @Override
    public final SubQuery.SubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    /*################################## blow SubQueryLimitAble method ##################################*/

    @Override
    public final SubQuery.SubQueryUnionAble<C> limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return this;
    }

    @Override
    public final SubQuery.SubQueryUnionAble<C> limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return this;
    }

    @Override
    public final SubQuery.SubQueryUnionAble<C> ifLimit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(function);
        return this;
    }

    @Override
    public final SubQuery.SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return this;
    }

    @Override
    public final SubQuery.SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return this;
    }

    /*################################## blow SubQueryUnionAble method ##################################*/

    @Override
    public final SubQuery.SubQueryUnionAble<C> brackets() {
        this.asSubQuery();
        return ComposeSubQueries.brackets(this.actualSelect.criteria(), thisSubQuery());
    }

    @Override
    public final <S extends SubQuery> SubQuery.SubQueryUnionAble<C> union(Function<C, S> function) {
        this.asSubQuery();
        return ComposeSubQueries.compose(this.actualSelect.criteria(), thisSubQuery(), UnionType.UNION, function);
    }

    @Override
    public final <S extends SubQuery> SubQuery.SubQueryUnionAble<C> unionAll(Function<C, S> function) {
        this.asSubQuery();
        return ComposeSubQueries.compose(this.actualSelect.criteria(), thisSubQuery(), UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends SubQuery> SubQuery.SubQueryUnionAble<C> unionDistinct(Function<C, S> function) {
        this.asSubQuery();
        return ComposeSubQueries.compose(this.actualSelect.criteria(), thisSubQuery()
                , UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow SubQueryAble method ##################################*/

    @Override
    public final SubQuery asSubQuery() {
        this.actualSelect.asSelect();
        return this.actualSelect;
    }

    /*################################## blow private method ##################################*/

    SubQuery thisSubQuery() {
        return this.asSubQuery();
    }


}
