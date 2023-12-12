package io.army.criteria.dialect;


import io.army.criteria.DqlStatement;


/**
 * <p>
 * This interface representing batch primary DQL statement that return result set.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link BatchReturningUpdate}</li>
 *         <li>{@link BatchReturningDelete}</li>
 *     </ul>
*
 * @since 1.0
 */
public interface BatchDqlStatement extends DqlStatement {


}
