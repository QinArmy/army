package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import reactor.core.publisher.Mono;


public interface StmtExecutorFactory extends ReactiveCloseable {

    Mono<MetaExecutor> metaExecutor();

    Mono<LocalStmtExecutor> localStmtExecutor();

    Mono<RmStmtExecutor> rmStmtExecutor();

}
