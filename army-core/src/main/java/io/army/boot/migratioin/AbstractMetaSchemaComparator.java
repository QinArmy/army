package io.army.boot.migratioin;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;
import io.army.util.Assert;
import io.army.util.ObjectUtils;
import org.springframework.lang.Nullable;

import java.sql.Connection;
import java.util.*;

public abstract class AbstractMetaSchemaComparator implements MetaSchemaComparator{

    @Override
    public final List<Migration> compare(Collection<TableMeta<?>> tableMetas, SchemaInfo schemaInfo, Dialect dialect)
            throws SchemaInfoException, MetaException {
        Assert.notNull(tableMetas,"tableMetas required");
        Assert.notNull(schemaInfo,"schemaInfo required");

        final Map<String, TableInfo> tableInfoMap = schemaInfo.tableMap();

        List<Migration> migrationList = new ArrayList<>();

        for (TableMeta<?> tableMeta : tableMetas) {
            TableInfo tableInfo = tableInfoMap.get(tableMeta.tableName());
            if (tableInfo == null) {
                // will create table
                migrationList.add(new MigrationImpl(tableMeta, true));

            } else {
                Migration migration = doMigrateTable(tableMeta, tableInfo);
                if (migration != null) {
                    // will alter table
                    migrationList.add(migration);
                }
            }
        }
        return Collections.unmodifiableList(migrationList);
    }

    /*################################## blow abstract method ##################################*/

    protected abstract boolean precisionOrScaleAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException;

    protected abstract boolean defaultValueAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException,MetaException;

    /*################################## blow private method ##################################*/

    @Nullable
    private Migration doMigrateTable(TableMeta<?> tableMeta, TableInfo tableInfo) {
        Assert.state(tableMeta.tableName().equals(tableInfo.name()),
                () -> String.format("TableMeta[%s] and TableInfo[%s] not match",
                        tableMeta.tableName(), tableInfo.name()));

        MigrationImpl migration = new MigrationImpl(tableMeta, false);

        // column migration
        migrateColumnIfNeed(tableMeta,tableInfo,migration);
        // index migration
        migrateIndexIfNeed(tableMeta,tableInfo,migration);

        if(migration.needAlter()){
            migration.makeFinal();
        }else {
            migration = null;
        }
        return migration;
    }


    private void migrateColumnIfNeed(TableMeta<?> tableMeta, TableInfo tableInfo,MigrationImpl migration){
        final Map<String, ColumnInfo> columnInfoMap = tableInfo.columnMap();

        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            ColumnInfo columnInfo = columnInfoMap.get(fieldMeta.fieldName());
            if (columnInfo == null) {
                // alter table add column
                migration.addColumnToAdd(fieldMeta);
            } else if (needAlterColumn(fieldMeta, columnInfo)) {
                // alter table alter column
                migration.addColumnToModify(fieldMeta);
            }
        }

    }

    private void migrateIndexIfNeed(TableMeta<?> tableMeta, TableInfo tableInfo,MigrationImpl migration){
        final Map<String, IndexInfo> indexInfoMap = tableInfo.indexMap();
        // index migration
        Set<String> indexNameSet = new HashSet<>();
        for (IndexMeta<?> indexMeta : tableMeta.indexCollection()) {
            IndexInfo indexInfo = indexInfoMap.get(indexMeta.name());
            if (indexInfo == null) {
                // alter table add index
                migration.addIndexToAdd(indexMeta);
            } else if(needAlterIndex(indexMeta,indexInfo)){
                // alter table alter index
                migration.addIndexToModify(indexMeta);
            }
            indexNameSet.add(indexMeta.name());
        }

        // find indexes than not in index meta
        Set<String> indexNameFromSchema = new HashSet<>(indexInfoMap.keySet());
        indexNameFromSchema.removeAll(indexNameSet);
        for (String indexName: indexNameFromSchema) {
            migration.addIndexToDrop(indexName);
        }
    }

    private boolean needAlterIndex(IndexMeta<?> indexMeta, IndexInfo indexInfo) throws SchemaInfoException{
        boolean need = false;
        if(indexMeta.isUnique() != indexInfo.unique()){
            need = true;
        }else if(indexMeta.fieldList().size() != indexInfo.columnMap().size()){
            need = true;
        }else {
            Map<String,IndexColumnInfo> columnInfoMap = indexInfo.columnMap();
            for (IndexFieldMeta<?,?> indexFieldMeta : indexMeta.fieldList()) {
                IndexColumnInfo info = columnInfoMap.get(indexFieldMeta.fieldName());

                if(info == null){
                    // index column not exists
                    need = true ;
                    break;
                }else {
                    Boolean fieldAsc = indexFieldMeta.fieldAsc();
                    if(fieldAsc != null && fieldAsc != info.asc()){
                        // index column order not match
                        need = true;
                        break;
                    }
                }

            }
        }
        return need;
    }


    private boolean needAlterColumn(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo) throws SchemaInfoException {
        // 1. data type
        assertJdbcTypeMatch(fieldMeta, columnInfo);
        boolean needAlter = false;

        if (precisionOrScaleAlter(fieldMeta, columnInfo)) {
            // 2. columnSize ,scale
            needAlter = true;
        } else if (defaultValueAlter(fieldMeta, columnInfo)) {
            // 3. default value
            needAlter = true;
        } else if (!columnInfo.nonNull()) {
            // 4. not null
            needAlter = true;
        } else if (!TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                && !ObjectUtils.nullSafeEquals(fieldMeta.comment(), columnInfo.comment())) {
            // 5. comment
            needAlter = true;
        }
        return needAlter;
    }

    private void assertJdbcTypeMatch(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException {
        if (fieldMeta.jdbcType() != columnInfo.jdbcType()) {
            throw new SchemaInfoException(ErrorCode.SQL_TYPE_NOT_MATCH
                    , "FieldMeta[%s] and ColumnInfo[%s] SQL type not match"
                    , fieldMeta.fieldName(), columnInfo.name());
        }
    }


}
