package io.army.boot.migratioin;

import io.army.GenericRmSessionFactory;
import io.army.criteria.MetaException;
import io.army.dialect.DDLSQLExecuteException;
import io.army.dialect.Dialect;
import io.army.meta.IndexMeta;
import io.army.schema.SchemaInfoException;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.sql.Connection;
import java.util.*;

final class SyncMetaMigratorImpl implements SyncMetaMigrator {

    SyncMetaMigratorImpl() {
    }

    @Override
    public final void migrate(Connection conn, GenericRmSessionFactory sessionFactory)
            throws SchemaExtractException, SchemaInfoException, MetaException, DDLSQLExecuteException {
        //1.extract schema meta from database
        SchemaInfo schemaInfo;
        schemaInfo = SchemaExtractor.build(conn)
                .extract(null);
        // 2. compare TableMeta and schema meta from database.
        List<List<Migration>> shardingList;
        shardingList = MetaSchemaComparator.build(sessionFactory.actualDatabase())
                .compare(schemaInfo, sessionFactory);
        // 3. create ddl by compare result
        List<Map<String, List<String>>> shardingDdlList;
        shardingDdlList = createDdlForShardingList(shardingList, sessionFactory.dialect());
        // 4. execute ddl
        DDLSQLExecutor.build()
                .executeDDL(sessionFactory.databaseIndex(), shardingDdlList, conn);
        // clear ddl cache.
        sessionFactory.dialect().clearForDDL();
    }


    /*################################## blow private method ##################################*/

    private static List<Map<String, List<String>>> createDdlForShardingList(List<List<Migration>> shardingList
            , Dialect dialect) {
        List<Map<String, List<String>>> ddlList = new ArrayList<>(shardingList.size());
        for (List<Migration> migrationList : shardingList) {
            ddlList.add(createDdlForOneSharding(migrationList, dialect));
        }
        return Collections.unmodifiableList(ddlList);
    }

    private static Map<String, List<String>> createDdlForOneSharding(List<Migration> migrationList, Dialect dialect) {
        Map<String, List<String>> map = new HashMap<>();
        for (Migration migration : migrationList) {

            final String tableName = migration.actualTableName();

            Assert.isTrue(!map.containsKey(tableName), "migrationList error");

            List<String> sqlList = new ArrayList<>();

            if (migration.newTable()) {
                // invoke  dialect generate  SQL
                sqlList.addAll(dialect.createTable(migration.table(), migration.tableSuffix()));
            } else {
                // invoke  dialect generate DML SQL
                createDdlForTable(migration, dialect, sqlList);
            }
            map.put(tableName, Collections.unmodifiableList(sqlList));
        }
        return Collections.unmodifiableMap(map);
    }

    private static void createDdlForTable(Migration migration, Dialect dialect, List<String> sqlList) {
        // 1. add column if need
        if (!CollectionUtils.isEmpty(migration.columnsToAdd())) {
            sqlList.addAll(dialect.addColumn(migration.table(), migration.tableSuffix(), migration.columnsToAdd()));
        }
        // 2. alter column if need
        if (!CollectionUtils.isEmpty(migration.columnsToChange())) {
            sqlList.addAll(dialect.changeColumn(migration.table(), migration.tableSuffix(), migration.columnsToChange()));
        }
        // 3. drop index if need
        if (!CollectionUtils.isEmpty(migration.indexesToDrop())) {
            sqlList.addAll(dialect.dropIndex(migration.table(), migration.tableSuffix(), migration.indexesToDrop()));
        }
        // 4. add index if need
        if (!CollectionUtils.isEmpty(migration.indexesToAdd())) {
            sqlList.addAll(dialect.addIndex(migration.table(), migration.tableSuffix(), migration.indexesToAdd()));
        }
        // 5. alter index if need
        if (!CollectionUtils.isEmpty(migration.indexesToAlter())) {
            List<String> dropList = new ArrayList<>(migration.indexesToAlter().size());
            for (IndexMeta<?> indexMeta : migration.indexesToAlter()) {
                dropList.add(indexMeta.name());
            }
            // 5-1. first drop index
            sqlList.addAll(dialect.dropIndex(migration.table(), migration.tableSuffix(), dropList));
            // 5-2. then add index
            sqlList.addAll(dialect.addIndex(migration.table(), migration.tableSuffix(), migration.indexesToAlter()));
        }

    }
}
