package io.army.boot.migratioin;

import java.util.StringJoiner;

final class IndexColumnInfo {

    private final TableInfo table;

    private final IndexInfo index;

    private final String name;

    private final boolean asc;

    public IndexColumnInfo(TableInfo table, IndexInfo index, String name, boolean asc) {
        this.table = table;
        this.index = index;
        this.name = name;
        this.asc = asc;
    }

    public TableInfo table() {
        return table;
    }

    public String name() {
        return name;
    }


    public IndexInfo index() {
        return index;
    }


    public boolean asc() {
        return asc;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("tableMeta=" + table.name())
                .add("indexMap=" + index.name())
                .add("name='" + name + "'")
                .add("ascExp=" + asc)
                .toString();
    }
}
