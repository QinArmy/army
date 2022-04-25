package io.army.criteria.impl.inner;

import io.army.criteria.SelectItem;
import io.army.criteria.SortItem;

import java.util.List;

public interface _PartRowSet extends _Statement {

    List<? extends SelectItem> selectItemList();

    List<? extends SortItem> orderByList();

    long offset();

    long rowCount();

}
