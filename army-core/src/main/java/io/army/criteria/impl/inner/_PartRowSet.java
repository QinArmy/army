package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;
import io.army.criteria.SelectItem;
import io.army.criteria.SortItem;
import io.army.lang.Nullable;

import java.util.List;

public interface _PartRowSet extends _Statement, RowSet, _Statement._RowCountSpec {


    int selectionSize();

    List<? extends SelectItem> selectItemList();

    List<? extends SortItem> orderByList();

    @Nullable
    _Expression offset();


}
