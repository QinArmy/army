package io.army.criteria.impl;

import io.army.criteria.Query;
import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._PartQuery;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;

abstract class StandardPartQuery<Q extends Query, C> implements Query.UnionSpec<Q, C>, Query.OrderBySpec<Q, C>
        , _PartQuery, Query {

    final C criteria;

    private List<SortPart> orderByList;

    private long offset = -1L;

    private long rowCount = -1L;

    private boolean prepared;

    StandardPartQuery(@Nullable C criteria) {
        this.criteria = criteria;
    }


    @Override
    public final boolean requiredBrackets() {
        return !CollectionUtils.isEmpty(this.orderByList) || this.offset >= 0L || this.rowCount >= 0L;
    }

    @Override
    public final Q asQuery() {
        _Assert.nonPrepared(this.prepared);
        final List<SortPart> sortPartList = this.orderByList;

        if (CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = Collections.unmodifiableList(sortPartList);
        } else {
            this.orderByList = Collections.emptyList();
        }

        final Q query;
        query = internalAsQuery();
        this.prepared = true;
        return query;
    }



    /*################################## blow _UniqueQuery method ##################################*/

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final List<SortPart> orderByList() {
        _Assert.prepared(this.prepared);
        return this.orderByList;
    }

    @Override
    public final long offset() {
        return this.offset;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    @Override
    public final void clear() {
        this.prepared = false;

        this.orderByList = null;
        this.offset = -1L;
        this.rowCount = -1L;

        this.internalClear();
    }


    abstract void internalClear();


    abstract Q internalAsQuery();


}
