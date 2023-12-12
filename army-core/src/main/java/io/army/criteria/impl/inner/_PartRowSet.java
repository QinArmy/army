package io.army.criteria.impl.inner;

import io.army.criteria.SortItem;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ParensRowSet}</li>
 *         <li>{@link _Query}</li>
 *     </ul>
 * * @see _UnionRowSet
 */
public interface _PartRowSet extends _Statement, _RowSet, _Statement._LimitClauseSpec {

    List<? extends SortItem> orderByList();


}
