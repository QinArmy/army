package io.army.sync;

import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Map;

public interface GenericSyncApiSessionFactory extends GenericSyncSessionFactory {

    Map<TableMeta<?>, DomainAdvice> domainInterceptorMap();

    @Nullable
    DomainAdvice domainInterceptorList(TableMeta<?> tableMeta);

    boolean hasCurrentSession();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);
}
