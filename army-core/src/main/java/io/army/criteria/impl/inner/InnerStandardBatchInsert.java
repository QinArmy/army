package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.meta.FieldMeta;

import java.util.Map;

@DeveloperForbid
public interface InnerStandardBatchInsert extends InnerBatchInsert {

    /**
     * @return a unmodifiable list
     * @see Insert.InsertOptionAble#commonValue(FieldMeta, Expression)
     */
    Map<FieldMeta<?, ?>, Expression<?>> commonValueMap();

    boolean ignoreGenerateValueIfCrash();


}
