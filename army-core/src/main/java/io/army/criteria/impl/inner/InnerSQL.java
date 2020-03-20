package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

@DeveloperForbid
public interface InnerSQL {

    /**
     * @return a unmodifiable list
     */
    List<TableWrapper> tableWrapperList();

    /**
     * @return a unmodifiable list
     */
    List<IPredicate> predicateList();

    /**
     * @return a unmodifiable map
     */
    Map<TableMeta<?>, Integer> tablePresentCountMap();

    void clear();
}
