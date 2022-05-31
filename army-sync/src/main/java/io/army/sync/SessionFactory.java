package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.GenericSession;
import io.army.session.GenericSessionFactory;
import io.army.session.SessionException;
import io.army.session.SessionFactoryException;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 */
public interface SessionFactory extends GenericSessionFactory, AutoCloseable {


    SessionContext currentSessionContext() throws SessionFactoryException;

    SessionBuilder builder();


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


    interface SessionBuilder {

        SessionBuilder name(@Nullable String name);

        /**
         * Optional,default is {@link SessionFactory#readonly()}
         */
        SessionBuilder readonly(boolean readonly);

        Session build() throws SessionException;

    }
}
