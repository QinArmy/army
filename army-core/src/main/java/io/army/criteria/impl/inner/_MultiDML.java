package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;

import java.util.List;

public interface _MultiDML extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<? extends TableWrapper> tableWrapperList();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();
}
