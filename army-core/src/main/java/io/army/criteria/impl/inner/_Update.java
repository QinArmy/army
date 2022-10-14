package io.army.criteria.impl.inner;

import java.util.List;

public interface _Update extends _Statement {


    /**
     * @return a unmodifiable list,non-empty
     */
    List<_ItemPair> itemPairList();


    List<_Predicate> predicateList();


}
