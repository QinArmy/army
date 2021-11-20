package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;

import java.util.List;

public interface _Update extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<? extends SetTargetPart> targetFieldList();

    /**
     * @return a unmodifiable list
     */
    List<? extends SetValuePart> valueExpList();

    List<IPredicate> predicateList();


}
