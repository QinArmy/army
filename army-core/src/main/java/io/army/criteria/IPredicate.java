package io.army.criteria;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * This method representing expression (this OR  (predicates))
     * </p>
     */
    IPredicate or(List<IPredicate> predicates);

    <C> IPredicate orMulti(Function<C, List<IPredicate>> function);

    IPredicate orMulti(Supplier<List<IPredicate>> supplier);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();


}
