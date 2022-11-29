package io.army.criteria.impl.inner;

import io.army.criteria.DerivedTable;
import io.army.criteria.TabularItem;

import java.util.List;

public interface _DerivedTable extends DerivedTable, TabularItem._DerivedTableSpec {

    /**
     * @return empty : representing no alias list.
     */
    List<String> columnAliasList();


}
