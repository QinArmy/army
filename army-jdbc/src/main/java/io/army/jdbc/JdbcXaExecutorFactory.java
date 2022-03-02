package io.army.jdbc;

import io.army.meta.ServerMeta;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.FactoryInfo;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.StmtExecutor;

import javax.sql.XADataSource;

final class JdbcXaExecutorFactory implements ExecutorFactory {

    static JdbcXaExecutorFactory create(XADataSource dataSource, ServerMeta serverMeta, FactoryInfo info) {
        return new JdbcXaExecutorFactory(dataSource, serverMeta, info);
    }

    private final XADataSource dataSource;

    private final ServerMeta serverMeta;

    private final FactoryInfo info;

    private JdbcXaExecutorFactory(XADataSource dataSource, ServerMeta serverMeta, FactoryInfo info) {
        this.dataSource = dataSource;
        this.serverMeta = serverMeta;
        this.info = info;
    }

    @Override
    public ServerMeta serverMeta() {
        return null;
    }

    @Override
    public MetaExecutor createMetaExecutor() {
        return null;
    }

    @Override
    public StmtExecutor createStmtExecutor() {
        return null;
    }


}
