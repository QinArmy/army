package io.army.boot.migratioin;

import io.army.dialect.DDLSQLExecuteException;
import io.jdbd.StatelessSession;
import io.jdbd.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

final class ReactiveBatchDDLSQLExecutor implements ReactiveDDLSQLExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ReactiveBatchDDLSQLExecutor.class);


    @Override
    public Mono<Void> executeDDL(int databaseIndex, List<Map<String, List<String>>> shardingDdlList
            , StatelessSession session) throws DDLSQLExecuteException {
        return Flux.fromIterable(shardingDdlList)
                .index()
                .flatMap(tuple2 -> doExecuteDDL(databaseIndex, tuple2.getT1(), tuple2.getT2(), session))
                .then();
    }

    private Mono<Void> doExecuteDDL(int databaseIndex, long shardingIndex, Map<String, List<String>> tableDdlMap
            , StatelessSession session) {
        return session.createStatement()
                .map(statement -> addBatch(databaseIndex, shardingIndex, statement, tableDdlMap))
                // execute ddl(s)
                .flatMapMany(Statement::executeBatch)
                .then();
    }

    private Statement addBatch(int databaseIndex, long shardingIndex, Statement statement
            , Map<String, List<String>> tableDdlMap) {

        StringBuilder builder = new StringBuilder();
        int ddlCount = 0;
        for (List<String> ddlList : tableDdlMap.values()) {
            for (String ddl : ddlList) {
                statement.addBatch(ddl);
                builder.append(ddl)
                        .append(";\n");
            }
            builder.append("\n\n");
            ddlCount += ddlList.size();
        }

        LOG.info("army will execute database[{}] sharding[{}] {} ddl(s):\n\n{}"
                , databaseIndex, shardingIndex, ddlCount, builder);
        return statement;
    }


}
