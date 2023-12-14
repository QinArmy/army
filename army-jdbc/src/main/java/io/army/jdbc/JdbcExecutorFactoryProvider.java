package io.army.jdbc;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sync.executor.SyncExecutorFactory;
import io.army.sync.executor.SyncStmtExecutorFactoryProvider;
import io.army.util.ClassUtils;
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

    public static JdbcExecutorFactoryProvider create(Object dataSource, String factoryName, ArmyEnvironment env) {
        if (!(dataSource instanceof DataSource || dataSource instanceof XADataSource)) {
            throw unsupportedDataSource(dataSource);
        }
        return new JdbcExecutorFactoryProvider((CommonDataSource) dataSource, factoryName);
    }


    final CommonDataSource dataSource;

    final String sessionFactoryName;

    int methodFlag = 0;

    ServerMeta meta;

    private JdbcExecutorFactoryProvider(CommonDataSource dataSource, String factoryName) {
        this.dataSource = dataSource;
        this.sessionFactoryName = factoryName;
    }


    @Override
    public ServerMeta createServerMeta(final Dialect usedDialect, @Nullable Function<String, Database> func)
            throws DataAccessException {
        final CommonDataSource dataSource = this.dataSource;

        XAConnection xaConnection = null;
        final ServerMeta meta;
        try {
            if (dataSource instanceof DataSource) {
                meta = createServerMetaAndDriverFlags(((DataSource) dataSource).getConnection(), usedDialect, func);
            } else if (dataSource instanceof XADataSource) {
                xaConnection = ((XADataSource) dataSource).getXAConnection();
                meta = createServerMetaAndDriverFlags(xaConnection.getConnection(), usedDialect, func);
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
                JdbcExecutorSupport.closeXaConnection(xaConnection);
            }
        }
    }

    @Override
    public SyncExecutorFactory createFactory(final ExecutorEnv env) throws DataAccessException {
        validateServerMeta(env.mappingEnv());
        return JdbcExecutorFactory.create(this, env);
    }


    private ServerMeta createServerMetaAndDriverFlags(final Connection connection, final Dialect usedDialect,
                                                      final @Nullable Function<String, Database> func) {
        try (Connection conn = connection) {
            final ServerMeta serverMeta;
            serverMeta = builderServerMeta(conn, usedDialect, func);

            LOG.debug("create {}", serverMeta);

            this.methodFlag = createDriverImplFlags(conn, serverMeta.serverDatabase());
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

    private static ServerMeta builderServerMeta(final Connection conn, final Dialect usedDialect,
                                                final @Nullable Function<String, Database> func)
            throws SQLException {
        final DatabaseMetaData metaData;
        metaData = conn.getMetaData();

        final String family, version;
        family = metaData.getDatabaseProductName();

        return ServerMeta.builder()

                .name(family)
                .database(Database.mapToDatabase(family, func))
                .catalog(conn.getCatalog())
                .schema(conn.getSchema())

                .version(metaData.getDatabaseProductVersion())
                .major(metaData.getDatabaseMajorVersion())
                .minor(metaData.getDatabaseMinorVersion())
                .subMinor(0) // TODO parse version

                .usedDialect(usedDialect)
                .supportSavePoint(metaData.supportsSavepoints())
                .driverSpi("java.sql")

                .build();
    }


    private static int createDriverImplFlags(final Connection conn, final Database serverDatabase) throws SQLException {

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

        switch (serverDatabase) {
            case MySQL: {
                try (Statement statement = conn.createStatement()) {
                    statement.execute("SELECT 1 AS one ; SELECT 2 AS two");

                    statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);

                    methodFlag |= JdbcExecutorFactory.MULTI_STMT;
                } catch (SQLException e) {
                    LOG.debug("{} driver don't support multi-statement.", serverDatabase.name());
                }
            }
            break;
            case PostgreSQL:
            case SQLite:
            case H2:
            case Oracle:
            default:// no-op
        }

        return methodFlag;


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
                ClassUtils.safeClassName(dataSource)
        );
        throw new ArmyException(m);
    }


}
