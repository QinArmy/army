package io.army.boot.migratioin;

import java.util.Map;
import java.util.StringJoiner;

class IndexInfo {

    private final TableInfo table;

    private final String name;

    private final boolean unique;

    private Map<String,IndexColumnInfo> columnMap;

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
        return columnMap;
    }


    @Override
    public String toString() {

        StringJoiner columnsJoiner = new StringJoiner(", ", "(", ")");
        for (IndexColumnInfo column : columnMap.values()) {
            columnsJoiner.add(column.name());
        }
        return new StringJoiner(", ", "[", "]")
                .add("table=" + table.name())
                .add("name='" + name + "'")
                .add("unique=" + unique)
                .add("columnMap=" + columnsJoiner.toString())
                .toString();

    }
}
