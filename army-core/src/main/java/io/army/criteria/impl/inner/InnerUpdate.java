package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.meta.FieldMeta;

import java.util.List;

@DeveloperForbid
public interface InnerUpdate extends InnerSQL {


    /**
     * @return a unmodifiable list
     */
    List<FieldMeta<?, ?>> targetFieldList();

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> valueExpList();

    List<IPredicate> predicateList();

    void clear();

}
