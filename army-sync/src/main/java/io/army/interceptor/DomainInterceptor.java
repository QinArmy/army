package io.army.interceptor;

import io.army.ProxySession;
import io.army.beans.ReadonlyWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;

public interface DomainInterceptor {

    int order();

    Collection<TableMeta<?>> tableMetaSet();

    void beforeInsert(TableMeta<?> tableMeta, ReadonlyWrapper wrapper, ProxySession session);

    void afterInsert(TableMeta<?> tableMeta, ReadonlyWrapper wrapper, ProxySession session);

    void beforeUpdate(TableMeta<?> tableMeta, List<FieldMeta<?, ?>> targetFieldList);

    void afterUpdate(TableMeta<?> tableMeta, List<FieldMeta<?, ?>> targetFieldList);


}
