package io.army.advice.sync;

import io.army.beans.ReadonlyWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sync.ProxySession;

import java.util.List;

public interface DomainAdvice {


    void beforeInsert(TableMeta<?> tableMeta, ReadonlyWrapper wrapper, ProxySession session);

    void afterInsert(TableMeta<?> tableMeta, ReadonlyWrapper wrapper, ProxySession session);

    void beforeUpdate(TableMeta<?> tableMeta, List<FieldMeta<?, ?>> targetFieldList, ProxySession session);

    void afterUpdate(TableMeta<?> tableMeta, List<FieldMeta<?, ?>> targetFieldList, ProxySession session);

    void beforeDelete(TableMeta<?> tableMeta, ProxySession session);

    void afterDelete(TableMeta<?> tableMeta, ProxySession session);


}
