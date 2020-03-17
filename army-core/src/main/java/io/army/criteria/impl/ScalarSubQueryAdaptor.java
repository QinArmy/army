package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class ScalarSubQueryAdaptor<E, C> extends AbstractExpression<E> implements ScalarSubQuery<E>, OuterQueryAble
        , ScalarSubQuery.ScalarSubQuerySelectionAble<E, C>, ScalarSubQuery.ScalarSubQueryFromAble<E, C>
        , ScalarSubQuery.ScalarSubQueryOnAble<E, C>, ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C>
        , ScalarSubQuery.ScalarSubQueryJoinAble<E, C>, ScalarSubQuery.ScalarSubQueryHavingAble<E, C> {


    private final MappingType mappingType;

    private final SubQuerySelect<C> actualSelect;

    ScalarSubQueryAdaptor(Class<E> javaType, MappingType mappingType, C criteria) {
        Assert.isAssignable(javaType, mappingType.javaType(), "javaType and mappingType not match.");
        this.mappingType = mappingType;
        this.actualSelect = SubQuerySelect.build(criteria);
    }


    /*################################## blow AbstractExpression method ##################################*/

    @Override
    protected final void afterSpace(SQLContext context) {
        this.actualSelect.appendSQL(context);
    }

    @Override
    protected final String beforeAs() {
        return "#ScalarSubQuery";
    }

    @Override
    public final MappingType mappingType() {
        return mappingType;
    }

    /*################################## blow OuterQueryAble method ##################################*/

    @Override
    public void outerQuery(QueryAble outerQuery) {
        this.actualSelect.outerQuery(outerQuery);
    }

    /*################################## blow ScalarSubQuery<E> method ##################################*/

    @Override
    public final QueryAble outerQuery() {
        return this.actualSelect.outerQuery();
    }

    @Override
    public List<Selection> selectionList() {
        return this.actualSelect.selectionList();
    }

    @Override
    public SubQuery subordinateSubQuery(String subordinateSubQueryAlias) {
        return this.actualSelect.subordinateSubQuery(subordinateSubQueryAlias);
    }

    @Override
    public final Selection selection() {
        List<Selection> selectionList = this.actualSelect.selectionList();
        Assert.state(selectionList.size() == 1, "selectionList size isn't 1,criteria error.");
        return selectionList.get(0);
    }

    /*################################## blow RowSubQuerySelectionAble method ##################################*/

    @Override
    public <T extends IDomain> ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(Distinct distinct, TableMeta<T> tableMeta) {
        this.actualSelect.select(distinct, tableMeta);
        return this;
    }

    @Override
    public <T extends IDomain> ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(TableMeta<T> tableMeta) {
        this.actualSelect.select(tableMeta);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(String subQueryAlias) {
        this.actualSelect.select(subQueryAlias);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(Distinct distinct, String RowSubQueryAlias) {
        this.actualSelect.select(distinct, RowSubQueryAlias);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(List<Selection> selectionList) {
        this.actualSelect.select(selectionList);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(Distinct distinct, List<Selection> selectionList) {
        this.actualSelect.select(distinct, selectionList);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(Function<C, List<Selection>> function) {
        this.actualSelect.select(function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryFromAble<E, C> select(Distinct distinct, Function<C, List<Selection>> function) {
        this.actualSelect.select(distinct, function);
        return this;
    }

    /*################################## blow RowSubQueryFromAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryOnAble<E, C> from(TableAble tableAble, String tableAlias) {
        this.actualSelect.from(tableAble, tableAlias);
        return this;
    }

    /*################################## blow RowSubQueryOnAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryJoinAble<E, C> on(List<IPredicate> predicateList) {
        this.actualSelect.on(predicateList);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryJoinAble<E, C> on(IPredicate predicate) {
        this.actualSelect.on(predicate);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function) {
        this.actualSelect.on(function);
        return this;
    }

    /*################################## blow RowSubQueryJoinAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryOnAble<E, C> leftJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.leftJoin(tableAble, tableAlias);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOnAble<E, C> join(TableAble tableAble, String tableAlias) {
        this.actualSelect.join(tableAble, tableAlias);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOnAble<E, C> rightJoin(TableAble tableAble, String tableAlias) {
        this.actualSelect.rightJoin(tableAble, tableAlias);
        return this;
    }


    /*################################## blow RowSubQueryWhereAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList) {
        this.actualSelect.where(predicateList);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function) {
        this.actualSelect.where(function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> where(IPredicate predicate) {
        this.actualSelect.where(predicate);
        return this;
    }


    /*################################## blow RowSubQueryWhereAndAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function) {
        this.actualSelect.and(function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow RowSubQueryGroupByAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp) {
        this.actualSelect.groupBy(groupExp);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifGroupBy(predicate, groupExp);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifGroupBy(predicate, expFunction);
        return this;
    }

    /*################################## blow RowSubQueryHavingAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> having(List<IPredicate> predicateList) {
        this.actualSelect.having(predicateList);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function) {
        this.actualSelect.having(function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> having(IPredicate predicate) {
        this.actualSelect.having(predicate);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList) {
        this.actualSelect.ifHaving(predicate, predicateList);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow RowSubQueryOrderByAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryLimitAble<E, C> orderBy(Expression<?> groupExp) {
        this.actualSelect.orderBy(groupExp);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp) {
        this.actualSelect.ifOrderBy(predicate, groupExp);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        this.actualSelect.ifOrderBy(predicate, expFunction);
        return this;
    }

    /*################################## blow RowSubQueryLimitAble method ##################################*/

    @Override
    public ScalarSubQuery<E> limit(int rowCount) {
        this.actualSelect.limit(rowCount);
        return asScalarSubQuery();
    }

    @Override
    public ScalarSubQuery<E> limit(int offset, int rowCount) {
        this.actualSelect.limit(offset, rowCount);
        return asScalarSubQuery();
    }

    @Override
    public ScalarSubQuery<E> limit(Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.limit(function);
        return asScalarSubQuery();
    }

    @Override
    public ScalarSubQuery<E> ifLimit(Predicate<C> predicate, int rowCount) {
        this.actualSelect.ifLimit(predicate, rowCount);
        return asScalarSubQuery();
    }

    @Override
    public ScalarSubQuery<E> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        this.actualSelect.ifLimit(predicate, offset, rowCount);
        return asScalarSubQuery();
    }

    @Override
    public ScalarSubQuery<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        this.actualSelect.ifLimit(predicate, function);
        return asScalarSubQuery();
    }



    /*################################## blow TableSubQueryAble method ##################################*/

    @Override
    public final ScalarSubQuery<E> asScalarSubQuery() {
        this.actualSelect.asSelect();
        return this;
    }


}
