package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.util.ArrayUtils;
import io.army.util.Assert;
import io.army.util.Pair;
import io.army.util.Triple;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * created  on 2018/10/21.
 */
class SelectedListImpl implements InnerSelectList {

    private final CriteriaContext criteriaContext = new MutableCriteriaContext(UUID.randomUUID().toString());



    private final List<?> selectionList;

    SelectedListImpl(@Nullable Object... selections) {
        Assert.assertNotNull(selections, "selections required");
        this.selectionList = ArrayUtils.asUnmodifiableList(selections);

    }

    SelectedListImpl(@NonNull List<?> selectionList) {
        Assert.assertNotEmpty(selectionList, "selectionList required");
        this.selectionList = Collections.unmodifiableList(selectionList);
    }

    @NonNull
    @Override
    public CriteriaContext getCriteriaContext() {
        return criteriaContext;
    }

    @Override
    public SqlBuilder setLockMode(LockMode lockMode) {
        return null;
    }

    @NonNull
    @Override
    public <X extends IDomain> SelectJoin<X> from(@NonNull Class<X> tableClass) throws CriteriaException {
        return null;
    }

    @NonNull
    @Override
    public <X> SelectJoin<X> from(SubQuery<X> subQuery) throws CriteriaException {
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
