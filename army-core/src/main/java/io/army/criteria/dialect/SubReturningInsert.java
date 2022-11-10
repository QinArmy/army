package io.army.criteria.dialect;


import io.army.criteria.DialectStatement;
import io.army.criteria.Insert;
import io.army.criteria.Statement;
import io.army.criteria.SubStatement;

/**
 * @see Insert
 * @see ReturningInsert
 * @see SubInsert
 * @since 1.0
 */
public interface SubReturningInsert extends DialectStatement, Statement.DqlInsert, SubStatement {

    @Deprecated
    interface _SubReturningInsertSpec extends _DqlInsertClause<SubReturningInsert> {

    }


}
