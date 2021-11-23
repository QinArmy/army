package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Expression;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface _ValueInsertContext extends _DmlContext {

    @Nullable
    _ValueInsertContext parentContext();

    Collection<FieldMeta<?, ?>> fieldMetas();

    Map<FieldMeta<?, ?>, Expression<?>> commonExpMap();

    List<ObjectWrapper> domainList();


}
