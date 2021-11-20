package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql.MySQLTable57Wrapper;
import io.army.criteria.impl.inner.mysql._MySQL57Query;
import io.army.criteria.mysql.MySQL57IndexHint;
import io.army.criteria.mysql.MySQL57Query;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractMySQL57Query<Q extends MySQL57Query, C> extends AbstractQuery<Q, C> implements MySQL57Query
        , MySQL57Query.MySQLSelectPartSpec<Q, C>, MySQL57Query.MySQLFromSpec<Q, C>
        , MySQL57Query.MySQLTableRouteJoinSpec<Q, C>, MySQL57Query.MySQLWhereSpec<Q, C>
        , MySQL57Query.MySQLWhereAndSpec<Q, C>, MySQL57Query.MySQLGroupBySpec<Q, C>
        , MySQL57Query.MySQLWithRollUpSpec<Q, C>, MySQL57Query.MySQLHavingSpec<Q, C>
        , _MySQL57Query {

    final MySQLTableRouteOnSpec<Q, C> tableRouteOnSpec;

    private MySQL57LockMode lockMode;

    private boolean enableIndexHint;

    private boolean withRollUp;

    AbstractMySQL57Query(C criteria) {
        super(criteria);
        this.tableRouteOnSpec = new MySQLTableRouteOnSpecImpl<>(this);
    }

    @Override
    public final <S extends SelectPart> MySQLFromSpec<Q, C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelectClause(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public final <S extends SelectPart> MySQLFromSpec<Q, C> select(Function<C, List<S>> function) {
        doSelectClause((Distinct) null, function.apply(this.criteria));
        return this;
    }

    @Override
    public final MySQLFromSpec<Q, C> select(Distinct distinct, SelectPart selectPart) {
        doSelectClause(distinct, selectPart);
        return this;
    }

    @Override
    public final MySQLFromSpec<Q, C> select(SelectPart selectPart) {
        doSelectClause((Distinct) null, selectPart);
        return this;
    }

    @Override
    public final <S extends SelectPart> MySQLFromSpec<Q, C> select(Distinct distinct, List<S> selectPartList) {
        doSelectClause(distinct, selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> MySQLFromSpec<Q, C> select(List<S> selectPartList) {
        doSelectClause((Distinct) null, selectPartList);
        return this;
    }

    @Override
    public final MySQLTableRouteJoinSpec<Q, C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    @Override
    public final MySQLJoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.NONE);
        return this;
    }

    @Override
    public final MySQLJoinSpec<Q, C> route(int databaseIndex, int tableIndex) {
        doRouteClause(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public final MySQLJoinSpec<Q, C> route(int tableIndex) {
        doRouteClause(-1, tableIndex);
        return this;
    }

    @Override
    public final MySQLJoinSpec<Q, C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function) {
        doAddIndexHint(function);
        return this;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta
            , String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.LEFT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta
            , String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.RIGHT);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> straightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.STRAIGHT_JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> straightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.STRAIGHT_JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLTableRouteOnSpec<Q, C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> tableMeta
            , String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.STRAIGHT_JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLOnSpec<Q, C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.STRAIGHT_JOIN);
        return this.tableRouteOnSpec;
    }

    @Override
    public final MySQLWhereAndSpec<Q, C> where(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    @Override
    public final MySQLGroupBySpec<Q, C> where(List<IPredicate> predicateList) {
        addPredicateList(predicateList);
        return this;
    }

    @Override
    public final MySQLGroupBySpec<Q, C> ifWhere(Function<C, List<IPredicate>> function) {
        addPredicateList(function.apply(this.criteria));
        return this;
    }

    @Override
    public final MySQLWhereAndSpec<Q, C> and(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    @Override
    public final MySQLWhereAndSpec<Q, C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }

    @Override
    public final MySQLWhereAndSpec<Q, C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }

    @Override
    public final MySQLWithRollUpSpec<Q, C> groupBy(SortPart sortPart) {
        addGroupBy(sortPart);
        return this;
    }

    @Override
    public final MySQLWithRollUpSpec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2) {
        addGroupByList(Arrays.asList(sortPart1, sortPart2));
        return this;
    }

    @Override
    public final MySQLWithRollUpSpec<Q, C> groupBy(List<SortPart> sortPartList) {
        Assert.notEmpty(sortPartList, "sortPartList not empty.");
        addGroupByList(sortPartList);
        return this;
    }

    @Override
    public final MySQLWithRollUpSpec<Q, C> groupBy(Function<C, List<SortPart>> function) {
        List<SortPart> sortPartList = function.apply(this.criteria);
        Assert.notEmpty(sortPartList, "sortPartList not empty.");
        addGroupByList(sortPartList);
        return this;
    }

    @Override
    public final MySQLWithRollUpSpec<Q, C> ifGroupBy(Function<C, List<SortPart>> function) {
        addGroupByList(function.apply(this.criteria));
        return this;
    }

    @Override
    public final MySQLHavingSpec<Q, C> withRollUp() {
        this.withRollUp = true;
        return this;
    }

    @Override
    public final MySQLHavingSpec<Q, C> withRollUp(Predicate<C> predicate) {
        this.withRollUp = predicate.test(this.criteria);
        return this;
    }

    @Override
    public final MySQLOrderBySpec<Q, C> having(IPredicate predicate) {
        addHaving(predicate);
        return this;
    }

    @Override
    public final MySQLOrderBySpec<Q, C> having(List<IPredicate> predicateList) {
        Assert.notEmpty(predicateList, "predicateList not empty.");
        addHavingList(predicateList);
        return this;
    }

    @Override
    public final MySQLOrderBySpec<Q, C> having(Function<C, List<IPredicate>> function) {
        List<IPredicate> predicateList = function.apply(this.criteria);
        Assert.notEmpty(predicateList, "predicateList not empty.");
        addHavingList(predicateList);
        return this;
    }

    @Override
    public final MySQLOrderBySpec<Q, C> ifHaving(Function<C, List<IPredicate>> function) {
        addHavingList(function.apply(this.criteria));
        return this;
    }

    @Override
    public final MySQLLimitSpec<Q, C> orderBy(SortPart sortPart) {
        addOrderBy(sortPart);
        return this;
    }

    @Override
    public final MySQLLimitSpec<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        addOrderByList(Arrays.asList(sortPart1, sortPart2));
        return this;
    }

    @Override
    public final MySQLLimitSpec<Q, C> orderBy(List<SortPart> sortPartList) {
        Assert.notEmpty(sortPartList, "sortPartList not empty.");
        addOrderByList(sortPartList);
        return this;
    }

    @Override
    public final MySQLLimitSpec<Q, C> orderBy(Function<C, List<SortPart>> function) {
        List<SortPart> sortPartList = function.apply(this.criteria);
        Assert.notEmpty(sortPartList, "sortPartList not empty.");
        addOrderByList(sortPartList);
        return this;
    }

    @Override
    public final MySQLLimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function) {
        addOrderByList(function.apply(this.criteria));
        return this;
    }


    @Override
    public final MySQLLockSpec<Q, C> limit(int rowCount) {
        doLimit(-1, rowCount);
        return this;
    }

    @Override
    public final MySQLLockSpec<Q, C> limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }

    @Override
    public final MySQLLockSpec<Q, C> ifLimit(Function<C, LimitOption> function) {
        LimitOption option = function.apply(this.criteria);
        if (option != null) {
            doLimit(option.offset(), option.rowCount());
        }
        return this;
    }

    @Override
    public final MySQLLockSpec<Q, C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            doLimit(-1, rowCount);
        }
        return this;
    }

    @Override
    public final MySQLLockSpec<Q, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            doLimit(offset, rowCount);
        }
        return this;
    }

    @Override
    public final QuerySpec<Q> forUpdate() {
        this.lockMode = MySQL57LockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final MySQLLockSpec<Q, C> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQL57LockMode.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final QuerySpec<Q> lockInShareMode() {
        this.lockMode = MySQL57LockMode.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final MySQLLockSpec<Q, C> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQL57LockMode.LOCK_IN_SHARE_MODE;
        }
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
        return ComposeQueries.compose(this.criteria, asQuery(), UnionType.UNION, function);
    }

    @Override
    public final UnionSpec<Q, C> unionDistinct(Function<C, Q> function) {
        return ComposeQueries.compose(this.criteria, asQuery(), UnionType.UNION_DISTINCT, function);
    }

    @Override
    public final SQLModifier lockMode() {
        return this.lockMode;
    }

    @Override
    public final boolean groupByWithRollUp() {
        return this.withRollUp;
    }

    /*################################## blow package method ##################################*/

    @Override
    final boolean hasLockClause() {
        return this.lockMode != null;
    }

    @Override
    void internalClear() {
        this.lockMode = null;
        this.withRollUp = false;
    }

    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        this.enableIndexHint = true;
        onMySQLAddTable(table, tableAlias);
    }

    @Override
    final void onNotAddTable() {
        this.enableIndexHint = false;
        super.onNotAddTable();
    }


    @Override
    final TableWrapperImpl createTableWrapper(TableAble tableAble, String alias, JoinType joinType) {
        return tableAble instanceof TableMeta
                ? new MySQLTableWrapperImpl(tableAble, alias, joinType)
                : super.createTableWrapper(tableAble, alias, joinType);
    }

    abstract void onMySQLAddTable(TableMeta<?> table, String tableAlias);


    /*################################## blow private method ##################################*/

    /**
     * @see #onAddTable(TableMeta, String)
     * @see #onNotAddTable()
     * @see #enableIndexHint
     * @see #createTableWrapper(TableAble, String, JoinType)
     */
    private void doAddIndexHint(Function<C, List<MySQL57IndexHint>> function) {
        if (this.enableIndexHint) {
            List<MySQL57IndexHint> hintList = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(hintList)) {
                TableWrapperImpl tableWrapper = lastTableWrapper();
                Assert.state(tableWrapper instanceof MySQLTableWrapperImpl, "tableWrapper not MySQLTableWrapperImpl");
                MySQLTableWrapperImpl mySQLTableWrapper = (MySQLTableWrapperImpl) tableWrapper;
                mySQLTableWrapper.indexHintList(hintList);
            }
            this.enableIndexHint = false;
        }
    }

    /*################################## blow private static inner class ##################################*/

    private enum MySQL57LockMode implements SQLModifier {

        FOR_UPDATE("FOR UPDATE"),

        LOCK_IN_SHARE_MODE("LOCK IN SHARE MODE");

        private final String modifier;

        MySQL57LockMode(String modifier) {
            this.modifier = modifier;
        }


        @Override
        public String render() {
            return this.modifier;
        }
    }

    private static final class MySQLTableWrapperImpl extends TableWrapperImpl implements MySQLTable57Wrapper {

        private List<MySQL57IndexHint> indexHintList = Collections.emptyList();

        private MySQLTableWrapperImpl(TableAble tableAble, String alias, JoinType jointType) {
            super(tableAble, alias, jointType);
        }

        @Override
        public List<MySQL57IndexHint> indexHintList() {
            return this.indexHintList;
        }

        private void indexHintList(List<MySQL57IndexHint> indexHintList) {
            Assert.state(this.indexHintList.isEmpty(), "indexHintList duplicate set.");
            Assert.notEmpty(indexHintList, "indexHintList must not empty.");
            this.indexHintList = Collections.unmodifiableList(new ArrayList<>(indexHintList));
        }
    }


    /**
     * Design this inner class for @link #route(int)} and {@link #route(int)} don't crash with top class
     */
    private static final class MySQLTableRouteOnSpecImpl<Q extends MySQL57Query, C> implements Query
            , MySQLTableRouteOnSpec<Q, C> {

        private final AbstractMySQL57Query<Q, C> mySQL57Query;

        private MySQLTableRouteOnSpecImpl(AbstractMySQL57Query<Q, C> mySQL57Query) {
            this.mySQL57Query = mySQL57Query;
        }

        @Override
        public boolean requiredBrackets() {
            return this.mySQL57Query.requiredBrackets();
        }

        @Override
        public MySQLJoinSpec<Q, C> on(List<IPredicate> predicateList) {
            this.mySQL57Query.doOnClause(predicateList);
            return this.mySQL57Query;
        }

        @Override
        public MySQLJoinSpec<Q, C> on(IPredicate predicate) {
            this.mySQL57Query.doOnClause(Collections.singletonList(predicate));
            return this.mySQL57Query;
        }

        @Override
        public MySQLJoinSpec<Q, C> on(Function<C, List<IPredicate>> function) {
            this.mySQL57Query.doOnClause(function.apply(this.mySQL57Query.criteria));
            return this.mySQL57Query;
        }

        @Override
        public MySQLIndexHintOnSpec<Q, C> route(int databaseIndex, int tableIndex) {
            this.mySQL57Query.doRouteClause(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public MySQLIndexHintOnSpec<Q, C> route(int tableIndex) {
            this.mySQL57Query.doRouteClause(-1, tableIndex);
            return this;
        }

        @Override
        public MySQLOnSpec<Q, C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function) {
            this.mySQL57Query.doAddIndexHint(function);
            return this;
        }

        @Override
        public boolean prepared() {
            return this.mySQL57Query.prepared();
        }
    }


}
