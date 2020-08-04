package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;

import java.util.List;

@DeveloperForbid
public interface InnerMultiDML extends InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<? extends TableWrapper> tableWrapperList();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();
}
