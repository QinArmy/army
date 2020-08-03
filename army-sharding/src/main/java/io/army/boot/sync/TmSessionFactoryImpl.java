package io.army.boot.sync;

import io.army.AbstractGenericSessionFactory;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.NotFoundRouteException;
import io.army.interceptor.DomainAdvice;
import io.army.meta.TableMeta;
import io.army.sharding.ShardingRoute;
import io.army.sharding.TableRoute;
import io.army.sync.GenericSyncSessionFactory;
import io.army.sync.ProxySession;
import io.army.sync.TmSessionFactory;
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
class TmSessionFactoryImpl extends AbstractGenericSessionFactory implements InnerTmSessionFactory, GenericSyncSessionFactory {

    final Map<TableMeta<?>, DomainAdvice> domainAdviceMap;

    final Map<TableMeta<?>, ShardingRoute> shardingRouteMap;

    private final Map<String, RmSessionFactoryImpl> sessionFactoryMap;

    private final CurrentSessionContext currentSessionContext;

    private final ProxySession proxySession;


    private boolean closed;

    TmSessionFactoryImpl(TmSessionFactionBuilderImpl builder) {
        super(builder);
        Assert.state(this.shardingMode == ShardingMode.SHARDING
                , () -> String.format("%s support only SHARDING ShardingMode", TmSessionFactoryImpl.class.getName()));

        this.domainAdviceMap = SyncSessionFactoryUtils.createDomainAdviceMap(builder.domainInterceptors());
        this.sessionFactoryMap = SyncShardingSessionFactoryUtils.createSessionFactoryMap(this, builder);
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
    public TableRoute tableRoute(TableMeta<?> tableMeta) throws NotFoundRouteException {
        return null;
    }

    @Override
    public void close() throws SessionFactoryException {
        this.closed = true;
    }


}
