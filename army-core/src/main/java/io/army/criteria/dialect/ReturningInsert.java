package io.army.criteria.dialect;


import io.army.criteria.DialectStatement;
import io.army.criteria.InsertStatement;
import io.army.criteria.SimpleDqlStatement;

/**
 * <p>
 * This interface representing simple(non-batch) INSERT statement that return result set. For example,Postgre INSERT
 * statement with RETURNING clause.
 * * @see io.army.criteria.Insert
 *
 * @see <a href="https://www.postgresql.org/docs/current/sql-insert.html">Postgre INSERT syntax</a>
 * @since 1.0
 */
public interface ReturningInsert extends SimpleDqlStatement, DialectStatement, InsertStatement {

    @Deprecated
    interface _ReturningInsertSpec extends _DqlInsertClause<ReturningInsert> {

    }


}
