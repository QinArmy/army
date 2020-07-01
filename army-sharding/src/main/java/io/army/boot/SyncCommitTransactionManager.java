package io.army.boot;

import io.army.TmSession;
import io.army.dialect.TransactionOption;
import io.army.tx.*;

/**
 * this class is a implementation of {@link TmTransaction}
 * ,use {@link javax.transaction.xa.XAResource} to finish XA transaction.
 */
final class SyncCommitTransactionManager extends AbstractTransactionManager {


    SyncCommitTransactionManager(TmSession session, TransactionOption option) {
        super(session, option);
    }


    @Override
    public final void commit() throws TransactionException {
        checkReadWrite("commit");
        if (this.status != TransactionStatus.ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't commit."
                    , this.status, TransactionStatus.ACTIVE);
        }

        this.status = TransactionStatus.COMMITTING;
        try {
            for (XATransaction xa : this.xaTransactionSet) {
                xaEnd(xa);
            }
            for (XATransaction xa : this.xaTransactionSet) {
                xaPrepare(xa);
            }
            for (XATransaction xa : this.xaTransactionSet) {
                xaCommit(xa);
            }
            this.status = TransactionStatus.COMMITTED;
        } catch (TransactionException e) {
            this.status = TransactionStatus.FAILED_COMMIT;
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
            for (XATransaction xa : this.xaTransactionSet) {
                xaEnd(xa);
            }
            for (XATransaction xa : this.xaTransactionSet) {
                xaPrepare(xa);
            }
            for (XATransaction xa : this.xaTransactionSet) {
                xaRollback(xa);
            }
            this.status = TransactionStatus.ROLLED_BACK;
        } catch (TransactionException e) {
            this.status = TransactionStatus.FAILED_ROLLBACK;
        }
    }

    /*################################## blow package method ##################################*/

    void addXaTransaction(TmSession session, XaResourceTransaction xa) {
        if (this.session != session) {
            throw new IllegalArgumentException("session error");
        }
        this.xaTransactionSet.add(xa);
    }


}
