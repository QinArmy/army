package io.army.boot.sync;

import io.army.GenericFactoryBuilderImpl;
import io.army.dialect.Database;
import io.army.interceptor.DomainAdvice;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

abstract class SyncSessionFactoryParams extends GenericFactoryBuilderImpl {


    private Collection<DomainAdvice> domainInterceptors;

    private SyncSessionFactoryParams() {

    }

    public Collection<DomainAdvice> getDomainInterceptors() {
        return domainInterceptors;
    }

    public void setDomainInterceptors(Collection<DomainAdvice> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
    }

    static final class Single extends SyncSessionFactoryParams {

        private DataSource dataSource;

        private Database sqlDialect;

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Database getSqlDialect() {
            return sqlDialect;
        }

        public void setSqlDialect(Database sqlDialect) {
            this.sqlDialect = sqlDialect;
        }
    }


    static final class Sharding extends SyncSessionFactoryParams {

        private Map<String, DataSource> dataSourceMap;

        private Map<String, Database> sqlDialectMap;

        private Database defaultSqlDialect;

        public Map<String, DataSource> getDataSourceMap() {
            return dataSourceMap;
        }

        public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
            this.dataSourceMap = dataSourceMap;
        }

        public Map<String, Database> getSqlDialectMap() {
            return sqlDialectMap;
        }

        public void setSqlDialectMap(Map<String, Database> sqlDialectMap) {
            this.sqlDialectMap = sqlDialectMap;
        }

        public Database getDefaultSqlDialect() {
            return defaultSqlDialect;
        }

        public void setDefaultSqlDialect(Database defaultSqlDialect) {
            this.defaultSqlDialect = defaultSqlDialect;
        }
    }
}
