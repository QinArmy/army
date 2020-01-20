package io.army.schema.migration;

import java.util.Map;
import java.util.StringJoiner;

public class TableInfo {

    private final SchemaInfo schema;

    private final String name;

    private String comment;

    private Map<String,ColumnInfo> columnMap;

    private Map<String,IndexInfo> indexMap;

    private boolean physicalTable;

     TableInfo(SchemaInfo schema, String name) {
        this.schema = schema;
        this.name = name;
    }


    public SchemaInfo schema() {
        return schema;
    }


    public String name() {
        return name;
    }



    public String comment() {
        return comment;
    }

    public TableInfo comment(String comment) {
        this.comment = comment;
        return this;
    }


    public Map<String,ColumnInfo> columnMap() {
        return columnMap;
    }

    public void columnMap(Map<String,ColumnInfo> columnMap) {
        this.columnMap = columnMap;
    }


    public Map<String,IndexInfo> indexMap() {
        return indexMap;
    }

    public void indexMap(Map<String,IndexInfo> indexMap) {
        this.indexMap = indexMap;
    }

    public boolean physicalTable() {
        return physicalTable;
    }

    public void physicalTable(boolean physicalTable) {
        this.physicalTable = physicalTable;
    }

    @Override
    public String toString() {

        StringJoiner columnsJoiner = new StringJoiner(", ", "(", ")");
        for (ColumnInfo column : columnMap.values()) {
            columnsJoiner.add(column.name());
        }
        StringJoiner indexJoiner = new StringJoiner(", ", "(", ")");
        for (IndexInfo index : indexMap.values()) {
            columnsJoiner.add(index.name());
        }
        return new StringJoiner(", ",  "[", "]")
                .add("catalog=" + schema.catalog())
                .add("schema=" + schema.name())
                .add("name='" + name + "'")
                .add("comment='" + comment + "'")
                .add("columnMap=" + columnsJoiner.toString())
                .add("indexMap=" + indexJoiner.toString())
                .add("physicalTable=" + physicalTable)
                .toString();
    }
}
