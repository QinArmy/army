package io.army.criteria;

import io.army.domain.IDomain;

import java.util.List;

/**
 *
 * @param <T> domain java class
 * @see SingleUpdateAble
 */
public interface WhereAbleOfSingleUpdate<T extends IDomain> extends SetAbleOfSingleUpdate<T>{

    OrderAbleOfSingleUpdate<T> where(List<Predicate> predicateList);
}
