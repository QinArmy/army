package io.army.criteria;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public interface IPredicate extends Expression {

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR predicate)
     * </p>
     */
    IPredicate or(IPredicate predicate);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... OR predicates[n])
     * </p>
     *
     * @param predicates return list that non-null,possibly empty.
     */
    IPredicate or(List<IPredicate> predicates);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... OR predicates[n])
     * </p>
     *
     * @param function return list that non-null,possibly empty.
     */
    <C> IPredicate or(Function<C, List<IPredicate>> function);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... OR predicates[n])
     * </p>
     *
     * @param supplier return list that non-null,possibly empty.
     */
    IPredicate or(Supplier<List<IPredicate>> supplier);

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR  predicates[0] OR predicates[1] OR ... OR predicates[n])
     * </p>
     *
     * @param consumer you can add 0-n {@link IPredicate}
     */
    IPredicate or(Consumer<List<IPredicate>> consumer);

    /**
     * Logical ADN
     * <p>
     * This method representing expression this ADN predicate
     * </p>
     *
     * @param predicate non-null
     */
    IPredicate and(IPredicate predicate);

    /**
     * Logical ADN
     * <p>
     * This method representing expression this OR  predicates[0] ADN predicates[1] ADN ... ADN predicates[n]
     * </p>
     *
     * @param predicates non-null,non-empty.
     */
    IPredicate and(List<IPredicate> predicates);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();


}
