package io.army.criteria;

/**
 * <p>
 * This interface is designed for NOT operator and  dialect logical operator,for example :
 *     <ul>
 *         <li>{@link io.army.criteria.impl.SQLs#not(IPredicate)}</li>
 *         <li>{@code MySQL.xor(IPredicate,IPredicate)}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface LogicalPredicate extends IPredicate, SimplePredicate {


}
