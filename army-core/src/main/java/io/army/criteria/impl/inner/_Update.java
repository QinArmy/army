package io.army.criteria.impl.inner;

import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;

import java.util.List;

public interface _Update extends _Statement {


    /**
     * @return a unmodifiable list
     */
    List<? extends SetLeftItem> leftItemList();

    /**
     * @return a unmodifiable list
     */
    List<? extends SetRightItem> rightItemList();

    List<_Predicate> predicateList();


}
