package io.army.jdbc;

import io.army.dialect.Database;
import io.army.session.DataAccessException;
import io.army.sync.executor.ExecutorEnv;
import io.army.sync.executor.LocalExecutorFactory;
import io.army.sync.executor.LocalStmtExecutor;
import io.army.util._Exceptions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiFunction;

final class JdbcLocalExecutorFactory extends JdbcExecutorFactory implements LocalExecutorFactory {

    static JdbcLocalExecutorFactory create(DataSource dataSource, ExecutorEnv env
            , int methodFlag) {
        return new JdbcLocalExecutorFactory(dataSource, env, methodFlag);
    }


    private final DataSource dataSource;

    private final BiFunction<JdbcLocalExecutorFactory, Connection, LocalStmtExecutor> executorFunction;


    private JdbcLocalExecutorFactory(final DataSource dataSource, final ExecutorEnv executorEnv
            , final int methodFlag) {
        super(executorEnv, methodFlag);
        this.dataSource = dataSource;
        this.executorFunction = localFunction(this.serverMeta.dialectDatabase());


    }


    @Override
    public LocalStmtExecutor createLocalStmtExecutor() throws DataAccessException {
        this.assertFactoryOpen();
        try {
            return this.executorFunction.apply(this, this.dataSource.getConnection());
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }


    @Override
    Connection getConnection() throws DataAccessException {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    private static BiFunction<JdbcLocalExecutorFactory, Connection, LocalStmtExecutor> localFunction(final Database database) {
        final BiFunction<JdbcLocalExecutorFactory, Connection, LocalStmtExecutor> func;
        switch (database) {
            case MySQL:
                func = MySQLExecutor::localExecutor;
                break;
            case Postgre:
            case Oracle:
            case H2:
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return func;
    }


}
