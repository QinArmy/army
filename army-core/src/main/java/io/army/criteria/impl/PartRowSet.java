package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.Dialect;
import io.army.dialect._MockDialects;
import io.army.stmt.SimpleStmt;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class PartRowSet<C, Q extends RowSet, FT, FS, FP, JT, JS, JP, UR, OR, LR, SP>
        extends JoinableClause<C, FT, FS, FP, JT, JS, JP>
        implements CriteriaContextSpec, _PartRowSet, Statement._OrderByClause<C, OR>, Query._LimitClause<C, LR>
        , Query._QueryUnionClause<C, UR, SP>, CriteriaSpec<C>, RowSet.RowSetSpec<Q>, _SelfDescribed {

    final CriteriaContext criteriaContext;

    private List<ArmySortItem> orderByList;

    private long offset = -1L;

    private long rowCount = -1L;

    private boolean prepared;


    PartRowSet(CriteriaContext criteriaContext, ClauseSupplier suppler) {
        super(suppler, criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
    }

    PartRowSet(CriteriaContext criteriaContext) {
        super(criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
    }

    @Override
    public final CriteriaContext getCriteriaContext() {
        return this.criteriaContext;
    }

    @Override
    public final UR bracket() {

        final boolean isUnionAndRowSet = this instanceof UnionAndRowSet;
        final Q thisQuery;
        thisQuery = this.asRowSet(!isUnionAndRowSet);

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
        return this.createUnionRowSet(this.asQuery(), UnionType.UNION, function.apply(this.criteria));
    }

    @Override
    public final UR union(Supplier<? extends RowSet> supplier) {
        return this.createUnionRowSet(this.asQuery(), UnionType.UNION, supplier.get());
    }

    @Override
    public final SP union() {
        return this.asUnionAndRowSet(UnionType.UNION);
    }

    @Override
    public final UR unionAll(Function<C, ? extends RowSet> function) {
        return this.createUnionRowSet(this.asQuery(), UnionType.UNION_ALL, function.apply(this.criteria));
    }

    @Override
    public final UR unionAll(Supplier<? extends RowSet> supplier) {
        return this.createUnionRowSet(this.asQuery(), UnionType.UNION_ALL, supplier.get());
    }

    @Override
    public final SP unionAll() {
        return this.asUnionAndRowSet(UnionType.UNION_ALL);
    }

    @Override
    public final UR unionDistinct(Function<C, ? extends RowSet> function) {
        return this.createUnionRowSet(this.asQuery(), UnionType.UNION_DISTINCT, function.apply(this.criteria));
    }

    @Override
    public final UR unionDistinct(Supplier<? extends RowSet> supplier) {
        return this.createUnionRowSet(this.asQuery(), UnionType.UNION_DISTINCT, supplier.get());
    }

    @Override
    public final SP unionDistinct() {
        return this.asUnionAndRowSet(UnionType.UNION_DISTINCT);
    }

    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR orderBy(Function<C, List<S>> function) {
        this.orderByList = CriteriaUtils.asSortItemList(function.apply(this.criteria));
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR orderBy(Supplier<List<S>> supplier) {
        this.orderByList = CriteriaUtils.asSortItemList(supplier.get());
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<List<SortItem>> consumer) {
        final List<SortItem> sortItemList = new ArrayList<>();
        consumer.accept(sortItemList);
        this.orderByList = CriteriaUtils.asSortItemList(sortItemList);
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR ifOrderBy(Supplier<List<S>> supplier) {
        final List<S> sortItemList;
        sortItemList = supplier.get();
        if (sortItemList != null && sortItemList.size() > 0) {
            this.orderByList = CriteriaUtils.asSortItemList(sortItemList);
        }
        this.onOrderBy();
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR ifOrderBy(Function<C, List<S>> function) {
        final List<S> sortItemList;
        sortItemList = function.apply(this.criteria);
        if (sortItemList != null && sortItemList.size() > 0) {
            this.orderByList = CriteriaUtils.asSortItemList(sortItemList);
        }
        this.onOrderBy();
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
    public final Q asQuery() {
        return this.asRowSet(true);
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
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
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


    void onOrderBy() {

    }

    abstract Dialect defaultDialect();

    abstract void validateDialect(Dialect mode);

    abstract Q internalAsRowSet(boolean fromAsQueryMethod);

    abstract UR createBracketQuery(RowSet rowSet);

    abstract void internalClear();

    abstract UR createUnionRowSet(RowSet left, UnionType unionType, RowSet right);

    abstract SP asUnionAndRowSet(UnionType unionType);


    private Q asRowSet(final boolean fromAsQueryMethod) {
        _Assert.nonPrepared(this.prepared);

        final List<? extends SortItem> sortItemList = this.orderByList;
        if (sortItemList == null) {
            this.orderByList = Collections.emptyList();
        }
        final Q query;
        query = internalAsRowSet(fromAsQueryMethod);
        this.prepared = true;
        return query;
    }


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


    /**
     * <p>
     * This interface representing the type that is returned by below methods:
     * <ul>
     *     <li> {@link DialectStatement._DialectUnionClause#union()}</li>
     *     <li> {@link DialectStatement._DialectUnionClause#unionAll()}</li>
     *     <li> {@link DialectStatement._DialectUnionClause#unionDistinct()}</li>
     * </ul>
     * </p>
     *  <p>
     *      This package interface
     *  </p>
     *
     * @since 1.0
     */
    interface UnionAndRowSet extends RowSet {

        RowSet leftRowSet();

        UnionType unionType();

    }


}
