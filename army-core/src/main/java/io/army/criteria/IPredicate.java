package io.army.criteria;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * can'field {@code then}
 * created  on 2018/10/8.
 */
public interface IPredicate extends Expression<Boolean> {

    IPredicate or(@Nullable IPredicate... andIPredicates);

    IPredicate or(List<IPredicate> andIPredicateList);


}
