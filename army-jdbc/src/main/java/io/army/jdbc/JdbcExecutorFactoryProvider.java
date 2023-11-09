package io.army.jdbc;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sync.executor.SyncLocalExecutorFactory;
import io.army.sync.executor.SyncStmtExecutorFactory;
import io.army.sync.executor.SyncStmtExecutorFactoryProvider;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class JdbcExecutorFactoryProvider implements SyncStmtExecutorFactoryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcExecutorFactoryProvider.class);

    public static JdbcExecutorFactoryProvider create(Object dataSource) {
        if (!(dataSource instanceof DataSource || dataSource instanceof XADataSource)) {
            throw unsupportedDataSource(dataSource);
        }
        return new JdbcExecutorFactoryProvider((CommonDataSource) dataSource);
    }


    final CommonDataSource dataSource;

    final String sessionFactoryName;

    int methodFlag = 0;

    ServerMeta meta;

    private JdbcExecutorFactoryProvider(CommonDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public ServerMeta createServerMeta(final Dialect usedDialect, @Nullable Function<String, Database> func) throws DataAccessException {
        final CommonDataSource dataSource = this.dataSource;

        XAConnection xaConnection = null;
        final ServerMeta meta;
        try {
            if (dataSource instanceof DataSource) {
                meta = this.innerCreateServerMeta(((DataSource) dataSource).getConnection(), usedDialect);
            } else if (dataSource instanceof XADataSource) {
                xaConnection = ((XADataSource) dataSource).getXAConnection();
                meta = this.innerCreateServerMeta(xaConnection.getConnection(), usedDialect);
            } else {
                //no bug,never here
                throw unsupportedDataSource(dataSource);
            }
            this.meta = meta;
            return meta;
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            String m = String.format("get %s failure.", Connection.class.getName());
            throw new DataAccessException(m, e);
        } finally {
            if (xaConnection != null) {
                try {
                    xaConnection.close();
                } catch (SQLException e) {
                    throw JdbcExecutor.wrapError(e);
                }
            }
        }
    }

    @Override
    public SyncStmtExecutorFactory createFactory(final ExecutorEnv env) {
        final CommonDataSource dataSource = this.dataSource;
        if (!(dataSource instanceof DataSource)) {
            String m = String.format("unsupported creating %s", SyncLocalExecutorFactory.class.getName());
            throw new UnsupportedOperationException(m);
        }
        this.validateServerMeta(env.mappingEnv());
        return JdbcLocalExecutorFactory.create((DataSource) dataSource, env, (byte) this.methodFlag);
    }


    private ServerMeta innerCreateServerMeta(final Connection connection, final Dialect usedDialect) {
        try (Connection conn = connection) {
            final ServerMeta serverMeta;
            serverMeta = doCreateServerMeta(conn, usedDialect);
            LOG.debug("create {}", serverMeta);
            int methodFlag = 0;
            try (PreparedStatement statement = conn.prepareStatement("SELECT 1 + ? AS armyJdbcTest")) {
                final Class<?> clazz = statement.getClass();

                if (definiteSetObjectMethod(clazz)) {
                    methodFlag |= JdbcExecutorFactory.SET_OBJECT_METHOD;
                }
                if (definiteExecuteLargeUpdateMethod(clazz)) {
                    methodFlag |= JdbcExecutorFactory.EXECUTE_LARGE_UPDATE_METHOD;
                }
                if (definiteExecuteLargeBatchMethod(clazz)) {
                    methodFlag |= JdbcExecutorFactory.EXECUTE_LARGE_BATCH_METHOD;
                }
            }
            this.methodFlag = methodFlag;
            return serverMeta;
        } catch (SQLException e) {
            throw JdbcExecutor.wrapError(e);
        } catch (Exception e) {
            String m = String.format("get server metadata occur error:%s", e.getMessage());
            throw new DataAccessException(m, e);
        }
    }


    private void validateServerMeta(final MappingEnv mappingEnv) {
        final ServerMeta serverMeta = this.meta;
        if (serverMeta == null) {
            throw new IllegalStateException(String.format("Don't create %s", ServerMeta.class.getName()));
        }
        if (mappingEnv.serverMeta() != serverMeta) {
            throw new IllegalArgumentException(String.format("%s not match.", ServerMeta.class.getName()));
        }
    }

    private static ServerMeta doCreateServerMeta(final Connection conn, final Dialect usedDialect)
            throws SQLException {
        final DatabaseMetaData metaData;
        metaData = conn.getMetaData();

        final String name, version;
        name = metaData.getDatabaseProductName();

        final Database database;
        database = getDatabase(name);
        return ServerMeta.builder()
                .name(name)
                .database(database)
                .catalog(conn.getCatalog())
                .schema(conn.getSchema())
                .version(metaData.getDatabaseProductVersion())
                .major(metaData.getDatabaseMajorVersion())
                .minor(metaData.getDatabaseMinorVersion())
                .subMinor(0) // TODO parse version
                .usedDialect(usedDialect)
                .supportSavePoint(metaData.supportsSavepoints())
                .build();
    }


    private static Database getDatabase(final String productName) {
        Objects.requireNonNull(productName, "productName is null");
        final Database database;
        if (productName.equals("MySQL")) {
            database = Database.MySQL;
        } else if (productName.equals("PostgreSQL")) {
            database = Database.PostgreSQL;
        } else {
            throw _Exceptions.unsupportedDatabaseFamily(productName);
        }
        return database;
    }

    private static boolean definiteSetObjectMethod(final Class<?> statementClass) {
        boolean match;
        try {
            final Method method;
            method = statementClass.getMethod("setObject", int.class, Object.class, SQLType.class);
            match = !method.getDeclaringClass().isInterface();
        } catch (NoSuchMethodException e) {
            match = false;
        }
        return match;
    }

    private static boolean definiteExecuteLargeUpdateMethod(final Class<?> statementClass) {
        boolean match;
        try {
            final Method method;
            method = statementClass.getMethod("executeLargeUpdate");
            match = !method.getDeclaringClass().isInterface();
        } catch (NoSuchMethodException e) {
            match = false;
        }
        return match;
    }

    private static boolean definiteExecuteLargeBatchMethod(final Class<?> statementClass) {
        boolean match;
        try {
            final Method method;
            method = statementClass.getMethod("executeLargeBatch");
            match = !method.getDeclaringClass().isInterface();
        } catch (NoSuchMethodException e) {
            match = false;
        }
        return match;
    }


    static DataSource getPrimaryDataSource(DataSource dataSource) {
        return dataSource;
    }


    private static ArmyException unsupportedDataSource(Object dataSource) {
        final String m;
        m = String.format("%s support only %s or %s,but dataSource is %s.",
                JdbcExecutorFactoryProvider.class.getName(),
                DataSource.class.getName(),
                XADataSource.class.getName(),
                _ClassUtils.safeClassName(dataSource)
        );
        throw new ArmyException(m);
    }


}
