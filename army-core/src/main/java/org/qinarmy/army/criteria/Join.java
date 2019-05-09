package org.qinarmy.army.criteria;

/**
 * created  on 2018/10/9.
 */
public interface Join<X> extends WhereAble {


    Join<X> as(String alias);


    <Y> Join<Y> join(Class<Y> tableClass);


    Join<X> on(Predicate... predicate);


}
