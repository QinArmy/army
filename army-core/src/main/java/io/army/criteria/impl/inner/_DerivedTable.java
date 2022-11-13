package io.army.criteria.impl.inner;

import io.army.criteria.DerivedTable;
import io.army.criteria.Selection;
import io.army.lang.Nullable;

import java.util.List;

public interface _DerivedTable extends DerivedTable {

    /**
     * @return empty : representing no alias list.
     */
    List<String> columnAliasList();

    @Nullable
    Selection selection(String derivedAlias);


}
