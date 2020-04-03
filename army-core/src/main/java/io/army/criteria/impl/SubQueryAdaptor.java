package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class SubQueryAdaptor<C> implements SubQuery.SubQuerySelectPartAble<C>
        , SubQuery.SubQueryFromAble<C>, SubQuery.SubQueryOnAble<C>, SubQuery.SubQueryJoinAble<C>
        , SubQuery.SubQueryWhereAndAble<C>, SubQuery.SubQueryHavingAble<C> {

    private final SubQuerySelect<C> actualSelect;

    SubQueryAdaptor(C criteria) {
        this.actualSelect = SubQuerySelect.build(criteria);
    }

    /*################################## blow SelfDescribed method ##################################*/

    /*################################## blow SubQuerySelectPartAble method ##################################*/

    @Override
    public <S extends SelectPart> SubQuery.SubQueryFromAble<C> select(Distinct distinct
            , Function<C, List<S>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart) {
        this.actualSelect.select(distinct, selectPart);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(SelectPart selectPart) {
        this.actualSelect.select(selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> SubQuery.SubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList) {
        this.actualSelect.select(distinct, selectPartList);
        return this;
    }

    @Override
    public <S extends SelectPart> SubQuery.SubQueryFromAble<C> select(List<S> selectPartList) {
        this.actualSelect.select(selectPartList);
        return this;
    }

    /*################################## blow SubQueryFromAble method ##################################*/

    @Override
    public SubQuery.SubQueryOnAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
        return this;
    }

    /*################################## blow SubQueryOnAble method ##################################*/

    @Override
    public SubQuery.SubQueryJoinAble<C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public SubQuery.SubQueryJoinAble<C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public SubQuery.SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow SubQueryJoinAble method ##################################*/

    @Override
    public SubQuery.SubQueryOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.leftJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.leftJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.join(tableMeta, tableAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.join(function, subQueryAlia);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.rightJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.rightJoin(function, subQueryAlia);
        return this;
    }

    /*################################## blow SubQueryWhereAble method ##################################*/

    @Override
    public SubQuery.SubQueryGroupByAble<C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public SubQuery.SubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryWhereAndAble<C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow SubQueryWhereAndAble method ##################################*/

    @Override
    public SubQuery.SubQueryWhereAndAble<C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public SubQuery.SubQueryWhereAndAble<C> and(Function<C, IPredicate> function) {
        this.actualSelect.and(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public SubQuery.SubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow SubQueryGroupByAble method ##################################*/

    @Override
    public SubQuery.SubQueryHavingAble<C> groupBy(Expression<?> groupExp) {
        this.actualSelect.groupBy(groupExp);
        return this;
    }

    @Override
    public SubQuery.SubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifGroupBy(predicate, groupExp);
        return this;
    }

    @Override
    public SubQuery.SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow SubQueryHavingAble method ##################################*/

    @Override
    public SubQuery.SubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryOrderByAble<C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public SubQuery.SubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public SubQuery.SubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow SubQueryOrderByAble method ##################################*/

    @Override
    public SubQuery.SubQueryLimitAble<C> orderBy(Expression<?> groupExp) {
        this.actualSelect.orderBy(groupExp);
        return this;
    }

    @Override
    public SubQuery.SubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifOrderBy(predicate, groupExp);
        return this;
    }

    @Override
    public SubQuery.SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifOrderBy(predicate, expFunction);
        return this;
    }

    /*################################## blow SubQueryLimitAble method ##################################*/

    @Override
    public SubQuery.SubQueryUnionAble<C> limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return this;
    }

    @Override
    public SubQuery.SubQueryUnionAble<C> limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return this;
    }

    @Override
    public SubQuery.SubQueryUnionAble<C> limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return this;
    }

    @Override
    public SubQuery.SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public SubQuery.SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(predicate, function);
        return this;
    }

    /*################################## blow SubQueryUnionAble method ##################################*/

    @Override
    public SubQuery.SubQueryUnionAble<C> brackets() {
        return ComposeSubQueries.brackets(this.actualSelect.criteria(), thisSubQuery());
    }

    @Override
    public <S extends SubQuery> SubQuery.SubQueryUnionAble<C> union(Function<C, S> function) {
        return ComposeSubQueries.compose(this.actualSelect.criteria(), thisSubQuery(), UnionType.UNION, function);
    }

    @Override
    public <S extends SubQuery> SubQuery.SubQueryUnionAble<C> unionAll(Function<C, S> function) {
        return ComposeSubQueries.compose(this.actualSelect.criteria(), thisSubQuery(), UnionType.UNION_ALL, function);
    }

    @Override
    public <S extends SubQuery> SubQuery.SubQueryUnionAble<C> unionDistinct(Function<C, S> function) {
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
