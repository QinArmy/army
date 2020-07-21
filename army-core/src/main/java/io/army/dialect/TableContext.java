package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.TableAble;
import io.army.criteria.impl.inner.InnerSingleTableSQL;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TableContext {

    public static final TableContext EMPTY = new TableContext();

    public static TableContext singleTable(InnerSingleTableSQL singleTable, String primaryRouteSuffix) {
        TableMeta<?> tableMeta = singleTable.tableMeta();
        String tableAlias = singleTable.tableAlias();

        return new TableContext(
                Collections.singletonMap(tableMeta, 1)
                , Collections.singletonMap(tableAlias, tableMeta)
                , Collections.singletonMap(tableMeta, tableAlias)
                , Collections.singletonMap(tableAlias, singleTable.tableIndex())
                , primaryRouteSuffix
        );
    }

    public static TableContext multiTable(List<? extends TableWrapper> tableWrapperList, String primaryRouteSuffix) {
        Map<TableMeta<?>, Integer> tableCountMap = new HashMap<>();
        Map<String, TableMeta<?>> aliasTableMap = new HashMap<>();
        Map<String, Integer> tableIndexMap = new HashMap<>();

        for (TableWrapper tableWrapper : tableWrapperList) {
            TableAble tableAble = tableWrapper.tableAble();
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
            TableAble tableAble = tableWrapper.tableAble();
            if (tableAble instanceof TableMeta) {
                TableMeta<?> tableMeta = (TableMeta<?>) tableAble;
                if (one.equals(tableCountMap.get(tableMeta))) {

                    tableAliasMap.putIfAbsent(tableMeta, tableWrapper.alias());
                }
            }

        }
        return new TableContext(
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


    private TableContext(Map<TableMeta<?>, Integer> tableCountMap, Map<String, TableMeta<?>> aliasTableMap
            , Map<TableMeta<?>, String> tableAliasMap, Map<String, Integer> tableIndexMap, String primaryRouteSuffix) {
        this.tableCountMap = tableCountMap;
        this.aliasTableMap = aliasTableMap;
        this.tableAliasMap = tableAliasMap;
        this.tableIndexMap = tableIndexMap;

        this.primaryRouteSuffix = primaryRouteSuffix;

    }

    private TableContext() {
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
