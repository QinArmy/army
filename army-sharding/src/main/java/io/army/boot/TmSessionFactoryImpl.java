package io.army.boot;

import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.TmSessionFactory;
import io.army.boot.sync.AbstractSyncSessionFactory;
import io.army.boot.sync.ProxySessionImpl;
import io.army.boot.sync.SyncSessionFactoryParams;
import io.army.boot.sync.SyncSessionFactoryUtils;
import io.army.context.spi.CurrentSessionContext;
import io.army.dialect.SQLDialect;
import io.army.sync.ProxySession;
import io.army.util.Assert;

import java.util.Map;

/**
 * this class is a implementation of {@link TmSessionFactory}.
 * Transaction Manager (TM) {@link TmSessionFactory}.
 * <p>
 * this class run only belonw {@link ShardingMode}:
 *     <ul>
 *         <li>{@link ShardingMode#SHARDING}</li>
 *     </ul>
 * </p>
 */
class TmSessionFactoryImpl extends AbstractSyncSessionFactory implements TmSessionFactory {

    private final Map<String, RmSessionFactoryImpl> sessionFactoryMap;

    private final CurrentSessionContext currentSessionContext;

    private final ProxySession proxySession;

    private boolean closed;

    TmSessionFactoryImpl(SyncSessionFactoryParams.Sharding factoryParams) {
        super(factoryParams);
        Assert.state(this.shardingMode == ShardingMode.SHARDING
                , () -> String.format("%s support only SHARDING ShardingMode", TmSessionFactoryImpl.class.getName()));

        this.sessionFactoryMap = SyncShardingSessionFactoryUtils.createSessionFactoryMap(this, factoryParams);
        this.currentSessionContext = SyncSessionFactoryUtils.buildCurrentSessionContext(this);
        this.proxySession = new ProxySessionImpl(this, this.currentSessionContext);
    }


    @Override
    public SessionBuilder builder() {
        return null;
    }

    @Override
    public boolean closed() {
        return this.closed;
    }

    @Override
    public boolean supportZone() {
        return false;
    }

    @Override
    public Map<String, SQLDialect> actualSQLDialectMap() {
        return null;
    }


    @Override
    public void close() throws SessionFactoryException {
        this.closed = true;
    }


}
