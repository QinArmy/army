package io.army.criteria.impl.inner;

import java.util.List;

public interface _Update extends _Statement {


    /**
     * @return a unmodifiable list,non-empty
     */
    List<_ItemPair> itemPairList();

    /**
     * @return a unmodifiable list,probably empty
     */
    List<_ItemPair> childItemPairList();

    List<_Predicate> predicateList();


}
