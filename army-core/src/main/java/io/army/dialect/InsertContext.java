package io.army.dialect;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.impl.inner.InnerInsert;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

public interface InsertContext extends SQLContext {

    StringBuilder fieldStringBuilder();

    @Nullable
    Expression<?> commonExp(FieldMeta<?, ?> fieldMeta);

    boolean defaultIfNull();

    InnerInsert innerInsert();

}
