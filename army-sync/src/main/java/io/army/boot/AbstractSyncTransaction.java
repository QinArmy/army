package io.army.boot;

import io.army.dialect.TransactionOption;
import io.army.tx.*;

import java.util.EnumSet;

abstract class AbstractSyncTransaction implements GenericSyncTransaction {

    static final EnumSet<TransactionStatus> ROLL_BACK_ABLE_SET = EnumSet.of(
            TransactionStatus.ACTIVE,
            TransactionStatus.MARKED_ROLLBACK,
            TransactionStatus.FAILED_COMMIT
    );

    static final EnumSet<TransactionStatus> END_ABLE_SET = EnumSet.of(
            TransactionStatus.COMMITTED,
            TransactionStatus.ROLLED_BACK
    );

    static final EnumSet<TransactionStatus> ROLL_BACK_ONLY_ABLE_SET = EnumSet.of(
            TransactionStatus.ACTIVE,
            TransactionStatus.MARKED_ROLLBACK
    );


    final Isolation isolation;

    final boolean readOnly;

    final String name;

    final long endMills;

    AbstractSyncTransaction(TransactionOption option) {
        this.readOnly = option.readOnly();
        this.isolation = option.isolation();

        this.name = option.name();
        int timeout = option.timeout();
        if (timeout > 0) {
            this.endMills = (System.currentTimeMillis() + timeout * 1000L);
        } else {
            this.endMills = timeout;
        }
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final Isolation isolation() {
        return this.isolation;
    }


    @Override
    public final boolean readOnly() {
        return this.readOnly;
    }

    @Override
    public final long getTimeToLiveInMillis() throws TransactionTimeOutException {
        checkTransaction();
        long liveInMills = this.endMills - System.currentTimeMillis();
        if (liveInMills < 0) {
            throw new TransactionTimeOutException("transaction[name:%s] timeout,live in mills is %s ."
                    , this.name, liveInMills);
        }
        return liveInMills;
    }

    @Override
    public final boolean rollbackOnly() {
        return this.status() == TransactionStatus.MARKED_ROLLBACK;
    }

    /*################################## blow package template method ##################################*/

    abstract TransactionStatus status();

    /*################################## blow package method ##################################*/


    final void checkTransaction() {
        if (END_ABLE_SET.contains(this.status())) {
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
        if (!this.readOnly && !LocalTransaction.END_ABLE_SET.contains(this.status())) {
            throw new IllegalTransactionStateException("transaction status[%s] not in %s,can't close."
                    , this.status(), LocalTransaction.END_ABLE_SET);
        }
    }


}
