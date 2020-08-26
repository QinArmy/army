package io.army.boot.reactive;

import io.army.wrapper.SQLWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ReactiveUpdateSQLExecutor {

   <N extends Number> Mono<N> update(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<N> resultClass);

    <N extends Number> Flux<N> batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<N> resultClass);

    <T> Flux<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass);

    static ReactiveUpdateSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new ReactiveUpdateSQLExecutorImpl(sessionFactory);
    }
}
