package io.army.criteria.impl;

import io.army.criteria.LockMode;
import io.army.criteria.Query;
import io.army.criteria.QueryAble;
import io.army.criteria.SqlBuilder;
import io.army.util.Pair;
import io.army.util.Triple;

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
