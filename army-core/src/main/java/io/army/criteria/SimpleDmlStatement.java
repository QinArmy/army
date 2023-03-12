package io.army.criteria;


/**
 * <p>
 * This interface representing simple(non-batch) primary DML statement that don't return result set.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link Insert}</li>
 *     <li>{@link Update}</li>
 *     <li>{@link Delete}</li>
 *     <li>The dml command interface that don't return result set</li>
 * </ul>
 * </p>
 *
 * @see SimpleDqlStatement
 * @see BatchDmlStatement
 * @see io.army.criteria.dialect.BatchDqlStatement
 * @since 1.0
 */
public interface SimpleDmlStatement extends DmlStatement {


}
