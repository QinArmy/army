package io.army.jdbc;

import io.army.Database;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.UnsupportedDataSourceTypeException;
import io.army.sync.executor.ExecutorEnvironment;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;
import io.army.sync.utils._SyncExceptions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;

public final class JdbcExecutorProvider implements ExecutorProvider {

    public static JdbcExecutorProvider getInstance() {
        return new JdbcExecutorProvider();
    }

    private JdbcExecutorProvider() {
    }


    @Override
    public ExecutorFactory createFactory(final Object dataSource, final ExecutorEnvironment env) {

        try {
            final ExecutorFactory factory;

            if (dataSource instanceof DataSource) {
                final DataSource ds = (DataSource) dataSource;
                final ServerMeta serverMeta;
                serverMeta = getServerMeta(getPrimaryDataSource(ds));
                factory = JdbcExecutorFactory.create(ds, serverMeta, env);
            } else {
                String m = String.format("dataSource isn't %s instance.", DataSource.class.getName());
                throw new UnsupportedDataSourceTypeException(m);
            }
            return factory;
        } catch (UnsupportedDataSourceTypeException e) {
            throw e;
        } catch (SQLException e) {
            throw _SyncExceptions.wrapDataAccess(e);
        } catch (Exception e) {
            String m = String.format("get server metadata occur error:%s", e.getMessage());
            throw new DataAccessException(m, e);
        }
    }


    private static ServerMeta getServerMeta(final DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
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
