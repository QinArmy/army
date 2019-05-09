package org.qinarmy.army.criteria;

import org.springframework.lang.NonNull;

import java.util.List;

/**
 * created  on 2018/10/21.
 */
public interface WhereAble {

    @NonNull
    QueryAble where(Predicate... predicates);

    @NonNull
    QueryAble where(@NonNull List<Predicate> predicateList);


}
