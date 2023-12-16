package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import io.army.session.Option;
import io.army.session.executor.ExecutorFactory;
import reactor.core.publisher.Mono;

import java.util.function.Function;


/**
 * <p>This interface representing {@link ReactiveStmtExecutor} factory.
 * <p>This interface extends {@link ExecutorFactory} and have overridden following methods:
 * <ul>
 *     <li>{@link #metaExecutor(Function)}</li>
 *     <li>{@link #localExecutor(String, boolean, Function)}</li>
 *     <li>{@link #rmExecutor(String, boolean, Function)}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface ReactiveExecutorFactory extends ExecutorFactory, ReactiveCloseable {


    @Override
    Mono<ReactiveMetaExecutor> metaExecutor(Function<Option<?>, ?> func);

    @Override
    Mono<ReactiveLocalExecutor> localExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> func);

    @Override
    Mono<ReactiveRmExecutor> rmExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> func);

}
