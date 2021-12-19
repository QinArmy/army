package io.army.criteria;

import java.util.List;

/**
 * can'field {@code then}
 */
public interface IPredicate extends Expression<Boolean> {

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicate)
     * </p>
     */
    IPredicate or(IPredicate predicate);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR ( predicate1 AND predicate2 ) )
     * </p>
     */
    IPredicate or(IPredicate predicate1, IPredicate predicate2);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR ( predicate1 AND predicate2 AND predicate3) )
     * </p>
     */
    IPredicate or(IPredicate predicate1, IPredicate predicate2, IPredicate predicate3);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  (predicates))
     * </p>
     */
    IPredicate or(List<IPredicate> predicates);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();


}
