package io.army.sync;

import io.army.tx.*;

abstract class AbstractSyncTransaction extends AbstractGenericTransaction implements GenericSyncTransaction {

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
        if (TransactionStatus.END_STATUS_SET.contains(this.status())) {
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
        if (!this.readOnly && !TransactionStatus.END_STATUS_SET.contains(this.status())) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't close."
                    , this.status(), TransactionStatus.END_STATUS_SET);
        }
    }


}
