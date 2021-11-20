package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._Delete;
import io.army.criteria.impl.inner._Statement;

import java.util.List;

public interface _MySQLSingleDelete extends _Statement, _Delete {

    /**
     * @return a unmodifiable list
     */
    List<SortPart> sortExpList();

    int rowCount();
}
