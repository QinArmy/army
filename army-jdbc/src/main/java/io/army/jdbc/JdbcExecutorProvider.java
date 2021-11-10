package io.army.jdbc;

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
import java.sql.SQLException;

public final class JdbcExecutorProvider implements ExecutorProvider {

    private static final JdbcExecutorProvider INSTANCE = new JdbcExecutorProvider();

    public static JdbcExecutorProvider getInstance() {
        return INSTANCE;
    }

    private JdbcExecutorProvider() {
    }


    @Override
    public ExecutorFactory createFactory(final Object dataSource, final FactoryInfo info) {

        try {
            final ExecutorFactory factory;
            final ServerMeta serverMeta;
            if (dataSource instanceof DataSource) {
                final DataSource ds = (DataSource) dataSource;
                serverMeta = getTxServerMeta(ds);
                factory = JdbcTxExecutorFactory.create(ds, serverMeta, info);
            } else if (dataSource instanceof XADataSource) {
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

            return null;
        }

    }

    private static ServerMeta getTxServerMeta(final DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return getServerMeta(conn);
        }
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


}
