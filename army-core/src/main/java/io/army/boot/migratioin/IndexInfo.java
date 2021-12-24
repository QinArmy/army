package io.army.boot.migratioin;

import io.army.util._Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

final class IndexInfo {

    private final TableInfo table;

    private final String name;

    private final boolean unique;

    private Map<String, IndexColumnInfo> columnMap = new HashMap<>();

    public IndexInfo(TableInfo table, String name, boolean unique) {
        this.table = table;
        this.name = name;
        this.unique = unique;
    }


    public TableInfo table() {
        return table;
    }


    public String name() {
        return name;
    }


    public boolean unique() {
        return unique;
    }

    void columnMap(Map<String,IndexColumnInfo> columnMap) {
        this.columnMap = columnMap;
    }


    public Map<String,IndexColumnInfo> columnMap() {
        _Assert.state(this.columnMap != null, "columnMap state error.");
        return this.columnMap;
    }


    @Override
    public String toString() {

        StringJoiner columnsJoiner = new StringJoiner(", ", "(", ")");
        for (IndexColumnInfo column : columnMap.values()) {
            columnsJoiner.add(column.name());
        }
        return new StringJoiner(", ", "[", "]")
                .add("tableMeta=" + table.name())
                .add("name='" + name + "'")
                .add("unique=" + unique)
                .add("columnMap=" + columnsJoiner.toString())
                .toString();

    }
}
