package io.army.criteria;


/**
 * <p>
 * This interface representing the element of ROW constructor. This interface is base interface of :
 *     <ul>
 *         <li>{@link SubQuery}</li>
 *         <li>{@link SQLExpression}</li>
 *         <li>{@code  io.army.criteria.impl.SelectionGroups.RowElementGroup}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface RowElement extends Item {

    interface DelayElement {

        boolean isDelay();
    }

}
