package io.army.criteria;


import io.army.criteria.impl.SQLs;

/**
 * <p>
 * This interface representing simple {@link IPredicate} :
 *     <ul>
 *         <li>{@link SQLs#TRUE}</li>
 *         <li>{@link SQLs#FALSE}</li>
 *         <li>parentheses predicate,for example (a.balance > 10 or a.id > 100)</li>
 *         <li>sql function that return {@link IPredicate}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SimplePredicate extends IPredicate {


}
