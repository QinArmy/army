package io.army.boot;

import io.army.*;
import io.army.dialect.Dialect;
import io.army.dialect.SQLDialect;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.env.Environment;
import io.army.env.StandardEnvironment;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public class MockSessionFactoryBuilder implements SessionFactoryBuilder {

    private SQLDialect sqlDialect;

    private String catalog;

    private String schemal;

    private StandardEnvironment env;



    @Override
    public SessionFactoryBuilder datasource(DataSource dataSource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionFactoryBuilder sqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
        return this;
    }

    @Override
    public SessionFactoryBuilder catalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    @Override
    public SessionFactoryBuilder schema(String schema) {
        this.schemal = schema;
        return this;
    }

    @Override
    public SessionFactoryBuilder environment(StandardEnvironment environment) {
        this.env = environment;
        return this;
    }

    @Override
    public SessionFactory build() {
        Environment environment = this.env ;
        if(environment == null){
            environment = new StandardEnvironment();
        }
        SchemaMeta schemaMeta = SessionFactoryBuilderImpl.createSchema(catalog,schemal);
        SQLDialect sqlDialect = this.sqlDialect;
        if(sqlDialect == null){
            sqlDialect = SQLDialect.MySQL57;
        }
        return new MockSessionFactory(environment,schemaMeta,sqlDialect);
    }

    private static class MockSessionFactory implements SessionFactory {

        private final Environment env;

        private final SchemaMeta schemaMeta;

        private final Dialect dialect;

        private final ZoneId zoneId;

        private boolean closed;

        MockSessionFactory(Environment env, SchemaMeta schemaMeta,
                           SQLDialect sqlDialect)
                throws ArmyRuntimeException {
            this.env = env;
            this.schemaMeta = schemaMeta;
            this.dialect = createDialect(this,sqlDialect);
            this.zoneId = SessionFactoryUtils.createZoneId(this.env,schemaMeta);
        }

        @Override
        public Environment environment() {
            return env;
        }

        @Override
        public SessionBuilder sessionBuilder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Session currentSession() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Dialect dialect() {
            return dialect;
        }

        @Override
        public SQLDialect databaseActualSqlDialect() {
            return dialect.sqlDialect();
        }

        @Override
        public ZoneId zoneId() {
            return zoneId;
        }

        @Override
        public SchemaMeta schemaMeta() {
            return schemaMeta;
        }

        @Override
        public Map<Class<?>, TableMeta<?>> tableMetaMap() {
           throw new UnsupportedOperationException();
        }

        @Override
        public Map<FieldMeta<?, ?>, MultiGenerator> fieldGeneratorMap() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProxySession getProxySession() {
            return null;
        }

        @Override
        public void close() throws ArmyRuntimeException {
            this.closed = true;
        }

        @Override
        public boolean isClosed() {
            return this.closed;
        }

        @Override
        public ShardingMode shardingMode() {
            return null;
        }
    }

    private static Dialect createDialect(SessionFactory sessionFactory, SQLDialect sqlDialect) {
        Dialect dialect;
        switch (sqlDialect) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                dialect = MySQLDialectFactory.createMySQLDialect(sqlDialect, sessionFactory);
                break;
            case SQL_Server:
            case OceanBase:
            case Postgre:
            case Oracle:
            case Db2:
            default:
                throw new IllegalArgumentException();
        }
        return dialect;
    }
}
