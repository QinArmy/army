package org.qinarmy.army.criteria;

import java.util.List;

/**
 * created  on 2019-02-01.
 */
public interface SingleUpdateWhere extends WhereAble {

    @Override
    OrderAble where(Predicate... predicates);

    @Override
    OrderAble where(List<Predicate> predicateList);
}
