package io.army.boot;

import io.army.GenericSyncSessionFactory;
import io.army.interceptor.DomainInterceptor;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

abstract class AbstractSyncSessionFactory extends AbstractGenericSessionFactory implements GenericSyncSessionFactory {


    private final Map<TableMeta<?>, List<DomainInterceptor>> domainInterceptorMap;


    AbstractSyncSessionFactory(SyncSessionFactoryParams factoryParams) {
        super(factoryParams);
        this.domainInterceptorMap = SyncSessionFactoryUtils.createDomainInterceptorMap(
                factoryParams.getDomainInterceptors());

    }

    /**
     * create inner session factory for {@link io.army.ShardingMode#SHARDING} or {@link io.army.ShardingMode#SAME_SCHEMA_SHARDING}
     */
    AbstractSyncSessionFactory(AbstractSyncSessionFactory sessionFactory) {
        super(sessionFactory);
        this.domainInterceptorMap = sessionFactory.domainInterceptorMap;
    }


    @Override
    public Map<TableMeta<?>, List<DomainInterceptor>> domainInterceptorMap() {
        return this.domainInterceptorMap;
    }

    @Override
    public List<DomainInterceptor> domainInterceptorList(TableMeta<?> tableMeta) {
        return this.domainInterceptorMap.get(tableMeta);
    }


}
