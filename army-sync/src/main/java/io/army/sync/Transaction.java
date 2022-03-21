package io.army.sync;

import io.army.tx.GenericTransaction;
import io.army.tx.TransactionException;
import io.army.tx.TransactionStatus;

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
