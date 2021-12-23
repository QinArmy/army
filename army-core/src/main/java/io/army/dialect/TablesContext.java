package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.TablePart;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.impl.inner._SingleDml;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TablesContext {

    public static final TablesContext EMPTY = new TablesContext();

    public static final String PARENT_ALIAS_PREFIX = "p_of_";

    public static final String CHILD_ALIAS_PREFIX = "c_of_";

    public static TablesContext singleTable(_SingleDml singleTable, boolean parent, String primaryRouteSuffix) {
        TableMeta<?> tableMeta = singleTable.table();
        if (parent) {
            tableMeta = ((ChildTableMeta<?>) tableMeta).parentMeta();
        }
        String tableAlias = singleTable.tableAlias();

        return new TablesContext(
                Collections.singletonMap(tableMeta, 1)
                , Collections.singletonMap(tableAlias, tableMeta)
                , Collections.singletonMap(tableMeta, tableAlias)
                , Collections.singletonMap(tableAlias, 0)
                , primaryRouteSuffix
        );
    }

    public static TablesContext multiTable(List<? extends TableWrapper> tableWrapperList, String primaryRouteSuffix) {
        Map<TableMeta<?>, Integer> tableCountMap = new HashMap<>();
        Map<String, TableMeta<?>> aliasTableMap = new HashMap<>();
        Map<String, Integer> tableIndexMap = new HashMap<>();

        for (TableWrapper tableWrapper : tableWrapperList) {
            TablePart tableAble = tableWrapper.tableAble();
            if (tableAble instanceof TableMeta) {
                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                Integer count = tableCountMap.computeIfAbsent(tableMeta, key -> 0);
                tableCountMap.replace(tableMeta, count, count + 1);
                if (aliasTableMap.putIfAbsent(tableWrapper.alias(), tableMeta) != null) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] alias[%s] duplication."
                            , tableMeta, tableWrapper.alias());
                }
                tableIndexMap.put(tableWrapper.alias(), tableWrapper.tableIndex());
            }
        }

        Map<TableMeta<?>, String> tableAliasMap = new HashMap<>();

        final Integer one = 1;
        for (TableWrapper tableWrapper : tableWrapperList) {
            TablePart tableAble = tableWrapper.tableAble();
            if (tableAble instanceof TableMeta) {
                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                if (one.equals(tableCountMap.get(tableMeta))) {

                    tableAliasMap.putIfAbsent(tableMeta, tableWrapper.alias());
                }
            }

        }
        return new TablesContext(
                Collections.unmodifiableMap(tableCountMap)
                , Collections.unmodifiableMap(aliasTableMap)
                , Collections.unmodifiableMap(tableAliasMap)
                , Collections.unmodifiableMap(tableIndexMap)
                , primaryRouteSuffix
        );
    }

    final Map<TableMeta<?>, Integer> tableCountMap;

    final Map<String, TableMeta<?>> aliasTableMap;

    final Map<TableMeta<?>, String> tableAliasMap;

    final Map<String, Integer> tableIndexMap;

    final String primaryRouteSuffix;


    private TablesContext(Map<TableMeta<?>, Integer> tableCountMap, Map<String, TableMeta<?>> aliasTableMap
            , Map<TableMeta<?>, String> tableAliasMap, Map<String, Integer> tableIndexMap
            , String primaryRouteSuffix) {
        this.tableCountMap = tableCountMap;
        this.aliasTableMap = aliasTableMap;
        this.tableAliasMap = tableAliasMap;
        this.tableIndexMap = tableIndexMap;

        this.primaryRouteSuffix = primaryRouteSuffix;

    }

    private TablesContext() {
        this.tableCountMap = Collections.emptyMap();
        this.aliasTableMap = Collections.emptyMap();
        this.tableAliasMap = Collections.emptyMap();
        this.tableIndexMap = Collections.emptyMap();

        this.primaryRouteSuffix = "";
    }

    boolean single() {
        return this.tableCountMap.size() == 1;
    }

    TableMeta<?> singleTable() {
        if (!single()) {
            throw new IllegalStateException("not single table");
        }
        return this.tableCountMap.keySet().iterator().next();
    }
}
