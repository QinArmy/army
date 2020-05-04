package io.army.boot;

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

    private final Connection connection;

    BatchDDLSQLExecutor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void executeDDL(Map<String, List<String>> tableDDLMap) throws DDLSQLExecuteException {
        try (Statement statement = connection.createStatement()) {
            StringBuilder builder = new StringBuilder();
            int sqlCount = 0;
            for (Map.Entry<String, List<String>> e : tableDDLMap.entrySet()) {

                for (String ddl : e.getValue()) {
                    statement.addBatch(ddl);
                    builder.append(ddl)
                            .append(";\n")
                    ;
                }
                builder.append("\n\n");
                sqlCount += e.getValue().size();
            }
            LOG.info("army will start {} ddl(s):\n\n{}", sqlCount, builder);
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DDLSQLExecuteException(ErrorCode.DDL_EXECUTE_ERROR, e, e.getMessage());
        }

    }

    /*################################## blow private method ##################################*/


}
