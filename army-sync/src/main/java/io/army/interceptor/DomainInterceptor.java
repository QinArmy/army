package io.army.interceptor;

import io.army.ProxySession;
import io.army.beans.ReadonlyWrapper;
import io.army.meta.TableMeta;

import java.util.Collection;

public interface DomainInterceptor {

    int order();

    Collection<TableMeta<?>> tableMetaSet();

    void afterGenerator(TableMeta<?> tableMeta, ReadonlyWrapper wrapper, ProxySession session);

    void afterPersist(TableMeta<?> tableMeta, ReadonlyWrapper wrapper, ProxySession session);


}
