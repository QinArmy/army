package io.army.datasource.sync;

import io.army.datasource.DataSourceRole;
import io.army.tx.sync.TransactionDefinitionHolder;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * Routing DataSource for read-write splitting.
 *
 * @see TransactionDefinitionHolder
 * @see io.army.tx.sync.TransactionDefinitionInterceptor
 * @see io.army.datasource.sync.PrimarySecondaryRoutingDataSource
 */
public class PrimarySecondaryRoutingXADataSource extends AbstractRoutingCommonDataSource<XADataSource>
        implements XADataSource {


    public PrimarySecondaryRoutingXADataSource(Map<DataSourceRole, XADataSource> dataSourceMap) {
        super(dataSourceMap);
    }

    @Override
    public XAConnection getXAConnection() throws SQLException {
        return determineTargetDataSource().getXAConnection();
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        return determineTargetDataSource().getXAConnection(user, password);
    }


}
