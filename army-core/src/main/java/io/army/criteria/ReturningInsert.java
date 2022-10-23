package io.army.criteria;


/**
 * @see Insert
 * @see SubInsert
 * @see SubReturningInsert
 * @since 1.0
 */
public interface ReturningInsert extends DqlStatement, DialectStatement, DqlInsert {

    @Deprecated
    interface _ReturningInsertSpec extends _DqlInsertClause<ReturningInsert> {

    }


}
