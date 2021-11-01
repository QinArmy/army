package io.army.jdbc;

import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.StmtExecutor;

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
    public StmtExecutor createSqlExecutor() throws Exception {
        return null;
    }




}
