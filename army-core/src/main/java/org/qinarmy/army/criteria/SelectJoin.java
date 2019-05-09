package org.qinarmy.army.criteria;

/**
 * created  on 2019-02-07.
 */
public interface SelectJoin<X> extends Join<X>, SelectWhereAble {

    @Override
    SelectJoin<X> as(String alias);

    @Override
    <Y> SelectJoin<Y> join(Class<Y> tableClass);

    @Override
    SelectJoin<X> on(Predicate... predicate);

    <Y> SelectJoin<Y> straightJoin(Class<Y> tableClass);

    <Y> SelectJoin<Y> crossJoin(Class<Y> tableClass);

    <Y> SelectJoin<Y> leftJoin(Class<Y> tableClass);


    <Y> SelectJoin<Y> rightJoin(Class<Y> tableClass);

}
