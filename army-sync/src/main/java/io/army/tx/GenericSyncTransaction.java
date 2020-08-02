package io.army.tx;

import io.army.sync.GenericSyncSession;

import java.io.Flushable;

public interface GenericSyncTransaction extends GenericTransaction, Flushable, AutoCloseable {

    GenericSyncSession session();


    void start() throws TransactionException;

    void commit() throws TransactionException;

    void rollback() throws TransactionException;



    @Override
    void flush() throws TransactionException;

    @Override
    void close() throws TransactionException;
}
