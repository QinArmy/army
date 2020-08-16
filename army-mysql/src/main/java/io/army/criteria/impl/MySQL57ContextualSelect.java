package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql.InnerMySQL57Select;
import io.army.criteria.impl.inner.mysql.MySQLTable57Wrapper;
import io.army.criteria.mysql.MySQL57Select;
import io.army.criteria.mysql.MySQLIndexHint;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class MySQL57ContextualSelect<C> extends AbstractSelect<C> implements MySQL57Select
        , MySQL57Select.MySQLSelectPartAble<C>, MySQL57Select.MySQLFromAble<C>, MySQL57Select.MySQLJoinAble<C>
        , MySQL57Select.MySQLTableRouteJoinAble<C>, MySQL57Select.MySQLWhereAble<C>
        , MySQL57Select.MySQLWhereAndAble<C>, MySQL57Select.MySQLGroupByAble<C>, MySQL57Select.MySQLWithRollUpAble<C>
        , MySQL57Select.MySQLHavingAble<C>, MySQL57Select.MySQLLockAble<C>, MySQL57Select.MySQLLimitAble<C>
        , MySQL57Select.MySQLUnionAble<C>, InnerMySQL57Select {

    static <C> MySQL57ContextualSelect<C> build(C criteria) {
        return new MySQL57ContextualSelect<>(criteria);
    }

    private final CriteriaContext criteriaContext;

    private final OnClauseImpl<C> joinClause;

    private MySQL57LockMode lockMode;

    private boolean withRollUp;

    private boolean enableIndexHint;

    private MySQL57ContextualSelect(C criteria) {
        super(criteria);
        this.joinClause = new OnClauseImpl<>(this);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextHolder.setContext(this.criteriaContext);
    }

    @Override
    public <S extends SelectPart> MySQLFromAble<C> select(Distinct distinct, Function<C, List<S>> function) {
        doSelectClause(distinct, function.apply(this.criteria));
        return this;
    }

    @Override
    public <S extends SelectPart> MySQLFromAble<C> select(Function<C, List<S>> function) {
        doSelectClause((Distinct) null, function.apply(this.criteria));
        return this;
    }

    @Override
    public MySQLFromAble<C> select(Distinct distinct, SelectPart selectPart) {
        doSelectClause(distinct, selectPart);
        return this;
    }

    @Override
    public MySQLFromAble<C> select(SelectPart selectPart) {
        doSelectClause((Distinct) null, selectPart);
        return this;
    }

    @Override
    public <S extends SelectPart> MySQLFromAble<C> select(Distinct distinct, List<S> selectPartList) {
        doSelectClause(distinct, selectPartList);
        return this;
    }

    @Override
    public <S extends SelectPart> MySQLFromAble<C> select(List<S> selectPartList) {
        doSelectClause((Distinct) null, selectPartList);
        return this;
    }

    @Override
    public MySQLTableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.NONE);
        return this;
    }

    @Override
    public MySQLJoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.NONE);
        return this;
    }

    @Override
    public MySQLAfterFromIndexHintAble<C> route(int databaseIndex, int tableIndex) {
        doRouteClause(databaseIndex, tableIndex);
        return this;
    }

    @Override
    public MySQLAfterFromIndexHintAble<C> route(int tableIndex) {
        doRouteClause(-1, tableIndex);
        return this;
    }

    @Override
    public MySQLJoinAble<C> indexHintList(Function<C, List<MySQLIndexHint>> function) {
        doAddIndexHint(function);
        return this;
    }

    @Override
    public MySQLTableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.LEFT);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.LEFT);
        return this.joinClause;
    }

    @Override
    public MySQLTableRouteOnAble<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.LEFT);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.LEFT);
        return this.joinClause;
    }

    @Override
    public MySQLTableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLTableRouteOnAble<C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLTableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.RIGHT);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT);
        return this.joinClause;
    }

    @Override
    public MySQLTableRouteOnAble<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.RIGHT);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.RIGHT);
        return this.joinClause;
    }

    /*################################## blow straight joint method ##################################*/

    @Override
    public MySQLTableRouteOnAble<C> straightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTable(tableMeta, tableAlias, JoinType.STRAIGHT_JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addSubQuery(function.apply(this.criteria), subQueryAlia, JoinType.STRAIGHT_JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLTableRouteOnAble<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias) {
        ifAddTable(predicate, tableMeta, tableAlias, JoinType.STRAIGHT_JOIN);
        return this.joinClause;
    }

    @Override
    public MySQLOnAble<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        ifAddSubQuery(function, subQueryAlia, JoinType.STRAIGHT_JOIN);
        return this.joinClause;
    }

    /*################################## blow MySQLWhereAble method ##################################*/

    @Override
    public MySQLGroupByAble<C> where(List<IPredicate> predicateList) {
        addPredicateList(predicateList);
        return this;
    }

    @Override
    public MySQLGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        addPredicateList(function.apply(this.criteria));
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> where(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> and(IPredicate predicate) {
        addPredicate(predicate);
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> ifAnd(@Nullable IPredicate predicate) {
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }

    @Override
    public MySQLWhereAndAble<C> ifAnd(Function<C, IPredicate> function) {
        IPredicate predicate = function.apply(this.criteria);
        if (predicate != null) {
            addPredicate(predicate);
        }
        return this;
    }

    @Override
    public MySQLWithRollUpAble<C> groupBy(SortPart sortPart) {
        addGroupBy(sortPart);
        return this;
    }

    @Override
    public MySQLWithRollUpAble<C> groupBy(List<SortPart> sortPartList) {
        addGroupByList(sortPartList);
        return this;
    }

    @Override
    public MySQLWithRollUpAble<C> groupBy(Function<C, List<SortPart>> function) {
        addGroupByList(function.apply(this.criteria));
        return this;
    }

    @Override
    public MySQLHavingAble<C> withRollUp() {
        this.withRollUp = true;
        return this;
    }

    @Override
    public MySQLOrderByAble<C> having(IPredicate predicate) {
        addHaving(predicate);
        return this;
    }

    @Override
    public MySQLOrderByAble<C> having(List<IPredicate> predicateList) {
        addHavingList(predicateList);
        return this;
    }

    @Override
    public MySQLOrderByAble<C> having(Function<C, List<IPredicate>> function) {
        addHavingList(function.apply(this.criteria));
        return this;
    }

    @Override
    public MySQLLimitAble<C> orderBy(SortPart sortPart) {
        addOrderBy(sortPart);
        return this;
    }

    @Override
    public MySQLLimitAble<C> orderBy(List<SortPart> sortPartList) {
        addOrderByList(sortPartList);
        return this;
    }

    @Override
    public MySQLLimitAble<C> orderBy(Function<C, List<SortPart>> function) {
        addOrderByList(function.apply(this.criteria));
        return this;
    }



    /*################################## blow LimitClause method ##################################*/

    @Override
    public MySQLLockAble<C> limit(int rowCount) {
        doLimit(-1, rowCount);
        return this;
    }

    @Override
    public MySQLLockAble<C> limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }

    @Override
    public MySQLLockAble<C> ifLimit(Function<C, LimitOption> function) {
        LimitOption option = function.apply(this.criteria);
        if (option != null) {
            doLimit(option.offset(), option.rowCount());
        }
        return this;
    }

    @Override
    public MySQLLockAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            doLimit(-1, rowCount);
        }
        return this;
    }

    @Override
    public MySQLLockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            doLimit(offset, rowCount);
        }
        return this;
    }

    @Override
    public SelectAble forUpdate() {
        this.lockMode = MySQL57LockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public SelectAble lockInShareMode() {
        this.lockMode = MySQL57LockMode.LOCK_IN_SHARE_MODE;
        return this;
    }

    /*################################## blow MySQLUnionAble method ##################################*/

    @Override
    public UnionAble<C> brackets() {
        return ComposeSelects.brackets(this.criteria, thisSelect());
    }

    @Override
    public <S extends Select> UnionAble<C> union(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION, function);
    }

    @Override
    public <S extends Select> UnionAble<C> unionAll(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_ALL, function);
    }

    @Override
    public <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function) {
        return ComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_DISTINCT, function);
    }

    @Override
    public final boolean groupByWithRollUp() {
        return this.withRollUp;
    }

    @Override
    public final SQLModifier lockMode() {
        return this.lockMode;
    }

    /*################################## blow package method ##################################*/

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {
        this.enableIndexHint = true;
        this.criteriaContext.onAddTable(table, tableAlias);
    }

    @Override
    void onNotAddTable() {
        this.enableIndexHint = false;
    }

    @Override
    void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    void internalAsSelect() {
        CriteriaContextHolder.clearContext(this.criteriaContext);
    }

    @Override
    void internalClear() {
        this.lockMode = null;
        this.withRollUp = false;
    }


    @Override
    final boolean hasLockClause() {
        return this.lockMode != null;
    }


    /*################################## blow private method ##################################*/

    /**
     * @see #onAddTable(TableMeta, String)
     * @see #onNotAddTable()
     * @see #enableIndexHint
     */
    private void doAddIndexHint(Function<C, List<MySQLIndexHint>> function) {
        if (this.enableIndexHint) {
            List<MySQLIndexHint> hintList = function.apply(this.criteria);
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

        private List<MySQLIndexHint> indexHintList = Collections.emptyList();

        private MySQLTableWrapperImpl(TableAble tableAble, String alias, JoinType jointType) {
            super(tableAble, alias, jointType);
        }

        @Override
        public List<MySQLIndexHint> indexHintList() {
            return this.indexHintList;
        }

        private void indexHintList(List<MySQLIndexHint> indexHintList) {
            Assert.state(this.indexHintList.isEmpty(), "indexHintList duplicate set.");
            Assert.notEmpty(indexHintList, "indexHintList must not empty.");
            this.indexHintList = Collections.unmodifiableList(new ArrayList<>(indexHintList));
        }
    }

    private static final class OnClauseImpl<C> implements MySQLTableRouteOnAble<C> {

        private final MySQL57ContextualSelect<C> contextualSelect;

        private OnClauseImpl(MySQL57ContextualSelect<C> contextualSelect) {
            this.contextualSelect = contextualSelect;
        }

        @Override
        public final MySQLJoinAble<C> on(List<IPredicate> predicateList) {
            this.contextualSelect.doOnClause(predicateList);
            return this.contextualSelect;
        }

        @Override
        public final MySQLJoinAble<C> on(IPredicate predicate) {
            this.contextualSelect.doOnClause(Collections.singletonList(predicate));
            return this.contextualSelect;
        }

        @Override
        public final MySQLJoinAble<C> on(Function<C, List<IPredicate>> function) {
            this.contextualSelect.doOnClause(function.apply(this.contextualSelect.criteria));
            return this.contextualSelect;
        }

        @Override
        public MySQLOnAble<C> indexHintList(Function<C, List<MySQLIndexHint>> function) {
            this.contextualSelect.doAddIndexHint(function);
            return this;
        }

        @Override
        public MySQLOnAble<C> route(int databaseIndex, int tableIndex) {
            this.contextualSelect.doRouteClause(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public MySQLOnAble<C> route(int tableIndex) {
            this.contextualSelect.doRouteClause(-1, tableIndex);
            return this;
        }
    }
}
