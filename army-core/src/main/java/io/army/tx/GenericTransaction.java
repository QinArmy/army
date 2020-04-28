package io.army.tx;

import java.io.Flushable;
import java.sql.Savepoint;

public interface GenericTransaction extends Flushable, AutoCloseable {

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
