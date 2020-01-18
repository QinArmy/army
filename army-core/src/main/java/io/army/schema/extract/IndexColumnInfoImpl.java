package io.army.schema.extract;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.StringJoiner;

class IndexColumnInfoImpl  implements IndexColumnInfo {

    private final TableInfo table;

    @JsonIgnore
    private final IndexInfo index;

    private final String name;

    private final boolean asc;

    public IndexColumnInfoImpl(TableInfo table, IndexInfo index, String name, boolean asc) {
        this.table = table;
        this.index = index;
        this.name = name;
        this.asc = asc;
    }

    @Override
    public TableInfo table() {
        return table;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public IndexInfo index() {
        return index;
    }

    @Override
    public boolean asc() {
        return asc;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("table=" + table.name())
                .add("indexes=" + index.name())
                .add("name='" + name + "'")
                .add("asc=" + asc)
                .toString();
    }
}
