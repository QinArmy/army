package io.army.criteria;


/**
 * @see Insert
 * @see SubInsert
 * @see SubReturningInsert
 * @since 1.0
 */
public interface ReturningInsert extends DqlStatement, DialectStatement, DqlStatement.DqlInsert {

    interface _ReturningInsertSpec extends DqlStatement._DqlInsertSpec<ReturningInsert> {

    }


}
