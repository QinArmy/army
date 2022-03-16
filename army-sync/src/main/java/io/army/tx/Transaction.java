package io.army.tx;

import io.army.sync.Session;

public interface Transaction extends GenericSyncTransaction {

    TransactionStatus status();

    @Override
    Session session();

    Object createSavepoint() throws TransactionException;

    void rollbackToSavepoint(Object savepoint) throws TransactionException;

    void releaseSavepoint(Object savepoint) throws TransactionException;

    boolean supportsSavePoints();



}
