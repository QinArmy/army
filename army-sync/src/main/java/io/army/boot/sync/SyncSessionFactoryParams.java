package io.army.boot.sync;

import io.army.boot.GenericSessionFactoryParams;
import io.army.dialect.SQLDialect;
import io.army.interceptor.DomainInterceptor;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

abstract class SyncSessionFactoryParams extends GenericSessionFactoryParams {


    private Collection<DomainInterceptor> domainInterceptors;

    private SyncSessionFactoryParams() {

    }

    public Collection<DomainInterceptor> getDomainInterceptors() {
        return domainInterceptors;
    }

    public void setDomainInterceptors(Collection<DomainInterceptor> domainInterceptors) {
        this.domainInterceptors = domainInterceptors;
    }

    static final class Single extends SyncSessionFactoryParams {

        private DataSource dataSource;

        private SQLDialect sqlDialect;

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public SQLDialect getSqlDialect() {
            return sqlDialect;
        }

        public void setSqlDialect(SQLDialect sqlDialect) {
            this.sqlDialect = sqlDialect;
        }
    }


    static final class Sharding extends SyncSessionFactoryParams {

        private Map<String, DataSource> dataSourceMap;

        private Map<String, SQLDialect> sqlDialectMap;

        private SQLDialect defaultSqlDialect;

        public Map<String, DataSource> getDataSourceMap() {
            return dataSourceMap;
        }

        public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
            this.dataSourceMap = dataSourceMap;
        }

        public Map<String, SQLDialect> getSqlDialectMap() {
            return sqlDialectMap;
        }

        public void setSqlDialectMap(Map<String, SQLDialect> sqlDialectMap) {
            this.sqlDialectMap = sqlDialectMap;
        }

        public SQLDialect getDefaultSqlDialect() {
            return defaultSqlDialect;
        }

        public void setDefaultSqlDialect(SQLDialect defaultSqlDialect) {
            this.defaultSqlDialect = defaultSqlDialect;
        }
    }
}
