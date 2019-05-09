package org.qinarmy.army.criteria;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
public interface SingleDeleteWhereAble extends WhereAble {

    @NonNull
    @Override
    OrderAble where(Predicate... predicates);

    @NonNull
    @Override
    OrderAble where(@NonNull List<Predicate> predicateList);

}
