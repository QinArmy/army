package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._SingleUpdate;

import java.util.List;

public interface _MySQLSingleUpdate extends _MySQLUpdate, _SingleUpdate {

    List<String> partitionList();


    List<? extends _IndexHint> indexHintList();


    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    long rowCount();

}
