package io.army.jdbc;

import io.army.codec.JsonCodec;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.SyncKey;
import io.army.mapping.MappingEnvironment;
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
import java.time.ZoneOffset;

final class JdbcExecutorFactory implements ExecutorFactory {

    static JdbcExecutorFactory create(DataSource dataSource, ServerMeta serverMeta, ExecutorEnvironment env
            , int methodFlag) {
        return new JdbcExecutorFactory(dataSource, serverMeta, env, methodFlag);
    }

    static final byte SET_OBJECT_METHOD = 1;

    static final byte EXECUTE_LARGE_UPDATE_METHOD = 2;

    static final byte EXECUTE_LARGE_BATCH_METHOD = 4;


    private final DataSource dataSource;

    final ServerMeta serverMeta;

    final Database database;

    final ExecutorEnvironment executorEnv;

    final ArmyEnvironment env;

    final MappingEnvironment mapEnv;

    final boolean useLargeUpdate;

    final boolean useSetObjectMethod;

    final boolean useExecuteLargeBatch;


    boolean closed;

    private JdbcExecutorFactory(DataSource dataSource, ServerMeta serverMeta, ExecutorEnvironment executorEnv
            , final int methodFlag) {
        this.dataSource = dataSource;
        this.serverMeta = serverMeta;
        this.database = serverMeta.database();
        this.executorEnv = executorEnv;

        this.env = executorEnv.environment();
        this.mapEnv = new JdbcMappingEnvironment(serverMeta, executorEnv);

        if (this.env.getOrDefault(SyncKey.JDBC_FORBID_V18)) {
            this.useLargeUpdate = false;
            this.useSetObjectMethod = false;
            this.useExecuteLargeBatch = false;
        } else {
            this.useLargeUpdate = (methodFlag & EXECUTE_LARGE_UPDATE_METHOD) != 0;
            this.useSetObjectMethod = (methodFlag & SET_OBJECT_METHOD) != 0;
            this.useExecuteLargeBatch = (methodFlag & EXECUTE_LARGE_BATCH_METHOD) != 0;
        }

    }

    @Override
    public ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Override
    public MappingEnvironment mappingEnvironment() {
        return this.mapEnv;
    }

    @Override
    public boolean supportSavePoints() {
        return true;
    }

    @Override
    public MetaExecutor createMetaExecutor() throws DataAccessException {
        if (this.closed) {
            throw closedError();
        }
        try {
            return JdbcMetaExecutor.create(this.dataSource.getConnection());
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public StmtExecutor createStmtExecutor() throws DataAccessException {
        if (this.closed) {
            throw closedError();
        }
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

    @Override
    public void close() throws DataAccessException {
        this.closed = true;
    }


    private static DataAccessException closedError() {
        String m = String.format("%s have closed.", JdbcExecutorFactory.class.getName());
        return new DataAccessException(m);
    }


    private static final class JdbcMappingEnvironment implements MappingEnvironment {

        private final ServerMeta serverMeta;

        private JdbcMappingEnvironment(ServerMeta serverMeta, ExecutorEnvironment env) {
            this.serverMeta = serverMeta;
        }

        @Override
        public boolean isReactive() {
            return false;
        }

        @Override
        public ServerMeta serverMeta() {
            return this.serverMeta;
        }

        @Override
        public ZoneOffset zoneOffset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonCodec jsonCodec() {
            throw new UnsupportedOperationException();
        }
    }//JdbcMappingEnvironment


}
