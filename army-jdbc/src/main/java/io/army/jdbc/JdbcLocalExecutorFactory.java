package io.army.jdbc;

import io.army.executor.ExecutorEnv;
import io.army.session.DataAccessException;
import io.army.sync.executor.SyncLocalExecutorFactory;
import io.army.sync.executor.SyncLocalStmtExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiFunction;

final class JdbcLocalExecutorFactory extends JdbcExecutorFactory implements SyncLocalExecutorFactory {

    static JdbcLocalExecutorFactory create(DataSource dataSource, ExecutorEnv env, int methodFlag) {
        return new JdbcLocalExecutorFactory(dataSource, env, methodFlag);
    }


    private final DataSource dataSource;

    private final BiFunction<JdbcLocalExecutorFactory, Connection, SyncLocalStmtExecutor> executorFunction;


    private JdbcLocalExecutorFactory(final DataSource dataSource, final ExecutorEnv executorEnv
            , final int methodFlag) {
        super(executorEnv, methodFlag);
        this.dataSource = dataSource;
        this.executorFunction = localFunction(this.serverMeta.serverDatabase());
    }


    @Override
    public SyncLocalStmtExecutor createLocalStmtExecutor() throws DataAccessException {
        this.assertFactoryOpen();
        try {
            return this.executorFunction.apply(this, this.dataSource.getConnection());
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }


    @Override
    void closeDataSource(final String dataSourceCloseMethod) throws DataAccessException {
        doCloseDataSource(this.dataSource, dataSourceCloseMethod);
    }


    @Override
    Connection getConnection() throws DataAccessException {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }


}
