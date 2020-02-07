package io.army.criteria;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
public interface SingleDeleteWhereAble extends WhereAble {

    @NonNull
    @Override
    OrderAble where(IPredicate... IPredicates);

    @NonNull
    @Override
    OrderAble where(@NonNull List<IPredicate> IPredicateList);

}
