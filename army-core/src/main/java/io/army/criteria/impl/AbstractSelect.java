package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerGeneralBaseQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.*;

abstract class AbstractSelect extends AbstractSQLDebug implements Select, Select.SelectAble, InnerGeneralBaseQuery {

    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList = new ArrayList<>();

    private List<TableWrapperImpl> tableWrapperList = new ArrayList<>();

    private boolean prepared;

    public final Select asSelect() {
        if (this.prepared) {
            return this;
        }
        processSelectPartList(this.selectPartList, this.tableWrapperList);

        Assert.state(!this.selectPartList.isEmpty(), "no select list clause.");
        Assert.state(!this.tableWrapperList.isEmpty(), "no from clause.");

        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        } else {
            this.modifierList = Collections.unmodifiableList(this.modifierList);
        }

        this.selectPartList = Collections.unmodifiableList(this.selectPartList);
        this.tableWrapperList = Collections.unmodifiableList(this.tableWrapperList);
        internalAsSelect();

        this.prepared = true;
        return this;
    }

    @Override
    public final boolean prepared() {
        return this.prepared;
    }

    @Override
    public final void clear() {
        if (!this.prepared) {
            return;
        }
        this.modifierList = null;
        this.selectPartList = null;
        this.tableWrapperList = null;
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


    final <S extends SelectPart> void doSelect(@Nullable Distinct distinct, List<S> selectPartList) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList = Collections.singletonList(distinct);
        }
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        this.selectPartList.addAll(selectPartList);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelect(List<M> modifierList, List<S> selectPartList) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");

        this.modifierList = new ArrayList<>(modifierList);
        Assert.notEmpty(selectPartList, "select clause must have SelectPart.");
        this.selectPartList.addAll(selectPartList);
    }

    final <S extends SelectPart> void doSelectClause(@Nullable Distinct distinct, S selectPart) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        if (distinct != null) {
            this.modifierList = Collections.singletonList(distinct);
        }
        this.selectPartList.add(selectPart);
    }

    final <M extends SQLModifier, S extends SelectPart> void doSelect(List<M> modifierList, S selectPart) {
        Assert.state(this.selectPartList.isEmpty(), "select clause ended.");
        this.modifierList = new ArrayList<>(modifierList);
        this.selectPartList.add(selectPart);
    }

    /**
     *
     */
    final void addTableAble(TableWrapperImpl wrapper) {

        if (wrapper.jointType == JoinType.NONE) {
            Assert.state(this.tableWrapperList.isEmpty(), "from clause ended.");
        } else {
            Assert.state(!this.tableWrapperList.isEmpty(), "no from clause.");
        }

        if (wrapper.tableAble instanceof TableMeta) {
            onAddTable((TableMeta<?>) wrapper.tableAble, wrapper.alias);
        } else if (wrapper.tableAble instanceof SubQuery) {
            onAddSubQuery((SubQuery) wrapper.tableAble, wrapper.alias);
        } else {
            doCheckTableAble(wrapper);
        }
        this.tableWrapperList.add(wrapper);
    }


    final void doOnClause(List<IPredicate> predicateList) {
        Assert.notEmpty(predicateList, "predicateList required");
        Assert.state(!this.tableWrapperList.isEmpty(), "no form/join clause.");

        TableWrapperImpl tableWrapper = this.tableWrapperList.get(this.tableWrapperList.size() - 1);
        tableWrapper.addOnPredicateList(predicateList);
    }

    void doCheckTableAble(TableWrapper wrapper) {
        throw new IllegalArgumentException(String.format("tableAble[%s] isn't TableMeta or SubQuery."
                , wrapper.alias()));
    }

    /*################################## blow package template method ##################################*/

    abstract void onAddTable(TableMeta<?> table, String tableAlias);

    abstract void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    abstract void internalAsSelect();

    abstract void internalClear();

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
