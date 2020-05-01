package io.army.tx;

import io.army.Session;

import java.io.Flushable;
import java.sql.Savepoint;

public interface Transaction extends GenericTransaction, Flushable, AutoCloseable {

    Session session();

    Savepoint createSavepoint() throws TransactionException;

    void rollbackToSavepoint(Savepoint savepoint) throws TransactionException;

    void releaseSavepoint(Savepoint savepoint) throws TransactionException;

    boolean rollbackOnly();

    boolean supportsSavePoints();

    void start() throws TransactionException;

    void rollback() throws TransactionException;

    void commit() throws TransactionException;

    void markRollbackOnly() throws TransactionException;

    @Override
    void flush() throws TransactionException;

    @Override
    void close() throws TransactionException;

}
