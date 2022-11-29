package io.army.criteria;


import io.army.lang.Nullable;

import java.util.List;

/**
 * <p>
 * This interface representing row set.This interface is base interface of below:
 *     <ul>
 *          <li>{@link DerivedTable}</li>
 *          <li>{@link io.army.meta.TableMeta}</li>
 *          <li>{@link NestedItems}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface TabularItem extends Item {


    interface _DerivedTableSpec {

        @Nullable
        Selection selection(String derivedAlias);

        List<Selection> selectionList();

        /**
         * @return empty : representing no alias list.
         */
        List<String> columnAliasList();

    }

}
