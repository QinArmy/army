package io.army.schema;


import io.army.meta.*;
import io.army.sqltype.DataType;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class ArmySchemaComparer implements _SchemaComparer {

    final ServerMeta serverMeta;


    ArmySchemaComparer(ServerMeta serverMeta) {
        this.serverMeta = serverMeta;
    }

    @Override
    public final _SchemaResult compare(SchemaInfo schemaInfo, SchemaMeta schemaMeta,
                                       Collection<TableMeta<?>> tableMetas) {
        if (compareSchema(schemaInfo, schemaMeta)) {
            String m = String.format("_SchemaInfo[%s,%s] and %s not match,serverMeta[%s].",
                    schemaInfo.catalog(), schemaInfo.schema(), schemaMeta, this.serverMeta);
            throw new IllegalArgumentException(m);
        }

        final Map<String, TableInfo> tableInfoMap = schemaInfo.tableMap();

        final _TableResult.Builder builder = _TableResult.builder();
        final List<TableMeta<?>> newTableList = _Collections.arrayList();
        final List<_TableResult> tableResultList = _Collections.arrayList();
        final boolean supportTableComment = this.supportTableComment();

        TableInfo tableInfo;
        for (TableMeta<?> table : tableMetas) {
            tableInfo = tableInfoMap.get(table.tableName());
            if (tableInfo == null) {
                newTableList.add(table);
                continue;
            }
            builder.table(table);
            if (supportTableComment) {
                builder.comment(!table.comment().equals(tableInfo.comment()));
            }

            compareColumns(tableInfo, table, builder);
            compareIndex(tableInfo, (TableMeta<?>) table, builder);
            tableResultList.add(builder.buildAndClear());
        }
        return new SchemaResult(schemaMeta.catalog(), schemaMeta.schema(), newTableList, tableResultList);
    }

    /**
     * @return true : schema isn't match.
     */
    abstract boolean compareSchema(SchemaInfo schemaInfo, SchemaMeta schemaMeta);

    /**
     * @return true : sql type definition is different.
     */
    abstract boolean compareSqlType(ColumnInfo columnInfo, FieldMeta<?> field, DataType dataType);

    /**
     * @return true : default expression definition is different.
     */
    abstract boolean compareDefault(ColumnInfo columnInfo, FieldMeta<?> field, DataType sqlType);

    /**
     * @return true : support column comment.
     */
    abstract boolean supportColumnComment();

    abstract boolean supportTableComment();

    abstract String primaryKeyName(TableMeta<?> table);


    private void compareColumns(TableInfo tableInfo, TableMeta<?> table, _TableResult.Builder tableBuilder) {

        final Map<String, ColumnInfo> columnMap = tableInfo.columnMap();
        final _FieldResult.Builder builder = _FieldResult.builder();
        final ServerMeta serverMeta = this.serverMeta;
        final boolean supportColumnComment = this.supportColumnComment();

        ColumnInfo column;
        DataType dataType;
        Boolean nullable;
        for (FieldMeta<?> field : table.fieldList()) {
            column = columnMap.get(field.columnName());
            if (column == null) {
                tableBuilder.appendNewColumn(field);
                continue;
            }
            dataType = field.mappingType().map(serverMeta);
            builder.field(field)
                    .sqlType(compareSqlType(column, field, dataType))
                    .defaultExp(compareDefault(column, field, dataType));
            nullable = column.nullable();
            if (nullable != null) {
                builder.nullable(nullable != field.nullable());
            }
            if (supportColumnComment) {
                builder.comment(!field.comment().equals(column.comment()));
            }
            if (builder.hasDifference()) {
                tableBuilder.appendFieldResult(builder.build());
            }
            builder.clear();

        }// for


    }

    private <T> void compareIndex(final TableInfo tableInfo, final TableMeta<T> table,
                                  final _TableResult.Builder tableBuilder) {

        final Map<String, IndexInfo> indexMap = tableInfo.indexMap();
        IndexInfo indexInfo;
        String indexName;
        List<IndexFieldMeta<T>> indexFieldList;
        List<String> columnList;
        List<Boolean> ascList;
        for (IndexMeta<T> index : table.indexList()) {
            if (index.isPrimaryKey()) {
                continue;
            }
            indexName = index.name();
            indexInfo = indexMap.get(indexName);
            if (indexInfo == null) {
                tableBuilder.appendNewIndex(indexName);
                continue;
            }
            if (indexInfo.unique() != index.isUnique()) {
                tableBuilder.appendChangeIndex(indexName);
                continue;
            }
            indexFieldList = index.fieldList();
            columnList = indexInfo.columnList();

            final int fieldSize = indexFieldList.size();
            if (columnList.size() != fieldSize) {
                tableBuilder.appendChangeIndex(indexName);
                continue;
            }
            ascList = indexInfo.ascList();
            for (int i = 0; i < fieldSize; i++) {
                IndexFieldMeta<T> field = indexFieldList.get(i);
                if (!columnList.contains(field.columnName())) {
                    tableBuilder.appendChangeIndex(indexName);
                    break;
                }
                Boolean fieldAsc = field.fieldAsc();
                Boolean columnAsc = ascList.get(i);
                if (fieldAsc == null || columnAsc == null) {
                    continue;
                }
                if (!fieldAsc.equals(columnAsc)) {
                    tableBuilder.appendChangeIndex(indexName);
                    break;
                }

            }//inner for

        }

    }


    private static final class SchemaResult implements _SchemaResult {

        private final String catalog;

        private final String schema;

        private final List<TableMeta<?>> newTableList;

        private final List<_TableResult> tableResultList;

        private SchemaResult(@Nullable String catalog, @Nullable String schema
                , List<TableMeta<?>> newTableList, List<_TableResult> tableResultList) {
            this.catalog = catalog;
            this.schema = schema;
            this.newTableList = _Collections.unmodifiableList(newTableList);
            this.tableResultList = _Collections.unmodifiableList(tableResultList);
        }

        @Override
        public String catalog() {
            return this.catalog;
        }

        @Override
        public String schema() {
            return this.schema;
        }

        @Override
        public List<TableMeta<?>> dropTableList() {
            return Collections.emptyList();
        }

        @Override
        public List<TableMeta<?>> newTableList() {
            return this.newTableList;
        }

        @Override
        public List<_TableResult> changeTableList() {
            return this.tableResultList;
        }

    }


}
