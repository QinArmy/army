package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractStandardSelect<C> extends AbstractSQL implements Select
        , Select.WhereAble<C>, Select.WhereAndAble<C>, Select.HavingAble<C>
        , Select.UnionClause<C>, Select.SelectPartAble<C>, Select.FromAble<C>
        , Select.JoinAble<C>, Select.OnAble<C>, InnerSelect {

    static final String NOT_PREPARED_MSG = "Select criteria don't haven invoke asSelect() method.";

    final C criteria;

    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList = new LinkedList<>();


    private List<IPredicate> predicateList = new ArrayList<>();

    private List<SortPart> groupExpList;

    private List<IPredicate> havingList;

    private List<SortPart> orderByList;

    private int offset = -1;

    private int rowCount = -1;

    private LockMode lockMode;

    private boolean prepared = false;


    AbstractStandardSelect(C criteria) {
        Assert.notNull(criteria, "criteria required");
        this.criteria = criteria;
    }


    /*################################## blow Select method ##################################*/


    @Override
    public final boolean requiredBrackets() {
        return CollectionUtils.isEmpty(this.orderByList)
                || this.offset > -1
                || this.rowCount > 0
                || this.lockMode != null
                ;
    }


    /*################################## blow SelectPartAble method ##################################*/

    @Override
    public <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelect(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public FromAble<C> select(Distinct distinct, SelectPart selectPart) {
        doSelect(distinct, selectPart);
        return this;
    }

    @Override
    public FromAble<C> select(SelectPart selectPart) {
        doSelect((Distinct) null, selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList) {
        doSelect(distinct, selectPartList);
        return this;
    }

    @Override
    public <S extends SelectPart> FromAble<C> select(List<S> selectPartList) {
        doSelect((Distinct) null, selectPartList);
        return this;
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public JoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.NONE));
        return this;
    }

    @Override
    public JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.NONE));
        return this;
    }


    /*################################## blow JoinAble method ##################################*/

    @Override
    public OnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.LEFT));
        return this;
    }

    @Override
    public OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.LEFT));
        return this;
    }

    @Override
    public OnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.JOIN));
        return this;
    }

    @Override
    public OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.JOIN));
        return this;
    }

    @Override
    public OnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new TableWrapperImpl(tableMeta, tableAlias, JoinType.RIGHT));
        return this;
    }

    @Override
    public OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT));
        return this;
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public final Select.JoinAble<C> on(List<IPredicate> predicateList) {
        doOn(predicateList);
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(IPredicate predicate) {
        doOn(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOn(function.apply(this.criteria));
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
        Assert.state(this.havingList.isEmpty(), "having clause ended.");
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
        return ComposeSelects.brackets(this.criteria, thisSelect());
    }

    @Override
    public final <S extends Select> UnionAble<C> union(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionAll(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow SelectAble method ##################################*/

    @Override
    public final Select asSelect() {
        if (prepared) {
            return this;
        }
        // before unmodifiableList .
        processSelectPartList(this.selectPartList);

        this.asSQL();
        this.modifierList = Collections.unmodifiableList(this.modifierList);
        this.selectPartList = Collections.unmodifiableList(this.selectPartList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.groupExpList = Collections.unmodifiableList(this.groupExpList);
        this.havingList = Collections.unmodifiableList(this.havingList);
        this.orderByList = Collections.unmodifiableList(this.orderByList);


        doAsSelect();

        this.prepared = true;
        return this;
    }


    /*################################## blow InnerQueryAble method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final List<SelectPart> selectPartList() {
        return this.selectPartList;
    }


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

    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }

    @Override
    public void clear() {
        super.beforeClear(NOT_PREPARED_MSG);

        this.modifierList = null;
        this.selectPartList = null;
        this.predicateList = null;
        this.groupExpList = null;

        this.havingList = null;
        this.orderByList = null;
        this.lockMode = null;

    }

    /*################################## blow package method ##################################*/

    @Override
    public final boolean prepared() {
        return this.prepared;
    }


    final <S extends SelectPart> void doSelect(@Nullable Distinct distinct, List<S> selectPartList) {
        if (distinct != null) {
            this.modifierList.add(distinct);
        }
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        this.selectPartList.addAll(selectPartList);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelect(List<M> modifierList, List<S> selectPartList) {
        this.modifierList.addAll(modifierList);
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        this.selectPartList.addAll(selectPartList);
    }

    final <S extends SelectPart> void doSelect(@Nullable Distinct distinct, S selectPart) {
        if (distinct != null) {
            this.modifierList.add(distinct);
        }
        this.selectPartList.add(selectPart);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelect(List<M> modifierList, S selectPart) {
        this.modifierList.addAll(modifierList);
        this.selectPartList.add(selectPart);
    }





    /*################################## blow package template method ##################################*/


    abstract void doAsSelect();


    /*################################## blow private method ##################################*/


    private Select thisSelect() {
        return this.asSelect();
    }


    /*################################## blow inner class ##################################*/


}
