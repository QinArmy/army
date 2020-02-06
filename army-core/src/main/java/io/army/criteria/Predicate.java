package io.army.criteria;

import org.springframework.lang.Nullable;

import java.util.List;

/**
 * can't {@code then}
 * created  on 2018/10/8.
 */
public interface Predicate extends Expression<Boolean> {

    Predicate or(Predicate... andPredicates);

    Predicate or(List<Predicate> andPredicateList);

}
