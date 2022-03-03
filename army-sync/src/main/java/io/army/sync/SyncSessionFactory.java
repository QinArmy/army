package io.army.sync;

import io.army.SessionFactoryException;
import io.army.session.GenericSession;
import io.army.session.GenericSessionFactory;


/**
 * This interface representing a sync session factory.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link SessionFactory}</li>
 *     <li>{@code io.army.boot.sync.RmSessionFactory}</li>
 *     <li>{@code io.army.TmSessionFactory}</li>
 * </ul>
 */
public interface SyncSessionFactory extends GenericSessionFactory, AutoCloseable {


    /**
     * Destroy this <tt>GenericSyncSessionFactory</tt> then release all resources (caches,
     * connection pools, etc).
     * <p/>
     * It is the responsibility of the application to ensure that there are no
     * open {@link GenericSessionFactory sessions} before calling this method asType the impact
     * on those {@link GenericSession sessions} is indeterminate.
     * <p/>
     * No-ops if already {@link #factoryClosed closed}.
     *
     * @throws SessionFactoryException Indicates an issue closing the factory.
     */
    @Override
    void close() throws SessionFactoryException;


}
