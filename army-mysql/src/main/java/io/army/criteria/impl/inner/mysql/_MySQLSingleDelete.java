package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.meta.SingleTableMeta;

import java.util.List;

public interface _MySQLSingleDelete extends _MySQLDelete, _SingleDelete, _Statement._RowCountSpec
        , _Statement._OrderByListSpec {

    @Override
    SingleTableMeta<?> table();

    List<String> partitionList();


}
