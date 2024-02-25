/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.jdbc;

import io.army.ArmyException;
import io.army.datasource.ReadWriteSplittingDataSource;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.DriverException;
import io.army.session.Option;
import io.army.session.executor.ExecutorFactorySupport;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.SyncExecutorFactory;
import io.army.sync.executor.SyncLocalStmtExecutor;
import io.army.sync.executor.SyncRmStmtExecutor;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * @see JdbcExecutorFactoryProvider
 * @since 0.6.0
 */
final class JdbcExecutorFactory extends ExecutorFactorySupport implements SyncExecutorFactory {

    static JdbcExecutorFactory create(JdbcExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        return new JdbcExecutorFactory(provider, executorEnv);
    }

    static final byte SET_OBJECT_METHOD = 1;
    static final byte EXECUTE_LARGE_UPDATE_METHOD = 2;
    static final byte EXECUTE_LARGE_BATCH_METHOD = 4;

    static final byte MULTI_STMT = 1 << 3;


    final ExecutorEnv executorEnv;

    final MappingEnv mappingEnv;

    final ServerMeta serverMeta;

    final Database serverDatabase;

    final ArmyEnvironment env;

    final boolean useLargeUpdate;

    final boolean useSetObjectMethod;

    final boolean useExecuteLargeBatch;

    final boolean useMultiStmt;

    /**
     * @see ArmyKey#TRUNCATED_TIME_TYPE
     */
    final boolean truncatedTimeType;

    final boolean sessionIdentifierEnable;

    final boolean postgreFetchSizeAutoCommit;

    private final String dataSourceCloseMethod;

    private final String sessionFactoryName;

    private final CommonDataSource dataSource;

    private final LocalExecutorFunction localFunc;

    private final RmExecutorFunction rmFunc;


    private boolean closed;

    /**
     * private constructor
     */
    private JdbcExecutorFactory(JdbcExecutorFactoryProvider provider, ExecutorEnv executorEnv) {
        super(executorEnv.environment());
        this.executorEnv = executorEnv;
        this.mappingEnv = executorEnv.mappingEnv();
        this.serverMeta = this.mappingEnv.serverMeta();
        this.serverDatabase = this.serverMeta.serverDatabase();

        final ArmyEnvironment env = executorEnv.environment();
        this.env = env;

        final int methodFlag = provider.methodFlag;
        if (this.env.getOrDefault(SyncKey.JDBC_FORBID_V18)) {
            this.useLargeUpdate = false;
            this.useSetObjectMethod = false;
            this.useExecuteLargeBatch = false;
        } else {
            this.useLargeUpdate = (methodFlag & EXECUTE_LARGE_UPDATE_METHOD) != 0;
            this.useSetObjectMethod = (methodFlag & SET_OBJECT_METHOD) != 0;
            this.useExecuteLargeBatch = (methodFlag & EXECUTE_LARGE_BATCH_METHOD) != 0;
        }

        switch (this.serverDatabase) {
            case MySQL:
                this.useMultiStmt = (methodFlag & MULTI_STMT) != 0;
                break;
            case PostgreSQL:
                this.useMultiStmt = true;
                break;
            case H2:
            default:
                throw _Exceptions.unexpectedEnum(this.serverDatabase);
        }

        this.dataSourceCloseMethod = env.get(ArmyKey.DATASOURCE_CLOSE_METHOD);
        this.truncatedTimeType = env.getOrDefault(ArmyKey.TRUNCATED_TIME_TYPE);
        this.sessionIdentifierEnable = env.getOrDefault(SyncKey.SESSION_IDENTIFIER_ENABLE);
        if (this.serverDatabase == Database.PostgreSQL) {
            this.postgreFetchSizeAutoCommit = this.armyEnv.getOrDefault(SyncKey.POSTGRE_FETCH_SIZE_AUTO_COMMIT);
        } else {
            this.postgreFetchSizeAutoCommit = false;
        }

        this.sessionFactoryName = provider.sessionFactoryName;
        this.dataSource = provider.dataSource;

        final Object[] funcArray;
        funcArray = createExecutorFunc(this.serverDatabase);

        this.localFunc = (LocalExecutorFunction) funcArray[0];
        this.rmFunc = (RmExecutorFunction) funcArray[1];


    }


    @Override
    public String driverSpiName() {
        return "JDBC";
    }


    @Override
    public boolean isResultItemDriverSpi() {
        // JDBC isn't ResultItem spi
        return false;
    }

    @Override
    public String executorVendor() {
        return "io.army.jdbc";
    }

