package io.army.criteria.impl.inner;

import java.util.List;

public interface _MultiDml extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<? extends _TableBlock> tableBlockList();

    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();
}
