package io.army.boot.reactive;

import io.army.dialect.InsertException;
import io.army.reactive.GenericReactiveRmSessionFactory;
import io.army.wrapper.SQLWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

interface ReactiveInsertSQLExecutor {

    Mono<Void> valueInsert(InnerGenericRmSession session, List<SQLWrapper> sqlWrapperList) throws InsertException;

    Mono<Integer> subQueryInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException;

    Mono<Long> subQueryLargeInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper) throws InsertException;

    <T> Flux<T> returningInsert(InnerGenericRmSession session, SQLWrapper sqlWrapper, Class<T> resultClass)
            throws InsertException;

    static ReactiveInsertSQLExecutor build(GenericReactiveRmSessionFactory sessionFactory) {
        return new ReactiveInsertSQLExecutorImpl(sessionFactory);
    }
}