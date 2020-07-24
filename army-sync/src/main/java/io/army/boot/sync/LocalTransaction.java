package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.SessionException;
import io.army.dialect.TransactionOption;
import io.army.sync.Session;
import io.army.tx.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashSet;
import java.util.Set;

final class LocalTransaction extends AbstractSyncTransaction implements Transaction {

    static final String SAVEPOINT_NAME_PREFIX = "ARMY_SAVEPOINT_";

    final InnerTxSession session;

    final Set<Savepoint> savepointSet = new HashSet<>();

    private TransactionStatus status;

    int savePointCounter = 0;

    LocalTransaction(InnerTxSession session, TransactionOption option) {
        super(option);
        this.session = session;
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
    public void start() throws TransactionException {
        checkTransaction();

        if (this.status != TransactionStatus.NOT_ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't start transaction."
                    , this.status, TransactionStatus.NOT_ACTIVE);
        }

        try {
            final Connection connection = this.session.connection();

            if (this.readOnly) {
                connection.setReadOnly(true);
            } else {
                connection.setAutoCommit(false);
            }
            if (this.isolation != Isolation.DEFAULT) {
                connection.setTransactionIsolation(isolation.level);
            }
            this.status = TransactionStatus.ACTIVE;
        } catch (SQLException e) {
            throw new CannotCreateTransactionException(ErrorCode.START_TRANSACTION_FAILURE
                    , e, "army start transaction failure");
        }

    }

    @Override
    public void commit() throws TransactionException {
        checkReadWrite("commit");

        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't commit."
                    , this.status, TransactionStatus.ACTIVE);
        }
        this.status = TransactionStatus.COMMITTING;
        try {
            this.session.connection().commit();
            this.status = TransactionStatus.COMMITTED;
        } catch (SQLException e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            throw new TransactionFailureException(e, "army commit transaction failure.");
        }
    }

    @Override
    public void rollback() throws TransactionException {
        checkReadWrite("rollback");

        if (!ROLL_BACK_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] don't in %s,can't rollback."
                    , this.status, ROLL_BACK_ABLE_SET);
        }
        this.status = TransactionStatus.ROLLING_BACK;
        try {
            this.session.connection().rollback();
            this.status = TransactionStatus.ROLLED_BACK;
        } catch (SQLException e) {
            this.status = TransactionStatus.FAILED_ROLLBACK;
            throw new TransactionFailureException(e, "army roll back transaction failure.");
        }

    }


    @Override
    public boolean supportsSavePoints() {
        checkTransaction();
        try {
            return this.session.connection().getMetaData().supportsSavepoints();
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army invoke supports save points  occur error.");
        }
    }

    @Override
    public Savepoint createSavepoint() throws TransactionException {
        checkReadWrite("createSavepoint");

        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't create save point."
                    , this.status, TransactionStatus.ACTIVE);
        }
        try {
            final String savePointName = SAVEPOINT_NAME_PREFIX + (this.savePointCounter++);
            final Savepoint savepoint = this.session.connection().setSavepoint(savePointName);
            this.savepointSet.add(savepoint);
            return savepoint;
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army create save point occur error.");
        }
    }

    @Override
    public void rollbackToSavepoint(final Savepoint savepoint) throws TransactionException {
        checkReadWrite("rollbackToSavepoint");

        if (!ROLL_BACK_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException(
                    "transaction status[%s] don't in %s,can't rollback to save point."
                    , this.status, ROLL_BACK_ABLE_SET);
        }
        if (!this.savepointSet.contains(savepoint)) {
            throw new UnKnownSavepointException("Savepoint[%s] not exists", savepoint);
        }
        try {
            this.session.connection().rollback(savepoint);
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army roll back to save point[%s] occur error.", savepoint);
        }
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws TransactionException {
        checkReadWrite("releaseSavepoint");

        if (!this.savepointSet.contains(savepoint)) {
            throw new UnKnownSavepointException("Savepoint[%s] not exists", savepoint);
        }
        try {
            this.session.connection().releaseSavepoint(savepoint);
            this.savepointSet.remove(savepoint);
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army release save point occur error.");
        }
    }

    @Override
    public void markRollbackOnly() throws TransactionException {
        checkReadWrite("markRollbackOnly");

        if (!ROLL_BACK_ONLY_ABLE_SET.contains(this.status())) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't mark roll back only."
                    , this.status, ROLL_BACK_ONLY_ABLE_SET);
        }
        this.status = TransactionStatus.MARKED_ROLLBACK;
    }

    @Override
    public void flush() throws TransactionException {
        checkReadWrite("flush");
        try {
            this.session.flush();
        } catch (SessionException e) {
            throw new TransactionUsageException(e.getErrorCode(), e, "transaction flush error.");
        }
    }

    @Override
    public void close() throws TransactionException {
        assertCanClose();
        this.session.closeTransaction(this);
    }

    /*################################## blow package method ##################################*/




    /*################################## blow private method ##################################*/


}
