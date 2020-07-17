package io.army.boot;

import io.army.ErrorCode;
import io.army.dialect.TransactionOption;
import io.army.tx.*;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;

/**
 * this class is a implementation of {@link XATransaction},use {@link XAResource} implement XA transaction.
 * <P>
 * this class used by {@link SyncCommitTransactionManager}
 * </P>
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see SyncCommitTransactionManager
 * @see XAResource
 */
final class XaResourceTransaction implements XATransaction {

    private static final EnumSet<XATransactionStatus> ROLL_BACK_ABLE_SET = EnumSet.of(
            XATransactionStatus.PREPARED,
            XATransactionStatus.FAILED_COMMIT
    );


    private static final EnumSet<XATransactionStatus> FORGET_ABLE_SET = EnumSet.of(
            XATransactionStatus.COMMITTED,
            XATransactionStatus.ROLLED_BACK
    );

    private final InnerRmSession session;

    private final XAResource xaResource;

    private final Xid xid;

    private final Isolation isolation;

    private final boolean readOnly;

    private final String name;

    private final int timeout;

    private final long endMills;
    /**
     * status maybe read by parallelly .
     *
     * @see SyncCommitTransactionManager#commit()
     * @see SyncCommitTransactionManager#rollback()
     */
    private XATransactionStatus status = XATransactionStatus.NOT_ACTIVE;

    private boolean rollBackOnly = false;

    XaResourceTransaction(InnerRmSession session, Xid xid, TransactionOption option) {
        this.session = session;
        this.xid = xid;

        this.isolation = option.isolation();
        this.readOnly = option.readOnly();
        this.name = option.name();
        this.timeout = option.timeout();

        if (this.readOnly) {
            this.xaResource = null;
        } else {
            this.xaResource = session.xaResource();
        }
        if (timeout > 0) {
            this.endMills = (System.currentTimeMillis() + this.timeout * 1000L);
        } else {
            this.endMills = timeout;
        }
    }

    @Override
    public RmSession session() {
        return this.session;
    }

    @Override
    public XATransactionStatus status() {
        return this.status;
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
    public boolean readOnly() {
        return this.readOnly;
    }

    @Override
    public boolean rollbackOnly() {
        return this.rollBackOnly;
    }

    @Override
    public long getTimeToLiveInMillis() throws TransactionTimeOutException {

        long liveInMills = this.endMills - System.currentTimeMillis();
        if (liveInMills < 0) {
            throw new TransactionTimeOutException("transaction[name:%s] timeout,live in mills is %s ."
                    , this.name, liveInMills);
        }
        return liveInMills;
    }

    @Override
    public void start() throws TransactionException {

        if (this.status != XATransactionStatus.NOT_ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't start transaction."
                    , this.status, XATransactionStatus.NOT_ACTIVE);
        }
        try {
            final Connection conn = this.session.connection();
            if (this.isolation != Isolation.DEFAULT) {
                conn.setTransactionIsolation(this.isolation.level);
            }

            if (this.readOnly) {
                conn.setReadOnly(true);
            } else {
                conn.setAutoCommit(false);
                this.xaResource.start(this.xid, XAResource.TMNOFLAGS);
                this.xaResource.setTransactionTimeout(this.timeout);
            }
            this.status = XATransactionStatus.ACTIVE;
        } catch (XAException | SQLException e) {
            throw new CannotCreateTransactionException(ErrorCode.START_TRANSACTION_FAILURE
                    , e, "army XA start failure");
        }
    }

