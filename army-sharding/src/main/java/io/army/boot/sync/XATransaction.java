package io.army.boot.sync;

import io.army.tx.GenericSyncTransaction;
import io.army.tx.TransactionException;

interface XATransaction extends GenericSyncTransaction {

    @Override
    RmSession session();

    XATransactionStatus status();

    @Override
    void start() throws TransactionException;

    void end() throws TransactionException;

    void prepare() throws TransactionException;

    void commitOnePhase() throws TransactionException;

    @Override
    void rollback() throws TransactionException;

    /**
     * uses two-phase commit,after {@link #end()} and {@link #prepare()} invoke.
     */
    @Override
    void commit() throws TransactionException;

    void forget() throws TransactionException;
}