package io.army.criteria;

import io.army.domain.IDomain;

import java.util.List;

public interface WhereAbleOfSingleUpdate<T extends IDomain> extends SingleSetAble<T> {

    OrderAbleOfSingleUpdate<T> where(List<Predicate> predicateList);
}
