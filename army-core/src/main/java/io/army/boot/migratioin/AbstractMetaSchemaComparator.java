package io.army.boot.migratioin;

import io.army.ErrorCode;
import io.army.GenericRmSessionFactory;
import io.army.criteria.MetaException;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;
import io.army.sharding.RouteUtils;
import io.army.util.Assert;
import io.army.util.ObjectUtils;
import io.army.util.StringUtils;

import java.util.*;

abstract class AbstractMetaSchemaComparator implements MetaSchemaComparator {

    @Override
    public final List<List<Migration>> compare(SchemaInfo schemaInfo, GenericRmSessionFactory sessionFactory)
            throws SchemaInfoException, MetaException {

        final Map<String, TableInfo> tableInfoMap = schemaInfo.tableMap();

        final int tableCount = sessionFactory.tableCountOfSharding();
        List<List<Migration>> shardingList = new ArrayList<>(tableCount);
        final Collection<TableMeta<?>> tableMetas = sessionFactory.tableMetaMap().values();
        for (int i = 0; i < tableCount; i++) {
            //1. obtain table suffix
            final String tableSuffix = RouteUtils.convertToSuffix(tableCount, i);
            List<Migration> migrationList = new ArrayList<>(tableMetas.size());

            for (TableMeta<?> tableMeta : tableMetas) {
                //2. obtain actual table name
                String actualTableName = tableMeta.tableName();
                if (tableSuffix != null) {
                    actualTableName += tableSuffix;
                }
                //3. create MigrationImpl
                TableInfo tableInfo = tableInfoMap.get(StringUtils.toLowerCase(actualTableName));
                if (tableInfo == null) {
                    // will create table
                    migrationList.add(new MigrationImpl(tableMeta, tableSuffix, true));

                } else {
                    Migration migration = doMigrateTable(tableMeta, tableSuffix, tableInfo);
                    if (migration != null) {
                        // will alter tableMeta
                        migrationList.add(migration);
                    }
                }
            }
            //4. add migrationList to shardingList
            shardingList.add(Collections.unmodifiableList(migrationList));
        }
        return Collections.unmodifiableList(shardingList);
    }


    /*################################## blow abstract method ##################################*/

    protected abstract boolean precisionOrScaleAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException;

    protected abstract boolean defaultValueAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException;

    /*################################## blow private method ##################################*/

    @Nullable
    private Migration doMigrateTable(TableMeta<?> tableMeta, @Nullable String tableSuffix, TableInfo tableInfo) {
        Assert.state(tableMeta.tableName().equals(tableInfo.name()),
                () -> String.format("TableMeta[%s] then TableInfo[%s] not match",
                        tableMeta.tableName(), tableInfo.name()));

        MigrationImpl migration = new MigrationImpl(tableMeta, tableSuffix, false);

        // column migration
        migrateColumnIfNeed(tableMeta, tableInfo, migration);
        // index migration
        migrateIndexIfNeed(tableMeta, tableInfo, migration);

        if (migration.needAlter()) {
            migration.makeFinal();
        } else {
            migration = null;
        }
        return migration;
    }


