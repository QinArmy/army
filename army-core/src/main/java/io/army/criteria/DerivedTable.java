package io.army.criteria;

import io.army.lang.Nullable;

import java.util.List;

/**
 * <p>
 * This interface representing derived table,this interface is base interface of below:
 *     <ul>
 *         <li>{@link SubQuery}</li>
 *         <li>{@link Values}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DerivedTable extends TabularItem {

    List<? extends SelectItem> selectItemList();

    @Nullable
    Selection selection(String derivedFieldName);

}
