package io.army.criteria;

import org.springframework.lang.Nullable;

/**
 * created  on 2018/10/8.
 */
public interface Predicate extends Expression<Boolean> {

    @Nullable
    SubQuery getSubQuery();

    Predicate and(Predicate predicate);

    Predicate or(Predicate... predicates);


}
