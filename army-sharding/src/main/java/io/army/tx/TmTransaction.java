package io.army.tx;

import io.army.TmSession;

public interface TmTransaction extends GenericSyncTransaction {

    @Override
    TmSession session();

    TransactionStatus status();

    boolean rollbackOnly();

    void markRollbackOnly() throws TransactionException;

    /**
     * no operation
     */
    @Override
    void start() throws TransactionException;

    /**
     * invoke all {@link XATransaction} finish operation.
     */
    @Override
    void rollback() throws TransactionException;

    /**
     * uses two-phase commit,invoke all {@link XATransaction} finish operation.
     */
    @Override
    void commit() throws TransactionException;
}
