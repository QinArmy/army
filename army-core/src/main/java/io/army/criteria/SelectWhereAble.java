package io.army.criteria;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
public interface SelectWhereAble extends WhereAble, GroupAble {

    @NonNull
    @Override
    GroupAble where(IPredicate... IPredicates);

    @NonNull
    @Override
    GroupAble where(@NonNull List<IPredicate> IPredicateList);

}
