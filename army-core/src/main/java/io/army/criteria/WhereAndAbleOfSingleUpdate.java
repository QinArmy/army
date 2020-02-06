package io.army.criteria;

import io.army.domain.IDomain;

public interface WhereAndAbleOfSingleUpdate <T extends IDomain> extends OrderAbleOfSingleUpdate<T> {

    WhereAndAbleOfSingleUpdate<T> and(Predicate predicate);
}
