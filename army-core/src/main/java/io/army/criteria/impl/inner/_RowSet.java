package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;

import java.util.List;

/**
 * <p>
 * This interface is inner interface of {@link RowSet}.This interface is base interface of below:
 *     <ul>
 *         <li>{@link _ParensRowSet}</li>
 *         <li>{@link _PartRowSet}</li>
 *         <li>{@link _UnionRowSet}</li>
 *         <li>{@link _PrimaryRowSet}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _RowSet extends _Statement, RowSet {

    int selectionSize();


    interface _SelectItemListSpec {

        List<? extends _SelectItem> selectItemList();
    }


}
