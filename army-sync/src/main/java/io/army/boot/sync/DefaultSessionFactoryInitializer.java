package io.army.boot.sync;

import io.army.ArmyRuntimeException;
import io.army.DataAccessException;
import io.army.ErrorCode;
import io.army.boot.migratioin.Meta2Schema;
import io.army.boot.migratioin.SchemaExtractor;
import io.army.boot.migratioin.SyncSchemaExtractorFactory;
import io.army.dialect.Dialect;
import io.army.meta.TableMeta;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

final class DefaultSessionFactoryInitializer implements SessionFactoryInitializer {

    private final Connection connection;

    private final Map<Class<?>, TableMeta<?>> tableMetaMap;

    private final Dialect dialect;

    DefaultSessionFactoryInitializer(Connection connection, Map<Class<?>, TableMeta<?>> tableMetaMap, Dialect dialect) {
        this.connection = connection;
        this.tableMetaMap = tableMetaMap;
        this.dialect = dialect;
    }

    @Override
    public void onStartup() {
        try {
            Map<String, List<String>> tableSqlMap;
            // 1. create schemaExtractor instance
            SchemaExtractor schemaExtractor = SyncSchemaExtractorFactory.build(connection);
            // 2. create DDL sql
            tableSqlMap = Meta2Schema.build().migrate(this.tableMetaMap.values(), schemaExtractor
                    , this.dialect);

            // 3. execute dml
            executeDDL(connection, tableSqlMap);
        } catch (ArmyRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }


    private void executeDDL(Connection conn, Map<String, List<String>> tableSqlMap) {
        if (tableSqlMap.isEmpty()) {
            return;
        }
        DDLSQLExecutor ddlsqlExecutor = new BatchDDLSQLExecutor(conn);
        ddlsqlExecutor.executeDDL(tableSqlMap);
    }


}
