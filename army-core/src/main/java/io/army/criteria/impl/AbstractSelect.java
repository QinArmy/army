package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSelectAble;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractSelect<C> extends AbstractSQL implements Select
        , Select.WhereAble<C>, Select.WhereAndAble<C>, Select.HavingAble<C>, InnerSelectAble {

    static final String NOT_PREPARED_MSG = "Select criteria don't haven invoke asSelect() method.";

    final C criteria;

    private List<SQLModifier> modifierList = new ArrayList<>(2);

    private List<SelectPart> selectPartList = new LinkedList<>();


    private List<IPredicate> predicateList = new ArrayList<>();

    private List<Expression<?>> groupExpList = new ArrayList<>(3);

    private List<IPredicate> havingList = new ArrayList<>(3);

    private List<Expression<?>> sortExpList = new ArrayList<>(3);

    private int offset = -1;

    private int rowCount = -1;

    private LockMode lockMode;

    private boolean prepared = false;


    AbstractSelect(C criteria) {
        Assert.notNull(criteria, "criteria required");
        this.criteria = criteria;
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public final Select.GroupByAble<C> where(List<IPredicate> predicateList) {
        Assert.state(this.predicateList.isEmpty(), "where ended.");
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final Select.GroupByAble<C> where(Function<C, List<IPredicate>> function) {
        return where(function.apply(this.criteria));
    }

    @Override
    public final Select.WhereAndAble<C> where(IPredicate predicate) {
        Assert.state(this.predicateList.isEmpty(), "where ended.");
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public final Select.WhereAndAble<C> and(IPredicate predicate) {
        Assert.state(!this.predicateList.isEmpty(), "no where clause.");
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> and(Function<C, IPredicate> function) {
        return and(function.apply(this.criteria));
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            and(predicate);
        }
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            and(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow GroupByAble method ##################################*/

    @Override
    public final Select.HavingAble<C> groupBy(Expression<?> groupExp) {
        Assert.state(this.groupExpList.isEmpty(), "group by clause ended.");
        groupExpList.add(groupExp);
        return this;
    }

    @Override
    public final Select.HavingAble<C> groupBy(Function<C, List<Expression<?>>> function) {
        Assert.state(this.groupExpList.isEmpty(), "group by clause ended.");
        groupExpList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.HavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        if (predicate.test(this.criteria)) {
            groupBy(groupExp);
        }
        return this;
    }

    @Override
    public final Select.HavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            groupBy(expFunction);
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
    public final Select.LimitAble<C> orderBy(Expression<?> groupExp) {
        Assert.state(this.sortExpList.isEmpty(), "order by clause ended.");
        this.sortExpList.add(groupExp);
        return this;
    }

    @Override
    public final Select.LimitAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        Assert.state(this.sortExpList.isEmpty(), "order by clause ended.");
        this.sortExpList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.LimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp) {
        if (predicate.test(this.criteria)) {
            orderBy(groupExp);
        }
        return this;
    }

    @Override
    public final Select.LimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            orderBy(expFunction);
        }
        return this;
    }


    /*################################## blow LimitAble method ##################################*/

    @Override
    public final Select.LockAble<C> limit(int rowCount) {
        Assert.state(this.rowCount < 0, "limit clause ended.");
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Select.LockAble<C> limit(int offset, int rowCount) {
        Assert.state(this.offset < 0, "limit clause ended.");
        Assert.state(this.rowCount < 0, "limit clause ended.");
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
    public final Select lock(LockMode lockMode) {
        Assert.state(this.lockMode == null, "lock clause ended.");
        this.lockMode = lockMode;
        return asSelect();
    }

    @Override
    public final Select lock(Function<C, LockMode> function) {
        return lock(function.apply(this.criteria));
    }

    @Override
    public final Select ifLock(Predicate<C> predicate, LockMode lockMode) {
        if (predicate.test(this.criteria)) {
            lock(lockMode);
        }
        return asSelect();
    }

    @Override
    public final Select ifLock(Predicate<C> predicate, Function<C, LockMode> function) {
        if (predicate.test(this.criteria)) {
            lock(function);
        }
        return asSelect();
    }

    /*################################## blow SelectAble method ##################################*/

    @Override
    public final Select asSelect() {
        if (prepared) {
            return this;
        }
        // before unmodifiableList .
        processSelectPartList();

        this.asSQL();
        this.modifierList = Collections.unmodifiableList(this.modifierList);
        this.selectPartList = Collections.unmodifiableList(this.selectPartList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.groupExpList = Collections.unmodifiableList(this.groupExpList);
        this.havingList = Collections.unmodifiableList(this.havingList);
        this.sortExpList = Collections.unmodifiableList(this.sortExpList);


        doAsSelect();

        this.prepared = true;
        return this;
    }


    /*################################## blow InnerQueryAble method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.modifierList;
    }

    @Override
    public final List<SelectPart> selectPartList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.selectPartList;
    }


    @Override
    public final List<IPredicate> predicateList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.predicateList;
    }

    @Override
    public final List<Expression<?>> groupExpList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.groupExpList;
    }

    @Override
    public final List<IPredicate> havingList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.havingList;
    }

    @Override
    public final List<Expression<?>> sortExpList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.sortExpList;
    }

    @Override
    public final int offset() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.offset;
    }

    @Override
    public final int rowCount() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.rowCount;
    }

    @Override
    public final LockMode lockMode() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.lockMode;
    }

    @Override
    public final void clear() {
        super.beforeClear(NOT_PREPARED_MSG);

        this.modifierList = null;
        this.selectPartList = null;
        this.predicateList = null;
        this.groupExpList = null;

        this.havingList = null;
        this.sortExpList = null;
        this.lockMode = null;

        this.doClear();
    }

    /*################################## blow package method ##################################*/

    @Override
    final boolean prepared() {
        return this.prepared;
    }


    final void doSelect(@Nullable Distinct distinct, List<SelectPart> selectPartList) {
        Assert.state(this.modifierList.isEmpty() && this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList.add(distinct);
        }
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        this.selectPartList.addAll(selectPartList);
    }

    final void doSelect(List<SQLModifier> modifierList, List<SelectPart> selectPartList) {
        Assert.state(this.modifierList.isEmpty() && this.selectPartList.isEmpty(), "select clause ended.");
        this.modifierList.addAll(modifierList);
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        this.selectPartList.addAll(selectPartList);
    }

    final void doSelect(@Nullable Distinct distinct, SelectionGroup selectionGroup) {
        Assert.state(this.modifierList.isEmpty() && this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList.add(distinct);
        }
        this.selectPartList.add(selectionGroup);
    }

    final void doSelect(List<SQLModifier> modifierList, SelectionGroup selectionGroup) {
        Assert.state(this.modifierList.isEmpty() && this.selectPartList.isEmpty(), "select clause ended.");
        this.modifierList.addAll(modifierList);
        this.selectPartList.add(selectionGroup);
    }

    final void doSelectBySelection(List<SQLModifier> modifierList, List<Selection> selectionList) {
        Assert.state(this.modifierList.isEmpty() && this.selectPartList.isEmpty(), "select clause ended.");
        this.modifierList.addAll(modifierList);
        this.selectPartList.addAll(selectionList);
    }





    /*################################## blow package template method ##################################*/


    abstract void doAsSelect();

    /**
     * @see #clear()
     */
    abstract void doClear();

    /*################################## blow private method ##################################*/


    /**
     * <ol>
     *     <li> process {@link FieldMeta} in {@link #selectPartList}</li>
     *     <li> process {@link SubQuerySelectGroup} in {@link #selectPartList}</li>
     * </ol>
     */
    private void processSelectPartList() {

        Map<TableMeta<?>, List<Selection>> tableFieldListMap = new HashMap<>();
        Map<String, SubQuerySelectGroup> subQuerySelectGroupMap = new LinkedHashMap<>();

        // 1. find FieldMata/SubQuerySelectGroup from selectPart as tableSelectionMap/subQuerySelectGroupMap.
        for (Iterator<SelectPart> iterator = this.selectPartList.iterator(); iterator.hasNext(); ) {
            SelectPart selectPart = iterator.next();

            if (selectPart instanceof FieldMeta) {
                // process fieldMeta
                processSelectFieldMeta((FieldMeta<?, ?>) selectPart, tableFieldListMap);
                // remove FieldMeta from selectPartList.
                iterator.remove();
            } else if (selectPart instanceof SubQuerySelectGroup) {
                SubQuerySelectGroup group = (SubQuerySelectGroup) selectPart;
                subQuerySelectGroupMap.put(group.tableAlias(), group);
            }

        }

        // 2. find table alias to create SelectionGroup .
        for (TableWrapper tableWrapper : this.immutableTableWrapperList()) {
            TableAble tableAble = tableWrapper.tableAble();

            if (tableAble instanceof TableMeta) {

                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                List<Selection> fieldMetaList = tableFieldListMap.remove(tableMeta);

                if (!CollectionUtils.isEmpty(fieldMetaList)) {
                    // create SelectGroup for alias table and add to selectPartList.
                    this.selectPartList.add(SQLS.fieldGroup(tableWrapper.alias(), fieldMetaList));
                }

            } else if (tableAble instanceof SubQuery) {
                SubQuerySelectGroup group = subQuerySelectGroupMap.remove(tableWrapper.alias());
                if (group != null) {
                    // finish SubQuerySelectGroup
                    group.finish((SubQuery) tableAble);
                }
            }
        }

        // 3. assert tableFieldListMap and subQuerySelectGroupMap all is empty.
        if (!tableFieldListMap.isEmpty()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "the table of FieldMeta not found form criteria context,please check from clause.");
        }
        if (!subQuerySelectGroupMap.isEmpty()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "SelectGroup of SubQuery[%s] no found from criteria context,please check from clause.");
        }

    }




    /*################################## blow inner class ##################################*/


}
