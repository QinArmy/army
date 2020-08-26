package io.army.tx.reactive;

import io.army.reactive.ReactiveSession;
import io.army.tx.TransactionException;
import reactor.core.publisher.Mono;

import java.sql.Savepoint;

public interface ReactiveTransaction extends GenericReactiveTransaction {

    @Override
    ReactiveSession session();


    Mono<Void> commit() throws TransactionException;

    Mono<Void> rollback() throws TransactionException;

    Mono<Savepoint> createSavepoint() throws TransactionException;

    Mono<Void> rollbackToSavepoint(Savepoint savepoint) throws TransactionException;

    Mono<Void> releaseSavepoint(Savepoint savepoint) throws TransactionException;


}
