package io.army.boot.migratioin;

import io.army.dialect.DDLSQLExecuteException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

interface DDLSQLExecutor {

    void executeDDL(int databaseIndex, List<Map<String, List<String>>> shardingDdlList, Connection conn)
            throws DDLSQLExecuteException;

    static DDLSQLExecutor build() {
        return new BatchDDLSQLExecutor();
    }

}
