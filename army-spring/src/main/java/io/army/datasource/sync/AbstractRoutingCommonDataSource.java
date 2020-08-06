package io.army.datasource.sync;

import io.army.datasource.DataSourceRole;
import io.army.tx.sync.TransactionDefinitionHolder;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.CommonDataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Abstract base class for Army's {@link CommonDataSource}
 * implementations for read-write splitting, taking care of the padding.
 *
 * <p>'Padding' in the context of this class means default implementations
 * for certain methods from the {@code DataSource} or {@code XADataSource} interface, such as
 * {@link #getLoginTimeout()}, {@link #setLoginTimeout(int)}, and so forth.
 *
 * @see TransactionDefinitionHolder
 * @see io.army.tx.sync.TransactionDefinitionInterceptor
 * @see io.army.datasource.sync.PrimarySecondaryRoutingDataSource
 * @see io.army.datasource.sync.PrimarySecondaryRoutingXADataSource
 * @since 1.0
 */
public abstract class AbstractRoutingCommonDataSource<D extends CommonDataSource> implements CommonDataSource {

    protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());

    protected final Map<DataSourceRole, D> dataSourceMap;

    private boolean lenientFallback = true;

    /**
     * transaction timeout boundary seconds
     */
    private int timeoutBoundary = 10;

    protected AbstractRoutingCommonDataSource(Map<DataSourceRole, D> dataSourceMap) {
        Map<DataSourceRole, D> newMap = new EnumMap<>(dataSourceMap);

        D dataSource;
        dataSource = newMap.get(DataSourceRole.PRIMARY);
        Assert.notNull(dataSource, "no PRIMARY XADataSource.");

        dataSource = newMap.get(DataSourceRole.SECONDARY);
        Assert.notNull(dataSource, "no SECONDARY XADataSource.");

        this.dataSourceMap = Collections.unmodifiableMap(newMap);
    }

    /**
     * Returns 0, indicating the default system timeout is to be used.
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    /**
     * Setting a login timeout is not supported.
     */
    @Override
    public void setLoginTimeout(int timeout) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    /**
     * LogWriter methods are not supported.
     */
    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("getLogWriter");
    }

    /**
     * LogWriter methods are not supported.
     */
    @Override
    public void setLogWriter(PrintWriter pw) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }


    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }


    public int getTimeoutBoundary() {
        return timeoutBoundary;
    }

    public void setTimeoutBoundary(int timeoutBoundary) {
        this.timeoutBoundary = timeoutBoundary;
    }

    /**
     * Specify whether to apply a lenient fallback to the default DataSource
     * if no specific DataSource could be found for the current lookup key.
     * <p>Default is "true", accepting lookup keys without a corresponding entry
     * in the target DataSource map - simply falling back to the default DataSource
     * in that case.
     * <p>Switch this flag to "false" if you would prefer the fallback to only apply
     * if the lookup key was {@code null}. Lookup keys without a DataSource
     * entry will then lead to an IllegalStateException.
     *
     * @see #determineCurrentLookupKey()
     */
    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    protected D determineTargetDataSource() {
        DataSourceRole lookupKey = determineCurrentLookupKey();
        if (lookupKey == null) {
            if (this.lenientFallback) {
                lookupKey = DataSourceRole.SECONDARY;
            } else {
                throw new IllegalStateException("Cannot determine target DataSource for null lookup key");
            }
        }
        D dataSource = this.dataSourceMap.get(lookupKey);
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return dataSource;
    }

    @Nullable
    protected DataSourceRole determineCurrentLookupKey() {
        DataSourceRole lookupKey;
        if (TransactionDefinitionHolder.isReadOnly()) {
            if (TransactionDefinitionHolder.getTimeout() >= this.timeoutBoundary) {
                lookupKey = DataSourceRole.TIMEOUT_SECONDARY;
            } else {
                lookupKey = DataSourceRole.SECONDARY;
            }
        } else {
            lookupKey = DataSourceRole.PRIMARY;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("routing datasource:{},thread:{},method:{}",
                    lookupKey, Thread.currentThread().getName(), TransactionDefinitionHolder.getName());
        }
        return lookupKey;
    }

    /**
     * see {@code io.army.boot.sync.SyncSessionFactoryUtils#obtainPrimaryDataSource(CommonDataSource) }
     */
    @SuppressWarnings({"unused"})
    public D getPrimaryDataSource() {
        return this.dataSourceMap.get(DataSourceRole.PRIMARY);
    }


}
