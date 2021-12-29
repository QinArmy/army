package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


abstract class BaseQuery<C, Q extends Query, U, SP, O, L, F, FS>
        extends PartQuery<C, Q, U, SP, O, L> {

    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList;

    private List<TableBlock> tableBlockList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<SortPart> groupByList;

    private List<_Predicate> havingList;


    private TableOnSpec<Q, C> noActionBlock;

    public BaseQuery(C criteria) {
        super(criteria);
    }


    public final <S extends SelectPart> SP select(List<SQLModifier> modifiers, Function<C, List<S>> function) {
        return this.select(modifiers, function.apply(this.criteria));
    }


    public final <S extends SelectPart> SP select(List<SQLModifier> modifiers, Supplier<List<S>> supplier) {
        return this.select(modifiers, supplier.get());
    }


    public final <S extends SelectPart> SP select(Function<C, List<S>> function) {
        return this.select(function.apply(this.criteria));
    }


    public final <S extends SelectPart> SP select(Supplier<List<S>> supplier) {
        return this.select(supplier.get());
    }


    public final SP select(SelectPart selectPart) {
        if (this instanceof ColumnSubQuery && !(selectPart instanceof Selection)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.selectPartList = Collections.singletonList(selectPart);
        return (SP) this;
    }


    public final SP select(SelectPart selectPart1, SelectPart selectPart2) {
        if (this instanceof ColumnSubQuery) {
            throw _Exceptions.castCriteriaApi();
        }
        this.selectPartList = Arrays.asList(selectPart1, selectPart2);
        return (SP) this;
    }


    public final SP select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3) {
        if (this instanceof ColumnSubQuery) {
            throw _Exceptions.castCriteriaApi();
        }
        this.selectPartList = Arrays.asList(selectPart1, selectPart2, selectPart3);
        return (SP) this;
    }


    public final <S extends SelectPart> SP select(List<SQLModifier> modifiers, List<S> selectPartList) {
        if (this instanceof ColumnSubQuery) {
            throw _Exceptions.castCriteriaApi();
        }
        if (selectPartList.size() == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        this.modifierList = new ArrayList<>(modifiers);
        this.selectPartList = new ArrayList<>(selectPartList);
        return (SP) this;
    }


    public final <S extends SelectPart> SP select(List<S> selectPartList) {
        if (this instanceof ColumnSubQuery) {
            throw _Exceptions.castCriteriaApi();
        }
        if (selectPartList.size() == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        this.selectPartList = new ArrayList<>(selectPartList);
        return (SP) this;
    }

    /*################################## blow FromSpec method ##################################*/


    public final F from(TableMeta<?> table, String tableAlias) {
        addTableBlock(new TableBlock.FromTableBlock(table, tableAlias));
        return (F) this;
    }


    public final FS from(Function<C, SubQuery> function, String subQueryAlia) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        assert subQuery != null;
        addTableBlock(new TableBlock.FromTableBlock(subQuery, subQueryAlia));
        return (FS) this;
    }


    public final FS from(Supplier<SubQuery> supplier, String subQueryAlia) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        addTableBlock(new TableBlock.FromTableBlock(subQuery, subQueryAlia));
        return (FS) this;
    }

    /*################################## blow JoinSpec method ##################################*/

    public final TableOnSpec<Q, C> leftJoin(TableMeta<?> table, String tableAlias) {
        return addTableBlock(new StandardQuery.StandardOnBlock<>(table, tableAlias, JoinType.LEFT_JOIN, this));
    }

    public final OnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return subQueryJoin(function, subQueryAlia, JoinType.LEFT_JOIN);
    }

    public final OnSpec<Q, C> leftJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return subQueryJoin(supplier, subQueryAlia, JoinType.LEFT_JOIN);
    }

    public final TableOnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return ifTableJoin(predicate, table, tableAlias, JoinType.LEFT_JOIN);
    }

    public final OnSpec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return ifSubQueryJoin(function, subQueryAlia, JoinType.LEFT_JOIN);
    }

    public final OnSpec<Q, C> ifLeftJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return ifSubQueryJoin(supplier, subQueryAlia, JoinType.LEFT_JOIN);
    }

    public final TableOnSpec<Q, C> join(TableMeta<?> table, String tableAlias) {
        return addTableBlock(new StandardQuery.StandardOnBlock<>(table, tableAlias, JoinType.JOIN, this));
    }

    public final OnSpec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia) {
        return subQueryJoin(function, subQueryAlia, JoinType.JOIN);
    }

    public final OnSpec<Q, C> join(Supplier<SubQuery> supplier, String subQueryAlia) {
        return subQueryJoin(supplier, subQueryAlia, JoinType.JOIN);
    }

    public final TableOnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return ifTableJoin(predicate, table, tableAlias, JoinType.JOIN);
    }

    public final OnSpec<Q, C> ifJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return ifSubQueryJoin(supplier, subQueryAlia, JoinType.JOIN);
    }

    public final OnSpec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return ifSubQueryJoin(function, subQueryAlia, JoinType.JOIN);
    }

    public final TableOnSpec<Q, C> rightJoin(TableMeta<?> table, String tableAlias) {
        return addTableBlock(new StandardQuery.StandardOnBlock<>(table, tableAlias, JoinType.RIGHT_JOIN, this));
    }

    public final OnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return subQueryJoin(function, subQueryAlia, JoinType.RIGHT_JOIN);
    }


    public final OnSpec<Q, C> rightJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return subQueryJoin(supplier, subQueryAlia, JoinType.RIGHT_JOIN);
    }

    public final TableOnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return ifTableJoin(predicate, table, tableAlias, JoinType.RIGHT_JOIN);
    }

    public final OnSpec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return ifSubQueryJoin(function, subQueryAlia, JoinType.RIGHT_JOIN);
    }

    public final OnSpec<Q, C> ifRightJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return ifSubQueryJoin(supplier, subQueryAlia, JoinType.RIGHT_JOIN);
    }


    public final WhereSpec<Q, C> where(final List<IPredicate> predicateList) {
        final List<_Predicate> list = new ArrayList<>(predicateList.size());
        for (IPredicate predicate : predicateList) {
            list.add((_Predicate) predicate);
        }
        this.predicateList = list;
        return this;
    }

    public final WhereSpec<Q, C> where(Function<C, List<IPredicate>> function) {
        return this.where(function.apply(this.criteria));
    }


    public final WhereSpec<Q, C> where(Supplier<List<IPredicate>> supplier) {
        return this.where(supplier.get());
    }


    public final WhereAndSpec<Q, C> where(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    public final WhereAndSpec<Q, C> and(IPredicate predicate) {
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    public final WhereAndSpec<Q, C> and(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        assert predicate != null;
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    public final WhereAndSpec<Q, C> and(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        assert predicate != null;
        this.predicateList.add((_Predicate) predicate);
        return this;
    }

    public final WhereAndSpec<Q, C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    public final WhereAndSpec<Q, C> ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    public final WhereAndSpec<Q, C> ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((_Predicate) predicate);
        }
        return this;
    }

    public final HavingSpec<Q, C> groupBy(SortPart sortPart) {
        this.groupByList = Collections.singletonList(sortPart);
        return this;
    }

    public final HavingSpec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2) {
        this.groupByList = Arrays.asList(sortPart1, sortPart2);
        return this;
    }

    public final HavingSpec<Q, C> groupBy(List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("group by clause is empty.");
        }
        this.groupByList = new ArrayList<>(sortPartList);
        return this;
    }

    public final HavingSpec<Q, C> groupBy(Function<C, List<SortPart>> function) {
        return this.groupBy(function.apply(this.criteria));
    }

    public final HavingSpec<Q, C> groupBy(Supplier<List<SortPart>> supplier) {
        return this.groupBy(supplier.get());
    }

    public final HavingSpec<Q, C> ifGroupBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.groupByList = Collections.singletonList(sortPart);
        }
        return this;
    }

    public final HavingSpec<Q, C> ifGroupBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> list;
        list = supplier.get();
        if (!CollectionUtils.isEmpty(list)) {
            this.groupByList = new ArrayList<>(list);
        }
        return this;
    }

    public final HavingSpec<Q, C> ifGroupBy(Function<C, List<SortPart>> function) {
        final List<SortPart> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.groupByList = new ArrayList<>(list);
        }
        return this;
    }


    public final HavingSpec<Q, C> having(final List<IPredicate> predicateList) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<_Predicate> list = new ArrayList<>(predicateList.size());
            for (IPredicate predicate : predicateList) {
                list.add((_Predicate) predicate);
            }
            if (list.size() == 0) {
                throw new CriteriaException("having clause is empty.");
            }
            this.havingList = list;
        }
        return this;
    }


    public final OrderBySpec<Q, C> having(IPredicate predicate) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((_Predicate) predicate);
        }
        return this;
    }

    public final OrderBySpec<Q, C> having(IPredicate predicate1, IPredicate predicate2) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<_Predicate> list = new ArrayList<>(2);
            list.add((_Predicate) predicate1);
            list.add((_Predicate) predicate2);
            this.havingList = list;
        }
        return this;
    }

    public final OrderBySpec<Q, C> having(Supplier<List<IPredicate>> supplier) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            this.having(supplier.get());
        }
        return this;
    }

    public final OrderBySpec<Q, C> ifHaving(@Nullable IPredicate predicate) {
        if (predicate != null && !CollectionUtils.isEmpty(this.groupByList)) {
            this.havingList = Collections.singletonList((_Predicate) predicate);
        }
        return this;
    }

    public final OrderBySpec<Q, C> ifHaving(Supplier<List<IPredicate>> supplier) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list = supplier.get();
            if (!CollectionUtils.isEmpty(list)) {
                this.having(list);
            }
        }
        return this;
    }

    public final OrderBySpec<Q, C> having(Function<C, List<IPredicate>> function) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.having(list);
            }
        }
        return this;
    }

    public final OrderBySpec<Q, C> ifHaving(Function<C, List<IPredicate>> function) {
        if (!CollectionUtils.isEmpty(this.groupByList)) {
            final List<IPredicate> list;
            list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.having(list);
            }
        }
        return this;
    }

    public final LockSpec<Q, C> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    public final LockSpec<Q, C> lock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        assert lockMode != null;
        this.lockMode = lockMode;
        return this;
    }

    public final LockSpec<Q, C> ifLock(@Nullable LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    public final LockSpec<Q, C> ifLock(Supplier<LockMode> supplier) {
        this.lockMode = supplier.get();
        return this;
    }

    public final LockSpec<Q, C> ifLock(Function<C, LockMode> function) {
        this.lockMode = function.apply(this.criteria);
        return this;
    }


    /*################################## blow package method ##################################*/



    /*################################## blow _Query method ##################################*/


    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }


    public final List<? extends SelectPart> selectPartList() {
        prepared();
        return this.selectPartList;
    }


    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }


    public final List<_Predicate> predicateList() {
        prepared();
        return this.predicateList;
    }


    public final List<SortPart> groupPartList() {
        return this.groupByList;
    }


    public final List<_Predicate> havingList() {
        return this.havingList;
    }

    @Nullable

    public final LockMode lockMode() {
        return this.lockMode;
    }


    protected final void internalClear() {

    }


    protected final Q internalAsQuery() {

        return this.onAsQuery();
    }

    abstract Q onAsQuery();


    abstract void onAddTable(TableMeta<?> table, String tableAlias);

    abstract void onAddSubQuery(SubQuery subQuery, String subQueryAlias);

    void doCheckTableAble(TableBlock block) {
        throw new IllegalArgumentException(String.format("tableAble[%s] isn't TableMeta or SubQuery."
                , block.alias()));
    }


    private TableOnSpec<Q, C> getNoActionBlock() {
        TableOnSpec<Q, C> onSpec = this.noActionBlock;
        if (onSpec == null) {
            onSpec = new StandardQuery.NoActionTableBlock<>(this);
            this.noActionBlock = onSpec;
        }
        return onSpec;
    }


    private TableOnSpec<Q, C> ifTableJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias, JoinType joinType) {
        final TableOnSpec<Q, C> onSpec;
        if (predicate.test(this.criteria)) {
            onSpec = addTableBlock(new StandardQuery.StandardOnBlock<>(table, tableAlias, joinType, this));
        } else {
            onSpec = getNoActionBlock();
        }
        return onSpec;
    }

    private OnSpec<Q, C> subQueryJoin(Function<C, SubQuery> function, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        assert subQuery != null;
        return addTableBlock(new StandardQuery.StandardOnBlock<>(subQuery, alias, joinType, this));
    }

    private OnSpec<Q, C> subQueryJoin(Supplier<SubQuery> supplier, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        return addTableBlock(new StandardQuery.StandardOnBlock<>(subQuery, alias, joinType, this));
    }

    private OnSpec<Q, C> ifSubQueryJoin(Function<C, SubQuery> function, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        final OnSpec<Q, C> onSpec;
        if (subQuery == null) {
            onSpec = getNoActionBlock();
        } else {
            onSpec = new StandardQuery.StandardOnBlock<>(subQuery, alias, joinType, this);
        }
        return onSpec;
    }

    private OnSpec<Q, C> ifSubQueryJoin(Supplier<SubQuery> supplier, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        final OnSpec<Q, C> onSpec;
        if (subQuery == null) {
            onSpec = getNoActionBlock();
        } else {
            onSpec = new StandardQuery.StandardOnBlock<>(subQuery, alias, joinType, this);
        }
        return onSpec;
    }


    private <T extends TableBlock> T addTableBlock(final T block) {
        this.tableBlockList.add(block);
        if (block.tablePart instanceof TableMeta) {
            onAddTable((TableMeta<?>) block.tablePart, block.alias);
        } else if (block.tablePart instanceof SubQuery) {
            onAddSubQuery((SubQuery) block.tablePart, block.alias);
        } else {
            doCheckTableAble(block);
        }
        return block;
    }


}
