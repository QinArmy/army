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
    public SubQuery.SubQueryFromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta) {
        this.actualSelect.select(distinct, tableAlias, tableMeta);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(String tableAlias, TableMeta<?> tableMeta) {
        this.actualSelect.select(tableAlias, tableMeta);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(Distinct distinct, String subQueryAlias) {
        this.actualSelect.select(distinct, subQueryAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(String subQueryAlias) {
        this.actualSelect.select(subQueryAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(List<SelectPart> selectPartList) {
        this.actualSelect.select(selectPartList);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(Distinct distinct, List<SelectPart> selectPartList) {
        this.actualSelect.select(distinct, selectPartList);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(Function<C, List<SelectPart>> function) {
        this.actualSelect.select(function);
        return this;
    }

    @Override
    public SubQuery.SubQueryFromAble<C> select(Distinct distinct, Function<C, List<SelectPart>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    /*################################## blow SubQueryFromAble method ##################################*/

    @Override
    public SubQuery.SubQueryOnAble<C> from(TableAble tableAble, String tableAlias) {
        this.actualSelect.from(tableAble, tableAlias);
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
    public SubQuery.SubQueryOnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.leftJoin(tableAble, tableAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> join(TableAble tableAble, String tableAlias) {
        this.actualSelect.join(tableAble, tableAlias);
        return this;
    }

    @Override
    public SubQuery.SubQueryOnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.rightJoin(tableAble, tableAlias);
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
    public SubQuery limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return asSubQuery();
    }

    @Override
    public SubQuery limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return asSubQuery();
    }

    @Override
    public SubQuery limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return asSubQuery();
    }

    @Override
    public SubQuery ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return asSubQuery();
    }

    @Override
    public SubQuery ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return asSubQuery();
    }

    @Override
    public SubQuery ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(predicate, function);
        return asSubQuery();
    }

    /*################################## blow TableSubQueryAble method ##################################*/

    @Override
    public final SubQuery asSubQuery() {
        this.actualSelect.asSelect();
        return this.actualSelect;
    }


}
