package io.army.boot.migratioin;

import java.util.Map;
import java.util.StringJoiner;

final class SchemaInfo {

    private final String catalog;

    private final String schema;

    private Map<String, TableInfo> tableMap;


    SchemaInfo(String catalog, String schema) {
        this.catalog = catalog;
        this.schema = schema;
    }

    public String catalog() {
        return catalog;
    }

    public String name() {
        return schema;
    }

    /**
     * @return a unmodifiable map
     */
    Map<String, TableInfo> tableMap() {
        return tableMap;
    }

    void tableMap(Map<String, TableInfo> tableMap) {
        this.tableMap = tableMap;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", catalog + "." + schema + "[", "]");
        for (String name : tableMap.keySet()) {
            joiner.add(name);
        }
        return joiner.toString();
    }
}
