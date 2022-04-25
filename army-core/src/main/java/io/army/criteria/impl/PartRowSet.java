package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.dialect.Dialect;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.stmt.SimpleStmt;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class PartRowSet<C, Q extends RowSet, UR, OR, LR, SP> implements CriteriaContextSpec, _PartRowSet, RowSet
        , Query.OrderByClause<C, OR>, Query.LimitClause<C, LR>, Query.QueryUnionClause<C, UR, SP>, CriteriaSpec<C>
        , Values.ValuesSpec {

    static final boolean FOR_AS_ROW_SET = false;

    final C criteria;

    final CriteriaContext criteriaContext;

    private List<ArmySortItem> orderByList;

    private long offset = -1L;

    private long rowCount = -1L;

    private boolean prepared;


    PartRowSet(CriteriaContext criteriaContext) {
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
    public final UR bracket() {

        final boolean isUnionAndRowSet = this instanceof UnionAndRowSet;
        final Q thisQuery;
        thisQuery = this.asRowSet(isUnionAndRowSet);

        final UR spec;
        if (!isUnionAndRowSet) {
            spec = createBracketQuery(thisQuery);
        } else if (!(this instanceof ScalarSubQuery)) {
            if (thisQuery != this) {
                throw asQueryMethodError();
            }
            final UnionAndRowSet unionAndRowSet = (UnionAndRowSet) this;
            spec = createUnionRowSet(unionAndRowSet.leftRowSet(), unionAndRowSet.unionType(), thisQuery);
        } else if (!(thisQuery instanceof ScalarSubQueryExpression)) {
            throw asQueryMethodError();
        } else if (((ScalarSubQueryExpression) thisQuery).subQuery != this) {
            throw asQueryMethodError();
        } else {
            final UnionAndRowSet unionAndRowSet = (UnionAndRowSet) this;
            spec = createUnionRowSet(unionAndRowSet.leftRowSet(), unionAndRowSet.unionType(), thisQuery);
        }
        return spec;
    }


    @Override
    public final UR union(Function<C, ? extends RowSet> function) {
        return this.createUnionRowSet(this.asRowSet(FOR_AS_ROW_SET), UnionType.UNION, function.apply(this.criteria));
    }

    @Override
    public final UR union(Supplier<? extends RowSet> supplier) {
        return this.createUnionRowSet(this.asRowSet(FOR_AS_ROW_SET), UnionType.UNION, supplier.get());
    }

    @Override
    public final SP union() {
        return this.asUnionAndRowSet(UnionType.UNION);
    }

    @Override
    public final UR unionAll(Function<C, ? extends RowSet> function) {
        return this.createUnionRowSet(this.asRowSet(FOR_AS_ROW_SET), UnionType.UNION_ALL, function.apply(this.criteria));
    }

    @Override
    public final UR unionAll(Supplier<? extends RowSet> supplier) {
        return this.createUnionRowSet(this.asRowSet(FOR_AS_ROW_SET), UnionType.UNION_ALL, supplier.get());
    }

    @Override
    public final SP unionAll() {
        return this.asUnionAndRowSet(UnionType.UNION_ALL);
    }

    @Override
    public final UR unionDistinct(Function<C, ? extends RowSet> function) {
        return this.createUnionRowSet(this.asRowSet(FOR_AS_ROW_SET), UnionType.UNION_DISTINCT, function.apply(this.criteria));
    }

    @Override
    public final UR unionDistinct(Supplier<? extends RowSet> supplier) {
        return this.createUnionRowSet(this.asRowSet(FOR_AS_ROW_SET), UnionType.UNION_DISTINCT, supplier.get());
    }

    @Override
    public final SP unionDistinct() {
        return this.asUnionAndRowSet(UnionType.UNION_DISTINCT);
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
        if (!_CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<C, List<SortItem>> function) {
        final List<SortItem> supplierResult;
        supplierResult = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(supplierResult)) {
            this.orderBy(supplierResult);
        }
        this.afterOrderBy();
        return (OR) this;
    }

    @Override
    public final LR limit(long rowCount) {
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
    public final LR limit(Supplier<? extends Number> rowCountSupplier) {
        final Number rowCountValue;
        rowCountValue = rowCountSupplier.get();
        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        } else {
            throw supplierReturnError(rowCountValue);
        }
        return (LR) this;
    }


    @Override
    public final LR limit(Function<String, ?> function, String rowCountKey) {
        final Object rowCountValue;
        rowCountValue = function.apply(rowCountKey);
        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        } else {
            throw supplierReturnError(rowCountValue);
        }
        return (LR) this;
    }


    @Override
    public final LR limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        final Number offsetValue, rowCountValue;
        offsetValue = offsetSupplier.get();

        if (offsetValue instanceof Long) {
            this.offset = (Long) offsetValue;
        } else if (offsetValue instanceof Integer) {
            this.offset = (Integer) offsetValue;
        } else {
            throw supplierReturnError(offsetValue);
        }
        rowCountValue = rowCountSupplier.get();
        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        } else {
            throw supplierReturnError(rowCountValue);
        }
        return (LR) this;
    }

    @Override
    public final LR limit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        final Object offsetValue, rowCountValue;
        offsetValue = function.apply(offsetKey);

        if (offsetValue instanceof Long) {
            this.offset = (Long) offsetValue;
        } else if (offsetValue instanceof Integer) {
            this.offset = (Integer) offsetValue;
        } else {
            throw functionReturnError(offsetValue);
        }
        rowCountValue = function.apply(rowCountKey);
        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        } else {
            throw functionReturnError(rowCountValue);
        }
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
    public final LR ifLimit(Supplier<? extends Number> rowCountSupplier) {
        final Object rowCountValue;
        rowCountValue = rowCountSupplier.get();
        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        final Object offsetValue, rowCountValue;
        offsetValue = offsetSupplier.get();
        if (offsetValue instanceof Long) {
            this.offset = (Long) offsetValue;
        } else if (offsetValue instanceof Integer) {
            this.offset = (Integer) offsetValue;
        } else {
            return (LR) this;
        }
        rowCountValue = rowCountSupplier.get();
        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        } else {
            this.offset = -1L;
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<String, ?> function, String rowCountKey) {
        final Object rowCountValue;
        rowCountValue = function.apply(rowCountKey);

        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        final Object offsetValue, rowCountValue;
        offsetValue = function.apply(offsetKey);
        rowCountValue = function.apply(rowCountKey);

        if (offsetValue instanceof Long) {
            this.offset = (Long) offsetValue;
        } else if (offsetValue instanceof Integer) {
            this.offset = (Integer) offsetValue;
        } else {
            return (LR) this;
        }

        if (rowCountValue instanceof Long) {
            this.rowCount = (Long) rowCountValue;
        } else if (rowCountValue instanceof Integer) {
            this.rowCount = (Integer) rowCountValue;
        } else {
            this.offset = -1L;
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
    public final Values asValues() {
        final Q rowSet;
        rowSet = this.asRowSet(FOR_AS_ROW_SET);
        if (!(rowSet instanceof Values)) {
            throw _Exceptions.castCriteriaApi();
        }
        return (Values) rowSet;
    }

    public final Q asQuery() {
        final Q rowSet;
        rowSet = this.asRowSet(FOR_AS_ROW_SET);
        if (!(rowSet instanceof Query)) {
            throw _Exceptions.castCriteriaApi();
        }
        return rowSet;
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
        return !_CollectionUtils.isEmpty(this.orderByList);
    }

    final Q asRowSet(final boolean justAsQuery) {
        _Assert.nonPrepared(this.prepared);

        final List<? extends SortItem> sortItemList = this.orderByList;
        if (sortItemList == null) {
            this.orderByList = Collections.emptyList();
        }
        final Q query;
        query = internalAsRowSet(justAsQuery);
        this.prepared = true;
        return query;
    }

    void afterOrderBy() {

    }

    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect mode);

    abstract Q internalAsRowSet(boolean justAsQuery);

    abstract UR createBracketQuery(RowSet rowSet);

    abstract void internalClear();

    abstract UR createUnionRowSet(RowSet left, UnionType unionType, RowSet right);

    abstract SP asUnionAndRowSet(UnionType unionType);


    private static CriteriaException supplierReturnError(Object value) {
        String m = String.format("%s return %s ,but it isn't %s or %s ."
                , Supplier.class.getName(), value, Long.class.getName(), Integer.class.getName());
        return new CriteriaException(m);
    }

    private static CriteriaException functionReturnError(Object value) {
        String m = String.format("%s return %s ,but it isn't %s or %s ."
                , Function.class.getName(), value, Long.class.getName(), Integer.class.getName());
        return new CriteriaException(m);
    }

    private static IllegalStateException asQueryMethodError() {
        return new IllegalStateException("onAsQuery(boolean) error");
    }


}
