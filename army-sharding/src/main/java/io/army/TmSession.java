package io.army;

import io.army.tx.TmTransaction;

import java.io.Flushable;
import java.sql.Connection;


/**
 * {@code Tm}  representing Transaction Manager.
 */
public interface TmSession extends GenericSyncApiSession, AutoCloseable, Flushable {

    @Override
    TmSessionFactory sessionFactory();

    TmTransaction sessionTransaction();

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
