package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;

import java.util.List;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ParensRowSet}</li>
 *         <li>{@link _Query}</li>
 *         <li>{@link _UnionRowSet}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _RowSet extends _Statement, RowSet {

    int selectionSize();

    List<? extends _SelectItem> selectionList();


}
