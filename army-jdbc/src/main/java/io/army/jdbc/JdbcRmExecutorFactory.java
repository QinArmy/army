package io.army.jdbc;

import io.army.dialect.Database;
import io.army.session.DataAccessException;
import io.army.sync.executor.ExecutorEnv;
import io.army.sync.executor.RmExecutorFactory;
import io.army.sync.executor.RmStmtExecutor;
import io.army.util._Exceptions;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiFunction;

final class JdbcRmExecutorFactory extends JdbcExecutorFactory implements RmExecutorFactory {

    static JdbcRmExecutorFactory create(XADataSource dataSource, ExecutorEnv executorEnv
            , int methodFlag) {
        return new JdbcRmExecutorFactory(dataSource, executorEnv, methodFlag);
    }


    private final XADataSource dataSource;

    private final BiFunction<JdbcRmExecutorFactory, XAConnection, RmStmtExecutor> executorFunction;

    private JdbcRmExecutorFactory(XADataSource dataSource, ExecutorEnv executorEnv, int methodFlag) {
        super(executorEnv, methodFlag);
        this.dataSource = dataSource;
        this.executorFunction = rmFunction(this.serverMeta.dialectDatabase());
    }


    @Override
    public RmStmtExecutor createRmStmtExecutor() {
        try {
            return this.executorFunction.apply(this, this.dataSource.getXAConnection());
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }


    @Override
    Connection getConnection() throws DataAccessException {
        try {
            return this.dataSource.getXAConnection().getConnection();
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        }
    }

    @Override
    void closeDataSource(String dataSourceCloseMethod) throws DataAccessException {
        doCloseDataSource(this.dataSource, dataSourceCloseMethod);
    }

    private static BiFunction<JdbcRmExecutorFactory, XAConnection, RmStmtExecutor> rmFunction(final Database database) {
        final BiFunction<JdbcRmExecutorFactory, XAConnection, RmStmtExecutor> function;
        switch (database) {
            case MySQL:
                function = MySQLExecutor::rmExecutor;
                break;
            case PostgreSQL:
            case Oracle:
            case H2:
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return function;
    }


}
