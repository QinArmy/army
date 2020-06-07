package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerQuery;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractStandardSelect<C> extends AbstractSelect implements
        Select.WhereAble<C>, Select.WhereAndAble<C>, Select.HavingAble<C>
        , Select.UnionClause<C>, Select.SelectPartAble<C>, Select.FromAble<C>
        , Select.JoinAble<C>, Select.OnAble<C>, InnerQuery {


    final C criteria;

    private List<IPredicate> predicateList = new ArrayList<>();

    private List<SortPart> groupExpList;

    private List<IPredicate> havingList;

    private List<SortPart> orderByList;

    private int offset = -1;

    private int rowCount = -1;

    private LockMode lockMode;


    AbstractStandardSelect(C criteria) {
        Assert.notNull(criteria, "criteria required");
        this.criteria = criteria;
    }


    /*################################## blow Select method ##################################*/


    @Override
    public final boolean requiredBrackets() {
        return !CollectionUtils.isEmpty(this.orderByList)
                || this.offset > -1
                || this.rowCount > -1
                || this.lockMode != null
                ;
    }


    /*################################## blow SelectPartAble method ##################################*/

    @Override
    public final <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelect(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public final FromAble<C> select(Distinct distinct, SelectPart selectPart) {
        doSelectClause(distinct, selectPart);
        return this;
    }

    @Override
    public final FromAble<C> select(SelectPart selectPart) {
        doSelectClause((Distinct) null, selectPart);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList) {
        doSelect(distinct, selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromAble<C> select(List<S> selectPartList) {
        doSelect((Distinct) null, selectPartList);
        return this;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public final JoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.NONE));
        return this;
    }

    @Override
    public final JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.NONE));
        return this;
    }


    /*################################## blow JoinAble method ##################################*/

    @Override
    public final OnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.LEFT));
        return this;
    }

    @Override
    public final OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.LEFT));
        return this;
    }

    @Override
    public final OnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.JOIN));
        return this;
    }

    @Override
    public final OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.JOIN));
        return this;
    }

    @Override
    public final OnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.RIGHT));
        return this;
    }

    @Override
    public final OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT));
        return this;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public final Select.JoinAble<C> on(List<IPredicate> predicateList) {
        doOnClause(predicateList);
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(IPredicate predicate) {
        doOnClause(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOnClause(function.apply(this.criteria));
        return this;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public final Select.GroupByAble<C> where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final Select.GroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public final Select.WhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> and(Function<C, IPredicate> function) {
        this.predicateList.add(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow GroupByAble method ##################################*/

    @Override
    public final HavingAble<C> groupBy(SortPart sortPart) {
        if (this.groupExpList == null) {
            this.groupExpList = new ArrayList<>(1);
        }
        this.groupExpList.add(sortPart);
        return this;
    }

    @Override
    public final HavingAble<C> groupBy(List<SortPart> sortPartList) {
        if (this.groupExpList == null) {
            this.groupExpList = new ArrayList<>(sortPartList.size());
        }
        this.groupExpList.addAll(sortPartList);
        return this;
    }

    @Override
    public final HavingAble<C> groupBy(Function<C, List<SortPart>> function) {
        return groupBy(function.apply(this.criteria));
    }

    @Override
    public final HavingAble<C> ifGroupBy(Predicate<C> test, SortPart sortPart) {
        if (test.test(this.criteria)) {
            groupBy(sortPart);
        }
        return this;
    }

    @Override
    public final HavingAble<C> ifGroupBy(Predicate<C> test, Function<C, List<SortPart>> function) {
        if (test.test(this.criteria)) {
            groupBy(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow HavingAble method ##################################*/

    @Override
    public final Select.OrderByAble<C> having(Function<C, List<IPredicate>> function) {
        if (this.groupExpList.isEmpty()) {
            return this;
        }
        this.havingList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.OrderByAble<C> having(IPredicate predicate) {
        if (this.groupExpList.isEmpty()) {
            return this;
        }
        Assert.state(this.havingList.isEmpty(), "having clause ended.");
        this.havingList.add(predicate);
        return this;
    }

    @Override
    public final Select.OrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        if (predicate.test(this.criteria)) {
            having(function);
        }
        return this;
    }

    @Override
    public final Select.OrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            having(predicate);
        }
        return this;
    }

    /*################################## blow OrderByAble method ##################################*/

    @Override
    public final LimitAble<C> orderBy(SortPart sortPart) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList<>(1);
        }
        this.orderByList.add(sortPart);
        return this;
    }

    @Override
    public final LimitClause<C> orderBy(List<SortPart> sortPartList) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList<>(sortPartList.size());
        }
        this.orderByList.addAll(sortPartList);
        return this;
    }

    @Override
    public final LimitAble<C> orderBy(Function<C, List<SortPart>> function) {
        orderBy(function.apply(this.criteria));
        return this;
    }

    @Override
    public final LimitAble<C> ifOrderBy(Predicate<C> test, SortPart sortPart) {
        if (test.test(this.criteria)) {
            orderBy(sortPart);
        }
        return this;
    }

    @Override
    public final LimitAble<C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function) {
        if (test.test(this.criteria)) {
            orderBy(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow LimitAble method ##################################*/

    @Override
    public final Select.LockAble<C> limit(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Select.LockAble<C> limit(int offset, int rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Select.LockAble<C> limit(Function<C, Pair<Integer, Integer>> function) {
        Pair<Integer, Integer> pair = function.apply(this.criteria);
        int offset = -1, rowCount = -1;
        if (pair.getFirst() != null) {
            offset = pair.getFirst();
        }
        if (pair.getSecond() != null) {
            rowCount = pair.getSecond();
        }
        limit(offset, rowCount);
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(offset, rowCount);
        }
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        if (predicate.test(this.criteria)) {
            limit(function);
        }
        return this;
    }

    /*################################## blow LockAble method ##################################*/

    @Override
    public final UnionClause<C> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final UnionClause<C> lock(Function<C, LockMode> function) {
        this.lockMode = function.apply(this.criteria);
        return this;
    }

    @Override
    public final UnionClause<C> ifLock(Predicate<C> predicate, LockMode lockMode) {
        if (predicate.test(this.criteria)) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final UnionClause<C> ifLock(Predicate<C> predicate, Function<C, LockMode> function) {
        if (predicate.test(this.criteria)) {
            this.lockMode = function.apply(this.criteria);
        }
        return this;
    }

    /*################################## blow UnionAble method ##################################*/

    @Override
    public final UnionAble<C> brackets() {
        this.asSelect();
        return ComposeSelects.brackets(this.criteria, thisSelect());
    }

    @Override
    public final <S extends Select> UnionAble<C> union(Function<C, S> function) {
        this.asSelect();
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionAll(Function<C, S> function) {
        this.asSelect();
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function) {
        this.asSelect();
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow SelectAble method ##################################*/

    @Override
    final void internalAsSelect() {
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        if (this.groupExpList == null) {
            this.groupExpList = Collections.emptyList();
        } else {
            this.groupExpList = CriteriaUtils.unmodifiableList(this.groupExpList);
        }

        if (this.havingList == null) {
            this.havingList = Collections.emptyList();
        } else {
            this.havingList = CriteriaUtils.unmodifiableList(this.havingList);
        }

        if (this.orderByList == null) {
            this.orderByList = Collections.emptyList();
        } else {
            this.orderByList = CriteriaUtils.unmodifiableList(this.orderByList);
        }

        concreteAsSelect();
    }



    /*################################## blow InnerQueryAble method ##################################*/

    @Override
    public final List<IPredicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public final List<SortPart> groupPartList() {
        return this.groupExpList;
    }

    @Override
    public final List<IPredicate> havingList() {
        return this.havingList;
    }

    @Override
    public final List<SortPart> orderPartList() {
        return this.orderByList;
    }

    @Override
    public final int offset() {
        return this.offset;
    }

    @Override
    public final int rowCount() {
        return this.rowCount;
    }

    public final LockMode lockMode() {
        return this.lockMode;
    }

    @Override
    final void internalClear() {
        this.predicateList = null;

        this.groupExpList = null;
        this.havingList = null;
        this.orderByList = null;
        this.lockMode = null;

        concreteClear();
    }


    /*################################## blow package template method ##################################*/

    abstract void concreteAsSelect();

    abstract void concreteClear();

    /*################################## blow private method ##################################*/


    private Select thisSelect() {
        return this.asSelect();
    }


    /*################################## blow inner class ##################################*/


}
