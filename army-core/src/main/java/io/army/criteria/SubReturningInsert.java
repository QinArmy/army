package io.army.criteria;


/**
 * @see Insert
 * @see ReturningInsert
 * @see SubInsert
 * @since 1.0
 */
public interface SubReturningInsert extends DialectStatement, DqlInsert, SubStatement {

    interface _SubReturningInsertSpec extends DqlStatement._DqlInsertSpec<SubReturningInsert> {

    }


}
