package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.meta.FieldMeta;

import java.util.Map;

public interface _SetInsert extends _Insert {

    Map<FieldMeta<?, ?>, Expression<?>> expMap();

}
