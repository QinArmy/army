package io.army.jdbc;

import io.army.meta.ServerMeta;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.FactoryInfo;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.StmtExecutor;
import io.army.sync.utils._SyncExceptions;

import javax.sql.DataSource;
import java.sql.SQLException;

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
    public MetaExecutor createMetaExecutor() {
        try {
            return JdbcMetaExecutor.create(this.dataSource.getConnection());
        } catch (SQLException e) {
            throw _SyncExceptions.wrapDataAccess(e);
        }
    }

    @Override
    public StmtExecutor createStmtExecutor() {
        return null;
    }


}
