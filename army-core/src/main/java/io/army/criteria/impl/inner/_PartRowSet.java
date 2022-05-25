package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;
import io.army.criteria.SelectItem;
import io.army.criteria.SortItem;

import java.util.List;

public interface _PartRowSet extends _Statement, RowSet {

    List<SelectItem> selectItemList();

    List<? extends SortItem> orderByList();

    long offset();

    long rowCount();

}
