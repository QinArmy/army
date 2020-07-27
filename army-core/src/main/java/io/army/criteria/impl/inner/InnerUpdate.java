package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.meta.FieldMeta;

import java.util.List;

@DeveloperForbid
public interface InnerUpdate extends InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<? extends SetTargetPart> targetFieldList();

    /**
     * @return a unmodifiable list
     */
    List<? extends SetValuePart> valueExpList();

    List<IPredicate> predicateList();

    void clear();

}
