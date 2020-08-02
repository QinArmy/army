package io.army.sync;

import io.army.ArmyRuntimeException;
import io.army.GenericSession;
import io.army.GenericSessionFactory;
import io.army.SessionFactoryException;

public interface GenericSyncSessionFactory extends GenericSessionFactory, AutoCloseable {




    /**
     * Destroy this <tt>SessionFactory</tt> then release all resources (caches,
     * connection pools, etc).
     * <p/>
     * It is the responsibility of the application to ensure that there are no
     * open {@link GenericSessionFactory sessions} before calling this method asType the impact
     * on those {@link GenericSession sessions} is indeterminate.
     * <p/>
     * No-ops if already {@link #closed closed}.
     *
     * @throws ArmyRuntimeException Indicates an issue closing the factory.
     */
    @Override
    void close() throws SessionFactoryException;


}
