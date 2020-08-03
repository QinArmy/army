package io.army.sync;

import io.army.GenericProxySession;
import io.army.interceptor.DomainAdvice;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Map;

/**
 * This interface representing a sync api session factory(used by developer).
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link SessionFactory}</li>
 *     <li>{@code io.army.TmSessionFactory}</li>
 * </ul>
 */
public interface GenericSyncApiSessionFactory extends GenericSyncSessionFactory {

    GenericProxySession proxySession();

    Map<TableMeta<?>, DomainAdvice> domainInterceptorMap();

    @Nullable
    DomainAdvice domainInterceptorList(TableMeta<?> tableMeta);

    boolean hasCurrentSession();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);
}
