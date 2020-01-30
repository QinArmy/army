package io.army;

import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import java.util.Map;

public interface SessionFactory extends AutoCloseable{

    Environment environment();

    SessionBuilder sessionBuilder();

    /**
     * @see CurrentSessionContext
     */
    Session currentSession();

    Dialect dialect();

    SQLDialect databaseActualSqlDialect();

    SchemaMeta schemaMeta();

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


}
