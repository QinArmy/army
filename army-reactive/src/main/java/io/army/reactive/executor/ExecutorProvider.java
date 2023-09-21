package io.army.reactive.executor;

import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.meta.ServerMeta;
import reactor.core.publisher.Mono;

/**
 * <p>This interface representing provider of reactive executor.
 * This implementation of this interface must declared :
 * <pre>
 *      <code>
 *          public static {implementation class of ExecutorProvider} create(Object){
 *
 *          }
 *      </code>
 *  </pre>
 */
public interface ExecutorProvider {

    Mono<ServerMeta> createServerMeta(Dialect usedDialect);

    Mono<StmtExecutorFactory> createExecutorFactory(ExecutorEnv env);

}
