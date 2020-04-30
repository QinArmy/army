package io.army.tx;

import io.army.Session;

import java.sql.Savepoint;

public interface Transaction extends GenericTransaction {

    Session session();

    Savepoint createSavepoint() throws TransactionException;

    void rollbackToSavepoint(Savepoint savepoint) throws TransactionException;

    void releaseSavepoint(Savepoint savepoint) throws TransactionException;

    boolean rollbackOnly();

    boolean readOnly();

    void start() throws TransactionException;

    void rollback() throws TransactionException;

    void commit() throws TransactionException;

    void rollbackOnly(boolean rollbackOnly) throws TransactionException;

    @Override
    void flush() throws TransactionException;

    @Override
    void close() throws TransactionException;

}
