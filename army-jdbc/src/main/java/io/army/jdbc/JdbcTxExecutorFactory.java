package io.army.jdbc;

import io.army.meta.ServerMeta;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.FactoryInfo;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.StmtExecutor;

import javax.sql.DataSource;

final class JdbcTxExecutorFactory implements ExecutorFactory {

    static JdbcTxExecutorFactory create(DataSource dataSource, ServerMeta serverMeta, FactoryInfo info) {
        return new JdbcTxExecutorFactory(dataSource, serverMeta, info);
    }

    private final DataSource dataSource;

    private final ServerMeta serverMeta;

    private final FactoryInfo info;


    private JdbcTxExecutorFactory(DataSource dataSource, ServerMeta serverMeta, FactoryInfo info) {
        this.dataSource = dataSource;
        this.serverMeta = serverMeta;
        this.info = info;
    }

    @Override
    public ServerMeta serverMeta() {
        return null;
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
