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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of all simple SELECT query.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleQuery<C, Q extends Query, W extends SQLWords, SR, FT, FS, FP, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        extends PartRowSet<C, Q, FT, FS, FP, JT, JS, JP, UR, OR, LR, SP>
        implements Statement._QueryWhereClause<C, WR, AR>, Statement._WhereAndClause<C, AR>, Query._GroupClause<C, GR>
        , Query._HavingClause<C, HR>, _Query, Statement._FromClause<C, FT, FS>, DialectStatement._DialectFromClause<FP>
        , DialectStatement._DialectSelectClause<C, W, SR>, DialectStatement._FromCteClause<FS>, Query._QuerySpec<Q>
        , JoinableClause.ClauseSupplier {

    private List<Hint> hintList;

    private List<? extends SQLWords> modifierList;

    private List<? extends SelectItem> selectItemList;

    private List<_TableBlock> tableBlockList;

    private List<_Predicate> predicateList;

    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;


    SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
        if (this instanceof SubStatement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }

    /*################################## blow io.army.criteria.DialectStatement.DialectSelectClause method ##################################*/

    @Override
    public final <S extends SelectItem> SR select(Supplier<List<Hint>> hints, List<W> modifiers, Function<C, List<S>> function) {
        return this.innerNonSafeSelect(hints.get(), modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(Supplier<List<Hint>> hints, List<W> modifiers, Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(hints.get(), modifiers, supplier.get());
    }

    @Override
    public final <S extends SelectItem> SR select(List<W> modifiers, Function<C, List<S>> function) {
        return this.innerNonSafeSelect(null, modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(List<W> modifiers, Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(null, modifiers, supplier.get());
    }


    /*################################## blow io.army.criteria.Query.SelectClause method ##################################*/

    @Override
    public final SR select(SelectItem selectItem) {
        return this.innerSafeSelect(null, Collections.singletonList(selectItem));
    }

    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2) {
        return this.innerSafeSelect(null, ArrayUtils.asUnmodifiableList(selectItem1, selectItem2));
    }

    @Override
    public final SR select(SelectItem selectItem1, SelectItem selectItem2, SelectItem selectItem3) {
        return this.innerSafeSelect(null, ArrayUtils.asUnmodifiableList(selectItem1, selectItem2, selectItem3));
    }

    @Override
    public final <S extends SelectItem> SR select(Function<C, List<S>> function) {
        return this.innerNonSafeSelect(null, null, function.apply(this.criteria));
    }

    @Override
    public final SR select(Consumer<List<SelectItem>> consumer) {
        List<SelectItem> list = new ArrayList<>();
        consumer.accept(list);
        return this.innerSafeSelect(null, Collections.unmodifiableList(list));
    }

    @Override
    public final <S extends SelectItem> SR select(Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(null, null, supplier.get());
    }

    @Override
    public final SR select(@Nullable W modifier, SelectItem selectItem) {
        return this.innerSafeSelect(modifier, Collections.singletonList(selectItem));
    }

    @Override
    public final SR select(@Nullable W modifier, SelectItem selectItem1, SelectItem selectItem2) {
        return this.innerSafeSelect(modifier, ArrayUtils.asUnmodifiableList(selectItem1, selectItem2));
    }

    @Override
    public final SR select(@Nullable W modifier, Consumer<List<SelectItem>> consumer) {
        final List<SelectItem> selectItemList = new ArrayList<>();
        consumer.accept(selectItemList);
        return this.innerSafeSelect(modifier, selectItemList);
    }

    @Override
    public final <S extends SelectItem> SR select(@Nullable W modifier, Function<C, List<S>> function) {
        return this.innerNonSafeSelect(modifier, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(@Nullable W modifier, Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(modifier, supplier.get());
    }

    /*################################## blow FromSpec method ##################################*/
    @Override
    public final FP from(TableMeta<?> table) {
        return (FP) this.clauseSupplier.createClause(_JoinType.NONE, table);
    }

    @Override
    public final FT from(TableMeta<?> table, String tableAlias) {
        this.clauseSupplier.createAndAddBlock(_JoinType.NONE, table, tableAlias);
        return (FT) this;
    }

    @Override
    public final FS from(String cteName) {
        this.clauseSupplier.createAndAddBlock(_JoinType.NONE, this.criteriaContext.refCte(cteName), "");
        return (FS) this;
    }

    @Override
    public final FS from(String cteName, String alias) {
        this.clauseSupplier.createAndAddBlock(_JoinType.NONE, this.criteriaContext.refCte(cteName), alias);
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS from(Supplier<T> supplier, String alias) {
        this.clauseSupplier.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
        return (FS) this;
    }


    @Override
    public final <T extends TableItem> FS from(Function<C, T> function, String alias) {
        this.clauseSupplier.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
        return (FS) this;
    }

    @Override
    public final WR where(Supplier<List<IPredicate>> supplier) {
        this.predicateList = CriteriaUtils.asPredicateList(supplier.get(), _Exceptions::predicateListIsEmpty);
        return (WR) this;
    }

    @Override
    public final WR where(Function<C, List<IPredicate>> function) {
        this.predicateList = CriteriaUtils.asPredicateList(function.apply(this.criteria)
                , _Exceptions::predicateListIsEmpty);
        return (WR) this;
    }

    @Override
    public final WR where(Consumer<List<IPredicate>> consumer) {
        final List<IPredicate> predicateList = new ArrayList<>();
        consumer.accept(predicateList);
        this.predicateList = CriteriaUtils.asPredicateList(predicateList, _Exceptions::predicateListIsEmpty);
        return (WR) this;
    }

    @Override
    public final AR where(IPredicate predicate) {
        return this.and(predicate);
    }

    @Override
    public final AR where(Function<Object, IPredicate> operator, DataField operand) {
        return this.and(operator.apply(operand));
    }

    @Override
    public final AR where(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final AR where(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final AR where(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final AR where(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final AR whereIf(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier);
    }

    @Override
    public final AR whereIf(Function<C, IPredicate> function) {
        return this.ifAnd(function);
    }

    @Override
    public final AR whereIf(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.ifAnd(operator, operand);
    }

    @Override
    public final AR whereIf(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.ifAnd(operator, operand, keyName);
    }

    @Override
    public final AR whereIf(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.ifAnd(operator, firstOperand, secondOperand);
    }

    @Override
    public final AR whereIf(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.ifAnd(operator, operand, firstKey, secondKey);
    }

    @Override
    public final WR ifWhere(Supplier<List<IPredicate>> supplier) {
        final List<IPredicate> predicateList;
        predicateList = supplier.get();
        if (predicateList != null && predicateList.size() > 0) {
            this.predicateList = CriteriaUtils.asPredicateList(predicateList, null);
        }
        return (WR) this;
    }

    @Override
    public final WR ifWhere(Function<C, List<IPredicate>> function) {
        final List<IPredicate> predicateList;
        predicateList = function.apply(this.criteria);
        if (predicateList != null && predicateList.size() > 0) {
            this.predicateList = CriteriaUtils.asPredicateList(predicateList, null);
        }
        return (WR) this;
    }


    @Override
    public final AR and(IPredicate predicate) {
        Objects.requireNonNull(predicate);
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        }
        predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final AR and(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }

    @Override
    public final AR and(Function<Object, IPredicate> operator, DataField operand) {
        return this.and(operator.apply(operand));
    }

    @Override
    public final AR and(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final AR and(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final AR and(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final AR and(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final AR ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.and(predicate);
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.and(predicate);
        }
        return (AR) this;
    }


    @Override
    public final AR ifAnd(Function<Object, IPredicate> operator, Supplier<?> operand) {
        final Object paramOrExp;
        paramOrExp = operand.get();
        if (paramOrExp != null) {
            this.and(operator.apply(paramOrExp));
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        final Object paramOrExp;
        paramOrExp = operand.apply(keyName);
        if (paramOrExp != null) {
            this.and(operator.apply(paramOrExp));
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        final Object first, second;
        if ((first = firstOperand.get()) != null && (second = secondOperand.get()) != null) {
            this.and(operator.apply(first, second));
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        final Object first, second;
        if ((first = operand.apply(firstKey)) != null && (second = operand.apply(secondKey)) != null) {
            this.and(operator.apply(first, second));
        }
        return (AR) this;
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
    public final <S extends SortItem> GR groupBy(Supplier<List<S>> supplier) {
        this.groupByList = CriteriaUtils.asSortItemList(supplier.get());
        return (GR) this;
    }

    @Override
    public final <S extends SortItem> GR groupBy(Function<C, List<S>> function) {
        this.groupByList = CriteriaUtils.asSortItemList(function.apply(this.criteria));
        return (GR) this;
    }

    @Override
    public final GR groupBy(Consumer<List<SortItem>> consumer) {
        final List<SortItem> sortItemList = new ArrayList<>();
        consumer.accept(sortItemList);
        this.groupByList = CriteriaUtils.asSortItemList(sortItemList);
        return (GR) this;
    }

    @Override
    public final <S extends SortItem> GR ifGroupBy(Supplier<List<S>> supplier) {
        final List<S> sortItemList;
        sortItemList = supplier.get();
        if (sortItemList != null && sortItemList.size() > 0) {
            this.groupByList = CriteriaUtils.asSortItemList(sortItemList);
        }
        return (GR) this;
    }

    @Override
    public final <S extends SortItem> GR ifGroupBy(Function<C, List<S>> function) {
        final List<S> sortItemList;
        sortItemList = function.apply(this.criteria);
        if (sortItemList != null && sortItemList.size() > 0) {
            this.groupByList = CriteriaUtils.asSortItemList(sortItemList);
        }
        return (GR) this;
    }

    @Override
    public final HR having(IPredicate predicate) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate1, IPredicate predicate2) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            this.havingList = ArrayUtils.asUnmodifiableList((OperationPredicate) predicate1
                    , (OperationPredicate) predicate2);
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<List<IPredicate>> supplier) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            this.havingList = CriteriaUtils.asPredicateList(supplier.get(), _Exceptions::havingIsEmpty);
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<C, List<IPredicate>> function) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            this.havingList = CriteriaUtils.asPredicateList(function.apply(this.criteria), _Exceptions::havingIsEmpty);
        }
        return (HR) this;
    }

    @Override
    public final HR having(Consumer<List<IPredicate>> consumer) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            final List<IPredicate> havingList = new ArrayList<>();
            consumer.accept(havingList);
            this.havingList = CriteriaUtils.asPredicateList(havingList, _Exceptions::havingIsEmpty);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(@Nullable IPredicate predicate) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0 && predicate != null) {
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Supplier<List<IPredicate>> supplier) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            final List<IPredicate> list;
            list = supplier.get();
            if (list != null && list.size() > 0) {
                this.havingList = CriteriaUtils.asPredicateList(list, null);
            }
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Function<C, List<IPredicate>> function) {
        final List<ArmySortItem> groupByList = this.groupByList;
        if (groupByList != null && groupByList.size() > 0) {
            final List<IPredicate> list;
            list = function.apply(this.criteria);
            if (list != null && list.size() > 0) {
                this.havingList = CriteriaUtils.asPredicateList(list, null);
            }
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
    public final List<? extends SelectItem> selectItemList() {
        final List<? extends SelectItem> selectItemList = this.selectItemList;
        assert selectItemList != null;
        return selectItemList;
    }

    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        return this.tableBlockList;
    }

    @Override
    public final List<_Predicate> predicateList() {
        prepared();
        return this.predicateList;
    }

    @Override
    public final List<? extends SortItem> groupPartList() {
        return this.groupByList;
    }

    @Override
    public final List<_Predicate> havingList() {
        return this.havingList;
    }

    public final Selection selection() {
        if (!(this instanceof ScalarSubQuery)) {
            throw _Exceptions.castCriteriaApi();
        }
        return (Selection) this.selectItemList().get(0);
    }

    public final ParamMeta paramMeta() {
        return this.selection().paramMeta();
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubQuery)) {
            throw _Exceptions.castCriteriaApi();
        }
        context.dialect().rowSet(this, context);
    }


    @Override
    protected final Q internalAsRowSet(final boolean fromAsQueryMethod) {
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        this.tableBlockList = this.criteriaContext.clear(this instanceof SubQuery);

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
        final List<? extends SortItem> groupByList = this.groupByList;
        if (groupByList == null || groupByList.size() == 0) {
            this.groupByList = Collections.emptyList();
            this.hintList = Collections.emptyList();
        } else if (_CollectionUtils.isEmpty(this.havingList)) {
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

    final boolean hasGroupBy() {
        final List<ArmySortItem> groupByList = this.groupByList;
        return groupByList != null && groupByList.size() > 0;
    }


    abstract Q onAsQuery(boolean fromAsQueryMethod);

    abstract void onClear();


    private <S extends SelectItem> SR innerNonSafeSelect(@Nullable List<Hint> hintList
            , @Nullable List<? extends SQLWords> modifierList, List<S> selectItemList) {
        if (hintList != null) {
            this.hintList = _CollectionUtils.asUnmodifiableList(hintList);
        }
        if (modifierList != null) {
            this.modifierList = _CollectionUtils.asUnmodifiableList(modifierList);
        }
        selectItemList = _CollectionUtils.asUnmodifiableList(selectItemList);
        this.selectItemList = selectItemList;
        this.criteriaContext.selectList(selectItemList); //notify context
        return (SR) this;
    }

    private <S extends SelectItem> SR innerNonSafeSelect(@Nullable SQLWords modifier, List<S> selectItemList) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        this.selectItemList = _CollectionUtils.asUnmodifiableList(selectItemList);
        this.criteriaContext.selectList(selectItemList); //notify context
        return (SR) this;
    }


    /**
     * @param selectItemList a unmodified list
     */
    private <S extends SelectItem> SR innerSafeSelect(@Nullable SQLWords modifier, List<S> selectItemList) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        this.selectItemList = selectItemList;
        this.criteriaContext.selectList(selectItemList); //notify context
        return (SR) this;
    }


}
