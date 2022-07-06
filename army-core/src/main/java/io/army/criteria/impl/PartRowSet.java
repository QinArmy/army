package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._PartRowSet;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class PartRowSet<C, Q extends RowSet, FT, FS, FP, JT, JS, JP, UR, OR, LR, SP>
        extends JoinableClause<C, FT, FS, FP, JT, JS, JP>
        implements CriteriaContextSpec, _PartRowSet, Statement._OrderByClause<C, OR>, Query._LimitClause<C, LR>
        , Query._QueryUnionClause<C, UR, SP>, CriteriaSpec<C>, RowSet._RowSetSpec<Q>, _SelfDescribed {

    final CriteriaContext criteriaContext;

    private List<ArmySortItem> orderByList;

    private long offset = -1L;

    private long rowCount = -1L;

    private Boolean prepared;


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

            spec = createUnionRowSet(unionAndRowSet.leftRowSet(), unionAndRowSet.unionType(), simpleBracket(thisQuery));
        } else if (!(thisQuery instanceof ScalarSubQueryExpression)) {
            throw asQueryMethodError();
        } else if (((ScalarSubQueryExpression) thisQuery).subQuery != this) {
            throw asQueryMethodError();
        } else {
            final UnionAndRowSet unionAndRowSet = (UnionAndRowSet) this;
            spec = createUnionRowSet(unionAndRowSet.leftRowSet(), unionAndRowSet.unionType(), simpleBracket(thisQuery));
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
    public final UR ifUnion(Function<C, ? extends RowSet> function) {
        return this.ifUnion(UnionType.UNION, function.apply(this.criteria));
    }


    @Override
    public final UR ifUnion(Supplier<? extends RowSet> supplier) {
        return this.ifUnion(UnionType.UNION, supplier.get());
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
    public final UR ifUnionAll(Function<C, ? extends RowSet> function) {
        return this.ifUnion(UnionType.UNION_ALL, function.apply(this.criteria));
    }

    @Override
    public final UR ifUnionAll(Supplier<? extends RowSet> supplier) {
        return this.ifUnion(UnionType.UNION_ALL, supplier.get());
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
    public final UR ifUnionDistinct(Function<C, ? extends RowSet> function) {
        return this.ifUnion(UnionType.UNION_DISTINCT, function.apply(this.criteria));
    }

    @Override
    public final UR ifUnionDistinct(Supplier<? extends RowSet> supplier) {
        return this.ifUnion(UnionType.UNION_DISTINCT, supplier.get());
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
    public final LR limit(final long rowCount) {
        if (rowCount < 0L) {
            throw CriteriaUtils.limitParamError(this.criteriaContext, rowCount);
        }
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<? extends Number> rowCountSupplier) {
        this.rowCount = CriteriaUtils.asLimitParam(this.criteriaContext, rowCountSupplier.get());
        return (LR) this;
    }


    @Override
    public final LR limit(Function<C, ? extends Number> function) {
        this.rowCount = CriteriaUtils.asLimitParam(this.criteriaContext, function.apply(this.criteria));
        return (LR) this;
    }

    @Override
    public final LR limit(Function<String, ?> function, String rowCountKey) {
        this.rowCount = CriteriaUtils.asLimitParam(this.criteriaContext, function.apply(rowCountKey));
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<? extends Number> rowCountSupplier) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.criteriaContext, rowCountSupplier.get());
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<C, ? extends Number> function) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.criteriaContext, function.apply(this.criteria));
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<String, ?> function, String rowCountKey) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.criteriaContext, function.apply(rowCountKey));
        return (LR) this;
    }

    /*-------------------below _LimitClause method-------------------*/

    @Override
    public final LR limit(final long offset, final long rowCount) {
        if (offset < 0L) {
            throw CriteriaUtils.limitParamError(this.criteriaContext, offset);
        }
        if (rowCount < 0L) {
            throw CriteriaUtils.limitParamError(this.criteriaContext, rowCount);
        }
        this.offset = offset;
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.limitPair(this.criteriaContext, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return (LR) this;
    }

    @Override
    public final LR limit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.limitPair(this.criteriaContext, function.apply(offsetKey)
                , function.apply(rowCountKey), this::limit);
        return (LR) this;
    }

    @Override
    public final LR limit(Consumer<BiConsumer<Long, Long>> consumer) {
        consumer.accept(this::limit);
        if (this.offset < 0L || this.rowCount < 0L) {
            throw CriteriaUtils.limitBiConsumerError(this.criteriaContext);
        }
        return (LR) this;
    }

    @Override
    public final LR limit(BiConsumer<C, BiConsumer<Long, Long>> consumer) {
        consumer.accept(this.criteria, this::limit);
        if (this.offset < 0L || this.rowCount < 0L) {
            throw CriteriaUtils.limitBiConsumerError(this.criteriaContext);
        }
        return (LR) this;
    }


    @Override
    public final LR ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.ifLimitPair(this.criteriaContext, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.ifLimitPair(this.criteriaContext, function.apply(offsetKey)
                , function.apply(rowCountKey), this::limit);
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Consumer<BiConsumer<Long, Long>> consumer) {
        consumer.accept(this::limit);
        if ((this.offset < 0L) ^ (this.rowCount < 0L)) {
            throw CriteriaUtils.ifLimitBiConsumerError(this.criteriaContext);
        }
        return (LR) this;
    }


    @Override
    public final LR ifLimit(BiConsumer<C, BiConsumer<Long, Long>> consumer) {
        consumer.accept(this.criteria, this::limit);
        if ((this.offset < 0L) ^ (this.rowCount < 0L)) {
            throw CriteriaUtils.ifLimitBiConsumerError(this.criteriaContext);
        }
        return (LR) this;
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
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
        this.prepared = Boolean.FALSE;
        this.orderByList = null;
        this.offset = -1;
        this.rowCount = -1;
        this.internalClear();
    }


    final boolean hasOrderBy() {
        return !_CollectionUtils.isEmpty(this.orderByList);
    }


    void onOrderBy() {

    }


    abstract Q internalAsRowSet(boolean fromAsQueryMethod);

    abstract UR createBracketQuery(RowSet rowSet);

    abstract UR getNoActionUnionRowSet(RowSet rowSet);

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
        this.prepared = Boolean.TRUE;
        return query;
    }

    /**
     * @see #bracket()
     */
    private RowSet simpleBracket(final Q thisQuery) {
        final RowSet._RowSetSpec<? extends RowSet> rowSetSpec;
        rowSetSpec = (RowSet._RowSetSpec<?>) createBracketQuery(thisQuery);
        return rowSetSpec.asQuery();
    }

    private UR ifUnion(final UnionType unionType, final @Nullable RowSet right) {
        final UR u;
        if (right == null) {
            u = this.getNoActionUnionRowSet(this.asQuery());
        } else if (right.isPrepared()) {
            u = this.createUnionRowSet(this.asQuery(), unionType, right);
        } else {
            throw new CriteriaException("Right RowSet non-prepared.");
        }
        return u;
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
