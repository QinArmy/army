package io.army.criteria.impl.inner.mysql;


import io.army.criteria.SortItem;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.meta.SingleTableMeta;

import java.util.List;

public interface _MySQLSingleUpdate extends _MySQLUpdate, _SingleUpdate, MySQLUpdate {

    @Override
    SingleTableMeta<?> table();

    List<String> partitionList();


    List<? extends _IndexHint> indexHintList();


    /**
     * @return a unmodifiable list
     */
    List<? extends SortItem> orderByList();

    long rowCount();

}
