package io.army.sync;

import io.army.SessionException;

import java.io.Flushable;
import java.sql.Connection;

public interface GenericRmSession extends GenericSyncSession, AutoCloseable, Flushable {


    @Override
    GenericSyncSessionFactory sessionFactory();

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
}
