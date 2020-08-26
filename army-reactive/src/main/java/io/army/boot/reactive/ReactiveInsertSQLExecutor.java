package io.army.boot.reactive;

import io.army.dialect.InsertException;
import io.army.wrapper.SQLWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ReactiveInsertSQLExecutor {

    Mono<Void> valueInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException;

    <N extends Number> Mono<N> subQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper
            , Class<N> resultClass) throws InsertException;

    <T> Flux<T> returningInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException;

    static ReactiveInsertSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new ReactiveInsertSQLExecutorImpl(sessionFactory);
    }
}
