package io.army.criteria.impl.inner;

import io.army.criteria.Query;
import io.army.criteria.ValuesQuery;

import java.util.List;


/**
 * <p>
 * This interface is inner interface of {@link ValuesQuery}.
 * </p>
 *
 * @since 1.0
 */
public interface _ValuesQuery extends ValuesQuery, _PartRowSet {

    List<_Selection> selectionList();

    List<List<_Expression>> rowList();


}