    @Override
    public MetaExecutor metaExecutor(Function<Option<?>, ?> optionFunc) throws DataAccessException {
        assertFactoryOpen();

        try {
            CommonDataSource dataSource = this.dataSource;
            if (dataSource instanceof ReadWriteSplittingDataSource) {
                dataSource = (CommonDataSource) ((ReadWriteSplittingDataSource<?>) dataSource).readWriteDataSource(optionFunc);
            }
            final JdbcMetaExecutor executor;
            if (dataSource instanceof DataSource) {
                executor = JdbcMetaExecutor.from(((DataSource) dataSource).getConnection());
            } else {
                executor = JdbcMetaExecutor.fromXa(((XADataSource) dataSource).getXAConnection());
            }
            return executor;
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    @Override
    public SyncLocalStmtExecutor localExecutor(final @Nullable String sessionName, final boolean readOnly,
                                               final Function<Option<?>, ?> optionFunc)
            throws DataAccessException {
        assertFactoryOpen();

        if (sessionName == null) {
            throw new NullPointerException();
        }


        Connection conn = null;
        try {
            CommonDataSource dataSource = this.dataSource;
            if (readOnly && dataSource instanceof ReadWriteSplittingDataSource) {
                dataSource = (CommonDataSource) ((ReadWriteSplittingDataSource<?>) dataSource).readOnlyDataSource(optionFunc);
            }
            if (!(dataSource instanceof DataSource)) {
                String m = String.format("%s isn't %s ,couldn't create local executor.", dataSource,
                        DataSource.class.getName());
                throw new DataAccessException(m);
            }
            conn = ((DataSource) dataSource).getConnection();
            return this.localFunc.apply(this, conn, sessionName);
        } catch (Exception e) {
            if (conn != null) {
                JdbcExecutor.closeResource(conn);
            }
            throw handleException(e);
        } catch (Throwable e) {
            if (conn != null) {
                JdbcExecutor.closeResource(conn);
            }
            throw e;
        }
    }

    @Override
    public SyncRmStmtExecutor rmExecutor(final @Nullable String sessionName, final boolean readOnly,
                                         final Function<Option<?>, ?> optionFunc)
            throws DataAccessException {
        assertFactoryOpen();

        if (sessionName == null) {
            throw new NullPointerException();
        }

        Object conn = null;
        try {
            CommonDataSource dataSource = this.dataSource;
            if (readOnly && dataSource instanceof ReadWriteSplittingDataSource) {
                dataSource = (CommonDataSource) ((ReadWriteSplittingDataSource<?>) dataSource).readOnlyDataSource(optionFunc);
            }
            if (dataSource instanceof DataSource) {
                conn = ((DataSource) dataSource).getConnection();
            } else {
                conn = ((XADataSource) dataSource).getXAConnection();
            }
            return this.rmFunc.apply(this, conn, sessionName);
        } catch (Exception e) {
            JdbcExecutorSupport.closeJdbcConnection(conn);
            throw handleException(e);
        } catch (Throwable e) {
            JdbcExecutorSupport.closeJdbcConnection(conn);
            throw e;
        }

    }

    @Nullable
    @Override
    public <T> T valueOf(Option<T> option) {
        return null;
    }


    @Override
    public Set<Option<?>> optionSet() {
        return Collections.emptySet();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }


    @Override
    public void close() throws DataAccessException {
        if (this.closed) {
            return;
        }
        final String dataSourceCloseMethod = this.dataSourceCloseMethod;
        if (dataSourceCloseMethod == null) {
            this.closed = true;
            return;
        }
        synchronized (this) {
            final CommonDataSource dataSource = this.dataSource;

            final Method method;
            try {
                method = dataSource.getClass().getMethod(dataSourceCloseMethod);
                method.invoke(dataSource);
            } catch (Exception e) {
                throw new DataAccessException(e);
            }
            this.closed = true;
        }
    }


    @Override
    public String toString() {
        return _StringUtils.builder(60)
                .append(getClass().getName())
                .append("[sessionFactoryName:")
                .append(this.sessionFactoryName)
                .append(",serverDatabase:")
                .append(this.serverDatabase.name())
                .append(",driver:JDBC,hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }


    ArmyException handleException(final Exception cause) {
        final ArmyException error;
        if (cause instanceof ArmyException) {
            error = (ArmyException) cause;
        } else if (cause instanceof SQLException) {
            // TODO convert to  ServerException
            error = new DriverException(cause, ((SQLException) cause).getSQLState(), ((SQLException) cause).getErrorCode());
        } else {
            error = new DataAccessException(cause);
        }
        return error;
    }


    private void assertFactoryOpen() {
        if (this.closed) {
            String m;
            m = String.format("%s have closed.", this);
            throw new DataAccessException(m);
        }
    }


    private static Object[] createExecutorFunc(final Database serverDatabase) {
        final LocalExecutorFunction localFunc;
        final RmExecutorFunction rmFunc;
        switch (serverDatabase) {
            case MySQL:
                localFunc = MySQLExecutor::localExecutor;
                rmFunc = MySQLExecutor::rmExecutor;
                break;
            case PostgreSQL:
                localFunc = PostgreExecutor::localExecutor;
                rmFunc = PostgreExecutor::rmExecutor;
                break;
            case H2:
            case SQLite:
            case Oracle:
            default:
                throw _Exceptions.unexpectedEnum(serverDatabase);
        }
        return new Object[]{localFunc, rmFunc};
    }


    @FunctionalInterface
    private interface LocalExecutorFunction {
        SyncLocalStmtExecutor apply(JdbcExecutorFactory factory, Connection conn, String sessionName);

    }

    @FunctionalInterface
    private interface RmExecutorFunction {
        SyncRmStmtExecutor apply(JdbcExecutorFactory factory, Object conn, String sessionName);
    }


}
