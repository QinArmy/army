package io.army.criteria;


import io.army.lang.Nullable;

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
public interface TabularItem {


    interface DerivedTableSpec {

        @Nullable
        Selection selection(String derivedAlias);

    }

}
