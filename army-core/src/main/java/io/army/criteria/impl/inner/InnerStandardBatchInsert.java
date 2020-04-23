package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

@DeveloperForbid
public interface InnerStandardBatchInsert extends InnerBatchInsert {

    /**
     * @return a unmodifiable list
     * @see Insert.InsertOptionAble#commonValue(FieldMeta, Expression)
     */
    Map<FieldMeta<?, ?>, Expression<?>> commonValueMap();

    boolean ignoreGenerateValueIfCrash();

    /**
     * @see Insert.InsertValuesAble
     * @see Insert.InsertIntoAble#insert(List)
     */
    List<IDomain> valueList();
}
