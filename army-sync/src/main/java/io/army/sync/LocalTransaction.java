package io.army.sync;

import io.army.session.DataAccessException;
import io.army.tx.*;

import java.util.List;

final class LocalTransaction extends _AbstractGenericTransaction implements Transaction {

    final LocalSession session;

    private TransactionStatus status;

    LocalTransaction(final LocalSession.LocalTransactionBuilder builder) {
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
    public boolean rollbackOnly() {
        return false;
    }

    @Override
    public void start() throws TransactionException {
        if (this.status != TransactionStatus.NOT_ACTIVE) {
            String m = String.format("%s status isn't %s,can't start transaction.", this, TransactionStatus.NOT_ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        try {
            final LocalSession session = this.session;
            final List<String> stmtList;
            stmtList = session.sessionFactory.dialect.startTransaction(this.isolation, this.readonly);
            session.stmtExecutor.executeBatch(stmtList);
            this.status = TransactionStatus.ACTIVE;
        } catch (DataAccessException e) {
            String m = String.format("%s start failure.", this);
            throw new TransactionSystemException(m, e);
        }

    }

    @Override
    public void commit() throws TransactionException {
        if (this.status != TransactionStatus.ACTIVE) {
            String m;
            m = String.format("%s status isn't %s,can't commit transaction.", this, TransactionStatus.NOT_ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        this.status = TransactionStatus.COMMITTING;
        final LocalSession session = this.session;
        try {
            session.stmtExecutor.execute("COMMIT");
            this.status = TransactionStatus.COMMITTED;
        } catch (DataAccessException e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            String m = String.format("%s commit failure.", this);
            throw new TransactionSystemException(m, e);
        } catch (Throwable e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            throw e;
        } finally {
            if (this.status == TransactionStatus.COMMITTED) {// end when only COMMITTED,because rollback
                session.endTransaction(this);
            }
        }

    }

    @Override
    public void rollback() throws TransactionException {
        switch (this.status) {
            case ACTIVE:
            case COMMITTING:// flush failure.
            case FAILED_COMMIT:
            case MARKED_ROLLBACK: {
                this.status = TransactionStatus.ROLLING_BACK;
                final LocalSession session = this.session;
                try {
                    session.clearChangedCache(this);
                    session.stmtExecutor.execute("ROLLBACK");
                    this.status = TransactionStatus.ROLLED_BACK;
                } catch (DataAccessException e) {
                    this.status = TransactionStatus.FAILED_ROLLBACK;
                    String m = String.format("%s roll back failure", this);
                    throw new TransactionSystemException(m, e);
                } catch (Throwable e) {
                    this.status = TransactionStatus.FAILED_ROLLBACK;
                    throw e;
                } finally {
                    if (this.status != TransactionStatus.ROLLING_BACK) {
                        //TODO validate
                        session.endTransaction(this);
                    }
                }
            }
            break;
            default: {
                String m = String.format("%s status not in [%s,%s],can't rollback transaction."
                        , this, TransactionStatus.ACTIVE, TransactionStatus.MARKED_ROLLBACK);
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
            String m = String.format("%s status isn't %s,can't create save point.", this, TransactionStatus.ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        try {
            return this.session.stmtExecutor.createSavepoint();
        } catch (DataAccessException e) {
            String m = String.format("%s create save point occur error.", this);
            throw new TransactionSystemException(m, e);
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
                    String m = String.format("%s rollback to save point failure.", this);
                    throw new TransactionSystemException(m, e);
                }
            }
            break;
            default: {
                String m;
                m = String.format("%s status not in [%s,%s],can't rollback transaction to save point."
                        , this, TransactionStatus.ACTIVE, TransactionStatus.MARKED_ROLLBACK);
                throw new IllegalTransactionStateException(m);
            }
        }
    }

    @Override
    public void releaseSavePoint(final Object savepoint) throws TransactionException {
        if (this.status != TransactionStatus.ACTIVE) {
            String m = String.format("%s status isn't %s,can't release save point.", this, TransactionStatus.ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        try {
            this.session.stmtExecutor.releaseSavepoint(savepoint);
        } catch (DataAccessException e) {
            String m = String.format("%s release save point occur error.", this);
            throw new TransactionSystemException(m, e);
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
                String m = String.format("%s  can't mark roll back only.", this);
                throw new IllegalTransactionStateException(m);
            }
        }

    }


}
