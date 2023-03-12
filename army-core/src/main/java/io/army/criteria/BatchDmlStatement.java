package io.army.criteria;

/**
 * <p>
 * This interface representing batch primary dml statement that don't return result set.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link BatchUpdate}</li>
 *         <li>{@link BatchDelete}</li>
 *     </ul>
 * </p>
 *
 * @see BatchUpdate
 * @see BatchDelete
 * @since 1.0
 */
public interface BatchDmlStatement extends DmlStatement {

}
