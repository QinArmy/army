package io.army.criteria;

import java.util.List;
import java.util.function.Consumer;
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
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... predicates[n])
     * </p>
     */
    IPredicate or(List<IPredicate> predicates);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... predicates[n])
     * </p>
     */
    <C> IPredicate or(Function<C, List<IPredicate>> function);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... predicates[n])
     * </p>
     */
    IPredicate or(Supplier<List<IPredicate>> supplier);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... predicates[n])
     * </p>
     */
    IPredicate or(Consumer<List<IPredicate>> consumer);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();


}
