package io.army.sync;

import io.army.session.Session;
import io.army.session.SessionFactory;
import io.army.session.SessionFactoryException;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 */
public interface LocalSessionFactory extends SyncSessionFactory, AutoCloseable {


    SessionContext currentSessionContext() throws SessionFactoryException;

    SessionBuilder builder();


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


    interface SessionBuilder extends SessionBuilderSpec<SessionBuilder, SyncLocalSession> {


    }


}
