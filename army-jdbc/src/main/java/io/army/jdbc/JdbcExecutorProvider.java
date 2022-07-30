package io.army.jdbc;

import io.army.dialect.Database;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.UnsupportedDataSourceTypeException;
import io.army.sync.executor.ExecutorEnvironment;
import io.army.sync.executor.ExecutorProvider;
import io.army.sync.executor.LocalExecutorFactory;
import io.army.sync.executor.RmExecutorFactory;
import io.army.util._ClassUtils;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Objects;

@SuppressWarnings("unused")
public final class JdbcExecutorProvider implements ExecutorProvider {

    public static JdbcExecutorProvider create(Object dataSource) {
        if (!(dataSource instanceof DataSource || dataSource instanceof XADataSource)) {
            throw unsupportedDataSource(dataSource);
        }
        return new JdbcExecutorProvider((CommonDataSource) dataSource);
    }


    private final CommonDataSource dataSource;

    private int methodFlag = 0;

    private ServerMeta meta;

    private JdbcExecutorProvider(CommonDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public ServerMeta createServerMeta() throws DataAccessException {
        final CommonDataSource dataSource = this.dataSource;

        XAConnection xaConnection = null;
        final ServerMeta meta;
        try {
            if (dataSource instanceof DataSource) {
                meta = this.innerCreateServerMeta(((DataSource) dataSource).getConnection());
            } else if (dataSource instanceof XADataSource) {
                xaConnection = ((XADataSource) dataSource).getXAConnection();
                meta = this.innerCreateServerMeta(xaConnection.getConnection());
            } else {
                //no bug,never here
                throw unsupportedDataSource(dataSource);
            }
            return meta;
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
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
                    throw JdbcExceptions.wrap(e);
                }
            }
        }
    }

    @Override
    public LocalExecutorFactory createLocalFactory(final ExecutorEnvironment env) {
        final CommonDataSource dataSource = this.dataSource;
        if (!(dataSource instanceof DataSource)) {
            String m = String.format("unsupported creating %s", LocalExecutorFactory.class.getName());
            throw new UnsupportedOperationException(m);
        }
        this.validateServerMeta(env.mappingEnv());
        return JdbcLocalExecutorFactory.create((DataSource) dataSource, env, (byte) this.methodFlag);
    }

    @Override
    public RmExecutorFactory createRmFactory(final ExecutorEnvironment env) {
        final CommonDataSource dataSource = this.dataSource;
        if (!(dataSource instanceof XADataSource)) {
            String m = String.format("unsupported creating %s", RmExecutorFactory.class.getName());
            throw new UnsupportedOperationException(m);
        }

        this.validateServerMeta(env.mappingEnv());
        return JdbcRmExecutorFactory.create((XADataSource) dataSource, env, (byte) this.methodFlag);
    }


    private ServerMeta innerCreateServerMeta(final Connection connection) {
        try (Connection conn = connection) {
            final ServerMeta serverMeta;
            serverMeta = doCreateServerMeta(conn);
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
            throw JdbcExceptions.wrap(e);
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

    private static ServerMeta doCreateServerMeta(final Connection conn) throws SQLException {
        final DatabaseMetaData metaData;
        metaData = conn.getMetaData();

        final String name, version;
        name = metaData.getDatabaseProductName();
        version = metaData.getDatabaseProductVersion();

        final Database database;
        database = getDatabase(name);

        final int major, minor;
        major = metaData.getDatabaseMajorVersion();
        minor = metaData.getDatabaseMinorVersion();

        return ServerMeta.create(name, database, version, major, minor);
    }


    private static Database getDatabase(final String productName) {
        Objects.requireNonNull(productName, "productName is null");
        final Database database;
        if (productName.equals("MySQL")) {
            database = Database.MySQL;
        } else if (productName.equals("PostgreSQL")) {
            database = Database.PostgreSQL;
        } else {
            throw new DataAccessException(String.format("Database[%s] currently unsupported.", productName));
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


    private static UnsupportedDataSourceTypeException unsupportedDataSource(Object dataSource) {
        final String m;
        m = String.format("%s support only %s or %s,but dataSource is %s."
                , JdbcExecutorProvider.class.getName()
                , DataSource.class.getName()
                , XADataSource.class.getName()
                , _ClassUtils.safeClassName(dataSource));
        throw new UnsupportedDataSourceTypeException(m);
    }


}
