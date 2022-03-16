package io.army.advice.sync;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sync.CurrentSession;

import java.util.List;

public interface DomainAdvice {


    void beforeInsert(TableMeta<?> tableMeta, ReadWrapper wrapper, CurrentSession session);

    void afterInsert(TableMeta<?> tableMeta, ReadWrapper wrapper, CurrentSession session);

    void beforeUpdate(TableMeta<?> tableMeta, List<FieldMeta<?>> targetFieldList, CurrentSession session);

    void afterUpdate(TableMeta<?> tableMeta, List<FieldMeta<?>> targetFieldList, CurrentSession session);

    void beforeDelete(TableMeta<?> tableMeta, CurrentSession session);

    void afterDelete(TableMeta<?> tableMeta, CurrentSession session);


}
