package io.army.reactive;

import io.army.session.TransactionException;
import reactor.core.publisher.Mono;

public interface Transaction extends io.army.session.Transaction {

    @Override
    ReactiveLocalSession session();

    /**
     * @throws TransactionException emit(not throw)
     */
    Mono<Void> start();

    /**
     * @throws TransactionException emit(not throw)
     */
    Mono<Void> commit();

    /**
     * @throws TransactionException emit(not throw)
     */
    Mono<Void> rollback();

    /**
     * @throws TransactionException emit(not throw)
     */
    Mono<Object> createSavepoint();

    /**
     * @throws TransactionException emit(not throw)
     */
    Mono<Void> rollbackToSavepoint(Object savepoint);

    /**
     * @throws TransactionException emit(not throw)
     */
    Mono<Void> releaseSavepoint(Object savepoint);


}
