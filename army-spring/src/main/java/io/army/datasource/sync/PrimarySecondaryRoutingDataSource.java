package io.army.datasource.sync;

import io.army.datasource.DataSourceRole;
import io.army.lang.Nullable;
import io.army.tx.sync.TransactionDefinitionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Routing DataSource for read-write splitting.
 */
public class PrimarySecondaryRoutingDataSource extends AbstractRoutingDataSource {


    private static final Logger LOG = LoggerFactory.getLogger(PrimarySecondaryRoutingDataSource.class);

    private Map<Object, Object> targetDataSources;

    /**
     * transaction timeout boundary seconds
     */
    private int timeoutBoundary = 10;


    @Override
    protected Object determineCurrentLookupKey() {
        String lookupKey;
        if (TransactionDefinitionHolder.isReadOnly()) {
            if (TransactionDefinitionHolder.getTimeout() >= this.timeoutBoundary) {
                lookupKey = DataSourceRole.TIMEOUT_SECONDARY.display;
            } else {
                lookupKey = DataSourceRole.SECONDARY.display;
            }
        } else {
            lookupKey = DataSourceRole.PRIMARY.display;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("routing datasource:{},thread:{},method:{}",
                    lookupKey, Thread.currentThread().getName(), TransactionDefinitionHolder.getName());
        }
        return lookupKey;
    }

    public final int getTimeoutBoundary() {
        return timeoutBoundary;
    }

    public void setTimeoutBoundary(int timeoutBoundary) {
        this.timeoutBoundary = timeoutBoundary;
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
    }

    @Nullable
    public DataSource getPrimaryDataSource() {
        if (this.targetDataSources == null) {
            return null;
        }
        Object lookupKey = this.targetDataSources.get(DataSourceRole.PRIMARY.display);
        return lookupKey == null ? null : resolveSpecifiedDataSource(lookupKey);
    }

}
