package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.meta.FieldMeta;

import java.util.List;

@DeveloperForbid
public interface InnerUpdate {

    /**
     * @return a unmodifiable list
     */
    List<FieldMeta<?, ?>> targetFieldList();

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> valueExpList();

    void clear();

}
