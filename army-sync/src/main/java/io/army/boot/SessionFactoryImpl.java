package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ArmyRuntimeException;
import io.army.ProxySession;
import io.army.ShardingMode;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class SessionFactoryImpl implements InnerSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryImpl.class);

    private final Environment env;

    private final DataSource dataSource;

    private final Map<Class<?>, TableMeta<?>> classTableMetaMap;

    private final Dialect dialect;

    private final SQLDialect databaseActualSqlDialect;

    private final SchemaMeta schemaMeta;

    private final ZoneId zoneId;

    private final ProxySession proxySession;

    final CurrentSessionContext currentSessionContext;

    private final Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap;

    private final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

    private final boolean readOnly;

    private boolean closed;


    SessionFactoryImpl(Environment env, DataSource dataSource, SchemaMeta schemaMeta,
                       Class<?> currentSessionContextClass,
                       SQLDialect sqlDialect)
            throws ArmyRuntimeException {
        Assert.notNull(env, "env required");
        Assert.notNull(schemaMeta, "schemaMeta required");
        Assert.notNull(dataSource, "dataSource required");

        this.env = env;
        this.dataSource = dataSource;
        this.schemaMeta = schemaMeta;
        this.currentSessionContext = SessionFactoryUtils.buildCurrentSessionContext(this, currentSessionContextClass);

        this.proxySession = new ProxySessionImpl(this.currentSessionContext);
        this.readOnly = env.getProperty(Environment.READONLY, Boolean.class, Boolean.FALSE);

        this.zoneId = SessionFactoryUtils.createZoneId(this.env, this.schemaMeta);

        List<String> packagesToScan = env.getRequiredPropertyList(PACKAGE_TO_SCAN, String[].class);
        this.classTableMetaMap = SessionFactoryUtils.scanPackagesForMeta(this.schemaMeta, packagesToScan);

        Pair<Dialect, SQLDialect> pair = SessionFactoryUtils.createDialect(sqlDialect, dataSource, this);
        this.dialect = pair.getFirst();
        this.databaseActualSqlDialect = pair.getSecond();

        SessionFactoryUtils.GeneratorWrapper generatorWrapper =
                SessionFactoryUtils.createGeneratorWrapper(this.classTableMetaMap.values(), this.env);
        this.fieldGeneratorMap = generatorWrapper.getGeneratorChain();
        this.tableGeneratorChain = generatorWrapper.getTableGeneratorChain();
    }


    @Override
    public void close() throws ArmyRuntimeException {
        this.closed = true;
    }

    @Override
    public ProxySession proxySession() {
        return this.proxySession;
    }

    @Override
    public final SessionBuilder builder() {
        return new SessionBuilderImpl();
    }

    @Override
    public final boolean hasCurrentSession() {
        return this.currentSessionContext.hasCurrentSession();
    }

    @Override
    public boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass) {
        return currentSessionContextClass.isInstance(this.currentSessionContext);
    }

    @Override
    public boolean readonly() {
        return this.readOnly;
    }

    @Override
    public CurrentSessionContext currentSessionContext() {
        return this.currentSessionContext;
    }

    @Override
    public final boolean isClosed() {
        return this.closed;
    }

    @Override
    public final Dialect dialect() {
        return dialect;
    }

    @Override
    public final SQLDialect databaseActualSqlDialect() {
        return databaseActualSqlDialect;
    }

    @Override
    public final ZoneId zoneId() {
        return zoneId;
    }

    @Override
    public final SchemaMeta schemaMeta() {
        return schemaMeta;
    }

    @Override
    public final Map<Class<?>, TableMeta<?>> tableMetaMap() {
        return this.classTableMetaMap;
    }

    @Override
    public Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap() {
        return fieldGeneratorMap;
    }

    @Override
    public Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain() {
        return tableGeneratorChain;
    }


    @Override
    public final DataSource dataSource() {
        return this.dataSource;
    }

    @Override
    public final Environment environment() {
        return env;
    }

    @Override
    public final ShardingMode shardingMode() {
        return ShardingMode.NO_SHARDING;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SessionFactoryImpl.class.getSimpleName() + "[", "]")
                .add("classTableMetaMap=" + classTableMetaMap.entrySet())
                .add("dialect=" + dialect)
                .add("closed=" + closed)
                .toString();
    }

    void initSessionFactory() throws ArmyAccessException {
        // 1.  migration meta
        SessionFactoryInitializer initializer = new DefaultSessionFactoryInitializer(this);
        initializer.onStartup();
    }

    /*################################## blow private method ##################################*/





}
