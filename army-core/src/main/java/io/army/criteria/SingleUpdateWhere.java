package io.army.criteria;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
public interface SingleUpdateWhere extends WhereAble {

    @Override
    OrderAble where(IPredicate... IPredicates);

    @Override
    OrderAble where(List<IPredicate> IPredicateList);
}
