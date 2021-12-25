package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class StandardQuery<Q extends Query, C> extends StandardPartQuery<Q, C> implements
        Query.SelectPartSpec<Q, C>, Query.FromSpec<Q, C>, Query.TableJoinSpec<Q, C>
        , Query.WhereAndSpec<Q, C>, Query.HavingSpec<Q, C>, _Query {


    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList;

    private List<TableBlock> tableBlockList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<SortPart> groupByList;

    private List<_Predicate> havingList;

    private LockMode lockMode;

    private TableOnSpec<Q, C> noActionBlock;


    StandardQuery(@Nullable C criteria) {
        super(criteria);
    }

    /*################################## blow SelectPartSpec method ##################################*/

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, Function<C, List<S>> function) {
        return this.select(distinct, function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, Supplier<List<S>> supplier) {
        return this.select(distinct, supplier.get());
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Function<C, List<S>> function) {
        return this.select(function.apply(this.criteria));
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Supplier<List<S>> supplier) {
        return this.select(supplier.get());
    }

    @Override
    public final FromSpec<Q, C> select(Distinct distinct, SelectPart selectPart) {
        this.modifierList = Collections.singletonList(distinct);
        this.selectPartList = Collections.singletonList(selectPart);
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(SelectPart selectPart) {
        this.selectPartList = Collections.singletonList(selectPart);
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(SelectPart selectPart1, SelectPart selectPart2) {
        this.selectPartList = Arrays.asList(selectPart1, selectPart2);
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3) {
        this.selectPartList = Arrays.asList(selectPart1, selectPart2, selectPart3);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, List<S> selectPartList) {
        if (selectPartList.size() == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        this.modifierList = Collections.singletonList(distinct);
        this.selectPartList = new ArrayList<>(selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(List<S> selectPartList) {
        if (selectPartList.size() == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        this.selectPartList = new ArrayList<>(selectPartList);
        return this;
    }

    /*################################## blow FromSpec method ##################################*/

    @Override
    public final TableJoinSpec<Q, C> from(TableMeta<?> table, String tableAlias) {
        addTableBlock(new TableBlock.FromTableBlock(table, tableAlias));
        return this;
    }

    @Override
    public final JoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        assert subQuery != null;
        addTableBlock(new TableBlock.FromTableBlock(subQuery, subQueryAlia));
        return this;
    }

    @Override
    public final JoinSpec<Q, C> from(Supplier<SubQuery> supplier, String subQueryAlia) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        addTableBlock(new TableBlock.FromTableBlock(subQuery, subQueryAlia));
        return this;
    }

    /*################################## blow JoinSpec method ##################################*/

    public final TableOnSpec<Q, C> leftJoin(TableMeta<?> table, String tableAlias) {
        return addTableBlock(new StandardOnBlock<>(table, tableAlias, JoinType.LEFT_JOIN, this));
    }

    public final OnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return subQueryJoin(function, subQueryAlia, JoinType.LEFT_JOIN)
    }

    public final OnSpec<Q, C> leftJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return subQueryJoin(supplier, subQueryAlia, JoinType.LEFT_JOIN)
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
        return addTableBlock(new StandardOnBlock<>(table, tableAlias, JoinType.JOIN, this));
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
        return addTableBlock(new StandardOnBlock<>(table, tableAlias, JoinType.RIGHT_JOIN, this));
    }

    public final OnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return subQueryJoin(function, subQueryAlia, JoinType.RIGHT_JOIN);
    }

    @Override
    public final OnSpec<Q, C> rightJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return subQueryJoin(supplier, subQueryAlia, JoinType.RIGHT_JOIN)
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


    public final WhereAndSpec<Q, C> where(IPredicate predicate) {
        final List<_Predicate> list = new ArrayList<>();
        list.add((_Predicate) predicate);
        this.predicateList = list;
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

    @Override
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final List<? extends SelectPart> selectPartList() {
        prepared();
        return this.selectPartList;
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
    public final List<SortPart> groupPartList() {
        return this.groupByList;
    }

    @Override
    public final List<_Predicate> havingList() {
        return this.havingList;
    }

    @Nullable
    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }

    @Override
    protected final void internalClear() {

    }

    @Override
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
            onSpec = new NoActionTableBlock<>(this);
            this.noActionBlock = onSpec;
        }
        return onSpec;
    }


    private TableOnSpec<Q, C> ifTableJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias, JoinType joinType) {
        final TableOnSpec<Q, C> onSpec;
        if (predicate.test(this.criteria)) {
            onSpec = addTableBlock(new StandardOnBlock<>(table, tableAlias, joinType, this));
        } else {
            onSpec = getNoActionBlock();
        }
        return onSpec;
    }

    private OnSpec<Q, C> subQueryJoin(Function<C, SubQuery> function, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        assert subQuery != null;
        return addTableBlock(new StandardOnBlock<>(subQuery, alias, joinType, this));
    }

    private OnSpec<Q, C> subQueryJoin(Supplier<SubQuery> supplier, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        return addTableBlock(new StandardOnBlock<>(subQuery, alias, joinType, this));
    }

    private OnSpec<Q, C> ifSubQueryJoin(Function<C, SubQuery> function, String alias, JoinType joinType) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        final OnSpec<Q, C> onSpec;
        if (subQuery == null) {
            onSpec = getNoActionBlock();
        } else {
            onSpec = new StandardOnBlock<>(subQuery, alias, joinType, this);
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
            onSpec = new StandardOnBlock<>(subQuery, alias, joinType, this);
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

    /*################################## blow private static inner class  ##################################*/


    static final class StandardOnBlock<Q extends Query, C> extends TableBlock implements TableOnSpec<Q, C> {


        private final StandardQuery<Q, C> query;


        private StandardOnBlock(TablePart tablePart, String alias, JoinType joinType
                , StandardQuery<Q, C> query) {
            super(tablePart, alias, joinType);
            this.query = query;
        }

        @Override
        public TableJoinSpec<Q, C> on(final List<IPredicate> predicateList) {
            final List<_Predicate> predicates = new ArrayList<>(predicateList.size());
            for (IPredicate predicate : predicateList) {
                predicates.add((_Predicate) predicate);
            }
            switch (predicates.size()) {
                case 0:
                    throw _Exceptions.onClauseIsEmpty();
                case 1:
                    this.predicates = Collections.singletonList(predicates.get(0));
                    break;
                default:
                    this.predicates = Collections.unmodifiableList(predicates);
            }
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate) {
            this.predicates = Collections.singletonList((_Predicate) predicate);
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate1, IPredicate predicate2) {
            this.predicates = ArrayUtils.asUnmodifiableList((_Predicate) predicate1, (_Predicate) predicate2);
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(Function<C, List<IPredicate>> function) {
            return this.on(function.apply(this.query.criteria));
        }

        @Override
        public TableJoinSpec<Q, C> on(Supplier<List<IPredicate>> supplier) {
            return this.on(supplier.get());
        }

        @Override
        public List<_Predicate> predicates() {
            final List<_Predicate> list = this.predicates;
            assert list != null;
            return list;
        }

        @Override
        public TableJoinSpec<Q, C> onId() {
            if (!(this.tablePart instanceof TableMeta)) {
                throw _Exceptions.castCriteriaApi();
            }
            final List<TableBlock> tableBlockList = this.query.tableBlockList;
            final int size = tableBlockList.size();
            final TableBlock thisBlock = tableBlockList.get(size - 1);
            if (this != thisBlock) {
                throw _Exceptions.castCriteriaApi();
            }
            final TableBlock lastBlock;
            lastBlock = tableBlockList.get(size - 2);
            if (!(lastBlock.tablePart instanceof TableMeta)) {
                throw _Exceptions.castCriteriaApi();
            }
            final _Predicate predicate;
            predicate = (_Predicate) (((TableMeta<?>) lastBlock.tablePart).id()
                    .equal(((TableMeta<?>) this.tablePart).id()));

            this.predicates = Collections.singletonList(predicate);
            return this.query;
        }


    }


    private static final class NoActionTableBlock<Q extends Query, C> implements TableOnSpec<Q, C> {

        private final StandardQuery<Q, C> query;

        private NoActionTableBlock(StandardQuery<Q, C> query) {
            this.query = query;
        }

        @Override
        public TableJoinSpec<Q, C> on(List<IPredicate> predicateList) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(IPredicate predicate1, IPredicate predicate2) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(Function<C, List<IPredicate>> function) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> on(Supplier<List<IPredicate>> supplier) {
            return this.query;
        }

        @Override
        public TableJoinSpec<Q, C> onId() {
            return this.query;
        }

    }


}
