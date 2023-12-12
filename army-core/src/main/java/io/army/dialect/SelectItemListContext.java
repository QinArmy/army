package io.army.dialect;

import io.army.criteria.Selection;
import io.army.criteria.impl.inner._SelectItem;

import java.util.List;

/**
 * <p>
 * This interface is base interface(class) of below:
 *     <ul>
 *         <li>{@link _SimpleQueryContext}</li>
 *         <li>{@link ParensSelectContext}</li>
 *     </ul>
 * * <p>
 *     Package interface
 * * @since 1.0
 */
interface SelectItemListContext extends _SqlContext {

    List<? extends _SelectItem> selectItemList();

}
