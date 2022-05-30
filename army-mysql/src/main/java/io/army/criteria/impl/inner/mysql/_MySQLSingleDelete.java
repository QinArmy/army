package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SortItem;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.mysql.MySQLDelete;
import io.army.meta.SingleTableMeta;

import java.util.List;

public interface _MySQLSingleDelete extends _MySQLDelete, _SingleDelete, MySQLDelete {

    @Override
    SingleTableMeta<?> table();

    List<String> partitionList();

    /**
     * @return a unmodifiable list
     */
    List<? extends SortItem> orderByList();

    long rowCount();


}
