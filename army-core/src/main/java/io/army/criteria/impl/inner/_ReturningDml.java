package io.army.criteria.impl.inner;

/**
 * <p>
 * This interface representing the dml statement with RETURNING clause that return result set.
 * This implementation possibly is :
 * <ul>
 *     <li>{@link io.army.criteria.dialect.ReturningInsert}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningUpdate}</li>
 *     <li>{@link io.army.criteria.dialect.ReturningDelete}</li>
 *     <li>{@link io.army.criteria.SubStatement} with RETURNING clause and return result set</li>
 * </ul>
*/
public interface _ReturningDml extends _Statement._ReturningListSpec {

}
