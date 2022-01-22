package io.army.jdbc;

import io.army.Database;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.session.UnsupportedDataSourceTypeException;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;
import io.army.sync.executor.FactoryInfo;
import io.army.sync.utils.SyncExceptions;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Objects;

public final class JdbcExecutorProvider implements ExecutorProvider {

    private static final JdbcExecutorProvider INSTANCE = new JdbcExecutorProvider();

    public static JdbcExecutorProvider getInstance() {
        return INSTANCE;
    }

    private JdbcExecutorProvider() {
    }


    @Override
    public ExecutorFactory createTxFactory(final Object dataSource, final FactoryInfo info) {

        try {
            final ExecutorFactory factory;
            final ServerMeta serverMeta;
            if (dataSource instanceof DataSource) {
                final DataSource ds = (DataSource) dataSource;
                try (Connection conn = ds.getConnection()) {
                    serverMeta = getServerMeta(conn);
                }
                factory = JdbcTxExecutorFactory.create(ds, serverMeta, info);
            } else {
                String m = String.format("dataSource isn't %s or %s instance."
                        , DataSource.class.getName(), XADataSource.class.getName());
                throw new UnsupportedDataSourceTypeException(m);
            }
            return factory;
        } catch (UnsupportedDataSourceTypeException e) {
            throw e;
        } catch (SQLException e) {
            throw SyncExceptions.wrapDataAccess(e);
        } catch (Exception e) {
            String m = String.format("get server metadata occur error:%s", e.getMessage());
            throw new DataAccessException(m, e);
        }
    }

    @Override
    public ExecutorFactory createXaFactory(Object dataSource, FactoryInfo info) {
        try {
            final ExecutorFactory factory;
            final ServerMeta serverMeta;
            if (dataSource instanceof XADataSource) {
                final XADataSource ds = (XADataSource) dataSource;
                serverMeta = getXaServerMeta(ds);
                factory = JdbcXaExecutorFactory.create(ds, serverMeta, info);
            } else {
                String m = String.format("dataSource isn't %s or %s instance."
                        , DataSource.class.getName(), XADataSource.class.getName());
                throw new UnsupportedDataSourceTypeException(m);
            }
            return factory;
        } catch (UnsupportedDataSourceTypeException e) {
            throw e;
        } catch (SQLException e) {
            throw SyncExceptions.wrapDataAccess(e);
        } catch (Exception e) {
            String m = String.format("get server metadata occur error:%s", e.getMessage());
            throw new DataAccessException(m, e);
        }
    }

    private static ServerMeta getServerMeta(final Connection connection) throws SQLException {
        try (Connection conn = connection) {
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

            return new JdbcServerMeta(name, database
                    , version, major
                    , minor, metaData.supportsSavepoints());
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


    private static ServerMeta getXaServerMeta(final XADataSource dataSource) throws SQLException {
        XAConnection xaConn = null;
        try {
            xaConn = dataSource.getXAConnection();
            try (Connection con = xaConn.getConnection()) {
                return getServerMeta(con);
            }
        } finally {
            if (xaConn != null) {
                xaConn.close();
            }
        }

    }


    private static final class JdbcServerMeta implements ServerMeta {

        private final String name;

        private final Database database;

        private final String version;

        private final int major;

        private final int minor;

        private final boolean supportSavePoint;

        private JdbcServerMeta(String name, Database database
                , String version, int major
                , int minor, boolean supportSavePoint) {
            this.name = name;
            this.database = database;
            this.version = version;
            this.major = major;

            this.minor = minor;
            this.supportSavePoint = supportSavePoint;
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
        public boolean supportSavePoint() {
            return this.supportSavePoint;
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
                    .append("\nsupportSavePoint=")
                    .append(this.supportSavePoint)
                    .toString();
        }


    }


}
