package io.army.boot.migratioin;

import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfoException;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.sql.Connection;
import java.util.*;

class Meta2SchemaImpl implements Meta2Schema {

    @Override
    public Map<String, List<String>> migrate(Collection<TableMeta<?>> tableMetas, Connection connection
            , Dialect dialect) throws SchemaInfoException, MetaException {

        // 1. extract schema from database's current schema.
        SchemaInfo schemaInfo;
        schemaInfo = SchemaExtractor.newInstance().extractor(connection);
        // 2. compare meta and schema .
        List<Migration> migrationList;
        migrationList = MetaSchemaComparator.build(dialect.sqlDialect())
                .compare(tableMetas, schemaInfo, dialect);
        // 3. generate DDL(DML) SQL
        return generateSQL(migrationList, dialect);
    }

    /*################################## blow private method ##################################*/


    private Map<String, List<String>> generateSQL(List<Migration> migrationList, Dialect dialect) {
        Map<String, List<String>> map = new HashMap<>();
        for (Migration migration : migrationList) {

            String tableName = migration.table().tableName();

            Assert.isTrue(!map.containsKey(tableName),"migrationList error");

            List<String> sqlList = new ArrayList<>();

            if (migration.newTable()) {
                // invoke  dialect generate create SQL
                sqlList.addAll(dialect.tableDefinition(migration.table()));
            } else {
                // invoke  dialect generate DML SQL
                generateDML(migration,dialect,sqlList);
            }
            map.put(tableName,Collections.unmodifiableList(sqlList));
        }
        return Collections.unmodifiableMap(map);
    }

    private void generateDML(Migration migration,Dialect dialect,List<String> sqlList){
        // 1. add column if need
        if (!CollectionUtils.isEmpty(migration.columnsToAdd())) {
            sqlList.addAll(dialect.addColumn(migration.table(), migration.columnsToAdd()));
        }
        // 2. alter column if need
        if (!CollectionUtils.isEmpty(migration.columnsToChange())) {
            sqlList.addAll(dialect.changeColumn(migration.table(), migration.columnsToChange()));
        }
        // 3. add index if need
        if (!CollectionUtils.isEmpty(migration.indexesToAdd())) {
            sqlList.addAll(dialect.addIndex(migration.table(), migration.indexesToAdd()));
        }
        // 4. alter index if need
        if (!CollectionUtils.isEmpty(migration.indexesToAlter())) {
            List<String> dropList = new ArrayList<>(migration.indexesToAlter().size());
            for (IndexMeta<?> indexMeta : migration.indexesToAlter()) {
                dropList.add(indexMeta.name());
            }
            // 4.1 first drop index
            sqlList.addAll(dialect.dropIndex(migration.table(), dropList));
            // 4.2 and add index
            sqlList.addAll(dialect.addIndex(migration.table(), migration.indexesToAlter()));
        }
        // 5. drop index if need
        if (!CollectionUtils.isEmpty(migration.indexesToDrop())) {
            sqlList.addAll(dialect.dropIndex(migration.table(), migration.indexesToDrop()));
        }
    }



}
