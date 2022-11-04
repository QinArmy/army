package io.army.criteria.impl.inner;

import io.army.criteria.SelectItem;

import java.util.List;

public interface _RowSet extends _Statement {

    int selectionSize();

    List<? extends SelectItem> selectItemList();


}
