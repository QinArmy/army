package io.army.reactive.executor;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import io.army.session.executor.ExecutorFactoryProvider;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing the provider of {@link ReactiveExecutorFactory}.
 * <p>This interface extends {@link ExecutorFactoryProvider} and
 * This interface have overridden following methods :
 * <ul>
 *     <li>{@link #createServerMeta(Dialect, Function)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 * for reactor.
 *
 * @since 1.0
 */
public interface ReactiveExecutorFactoryProvider extends ExecutorFactoryProvider {

    @Override
    Mono<ServerMeta> createServerMeta(Dialect useDialect, @Nullable Function<String, Database> func);


    @Override
    Mono<ReactiveExecutorFactory> createFactory(ExecutorEnv env);


}
