package io.army.wrapper;

import io.army.criteria.Selection;

import java.util.List;

public interface ReturningUpdateSQLWrapper extends SimpleUpdateSQLWrapper, SQLWrapper {

    List<Selection> selectionList();
}
