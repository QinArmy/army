package io.army.criteria.impl.inner;

import io.army.criteria.ValuesQuery;

import java.util.List;


/**
 * <p>
 * This interface is inner interface of {@link ValuesQuery}.
 *
 * @since 0.6.0
 */
public interface _ValuesQuery extends ValuesQuery, _PartRowSet, _RowSet._SelectItemListSpec {

    @Override
    List<_Selection> selectItemList();

    List<List<_Expression>> rowList();


}
