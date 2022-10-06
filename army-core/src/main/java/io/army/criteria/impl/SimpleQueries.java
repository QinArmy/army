package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;


/**
 * <p>
 * This class is base class of all simple SELECT query.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleQueries<C, Q extends Item, W extends Query.SelectModifier, SR, FT, FS, FC, JT, JS, JC, WR, WA, GR, HR, OR, SP>
        extends JoinableClause<C, FT, FS, FC, JT, JS, JC, WR, WA, OR>
        implements Query._DynamicHintModifierSelectClause<C, W, SR>
        , Query._FromModifierClause<C, FT, FS>, Query._FromModifierCteClause<FC>
        , Statement._QueryWhereClause<C, WR, WA>, Query._GroupClause<C, GR>
        , Query._HavingClause<C, HR>, Query._QuerySpec<Q>
        , TabularItem.DerivedTableSpec, Query._QueryUnionClause<SP>
        , Query._QueryIntersectClause<SP>, Query._QueryExceptClause<SP>
        , Query._QueryMinusClause<SP>, Query
        , Statement, _Query, _SelfDescribed {

    private List<Hint> hintList;

    private List<? extends Query.SelectModifier> modifierList;

    private List<SelectItem> selectItemList;

    private List<_TableBlock> tableBlockList;


    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;

    private Boolean prepared;


    SimpleQueries(CriteriaContext context) {
        super(context);
        if (context.getOuterContext() == null) {
            ContextStack.setContextStack(this.context);
        } else {
            ContextStack.push(this.context);
        }
    }


    @Override
    public final SR select(SelectItem selectItem) {
        this.context.onAddSelectItem(selectItem);
        return (SR) this;
    }

    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2) {
        this.context.onAddSelectItem(selectItem1)
                .onAddSelectItem(selectItem2);
        return (SR) this;
    }

    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3) {
        this.context.onAddSelectItem(selectItem1)
                .onAddSelectItem(selectItem2)
                .onAddSelectItem(selectItem3);
        return (SR) this;
    }

    @Override
    public final SR select(Consumer<Consumer<SelectItem>> consumer) {
        consumer.accept(this.context::onAddSelectItem);
        return (SR) this;
    }

    @Override
    public final SR select(BiConsumer<C, Consumer<SelectItem>> consumer) {
        consumer.accept(this.criteria, this.context::onAddSelectItem);
        return (SR) this;
    }

    @Override
    public final SR select(W modifier, Consumer<Consumer<SelectItem>> consumer) {
        this.modifierList = this.asModifierList(Collections.singletonList(modifier));
        consumer.accept(this.context::onAddSelectItem);
        return (SR) this;
    }

    @Override
    public final SR select(W modifier, BiConsumer<C, Consumer<SelectItem>> consumer) {
        this.modifierList = this.asModifierList(Collections.singletonList(modifier));
        consumer.accept(this.criteria, this.context::onAddSelectItem);
        return (SR) this;
    }

    @Override
    public final SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(this.context::onAddSelectItem);
        return (SR) this;
    }

    @Override
    public final SR select(Supplier<List<Hint>> hints, List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer) {
        this.hintList = this.asHintList(hints.get());
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(this.criteria, this.context::onAddSelectItem);
        return (SR) this;
    }

    /*################################## blow FromSpec method ##################################*/

    @Override
    public final FT from(TableMeta<?> table, String tableAlias) {
        return this.onAddNoOnTableItem(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> FS from(Supplier<T> supplier, String alias) {
        return this.onAddNoOnQueryItem(_JoinType.NONE, null, supplier.get(), alias);
    }

    @Override
    public final <T extends TabularItem> FS from(Function<C, T> function, String alias) {
        return this.onAddNoOnQueryItem(_JoinType.NONE, null, function.apply(this.criteria), alias);
    }

    @Override
    public final FT from(Query.TabularModifier modifier, TableMeta<?> table, String tableAlias) {
        return this.onAddNoOnTableItem(_JoinType.NONE, modifier, table, tableAlias);
    }

    @Override
    public final <T extends TabularItem> FS from(Query.TabularModifier modifier, Supplier<T> supplier, String alias) {
        return this.onAddNoOnQueryItem(_JoinType.NONE, modifier, supplier.get(), alias);
    }

    @Override
    public final <T extends TabularItem> FS from(Query.TabularModifier modifier, Function<C, T> function, String alias) {
        return this.onAddNoOnQueryItem(_JoinType.NONE, modifier, function.apply(this.criteria), alias);
    }

    @Override
    public final FC from(String cteName) {
        return this.onAddNoOnCteItem(_JoinType.NONE, null, cteName, "");
    }

    @Override
    public final FC from(String cteName, String alias) {
        return this.onAddNoOnCteItem(_JoinType.NONE, null, cteName, alias);
    }

    @Override
    public final FC from(Query.TabularModifier modifier, String cteName) {
        return this.onAddNoOnCteItem(_JoinType.NONE, modifier, cteName, "");
    }

    @Override
    public final FC from(Query.TabularModifier modifier, String cteName, String alias) {
        return this.onAddNoOnCteItem(_JoinType.NONE, modifier, cteName, alias);
    }

    @Override
    public final WR ifWhere(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        return (WR) this;
    }

    @Override
    public final WR ifWhere(BiConsumer<C, Consumer<IPredicate>> consumer) {
        consumer.accept(this.criteria, this::and);
        return (WR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem) {
        this.groupByList = Collections.singletonList((ArmySortItem) sortItem);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2) {
        this.groupByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.groupByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        return (GR) this;
    }


    @Override
    public final GR groupBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(true);
    }

    @Override
    public final GR groupBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::addGroupByItem);
        return this.endGroupBy(true);
    }


    @Override
    public final GR ifGroupBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addGroupByItem);
        return this.endGroupBy(false);
    }

    @Override
    public final GR ifGroupBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::addGroupByItem);
        return this.endGroupBy(false);
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate) {
        if (this.groupByList != null) {
            if (predicate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate1, final @Nullable IPredicate predicate2) {
        if (this.groupByList != null) {
            if (predicate1 == null || predicate2 == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.havingList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate) predicate1
                    , (OperationPredicate) predicate2
            );
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<IPredicate> supplier) {
        if (this.groupByList != null) {
            this.having(supplier.get());
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<C, IPredicate> function) {
        if (this.groupByList != null) {
            this.having(function.apply(this.criteria));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<Object, IPredicate> operator, Supplier<?> operand) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<Object, IPredicate> operator, Function<String, ?> operand, String operandKey) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.apply(operandKey)));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand) {
        if (this.groupByList != null) {
            this.having(operator.apply(firstOperand.get(), secondOperand.get()));
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey) {
        if (this.groupByList != null) {
            this.having(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
        }
        return (HR) this;
    }

    @Override
    public final HR having(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(false);
        }
        return (HR) this;
    }

    @Override
    public final HR having(BiConsumer<C, Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this.criteria, this::addHavingPredicate);
            this.endHaving(false);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Consumer<Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this::addHavingPredicate);
            this.endHaving(true);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(BiConsumer<C, Consumer<IPredicate>> consumer) {
        if (this.groupByList != null) {
            consumer.accept(this.criteria, this::addHavingPredicate);
            this.endHaving(true);
        }
        return (HR) this;
    }

    @Override
    public final SP union() {
        return this.createQueryUnion(UnionType.UNION);
    }

    @Override
    public final SP unionAll() {
        return this.createQueryUnion(UnionType.UNION_ALL);
    }

    @Override
    public final SP unionDistinct() {
        return this.createQueryUnion(UnionType.UNION_DISTINCT);
    }

    @Override
    public final SP intersect() {
        return this.createQueryUnion(UnionType.INTERSECT);
    }


    @Override
    public final SP intersectAll() {
        return this.createQueryUnion(UnionType.INTERSECT_ALL);
    }

    @Override
    public final SP intersectDistinct() {
        return this.createQueryUnion(UnionType.INTERSECT_DISTINCT);
    }

    @Override
    public final SP except() {
        return this.createQueryUnion(UnionType.EXCEPT);
    }


    @Override
    public final SP exceptAll() {
        return this.createQueryUnion(UnionType.EXCEPT_ALL);
    }

    @Override
    public final SP exceptDistinct() {
        return this.createQueryUnion(UnionType.EXCEPT_DISTINCT);
    }

    @Override
    public final SP minus() {
        return this.createQueryUnion(UnionType.MINUS);
    }

    @Override
    public final SP minusAll() {
        return this.createQueryUnion(UnionType.MINUS_ALL);
    }

    @Override
    public final SP minusDistinct() {
        return this.createQueryUnion(UnionType.MINUS_DISTINCT);
    }

    /*################################## blow _Query method ##################################*/

    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<? extends SQLWords> modifierList() {
        final List<? extends SQLWords> list = this.modifierList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final int selectionSize() {
        return this.context.selectionSize();
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        final List<? extends SelectItem> list = this.selectItemList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_TableBlock> tableBlockList() {
        final List<_TableBlock> list = this.tableBlockList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final List<? extends SortItem> groupByList() {
        final List<? extends SortItem> list = this.groupByList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_Predicate> havingList() {
        final List<_Predicate> list = this.havingList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubQuery)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        context.parser().rowSet(this, context);
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public final Q asQuery() {
        this.endQueryStatement();
        return this.onAsQuery();
    }


    @Override
    public final void clear() {
        this.hintList = null;
        this.modifierList = null;
        this.selectItemList = null;
        this.tableBlockList = null;

        this.groupByList = null;
        this.havingList = null;
        this.onClear();
    }


    @Override
    public final Selection selection(final String derivedAlias) {
        if (!(this instanceof SubQuery)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.context.selection(derivedAlias);
    }


    abstract void onEndQuery();

    abstract Q onAsQuery();

    abstract void onClear();

    abstract List<W> asModifierList(@Nullable List<W> modifiers);

    abstract List<Hint> asHintList(@Nullable List<Hint> hints);


    private void endQueryStatement() {
        _Assert.nonPrepared(this.prepared);
        // hint list
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        // modifier list
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        final CriteriaContext context = this.context;
        // selection list
        this.selectItemList = context.endSelectClause();

        this.endWhereClause();

        // group by and having
        if (this.groupByList == null) {
            this.groupByList = Collections.emptyList();
            this.havingList = Collections.emptyList();
        } else if (this.havingList == null) {
            this.havingList = Collections.emptyList();
        }

        this.endOrderByClause();

        this.onEndQuery();

        this.tableBlockList = context.endContext();
        if (context.getOuterContext() == null) {
            ContextStack.clearContextStack(context);
        } else {
            ContextStack.pop(context);
        }
        this.prepared = Boolean.TRUE;

    }

    private void addGroupByItem(final @Nullable SortItem sortItem) {
        if (sortItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            itemList = new ArrayList<>();
            this.groupByList = itemList;
        } else if (!(itemList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        itemList.add((ArmySortItem) sortItem);
    }

    private GR endGroupBy(final boolean required) {
        final List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            if (required) {
                throw ContextStack.criteriaError(this.context, "group by clause is empty");
            }
            //null,no-op
        } else if (itemList instanceof ArrayList) {
            this.groupByList = _CollectionUtils.unmodifiableList(itemList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (GR) this;
    }

    private void addHavingPredicate(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.havingList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.havingList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        predicateList.add((OperationPredicate) predicate);
    }

    private void endHaving(final boolean optional) {
        final List<_Predicate> predicateList = this.havingList;
        if (this.groupByList == null) {
            this.havingList = Collections.emptyList();
        } else if (predicateList == null) {
            if (!optional) {
                throw ContextStack.criteriaError(this.context, "having clause is empty");
            }
            this.havingList = Collections.emptyList();
        } else if (predicateList instanceof ArrayList) {
            this.havingList = _CollectionUtils.unmodifiableList(predicateList);
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }

    abstract SP createQueryUnion(UnionType unionType);


    static abstract class SelectClauseDispatcher<C, W extends Query.SelectModifier, SR>
            implements Query._DynamicHintModifierSelectClause<C, W, SR> {

        @Override
        public final SR select(SelectItem selectItem) {
            return this.createSelectClause()
                    .select(selectItem);
        }

        @Override
        public final SR select(SelectItem selectItem1, SelectItem selectItem2) {
            return this.createSelectClause()
                    .select(selectItem1, selectItem2);
        }

        @Override
        public final SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3) {
            return this.createSelectClause()
                    .select(selectItem1, selectItem2, selectItem3);
        }

        @Override
        public final SR select(Consumer<Consumer<SelectItem>> consumer) {
            return this.createSelectClause()
                    .select(consumer);
        }

        @Override
        public final SR select(BiConsumer<C, Consumer<SelectItem>> consumer) {
            return this.createSelectClause()
                    .select(consumer);
        }

        @Override
        public final SR select(W modifier, Consumer<Consumer<SelectItem>> consumer) {
            return this.createSelectClause()
                    .select(modifier, consumer);
        }

        @Override
        public final SR select(W modifier, BiConsumer<C, Consumer<SelectItem>> consumer) {
            return this.createSelectClause()
                    .select(modifier, consumer);
        }

        @Override
        public final SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer) {
            return this.createSelectClause()
                    .select(hints, modifiers, consumer);
        }

        @Override
        public final SR select(Supplier<List<Hint>> hints, List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer) {
            return this.createSelectClause()
                    .select(hints, modifiers, consumer);
        }

        abstract Query._DynamicHintModifierSelectClause<C, W, SR> createSelectClause();


    }//SelectClauseDispatcher

    static abstract class WithSelectClauseDispatcher<C, B extends CteBuilderSpec, WE, WS, W extends Query.SelectModifier, SR>
            extends SelectClauseDispatcher<C, W, SR>
            implements _DynamicWithCteClause<C, B, WE>
            , Query._StaticWithCteClause<WS> {

        @Override
        public final WE with(Consumer<B> consumer) {
            return this.createDynamicWithClause()
                    .with(consumer);
        }

        @Override
        public final WE with(BiConsumer<C, B> consumer) {
            return this.createDynamicWithClause()
                    .with(consumer);
        }

        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            return this.createDynamicWithClause()
                    .withRecursive(consumer);
        }

        @Override
        public final WE withRecursive(BiConsumer<C, B> consumer) {
            return this.createDynamicWithClause()
                    .withRecursive(consumer);
        }

        @Override
        public final WE ifWith(Consumer<B> consumer) {
            return this.createDynamicWithClause()
                    .ifWith(consumer);
        }

        @Override
        public final WE ifWith(BiConsumer<C, B> consumer) {
            return this.createDynamicWithClause()
                    .ifWith(consumer);
        }

        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            return this.createDynamicWithClause()
                    .ifWithRecursive(consumer);
        }

        @Override
        public final WE ifWithRecursive(BiConsumer<C, B> consumer) {
            return this.createDynamicWithClause()
                    .ifWithRecursive(consumer);
        }

        @Override
        public final WS with(String name) {
            return this.createStaticWithClause()
                    .with(name);
        }

        @Override
        public final WS withRecursive(String name) {
            return this.createStaticWithClause()
                    .withRecursive(name);
        }


        abstract _DynamicWithCteClause<C, B, WE> createDynamicWithClause();

        abstract Query._StaticWithCteClause<WS> createStaticWithClause();


    }//WithSelectClauseDispatcher


}