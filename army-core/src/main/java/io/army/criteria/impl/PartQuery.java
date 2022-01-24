package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartQuery;
import io.army.dialect.Dialect;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.stmt.SimpleStmt;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class PartQuery<C, Q extends Query, UR, OR, LR, SP> implements CriteriaContextSpec, _PartQuery, Query
        , Query.OrderByClause<C, OR>, Query.LimitClause<C, LR>
        , Query.UnionClause<C, UR, SP, Q>, Query.QuerySpec<Q>, CriteriaSpec<C> {

    final C criteria;

    final CriteriaContext criteriaContext;

    private List<ArmySortItem> orderByList;

    private long offset = -1L;

    private long rowCount = -1L;

    private boolean prepared;


    PartQuery(CriteriaContext criteriaContext) {
        this.criteria = criteriaContext.criteria();
        this.criteriaContext = criteriaContext;
    }

    @Override
    public final C getCriteria() {
        return this.criteria;
    }

    @Override
    public final CriteriaContext getCriteriaContext() {
        return this.criteriaContext;
    }

    @Override
    public final UR union(Function<C, Q> function) {
        return innerCreate(UnionType.UNION, function);
    }

    @Override
    public final UR union(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION, supplier);
    }

    @Override
    public final SP union() {
        return this.asQueryAndQuery(UnionType.UNION);
    }

    @Override
    public final UR unionAll(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_ALL, function);
    }

    @Override
    public final UR unionAll(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_ALL, supplier);
    }

    @Override
    public final SP unionAll() {
        return this.asQueryAndQuery(UnionType.UNION_ALL);
    }

    @Override
    public final UR unionDistinct(Function<C, Q> function) {
        return innerCreate(UnionType.UNION_DISTINCT, function);
    }

    @Override
    public final UR unionDistinct(Supplier<Q> supplier) {
        return innerCreate(UnionType.UNION_DISTINCT, supplier);
    }

    @Override
    public final SP unionDistinct() {
        return this.asQueryAndQuery(UnionType.UNION_DISTINCT);
    }

    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList((ArmySortItem) sortItem1, (ArmySortItem) sortItem2);
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList((ArmySortItem) sortItem1, (ArmySortItem) sortItem2, (ArmySortItem) sortItem3);
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(List<SortItem> sortItemList) {
        final int size = sortItemList.size();
        switch (sortItemList.size()) {
            case 0:
                throw new CriteriaException("sortItemList must not empty.");
            case 1:
                this.orderByList = Collections.singletonList((ArmySortItem) sortItemList);
                break;
            default: {
                final List<ArmySortItem> tempList = new ArrayList<>(size);
                for (SortItem sortItem : sortItemList) {
                    tempList.add((ArmySortItem) sortItem);
                }
                this.orderByList = Collections.unmodifiableList(tempList);
            }
        }
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(Function<C, List<SortItem>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    @Override
    public final OR orderBy(Supplier<List<SortItem>> supplier) {
        return this.orderBy(supplier.get());
    }

    @Override
    public final OR ifOrderBy(@Nullable SortItem sortItem) {
        if (sortItem != null) {
            this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        }
        this.afterOrderBy();
        return (OR) this;
    }


    @Override
    public final OR ifOrderBy(Supplier<List<SortItem>> supplier) {
        final List<SortItem> supplierResult;
        supplierResult = supplier.get();
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<C, List<SortItem>> function) {
        final List<SortItem> supplierResult;
        supplierResult = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final LR limit(long rowCount) {
        this.offset = 0L;
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        assert option != null;
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<Long> rowCount) {
        this.rowCount = rowCount.get();
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<Long> offset, Supplier<Long> rowCount) {
        this.offset = offset.get();
        this.rowCount = rowCount.get();
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<Long> rowCountSupplier) {
        final Long rowCount;
        rowCount = rowCountSupplier.get();
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<Long> offsetSupplier, Supplier<Long> rowCountSupplier) {
        final Long offset, rowCount;
        offset = offsetSupplier.get();
        rowCount = rowCountSupplier.get();
        if (offset != null && rowCount != null) {
            this.offset = offset;
            this.rowCount = rowCount;
        }
        return (LR) this;
    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }


    @Override
    public final Q asQuery() {
        return this.innerAsQuery(true);
    }

    @Override
    public final List<? extends SortItem> orderByList() {
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


    @Override
    public final String mockAsString(Dialect dialect, Visible visible, boolean beautify) {
        final SimpleStmt stmt;
        stmt = this.mockAsStmt(dialect, visible);
        return "SELECT sql:\n" + stmt.sql();
    }


    @Override
    public final SimpleStmt mockAsStmt(Dialect dialect, Visible visible) {
        if (this instanceof SubQuery) {
            throw new IllegalStateException("mockAsStmt(DialectMode) support only Select statement.");
        }
        this.validateDialect(dialect);
        final SimpleStmt stmt;
        stmt = _MockDialects.from(dialect).select((Select) this, visible);
        _Assert.noNamedParam(stmt.paramGroup());
        return stmt;
    }

    @Override
    public final String toString() {
        final String s;
        if (this.prepared && this instanceof Select) {
            s = this.mockAsString(this.defaultDialect(), Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    final boolean hasOrderBy() {
        return !CollectionUtils.isEmpty(this.orderByList);
    }

    final Q asQueryAndQuery() {
        return innerAsQuery(false);
    }

    void afterOrderBy() {

    }

    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect mode);

    abstract Q internalAsQuery(boolean justAsQuery);

    abstract void internalClear();

    abstract UR createUnionQuery(Q left, UnionType unionType, Q right);

    abstract SP asQueryAndQuery(UnionType unionType);

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

    private Q innerAsQuery(final boolean justAsQuery) {
        _Assert.nonPrepared(this.prepared);

        final List<? extends SortItem> sortItemList = this.orderByList;
        if (sortItemList == null) {
            this.orderByList = Collections.emptyList();
        }
        final Q query;
        query = internalAsQuery(justAsQuery);
        this.prepared = true;
        return query;
    }


}
