package io.army.sync;

import io.army.session.Session;
import io.army.session.SessionFactory;
import io.army.session.SessionFactoryException;

/**
 * <p>
 * This interface representing blocking way session factory.
 * This interface is only base interface of :
 * <ul>
 *     <li>{@link SyncLocalSessionFactory}</li>
 *     <li>{@link SyncRmSessionFactory}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SyncSessionFactory extends SessionFactory, AutoCloseable {

    /**
     * Destroy this <tt>GenericSyncSessionFactory</tt> then release all resources (caches,
     * connection pools, etc).
     * <p/>
     * It is the responsibility of the application to ensure that there are no
     * open {@link SessionFactory sessions} before calling this method asType the impact
     * on those {@link Session sessions} is indeterminate.
     * <p/>
     * No-ops if already {@link #isClosed closed}.
     *
     * @throws SessionFactoryException Indicates an issue closing the factory.
     */
    @Override
    void close() throws SessionFactoryException;

}
