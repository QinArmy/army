package io.army.criteria.dialect;

import io.army.criteria.DialectStatement;
import io.army.criteria.SimpleDqlStatement;
import io.army.criteria.Statement;
import io.army.criteria.UpdateStatement;

/**
 * <p>
 * This interface representing simple(non-batch) primary update statement with RETURNING clause that can return result set.
 *
 * @since 0.6.0
 */
public interface ReturningUpdate extends UpdateStatement, SimpleDqlStatement, DialectStatement,
        Statement.DmlStatementSpec {


}
