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
import java.util.function.*;

@SuppressWarnings("unchecked")
abstract class PartRowSet<C, Q extends RowSet, FT, FS, FP, FJ, JT, JS, JP, UR, OR, LR, SP>
        extends JoinableClause<C, FT, FS, FP, FJ, JT, JS, JP>
        implements _PartRowSet, Statement._OrderByClause<C, OR>, Query._LimitClause<C, LR>
        , Query._QueryUnionClause<C, UR, SP>, RowSet._RowSetSpec<Q>, _SelfDescribed {


    private List<ArmySortItem> orderByList;

    private long offset = -1L;

    private long rowCount = -1L;

    private Boolean prepared;


    PartRowSet(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    PartRowSet(CriteriaContext criteriaContext, ClauseCreator<FP, JT, JS, JP> clauseCreator) {
        super(criteriaContext, clauseCreator);
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
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addOrderByItem);
        return this.endOrderBy(false);
    }

    @Override
    public final OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::addOrderByItem);
        return this.endOrderBy(false);
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Supplier<?> operand) {
        final Object value;
        if ((value = operand.get()) != null) {
            this.orderBy(operator.apply(value));
        }
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Function<String, ?> operand, String operandKey) {
        final Object value;
        if ((value = operand.apply(operandKey)) != null) {
            this.orderBy(operator.apply(value));
        }
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Supplier<?> firstOperand, Supplier<?> secondOperand) {
        final Object firstValue, secondValue;
        if ((firstValue = firstOperand.get()) != null && (secondValue = secondOperand.get()) != null) {
            this.orderBy(operator.apply(firstValue, secondValue));
        }
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Function<String, ?> operand, String firstKey, String secondKey) {
        final Object firstValue, secondValue;
        if ((firstValue = operand.apply(firstKey)) != null && (secondValue = operand.apply(secondKey)) != null) {
            this.orderBy(operator.apply(firstValue, secondValue));
        }
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addOrderByItem);
        return this.endOrderBy(true);
    }

    @Override
    public final OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::addOrderByItem);
        return this.endOrderBy(true);
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
        final List<ArmySortItem> itemList = this.orderByList;
        return itemList != null && itemList.size() > 0;
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

    private void addOrderByItem(final @Nullable SortItem sortItem) {
        if (sortItem == null) {
            throw CriteriaContextStack.nullPointer(this.criteriaContext);
        }
        List<ArmySortItem> itemList = this.orderByList;
        if (itemList == null) {
            itemList = new ArrayList<>();
            this.orderByList = itemList;
        } else if (!(itemList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        itemList.add((ArmySortItem) sortItem);
    }

    /**
     * @see #orderBy(Consumer)
     * @see #orderBy(BiConsumer)
     */
    private OR endOrderBy(final boolean optional) {
        final List<ArmySortItem> itemList = this.orderByList;
        if (itemList == null) {
            if (!optional) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "order by clause is empty.");
            }
            this.orderByList = Collections.emptyList();
        } else if (itemList instanceof ArrayList) {
            this.orderByList = _CollectionUtils.unmodifiableList(itemList);
        } else {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        if (this instanceof OrderByEventListener) {
            ((OrderByEventListener) this).orderByEvent();
        }
        return (OR) this;
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

    interface OrderByEventListener {

        void orderByEvent();

    }


}
