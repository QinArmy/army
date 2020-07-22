package io.army.tx;

import io.army.sync.Session;

import java.sql.Savepoint;

public interface Transaction extends GenericSyncTransaction {

    TransactionStatus status();

    @Override
    Session session();

    Savepoint createSavepoint() throws TransactionException;

    void rollbackToSavepoint(Savepoint savepoint) throws TransactionException;

    void releaseSavepoint(Savepoint savepoint) throws TransactionException;

    boolean supportsSavePoints();



}
