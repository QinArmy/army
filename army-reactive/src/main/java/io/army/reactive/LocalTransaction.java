package io.army.reactive;

import io.army.tx.TransactionException;
import io.army.tx._ArmyTransaction;
import reactor.core.publisher.Mono;

final class LocalTransaction extends _ArmyTransaction implements Transaction {


    private final LocalSession session;


    LocalTransaction(LocalSession.LocalTransactionBuilder builder) {
        super(builder);
        this.session = builder.session;
    }

    @Override
    public Session session() {
        return this.session;
    }

    @Override
    public Mono<Void> start() {
        return Mono.empty();
    }

    @Override
    public Enum<?> status() {
        return null;
    }


    @Override
    public boolean rollbackOnly() {
        return false;
    }

    @Override
    public void markRollbackOnly() throws TransactionException {

    }


    @Override
    public Mono<Void> commit() {
        return null;
    }

    @Override
    public Mono<Void> rollback() {
        return null;
    }

    @Override
    public Mono<Object> createSavepoint() {
        return null;
    }

    @Override
    public Mono<Void> rollbackToSavepoint(Object savepoint) {
        return null;
    }

    @Override
    public Mono<Void> releaseSavepoint(Object savepoint) {
        return null;
    }


}
