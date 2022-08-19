package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

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
abstract class SimpleQuery<C, Q extends Query, W extends SQLWords, SR, FT, FS, FP, FJ, JT, JS, JP, WR, WA, GR, HR, OR, LR, UR, SP>
        extends PartRowSet<C, Q, FT, FS, FP, FJ, JT, JS, JP, UR, OR, LR, SP>
        implements Statement._QueryWhereClause<C, WR, WA>, Statement._WhereAndClause<C, WA>, Query._GroupClause<C, GR>
        , Query._HavingClause<C, HR>, _Query, Statement._FromClause<C, FT, FS>, DialectStatement._DialectFromClause<FP>
        , DialectStatement._DialectSelectClause<C, W, SR>, DialectStatement._FromCteClause<FS>, Query._QuerySpec<Q>
        , JoinableClause.ClauseCreator<FP, JT, JS, JP> {

    private List<Hint> hintList;

    private List<? extends SQLWords> modifierList;

    private List<SelectItem> selectItemList;

    private int selectionSize;

    private List<_TableBlock> tableBlockList;

    private List<_Predicate> predicateList;

    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;


    SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
        if (this instanceof SubStatement) {
            CriteriaContextStack.push(this.context);
        } else {
            CriteriaContextStack.setContextStack(this.context);
        }
    }

    @Override
    public final SR select(SelectItem selectItem) {
        return this.singleSelectItem(selectItem);
    }

    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2) {
        this.selectItemList = new ArrayList<>(2);
        this.addSelectItem(selectItem1);
        this.addSelectItem(selectItem2);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3) {
        this.selectItemList = new ArrayList<>(3);
        this.addSelectItem(selectItem1);
        this.addSelectItem(selectItem2);
        this.addSelectItem(selectItem3);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(Consumer<Consumer<SelectItem>> consumer) {
        consumer.accept(this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(BiConsumer<C, Consumer<SelectItem>> consumer) {
        consumer.accept(this.criteria, this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(@Nullable W modifier, SelectItem selectItem) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        return this.singleSelectItem(selectItem);
    }

    @Override
    public final SR select(@Nullable W modifier, SelectItem selectItem1, SelectItem selectItem2) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        this.selectItemList = new ArrayList<>(2);
        this.addSelectItem(selectItem1);
        this.addSelectItem(selectItem2);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(@Nullable W modifier, Consumer<Consumer<SelectItem>> consumer) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        consumer.accept(this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(@Nullable W modifier, BiConsumer<C, Consumer<SelectItem>> consumer) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        consumer.accept(this.criteria, this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer) {
        final List<Hint> hintList;
        hintList = hints.get();
        if (hintList != null) {
            this.hintList = this.asHintList(hintList);
        }
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(Supplier<List<Hint>> hints, List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer) {
        final List<Hint> hintList;
        hintList = hints.get();
        if (hintList != null) {
            this.hintList = this.asHintList(hintList);
        }
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(this.criteria, this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(List<W> modifiers, Consumer<Consumer<SelectItem>> consumer) {
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(this::addSelectItem);
        return this.addSelectItemEnd();
    }

    @Override
    public final SR select(List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer) {
        this.modifierList = this.asModifierList(modifiers);
        consumer.accept(this.criteria, this::addSelectItem);
        return this.addSelectItemEnd();
    }

    /*################################## blow FromSpec method ##################################*/
    @Override
    public final FP from(TableMeta<?> table) {
        return this.createNoOnTableClause(_JoinType.NONE, null, table);
    }

    @Override
    public final FT from(TableMeta<?> table, String tableAlias) {
        this.context.onAddBlock(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
        return (FT) this;
    }

    @Override
    public final FS from(String cteName) {
        final _TableBlock block;
        block = this.createNoOnItemBlock(_JoinType.NONE, null, this.context.refCte(cteName), "");
        this.context.onAddBlock(block);
        return (FS) this;
    }

    @Override
    public final FS from(String cteName, String alias) {
        final _TableBlock block;
        block = this.createNoOnItemBlock(_JoinType.NONE, null, this.context.refCte(cteName), alias);
        this.context.onAddBlock(block);
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS from(Supplier<T> supplier, String alias) {
        this.context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias));
        return (FS) this;
    }


    @Override
    public final <T extends TableItem> FS from(Function<C, T> function, String alias) {
        final _TableBlock block;
        block = this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria), alias);
        this.context.onAddBlock(block);
        return (FS) this;
    }


    @Override
    public final WR where(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        if (this.predicateList == null) {
            throw CriteriaContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
        }
        return (WR) this;
    }

    @Override
    public final WR where(BiConsumer<C, Consumer<IPredicate>> consumer) {
        consumer.accept(this.criteria, this::and);
        if (this.predicateList == null) {
            throw CriteriaContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
        }
        return (WR) this;
    }

    @Override
    public final WA where(IPredicate predicate) {
        return this.and(predicate);
    }

    @Override
    public final WA where(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA where(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, DataField operand) {
        return this.and(operator.apply(operand));
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final WA where(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final WA where(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final WA whereIfNonNull(@Nullable Function<Object, IPredicate> operator, @Nullable Object operand) {
        return this.ifNonNullAnd(operator, operand);
    }

    @Override
    public final WA whereIf(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier);
    }

    @Override
    public final WA whereIf(Function<C, IPredicate> function) {
        return this.ifAnd(function);
    }

    @Override
    public final WA whereIf(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.ifAnd(operator, operand);
    }

    @Override
    public final WA whereIf(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.ifAnd(operator, operand, keyName);
    }

    @Override
    public final WA whereIf(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.ifAnd(operator, firstOperand, secondOperand);
    }

    @Override
    public final WA whereIf(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.ifAnd(operator, operand, firstKey, secondKey);
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
    public final WA and(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (WA) this;
    }

    @Override
    public final WA and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA and(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, DataField operand) {
        return this.and(operator.apply(operand));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final WA and(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final WA and(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final WA ifNonNullAnd(@Nullable Function<Object, IPredicate> operator, @Nullable Object operand) {
        if (operator != null && operand != null) {
            this.and(operator.apply(operand));
        }
        return (WA) this;
    }

    @Override
    public final WA ifNonNullAnd(Function<Object, ? extends Expression> firstOperator, @Nullable Object firstOperand
            , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand) {
        if (firstOperand != null) {
            this.and(secondOperator.apply(firstOperator.apply(firstOperand), secondOperand));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.and(predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.and(predicate);
        }
        return (WA) this;
    }


    @Override
    public final WA ifAnd(Function<Object, IPredicate> operator, Supplier<?> operand) {
        final Object paramOrExp;
        paramOrExp = operand.get();
        if (paramOrExp != null) {
            this.and(operator.apply(paramOrExp));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        final Object paramOrExp;
        paramOrExp = operand.apply(keyName);
        if (paramOrExp != null) {
            this.and(operator.apply(paramOrExp));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        final Object first, second;
        if ((first = firstOperand.get()) != null && (second = secondOperand.get()) != null) {
            this.and(operator.apply(first, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        final Object first, second;
        if ((first = operand.apply(firstKey)) != null && (second = operand.apply(secondKey)) != null) {
            this.and(operator.apply(first, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<Object, ? extends Expression> firstOperator, Supplier<?> firstOperand
            , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand) {
        final Object firstValue;
        firstValue = firstOperand.get();
        if (firstValue != null) {
            final Expression expression;
            expression = firstOperator.apply(firstValue);
            assert expression != null;
            this.and(secondOperator.apply(expression, secondOperand));
        }
        return (WA) this;
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
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(final @Nullable IPredicate predicate1, final @Nullable IPredicate predicate2) {
        if (this.groupByList != null) {
            if (predicate1 == null || predicate2 == null) {
                throw CriteriaContextStack.nullPointer(this.context);
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
    public HR ifHaving(Consumer<Consumer<IPredicate>> consumer) {
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

    /*################################## blow _Query method ##################################*/

    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<? extends SQLWords> modifierList() {
        return this.modifierList;
    }


    @Override
    public final int selectionSize() {
        return this.selectionSize;
    }

    @Override
    public final List<? extends SelectItem> selectItemList() {
        final List<? extends SelectItem> selectItemList = this.selectItemList;
        assert selectItemList != null;
        return selectItemList;
    }

    @Override
    public final List<_TableBlock> tableBlockList() {
        return this.tableBlockList;
    }

    @Override
    public final List<_Predicate> predicateList() {
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null || predicateList instanceof ArrayList) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return predicateList;
    }

    @Override
    public final List<? extends SortItem> groupByList() {
        return this.groupByList;
    }

    @Override
    public final List<_Predicate> havingList() {
        return this.havingList;
    }

    public final Selection selection() {
        if (!(this instanceof ScalarSubQuery)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return (Selection) this.selectItemList().get(0);
    }

    public final ParamMeta paramMeta() {
        return this.selection().paramMeta();
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubQuery)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        context.parser().rowSet(this, context);
    }


    @Override
    protected final Q internalAsRowSet(final boolean fromAsQueryMethod) {
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.context);
        } else {
            CriteriaContextStack.clearContextStack(this.context);
        }
        this.tableBlockList = this.context.clear();

        // hint list
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        // modifier list
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        // selection list
        final List<? extends SelectItem> selectPartList = this.selectItemList;
        if (selectPartList == null || selectPartList.size() == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        if (this instanceof ScalarSubQuery
                && (selectPartList.size() != 1 || !(selectPartList.get(0) instanceof Selection))) {
            throw _Exceptions.ScalarSubQuerySelectionError();
        }

        if (this.predicateList == null) {
            this.predicateList = Collections.emptyList();
        }

        // group by and having
        if (this.groupByList == null) {
            this.groupByList = Collections.emptyList();
            this.havingList = Collections.emptyList();
        } else if (this.havingList == null) {
            this.havingList = Collections.emptyList();
        }
        return this.onAsQuery(fromAsQueryMethod);
    }

    @SuppressWarnings("unchecked")
    final Q finallyAsQuery(final boolean fromAsQueryMethod) {
        final Q thisQuery, resultQuery;
        if (this instanceof ScalarSubQuery) {
            thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery) this);
        } else {
            thisQuery = (Q) this;
        }
        if (fromAsQueryMethod && this instanceof UnionAndRowSet) {
            final UnionAndRowSet union = (UnionAndRowSet) this;
            final Query._QuerySpec<Q> spec;
            spec = (Query._QuerySpec<Q>) createUnionRowSet(union.leftRowSet(), union.unionType(), thisQuery);
            resultQuery = spec.asQuery();
        } else {
            resultQuery = thisQuery;
        }
        return resultQuery;
    }


    @Override
    final void internalClear() {
        this.hintList = null;
        this.modifierList = null;
        this.selectItemList = null;
        this.tableBlockList = null;

        this.predicateList = null;
        this.groupByList = null;
        this.havingList = null;
        this.onClear();
    }

    @Override
    public FP createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        throw CriteriaContextStack.castCriteriaApi(this.context);
    }

    @Override
    public JP createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        throw CriteriaContextStack.castCriteriaApi(this.context);
    }

    final boolean hasGroupBy() {
        final List<ArmySortItem> groupByList = this.groupByList;
        return groupByList != null && groupByList.size() > 0;
    }


    abstract Q onAsQuery(boolean fromAsQueryMethod);

    abstract void onClear();

    abstract List<W> asModifierList(@Nullable List<W> modifiers);

    abstract List<Hint> asHintList(@Nullable List<Hint> hints);


    private void addSelectItem(final SelectItem selectItem) {
        if (selectItem instanceof DerivedGroup) {
            this.context.onAddDerivedGroup((DerivedGroup) selectItem);
        }
        List<SelectItem> selectItemList = this.selectItemList;
        if (selectItemList == null) {
            selectItemList = new ArrayList<>();
            this.selectItemList = selectItemList;
            this.selectionSize = 0;
        }
        selectItemList.add(selectItem);
        if (selectItem instanceof Selection) {
            this.selectionSize++;
        } else if (selectItem instanceof SelectionGroup) {
            this.selectionSize += ((SelectionGroup) selectItem).selectionList().size();
        } else {
            throw _Exceptions.unknownSelectItem(selectItem);
        }
    }

    private SR singleSelectItem(final SelectItem selectItem) {
        if (selectItem instanceof DerivedGroup) {
            this.context.onAddDerivedGroup((DerivedGroup) selectItem);
        }
        final List<SelectItem> selectItemList;
        selectItemList = Collections.singletonList(selectItem);
        this.selectItemList = selectItemList;
        this.context.selectList(selectItemList);
        this.selectionSize = 1;
        return (SR) this;
    }

    private SR addSelectItemEnd() {
        List<SelectItem> selectItemList = this.selectItemList;
        if (selectItemList == null) {
            throw _Exceptions.selectListIsEmpty();
        }
        selectItemList = _CollectionUtils.unmodifiableList(selectItemList);
        this.selectItemList = selectItemList;
        this.context.selectList(selectItemList);
        return (SR) this;
    }


    private void addGroupByItem(final @Nullable SortItem sortItem) {
        if (sortItem == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            itemList = new ArrayList<>();
            this.groupByList = itemList;
        } else if (!(itemList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        itemList.add((ArmySortItem) sortItem);
    }

    private GR endGroupBy(final boolean required) {
        final List<ArmySortItem> itemList = this.groupByList;
        if (itemList == null) {
            if (required) {
                throw CriteriaContextStack.criteriaError(this.context, "group by clause is empty");
            }
            //null,no-op
        } else if (itemList instanceof ArrayList) {
            this.groupByList = _CollectionUtils.unmodifiableList(itemList);
        } else {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return (GR) this;
    }

    private void addHavingPredicate(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.havingList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.havingList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }

        predicateList.add((OperationPredicate) predicate);
    }

    private void endHaving(final boolean optional) {
        final List<_Predicate> predicateList = this.havingList;
        if (this.groupByList == null) {
            this.havingList = Collections.emptyList();
        } else if (predicateList == null) {
            if (!optional) {
                throw CriteriaContextStack.criteriaError(this.context, "having clause is empty");
            }
            this.havingList = Collections.emptyList();
        } else if (predicateList instanceof ArrayList) {
            this.havingList = _CollectionUtils.unmodifiableList(predicateList);
        } else {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }

    }


}
