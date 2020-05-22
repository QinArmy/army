package io.army.criteria;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * can'field {@code then}
 */
public interface IPredicate extends Expression<Boolean> {

    /**
     * Logical OR
     */
    IPredicate or(@Nullable IPredicate... andIPredicates);

    /**
     * Logical OR
     */
    IPredicate or(List<IPredicate> andIPredicateList);


    /**
     * Logical NOT
     */
    IPredicate not(IPredicate predicate);

}
