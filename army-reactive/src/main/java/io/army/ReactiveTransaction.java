package io.army;

import io.army.tx.Isolation;
import io.army.tx.TransactionException;
import reactor.core.publisher.Mono;

import java.sql.Savepoint;

public interface ReactiveTransaction {

    Mono<Void> start();

    Mono<Savepoint> createSavepoint() throws TransactionException;

    Mono<Savepoint> rollbackToSavepoint(Savepoint savepoint) throws TransactionException;

    Mono<Void> releaseSavepoint(Savepoint savepoint) throws TransactionException;

    boolean rollbackOnly();

    boolean readOnly();

    Isolation isolation();

    Mono<Void> rollback() throws TransactionException;

    Mono<Void> commit() throws TransactionException;

    Mono<Void> rollbackOnly(boolean rollbackOnly) throws TransactionException;

    ReactiveSession session();


}
