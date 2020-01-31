package io.army.boot;

import io.army.*;
import io.army.boot.migratioin.Meta2Schema;
import io.army.criteria.DDLSQLExecutor;
import io.army.dialect.Dialect;
import io.army.dialect.SQL;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import io.army.generator.GeneratorFactory;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

class SessionFactoryImpl implements InnerSessionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryImpl.class);

    private final Environment env;

    private final DataSource dataSource;

    private final Map<Class<?>, TableMeta<?>> classTableMetaMap;

    private final Dialect dialect;

    private final SQLDialect databaseActualSqlDialect;

    private final SchemaMeta schemaMeta;

    private final ZoneId zoneId;

    private final  Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap;

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
        this.zoneId = createZoneId();

        List<String> packagesToScan = env.getRequiredPropertyList(PACKAGE_TO_SCAN, String[].class);
        this.classTableMetaMap = SessionFactoryUtils.scanPackagesForMeta(this.schemaMeta, packagesToScan);
        Pair<Dialect, SQLDialect> pair = SessionFactoryUtils.createDialect(sqlDialect, dataSource, this);
        this.dialect = pair.getFirst();
        this.databaseActualSqlDialect = pair.getSecond();

        this.fieldGeneratorMap = createFieldGenerator();
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
    public final DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public final Environment environment() {
        return env;
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


    private Map<FieldMeta<?, ?>, MultiGenerator> createFieldGenerator() {
        Map<FieldMeta<?, ?>, MultiGenerator> generatorMap = new HashMap<>();

        for (TableMeta<?> tableMeta : classTableMetaMap.values()) {
            for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
                if (fieldMeta.generator() != null) {
                    generatorMap.put(fieldMeta, GeneratorFactory.getGenerator(fieldMeta,this.env));
                }
            }
        }
        return Collections.unmodifiableMap(generatorMap);
    }

    private void migrationIfNeed() throws ArmyAccessException {
        try {
            // 1. generate sql
            Map<String, List<String>> tableSqlMap;
            tableSqlMap = Meta2Schema.build().migrate(classTableMetaMap.values(), dataSource.getConnection(), dialect);

            if (LOG.isDebugEnabled()) {
                printMigrationSql(tableSqlMap);
            }
            // 2. execute sql
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
            LOG.debug("\ntable:{}\n", e.getKey());
            for (String sql : e.getValue()) {
                LOG.debug("{}", sql);
            }
            LOG.debug("\n");
        }
    }

    private ZoneId createZoneId() {
        String zoneIdText = env.getProperty(String.format(schemaMeta.catalog(), schemaMeta.schema()));
        ZoneId zoneId;
        if (StringUtils.hasText(zoneIdText)) {
            zoneId = ZoneId.of(zoneIdText);
        } else {
            zoneId = ZoneId.systemDefault();
        }
        return zoneId;
    }


}
