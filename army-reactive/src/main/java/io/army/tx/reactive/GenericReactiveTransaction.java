package io.army.tx.reactive;

import io.army.reactive.GenericReactiveSession;
import io.army.tx.GenericTransaction;
import io.army.tx.TransactionException;
import reactor.core.publisher.Mono;

public interface GenericReactiveTransaction extends GenericTransaction {

    GenericReactiveSession session();


    Mono<Void> start() throws TransactionException;

    Mono<Void> commit() throws TransactionException;

    Mono<Void> rollback() throws TransactionException;

    boolean rollbackOnly();

    void markRollbackOnly() throws TransactionException;

    Mono<Void> flush() throws TransactionException;

    Mono<Void> close() throws TransactionException;
}
