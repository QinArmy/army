package io.army.criteria;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2018/10/21.
 */
public interface WhereAble {

    @NonNull
    QueryAble where(IPredicate... IPredicates);

    @NonNull
    QueryAble where(@NonNull List<IPredicate> IPredicateList);


}
