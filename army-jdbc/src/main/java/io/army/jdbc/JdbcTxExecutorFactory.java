package io.army.jdbc;

import io.army.meta.ServerMeta;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.SqlExecutor;

import javax.sql.DataSource;

final class JdbcTxExecutorFactory implements ExecutorFactory {

    static JdbcTxExecutorFactory create(DataSource dataSource) {
        return new JdbcTxExecutorFactory(dataSource);
    }

    private final DataSource dataSource;


    JdbcTxExecutorFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MetaExecutor createMetaExecutor() throws Exception {
        return JdbcMetaExecutor.create(this.dataSource.getConnection());
    }

    @Override
    public SqlExecutor createSqlExecutor() throws Exception {
        return null;
    }




}
