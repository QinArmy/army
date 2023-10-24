package io.army.sync;

import io.army.session.DataAccessException;
import io.army.tx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class SyncLocalTransaction extends _ArmyTransaction implements LocalTransaction {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLocalTransaction.class);

    final ArmySyncLocalSession session;

    private TransactionStatus status;

    SyncLocalTransaction(final ArmySyncLocalSession.LocalTransactionBuilder builder) {
        super(builder);
        this.session = builder.session;
        this.status = TransactionStatus.NOT_ACTIVE;
    }

    @Override
    public SyncLocalSession session() {
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
    public LocalTransaction start() throws TransactionException {
        if (this.status != TransactionStatus.NOT_ACTIVE) {
            String m = String.format("%s status isn't %s,can't start transaction.", this, TransactionStatus.NOT_ACTIVE);
            throw new IllegalTransactionStateException(m);
        }
        try {
            final ArmySyncLocalSession session = this.session;
            final List<String> stmtList;
            stmtList = session.factory.dialectParser.startTransaction(this.isolation, this.readonly);

            printStmtListIfNeed(session.factory, LOG, stmtList);

            session.stmtExecutor.executeBatch(stmtList);
            this.status = TransactionStatus.ACTIVE;

            return this;
        } catch (DataAccessException e) {
            String m = String.format("%s start failure.", this);
            throw new TransactionSystemException(m, e);
        }

    }

    @Override
    public void commit() throws TransactionException {
        final TransactionStatus oldStatus = this.status;
        switch (oldStatus) {
            case ACTIVE: {
                this.status = TransactionStatus.COMMITTING;
                final ArmySyncLocalSession session = this.session;
                try {
                    final String command = "COMMIT";
                    printStmtIfNeed(session.factory, LOG, command);
                    session.stmtExecutor.execute(command);
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
            break;
            case NOT_ACTIVE:
                LOG.debug("transaction[name : {}]'s status is {},so no action.", this.name, oldStatus);
                this.session.endTransaction(this);
                break;
            default: {
                String m;
                m = String.format("%s status isn't %s,can't commit transaction.", this, oldStatus);
                throw new IllegalTransactionStateException(m);
            }
        }


    }

    @Override
    public void rollback() throws TransactionException {
        final TransactionStatus oldStatus = this.status;
        switch (oldStatus) {
            case ACTIVE:
            case COMMITTING:// flush failure.
            case FAILED_COMMIT:
            case MARKED_ROLLBACK: {
                this.status = TransactionStatus.ROLLING_BACK;
                final ArmySyncLocalSession session = this.session;
                try {
                    session.clearChangedCache(this);

                    final String command = "ROLLBACK";

                    printStmtIfNeed(session.factory, LOG, command);

                    session.stmtExecutor.execute(command);
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
            case NOT_ACTIVE:
                LOG.debug("transaction[name : {}]'s status is {},so no action", this.name, oldStatus);
                this.session.endTransaction(this);
                break;
            default: {
                String m = String.format("%s status is %s,can't rollback transaction.", this, oldStatus);
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
