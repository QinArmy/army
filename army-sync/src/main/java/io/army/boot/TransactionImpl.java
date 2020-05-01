package io.army.boot;

import io.army.ErrorCode;
import io.army.Session;
import io.army.dialect.TransactionOption;
import io.army.tx.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

final class TransactionImpl implements Transaction {

    public static final String SAVEPOINT_NAME_PREFIX = "ARMY_SAVEPOINT_";

    private static final EnumSet<TransactionStatus> ROLL_BACK_ABLE_SET = EnumSet.of(
            TransactionStatus.ACTIVE,
            TransactionStatus.MARKED_ROLLBACK
    );

    static final EnumSet<TransactionStatus> END_ABLE_SET = EnumSet.of(
            TransactionStatus.COMMITTED,
            TransactionStatus.ROLLED_BACK
    );

    private static final EnumSet<TransactionStatus> ROLL_BACK_ONLY_ABLE_SET = EnumSet.of(
            TransactionStatus.ACTIVE,
            TransactionStatus.MARKED_ROLLBACK
    );


    private final InnerTxSession session;

    private final TransactionOption option;

    private final Isolation isolation;

    private final boolean readOnly;

    private final String name;

    private final Set<Savepoint> savepointSet = new HashSet<>();

    private final long endMills;

    private boolean rollbackOnly = false;

    private int savePointCounter = 0;

    private TransactionStatus status;

    TransactionImpl(InnerTxSession session, TransactionOption option) {
        this.session = session;
        this.option = option;
        this.readOnly = this.option.readOnly();
        this.isolation = option.isolation();

        this.name = option.name();
        this.status = TransactionStatus.NOT_ACTIVE;
        this.endMills = (System.currentTimeMillis() + option.timeout() * 1000L);
    }


    @Override
    public Session session() {
        return this.session;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Isolation isolation() {
        return this.isolation;
    }

    @Override
    public TransactionStatus status() {
        return this.status;
    }

    @Override
    public void start() throws TransactionException {
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
                connection.setReadOnly(false);
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
    public void rollback() throws TransactionException {
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
    public void commit() throws TransactionException {
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
    public Savepoint createSavepoint() throws TransactionException {
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
        if (!ROLL_BACK_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] don't in %s,can't rollback."
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
    public boolean rollbackOnly() {
        return this.rollbackOnly;
    }

    @Override
    public boolean supportsSavePoints() {
        try {
            return this.session.connection().getMetaData().supportsSavepoints();
        } catch (SQLException e) {
            throw new TransactionSystemException(e, "army invoke supportsSavepoints  occur error.");
        }
    }

    @Override
    public boolean readOnly() {
        return this.readOnly;
    }

    @Override
    public long getTimeToLiveInMillis() throws TransactionTimeOutException {
        long liveInMills = this.endMills - System.currentTimeMillis();
        if (liveInMills < 0) {
            throw new TransactionTimeOutException("transaction[name:%s] timeout,live in mills is %s ."
                    , this.option.name(), liveInMills);
        }
        return liveInMills;
    }


    @Override
    public void markRollbackOnly() throws TransactionException {
        if (!ROLL_BACK_ONLY_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't mark roll back only."
                    , this.status, ROLL_BACK_ONLY_ABLE_SET);
        }
        this.rollbackOnly = true;
        this.status = TransactionStatus.MARKED_ROLLBACK;
    }

    @Override
    public void flush() throws TransactionException {
        // no-op
    }

    @Override
    public void close() throws TransactionException {
        if (!END_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't close."
                    , this.status, END_ABLE_SET);
        }
        this.session.closeTransaction(this);
    }
}
