package io.army.criteria;


import io.army.util.Pair;
import io.army.util.Triple;

/**
 * created  on 2018/10/21.
 */
public interface QueryAble extends CriteriaContextCapable {

    SqlBuilder setLockMode(LockMode lockMode);

    <R> Query<R> createQuery(Class<R> resultType);

    <F, S> Query<Pair<F, S>> createQuery(Class<F> firstType, Class<S> secondType);


    <F, S, T> Query<Triple<F, S, T>> createQuery(Class<F> firstType, Class<S> secondType, Class<T> thirdType);


}
