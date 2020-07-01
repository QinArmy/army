package io.army.boot;

import io.army.Session;
import io.army.tx.*;

import java.sql.Savepoint;

final class SyncShardingTransaction implements Transaction {

    @Override
    public Session session() {
        return null;
    }

    @Override
    public Savepoint createSavepoint() throws TransactionException {
        return null;
    }

    @Override
    public void rollbackToSavepoint(Savepoint savepoint) throws TransactionException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws TransactionException {

    }

    @Override
    public boolean rollbackOnly() {
        return false;
    }

    @Override
    public boolean supportsSavePoints() {
        return false;
    }

    @Override
    public void start() throws TransactionException {

    }

    @Override
    public void rollback() throws TransactionException {

    }

    @Override
    public void commit() throws TransactionException {

    }

    @Override
    public void markRollbackOnly() throws TransactionException {

    }

    @Override
    public void flush() throws TransactionException {

    }

    @Override
    public void close() throws TransactionException {

    }

    @Override
    public TransactionStatus status() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Isolation isolation() {
        return null;
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public long getTimeToLiveInMillis() throws TransactionTimeOutException {
        return 0;
    }
}
