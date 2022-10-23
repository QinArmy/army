package io.army.criteria;


/**
 * @see Insert
 * @see ReturningInsert
 * @see SubInsert
 * @since 1.0
 */
public interface SubReturningInsert extends DialectStatement, DqlInsert, SubStatement {

    @Deprecated
    interface _SubReturningInsertSpec extends _DqlInsertClause<SubReturningInsert> {

    }


}
