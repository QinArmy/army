package io.army.criteria;

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
public interface DerivedTable extends TabularItem, TabularItem.DerivedTableSpec {

    List<? extends SelectItem> selectItemList();


}
