package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.inner.postgre.PostgreInnerQuery;
import io.army.criteria.inner.postgre.PostgreLockWrapper;
import io.army.criteria.inner.postgre.PostgreTableWrapper;
import io.army.criteria.postgre.PostgreAliasFuncTable;
import io.army.criteria.postgre.PostgreFuncTable;
import io.army.criteria.postgre.PostgreSelect;
import io.army.criteria.postgre.PostgreWindow;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractPostgreMultiSelect<C> extends AbstractSQL implements PostgreSelect
        , PostgreSelect.PostgreSelectPartAble<C>, PostgreSelect.PostgreFromAble<C>
        , PostgreSelect.PostgreHavingAble<C>, PostgreSelect.PostgreWhereAndAble<C>
        , PostgreSelect.PostgreTableSampleAble<C>, PostgreSelect.PostgreJoinAble<C>
        , PostgreSelect.PostgreTableSampleOnAble<C>, PostgreSelect.PostgreLockOfTablesAble<C>
        , PostgreInnerQuery {

    static final String NOT_PREPARED_MSG = "Select criteria don't haven invoke asSelect() method.";


    final C criteria;

    private List<SQLModifier> modifierList = new ArrayList<>(1);

    private List<Expression<?>> distinctOnExpList;

    private List<SelectPart> selectPartList = new ArrayList<>();

    private List<IPredicate> predicateList = new ArrayList<>();

    private List<Expression<?>> groupExpList;

    private List<IPredicate> havingList;

    private List<PostgreWindow> windowList;

    private List<Expression<?>> sortExpList;

    private int offset = -1;

    private int rowCount = -1;

    private List<PostgreLockWrapper> lockWrapperList;


    private boolean prepared;

    AbstractPostgreMultiSelect(C criteria) {
        this.criteria = criteria;

    }

    /*################################## blow PostgreSelectPartAble method ##################################*/

    @Override
    public final <S extends SelectPart> PostgreSelect.PostgreFromAble<C> select(Distinct distinct
            , Function<C, List<S>> function) {
        this.modifierList.add(distinct);
        this.selectPartList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreFromAble<C> select(Distinct distinct, SelectPart selectPart) {
        this.modifierList.add(distinct);
        this.selectPartList.add(selectPart);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreFromAble<C> select(SelectPart selectPart) {
        this.selectPartList.add(selectPart);
        return this;
    }

    @Override
    public final <S extends SelectPart> PostgreSelect.PostgreFromAble<C> select(Distinct distinct
            , List<S> selectPartList) {
        this.modifierList.add(distinct);
        this.selectPartList.addAll(selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> PostgreSelect.PostgreFromAble<C> select(List<S> selectPartList) {
        this.selectPartList.addAll(selectPartList);
        return this;
    }

    @Override
    public final <S extends SelectPart> PostgreSelect.PostgreFromAble<C> selectDistinct(
            Function<C, List<Expression<?>>> onExpsFunction, Function<C, List<S>> selectParsFunction) {
        this.modifierList.add(Distinct.DISTINCT);
        List<Expression<?>> expressionList = onExpsFunction.apply(this.criteria);
        if (this.distinctOnExpList == null) {
            this.distinctOnExpList = new ArrayList<>(expressionList.size());
        }
        this.distinctOnExpList.addAll(expressionList);
        this.selectPartList.addAll(selectParsFunction.apply(this.criteria));
        return this;
    }

    @Override
    public final <S extends SelectPart> PostgreSelect.PostgreFromAble<C> selectDistinct(
            Function<C, List<Expression<?>>> onFunction, S selectPart) {
        this.modifierList.add(Distinct.DISTINCT);
        List<Expression<?>> expressionList = onFunction.apply(this.criteria);
        if (this.distinctOnExpList == null) {
            this.distinctOnExpList = new ArrayList<>(expressionList.size());
        }
        this.distinctOnExpList.addAll(expressionList);
        this.selectPartList.add(selectPart);
        return this;
    }

    /*################################## blow PostgreFromAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreTableSampleAble<C> from(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new PostgreTableWrapperImpl(tableMeta, tableAlias, JoinType.NONE, new ArrayList<>(2)));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> from(Function<C, SubQuery> subQueryFunction, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(subQueryFunction.apply(this.criteria), subQueryAlia, JoinType.NONE));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> fromLateral(Function<C, SubQuery> subQueryFunction
            , String subQueryAlia) {
        PostgreTableWrapperImpl tableWrapper = new PostgreTableWrapperImpl(
                subQueryFunction.apply(this.criteria), subQueryAlia, JoinType.NONE, PostgreModifier.LATERAL
        );
        addTableAble(tableWrapper);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> fromWithQuery(String withSubQueryName) {
        addTableAble(new TableWrapperImpl(new WithQuery(withSubQueryName), "", JoinType.NONE));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreWhereAble<C> fromFunc(Function<C, PostgreFuncTable> funcFunction) {
        addTableAble(new TableWrapperImpl(funcFunction.apply(this.criteria), "", JoinType.NONE));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> fromAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction) {
        PostgreAliasFuncTable table = funcFunction.apply(this.criteria);
        addTableAble(new TableWrapperImpl(table, table.tableAlias(), JoinType.NONE));
        return this;
    }


    /*################################## blow PostgreWhereAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreGroupByAble<C> where(List<IPredicate> predicateList) {
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreGroupByAble<C> where(Function<C, List<IPredicate>> function) {
        this.predicateList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreWhereAndAble<C> where(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    /*################################## blow PostgreWhereAndAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreWhereAndAble<C> and(IPredicate predicate) {
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreWhereAndAble<C> and(Function<C, IPredicate> function) {
        this.predicateList.add(function.apply(this.criteria));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(predicate);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreWhereAndAble<C> ifAnd(Predicate<C> testPredicate
            , Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            this.predicateList.add(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow PostgreTableSampleAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction) {
        addTableSample(samplingMethodFunction.apply(this.criteria), null);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
            , Expression<Double> seedExp) {
        addTableSample(samplingMethodFunction.apply(this.criteria), seedExp);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> tableSample(Function<C, Expression<?>> samplingMethodFunction
            , Function<C, Expression<Double>> seedFunction) {
        addTableSample(samplingMethodFunction.apply(this.criteria), seedFunction.apply(this.criteria));
        return this;
    }


    /*################################## blow PostgreJoinAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreTableSampleOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new PostgreTableWrapperImpl(tableMeta, tableAlias, JoinType.LEFT, new ArrayList<>(2)));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.LEFT));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> leftJoinLateral(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new PostgreTableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.LEFT
                , PostgreModifier.LATERAL));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> leftJoinWithQuery(String withSubQueryName) {
        addTableAble(new TableWrapperImpl(new WithQuery(withSubQueryName), "", JoinType.LEFT));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> leftJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction) {
        PostgreAliasFuncTable table = funcFunction.apply(this.criteria);
        addTableAble(new TableWrapperImpl(table, table.tableAlias(), JoinType.LEFT));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreTableSampleOnAble<C> join(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new PostgreTableWrapperImpl(tableMeta, tableAlias, JoinType.JOIN, new ArrayList<>(2)));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.JOIN));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> joinLateral(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new PostgreTableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.JOIN
                , PostgreModifier.LATERAL));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> joinWithQuery(String withSubQueryName) {
        addTableAble(new TableWrapperImpl(new WithQuery(withSubQueryName), "", JoinType.JOIN));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> joinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction) {
        PostgreAliasFuncTable table = funcFunction.apply(this.criteria);
        addTableAble(new TableWrapperImpl(table, table.tableAlias(), JoinType.JOIN));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreTableSampleOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new PostgreTableWrapperImpl(tableMeta, tableAlias, JoinType.RIGHT, new ArrayList<>(2)));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.RIGHT));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> rightJoinWithQuery(String withSubQueryName) {
        addTableAble(new TableWrapperImpl(new WithQuery(withSubQueryName), "", JoinType.RIGHT));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> rightJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction) {
        PostgreAliasFuncTable table = funcFunction.apply(this.criteria);
        addTableAble(new TableWrapperImpl(table, table.tableAlias(), JoinType.RIGHT));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreTableSampleOnAble<C> fullJoin(TableMeta<?> tableMeta, String tableAlias) {
        addTableAble(new PostgreTableWrapperImpl(tableMeta, tableAlias, JoinType.FULL, new ArrayList<>(2)));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> fullJoin(Function<C, SubQuery> function, String subQueryAlia) {
        addTableAble(new TableWrapperImpl(function.apply(this.criteria), subQueryAlia, JoinType.FULL));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> fullJoinWithQuery(String withSubQueryName) {
        addTableAble(new TableWrapperImpl(new WithQuery(withSubQueryName), "", JoinType.FULL));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreOnAble<C> fullJoinAliasFunc(Function<C, PostgreAliasFuncTable> funcFunction) {
        PostgreAliasFuncTable table = funcFunction.apply(this.criteria);
        addTableAble(new TableWrapperImpl(table, table.tableAlias(), JoinType.FULL));
        return this;
    }


    /*################################## blow PostgreOnAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreJoinAble<C> on(List<IPredicate> predicateList) {
        doOn(predicateList);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> on(IPredicate predicate) {
        doOn(Collections.singletonList(predicate));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreJoinAble<C> on(Function<C, List<IPredicate>> function) {
        doOn(function.apply(this.criteria));
        return this;
    }


    /*################################## blow PostgreGroupByAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreHavingAble<C> groupBy(Expression<?> groupExp) {
        if (this.groupExpList == null) {
            this.groupExpList = new ArrayList<>(1);
        }
        this.groupExpList.add(groupExp);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreHavingAble<C> groupBy(List<Expression<?>> groupExpList) {
        if (this.groupExpList == null) {
            this.groupExpList = new ArrayList<>(groupExpList.size());
        }
        this.groupExpList.addAll(groupExpList);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreHavingAble<C> groupBy(Function<C, List<Expression<?>>> function) {
        List<Expression<?>> list = function.apply(this.criteria);
        if (this.groupExpList == null) {
            this.groupExpList = new ArrayList<>(list.size());
        }
        this.groupExpList.addAll(list);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp) {
        if (predicate.test(this.criteria)) {
            groupBy(groupExp);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreHavingAble<C> ifGroupBy(Predicate<C> predicate
            , Function<C, List<Expression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            groupBy(expFunction.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow PostgreHavingAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreNoLockWindowAble<C> having(IPredicate predicate) {
        if (!CollectionUtils.isEmpty(groupExpList)) {
            if (this.havingList == null) {
                this.havingList = new ArrayList<>(1);
            }
            this.havingList.add(predicate);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreNoLockWindowAble<C> having(Function<C, List<IPredicate>> function) {
        if (!CollectionUtils.isEmpty(groupExpList)) {
            List<IPredicate> list = function.apply(this.criteria);
            if (this.havingList == null) {
                this.havingList = new ArrayList<>(list.size());
            }
            this.havingList.addAll(list);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreNoLockWindowAble<C> ifHaving(Predicate<C> predicate
            , Function<C, List<IPredicate>> function) {
        if (predicate.test(this.criteria)) {
            having(function);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreNoLockWindowAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            having(predicate);
        }
        return this;
    }


    /*################################## blow PostgreWindowAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreNoLockOrderByAble<C> window(
            Function<C, List<PostgreWindow>> windowListFunction) {
        List<PostgreWindow> list = windowListFunction.apply(this.criteria);
        if (this.windowList == null) {
            this.windowList = new ArrayList<>(list.size());
        }
        this.windowList.addAll(list);
        return this;
    }

    /*################################## blow PostgreOrderByAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreLimitAble<C> orderBy(Expression<?> orderExp) {
        if (this.sortExpList == null) {
            this.sortExpList = new ArrayList<>(1);
        }
        this.sortExpList.add(orderExp);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLimitAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        List<Expression<?>> list = function.apply(this.criteria);
        if (this.sortExpList == null) {
            this.sortExpList = new ArrayList<>(list.size());
        }
        this.sortExpList.addAll(list);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp) {
        if (predicate.test(this.criteria)) {
            orderBy(orderExp);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLimitAble<C> ifOrderBy(Predicate<C> predicate
            , Function<C, List<Expression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            orderBy(expFunction);
        }
        return this;
    }


    /*################################## blow PostgreLimitAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreLockAble<C> limit(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockAble<C> limit(int offset, int rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockAble<C> limit(Function<C, Pair<Integer, Integer>> function) {
        Pair<Integer, Integer> pair = function.apply(this.criteria);
        if (pair.getFirst() != null) {
            this.offset = pair.getFirst();
        }
        if (pair.getSecond() != null) {
            this.rowCount = pair.getSecond();
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            this.rowCount = rowCount;
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            this.offset = offset;
            this.rowCount = rowCount;
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockAble<C> ifLimit(Predicate<C> predicate
            , Function<C, Pair<Integer, Integer>> function) {
        if (predicate.test(this.criteria)) {
            limit(function);
        }
        return this;
    }

    /*################################## blow PostgreComposeAble method ##################################*/

    @Override
    public final PostgreComposeAble<C> brackets() {
        return PostgreComposeSelects.brackets(this.criteria, thisSelect());
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> union(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> unionAll(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> unionDistinct(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), UnionType.UNION_DISTINCT, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> intersect(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), PostgreModifier.INTERSECT, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> intersectAll(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), PostgreModifier.INTERSECT_ALL, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> intersectDistinct(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), PostgreModifier.INTERSECT_DISTINCT, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> except(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), PostgreModifier.EXCEPT, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> exceptAll(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), PostgreModifier.EXCEPT_ALL, function);
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> exceptDistinct(Function<C, S> function) {
        return PostgreComposeSelects.compose(this.criteria, thisSelect(), PostgreModifier.EXCEPT_DISTINCT, function);
    }

    /*################################## blow PostgreLockAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreLockOfTablesAble<C> lock(LockMode lockMode) {
        if (this.lockWrapperList == null) {
            this.lockWrapperList = new ArrayList<>(3);
        }
        this.lockWrapperList.add(new LockWrapper(lockMode));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockOfTablesAble<C> lock(Function<C, LockMode> function) {
        if (this.lockWrapperList == null) {
            this.lockWrapperList = new ArrayList<>(3);
        }
        this.lockWrapperList.add(new LockWrapper(function.apply(this.criteria)));
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockOfTablesAble<C> ifLock(Predicate<C> predicate, LockMode lockMode) {
        if (predicate.test(this.criteria)) {
            lock(lockMode);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockOfTablesAble<C> ifLock(Predicate<C> predicate
            , Function<C, LockMode> function) {
        if (predicate.test(this.criteria)) {
            lock(function);
        }
        return this;
    }

    /*################################## blow PostgreLockOfTablesAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreLockOptionAble<C> ofTable(TableMeta<?> lockTable) {
        Assert.state(!CollectionUtils.isEmpty(this.lockWrapperList)
                , "lockWrapperList is null/empty,criteria state error.");
        LockWrapper lockWrapper = (LockWrapper) this.lockWrapperList.get(this.lockWrapperList.size() - 1);
        lockWrapper.addOfTable(lockTable);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockOptionAble<C> ofTable(List<TableMeta<?>> lockTableList) {
        Assert.state(!CollectionUtils.isEmpty(this.lockWrapperList)
                , "lockWrapperList is null/empty,criteria state error.");
        LockWrapper lockWrapper = (LockWrapper) this.lockWrapperList.get(this.lockWrapperList.size() - 1);
        lockWrapper.addOfTables(lockTableList);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockOptionAble<C> ifOfTable(Predicate<C> predicate, TableMeta<?> lockTable) {
        if (predicate.test(this.criteria)) {
            ofTable(lockTable);
        }
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockOptionAble<C> ifOfTable(Predicate<C> predicate
            , Function<C, List<TableMeta<?>>> function) {
        if (predicate.test(this.criteria)) {
            ofTable(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow PostgreLockOptionAble method ##################################*/

    @Override
    public final PostgreSelect.PostgreLockAble<C> noWait() {
        Assert.state(!CollectionUtils.isEmpty(this.lockWrapperList)
                , "lockWrapperList is null/empty,criteria state error.");
        LockWrapper lockWrapper = (LockWrapper) this.lockWrapperList.get(this.lockWrapperList.size() - 1);
        lockWrapper.lockOption(LockOptions.NOWAIT);
        return this;
    }

    @Override
    public final PostgreSelect.PostgreLockAble<C> skipLocked() {
        Assert.state(!CollectionUtils.isEmpty(this.lockWrapperList)
                , "lockWrapperList is null/empty,criteria state error.");
        LockWrapper lockWrapper = (LockWrapper) this.lockWrapperList.get(this.lockWrapperList.size() - 1);
        lockWrapper.lockOption(LockOptions.SKIP_LOCKED);
        return this;
    }

    /*################################## blow PostgreSelectAble method ##################################*/

    @Override
    public final PostgreSelect asSelect() {
        if (prepared) {
            return this;
        }
        beforeAsSelect();

        // before unmodifiableList .
        processSelectPartList(this.selectPartList);
        // immutable tableWrapperList
        this.asSQL();
        this.modifierList = Collections.unmodifiableList(this.modifierList);

        // immutable distinctOnExpList
        if (this.distinctOnExpList == null) {
            this.distinctOnExpList = Collections.emptyList();
        } else {
            this.distinctOnExpList = Collections.unmodifiableList(this.distinctOnExpList);
        }

        this.selectPartList = Collections.unmodifiableList(this.selectPartList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        // immutable groupExpList and havingList
        if (this.groupExpList == null) {
            this.groupExpList = Collections.emptyList();
            this.havingList = Collections.emptyList();
        } else {
            this.groupExpList = Collections.unmodifiableList(this.groupExpList);
            if (this.havingList == null) {
                this.havingList = Collections.emptyList();
            } else {
                this.havingList = Collections.unmodifiableList(this.havingList);
            }
        }

        if (this.windowList == null) {
            this.windowList = Collections.emptyList();
        } else {
            this.windowList = Collections.unmodifiableList(this.windowList);
        }

        if (this.lockWrapperList == null) {
            this.lockWrapperList = Collections.emptyList();
        } else {
            this.lockWrapperList = Collections.unmodifiableList(this.lockWrapperList);
        }
        // invoke descendant template method
        this.doAsSelect();

        this.prepared = true;
        return this;
    }


    /*################################## blow PostgreSelect method ##################################*/


    @Override
    public final boolean requiredBrackets() {
        return !CollectionUtils.isEmpty(this.sortExpList)
                || this.rowCount > 1
                || this.offset > -1
                || !CollectionUtils.isEmpty(this.lockWrapperList);
    }

    /*################################## blow PostgreInnerQuery method ##################################*/

    @Override
    public final List<Expression<?>> distinctOnExpList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.distinctOnExpList;
    }

    @Override
    public final List<PostgreWindow> windowList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.windowList;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.modifierList;
    }

    @Override
    public final List<SelectPart> selectPartList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.selectPartList;
    }

    @Override
    public final List<Expression<?>> groupExpList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.groupExpList;
    }

    @Override
    public final List<IPredicate> havingList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.havingList;
    }

    @Override
    public final List<Expression<?>> sortExpList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.sortExpList;
    }

    @Override
    public final int offset() {
        return this.offset;
    }

    @Override
    public final int rowCount() {
        return this.rowCount;
    }

    @Override
    public final List<IPredicate> predicateList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.predicateList;
    }

    @Override
    public final List<PostgreLockWrapper> lockWrapperList() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);
        return this.lockWrapperList;
    }

    @Override
    public void clear() {
        Assert.state(this.prepared, NOT_PREPARED_MSG);

        this.modifierList = null;
        this.distinctOnExpList = null;
        this.selectPartList = null;
        this.predicateList = null;

        this.groupExpList = null;
        this.havingList = null;
        this.windowList = null;
        this.sortExpList = null;

        this.lockWrapperList = null;

    }

    /*################################## blow package method ##################################*/

    @Override
    final boolean prepared() {
        return this.prepared;
    }

    @Override
    final int tableWrapperCount() {
        return 6;
    }

    @Override
    void onAddTable(TableMeta<?> table, String tableAlias) {

    }


    /*################################## blow package template method ##################################*/

    abstract void beforeAsSelect();

    abstract void doAsSelect();

    /*################################## blow private method ##################################*/

    private PostgreSelect thisSelect() {
        return this.asSelect();
    }

    private void addTableSample(Expression<?> funcExp, @Nullable Expression<Double> repeatableFuncExp) {
        TableWrapper tableWrapper = lastTableWrapper();
        if (tableWrapper.tableAble() instanceof TableMeta) {
            if (tableWrapper instanceof PostgreTableWrapperImpl) {

                PostgreTableWrapperImpl wrapper = (PostgreTableWrapperImpl) tableWrapper;
                wrapper.tableSampleFuncList.add(funcExp);
                if (repeatableFuncExp != null) {
                    wrapper.tableSampleFuncList.add(repeatableFuncExp);
                }
                wrapper.immutable();

            } else {
                throw new IllegalStateException(String.format(
                        "TableWrapper[%s] isn't  instance of PostgreTableWrapperImpl,state error."
                        , tableWrapper.alias()));
            }
        } else {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "last table[%s] isn't TableMeta,can't add samplingMethod.", tableWrapper.alias());
        }
    }

    /*################################## blow static inner class ##################################*/

    private static final class PostgreTableWrapperImpl extends TableWrapperImpl implements PostgreTableWrapper {

        private final SQLModifier lateral;

        private List<Expression<?>> tableSampleFuncList;


        public PostgreTableWrapperImpl(TableAble tableAble, String alias, JoinType jointType
                , @Nullable SQLModifier lateral) {
            super(tableAble, alias, jointType);
            this.lateral = lateral;
            this.tableSampleFuncList = Collections.emptyList();
        }

        PostgreTableWrapperImpl(TableMeta<?> tableAble, String alias, JoinType jointType
                , List<Expression<?>> tableSampleFuncList) {
            super(tableAble, alias, jointType);
            this.lateral = null;
            this.tableSampleFuncList = tableSampleFuncList;
        }

        @Override
        public final SQLModifier lateral() {
            return this.lateral;
        }

        @Override
        public List<Expression<?>> tableSampleFuncList() {
            return this.tableSampleFuncList;
        }

        private void immutable() {
            this.tableSampleFuncList = Collections.unmodifiableList(this.tableSampleFuncList);
        }
    }

    private static final class WithQuery implements TableAble {

        private final String withQueryName;

        WithQuery(String withQueryName) {
            Assert.notNull(withQueryName, "withQueryName required");
            this.withQueryName = withQueryName;
        }

        @Override
        public void appendSQL(SQLContext context) {
            context.stringBuilder()
                    .append(" ")
                    .append(context.dml().quoteIfNeed(this.withQueryName))
            ;
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public String toString() {
            return this.withQueryName;
        }
    }

    private static final class LockWrapper implements PostgreLockWrapper {

        private final SQLModifier lockMode;

        private List<TableMeta<?>> ofTableList;

        private SQLModifier lockOption;

        private boolean prepared;

        LockWrapper(SQLModifier lockMode) {
            this.lockMode = lockMode;
        }

        @Override
        public SQLModifier lockMode() {
            return this.lockMode;
        }

        @Override
        public List<TableMeta<?>> lockTableList() {
            Assert.state(this.prepared, "not prepared");
            return this.ofTableList;
        }

        @Nullable
        @Override
        public SQLModifier lockOption() {
            return this.lockOption;
        }

        void addOfTable(TableMeta<?> ofTable) {
            Assert.state(!this.prepared, "ofTableList prepared");
            this.ofTableList = Collections.singletonList(ofTable);
            this.prepared = true;
        }

        void addOfTables(List<TableMeta<?>> ofTableList) {
            Assert.state(!this.prepared, "ofTableList prepared");
            if (this.ofTableList == null) {
                this.ofTableList = new ArrayList<>(ofTableList.size());
            }
            this.ofTableList.addAll(ofTableList);
            this.prepared = true;
        }

        void lockOption(LockOptions lockOption) {
            Assert.state(this.lockOption == null, "lockOption not null");
            this.lockOption = lockOption;
        }

    }

    private enum LockOptions implements SQLModifier {
        NOWAIT("NOWAIT"),
        SKIP_LOCKED("SKIP LOCKED");
        private final String keyWords;

        LockOptions(String keyWords) {
            this.keyWords = keyWords;
        }

        @Override
        public String render() {
            return this.keyWords;
        }
    }


}
