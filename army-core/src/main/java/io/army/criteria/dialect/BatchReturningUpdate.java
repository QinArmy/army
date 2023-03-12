package io.army.criteria.dialect;

import io.army.criteria.UpdateStatement;


/**
 * <p>
 * This interface representing batch primary UPDATE statement that return result set. For example,Postgre UPDATE
 * statement with RETURNING clause.
 * </p>
 *
 * @see BatchReturningDelete
 * @since 1.0
 */
public interface BatchReturningUpdate extends BatchDqlStatement, UpdateStatement {


}
