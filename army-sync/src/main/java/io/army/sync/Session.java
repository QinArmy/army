package io.army.sync;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;

import javax.transaction.TransactionalException;
import java.io.Flushable;
import java.sql.Connection;

public interface Session extends GenericSyncApiSession, AutoCloseable, Flushable {

    @Override
    SessionFactory sessionFactory();

    Transaction sessionTransaction() throws NoSessionTransactionException;

    /**
     * <o>
     * <li>invoke {@link io.army.context.spi.CurrentSessionContext#removeCurrentSession(Session)},if need</li>
     * <li>invoke {@link Connection#close()}</li>
     * </o>
     *
     * @throws SessionException close session occur error.
     */
    @Override
    void close() throws SessionException;

    @Override
    void flush() throws SessionException;

    TransactionBuilder builder(boolean readOnly, Isolation isolation, int timeoutSeconds)
            throws TransactionalException;

    interface TransactionBuilder {

        TransactionBuilder name(@Nullable String txName);

        Transaction build() throws TransactionalException;

    }
}
