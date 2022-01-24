package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SortItem;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.meta.SingleTableMeta;

import java.util.List;

public interface _MySQLSingleDelete extends _MySQLDelete, _SingleDelete {

    @Override
    SingleTableMeta<?> table();

    /**
     * @return a unmodifiable list
     */
    List<SortItem> orderByList();

    long rowCount();


}
