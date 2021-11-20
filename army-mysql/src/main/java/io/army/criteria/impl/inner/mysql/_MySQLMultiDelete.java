package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._MultiDML;
import io.army.criteria.impl.inner._SpecialDelete;

import java.util.List;

public interface _MySQLMultiDelete extends _MultiDML, _SpecialDelete {

    List<SimpleTableWrapper> targetTableList();
}
