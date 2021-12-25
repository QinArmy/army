package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._SortPart;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Assert;

import java.util.*;
import java.util.function.Function;

abstract class AbstractQuery<Q extends Query, C> extends AbstractSQLDebug implements Query, _Query {

    protected final C criteria;


    AbstractQuery(C criteria) {
        _Assert.notNull(criteria, "criteria required");
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
    public Q asQuery() {
        if (this.prepared) {
            return (Q) this;
        }
        processSelectPartList(this.selectPartList, this.tableBlockList);

        _Assert.state(!this.selectPartList.isEmpty(), "no select list clause.");
        _Assert.state(!this.tableBlockList.isEmpty(), "no from clause.");

        this.modifierList = asUnmodifiableList(this.modifierList);
        this.selectPartList = Collections.unmodifiableList(this.selectPartList);
        this.tableBlockList = Collections.unmodifiableList(this.tableBlockList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.groupByList = asUnmodifiableList(this.groupByList);
        this.havingList = asUnmodifiableList(this.havingList);
        this.orderByList = asUnmodifiableList(this.orderByList);

        internalAsSelect();

        this.prepared = true;
        return (Q) this;
    }

    @Override
    public void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public void clear() {
        _Assert.nonPrepared(this.prepared);
        this.modifierList = null;
        this.selectPartList = null;
        this.tableBlockList = null;
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
    public final List<? extends _TableBlock> tableWrapperList() {
        return this.tableBlockList;
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
        _Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList = Collections.singletonList(distinct);
        }
        _Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        if (this instanceof ColumnSubQuery && selectPartList.size() != 1) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        this.selectPartList.addAll(selectPartList);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelectClause(List<M> modifierList, List<S> selectPartList) {
        _Assert.state(this.selectPartList.isEmpty(), "select clause ended.");

        this.modifierList = new ArrayList<>(modifierList);
        _Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        if (this instanceof ColumnSubQuery && selectPartList.size() != 1) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        this.selectPartList.addAll(selectPartList);
    }

    final <S extends SelectPart> void doSelectClause(@Nullable Distinct distinct, S... selectParts) {
        _Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList = Collections.singletonList(distinct);
        }
        if (this instanceof ColumnSubQuery && (selectParts.length != 1 || !(selectParts[0] instanceof Selection))) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        Collections.addAll(this.selectPartList, selectParts);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelectClause(List<M> modifierList, S selectPart) {
        _Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        this.modifierList = new ArrayList<>(modifierList);
        if (this instanceof ColumnSubQuery && !(selectPart instanceof Selection)) {
            throw new IllegalArgumentException("ColumnSubQuery only one selection.");
        }
        this.selectPartList.add(selectPart);
    }


    void doCheckTableAble(TableBlock block) {
        throw new IllegalArgumentException(String.format("tableAble[%s] isn't TableMeta or SubQuery."
                , block.alias()));
    }

    final void addPredicate(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
    }

    final void addPredicateList(List<IPredicate> predicateList) {
        final List<_Predicate> predicates = new ArrayList<>(predicateList.size());
        for (IPredicate predicate : predicateList) {
            predicates.add((_Predicate) predicate);
        }
        this.predicateList = predicates;
    }

    final void addGroupBy(SortPart sortPart) {
        this.groupByList = Collections.singletonList(sortPart);
    }

    final void addGroupByList(final List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("group by clause sortPartList is empty.");
        }
        this.groupByList = new ArrayList<>(sortPartList);
    }

    final void addHaving(final IPredicate predicate) {
        final List<SortPart> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            this.havingList = Collections.singletonList((_Predicate) predicate);
        }

    }

    final void addHavingList(final Function<C, List<IPredicate>> function) {
        final List<SortPart> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            final List<IPredicate> predicateList;
            addHavingList(function.apply(this.criteria));
        }
    }

    final void addHavingList(final List<IPredicate> predicateList) {
        final List<SortPart> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            final List<_Predicate> list = new ArrayList<>(predicateList.size());
            for (IPredicate predicate : predicateList) {
                list.add((_Predicate) predicate);
            }
            this.havingList = list;
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

    final TableBlock lastTableBlock() {
        return this.tableBlockList.get(this.tableBlockList.size() - 1);
    }

    final TableBlock beforeBlock(final TableBlock block) {
        final int size = this.tableBlockList.size();
        final TableBlock last;
        last = this.tableBlockList.get(size - 1);
        if (block != last) {
            throw new IllegalArgumentException("block error");
        }
        return this.tableBlockList.get(size - 2);
    }

    TableBlockImpl createTableWrapper(TablePart tableAble, String alias, JoinType joinType) {
        return new TableBlockImpl(tableAble, alias, joinType);
    }

    void onNotAddDerivedTable() {

    }

    void onNotAddTable() {

    }

    static CriteriaException selectListClauseEmpty() {
        return new CriteriaException("selection list clause is empty.");
    }

    /*################################## blow package template method ##################################*/

    abstract void onAddTable(TableMeta<?> table, String tableAlias);

    abstract void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    abstract void internalAsSelect();

    abstract void internalClear();

    abstract boolean hasLockClause();





    /**
     * <ol>
     *     <li> process {@link FieldMeta} in {@code selectPartList}</li>
     *     <li> process {@link SubQuerySelectionGroup} in {@code selectPartList}</li>
     * </ol>
     */
    static void processSelectPartList(List<SelectPart> selectPartList, List<TableBlockImpl> tableWrapperList) {

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
        for (_TableBlock tableBlock : tableWrapperList) {
            TablePart tableAble = tableBlock.table();

            if (tableAble instanceof SubQuery) {
                SubQuerySelectionGroup group = subQuerySelectGroupMap.remove(tableBlock.alias());
                if (group != null) {
                    // finish SubQuerySelectGroup
                    group.finish((SubQuery) tableAble, tableBlock.alias());
                }
            } else if (tableAble instanceof TableMeta) {
                tableSelectGroupMap.remove(tableBlock.alias());
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

    static CriteriaException onClauseIsEmpty() {
        return new CriteriaException("on clause is empty");
    }

    static abstract class TableBlock implements _TableBlock {

        final TablePart tablePart;

        final String alias;

        final JoinType joinType;

        List<_Predicate> predicates;

        TableBlock(TablePart tablePart, String alias, JoinType joinType) {
            this.tablePart = tablePart;
            this.alias = alias;
            this.joinType = joinType;
        }

        @Override
        public final TablePart table() {
            return this.tablePart;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final SQLModifier jointType() {
            return this.joinType;
        }


    }


    static final class FromTableBlock extends TableBlock {

        FromTableBlock(TablePart tablePart, String alias) {
            super(tablePart, alias, JoinType.NONE);
        }

        @Override
        public List<_Predicate> predicates() {
            return Collections.emptyList();
        }

    }


}
