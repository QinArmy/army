package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.sync.TmSession;
import io.army.tx.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * this class is a implementation of {@link TmTransaction}
 * ,use {@link javax.transaction.xa.XAResource} to finish XA transaction.
 * <p>
 * this class used by {@link TmSession}
 *
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
final class SyncCommitTransactionManager extends AbstractTransactionManager {


    private final Map<Integer, XATransaction> xaTransactionMap = new HashMap<>();

    SyncCommitTransactionManager(TmSession session, TransactionOption option) {
        super(session, option);
    }


    @Override
    public final void commit() throws TransactionException {
        checkReadWrite("commit");
        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("tm transaction status[%s] isn't %s,can't commit."
                    , this.status, TransactionStatus.ACTIVE);
        }
        if (this.xaTransactionMap.size() == 1) {
            doOnePhaseCommit();
        } else {
            doTwoPhaseCommit();
        }

    }

    @Override
    public final void rollback() throws TransactionException {
        checkReadWrite("rollback");
        if (!ROLL_BACK_ABLE_SET.contains(this.status)) {
            throw new IllegalTransactionStateException("transaction status[%s] don't in %s,can't rollback."
                    , this.status, ROLL_BACK_ABLE_SET);
        }
        this.status = TransactionStatus.ROLLING_BACK;
        try {
            final Collection<XATransaction> transactions = this.xaTransactionMap.values();
            for (XATransaction xa : transactions) {
                xaEnd(xa);
            }
            for (XATransaction xa : transactions) {
                xaPrepare(xa);
            }
            for (XATransaction xa : transactions) {
                xaRollback(xa);
            }
            for (XATransaction xa : transactions) {
                xaForget(xa);
            }
            this.status = TransactionStatus.ROLLED_BACK;
        } catch (TransactionException e) {
            this.status = TransactionStatus.FAILED_ROLLBACK;
            throw e;
        }
    }

    @Override
    public final boolean transactionEnded() {
        return END_STATUS_SET.contains(this.status);
    }

    /*################################## blow package method ##################################*/

    final void addXaTransaction(TmSession tmSession, XATransaction startedXa) {
        if (tmSession != this.session) {
            throw new IllegalArgumentException("session error");
        }

        final int databaseIndex = startedXa.session().sessionFactory().databaseIndex();

        if (startedXa.status() != XATransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("RmSessionFactory[%s] XATransaction[%s] not start."
                    , databaseIndex
                    , startedXa.name()
            );
        }
        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("%s TmTransaction[%s] not active status."
                    , this.session.sessionFactory()
                    , this.name()
            );
        }
        if (this.xaTransactionMap.putIfAbsent(databaseIndex, startedXa) != null) {
            throw new TransactionUsageException(ErrorCode.TRANSACTION_ERROR
                    , "RmSessionFactory[%s] XATransaction duplication.", databaseIndex);
        }
    }

    /*################################## blow private method ##################################*/


    private void doOnePhaseCommit() {
        Iterator<XATransaction> iterator = this.xaTransactionMap.values().iterator();
        if (iterator.hasNext()) {
            XATransaction tx = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException(String.format(
                        "tm transaction[%s] have multi xa transaction resource,can't one phase commit.", this.name()));
            }
            try {
                this.status = TransactionStatus.COMMITTING;
                tx.end();
                tx.commitOnePhase();
                this.status = TransactionStatus.COMMITTED;
            } catch (TransactionException e) {
                this.status = TransactionStatus.FAILED_COMMIT;
                throw e;
            }
        } else {
            throw new IllegalStateException(String.format(
                    "tm transaction[%s] no xa transaction resource,can't one phase commit.", this.name()));
        }

    }

    private void doTwoPhaseCommit() {
        this.status = TransactionStatus.COMMITTING;
        try {
            final Collection<XATransaction> transactions = this.xaTransactionMap.values();
            for (XATransaction xa : transactions) {
                xaEnd(xa);
            }
            for (XATransaction xa : transactions) {
                xaPrepare(xa);
            }
            for (XATransaction xa : transactions) {
                xaCommit(xa);
            }
            for (XATransaction xa : transactions) {
                xaForget(xa);
            }
            this.status = TransactionStatus.COMMITTED;
        } catch (TransactionException e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            throw e;
        }
    }

    private void xaEnd(XATransaction tx) {
        final XATransactionStatus xaStatus = tx.status();
        switch (xaStatus) {
            case ACTIVE:
                tx.end();
                break;
            case IDLE:
            case PREPARED:
            case FAILED_COMMIT:
            case FAILED_PREPARE:
            case FAILED_ROLLBACK:
                // here ,classic, commit failure,now rollback.
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{} - {}] status is {},ignore end command.", this.name(), tx.name(), xaStatus);
                }
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s - %s] status[%s] error,couldn't execute end command."
                        , this.name(), tx.name(), xaStatus);
        }
    }

    private void xaPrepare(XATransaction tx) {
        final XATransactionStatus xaStatus = tx.status();
        switch (xaStatus) {
            case IDLE:
                tx.prepare();
                break;
            case PREPARED:
            case FAILED_COMMIT:
            case FAILED_ROLLBACK:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{} - {}] status is {},ignore prepare command."
                            , this.name(), tx.name(), xaStatus);
                }
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s - %s] status[%s] error,couldn't execute prepare command."
                        , this.name(), tx.name(), xaStatus);

        }
    }

    private void xaCommit(XATransaction tx) {
        final XATransactionStatus xaStatus = tx.status();
        switch (xaStatus) {
            case PREPARED:
            case FAILED_COMMIT:
                tx.commit();
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s - %s] status[%s] error,couldn't execute commit command."
                        , this.name(), tx.name(), xaStatus);

        }

    }

    private void xaRollback(XATransaction tx) {
        final XATransactionStatus xaStatus = tx.status();
        switch (xaStatus) {
            case PREPARED:
            case FAILED_COMMIT:
                tx.rollback();
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s - %s] status[%s] error,couldn't execute rollback command."
                        , this.name(), tx.name(), xaStatus);
        }
    }

    private void xaForget(XATransaction tx) {
        final XATransactionStatus xaStatus = tx.status();
        switch (xaStatus) {
            case COMMITTED:
            case ROLLED_BACK:
            case FAILED_FORGET:
                tx.forget();
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s-%s] status[%s] error,couldn't execute forget command."
                        , this.name(), tx.name(), xaStatus);
        }

    }


}
