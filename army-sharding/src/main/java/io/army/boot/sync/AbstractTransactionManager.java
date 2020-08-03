package io.army.boot.sync;

import io.army.sync.TmSession;
import io.army.tx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 */
abstract class AbstractTransactionManager extends AbstractSyncTransaction implements TmTransaction {

    final Logger LOG = LoggerFactory.getLogger(getClass());

    final TmSession session;

    final Set<XATransaction> xaTransactionSet = new HashSet<>();

    TransactionStatus status = TransactionStatus.NOT_ACTIVE;

    AbstractTransactionManager(TmSession session, TransactionOption option) {
        super(option);
        this.session = session;
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
        // no-op
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


}
