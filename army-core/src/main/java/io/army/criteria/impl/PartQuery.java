package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.LimitOption;
import io.army.criteria.Query;
import io.army.criteria.SortPart;
import io.army.criteria.impl.inner._PartQuery;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class PartQuery<C, Q extends Query, UR, SP, OR, LR> implements _PartQuery, Query
        , Query.OrderByClause<C, OR>, Query.LimitClause<C, LR>
        , Query.UnionClause<C, UR, SP, Q>, Query.QuerySpec<Q> {

    final C criteria;

    private List<SortPart> orderByList;

    private long offset;

    private long rowCount;

    private boolean prepared;


    PartQuery(@Nullable C criteria) {
        this.criteria = criteria;
    }


    public final UR union(Function<C, Q> function) {
        return innerCreate(UnionType.UNION, function);
    }

    public final UR union(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION, supplier);
    }

    public final SP union() {
        return this.asQueryAndSelect(UnionType.UNION);
    }

    public final UR unionAll(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_ALL, function);
    }

    public final UR unionAll(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_ALL, supplier);
    }

    public final SP unionAll() {
        return this.asQueryAndSelect(UnionType.UNION_ALL);
    }

    public final UR unionDistinct(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_DISTINCT, function);
    }

    public final UR unionDistinct(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_DISTINCT, supplier);
    }

    public final SP unionDistinct() {
        return this.asQueryAndSelect(UnionType.UNION_DISTINCT);
    }

    public final OR orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return (OR) this;
    }

    public final OR orderBy(SortPart sortPart1, SortPart sortPart2) {
        return this.orderBy(Arrays.asList(sortPart1, sortPart2));
    }

    public final OR orderBy(List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("sortPartList must not empty.");
        }
        this.orderByList = new ArrayList<>(sortPartList);
        return (OR) this;
    }

    public final OR orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    public final OR orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    public final OR ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return (OR) this;
    }

    public final OR ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> supplierResult;
        supplierResult = supplier.get();
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        return (OR) this;
    }

    public final OR ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> supplierResult;
        supplierResult = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        return (OR) this;
    }

    public final LR limit(long rowCount) {
        this.offset = 0L;
        this.rowCount = rowCount;
        return (LR) this;
    }

    public final LR limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return (LR) this;
    }

    public final LR limit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return (LR) this;
    }

    public final LR limit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return (LR) this;
    }

    public final LR ifLimit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return (LR) this;
    }

    public final LR ifLimit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return (LR) this;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean requiredBrackets() {
        return !CollectionUtils.isEmpty(this.orderByList) || this.offset >= 0L || this.rowCount >= 0L;
    }

    @Override
    public final Q asQuery() {
        _Assert.nonPrepared(this.prepared);


        final List<SortPart> sortPartList = this.orderByList;
        if (sortPartList == null) {
            this.orderByList = Collections.emptyList();
        } else {
            this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
        }

        final Q query;
        query = internalAsQuery();
        this.prepared = true;
        return query;
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
        this.offset = -1;
        this.rowCount = -1;
        this.internalClear();
    }

    abstract Q internalAsQuery();

    abstract void internalClear();

    abstract UR createUnionQuery(Q left, UnionType unionType, Q right);

    abstract SP asQueryAndSelect(UnionType unionType);

    private UR innerCreate(UnionType unionType, Function<C, Q> function) {
        final Q left, right;
        //firstly(must),end this query
        left = this.asQuery();
        right = function.apply(this.criteria);
        assert right != null;
        right.prepared();
        return createUnionQuery(left, unionType, right);
    }

    private UR innerCreate(UnionType unionType, Supplier<Q> supplier) {
        final Q left, right;
        //firstly(must),end this query
        left = this.asQuery();
        right = supplier.get();
        assert right != null;
        right.prepared();
        return createUnionQuery(left, unionType, right);
    }





}
