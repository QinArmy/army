package io.army.boot.migratioin;

import io.army.dialect.DDLSQLExecuteException;
import io.jdbd.StatelessSession;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

interface ReactiveDDLSQLExecutor {

    Mono<Void> executeDDL(int databaseIndex, List<Map<String, List<String>>> shardingDdlList, StatelessSession session)
            throws DDLSQLExecuteException;

    static ReactiveDDLSQLExecutor build() {
        return new ReactiveBatchDDLSQLExecutor();
    }

}
