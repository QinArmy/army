package io.army.criteria.impl.inner;

import io.army.criteria.standard.StandardUpdate;

import java.util.List;

public interface _DomainUpdate extends _SingleUpdate, StandardUpdate {

    /**
     * @return a unmodifiable list,probably empty
     */
    List<_ItemPair> childItemPairList();

}
