package io.army.jdbc;

import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.SyncStmtExecutorFactory;

import javax.sql.CommonDataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

abstract class JdbcExecutorFactory implements SyncStmtExecutorFactory {

    static final byte SET_OBJECT_METHOD = 1;
    static final byte EXECUTE_LARGE_UPDATE_METHOD = 2;
    static final byte EXECUTE_LARGE_BATCH_METHOD = 4;
    final ExecutorEnv executorEnv;

    final MappingEnv mappingEnv;

    final ServerMeta serverMeta;

    final Database serverDataBase;

    final ArmyEnvironment env;

    final boolean useLargeUpdate;

    final boolean useSetObjectMethod;

    final boolean useExecuteLargeBatch;

    final boolean databaseSessionHolder;

    /**
     * @see ArmyKey#TRUNCATED_TIME_TYPE
     */
    final boolean truncatedTimeType;

    final boolean sessionIdentifierEnable;

    private final String dataSourceCloseMethod;

    private boolean closed;

    JdbcExecutorFactory(final ExecutorEnv executorEnv, final int methodFlag) {
        this.executorEnv = executorEnv;
        this.mappingEnv = executorEnv.mappingEnv();
        this.serverMeta = this.mappingEnv.serverMeta();
        this.serverDataBase = this.serverMeta.serverDatabase();

        final ArmyEnvironment env = executorEnv.environment();
        this.env = env;

        if (this.env.getOrDefault(SyncKey.JDBC_FORBID_V18)) {
            this.useLargeUpdate = false;
            this.useSetObjectMethod = false;
            this.useExecuteLargeBatch = false;
        } else {
            this.useLargeUpdate = (methodFlag & EXECUTE_LARGE_UPDATE_METHOD) != 0;
            this.useSetObjectMethod = (methodFlag & SET_OBJECT_METHOD) != 0;
            this.useExecuteLargeBatch = (methodFlag & EXECUTE_LARGE_BATCH_METHOD) != 0;
        }

        this.databaseSessionHolder = env.getOrDefault(ArmyKey.DATABASE_SESSION_HOLDER_ENABLE);
        this.dataSourceCloseMethod = env.get(ArmyKey.DATASOURCE_CLOSE_METHOD);
        this.truncatedTimeType = env.getOrDefault(ArmyKey.TRUNCATED_TIME_TYPE);
        this.sessionIdentifierEnable = env.getOrDefault(SyncKey.SESSION_IDENTIFIER_ENABLE);

    }


    @Override
    public final boolean supportSavePoints() {
        //JDBC support save point api
        return true;
    }

    @Override
    public final MetaExecutor createMetaExecutor() throws DataAccessException {
        this.assertFactoryOpen();
        return JdbcMetaExecutor.create(this.getConnection());
    }

    @Override
    public final void close() throws DataAccessException {
        if (this.closed) {
            return;
        }
        synchronized (this) {
            final String dataSourceCloseMethod = this.dataSourceCloseMethod;
            if (dataSourceCloseMethod != null) {
                this.closeDataSource(dataSourceCloseMethod);
            }
            this.closed = true;
        }
    }


    @Override
    public final String toString() {
        return String.format("%s ,%s", this.getClass().getName(), this.serverMeta);
    }

    abstract Connection getConnection() throws DataAccessException;

    abstract void closeDataSource(String dataSourceCloseMethod) throws DataAccessException;


    final void assertFactoryOpen() {
        if (this.closed) {
            String m;
            m = String.format("%s have closed.", this);
            throw new DataAccessException(m);
        }
    }


    static void doCloseDataSource(final CommonDataSource dataSource, final String dataSourceCloseMethod)
            throws DataAccessException {
        final Method method;
        try {
            method = dataSource.getClass().getMethod(dataSourceCloseMethod);
            method.invoke(dataSource);
        } catch (Throwable e) {
            throw new DataAccessException(e);
        }
    }


}
