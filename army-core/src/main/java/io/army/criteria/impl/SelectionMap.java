package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.lang.Nullable;

import java.util.List;

/**
 * <p>
 * Package interface,this interface is base interface of below:
 *     <ul>
 *         <li>{@link ArmyDerivedBlock}</li>
 *         <li>{@link ArmyDerivedTable}</li>
 *         <li>{@link ArmyCte}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
interface SelectionMap {

    @Nullable
    Selection selection(String name);


    List<? extends Selection> selectionList();

}
