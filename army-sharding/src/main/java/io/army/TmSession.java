package io.army;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Select;
import io.army.criteria.Update;
import io.army.sync.GenericSingleDatabaseSyncSession;
import io.army.sync.Session;
import io.army.tx.TmTransaction;

import java.io.Flushable;
import java.sql.Connection;


/**
 * This class have blow feature:
 * <ul>
 *     <li>manage {@code io.army.boot.RmSession}</li>
 *     <li>manage session cache</li>
 *     <li>route {@code io.army.boot.RmSession} by {@link Select},{@link Insert} ,{@link Update},{@link Delete}</li>
 * </ul>
 * {@code Tm}  representing Transaction Manager.
 */
public interface TmSession extends GenericSingleDatabaseSyncSession, AutoCloseable, Flushable {

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
