package io.army.criteria.impl.inner;

import io.army.criteria.Selection;
import io.army.dialect._SqlContext;

import javax.annotation.Nullable;

import java.util.List;

/**
 * <p>
 * Package interface,this interface is base interface of below:
 *     <ul>
 *         <li>{@link _AliasDerivedBlock}</li>
 *         <li>{@link _DerivedTable}</li>
 *         <li>{@link  _Cte}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _SelectionMap {

    /**
     * <p>
     * Note,the {@link Selection} couldn't be rendered. so couldn't invoke below method:
     *     <ul>
     *         <li>{@link io.army.criteria.impl.inner._SelfDescribed#appendSql(StringBuilder, _SqlContext)}</li>
     *         <li>{@link _Selection#appendSelectItem(StringBuilder, _SqlContext)}</li>
     *     </ul>
     * </p>
     *
     * @return the {@link Selection} that couldn't be rendered.
     */
    @Nullable
    Selection refSelection(String name);

    /**
     * <p>
     * Note,any {@link Selection} of list couldn't be rendered. so couldn't invoke below method:
     *     <ul>
     *         <li>{@link io.army.criteria.impl.inner._SelfDescribed#appendSql(StringBuilder, _SqlContext)}</li>
     *         <li>{@link _Selection#appendSelectItem(StringBuilder, _SqlContext)}</li>
     *     </ul>
     * </p>
     *
     * @return a unmodified list, the list of {@link Selection} that couldn't be rendered.
     */
    List<? extends Selection> refAllSelection();


}
