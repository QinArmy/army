package io.army.boot.reactive;

import io.army.stmt.SimpleStmt;
import reactor.core.publisher.Flux;

import java.util.Optional;

interface SelectSQLExecutor {

    <R> Flux<R> select(InnerGenericRmSession session, SimpleStmt sqlWrapper, Class<R> resultClass);

    <R> Flux<Optional<R>> selectOptional(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , Class<R> columnClass);

    static SelectSQLExecutor build(InnerGenericRmSessionFactory sessionFactory) {
        return new SelectSQLExecutorImpl(sessionFactory);
    }

}
