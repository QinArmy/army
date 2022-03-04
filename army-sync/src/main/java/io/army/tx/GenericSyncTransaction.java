package io.army.tx;

import io.army.sync.SyncSession;

public interface GenericSyncTransaction extends GenericTransaction, AutoCloseable {

    SyncSession session();


    void start() throws TransactionException;

    void commit() throws TransactionException;

    void rollback() throws TransactionException;

    void flush() throws TransactionException;

    @Override
    void close() throws TransactionException;
}
