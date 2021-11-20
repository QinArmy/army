package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;

import java.util.List;

public interface _Delete extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();

}
