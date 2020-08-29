package io.army.boot.migratioin;

import io.army.dialect.Dialect;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.*;

abstract class MigratorUtils {

    private MigratorUtils() {
        throw new UnsupportedOperationException();
    }

    static List<Map<String, List<String>>> createDdlForShardingList(List<List<Migration>> shardingList
            , Dialect dialect) {
        List<Map<String, List<String>>> ddlList = new ArrayList<>(shardingList.size());
        for (List<Migration> migrationList : shardingList) {
            ddlList.add(createDdlForOneSharding(migrationList, dialect));
        }
        return ddlList.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(ddlList);
    }

    /*################################## blow private method ##################################*/

    private static Map<String, List<String>> createDdlForOneSharding(List<Migration> migrationList
            , Dialect dialect) {
        Map<String, List<String>> map = new HashMap<>();
        for (Migration migration : migrationList) {

            final String tableName = obtainActualTableName(migration);

            Assert.isTrue(!map.containsKey(tableName), "migrationList error");

            List<String> sqlList = new ArrayList<>();

            if (migration instanceof Migration.TableMigration) {
                // invoke  dialect generate  SQL
                sqlList.addAll(dialect.createTable(migration.tableMeta(), migration.tableSuffix()));
            } else {
                Migration.MemberMigration memberMigration = (Migration.MemberMigration) migration;
                // invoke  dialect generate DML SQL
                createDdlForTable(memberMigration, dialect, sqlList);
                if (memberMigration.modifyTableComment()) {
                    sqlList.addAll(dialect.modifyTableComment(migration.tableMeta(), migration.tableSuffix()));
                }
            }
            map.put(tableName, Collections.unmodifiableList(sqlList));
        }
        return Collections.unmodifiableMap(map);
    }

    private static void createDdlForTable(Migration.MemberMigration migration, Dialect dialect, List<String> sqlList) {
        // 1. add column if need
        if (!CollectionUtils.isEmpty(migration.columnsToAdd())) {
            sqlList.addAll(dialect.addColumn(migration.tableMeta(), migration.tableSuffix(), migration.columnsToAdd()));
        }
        // 2. change column if need
        if (!CollectionUtils.isEmpty(migration.columnsToChange())) {
            sqlList.addAll(dialect.changeColumn(migration.tableMeta(), migration.tableSuffix()
                    , migration.columnsToChange()));
        }
        // 3. drop index if need
        if (!CollectionUtils.isEmpty(migration.indexesToDrop())) {
            sqlList.addAll(dialect.dropIndex(migration.tableMeta(), migration.tableSuffix()
                    , migration.indexesToDrop()));
        }
        // 4. add index if need
        if (!CollectionUtils.isEmpty(migration.indexesToAdd())) {
            sqlList.addAll(dialect.addIndex(migration.tableMeta(), migration.tableSuffix()
                    , migration.indexesToAdd()));
        }
        // 5. alter index if need
        if (!CollectionUtils.isEmpty(migration.indexesToAlter())) {
            List<IndexMeta<?>> indexesToAlter = migration.indexesToAlter();
            List<String> dropList = new ArrayList<>(indexesToAlter.size());
            for (IndexMeta<?> indexMeta : indexesToAlter) {
                dropList.add(indexMeta.name());
            }
            // 5-1. first drop index
            sqlList.addAll(dialect.dropIndex(migration.tableMeta(), migration.tableSuffix(), dropList));
            // 5-2. then add index
            sqlList.addAll(dialect.addIndex(migration.tableMeta(), migration.tableSuffix(), indexesToAlter));
        }
        //6. modify column  comment
        if (!CollectionUtils.isEmpty(migration.columnToModifyComment())) {
            for (FieldMeta<?, ?> fieldMeta : migration.columnToModifyComment()) {
                sqlList.addAll(
                        dialect.modifyColumnComment(fieldMeta, migration.tableSuffix())
                );
            }

        }

    }

    private static String obtainActualTableName(Migration migration) {
        String tableName = migration.tableMeta().tableName();
        String tableSuffix = migration.tableSuffix();
        if (tableSuffix != null) {
            tableName += tableSuffix;
        }
        return tableName;
    }
}
