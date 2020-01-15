package io.army;

import io.army.context.spi.CurrentSessionContext;
import io.army.meta.TableMeta;

import java.util.List;

public interface SessionFactory extends AutoCloseable{

    SessionFactoryOptions options();

    SessionBuilder builder();

    /**
     * @see CurrentSessionContext
     */
    Session currentSession();

    /**
     * Destroy this <tt>SessionFactory</tt> and release all resources (caches,
     * connection pools, etc).
     * <p/>
     * It is the responsibility of the application to ensure that there are no
     * open {@link SessionFactory sessions} before calling this method as the impact
     * on those {@link io.army.Session sessions} is indeterminate.
     * <p/>
     * No-ops if already {@link #isClosed closed}.
     *
     * @throws ArmyRuntimeException Indicates an issue closing the factory.
     */
    void close() throws ArmyRuntimeException;

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean isClosed();

    List<TableMeta<?>> tables();



}
