package io.army.tx;

import io.army.sync.Session;

public interface Transaction extends GenericTransaction {

    void start() throws TransactionException;

    void commit() throws TransactionException;

    /**
     * rollback and clear changed cache.
     */
    void rollback() throws TransactionException;

    TransactionStatus status();

    Session session();

    Object createSavePoint() throws TransactionException;

    void rollbackToSavePoint(Object savepoint) throws TransactionException;

    void releaseSavePoint(Object savepoint) throws TransactionException;



}
