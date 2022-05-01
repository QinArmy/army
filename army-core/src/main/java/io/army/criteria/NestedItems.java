package io.army.criteria;

/**
 * <p>
 * This interface representing dialect nested join expressioin.
 * </p>
 *
 * @since 1.0
 */
public interface NestedItems extends TableItem {

    interface TableItemGroupSpec {

        NestedItems asTableGroup();

    }


}
