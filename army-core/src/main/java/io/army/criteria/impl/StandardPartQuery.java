package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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


    @Override
    public final Query.UnionSpec<Q, C> union(Function<C, Q> function) {
        return innerCreate(UnionType.UNION, function);
    }

    @Override
    public final Query.UnionSpec<Q, C> union(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION, supplier);
    }

    @Override
    public final SelectPartSpec<Q, C> union() {
        return this.asQueryAndSelect(UnionType.UNION);
    }

    @Override
    public final Query.UnionSpec<Q, C> unionAll(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_ALL, function);
    }

    @Override
    public final Query.UnionSpec<Q, C> unionAll(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_ALL, supplier);
    }

    @Override
    public final SelectPartSpec<Q, C> unionAll() {
        return this.asQueryAndSelect(UnionType.UNION_ALL);
    }

    @Override
    public final Query.UnionSpec<Q, C> unionDistinct(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_DISTINCT, function);
    }

    @Override
    public final Query.UnionSpec<Q, C> unionDistinct(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_DISTINCT, supplier);
    }

    @Override
    public final SelectPartSpec<Q, C> unionDistinct() {
        return this.asQueryAndSelect(UnionType.UNION_DISTINCT);
    }

    @Override
    public final Query.LimitSpec<Q, C> orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return this;
    }

    @Override
    public final Query.LimitSpec<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = Arrays.asList(sortPart1, sortPart2);
        return this;
    }

    @Override
    public final Query.LimitSpec<Q, C> orderBy(List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("sortPartList must not empty.");
        }
        this.orderByList = new ArrayList<>(sortPartList);
        return this;
    }

    @Override
    public final Query.LimitSpec<Q, C> orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    @Override
    public final Query.LimitSpec<Q, C> orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    @Override
    public final Query.LimitSpec<Q, C> ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return this;
    }

    @Override
    public final Query.LimitSpec<Q, C> ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> supplierResult;
        supplierResult = supplier.get();
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        return this;
    }

    @Override
    public final Query.LimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> supplierResult;
        supplierResult = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        return this;
    }

    @Override
    public final Query.LockSpec<Q, C> limit(long rowCount) {
        this.offset = 0L;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Query.LockSpec<Q, C> limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Query.LockSpec<Q, C> limit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return this;
    }

    @Override
    public final Query.LockSpec<Q, C> limit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return this;
    }

    @Override
    public final Query.LockSpec<Q, C> ifLimit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return this;
    }

    @Override
    public final Query.LockSpec<Q, C> ifLimit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return this;
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

    abstract Query.UnionSpec<Q, C> createUnionQuery(Q left, UnionType unionType, Q right);

    abstract Q internalAsQuery();

    abstract SelectPartSpec<Q, C> asQueryAndSelect(UnionType unionType);


    private Query.UnionSpec<Q, C> innerCreate(UnionType unionType, Function<C, Q> function) {
        final Q left, right;
        //firstly(must),end this query
        left = this.asQuery();
        right = function.apply(this.criteria);
        assert right != null;
        right.prepared();
        return createUnionQuery(left, unionType, right);
    }

    private Query.UnionSpec<Q, C> innerCreate(UnionType unionType, Supplier<Q> supplier) {
        final Q left, right;
        //firstly(must),end this query
        left = this.asQuery();
        right = supplier.get();
        assert right != null;
        right.prepared();
        return createUnionQuery(left, unionType, right);
    }


}
