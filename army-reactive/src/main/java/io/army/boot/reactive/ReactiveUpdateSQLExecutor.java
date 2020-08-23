package io.army.boot.reactive;

import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.wrapper.SQLWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface ReactiveUpdateSQLExecutor {

    Mono<Integer> update(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    Mono<Long> largeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    Flux<Integer> batchUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    Flux<Long> batchLargeUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper);

    <T> Flux<T> returningUpdate(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass);

    static ReactiveUpdateSQLExecutor build(GenericReactiveRmSessionFactory sessionFactory) {
        return new ReactiveUpdateSQLExecutorImpl(sessionFactory);
    }
}
