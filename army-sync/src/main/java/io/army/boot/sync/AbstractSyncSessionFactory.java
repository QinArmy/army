package io.army.boot.sync;

import io.army.AbstractGenericSessionFactory;
import io.army.interceptor.DomainAdvice;
import io.army.meta.TableMeta;
import io.army.sync.GenericSyncSessionFactory;

import java.util.List;
import java.util.Map;

abstract class AbstractSyncSessionFactory extends AbstractGenericSessionFactory
        implements GenericSyncSessionFactory {


    final Map<TableMeta<?>, List<DomainAdvice>> domainInterceptorMap;


    AbstractSyncSessionFactory(AbstractSyncSessionFactoryBuilder factoryBuilder) {
        super(factoryBuilder);
        this.domainInterceptorMap = SyncSessionFactoryUtils.createDomainInterceptorMap(
                factoryBuilder.domainInterceptors());

    }


    @Override
    public Map<TableMeta<?>, List<DomainAdvice>> domainInterceptorMap() {
        return this.domainInterceptorMap;
    }

    @Override
    public List<DomainAdvice> domainInterceptorList(TableMeta<?> tableMeta) {
        return this.domainInterceptorMap.get(tableMeta);
    }


}
