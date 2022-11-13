package io.army.criteria.dialect;


import io.army.criteria.DialectStatement;
import io.army.criteria.DqlStatement;
import io.army.criteria.Insert;
import io.army.criteria.Statement;

/**
 * @see Insert
 * @see SubInsert
 * @see SubReturningInsert
 * @since 1.0
 */
public interface ReturningInsert extends DqlStatement, DialectStatement, Statement.DqlInsert {

    @Deprecated
    interface _ReturningInsertSpec extends _DqlInsertClause<ReturningInsert> {

    }


}