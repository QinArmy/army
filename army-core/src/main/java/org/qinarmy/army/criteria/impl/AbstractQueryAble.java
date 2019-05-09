package org.qinarmy.army.criteria.impl;

import org.qinarmy.army.criteria.LockMode;
import org.qinarmy.army.criteria.Query;
import org.qinarmy.army.criteria.QueryAble;
import org.qinarmy.army.criteria.SqlBuilder;
import org.qinarmy.army.util.Pair;
import org.qinarmy.army.util.Triple;

/**
 * created  on 2018-12-24.
 */
abstract class AbstractQueryAble implements QueryAble {

    @Override
    public SqlBuilder setLockMode(LockMode lockMode) {
        return null;
    }

    @Override
    public <R> Query<R> createQuery(Class<R> resultType) {
        return null;
    }

    @Override
    public <F, S> Query<Pair<F, S>> createQuery(Class<F> firstType, Class<S> secondType) {
        return null;
    }

    @Override
    public <F, S, T> Query<Triple<F, S, T>> createQuery(Class<F> firstType, Class<S> secondType, Class<T> thirdType) {
        return null;
    }
}
