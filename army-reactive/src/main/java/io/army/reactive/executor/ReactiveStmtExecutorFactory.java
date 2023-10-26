package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import io.army.session.executor.StmtExecutorFactory;
import reactor.core.publisher.Mono;


public interface ReactiveStmtExecutorFactory extends StmtExecutorFactory, ReactiveCloseable {


    Mono<MetaExecutor> metaExecutor();

    Mono<LocalStmtExecutor> localExecutor();

    Mono<RmStmtExecutor> rmExecutor();

}
