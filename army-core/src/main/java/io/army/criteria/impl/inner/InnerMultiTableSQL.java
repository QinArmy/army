package io.army.criteria.impl.inner;

import java.util.List;

@DeveloperForbid
public interface InnerMultiTableSQL extends InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<? extends TableWrapper> tableWrapperList();
}
