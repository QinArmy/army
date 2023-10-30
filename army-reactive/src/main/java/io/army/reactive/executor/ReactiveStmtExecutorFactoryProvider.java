package io.army.reactive.executor;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;

import javax.annotation.Nullable;

import io.army.meta.ServerMeta;
import io.army.session.executor.StmtExecutorFactoryProviderSpec;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * <p>This interface representing the provider of {@link ReactiveStmtExecutorFactory}.
 * <p>This interface extends {@link StmtExecutorFactoryProviderSpec} and
 * This interface have overridden following methods :
 * <ul>
 *     <li>{@link #createServerMeta(Dialect, Function)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 * for reactor.
 *
 * @since 1.0
 */
public interface ReactiveStmtExecutorFactoryProvider extends StmtExecutorFactoryProviderSpec {

    @Override
    Mono<ServerMeta> createServerMeta(Dialect useDialect, @Nullable Function<String, Database> func);


    @Override
    Mono<ReactiveStmtExecutorFactory> createFactory(ExecutorEnv env);


}
