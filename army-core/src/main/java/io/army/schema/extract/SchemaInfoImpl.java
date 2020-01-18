package io.army.schema.extract;

import java.util.List;
import java.util.StringJoiner;

class SchemaInfoImpl implements SchemaInfo {

    private final String catalog;

    private final String schema;

    private List<TableInfo> tables;


    SchemaInfoImpl(String catalog, String schema) {
        this.catalog = catalog;
        this.schema = schema;
    }

    @Override
    public String catalog() {
        return catalog;
    }

    @Override
    public String name() {
        return schema;
    }

    @Override
    public List<TableInfo> tables() {
        return tables;
    }

    public SchemaInfoImpl setTables(List<TableInfo> tables) {
        this.tables = tables;
        return this;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", catalog + "." + schema + "[", "]");
        for (TableInfo table : tables) {
            joiner.add(table.name());
        }
        return joiner.toString();
    }
}
