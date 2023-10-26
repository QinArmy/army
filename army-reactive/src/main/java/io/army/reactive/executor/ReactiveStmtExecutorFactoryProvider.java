package io.army.reactive.executor;

import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import io.army.session.executor.StmtExecutorFactoryProviderSpec;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing the provider of {@link ReactiveStmtExecutorFactory}.
 * <p>This interface extends {@link StmtExecutorFactoryProviderSpec} and
 * This interface have overridden following methods :
 * <ul>
 *     <li>{@link #createServerMeta(Dialect)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 * for reactor.
 *
 * @since 1.0
 */
public interface ReactiveStmtExecutorFactoryProvider extends StmtExecutorFactoryProviderSpec {

    @Override
    Mono<ServerMeta> createServerMeta(Dialect useDialect);

    @Override
    Mono<ReactiveStmtExecutorFactory> createFactory(ExecutorEnv env);


}
