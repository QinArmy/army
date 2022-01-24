package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * <p>
 * This interface representing group consist of multi select list clause items.
 * </p>
 *
 * @see SQLs#group(TableMeta, String)
 * @see SQLs#derivedGroup(String)
 * @since 1.0
 */
public interface SelectionGroup extends SelectItem {

    List<? extends Selection> selectionList();


}
