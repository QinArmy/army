package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractStandardQuery<Q extends Query, C> extends AbstractQuery<Q, C> implements
        Query.SelectPartSpec<Q, C>, Query.FromSpec<Q, C>, Query.TableRouteJoinSpec<Q, C>
        , Query.WhereAndSpec<Q, C>, Query.HavingSpec<Q, C>, _StandardQuery {

    final TableRouteOnSpec<Q, C> tableRouteOnSpec;

    private LockMode lockMode;

    AbstractStandardQuery(C criteria) {
        super(criteria);
        this.tableRouteOnSpec = new TableRouteOnSpecImpl<>(this);
    }

    /*################################## blow SelectPartSpec method ##################################*/

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelectClause(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Function<C, List<S>> function) {
        doSelectClause((Distinct) null, function.apply(this.criteria));
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(Distinct distinct, SelectPart selectPart) {
        doSelectClause(distinct, selectPart);
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(SelectPart selectPart) {
        doSelectClause((Distinct) null, selectPart);
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(SelectPart selectPart1, SelectPart selectPart2) {
        doSelectClause((Distinct) null, selectPart1, selectPart2);
        return this;
    }

    @Override
    public final FromSpec<Q, C> select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3) {
        doSelectClause((Distinct) null, selectPart1, selectPart2, selectPart3);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, List<S> selectPartList) {
        doSelectClause(distinct, selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> FromSpec<Q, C> select(List<S> selectPartList) {
        doSelectClause((Distinct) null, selectPartList);
        return this;
    }

    /*################################## blow FromSpec method ##################################*/

    @Override
    public final TableRouteJoinSpec<Q, C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    @Override
    public final JoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.NONE);
        return this;
    }

    @Override
    public final JoinSpec<Q, C> route(int databaseIndex, int tableIndex) {
        doRouteClause(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final JoinSpec<Q, C> route(int tableIndex) {
        doRouteClause(-1, tableIndex);
        return this;
    }

    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final TableRouteOnSpec<Q, C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final OnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final TableRouteOnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final OnSpec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final TableRouteOnSpec<Q, C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final OnSpec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final TableRouteOnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final OnSpec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final TableRouteOnSpec<Q, C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final OnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final TableRouteOnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final OnSpec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    /*################################## blow WhereSpec method ##################################*/

    @Override
    public final GroupBySpec<Q, C> where(List<IPredicate> predicateList) {
        addPredicateList(predicateList);
        return this;
    }

    @Override
    public final GroupBySpec<Q, C> where(Function<C, List<IPredicate>> function) {
        addPredicateList(function.apply(this.criteria));
        return this;
    }

    @Override
    public final WhereAndSpec<Q, C> where(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    /*################################## blow WhereAndSpec method ##################################*/

    @Override
    public final WhereAndSpec<Q, C> and(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    @Override
    public final WhereAndSpec<Q, C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }


    @Override
    public final WhereAndSpec<Q, C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }

    /*################################## blow GroupBySpec method ##################################*/

    @Override
    public final HavingSpec<Q, C> groupBy(SortPart sortPart) {
        addGroupBy(sortPart);
        return this;
    }

    @Override
    public final HavingSpec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2) {
        addGroupByList(Arrays.asList(sortPart1, sortPart2));
        return this;
    }

    @Override
    public final HavingSpec<Q, C> groupBy(List<SortPart> sortPartList) {
        _Assert.notEmpty(sortPartList, "sortPartList must not empty.");
        addGroupByList(sortPartList);
        return this;
    }

    @Override
    public final HavingSpec<Q, C> groupBy(Function<C, List<SortPart>> function) {
        List<SortPart> sortPartList = function.apply(this.criteria);
        _Assert.notEmpty(sortPartList, "sortPartList must not empty.");
        addGroupByList(sortPartList);
        return this;
    }

    @Override
    public final HavingSpec<Q, C> ifGroupBy(Function<C, List<SortPart>> function) {
        addGroupByList(function.apply(this.criteria));
        return this;
    }

    /*################################## blow HavingSpec method ##################################*/

    @Override
    public final OrderBySpec<Q, C> having(IPredicate predicate) {
        addHaving(predicate);
        return this;
    }

    @Override
    public final OrderBySpec<Q, C> having(List<IPredicate> predicateList) {
        _Assert.notEmpty(predicateList, "predicateList not empty.");
        addHavingList(predicateList);
        return this;
    }

    @Override
    public final OrderBySpec<Q, C> having(Function<C, List<IPredicate>> function) {
        List<IPredicate> predicateList = function.apply(this.criteria);
        _Assert.notEmpty(predicateList, "predicateList not empty.");
        addHavingList(predicateList);
        return this;
    }

    @Override
    public final OrderBySpec<Q, C> ifHaving(Function<C, List<IPredicate>> function) {
        addHavingList(function.apply(this.criteria));
        return this;
    }

    /*################################## blow OrderBySpec method ##################################*/

    @Override
    public final LimitClause<Q, C> orderBy(SortPart sortPart) {
        addOrderBy(sortPart);
        return this;
    }

    @Override
    public final LimitClause<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        addOrderByList(Arrays.asList(sortPart1, sortPart2));
        return this;
    }

    @Override
    public final LimitSpec<Q, C> orderBy(List<SortPart> sortPartList) {
        _Assert.notEmpty(sortPartList, "sortPartList must not empty.");
        addOrderByList(sortPartList);
        return this;
    }

    @Override
    public final LimitClause<Q, C> orderBy(Function<C, List<SortPart>> function) {
        List<SortPart> sortPartList = function.apply(this.criteria);
        _Assert.notEmpty(sortPartList, "sortPartList must not empty.");
        addOrderByList(sortPartList);
        return this;
    }

    @Override
    public final LimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function) {
        addOrderByList(function.apply(this.criteria));
        return this;
    }

    /*################################## blow LimitSpec method ##################################*/

    @Override
    public final LockSpec<Q, C> limit(int rowCount) {
        doLimit(-1, rowCount);
        return this;
    }

    @Override
    public final LockSpec<Q, C> limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }

    @Override
    public final LockSpec<Q, C> ifLimit(Function<C, LimitOption> function) {
        LimitOption option = function.apply(this.criteria);
        if (option != null) {
            doLimit(option.offset(), option.rowCount());
        }
        return this;
    }

    @Override
    public final LockSpec<Q, C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public final LockSpec<Q, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(offset, rowCount);
        }
        return this;
    }

    @Override
    public final QuerySpec<Q> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final QuerySpec<Q> ifLock(Function<C, LockMode> function) {
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

    @Nullable
    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }


    /*################################## blow package method ##################################*/

    @Override
    final boolean hasLockClause() {
        return this.lockMode != null;
    }

    @Override
    void internalClear() {
        this.lockMode = null;
    }

    /*################################## blow private static inner class  ##################################*/

    /**
     * Design this inner class for @link #route(int)} and {@link #route(int)} don't crash with top class
     */
    private static final class TableRouteOnSpecImpl<Q extends Query, C> implements Query, TableRouteOnSpec<Q, C> {

        private final AbstractStandardQuery<Q, C> standardQuery;

        private TableRouteOnSpecImpl(AbstractStandardQuery<Q, C> standardQuery) {
            this.standardQuery = standardQuery;
        }

        @Override
        public boolean requiredBrackets() {
            return this.standardQuery.requiredBrackets();
        }

        @Override
        public JoinSpec<Q, C> on(List<IPredicate> predicateList) {
            this.standardQuery.doOnClause(predicateList);
            return this.standardQuery;
        }

        @Override
        public JoinSpec<Q, C> on(IPredicate predicate) {
            this.standardQuery.doOnClause(Collections.singletonList(predicate));
            return this.standardQuery;
        }

        @Override
        public JoinSpec<Q, C> on(Function<C, List<IPredicate>> function) {
            this.standardQuery.doOnClause(function.apply(this.standardQuery.criteria));
            return this.standardQuery;
        }

        @Override
        public OnSpec<Q, C> route(int databaseIndex, int tableIndex) {
            this.standardQuery.doRouteClause(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public OnSpec<Q, C> route(int tableIndex) {
            this.standardQuery.doRouteClause(-1, tableIndex);
            return this;
        }

        @Override
        public void prepared() {
            this.standardQuery.prepared();
        }
    }

}
