package io.army.sync;

import io.army.SessionException;
import io.army.session.DataAccessException;
import io.army.tx.*;

import java.util.List;

final class LocalTransaction extends AbstractGenericTransaction implements Transaction {

    final SessionImpl session;

    private TransactionStatus status;

    LocalTransaction(final SessionImpl.LocalTransactionBuilder builder) {
        super(builder);
        this.session = builder.session;
        this.status = TransactionStatus.NOT_ACTIVE;
    }

    @Override
    public Session session() {
        return this.session;
    }

    @Override
    public TransactionStatus status() {
        return this.status;
    }

    @Override
    public boolean nonActive() {
        return false;
    }

    @Override
    public boolean rollbackOnly() {
        return false;
    }

    @Override
    public void start() throws TransactionException {
        if (this.status != TransactionStatus.NOT_ACTIVE) {
            String m = String.format("transaction status[%s] isn't %s,can't start transaction."
                    , this.status, TransactionStatus.NOT_ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        try {
            this.startMills = System.currentTimeMillis();
            final SessionImpl session = this.session;
            final List<String> stmtList;
            stmtList = session.sessionFactory.dialect.startTransaction(this.isolation, this.readonly);
            session.stmtExecutor.executeBatch(stmtList);
            this.status = TransactionStatus.ACTIVE;
        } catch (DataAccessException e) {
            throw new CannotCreateTransactionException("start transaction failure", e);
        }

    }

    @Override
    public void commit() throws TransactionException {
        if (this.status != TransactionStatus.ACTIVE) {
            String m = String.format("transaction status[%s] isn't %s,can't commit transaction."
                    , this.status, TransactionStatus.NOT_ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        this.status = TransactionStatus.COMMITTING;
        try {
            final SessionImpl session = this.session;
            session.flush();
            session.stmtExecutor.execute("COMMIT");
            this.status = TransactionStatus.COMMITTED;
        } catch (DataAccessException e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            throw new TransactionFailureException("army commit transaction failure.", e);
        } catch (Throwable e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            throw e;
        }
    }

    @Override
    public void rollback() throws TransactionException {
        switch (this.status) {
            case ACTIVE:
            case MARKED_ROLLBACK: {
                this.status = TransactionStatus.ROLLING_BACK;
                try {
                    final SessionImpl session = this.session;
                    session.clearChangedCache(this);
                    session.stmtExecutor.execute("ROLLBACK");
                    this.status = TransactionStatus.ROLLED_BACK;
                } catch (DataAccessException e) {
                    this.status = TransactionStatus.FAILED_ROLLBACK;
                    throw new TransactionFailureException("army rollback transaction failure.", e);
                } catch (Throwable e) {
                    this.status = TransactionStatus.FAILED_ROLLBACK;
                    throw e;
                }
            }
            break;
            default: {
                String m = String.format("transaction status[%s] not in [%s,%s],can't rollback transaction."
                        , this.status, TransactionStatus.ACTIVE, TransactionStatus.MARKED_ROLLBACK);
                throw new IllegalTransactionStateException(m);
            }
        }

    }


    @Override
    public Object createSavePoint() throws TransactionException {
        if (this.readonly) {
            String m = String.format("%s is readonly,couldn't create save points.", this);
            throw new ReadOnlyTransactionException(m);
        }
        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't create save point."
                    , this.status, TransactionStatus.ACTIVE);
        }
        try {
            return this.session.stmtExecutor.createSavepoint();
        } catch (DataAccessException e) {
            throw new TransactionSystemException("army create save point occur error.", e);
        }

    }

    @Override
    public void rollbackToSavePoint(final Object savepoint) throws TransactionException {
        switch (this.status) {
            case ACTIVE:
            case MARKED_ROLLBACK: {
                try {
                    this.session.stmtExecutor.rollbackToSavepoint(savepoint);
                } catch (DataAccessException e) {
                    throw new TransactionFailureException("army rollback transaction to save point failure.", e);
                }
            }
            break;
            default: {
                String m;
                m = String.format("transaction status[%s] not in [%s,%s],can't rollback transaction to save point."
                        , this.status, TransactionStatus.ACTIVE, TransactionStatus.MARKED_ROLLBACK);
                throw new IllegalTransactionStateException(m);
            }
        }
    }

    @Override
    public void releaseSavePoint(final Object savepoint) throws TransactionException {
        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't release save point."
                    , this.status, TransactionStatus.ACTIVE);
        }
        try {
            this.session.stmtExecutor.releaseSavepoint(savepoint);
        } catch (DataAccessException e) {
            throw new TransactionSystemException("army release save point occur error.", e);
        }
    }

    @Override
    public void markRollbackOnly() throws TransactionException {
        switch (this.status) {
            case ACTIVE:
            case MARKED_ROLLBACK:
                this.status = TransactionStatus.MARKED_ROLLBACK;
                break;
            default: {
                String m = String.format("transaction status[%s] can't mark roll back only."
                        , this.status);
                throw new IllegalTransactionStateException(m);
            }
        }

    }

    @Override
    public void flush() throws TransactionException {
        try {
            this.session.flush();
        } catch (SessionException e) {
            throw new TransactionUsageException("flush session cache occur error.", e);
        }
    }


    @Override
    public boolean transactionEnded() {
        return TransactionStatus.END_STATUS_SET.contains(this.status);
    }



    /*################################## blow package method ##################################*/


    /*################################## blow private method ##################################*/


}
