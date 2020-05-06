package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerSQL;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;

/**
 * <ol>
 *     <li>invoke {@link #asSQL()} method in below method :
 *      <ul>
 *          <li>{@link Select.SelectAble#asSelect()}</li>
 *          <li>{@link SubQuery.SubQueryAble#asSubQuery()}</li>
 *          <li>{@link RowSubQuery.RowSubQueryAble#asSubQuery()} </li>
 *          <li>{@link ColumnSubQuery.ColumnSubQueryAble#asSubQuery()}</li>
 *          <li>{@link ScalarSubQuery.ScalarSubQueryAble#asSubQuery()}</li>
 *          <li>{@link Update.UpdateAble#asUpdate()}</li>
 *          <li>{@link Delete.DeleteAble#asDelete()}</li>
 *      </ul>
 *     </li>
 *     <li>invoke {@link #beforeClear(String)} in {@link #clear()}</li>
 * </ol>
 */
abstract class AbstractSQL extends AbstractSQLDebug implements QueryAble, InnerSQL {

    private List<TableWrapper> tableWrapperList = new ArrayList<>(tableWrapperCount());

    /*################################## blow cache props ##################################*/

    private Map<TableMeta<?>, Integer> tablePresentCountMap = new HashMap<>(tableWrapperCount() + 3);



    /*################################## blow InnerSQL method ##################################*/

    public final List<TableWrapper> tableWrapperList() {
        return this.tableWrapperList;
    }

    public final Map<TableMeta<?>, Integer> tablePresentCountMap() {
        return this.tablePresentCountMap;
    }

    @Override
    public void clear() {

    }

    /*################################## blow package method ##################################*/

    final void asSQL() {
        this.tableWrapperList = Collections.unmodifiableList(this.tableWrapperList);
        this.tablePresentCountMap = Collections.unmodifiableMap(this.tablePresentCountMap);
    }

    final int tableWrapperListSize() {
        return this.tableWrapperList.size();
    }

    final List<TableWrapper> immutableTableWrapperList() {
        if (prepared()) {
            return this.tableWrapperList;
        }
        return Collections.unmodifiableList(this.tableWrapperList);
    }

    /**
     *
     */
    final void processSelectFieldMeta(FieldMeta<?, ?> fieldMeta
            , Map<TableMeta<?>, List<Selection>> tableFieldListMap) {

        int refCount = this.tablePresentCountMap.getOrDefault(fieldMeta.tableMeta(), 0);

        switch (refCount) {
            case 0:
                String msg = "not found the table of FieldMeta[%s] from criteria context,please check from clause.";
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, msg, fieldMeta);
            case 1:
                List<Selection> fieldMetaList = tableFieldListMap.computeIfAbsent(fieldMeta.tableMeta()
                        , key -> new ArrayList<>());
                fieldMetaList.add(fieldMeta);
                break;
            default:
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "FieldMeta[%s] ambiguity,please check select clause and from clause.", fieldMeta);
        }
    }


    final void doOn(List<IPredicate> predicateList) {
        Assert.notEmpty(predicateList, "predicateList required");
        Assert.state(!this.tableWrapperList.isEmpty(), "no form/join clause.");

        TableWrapperImpl tableWrapper = (TableWrapperImpl) this.tableWrapperList.get(this.tableWrapperList.size() - 1);

        Assert.state(tableWrapper.onPredicateList.isEmpty()
                , () -> String.format("on clause of table[%s] ended.", tableWrapper.alias()));

        tableWrapper.onPredicateList.addAll(predicateList);
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
            TableMeta<?> tableMeta = (TableMeta<?>) wrapper.tableAble;
            int refCount = tablePresentCountMap.getOrDefault(tableMeta, 0);
            tablePresentCountMap.put(tableMeta, ++refCount);

            onAddTable(tableMeta, wrapper.alias);
        } else {
            doCheckTableAble(wrapper);
        }
        this.tableWrapperList.add(wrapper);
    }


    final void beforeClear(String msg) {
        Assert.state(prepared(), msg);

        this.tableWrapperList = null;
        this.tablePresentCountMap = null;
    }

    final TableWrapper lastTableWrapper() {
        Assert.state(!this.tableWrapperList.isEmpty(), "tableWrapperList is empty.");
        return this.tableWrapperList.get(this.tableWrapperList.size() - 1);
    }


    /**
     * <ol>
     *     <li> process {@link FieldMeta} in {@code selectPartList}</li>
     *     <li> process {@link SubQuerySelectGroup} in {@code selectPartList}</li>
     * </ol>
     */
    final void processSelectPartList(final List<SelectPart> selectPartList) {

        Map<TableMeta<?>, List<Selection>> tableFieldListMap = new HashMap<>();
        Map<String, SubQuerySelectGroup> subQuerySelectGroupMap = new LinkedHashMap<>();

        // 1. find FieldMata/SubQuerySelectGroup from selectPart as tableSelectionMap/subQuerySelectGroupMap.
        for (Iterator<SelectPart> iterator = selectPartList.iterator(); iterator.hasNext(); ) {
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
        for (TableWrapper tableWrapper : this.tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();

            if (tableAble instanceof TableMeta) {

                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                List<Selection> fieldMetaList = tableFieldListMap.remove(tableMeta);

                if (!CollectionUtils.isEmpty(fieldMetaList)) {
                    // create SelectGroup for alias table and add to selectPartList.
                    selectPartList.add(SQLS.fieldGroup(tableWrapper.alias(), fieldMetaList));
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


    /**
     * @see #tableWrapperList
     */
    int tableWrapperCount() {
        return 6;
    }


    /*################################## blow package template method ##################################*/


    void doCheckTableAble(TableWrapper wrapper) {
        throw new IllegalArgumentException(String.format("tableAble[%s] isn't TableMeta or SubQuery."
                , wrapper.alias()));
    }

    abstract boolean prepared();


    abstract void onAddTable(TableMeta<?> table, String tableAlias);

    abstract void onAddSubQuery(SubQuery subQuery, String subQueryAlias);


    /*################################## blow static inner class ##################################*/

    static class TableWrapperImpl implements TableWrapper {

        private final TableAble tableAble;

        private final String alias;

        private final SQLModifier jointType;

        final List<IPredicate> onPredicateList = new ArrayList<>();

        TableWrapperImpl(TableAble tableAble, String alias, JoinType jointType) {
            this.tableAble = tableAble;
            this.alias = alias;
            this.jointType = jointType;
        }

        public final TableAble tableAble() {
            return tableAble;
        }

        public final String alias() {
            return alias;
        }

        public final SQLModifier jointType() {
            return jointType;
        }

        public final List<IPredicate> onPredicateList() {
            return onPredicateList;
        }
    }


}
