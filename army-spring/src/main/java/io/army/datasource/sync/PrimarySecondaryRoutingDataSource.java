package io.army.datasource.sync;

import io.army.datasource.DataSourceRole;
import io.army.tx.sync.TransactionDefinitionHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Routing DataSource for read-write splitting.
 *
 * @see TransactionDefinitionHolder
 * @see io.army.tx.sync.TransactionDefinitionInterceptor
 * @see io.army.datasource.sync.PrimarySecondaryRoutingXADataSource
 */
public class PrimarySecondaryRoutingDataSource extends AbstractRoutingCommonDataSource<DataSource>
        implements DataSource {

    public PrimarySecondaryRoutingDataSource(Map<DataSourceRole, DataSource> dataSourceMap) {
        super(dataSourceMap);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }
}
