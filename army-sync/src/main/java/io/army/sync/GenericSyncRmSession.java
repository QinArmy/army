package io.army.sync;

import io.army.SessionException;
import io.army.tx.GenericTransaction;
import io.army.tx.NoSessionTransactionException;

import java.io.Flushable;
import java.sql.Connection;

public interface GenericSyncRmSession extends GenericSyncSession, AutoCloseable, Flushable {


    @Override
    GenericSyncSessionFactory sessionFactory();

    GenericTransaction sessionTransaction() throws NoSessionTransactionException;

    /**
     * <o>
     * <li>invoke {@link io.army.context.spi.CurrentSessionContext#removeCurrentSession(GenericSyncSession)},if need</li>
     * <li>invoke {@link Connection#close()}</li>
     * </o>
     *
     * @throws SessionException close session occur error.
     */
    @Override
    void close() throws SessionException;

    @Override
    void flush() throws SessionException;
}
