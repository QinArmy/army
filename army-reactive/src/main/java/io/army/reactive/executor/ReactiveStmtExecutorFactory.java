package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import io.army.session.executor.StmtExecutorFactorySpec;
import reactor.core.publisher.Mono;


/**
 * <p>This interface representing {@link ReactiveStmtExecutor} factory.
 * <p>This interface extends {@link StmtExecutorFactorySpec} and have overridden following methods:
 * <ul>
 *     <li>{@link #metaExecutor(String)}</li>
 *     <li>{@link #localExecutor(String)}</li>
 *     <li>{@link #rmExecutor(String)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface ReactiveStmtExecutorFactory extends StmtExecutorFactorySpec, ReactiveCloseable {


    @Override
    Mono<ReactiveMetaExecutor> metaExecutor(String name);

    @Override
    Mono<ReactiveLocalStmtExecutor> localExecutor(String name);

    @Override
    Mono<ReactiveRmStmtExecutor> rmExecutor(String name);

}
