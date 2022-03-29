package io.army.jdbc;

import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.Database;
import io.army.session.UnsupportedDataSourceTypeException;
import io.army.sync.executor.ExecutorEnvironment;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Objects;

@SuppressWarnings("unused")
public final class JdbcExecutorProvider implements ExecutorProvider {

    public static JdbcExecutorProvider getInstance() {
        return new JdbcExecutorProvider();
    }

    private JdbcExecutorProvider() {
    }


    @Override
    public ExecutorFactory createFactory(final Object dataSource, final ExecutorEnvironment env) {
        if (!(dataSource instanceof DataSource)) {
            String m = String.format("dataSource isn't %s instance.", DataSource.class.getName());
            throw new UnsupportedDataSourceTypeException(m);
        }
        final DataSource ds = (DataSource) dataSource;
        try (Connection conn = ds.getConnection()) {
            final ServerMeta serverMeta;
            serverMeta = getServerMeta(conn);
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
            final ExecutorFactory factory;
            factory = JdbcExecutorFactory.create(ds, serverMeta, env, methodFlag);
            return factory;
        } catch (UnsupportedDataSourceTypeException e) {
            throw e;
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        } catch (Exception e) {
            String m = String.format("get server metadata occur error:%s", e.getMessage());
            throw new DataAccessException(m, e);
        }

    }


    private static ServerMeta getServerMeta(final Connection conn) throws SQLException {
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

        return new JdbcServerMeta(name, database, version, major, minor);
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


    private static final class JdbcServerMeta implements ServerMeta {

        private final String name;

        private final Database database;

        private final String version;

        private final int major;

        private final int minor;

        private JdbcServerMeta(String name, Database database
                , String version, int major, int minor) {
            this.name = name;
            this.database = database;
            this.version = version;
            this.major = major;

            this.minor = minor;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public Database database() {
            return this.database;
        }

        @Override
        public String version() {
            return this.version;
        }

        @Override
        public int major() {
            return this.major;
        }

        @Override
        public int minor() {
            return this.minor;
        }

        @Override
        public boolean meetsMinimum(final int major, final int minor) {
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.version);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ServerMeta) {
                final ServerMeta v = (ServerMeta) obj;
                match = this.name.equals(v.name()) && this.version.equals(v.version());
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("name=")
                    .append(this.name)
                    .append("\ndatabase=")
                    .append(this.database)
                    .append("\nversion=")
                    .append(this.version)
                    .append("\nmajor=")
                    .append(this.major)
                    .append("\nminor=")
                    .append(this.minor)
                    .toString();
        }


    }


}
