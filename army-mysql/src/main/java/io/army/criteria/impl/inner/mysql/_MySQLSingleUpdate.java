package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortItem;
import io.army.criteria.impl.inner._SingleUpdate;

import java.util.List;

public interface _MySQLSingleUpdate extends _MySQLUpdate, _SingleUpdate {


    List<String> partitionList();


    List<? extends _IndexHint> indexHintList();


    /**
     * @return a unmodifiable list
     */
    List<SortItem> orderByList();

    long rowCount();

}
