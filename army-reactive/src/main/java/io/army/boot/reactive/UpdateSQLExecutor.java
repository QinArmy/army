package io.army.boot.reactive;

import io.army.stmt.Stmt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface UpdateSQLExecutor {

    <N extends Number> Mono<N> update(InnerGenericRmSession session, Stmt stmt, Class<N> resultClass);

    <N extends Number> Flux<N> batchUpdate(InnerGenericRmSession session, Stmt stmt, Class<N> resultClass);

    <T> Flux<T> returningUpdate(InnerGenericRmSession session, Stmt stmt, Class<T> resultClass);

    static UpdateSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new UpdateSQLExecutorImpl(sessionFactory);
    }
}
