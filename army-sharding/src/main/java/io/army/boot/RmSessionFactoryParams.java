package io.army.boot;

import io.army.dialect.SQLDialect;

import javax.sql.DataSource;

final class RmSessionFactoryParams {

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
