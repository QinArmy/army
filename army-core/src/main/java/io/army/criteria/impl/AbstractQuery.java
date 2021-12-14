package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._SortPart;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractQuery<Q extends Query, C> extends AbstractSQLDebug implements Query, _Query {

    protected final C criteria;

    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList = new ArrayList<>();

    private List<TableWrapperImpl> tableWrapperList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<_SortPart> groupByList;

    private List<_Predicate> havingList;

    private List<_SortPart> orderByList;

    private int offset = -1;

    private int rowCount = -1;

    private boolean ableRouteClause;

    private boolean ableOnClause;

    private boolean prepared;

    AbstractQuery(C criteria) {
        Assert.notNull(criteria, "criteria required");
        this.criteria = criteria;
    }

    @Override
    public final boolean requiredBrackets() {
        return !CollectionUtils.isEmpty(this.orderByList)
                || this.offset > -1
                || this.rowCount > -1
                || hasLockClause()
                ;
    }


    @SuppressWarnings("unchecked")
    public final Q asQuery() {
        if (this.prepared) {
            return (Q) this;
        }
        processSelectPartList(this.selectPartList, this.tableWrapperList);

        Assert.state(!this.selectPartList.isEmpty(), "no select list clause.");
        Assert.state(!this.tableWrapperList.isEmpty(), "no from clause.");

        this.modifierList = asUnmodifiableList(this.modifierList);
        this.selectPartList = Collections.unmodifiableList(this.selectPartList);
        this.tableWrapperList = Collections.unmodifiableList(this.tableWrapperList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.groupByList = asUnmodifiableList(this.groupByList);
        this.havingList = asUnmodifiableList(this.havingList);
        this.orderByList = asUnmodifiableList(this.orderByList);

        internalAsSelect();

        this.prepared = true;
        return (Q) this;
    }

    @Override
    public final void prepared() {
        Assert.prepared(this.prepared);
    }

    @Override
    public final void clear() {
        Assert.nonPrepared(this.prepared);
        this.modifierList = null;
        this.selectPartList = null;
        this.tableWrapperList = null;
        this.predicateList = null;

        this.groupByList = null;
        this.havingList = null;
        this.orderByList = null;

        this.offset = -1;
        this.rowCount = -1;
        internalClear();

        this.prepared = false;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final List<SelectPart> selectPartList() {
        return this.selectPartList;
    }

    @Override
    public final List<? extends TableWrapper> tableWrapperList() {
        return this.tableWrapperList;
    }

    @Override
    public final List<_Predicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public final List<_SortPart> groupPartList() {
        return this.groupByList;
    }

    @Override
    public final List<_Predicate> havingList() {
        return this.havingList;
    }

    @Override
    public final List<_SortPart> orderPartList() {
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

    final <S extends SelectPart> void doSelectClause(@Nullable Distinct distinct, List<S> selectPartList) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList = Collections.singletonList(distinct);
        }
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        if (this instanceof ColumnSubQuery && selectPartList.size() != 1) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        this.selectPartList.addAll(selectPartList);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelectClause(List<M> modifierList, List<S> selectPartList) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");

        this.modifierList = new ArrayList<>(modifierList);
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        if (this instanceof ColumnSubQuery && selectPartList.size() != 1) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        this.selectPartList.addAll(selectPartList);
    }

    final <S extends SelectPart> void doSelectClause(@Nullable Distinct distinct, S... selectParts) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList = Collections.singletonList(distinct);
        }
        if (this instanceof ColumnSubQuery && (selectParts.length != 1 || !(selectParts[0] instanceof Selection))) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        Collections.addAll(this.selectPartList, selectParts);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelectClause(List<M> modifierList, S selectPart) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        this.modifierList = new ArrayList<>(modifierList);
        if (this instanceof ColumnSubQuery && !(selectPart instanceof Selection)) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        this.selectPartList.add(selectPart);
    }


    final void doOnClause(List<IPredicate> predicateList) {
        if (this.ableOnClause) {
            Assert.notEmpty(predicateList, "predicateList required");
            Assert.state(!this.tableWrapperList.isEmpty(), "no form/join clause.");

            TableWrapperImpl tableWrapper = this.tableWrapperList.get(this.tableWrapperList.size() - 1);
            tableWrapper.addOnPredicateList(predicateList);
            this.ableOnClause = false;
        }
    }

    final void addTable(TableMeta<?> tableMeta, String tableAlias, JoinType joinType) {
        addTableAble(createTableWrapper(tableMeta, tableAlias, joinType));
    }

    final void addSubQuery(SubQuery subQuery, String subQueryAlias, JoinType joinType) {
        addTableAble(createTableWrapper(subQuery, subQueryAlias, joinType));
    }


    final void ifAddTable(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias, JoinType joinType) {
        if (predicate.test(this.criteria)) {
            addTableAble(createTableWrapper(tableMeta, tableAlias, joinType));
        } else {
            this.ableOnClause = false;
            this.ableRouteClause = false;
            onNotAddTable();
        }
    }

    final void ifAddSubQuery(Function<C, SubQuery> function, String subQueryAlias, JoinType joinType) {
        SubQuery subQuery = function.apply(this.criteria);
        if (subQuery == null) {
            this.ableOnClause = false;
            onNotAddDerivedTable();
        } else {
            addTableAble(createTableWrapper(subQuery, subQueryAlias, joinType));
        }
    }


    final void doRouteClause(int databaseIndex, int tableIndex) {
        if (this.ableRouteClause) {
            TableWrapperImpl tableWrapper = this.tableWrapperList.get(this.tableWrapperList.size() - 1);
            tableWrapper.route(databaseIndex, tableIndex);
            this.ableRouteClause = false;
        }
    }

    void doCheckTableAble(TableWrapper wrapper) {
        throw new IllegalArgumentException(String.format("tableAble[%s] isn't TableMeta or SubQuery."
                , wrapper.alias()));
    }

    final void addPredicate(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
    }

    final void addPredicateList(List<IPredicate> predicateList) {
        CriteriaUtils.addPredicates(predicateList, this.predicateList);
    }

    final void addGroupBy(SortPart sortPart) {
        if (this.groupByList == null) {
            this.groupByList = new ArrayList<>(1);
        }
        this.groupByList.add((_SortPart) sortPart);
    }

    final void addGroupByList(List<SortPart> sortPartList) {
        if (!CollectionUtils.isEmpty(sortPartList)) {
            List<_SortPart> groupByList = this.groupByList;
            if (groupByList == null) {
                groupByList = new ArrayList<>(sortPartList.size());
                this.groupByList = groupByList;
            }
            CriteriaUtils.addSortParts(sortPartList, groupByList);
        }
    }

    final void addHaving(IPredicate predicate) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            if (this.havingList == null) {
                this.havingList = new ArrayList<>(1);
            }
            this.havingList.add((_Predicate) predicate);
        }

    }

    final void addHavingList(List<IPredicate> predicateList) {
        if (!CollectionUtils.isEmpty(this.groupByList) && !predicateList.isEmpty()) {
            List<_Predicate> havingList = this.havingList;
            if (havingList == null) {
                havingList = new ArrayList<>(predicateList.size());
                this.havingList = havingList;
            }
            CriteriaUtils.addPredicates(predicateList, havingList);
        }
    }

    final void addOrderBy(SortPart sortPart) {
        if (this.orderByList == null) {
            this.orderByList = new ArrayList<>(1);
        }
        this.orderByList.add((_SortPart) sortPart);
    }

    final void addOrderByList(final List<SortPart> sortPartList) {
        if (!sortPartList.isEmpty()) {
            List<_SortPart> orderByList = this.orderByList;
            if (orderByList == null) {
                orderByList = new ArrayList<>(sortPartList.size());
                this.orderByList = orderByList;
            }
            CriteriaUtils.addSortParts(sortPartList, orderByList);
        }
    }

    final void doLimit(int offset, int rowCount) {
        if (this instanceof RowSubQuery && rowCount > -1 && rowCount != 1) {
            throw new IllegalArgumentException("RowSubQuery limit clause rowCount only one.");
        }
        this.offset = offset;
        this.rowCount = rowCount;
    }

    final TableWrapperImpl lastTableWrapper() {
        Assert.state(!this.tableWrapperList.isEmpty(), "tableWrapperList is empty.");

        TableWrapperImpl tableWrapper = this.tableWrapperList.get(this.tableWrapperList.size() - 1);
        Assert.state(tableWrapper.getClass() != TableWrapperImpl.class
                , "tableWrapper isn't sub class of TableWrapperImpl");
        return tableWrapper;
    }

    TableWrapperImpl createTableWrapper(TableAble tableAble, String alias, JoinType joinType) {
        return new TableWrapperImpl(tableAble, alias, joinType);
    }

    void onNotAddDerivedTable() {

    }

    void onNotAddTable() {

    }

    /*################################## blow package template method ##################################*/

    abstract void onAddTable(TableMeta<?> table, String tableAlias);

    abstract void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    abstract void internalAsSelect();

    abstract void internalClear();

    abstract boolean hasLockClause();


    /**
     *
     */
    private void addTableAble(TableWrapperImpl wrapper) {

        if (wrapper.jointType == JoinType.NONE) {
            Assert.state(this.tableWrapperList.isEmpty(), "from clause ended.");
        } else {
            Assert.state(!this.tableWrapperList.isEmpty(), "no from clause.");
        }

        this.tableWrapperList.add(wrapper);
        this.ableOnClause = true;
        if (wrapper.tableAble instanceof TableMeta) {
            onAddTable((TableMeta<?>) wrapper.tableAble, wrapper.alias);
            this.ableRouteClause = true;
        } else if (wrapper.tableAble instanceof SubQuery) {
            onAddSubQuery((SubQuery) wrapper.tableAble, wrapper.alias);
        } else {
            doCheckTableAble(wrapper);
        }

    }


    /**
     * <ol>
     *     <li> process {@link FieldMeta} in {@code selectPartList}</li>
     *     <li> process {@link SubQuerySelectionGroup} in {@code selectPartList}</li>
     * </ol>
     */
    static void processSelectPartList(List<SelectPart> selectPartList, List<TableWrapperImpl> tableWrapperList) {

        Map<String, SubQuerySelectionGroup> subQuerySelectGroupMap = new LinkedHashMap<>();

        Map<String, TableSelectionGroup> tableSelectGroupMap = new LinkedHashMap<>();

        // 1. find TableSelectionGroup/SubQuerySelectGroup from selectPart as tableSelectGroupMap/subQuerySelectGroupMap.
        for (SelectPart selectPart : selectPartList) {
            if (selectPart instanceof SubQuerySelectionGroup) {
                SubQuerySelectionGroup group = (SubQuerySelectionGroup) selectPart;
                if (subQuerySelectGroupMap.putIfAbsent(group.tableAlias(), group) != null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "derived group[%s] duplication"
                            , group.tableAlias());
                }
            } else if (selectPart instanceof TableSelectionGroup) {
                TableSelectionGroup group = (TableSelectionGroup) selectPart;
                if (tableSelectGroupMap.putIfAbsent(group.tableAlias(), group) != null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "group[%s] duplication"
                            , group.tableAlias());
                }
            }
        }

        // 2. find table alias to create SelectionGroup .
        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if (tableAble instanceof SubQuery) {
                SubQuerySelectionGroup group = subQuerySelectGroupMap.remove(tableWrapper.alias());
                if (group != null) {
                    // finish SubQuerySelectGroup
                    group.finish((SubQuery) tableAble, tableWrapper.alias());
                }
            } else if (tableAble instanceof TableMeta) {
                tableSelectGroupMap.remove(tableWrapper.alias());
            }
        }

        // 3. assert tableFieldListMap and subQuerySelectGroupMap all is empty.
        if (!subQuerySelectGroupMap.isEmpty()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "SelectionGroup of SubQueries[%s] no found from criteria context,please check from clause."
                    , subQuerySelectGroupMap.keySet());
        }
        if (!tableSelectGroupMap.isEmpty()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "SelectionGroup of Tables[%s] no found from criteria context,please check from clause."
                    , tableSelectGroupMap.keySet());
        }

    }


}
