package io.army.reactive.executor;

import io.army.reactive.Closeable;
import reactor.core.publisher.Mono;


public interface StmtExecutorFactory extends Closeable {

    Mono<MetaExecutor> metaExecutor();

    Mono<LocalStmtExecutor> localStmtExecutor();

    Mono<RmStmtExecutor> rmStmtExecutor();

}
