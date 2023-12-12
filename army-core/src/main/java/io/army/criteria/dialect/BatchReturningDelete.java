package io.army.criteria.dialect;

import io.army.criteria.DeleteStatement;
import io.army.criteria.DialectStatement;


/**
 * <p>
 * This interface representing batch primary DELETE statement that return result set. For example,Postgre DELETE
 * statement with RETURNING clause.
*
 * @see BatchReturningDelete
 * @since 1.0
 */
public interface BatchReturningDelete extends BatchDqlStatement,
        DeleteStatement,
        DialectStatement {


}
