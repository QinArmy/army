package io.army.tx;

import io.army.sync.SyncSession;

public interface GenericSyncTransaction extends GenericTransaction {

    SyncSession session();


    void start() throws TransactionException;

    void commit() throws TransactionException;

    /**
     * rollback and clear changed cache.
     */
    void rollback() throws TransactionException;

    void flush() throws TransactionException;

}
