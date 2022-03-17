package io.army.sync;

import io.army.SessionException;
import io.army.SessionFactoryException;
import io.army.lang.Nullable;
import io.army.session.FactoryMode;
import io.army.session.GenericSession;
import io.army.session.GenericSessionFactory;

/**
 * This interface representing single database(or single schema).
 * This interface run only below:
 * <ul>
 *     <li>{@link FactoryMode#NO_SHARDING}</li>
 *     <li>{@link FactoryMode#TABLE_SHARDING}</li>
 * </ul>
 */
public interface SessionFactory extends GenericSessionFactory, AutoCloseable {


    CurrentSessionContext currentSessionContext() throws SessionFactoryException;

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
