package org.qinarmy.army.criteria;

/**
 * created  on 2018/11/24.
 */
public interface HavingAble extends OrderAble {

    OrderAble having(Predicate... predicates);

}
