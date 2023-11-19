package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import io.army.session.executor.StmtExecutorFactorySpec;
import reactor.core.publisher.Mono;


/**
 * <p>This interface representing {@link ReactiveStmtExecutor} factory.
 * <p>This interface extends {@link StmtExecutorFactorySpec} and have overridden following methods:
 * <ul>
 *     <li>{@link #metaExecutor()}</li>
 *     <li>{@link #localExecutor(String, boolean)}</li>
 *     <li>{@link #rmExecutor(String, boolean)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface ReactiveStmtExecutorFactory extends StmtExecutorFactorySpec, ReactiveCloseable {


    @Override
    Mono<ReactiveMetaExecutor> metaExecutor();

    @Override
    Mono<ReactiveLocalStmtExecutor> localExecutor(String sessionName, boolean readOnly);

    @Override
    Mono<ReactiveRmStmtExecutor> rmExecutor(String sessionName, boolean readOnly);

}
