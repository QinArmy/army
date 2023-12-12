package io.army.criteria;

/**
 * <p>
 * This interface representing simple(non-batch) primary INSERT statement that don't return result set.
*
 * @see io.army.criteria.dialect.ReturningInsert
 * @since 1.0
 */
public interface Insert extends SimpleDmlStatement, InsertStatement {


}
