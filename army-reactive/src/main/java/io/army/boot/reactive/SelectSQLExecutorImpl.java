package io.army.boot.reactive;

import io.army.stmt.SimpleStmt;
import reactor.core.publisher.Flux;

import java.util.Optional;

final class SelectSQLExecutorImpl extends SQLExecutorSupport implements SelectSQLExecutor {

    SelectSQLExecutorImpl(InnerGenericRmSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public final <R> Flux<R> select(InnerGenericRmSession session, SimpleStmt sqlWrapper, Class<R> resultClass) {
        return this.doExecuteSimpleQuery(session, sqlWrapper, resultClass);
    }

    @Override
    public final <R> Flux<Optional<R>> selectOptional(InnerGenericRmSession session, SimpleStmt sqlWrapper
            , Class<R> columnClass) {
        return this.doExecuteColumnQuery(session, sqlWrapper, columnClass);
    }

}
