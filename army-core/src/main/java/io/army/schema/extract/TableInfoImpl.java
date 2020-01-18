package io.army.schema.extract;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.StringJoiner;

class TableInfoImpl implements TableInfo {

    @JsonIgnore
    private final SchemaInfo schema;

    private final String name;

    private String comment;

    private List<ColumnInfo> columns;

    private List<IndexInfo> indexes;

    private boolean physicalTable;

     TableInfoImpl(SchemaInfo schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    @Override
    public SchemaInfo schema() {
        return schema;
    }

    @Override
    public String name() {
        return name;
    }


    @Override
    public String comment() {
        return comment;
    }

    public TableInfoImpl setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public List<ColumnInfo> columns() {
        return columns;
    }

    public TableInfoImpl setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
        return this;
    }

    @Override
    public List<IndexInfo> indexes() {
        return indexes;
    }

    public TableInfoImpl setIndexes(List<IndexInfo> indexes) {
        this.indexes = indexes;
        return this;
    }

    public boolean physicalTable() {
        return physicalTable;
    }

    public TableInfoImpl setPhysicalTable(boolean physicalTable) {
        this.physicalTable = physicalTable;
        return this;
    }

    @Override
    public String toString() {

        StringJoiner columnsJoiner = new StringJoiner(", ", "(", ")");
        for (ColumnInfo column : columns) {
            columnsJoiner.add(column.getName());
        }
        StringJoiner indexJoiner = new StringJoiner(", ", "(", ")");
        for (IndexInfo index : indexes) {
            columnsJoiner.add(index.name());
        }
        return new StringJoiner(", ",  "[", "]")
                .add("catalog=" + schema.catalog())
                .add("schema=" + schema.name())
                .add("name='" + name + "'")
                .add("comment='" + comment + "'")
                .add("columns=" + columnsJoiner.toString())
                .add("indexes=" + indexJoiner.toString())
                .add("physicalTable=" + physicalTable)
                .toString();
    }
}
