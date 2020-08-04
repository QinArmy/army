package io.army.boot.sync;

import io.army.tx.*;

import java.util.EnumSet;

abstract class AbstractSyncTransaction extends AbstractGenericTransaction implements GenericSyncTransaction {

    static final EnumSet<TransactionStatus> ROLL_BACK_ABLE_SET = EnumSet.of(
            TransactionStatus.ACTIVE,
            TransactionStatus.MARKED_ROLLBACK,
            TransactionStatus.FAILED_COMMIT
    );

    static final EnumSet<TransactionStatus> END_STATUS_SET = EnumSet.of(
            TransactionStatus.COMMITTED,
            TransactionStatus.ROLLED_BACK
    );

    static final EnumSet<TransactionStatus> ROLL_BACK_ONLY_ABLE_SET = EnumSet.of(
            TransactionStatus.ACTIVE,
            TransactionStatus.MARKED_ROLLBACK
    );

    protected boolean rollbackOnly;

    AbstractSyncTransaction(TransactionOption option) {
        super(option);
    }

    @Override
    public final boolean rollbackOnly() {
        return this.rollbackOnly;
    }

    @Override
    public final boolean nonActive() {
        return this.status() != TransactionStatus.ACTIVE;
    }

    /*################################## blow package template method ##################################*/

    public abstract TransactionStatus status();


    /*################################## blow package method ##################################*/


    final void checkTransaction() {
        if (END_STATUS_SET.contains(this.status())) {
            throw new TransactionClosedException("transaction ended.");
        }
    }

    final void checkReadWrite(String action) {
        TransactionStatus status = this.status();
        if (!this.readOnly) {
            throw new ReadOnlyTransactionException("transaction[status=%s]can't %s."
                    , status, action);
        }
    }

    final void assertCanClose() {
        if (!this.readOnly && !LocalTransaction.END_STATUS_SET.contains(this.status())) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't close."
                    , this.status(), LocalTransaction.END_STATUS_SET);
        }
    }


}
