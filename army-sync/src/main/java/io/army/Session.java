package io.army;

import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;

import java.io.Flushable;
import java.sql.Connection;

public interface Session extends GenericSyncSession, AutoCloseable, Flushable {

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

    TransactionBuilder builder() throws SessionException;

    interface TransactionBuilder {

        TransactionBuilder readOnly(boolean readOnly);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder timeout(int seconds);

        TransactionBuilder name(@Nullable String txName);

        Transaction build();

    }
}