    @Override
    public void markRollbackOnly() throws TransactionException {
        checkReadWrite("markRollbackOnly");

        if (this.status != XATransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't mark roll back only."
                    , this.status, XATransactionStatus.ACTIVE);
        }
        this.rollBackOnly = true;
    }


    @Override
    public void end() throws TransactionException {
        checkReadWrite("end");

        if (this.status != XATransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't start transaction."
                    , this.status, XATransactionStatus.ACTIVE);
        }
        try {
            if (!this.readOnly) {
                this.status = XATransactionStatus.IDLING;
                this.xaResource.end(this.xid, XAResource.TMSUCCESS);
            }
            this.status = XATransactionStatus.IDLE;
        } catch (XAException e) {
            this.status = XATransactionStatus.FAILED_IDLE;
            throw new TransactionFailureException(e, "army XA end transaction failure.");
        }
    }

    @Override
    public void prepare() throws TransactionException {
        checkReadWrite("prepare");

        if (this.status != XATransactionStatus.IDLE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't prepare transaction."
                    , this.status, XATransactionStatus.IDLE);
        }
        try {
            if (!this.readOnly) {
                this.status = XATransactionStatus.PREPARING;
                this.xaResource.prepare(this.xid);
            }
            this.status = XATransactionStatus.PREPARED;
        } catch (XAException e) {
            this.status = XATransactionStatus.FAILED_PREPARE;
            throw new TransactionFailureException(e, "army XA prepare transaction failure.");
        }
    }

    @Override
    public void commit() throws TransactionException {
        doCommit(false);
    }

    @Override
    public void commitOnePhase() throws TransactionException {
        doCommit(true);
    }

    @Override
    public void rollback() throws TransactionException {
        checkReadWrite("rollback");

        if (!ROLL_BACK_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't rollback transaction."
                    , this.status, ROLL_BACK_ABLE_SET);
        }
        try {
            if (!this.readOnly) {
                this.status = XATransactionStatus.ROLLING_BACK;
                this.xaResource.rollback(this.xid);
            }
            this.status = XATransactionStatus.ROLLED_BACK;
        } catch (XAException e) {
            this.status = XATransactionStatus.FAILED_ROLLBACK;
            throw new TransactionFailureException(e, "army XA rollback transaction failure.");
        }
    }

    @Override
    public void forget() throws TransactionException {
        checkReadWrite("forget");

        if (!FORGET_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't forget transaction."
                    , this.status, FORGET_ABLE_SET);
        }
        try {
            if (!this.readOnly) {
                this.status = XATransactionStatus.FORGETTING;
                this.xaResource.forget(this.xid);
            }
            this.status = XATransactionStatus.FORGOT;
        } catch (XAException e) {
            this.status = XATransactionStatus.FAILED_FORGET;
            throw new TransactionFailureException(e, "army XA forget transaction failure.");
        }
    }

    @Override
    public void flush() throws TransactionException {
        //no-op
    }

    @Override
    public void close() throws TransactionException {
        //no-op
    }

    /*################################## blow package template method ##################################*/


    private void checkReadWrite(String action) {
        if (this.readOnly) {
            throw new ReadOnlyTransactionException("transaction[status=%s]can't %s."
                    , status, action);
        }
    }

    /*################################## blow private method ##################################*/

    private void doCommit(final boolean onePhase) throws TransactionException {
        if (onePhase) {
            checkReadWrite("commitOnePhase");
            if (this.status != XATransactionStatus.IDLE) {
                throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't commit transaction."
                        , this.status, XATransactionStatus.IDLE);
            }
        } else {
            checkReadWrite("commit");
            if (this.status != XATransactionStatus.PREPARED) {
                throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't commit transaction."
                        , this.status, XATransactionStatus.PREPARED);
            }
        }
        if (this.rollBackOnly) {
            throw new TransactionRollbackOnlyException("transaction[xid:%s] marked rollback,can't commit transaction."
                    , this.xid);
        }

        try {
            if (!this.readOnly) {
                this.status = XATransactionStatus.COMMITTING;
                this.xaResource.commit(this.xid, onePhase);
            }
            this.status = XATransactionStatus.COMMITTED;
        } catch (XAException e) {
            this.status = XATransactionStatus.FAILED_COMMIT;
            throw new TransactionFailureException(e, "army XA commit transaction failure.");
        }
    }


}
