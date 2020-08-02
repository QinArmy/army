package io.army.boot.sync;

import io.army.dialect.Database;

import javax.sql.DataSource;

final class RmSessionFactoryParams {

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
