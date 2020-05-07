package io.army.dialect;

import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.Map;

public final class TableContext {

    public static final TableContext EMPTY = new TableContext();

    protected final Map<TableMeta<?>, Integer> tableCountMap;

    protected final Map<String, TableMeta<?>> aliasTableMap;

    protected final Map<TableMeta<?>, String> tableAliasMap;

    public TableContext(Map<TableMeta<?>, Integer> tableCountMap
            , Map<String, TableMeta<?>> aliasTableMap
            , Map<TableMeta<?>, String> tableAliasMap) {
        this.tableCountMap = Collections.unmodifiableMap(tableCountMap);
        this.aliasTableMap = Collections.unmodifiableMap(aliasTableMap);
        this.tableAliasMap = Collections.unmodifiableMap(tableAliasMap);
    }

    private TableContext() {
        this.tableCountMap = Collections.emptyMap();
        this.aliasTableMap = Collections.emptyMap();
        this.tableAliasMap = Collections.emptyMap();
    }
}
