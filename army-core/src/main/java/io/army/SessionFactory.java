package io.army;

import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public interface SessionFactory extends AutoCloseable{

    String PACKAGE_TO_SCAN = "io.army.packageToScan";

    String ZONE_ID_OF_SCHEMA = "io.army.%s.%s.zoneId";

    Environment environment();

    SessionBuilder sessionBuilder();

    /**
     * @see CurrentSessionContext
     */
    Session currentSession();

    Dialect dialect();

    SQLDialect databaseActualSqlDialect();

    ZoneId zoneId();

    SchemaMeta schemaMeta();

    Map<Class<?>, TableMeta<?>> tableMetaMap();

    Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap();

    Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain();

    ProxySession getProxySession();

    /**
     * Destroy this <tt>SessionFactory</tt> then release all resources (caches,
     * connection pools, etc).
     * <p/>
     * It is the responsibility of the application to ensure that there are no
     * open {@link SessionFactory sessions} before calling this method asType the impact
     * on those {@link io.army.Session sessions} is indeterminate.
     * <p/>
     * No-ops if already {@link #isClosed closed}.
     *
     * @throws ArmyRuntimeException Indicates an issue closing the factory.
     */
    void close() throws ArmyRuntimeException;

    ShardingMode shardingMode();

    /**
     * Is this factory already closed?
     *
     * @return True if this factory is already closed; false otherwise.
     */
    boolean isClosed();


}
