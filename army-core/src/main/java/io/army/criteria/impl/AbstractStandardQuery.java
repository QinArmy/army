package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class AbstractStandardQuery<Q extends Query, C> extends AbstractQuery<Q, C> implements
        Query.SelectPartSpec<Q, C>, Query.FromSpec<Q, C>, Query.TableJoinSpec<Q, C>
        , Query.WhereAndSpec<Q, C>, Query.HavingSpec<Q, C>, _StandardQuery {


    private List<SQLModifier> modifierList;

    private List<SelectPart> selectPartList;

    private List<TableBlock> tableBlockList = new ArrayList<>();

    private List<_Predicate> predicateList = new ArrayList<>();

    private List<SortPart> groupByList;

    private List<_Predicate> havingList;

    private List<SortPart> orderByList;

    private long offset = -1;

    private long rowCount = -1;


    private LockMode lockMode;

    private boolean prepared;

    private TableOnSpec<Q, C> noActionBlock;

    private HavingSpec<Q, C> noActionHavingSpec;

    AbstractStandardQuery(C criteria) {
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
            throw selectListClauseEmpty();
        }
        this.modifierList = Collections.singletonList(distinct);
        this.selectPartList = new ArrayList<>(selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(List<S> selectPartList) {
        if (selectPartList.size() == 0) {
            throw selectListClauseEmpty();
        }
        this.selectPartList = new ArrayList<>(selectPartList);
        return this;
    }

    /*################################## blow FromSpec method ##################################*/

    @Override
    public final TableJoinSpec<Q, C> from(TableMeta<?> table, String tableAlias) {
        addTableBlock(new FromTableBlock(table, tableAlias));
        return this;
    }

    @Override
    public final JoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        final SubQuery subQuery;
        subQuery = function.apply(this.criteria);
        assert subQuery != null;
        addTableBlock(new FromTableBlock(subQuery, subQueryAlia));
        return this;
    }

    @Override
    public final JoinSpec<Q, C> from(Supplier<SubQuery> supplier, String subQueryAlia) {
        final SubQuery subQuery;
        subQuery = supplier.get();
        assert subQuery != null;
        addTableBlock(new FromTableBlock(subQuery, subQueryAlia));
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

    public final LimitSpec<Q, C> orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return this;
    }

    public final LimitSpec<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = Arrays.asList(sortPart1, sortPart2);
        return this;
    }

    public final LimitSpec<Q, C> orderBy(final List<SortPart> sortPartList) {
        if (sortPartList.size() == 0) {
            throw new CriteriaException("order by clause is empty.");
        }
        this.orderByList = new ArrayList<>(sortPartList);
        return this;
    }

    public final LimitSpec<Q, C> orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    public final LimitSpec<Q, C> orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    public final LimitSpec<Q, C> ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return this;
    }


    public final LimitSpec<Q, C> ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> list;
        list = supplier.get();
        if (!CollectionUtils.isEmpty(list)) {
            this.orderBy(list);
        }
        return this;
    }

    public final LimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.orderBy(list);
        }
        return this;
    }

    public final LockSpec<Q, C> limit(long rowCount) {
        this.offset = -1;
        this.rowCount = rowCount;
        return this;
    }

    public final LockSpec<Q, C> limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }


    public final LockSpec<Q, C> limit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return this;
    }

    public final LockSpec<Q, C> limit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        this.offset = option.offset();
        this.rowCount = option.rowCount();
        return this;
    }

    public final LockSpec<Q, C> ifLimit(Supplier<LimitOption> supplier) {
        final LimitOption option;
        option = supplier.get();
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
        }
        return this;
    }

    public final LockSpec<Q, C> ifLimit(Function<C, LimitOption> function) {
        final LimitOption option;
        option = function.apply(this.criteria);
        if (option != null) {
            this.offset = option.offset();
            this.rowCount = option.rowCount();
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

    @Override
    public final UnionSpec<Q, C> bracketsQuery() {
        return ComposeQueries.brackets(this.criteria, asQuery());
    }

    @Override
    public final UnionSpec<Q, C> union(Function<C, Q> function) {
        return ComposeQueries.compose(this.criteria, asQuery(), UnionType.UNION, function);
    }

    @Override
    public final UnionSpec<Q, C> unionAll(Function<C, Q> function) {
        return ComposeQueries.compose(this.criteria, asQuery(), UnionType.UNION_ALL, function);
    }

    @Override
    public final UnionSpec<Q, C> unionDistinct(Function<C, Q> function) {
        return ComposeQueries.compose(this.criteria, asQuery(), UnionType.UNION_DISTINCT, function);
    }

    @Override
    public final UnionSpec<Q, C> union(final Supplier<Q> supplier) {
        Q thisQuery, otherQuery;
        thisQuery = this.asQuery();
        if (thisQuery.requiredBrackets()) {
            thisQuery = ComposeQueries.brackets(this.criteria, thisQuery);
        }
        otherQuery = supplier.get();
        assert otherQuery != null;
        if (otherQuery.requiredBrackets()) {

        }
        return this;
    }

    @Override
    public final SelectPartSpec<Q, C> union() {
        return null;
    }

    @Override
    public final SelectPartSpec<Q, C> unionAll() {
        return null;
    }

    @Override
    public final SelectPartSpec<Q, C> unionDistinct() {
        return null;
    }

    @Override
    public final UnionSpec<Q, C> unionAll(Supplier<Q> function) {
        return null;
    }

    @Override
    public final UnionSpec<Q, C> unionDistinct(Supplier<Q> function) {
        return null;
    }

    /*################################## blow package method ##################################*/

    @Override
    public final Q asQuery() {
        return (Q) this;
    }


    @Nullable
    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }

    @Override
    final boolean hasLockClause() {
        return this.lockMode != null;
    }

    @Override
    void internalClear() {
        this.lockMode = null;
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


    private StandardOnBlock<Q, C> addTableBlock(final StandardOnBlock<Q, C> block) {
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


        private final AbstractStandardQuery<Q, C> query;


        private StandardOnBlock(TablePart tablePart, String alias, JoinType joinType
                , AbstractStandardQuery<Q, C> query) {
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
                    throw onClauseIsEmpty();
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
        public TableJoinSpec<Q, C> onPrimary() {
            if (!(this.tablePart instanceof TableMeta)) {
                throw new IllegalStateException("You couldn't cast select api instance");
            }
            final TableBlock lastBlock;
            lastBlock = this.query.beforeBlock(this);
            if (!(lastBlock.tablePart instanceof TableMeta)) {
                throw new IllegalStateException("You couldn't cast select api instance");
            }
            final _Predicate predicate;
            predicate = (_Predicate) (((TableMeta<?>) lastBlock.tablePart).id()
                    .equal(((TableMeta<?>) this.tablePart).id()));

            this.predicates = Collections.singletonList(predicate);
            return this.query;
        }


    }


    private static final class NoActionTableBlock<Q extends Query, C> implements TableOnSpec<Q, C> {

        private final AbstractStandardQuery<Q, C> query;

        private NoActionTableBlock(AbstractStandardQuery<Q, C> query) {
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
        public TableJoinSpec<Q, C> onPrimary() {
            return this.query;
        }

    }


}