    private void migrateColumnIfNeed(TableMeta<?> tableMeta, TableInfo tableInfo, MigrationImpl migration) {
        final Map<String, ColumnInfo> columnInfoMap = tableInfo.columnMap();

        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            // make key lower case
            ColumnInfo columnInfo = columnInfoMap.get(StringUtils.toLowerCase(fieldMeta.fieldName()));
            if (columnInfo == null) {
                // alter tableMeta add column
                migration.addColumnToAdd(fieldMeta);
            } else if (needAlterColumn(fieldMeta, columnInfo)) {
                // alter tableMeta alter column
                migration.addColumnToModify(fieldMeta);
            }
        }

    }

    private void migrateIndexIfNeed(TableMeta<?> tableMeta, TableInfo tableInfo, MigrationImpl migration) {
        final Map<String, IndexInfo> indexInfoMap = tableInfo.indexMap();
        // index migration
        Set<String> indexNameSet = new HashSet<>();

        for (IndexMeta<?> indexMeta : tableMeta.indexCollection()) {
            String indexName = StringUtils.toLowerCase(indexMeta.name());
            IndexInfo indexInfo = indexInfoMap.get(indexName);
            if (indexInfo == null) {
                if (!indexMeta.isPrimaryKey()) {
                    // alter tableMeta add index
                    migration.addIndexToAdd(indexMeta);
                }
            } else if (needAlterIndex(indexMeta, indexInfo)) {
                // alter tableMeta alter index
                migration.addIndexToModify(indexMeta);
            }
            indexNameSet.add(indexName);
        }

        // find indexes than not in index meta
        Set<String> indexNameFromSchema = new HashSet<>(indexInfoMap.keySet());
        indexNameFromSchema.removeAll(indexNameSet);
        for (String indexName : indexNameFromSchema) {
            if (primaryKeyIndex(indexInfoMap.get(indexName))) {
                continue;
            }
            migration.addIndexToDrop(indexName);
        }
    }

    private boolean primaryKeyIndex(IndexInfo indexInfo) {
        boolean yes = false;
        if ("PRIMARY".equalsIgnoreCase(indexInfo.name())) {
            yes = true;
        } else if (indexInfo.unique()) {
            Map<String, IndexColumnInfo> indexColumnInfoMap = indexInfo.columnMap();
            yes = indexColumnInfoMap.size() == 1
                    && indexColumnInfoMap.containsKey(TableMeta.ID);
        }
        return yes;
    }


    private boolean needAlterIndex(IndexMeta<?> indexMeta, IndexInfo indexInfo) throws SchemaInfoException {
        if (primaryKeyIndex(indexInfo)) {
            return false;
        }
        boolean need;
        if (indexMeta.unique() != indexInfo.unique()) {
            need = true;
        } else if (indexMeta.fieldList().size() != indexInfo.columnMap().size()) {
            need = true;
        } else {
            need = indexOrderMatch(indexMeta, indexInfo);
        }
        return need;
    }

    private boolean indexOrderMatch(IndexMeta<?> indexMeta, IndexInfo indexInfo) {
        Map<String, IndexColumnInfo> columnInfoMap = indexInfo.columnMap();
        boolean need = false;
        for (IndexFieldMeta<?, ?> indexFieldMeta : indexMeta.fieldList()) {
            IndexColumnInfo info = columnInfoMap.get(StringUtils.toLowerCase(indexFieldMeta.fieldName()));

            if (info == null) {
                // index column not exists
                need = true;
                break;
            } else {
                Boolean fieldAsc = indexFieldMeta.fieldAsc();
                if (fieldAsc != null && fieldAsc != info.asc()) {
                    // index column asSort not match
                    need = true;
                    break;
                }
            }

        }
        return need;
    }


    private boolean needAlterColumn(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo) throws SchemaInfoException {
        // 1. data type, TODO zoro add dialect jdbc mapping
        assertJdbcTypeMatch(fieldMeta, columnInfo);
        boolean needAlter = false;

        if (precisionOrScaleAlter(fieldMeta, columnInfo)) {
            // 2. columnSize ,scale
            needAlter = true;
        } else if (defaultValueAlter(fieldMeta, columnInfo)) {
            // 3. default value
            needAlter = true;
        } else if (columnInfo.nullable() != fieldMeta.nullable()) {
            // 4. nullableKeyword
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
        if (fieldMeta.mappingMeta().jdbcType() != columnInfo.jdbcType()) {
            throw new SchemaInfoException(ErrorCode.SQL_TYPE_NOT_MATCH
                    , "FieldMeta[%s] then ColumnInfo[%s] SQL type not match"
                    , fieldMeta.fieldName(), columnInfo.name());
        }
    }


}
