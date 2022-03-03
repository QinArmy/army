package io.army.tx;

import io.army.sync.SyncSession;

import java.io.Flushable;

public interface GenericSyncTransaction extends GenericTransaction, Flushable, AutoCloseable {

    SyncSession session();


    void start() throws TransactionException;

    void commit() throws TransactionException;

    void rollback() throws TransactionException;



    @Override
    void flush() throws TransactionException;

    @Override
    void close() throws TransactionException;
}
