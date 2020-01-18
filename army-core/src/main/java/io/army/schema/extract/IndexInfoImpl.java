package io.army.schema.extract;

import java.util.List;
import java.util.StringJoiner;

class IndexInfoImpl implements IndexInfo {

    private final TableInfo table;

    private final String name;

    private final boolean unique;

    private List<IndexColumnInfo> columns;

    public IndexInfoImpl(TableInfo table, String name, boolean unique) {
        this.table = table;
        this.name = name;
        this.unique = unique;
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
    public boolean unique() {
        return unique;
    }

    void columns(List<IndexColumnInfo> columns) {
        this.columns = columns;
    }

    @Override
    public List<IndexColumnInfo> columns() {
        return columns;
    }

    @Override
    public String toString() {

        StringJoiner columnsJoiner = new StringJoiner(", ", "(", ")");
        for (IndexColumnInfo column : columns) {
            columnsJoiner.add(column.name());
        }
        return new StringJoiner(", ", "[", "]")
                .add("table=" + table.name())
                .add("name='" + name + "'")
                .add("unique=" + unique)
                .add("columns=" + columnsJoiner.toString())
                .toString();

    }
}
