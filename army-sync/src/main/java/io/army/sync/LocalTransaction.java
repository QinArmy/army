package io.army.sync;

import io.army.session.Transaction;
import io.army.session.TransactionException;
import io.army.session.TransactionStatus;

public interface LocalTransaction extends Transaction {

    @Override
    SyncLocalSession session();


    LocalTransaction start() throws TransactionException;

    void commit() throws TransactionException;

    /**
     * rollback and clear changed cache.
     */
    void rollback() throws TransactionException;

    TransactionStatus status();


    Object createSavePoint() throws TransactionException;

    void rollbackToSavePoint(Object savepoint) throws TransactionException;

    void releaseSavePoint(Object savepoint) throws TransactionException;


}
