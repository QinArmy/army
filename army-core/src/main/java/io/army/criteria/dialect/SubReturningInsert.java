package io.army.criteria.dialect;


import io.army.criteria.DialectStatement;
import io.army.criteria.InsertStatement;
import io.army.criteria.Statement;
import io.army.criteria.SubStatement;

/**
 * @see InsertStatement
 * @see ReturningInsert
 * @see SubInsert
 * @since 1.0
 */
@Deprecated
public interface SubReturningInsert extends DialectStatement, Statement.DqlInsert, SubStatement {

    @Deprecated
    interface _SubReturningInsertSpec extends _DqlInsertClause<SubReturningInsert> {

    }


}
