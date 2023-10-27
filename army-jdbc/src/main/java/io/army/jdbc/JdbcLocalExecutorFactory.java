package io.army.jdbc;

import io.army.dialect.Database;
import io.army.executor.ExecutorEnv;
import io.army.session.DataAccessException;
import io.army.sync.executor.SyncLocalExecutorFactory;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.util._Exceptions;

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

    private static BiFunction<JdbcLocalExecutorFactory, Connection, SyncLocalStmtExecutor> localFunction(
            final Database database) {
        final BiFunction<JdbcLocalExecutorFactory, Connection, SyncLocalStmtExecutor> func;
        switch (database) {
            case MySQL:
                func = MySQLExecutor::localExecutor;
                break;
            case PostgreSQL:
                func = PostgreExecutor::localExecutor;
                break;
            case Oracle:
            case H2:
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return func;
    }


}
