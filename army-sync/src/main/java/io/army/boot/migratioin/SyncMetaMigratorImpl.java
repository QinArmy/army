package io.army.boot.migratioin;

import io.army.dialect.DDLSQLExecuteException;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.session.GenericRmSessionFactory;

import java.sql.Connection;

final class SyncMetaMigratorImpl implements SyncMetaMigrator {

    SyncMetaMigratorImpl() {
    }

    @Override
    public final void migrate(Connection conn, GenericRmSessionFactory sessionFactory)
            throws SchemaExtractException, SchemaInfoException, MetaException, DDLSQLExecuteException {
//        //1.extract schema meta from database
//        SchemaInfo schemaInfo;
//        schemaInfo = SchemaExtractor.build(conn)
//                .extract(null);
//        // 2. compare TableMeta and schema meta from database.
//        List<List<MigrationMember>> shardingList;
//        shardingList = MetaSchemaComparator.build(sessionFactory)
//                .compare(schemaInfo);
//
//        if (!shardingList.isEmpty()) {
//            // 3. create ddl by compare result
//            List<Map<String, List<String>>> shardingDdlList;
//            shardingDdlList = createDdlForShardingList(shardingList, sessionFactory.dialect());
//            // 4. execute ddl
//            DDLSQLExecutor.build()
//                    .executeDDL(sessionFactory.databaseIndex(), shardingDdlList, conn);
//            // clear ddl cache.
//            sessionFactory.dialect().clearForDDL();
//        }
    }


    /*################################## blow private method ##################################*/


}
