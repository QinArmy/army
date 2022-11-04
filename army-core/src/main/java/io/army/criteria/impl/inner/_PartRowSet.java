package io.army.criteria.impl.inner;

import io.army.criteria.SortItem;

import java.util.List;

/**
 * @see io.army.dialect._ParensRowSet
 */
public interface _PartRowSet extends _Statement, _RowSet, _Statement._LimitClauseSpec {

    List<? extends SortItem> orderByList();


}
