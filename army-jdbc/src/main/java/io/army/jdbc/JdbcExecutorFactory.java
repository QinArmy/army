package io.army.jdbc;

import io.army.Database;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sync.executor.ExecutorEnvironment;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.StmtExecutor;
import io.army.util._Exceptions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

final class JdbcExecutorFactory implements ExecutorFactory {

    static JdbcExecutorFactory create(DataSource dataSource, ServerMeta serverMeta, ExecutorEnvironment info) {
        return new JdbcExecutorFactory(dataSource, serverMeta, info);
    }

    private final DataSource dataSource;

    final ServerMeta serverMeta;

    final Database database;

    final ExecutorEnvironment env;


    private JdbcExecutorFactory(DataSource dataSource, ServerMeta serverMeta, ExecutorEnvironment env) {
        this.dataSource = dataSource;
        this.serverMeta = serverMeta;
        this.database = serverMeta.database();
        this.env = env;
    }

    @Override
    public ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Override
    public MetaExecutor createMetaExecutor() throws DataAccessException {
        try {
            return JdbcMetaExecutor.create(this.dataSource.getConnection());
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public StmtExecutor createStmtExecutor() throws DataAccessException {
        final Connection connection;
        try {
            connection = this.dataSource.getConnection();
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
        final StmtExecutor executor;
        switch (this.database) {
            case MySQL:
                executor = MySQLExecutor.create(this, connection);
                break;
            case PostgreSQL:
            case H2:
            case Oracle:
            case Firebird:
            default:
                throw _Exceptions.unexpectedEnum(this.database);
        }
        return executor;
    }


}
