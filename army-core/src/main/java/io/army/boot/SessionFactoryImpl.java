package io.army.boot;

import io.army.*;
import io.army.boot.migratioin.Meta2Schema;
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
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

    private final Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap;

    private final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;


    private boolean closed;


    SessionFactoryImpl(Environment env, DataSource dataSource, SchemaMeta schemaMeta,
                       @Nullable SQLDialect sqlDialect)
            throws ArmyRuntimeException {
        Assert.notNull(env, "env required");
        Assert.notNull(schemaMeta, "schemaMeta required");
        Assert.notNull(dataSource, "dataSource required");

        this.env = env;
        this.dataSource = dataSource;
        this.schemaMeta = schemaMeta;
        this.zoneId = SessionFactoryUtils.createZoneId(this.env,this.schemaMeta);

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
    public SessionBuilder sessionBuilder() {
        return new SessionBuilderImpl(this);
    }

    @Override
    public Session currentSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws ArmyRuntimeException {
        this.closed = true;
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
    public ProxySession getProxySession() {
        return null;
    }

    @Override
    public final DataSource getDataSource() {
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
        migrationIfNeed();


    }

    /*################################## blow private method ##################################*/


    private void migrationIfNeed() throws ArmyAccessException {
        try {
            // 1. generate dml
            Map<String, List<String>> tableSqlMap;
            tableSqlMap = Meta2Schema.build().migrate(classTableMetaMap.values(), dataSource.getConnection(), dialect);

            if (LOG.isDebugEnabled()) {
                printMigrationSql(tableSqlMap);
            }
            // 2. execute dml
            executeDDL(tableSqlMap);
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }

    private void executeDDL(Map<String, List<String>> tableSqlMap) {
        if (tableSqlMap.isEmpty()) {
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            DDLSQLExecutor ddlsqlExecutor = new BatchDDLSQLExecutor(connection);
            ddlsqlExecutor.executeDDL(tableSqlMap);
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }

    private void printMigrationSql(Map<String, List<String>> tableSqlMap) {
        for (Map.Entry<String, List<String>> e : tableSqlMap.entrySet()) {
            LOG.debug("\ntableMeta:{}\n", e.getKey());
            for (String sql : e.getValue()) {
                LOG.debug("{}", sql);
            }
            LOG.debug("\n");
        }
    }


}
