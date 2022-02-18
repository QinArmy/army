package io.army.schema;

import io.army.dialect.Dialect;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

abstract class AbstractSchemaComparer implements _SchemaComparer {


    Dialect dialect;

    @Override
    public final _SchemaResult compare(_SchemaInfo schemaInfo, SchemaMeta schemaMeta
            , Collection<TableMeta<?>> tableMetas) {
        if (!compareSchema(schemaInfo, schemaMeta)) {
            String m = String.format("_SchemaInfo[%s,%s] and %s not match,dialect[%s]."
                    , schemaInfo.catalog(), schemaInfo.schema(), schemaMeta, this.dialect);
            throw new IllegalArgumentException(m);
        }

        final Map<String, _TableInfo> tableInfoMap = schemaInfo.tableMap();
        _TableInfo tableInfo;
        final _TableResultBuilder builder = new _TableResultBuilder();
        final List<TableMeta<?>> newTableList = new ArrayList<>();
        for (TableMeta<?> table : tableMetas) {
            tableInfo = tableInfoMap.get(table.tableName());
            if (tableInfo == null) {
                newTableList.add(table);
                continue;
            }
            if (!table.comment().equals(tableInfo.comment())) {
                builder.comment();
            }
            compareColumns(tableInfo, table, builder);

        }
        return null;
    }


    abstract boolean compareSchema(_SchemaInfo schemaInfo, SchemaMeta schemaMeta);

    private void compareColumns(_TableInfo tableInfo, TableMeta<?> table, _TableResultBuilder builder) {
        _ColumnInfo column;
        final Map<String, _ColumnInfo> columnMap = tableInfo.columnMap();
        for (FieldMeta<?, ?> field : table.fieldList()) {
            column = columnMap.get(field.columnName());
            if (column == null) {
                builder.appendNewColumn(field);
                continue;
            }


        }// for


    }


}
