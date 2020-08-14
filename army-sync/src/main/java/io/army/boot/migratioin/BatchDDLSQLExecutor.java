package io.army.boot.migratioin;

import io.army.ErrorCode;
import io.army.dialect.DDLSQLExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

final class BatchDDLSQLExecutor implements DDLSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(BatchDDLSQLExecutor.class);


    BatchDDLSQLExecutor() {
    }

    @Override
    public final void executeDDL(int databaseIndex, List<Map<String, List<String>>> shardingDdlList, Connection conn)
            throws DDLSQLExecuteException {

        final int size = shardingDdlList.size();
        for (int i = 0; i < size; i++) {
            doExecuteDDL(databaseIndex, i, shardingDdlList.get(i), conn);
        }
    }


    /*################################## blow private method ##################################*/

    private void doExecuteDDL(int databaseIndex, int shardingIndex, Map<String, List<String>> tableDdlMap
            , Connection conn) {

        try (Statement statement = conn.createStatement()) {
            StringBuilder builder = new StringBuilder();
            int sqlCount = 0;
            for (Map.Entry<String, List<String>> e : tableDdlMap.entrySet()) {

                for (String ddl : e.getValue()) {
                    statement.addBatch(ddl);
                    builder.append(ddl)
                            .append(";\n");
                }
                builder.append("\n\n");
                sqlCount += e.getValue().size();
            }
            LOG.info("army will execute database[{}] sharding[{}] {} ddl(s):\n\n{}"
                    , databaseIndex, shardingIndex, sqlCount, builder);
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DDLSQLExecuteException(ErrorCode.DDL_EXECUTE_ERROR, e, e.getMessage());
        }
    }

}
