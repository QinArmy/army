package io.army.criteria.impl.inner;

import io.army.criteria.DerivedTable;
import io.army.criteria.Selection;
import io.army.lang.Nullable;


public interface _DerivedTable extends DerivedTable, _RowSet {


    @Nullable
    Selection selection(String name);

}
