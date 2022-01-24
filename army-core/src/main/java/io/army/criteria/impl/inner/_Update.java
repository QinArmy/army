package io.army.criteria.impl.inner;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValueItem;

import java.util.List;

public interface _Update extends _Statement {


    /**
     * @return a unmodifiable list
     */
    List<? extends SetTargetPart> fieldList();

    /**
     * @return a unmodifiable list
     */
    List<? extends SetValueItem> valueExpList();

    List<_Predicate> predicateList();


}
