package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortItem;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.TableMeta;

import java.util.List;

public interface _MySQLSingleUpdate extends _MySQLUpdate, _SingleUpdate {

    @Override
    TableMeta<?> table();

    List<String> partitionList();


    List<? extends _IndexHint> indexHintList();


    /**
     * @return a unmodifiable list
     */
    List<? extends SortItem> orderByList();

    long rowCount();

}
