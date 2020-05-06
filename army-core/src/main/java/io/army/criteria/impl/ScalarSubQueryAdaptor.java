package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerStandardSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * this class is a implementation of {@link ScalarSubQuery}.
 *
 * @param <E> {@link ScalarSubQuery#selection()}'s Java Type.
 * @param <C> custom object for Dynamic SQL.
 */
final class ScalarSubQueryAdaptor<E, C> extends AbstractExpression<E> implements ScalarSubQuery<E>
        , ScalarSubQuery.ScalarSubQuerySelectionAble<E, C>, ScalarSubQuery.ScalarSubQueryFromAble<E, C>
        , ScalarSubQuery.ScalarSubQueryOnAble<E, C>, ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C>
        , ScalarSubQuery.ScalarSubQueryJoinAble<E, C>, ScalarSubQuery.ScalarSubQueryHavingAble<E, C>
        , InnerStandardSubQuery {


    private final MappingMeta mappingType;

    private final SubQuerySelect<C> actualSelect;

    ScalarSubQueryAdaptor(Class<E> javaType, MappingMeta mappingType, C criteria) {
        Assert.isAssignable(javaType, mappingType.javaType(), "javaType and paramMeta not match.");
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
        return "#ScalarSubQuery:" + System.identityHashCode(this);
    }

    @Override
    public final MappingMeta mappingType() {
        return mappingType;
    }

    /*################################## blow ScalarSubQuery<E> method ##################################*/

    @Override
    public List<SelectPart> selectPartList() {
        return this.actualSelect.selectPartList();
    }


    @Override
    public Selection selection(String derivedFieldName) {
        return this.actualSelect.selection(derivedFieldName);
    }

    @Override
    public final Selection selection() {
        List<SelectPart> selectPartList = this.actualSelect.selectPartList();
        Assert.state(selectPartList.size() == 1, "selectPartList size isn't 1,criteria error.");
        return (Selection) selectPartList.get(0);
    }

    /*################################## blow ScalarSubQuerySelectionAble method ##################################*/

    @Override
    public ScalarSubQuerySelectionAble<E, C> select(Distinct distinct, Selection selection) {
        this.actualSelect.select(distinct, Collections.singletonList(selection));
        return this;
    }

    @Override
    public ScalarSubQuerySelectionAble<E, C> select(Selection selection) {
        this.actualSelect.select(Collections.singletonList(selection));
        return this;
    }

    /*################################## blow ScalarSubQueryFromAble method ##################################*/

    @Override
    public ScalarSubQueryFromAble<E, C> from(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.from(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ScalarSubQueryFromAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.from(function, subQueryAlia);
        return this;
    }
    /*################################## blow ScalarSubQueryOnAble method ##################################*/

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

    /*################################## blow ScalarSubQueryJoinAble method ##################################*/

    @Override
    public ScalarSubQueryOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.leftJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ScalarSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.leftJoin(function, subQueryAlia);
        return this;
    }

    @Override
    public ScalarSubQueryOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.join(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ScalarSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.join(function, subQueryAlia);
        return this;
    }

    @Override
    public ScalarSubQueryOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        this.actualSelect.rightJoin(tableMeta, tableAlias);
        return this;
    }

    @Override
    public ScalarSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        this.actualSelect.rightJoin(function, subQueryAlia);
        return this;
    }

    /*################################## blow ScalarSubQueryWhereAble method ##################################*/

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


    /*################################## blow ScalarSubQueryWhereAndAble method ##################################*/

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> and(IPredicate predicate) {
        this.actualSelect.and(predicate);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifAnd(testPredicate, predicate);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate
            , Function<C, IPredicate> function) {
        this.actualSelect.ifAnd(testPredicate, function);
        return this;
    }

    /*################################## blow ScalarSubQueryGroupByAble method ##################################*/

    @Override
    public ScalarSubQueryHavingAble<E, C> groupBy(SortPart sortPart) {
        this.actualSelect.groupBy(sortPart);
        return this;
    }

    @Override
    public ScalarSubQueryHavingAble<E, C> groupBy(List<SortPart> sortPartList) {
        this.actualSelect.groupBy(sortPartList);
        return this;
    }

    @Override
    public ScalarSubQueryHavingAble<E, C> groupBy(Function<C, List<SortPart>> function) {
        this.actualSelect.groupBy(function);
        return this;
    }

    @Override
    public ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, SortPart sortPart) {
        this.actualSelect.ifGroupBy(predicate, sortPart);
        return this;
    }

    @Override
    public ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortPart>> function) {
        this.actualSelect.ifGroupBy(predicate, function);
        return this;
    }

    /*################################## blow ScalarSubQueryHavingAble method ##################################*/

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
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        this.actualSelect.ifHaving(predicate, function);
        return this;
    }

    @Override
    public ScalarSubQuery.ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        this.actualSelect.ifHaving(testPredicate, predicate);
        return this;
    }

    /*################################## blow ScalarSubQueryOrderByAble method ##################################*/

    @Override
    public ScalarSubQueryLimitAble<E, C> orderBy(SortPart sortPart) {
        this.actualSelect.orderBy(sortPart);
        return this;
    }

    @Override
    public ScalarSubQueryLimitAble<E, C> orderBy(List<SortPart> sortPartList) {
        this.actualSelect.orderBy(sortPartList);
        return this;
    }

    @Override
    public ScalarSubQueryLimitAble<E, C> orderBy(Function<C, List<SortPart>> function) {
        this.actualSelect.orderBy(function);
        return this;
    }

    @Override
    public ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> test, SortPart sortPart) {
        this.actualSelect.ifOrderBy(test, sortPart);
        return this;
    }

    @Override
    public ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function) {
        this.actualSelect.ifOrderBy(test, function);
        return this;
    }

    /*################################## blow ScalarSubQueryLimitAble method ##################################*/

    @Override
    public ScalarSubQueryAble<E> limitOne() {
        this.actualSelect.limit(1);
        return this;
    }

    /*################################## blow ScalarSubQueryAble method ##################################*/

    @Override
    public final ScalarSubQuery<E> asSubQuery() {
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
