package io.army.criteria.impl.inner;


import io.army.criteria.Selection;
import io.army.dialect._SqlContext;

import javax.annotation.Nullable;

import io.army.meta.TableMeta;

import java.util.List;

/**
 * <p>
 * This interface representing group consist of multi select list clause items.
 * </p>
 *
 * @since 1.0
 */
public interface _SelectionGroup extends _SelectItem {

    String tableAlias();


    /**
     * <p>
     * Note,any element of the list couldn't be rendered. so couldn't invoke below method:
     *     <ul>
     *         <li>{@link io.army.criteria.impl.inner._SelfDescribed#appendSql(StringBuilder, _SqlContext)}</li>
     *         <li>{@link _Selection#appendSelectItem(StringBuilder, _SqlContext)}</li>
     *     </ul>
     * </p>
     *
     * @return element list of this group.
     */
    List<? extends Selection> selectionList();


    interface _TableFieldGroup extends _SelectionGroup {


        boolean isLegalGroup(@Nullable TableMeta<?> table);

    }


}
