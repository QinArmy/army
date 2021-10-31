package io.army.boot.reactive;

import io.army.dialect.InsertException;
import io.army.stmt.Stmt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface InsertSQLExecutor {

    Mono<Void> valueInsert(InnerGenericRmSession session, Stmt stmt) throws InsertException;

    <N extends Number> Mono<N> subQueryInsert(InnerGenericRmSession session, Stmt stmt
            , Class<N> resultClass) throws InsertException;

    <T> Flux<T> returningInsert(InnerGenericRmSession session, Stmt stmt, Class<T> resultClass)
            throws InsertException;

    static InsertSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new InsertSQLExecutorImpl(sessionFactory);
    }
}
