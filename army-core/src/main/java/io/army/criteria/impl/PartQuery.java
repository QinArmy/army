package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util.CollectionUtils;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class PartQuery<C, Q extends Query, U, SP, O, L>
        implements _PartQuery, Query, Query.QuerySpec<Q> {

    final C criteria;

    private List<SortPart> orderByList;

    private long offset;

    private long rowCount;

    private boolean prepared;


    PartQuery(@Nullable C criteria) {
        this.criteria = criteria;
    }


    public final U union(Function<C, Q> function) {
        return innerCreate(UnionType.UNION, function);
    }

    public final U union(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION, supplier);
    }

    public final SP union() {
        return this.asQueryAndSelect(UnionType.UNION);
    }

    public final U unionAll(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_ALL, function);
    }

    public final U unionAll(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_ALL, supplier);
    }

    public final SP unionAll() {
        return this.asQueryAndSelect(UnionType.UNION_ALL);
    }

    public final U unionDistinct(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_DISTINCT, function);
    }

    public final U unionDistinct(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_DISTINCT, supplier);
    }

    public final SP unionDistinct() {
        return this.asQueryAndSelect(UnionType.UNION_DISTINCT);
    }

    public final O orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return (O) this;
    }

    public final O orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = Arrays.asList(sortPart1, sortPart2);
        return (O) this;
    }

    public final O orderBy(List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("sortPartList must not empty.");
        }
        this.orderByList = new ArrayList<>(sortPartList);
        return (O) this;
    }

    public final O orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    public final O orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    public final O ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return (O) this;
    }

    public final O ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> supplierResult;
        supplierResult = supplier.get();
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        return (O) this;
    }

    public final O ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> supplierResult;
        supplierResult = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        return (O) this;
    }

    public final L limit(long rowCount) {
        this.offset = 0L;
        this.rowCount = rowCount;
        return (L) this;
    }

    public final L limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return (L) this;
    }

    public final L limit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return (L) this;
    }

    public final L limit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return (L) this;
    }

    public final L ifLimit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return (L) this;
    }

    public final L ifLimit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return (L) this;
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

    }

    abstract Q internalAsQuery();

    abstract U createUnionQuery(Q left, UnionType unionType, Q right);

    abstract SP asQueryAndSelect(UnionType unionType);

    private U innerCreate(UnionType unionType, Function<C, Q> function) {
        final Q left, right;
        //firstly(must),end this query
        left = this.asQuery();
        right = function.apply(this.criteria);
        assert right != null;
        right.prepared();
        return createUnionQuery(left, unionType, right);
    }

    private U innerCreate(UnionType unionType, Supplier<Q> supplier) {
        final Q left, right;
        //firstly(must),end this query
        left = this.asQuery();
        right = supplier.get();
        assert right != null;
        right.prepared();
        return createUnionQuery(left, unionType, right);
    }


    static abstract class PartQueryHolder<C> {

        final C criteria;

        private List<SortPart> orderByList;

        private long offset;

        private long rowCount;

        PartQueryHolder(@Nullable C criteria) {
            this.criteria = criteria;
        }

        final boolean requiredBrackets() {
            return !CollectionUtils.isEmpty(this.orderByList) || this.offset >= 0L || this.rowCount >= 0L;
        }

        final void orderBy(SortPart sortPart) {
            this.orderByList = Collections.singletonList(sortPart);
        }

        final void orderBy(SortPart sortPart1, SortPart sortPart2) {
            this.orderByList = Arrays.asList(sortPart1, sortPart2);
        }

        final void orderBy(List<SortPart> sortPartList) {
            if (sortPartList.size() == 0) {
                throw new CriteriaException("sortPartList must not empty.");
            }
            this.orderByList = new ArrayList<>(sortPartList);
        }

        final void orderBy(Function<C, List<SortPart>> function) {
            this.orderBy(function.apply(this.criteria));
        }

        final void orderBy(Supplier<List<SortPart>> supplier) {
            this.orderBy(supplier.get());
        }

        final void ifOrderBy(@Nullable SortPart sortPart) {
            if (sortPart != null) {
                this.orderByList = Collections.singletonList(sortPart);
            }
        }

        final void ifOrderBy(Supplier<List<SortPart>> supplier) {
            final List<SortPart> supplierResult;
            supplierResult = supplier.get();
            if (!CollectionUtils.isEmpty(supplierResult)) {
                this.orderBy(supplierResult);
            }
        }


        final void ifOrderBy(Function<C, List<SortPart>> function) {
            final List<SortPart> supplierResult;
            supplierResult = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(supplierResult)) {
                this.orderBy(supplierResult);
            }
        }

        final void limit(long rowCount) {
            this.offset = -1L;
            this.rowCount = rowCount;
        }

        final void limit(Function<C, LimitOption> function) {
            final LimitOption option;
            option = function.apply(this.criteria);
            assert option != null;
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }

        final void limit(Supplier<LimitOption> supplier) {
            final LimitOption option;
            option = supplier.get();
            assert option != null;
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }

        final void ifLimit(Function<C, LimitOption> function) {
            final LimitOption option;
            option = function.apply(this.criteria);
            if (option != null) {
                this.offset = option.offset();
                this.rowCount = option.rowCount();
            }
        }


        final void ifLimit(Supplier<LimitOption> supplier) {
            final LimitOption option;
            option = supplier.get();
            if (option != null) {
                this.offset = option.offset();
                this.rowCount = option.rowCount();
            }

        }


    }

    abstract class QueryHolder<C> extends PartQueryHolder<C> {

        private List<SQLModifier> modifierList;

        private List<SelectPart> selectPartList;

        private List<TableBlock> tableBlockList = new ArrayList<>();

        private List<_Predicate> predicateList = new ArrayList<>();

        private List<SortPart> groupByList;

        private List<_Predicate> havingList;

        private LockMode lockMode;

        private TableOnSpec<Q, C> noActionBlock;


        QueryHolder(@Nullable C criteria) {
            super(criteria);
        }


    }


}
