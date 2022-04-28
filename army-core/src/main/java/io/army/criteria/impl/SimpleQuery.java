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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


@SuppressWarnings("unchecked")
abstract class SimpleQuery<C, Q extends Query, SR, FT, FS, FP, JT, JS, JP, JE, WR, AR, GR, HR, OR, LR, UR, SP>
        extends PartRowSet<C, Q, UR, OR, LR, SP> implements StandardStatement.SelectClauseForStandard<C, SR>
        , DialectStatement.DialectJoinClause<C, JT, JS, JP, FT, FS, JE, FP>, Statement.WhereClause<C, WR, AR>
        , Statement.WhereAndClause<C, AR>, Query.GroupClause<C, GR>, Query.HavingClause<C, HR>, _Query
        , DialectStatement.DialectFromClause<C, FT, FS, FP, JE>, DialectStatement.DialectLeftBracketClause<C, FT, FS, FP>
        , Statement.RightBracketClause<FS>, DialectStatement.DialectSelectClause<C, SR>, Query.QuerySpec<Q> {


    private List<Hint> hintList;

    private List<? extends SQLModifier> modifierList;

    private List<? extends SelectItem> selectItemList;

    private List<_TableBlock> tableBlockList;

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<ArmySortItem> groupByList;

    private List<_Predicate> havingList;


    SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
        if (this instanceof NonPrimaryStatement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
    }

    /*################################## blow io.army.criteria.DialectStatement.DialectSelectClause method ##################################*/

    @Override
    public final SR select(SQLModifier modifier, SelectItem selectItem) {
        return this.innerSafeSelect(modifier, Collections.singletonList(selectItem));
    }

    @Override
    public final SR select(SQLModifier modifier, SelectItem selectItem1, SelectItem selectItem2) {
        return this.innerSafeSelect(modifier, ArrayUtils.asUnmodifiableList(selectItem1, selectItem2));
    }

    @Override
    public final SR select(SQLModifier modifier, Consumer<List<SelectItem>> consumer) {
        final List<SelectItem> selectItemList = new ArrayList<>();
        consumer.accept(selectItemList);
        return this.innerSafeSelect(modifier, selectItemList);
    }

    @Override
    public final <S extends SelectItem, M extends SQLModifier> SR select(Supplier<List<Hint>> hints, List<M> modifiers, Function<C, List<S>> function) {
        return this.innerNonSafeSelect(hints.get(), modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem, M extends SQLModifier> SR select(Supplier<List<Hint>> hints, List<M> modifiers, Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(hints.get(), modifiers, supplier.get());
    }

    @Override
    public final <M extends SQLModifier> SR select(Supplier<List<Hint>> hints, List<M> modifiers, Consumer<List<SelectItem>> consumer) {
        List<SelectItem> selectItemList = new ArrayList<>();
        consumer.accept(selectItemList);

        this.hintList = _CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = _CollectionUtils.asUnmodifiableList(modifiers);
        selectItemList = Collections.unmodifiableList(selectItemList);

        this.selectItemList = selectItemList;
        this.criteriaContext.selectList(selectItemList); //notify context
        return (SR) this;
    }


    @Override
    public final <S extends SelectItem, M extends SQLModifier> SR select(List<M> modifiers, Function<C, List<S>> function) {
        return this.innerNonSafeSelect(null, modifiers, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem, M extends SQLModifier> SR select(List<M> modifiers, Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(null, modifiers, supplier.get());
    }

    @Override
    public final <M extends SQLModifier> SR select(List<M> modifiers, Consumer<List<SelectItem>> consumer) {
        return this.select(Collections::emptyList, modifiers, consumer);
    }

    /*################################## blow io.army.criteria.StandardStatement.SelectClauseForStandard method ##################################*/

    @Override
    public final SR select(@Nullable Distinct modifier, SelectItem selectItem) {
        return this.innerSafeSelect(modifier, Collections.singletonList(selectItem));
    }

    @Override
    public final SR select(@Nullable Distinct modifier, SelectItem selectItem1, SelectItem selectItem2) {
        return this.innerSafeSelect(modifier, ArrayUtils.asUnmodifiableList(selectItem1, selectItem2));
    }

    @Override
    public final <S extends SelectItem> SR select(@Nullable Distinct modifier, Function<C, List<S>> function) {
        return this.innerNonSafeSelect(modifier, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectItem> SR select(@Nullable Distinct modifier, Supplier<List<S>> supplier) {
        return this.innerNonSafeSelect(modifier, supplier.get());
    }

    @Override
    public final SR select(@Nullable Distinct modifier, Consumer<List<SelectItem>> consumer) {
        final List<SelectItem> selectItemList = new ArrayList<>();
        consumer.accept(selectItemList);
        return this.innerSafeSelect(modifier, Collections.unmodifiableList(selectItemList));
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



    /*################################## blow FromSpec method ##################################*/

    @Override
    public final JE from() {
        this.criteriaContext.onJoinType(_JoinType.NONE);
        return (JE) this;
    }

    @Override
    public final FP from(TableMeta<?> table) {
        return this.createNextClauseWithoutOnClause(_JoinType.NONE, table);
    }

    @Override
    public final FT from(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.createTableBlockWithoutOnClause(_JoinType.NONE, table, tableAlias);
        this.criteriaContext.onBlockWithoutOnClause(block);
        return (FT) this;
    }

    @Override
    public final <T extends TableItem> FS from(Function<C, T> function, String alias) {
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(function.apply(this.criteria), alias));
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS from(Supplier<T> supplier, String alias) {
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(supplier.get(), alias));
        return (FS) this;
    }


    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.LEFT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JP leftJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.LEFT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS leftJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.LEFT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE leftJoin() {
        this.criteriaContext.onJoinType(_JoinType.LEFT_JOIN);
        return (JE) this;
    }

    @Override
    public final JE join() {
        this.criteriaContext.onJoinType(_JoinType.JOIN);
        return (JE) this;
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }


    @Override
    public final JP join(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.JOIN, table);
    }

    @Override
    public final <T extends TableItem> JS join(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS join(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE rightJoin() {
        this.criteriaContext.onJoinType(_JoinType.RIGHT_JOIN);
        return (JE) this;
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.RIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JP rightJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS rightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.RIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE crossJoin() {
        this.criteriaContext.onJoinType(_JoinType.CROSS_JOIN);
        return (JE) this;
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        block = this.createTableBlockWithoutOnClause(_JoinType.CROSS_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock(block);
        return (FT) this;
    }

    @Override
    public final FP crossJoin(TableMeta<?> table) {
        return this.createNextClauseWithoutOnClause(_JoinType.CROSS_JOIN, table);
    }

    @Override
    public final <T extends TableItem> FS crossJoin(Function<C, T> function, String alias) {
        this.criteriaContext.onAddBlock(TableBlock.crossBlock(function.apply(this.criteria), alias));
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS crossJoin(Supplier<T> supplier, String alias) {
        this.criteriaContext.onAddBlock(TableBlock.crossBlock(supplier.get(), alias));
        return (FS) this;
    }

    @Override
    public final JE fullJoin() {
        this.criteriaContext.onJoinType(_JoinType.FULL_JOIN);
        return (JE) this;
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = createTableBlock(_JoinType.FULL_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JP fullJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS fullJoin(Function<C, T> function, String alias) {
        final JS block;
        block = createOnBlock(_JoinType.FULL_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JE straightJoin() {
        this.criteriaContext.onJoinType(_JoinType.STRAIGHT_JOIN);
        return (JE) this;
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JP straightJoin(TableMeta<?> table) {
        return this.createNextClauseWithOnClause(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Function<C, T> function, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias) {
        final JS block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final DialectStatement.DialectLeftBracketClause<C, FT, FS, FP> leftBracket() {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        return this;
    }

    @Override
    public final FP leftBracket(TableMeta<?> table) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        return (FP) this;
    }

    @Override
    public final FT leftBracket(final TableMeta<?> table, final String tableAlias) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        this.criteriaContext.onBlockWithoutOnClause(this.createTableBlockWithoutOnClause(_JoinType.NONE, table, tableAlias));
        return (FT) this;
    }

    @Override
    public final <T extends TableItem> FS leftBracket(Function<C, T> function, String alias) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(function.apply(this.criteria), alias));
        return (FS) this;
    }

    @Override
    public final <T extends TableItem> FS leftBracket(Supplier<T> supplier, String alias) {
        this.criteriaContext.onBracketBlock(CriteriaUtils.leftBracketBlock());
        this.criteriaContext.onBlockWithoutOnClause(TableBlock.noneBlock(supplier.get(), alias));
        return (FS) this;
    }

    @Override
    public final FS rightBracket() {
        this.criteriaContext.onBracketBlock(CriteriaUtils.rightBracketBlock());
        return (FS) this;
    }

    @Override
    public final WR where(final List<IPredicate> predicateList) {
        final List<_Predicate> list = this.predicateList;
        for (IPredicate predicate : predicateList) {
            list.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (WR) this;
    }

    @Override
    public final WR where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }

    @Override
    public final WR where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public final WR where(Consumer<List<IPredicate>> consumer) {
        final List<IPredicate> list = new ArrayList<>();
        consumer.accept(list);
        return this.where(list);
    }

    @Override
    public final AR where(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final AR and(IPredicate predicate) {
        this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR and(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        assert predicate != null;
        this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR and(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        assert predicate != null;
        this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        return (AR) this;
    }

    @Override
    public final AR ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
        }
        return (AR) this;
    }

    @Override
    public final AR ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);// must cast to OperationPredicate
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
        this.groupByList = ArrayUtils.asUnmodifiableList((ArmySortItem) sortItem1, (ArmySortItem) sortItem2);
        return (GR) this;
    }

    @Override
    public final GR groupBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.groupByList = ArrayUtils.asUnmodifiableList((ArmySortItem) sortItem1, (ArmySortItem) sortItem2, (ArmySortItem) sortItem3);
        return (GR) this;
    }

    @Override
    public final GR groupBy(List<SortItem> sortItemList) {
        final int size = sortItemList.size();
        switch (size) {
            case 0:
                throw new CriteriaException("sortItemList must be not empty.");
            case 1:
                this.groupByList = Collections.singletonList((ArmySortItem) sortItemList.get(0));
                break;
            default: {
                final List<ArmySortItem> tempList = new ArrayList<>(size);
                for (SortItem sortItem : sortItemList) {
                    tempList.add((ArmySortItem) sortItem);
                }
                this.groupByList = Collections.unmodifiableList(tempList);
            }
        }
        return (GR) this;
    }

    @Override
    public final GR groupBy(Function<C, List<SortItem>> function) {
        return this.groupBy(function.apply(this.criteria));
    }

    @Override
    public final GR groupBy(Supplier<List<SortItem>> supplier) {
        return this.groupBy(supplier.get());
    }

    @Override
    public final GR groupBy(Consumer<List<SortItem>> consumer) {
        final List<SortItem> list = new ArrayList<>();
        consumer.accept(list);
        return this.groupBy(list);
    }

    @Override
    public final GR ifGroupBy(@Nullable SortItem sortItem) {
        if (sortItem != null) {
            this.groupByList = Collections.singletonList((ArmySortItem) sortItem);
        }
        return (GR) this;
    }

    @Override
    public final GR ifGroupBy(Supplier<List<SortItem>> supplier) {
        final List<SortItem> list;
        list = supplier.get();
        if (!_CollectionUtils.isEmpty(list)) {
            this.groupBy(list);
        }
        return (GR) this;
    }

    @Override
    public final GR ifGroupBy(Function<C, List<SortItem>> function) {
        final List<SortItem> list;
        list = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(list)) {
            this.groupBy(list);
        }
        return (GR) this;
    }

    @Override
    public final HR having(final List<IPredicate> predicateList) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            final int size = predicateList.size();
            switch (size) {
                case 0:
                    throw new CriteriaException("having predicate list must not empty.");
                case 1:
                    this.predicateList = Collections.singletonList((OperationPredicate) predicateList.get(0));
                    break;
                default: {
                    final List<_Predicate> list = new ArrayList<>(size);
                    for (IPredicate predicate : predicateList) {
                        list.add((OperationPredicate) predicate);
                    }
                    this.havingList = Collections.unmodifiableList(list);
                }
            }
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR having(IPredicate predicate1, IPredicate predicate2) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = ArrayUtils.asUnmodifiableList((OperationPredicate) predicate1
                    , (OperationPredicate) predicate2);
        }
        return (HR) this;
    }

    @Override
    public final HR having(Supplier<List<IPredicate>> supplier) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            this.having(supplier.get());
        }
        return (HR) this;
    }

    @Override
    public final HR having(Function<C, List<IPredicate>> function) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            this.having(function.apply(this.criteria));
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(@Nullable IPredicate predicate) {
        if (predicate != null && !_CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((OperationPredicate) predicate);
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Supplier<List<IPredicate>> supplier) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list;
            list = supplier.get();
            if (!_CollectionUtils.isEmpty(list)) {
                this.having(list);
            }
        }
        return (HR) this;
    }

    @Override
    public final HR ifHaving(Function<C, List<IPredicate>> function) {
        if (!_CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list;
            list = function.apply(this.criteria);
            if (!_CollectionUtils.isEmpty(list)) {
                this.having(list);
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
    public final List<? extends SQLModifier> modifierList() {
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
        prepared();
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


    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubQuery)) {
            throw _Exceptions.castCriteriaApi();
        }
        context.dialect().rowSet(this, context);
    }


    @Override
    protected final Q internalAsRowSet(final boolean fromAsQueryMethod) {
        this.tableBlockList = this.criteriaContext.clear();
        if (this instanceof NonPrimaryStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }

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
        if (_CollectionUtils.isEmpty(selectPartList)) {
            throw _Exceptions.selectListIsEmpty();
        }
        if (this instanceof ScalarSubQuery
                && (selectPartList.size() != 1 || !(selectPartList.get(0) instanceof Selection))) {
            throw _Exceptions.ScalarSubQuerySelectionError();
        }

        // group by and having
        final List<? extends SortItem> groupByList = this.groupByList;
        if (_CollectionUtils.isEmpty(groupByList)) {
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
            final Query.QuerySpec<Q> spec;
            spec = (Query.QuerySpec<Q>) createUnionRowSet(union.leftRowSet(), union.unionType(), thisQuery);
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

    abstract _TableBlock createTableBlockWithoutOnClause(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JT createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias);

    abstract JS createOnBlock(_JoinType joinType, TableItem tableItem, String alias);

    abstract FP createNextClauseWithoutOnClause(_JoinType joinType, TableMeta<?> table);


    JP createNextClauseWithOnClause(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }


    private <S extends SelectItem> SR innerNonSafeSelect(@Nullable List<Hint> hintList
            , @Nullable List<? extends SQLModifier> modifierList, List<S> selectItemList) {
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

    private <S extends SelectItem> SR innerNonSafeSelect(@Nullable SQLModifier modifier, List<S> selectItemList) {
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
    private <S extends SelectItem> SR innerSafeSelect(@Nullable SQLModifier modifier, List<S> selectItemList) {
        if (modifier != null) {
            this.modifierList = Collections.singletonList(modifier);
        }
        this.selectItemList = selectItemList;
        this.criteriaContext.selectList(selectItemList); //notify context
        return (SR) this;
    }


    static IllegalStateException asQueryMethodError() {
        return new IllegalStateException("onAsQuery(boolean) error");
    }


}
