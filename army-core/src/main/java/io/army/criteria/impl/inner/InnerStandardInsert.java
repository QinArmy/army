package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

@DeveloperForbid
public interface InnerStandardInsert extends InnerInsert {

    boolean defaultValueIfNull();

    Map<FieldMeta<?, ?>, Expression<?>> expFieldValueMap();

    List<FieldMeta<?, ?>> fieldMetaList();

    ValueWrapper valueWrapper();
}
