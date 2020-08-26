package io.army.boot.reactive;

import io.army.wrapper.SimpleSQLWrapper;
import reactor.core.publisher.Flux;

import java.util.Optional;

interface ReactiveSelectSQLExecutor {

    <R> Flux<R> select(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper, Class<R> resultClass);

    <R> Flux<Optional<R>> selectOptional(InnerGenericRmSession session, SimpleSQLWrapper sqlWrapper
            , Class<R> columnClass);

    static ReactiveSelectSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new ReactiveSelectSQLExecutorImpl(sessionFactory);
    }

}
