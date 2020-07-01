package io.army.boot;

import io.army.TmSession;
import io.army.dialect.TransactionOption;
import io.army.tx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

abstract class AbstractTransactionManager extends AbstractSyncTransaction implements TmTransaction {

    final Logger LOG = LoggerFactory.getLogger(getClass());

    private final TransactionOption option;

    final TmSession session;

    final Set<XATransaction> xaTransactionSet = new HashSet<>();

    TransactionStatus status = TransactionStatus.NOT_ACTIVE;

    AbstractTransactionManager(TmSession session, TransactionOption option) {
        super(option);
        this.session = session;
        this.option = option;
    }

    @Override
    public final TmSession session() {
        return this.session;
    }

    @Override
    public final TransactionStatus status() {
        return this.status;
    }

    @Override
    public final void start() throws TransactionException {
        if (this.status != TransactionStatus.NOT_ACTIVE) {
            throw new IllegalTransactionStateException("transaction status[%s] isn't %s,can't start transaction."
                    , this.status, TransactionStatus.NOT_ACTIVE);
        }
        this.status = TransactionStatus.ACTIVE;
    }

    @Override
    public final void flush() throws TransactionException {

    }

    @Override
    public void close() throws TransactionException {
        // no-op
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

    /*################################## blow private method ##################################*/

    final XATransaction xaEnd(XATransaction tx) {
        switch (tx.status()) {
            case ACTIVE:
                tx.end();
                break;
            case IDLE:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{}-{}] status is IDLE,ignore end command.", this.name, tx.name());
                }
                break;
            case PREPARED:
                // here ,classic, commit failure,now rollback.
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{}-{}] status is PREPARE,ignore end command.", this.name, tx.name());
                }
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s-%s] status[%s] error,couldn't execute end command."
                        , this.name, tx.name(), tx.status());

        }
        return tx;
    }

    final XATransaction xaPrepare(XATransaction tx) {
        switch (tx.status()) {
            case IDLE:
                tx.prepare();
                break;
            case PREPARED:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("transaction[{}-{}] status is PREPARE,ignore prepare command.", this.name, tx.name());
                }
                break;
            default:
                throw new IllegalTransactionStateException(
                        "transaction[%s-%s] status[%s] error,couldn't execute prepare command."
                        , this.name, tx.name(), tx.status());

        }
        return tx;
    }

    final void xaCommit(XATransaction tx) {
        if (tx.status() != XATransactionStatus.PREPARED) {
            throw new IllegalTransactionStateException(
                    "transaction[%s-%s] status[%s] error,couldn't execute commit command."
                    , this.name, tx.name(), tx.status());
        }
        tx.commit();
    }

    final void xaRollback(XATransaction tx) {
        if (tx.status() != XATransactionStatus.PREPARED) {
            throw new IllegalTransactionStateException(
                    "transaction[%s-%s] status[%s] error,couldn't execute rollback command."
                    , this.name, tx.name(), tx.status());
        }
        tx.rollback();
    }


}
