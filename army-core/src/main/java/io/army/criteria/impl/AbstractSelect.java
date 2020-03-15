package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerQueryAble;
import io.army.dialect.SQLDialect;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class AbstractSelect<C> extends AbstractSQLAble implements
        Select, Select.SelectionAble<C>, Select.FromAble<C>, Select.JoinAble<C>,
        Select.OnAble<C>, Select.WhereAndAble<C>, Select.LimitAble<C> ,InnerQueryAble {

    private static final class TableWrapperImpl implements TableWrapper {

        private final TableAble tableAble;

        private final String alias;

        private final JoinType jointType;

        private final List<IPredicate> predicateList = new ArrayList<>();

        TableWrapperImpl(TableAble tableAble, String alias, JoinType jointType) {
            this.tableAble = tableAble;
            this.alias = alias;
            this.jointType = jointType;
        }

        public TableAble getTableAble() {
            return tableAble;
        }

        public String getAlias() {
            return alias;
        }

        public JoinType getJointType() {
            return jointType;
        }

        public List<IPredicate> getPredicateList() {
            return predicateList;
        }
    }


    private final C criteria;

    private List<SQLModifier> modifierList = new ArrayList<>(2);

    private List<Selection> selectionList = new ArrayList<>();

    private List<TableWrapper> tableWrapperList = new ArrayList<>(6);

    private List<IPredicate> predicateList = new ArrayList<>();

    private List<SortExpression<?>> groupExpList = new ArrayList<>(3);

    private List<IPredicate> havingList = new ArrayList<>(3);

    private List<SortExpression<?>> sortExpList = new ArrayList<>(3);

    private int offset = -1;

    private int rowCount = -1;

    private LockMode lockMode;

    private boolean prepared = false;


    AbstractSelect(C criteria) {
        this.criteria = criteria;
    }



    /*################################## blow SelectListAble method ##################################*/

    @Override
    public final <T extends IDomain> Select.FromAble<C> select(Distinct distinct, TableMeta<T> tableMeta) {
        Assert.state(this.modifierList.isEmpty(), "select clause ended.");

        this.modifierList.add(distinct);
        appendSelection(tableMeta);
        return this;
    }

    @Override
    public final <T extends IDomain> Select.FromAble<C> select(TableMeta<T> tableMeta) {
        appendSelection(tableMeta);
        return this;
    }

    @Override
    public final Select.FromAble<C> select(Distinct distinct, List<Selection> selectionList) {
        Assert.state(this.modifierList.isEmpty(), "select clause ended.");
        Assert.state(this.selectionList.isEmpty(), "select clause ended.");

        this.modifierList.add(distinct);
        this.selectionList.addAll(selectionList);
        return this;
    }

    @Override
    public final Select.FromAble<C> select(List<Selection> selectionList) {
        Assert.state(this.selectionList.isEmpty(), "select clause ended.");
        this.selectionList.addAll(selectionList);
        return this;
    }

    @Override
    public final Select.FromAble<C> select(Distinct distinct, Function<C, List<Selection>> function) {
        return select(distinct, function.apply(this.criteria));
    }

    @Override
    public final Select.FromAble<C> select(Function<C, List<Selection>> function) {
        return select(function.apply(this.criteria));
    }

    /*################################## blow FromAble method ##################################*/

    @Override
    public final <T extends IDomain> Select.JoinAble<C> from(TableMeta<T> tableAble, String tableAlias) {
        Assert.state(this.tableWrapperList.isEmpty(), "form clause ended.");
        this.tableWrapperList.add(new TableWrapperImpl(tableAble, tableAlias, JoinType.NONE));
        return this;
    }

    /*################################## blow JoinAble method ##################################*/

    @Override
    public Select.OnAble<C> leftJoin(TableAble tableAble, String tableAlias) {
        return appendTableWrapper(tableAble, tableAlias, JoinType.LEFT);
    }

    @Override
    public final Select.OnAble<C> join(TableAble tableAble, String tableAlias) {
        return appendTableWrapper(tableAble, tableAlias, JoinType.JOIN);
    }

    @Override
    public final Select.OnAble<C> rightJoin(TableAble tableAble, String tableAlias) {
        return appendTableWrapper(tableAble, tableAlias, JoinType.RIGHT);
    }

    /*################################## blow OnAble method ##################################*/

    @Override
    public final Select.JoinAble<C> on(List<IPredicate> predicateList) {
        Assert.state(!this.tableWrapperList.isEmpty(), "no form clause.");
        TableWrapperImpl tableWrapper = (TableWrapperImpl) this.tableWrapperList.get(this.tableWrapperList.size() - 1);

        Assert.state(tableWrapper.predicateList.isEmpty()
                , () -> String.format("on clause of %s ended", tableWrapper.alias));

        tableWrapper.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final Select.JoinAble<C> on(IPredicate predicate) {
        return on(Collections.singletonList(predicate));
    }

    @Override
    public final Select.JoinAble<C> on(Function<C, List<IPredicate>> function) {
        return on(function.apply(this.criteria));
    }

    /*################################## blow WhereAble method ##################################*/

    @Override
    public final Select.GroupByAble<C> where(List<IPredicate> predicateList) {
        Assert.state(!this.tableWrapperList.isEmpty(), "no from clause.");
        Assert.state(this.predicateList.isEmpty(), "where ended.");
        this.predicateList.addAll(predicateList);
        return this;
    }

    @Override
    public final Select.GroupByAble<C> where(Function<C, List<IPredicate>> function) {
        return where(function.apply(this.criteria));
    }

    @Override
    public final Select.WhereAndAble<C> where(IPredicate predicate) {
        where(Collections.singletonList(predicate));
        return this;
    }

    /*################################## blow WhereAndAble method ##################################*/

    @Override
    public final Select.WhereAndAble<C> and(IPredicate predicate) {
        Assert.state(!this.predicateList.isEmpty(), "no where clause.");
        this.predicateList.add(predicate);
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> and(Function<C, IPredicate> function) {
        return and(function.apply(this.criteria));
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            and(predicate);
        }
        return this;
    }

    @Override
    public final Select.WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function) {
        if (testPredicate.test(this.criteria)) {
            and(function.apply(this.criteria));
        }
        return this;
    }

    /*################################## blow GroupByAble method ##################################*/

    @Override
    public final Select.HavingAble<C> groupBy(SortExpression<?> groupExp) {
        Assert.state(this.groupExpList.isEmpty(), "group by clause ended.");
        groupExpList.add(groupExp);
        return this;
    }

    @Override
    public final Select.HavingAble<C> groupBy(Function<C, List<SortExpression<?>>> function) {
        Assert.state(this.groupExpList.isEmpty(), "group by clause ended.");
        groupExpList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.HavingAble<C> ifGroupBy(Predicate<C> predicate, SortExpression<?> groupExp) {
        if (predicate.test(this.criteria)) {
            groupBy(groupExp);
        }
        return this;
    }

    @Override
    public final Select.HavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            groupBy(expFunction);
        }
        return this;
    }

    /*################################## blow HavingAble method ##################################*/

    @Override
    public final Select.OrderByAble<C> having(List<IPredicate> predicateList) {
        Assert.state(!this.groupExpList.isEmpty(), "no group by clause.");
        Assert.state(this.havingList.isEmpty(), "having clause ended.");
        this.havingList.addAll(predicateList);
        return this;
    }

    @Override
    public final Select.OrderByAble<C> having(Function<C, List<IPredicate>> function) {
        return having(function.apply(this.criteria));
    }

    @Override
    public final Select.OrderByAble<C> having(IPredicate predicate) {
        return having(Collections.singletonList(predicate));
    }

    @Override
    public final Select.OrderByAble<C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList) {
        if (predicate.test(this.criteria)) {
            having(predicateList);
        }
        return this;
    }

    @Override
    public final Select.OrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function) {
        if (predicate.test(this.criteria)) {
            having(function.apply(this.criteria));
        }
        return this;
    }

    @Override
    public final Select.OrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate) {
        if (testPredicate.test(this.criteria)) {
            having(predicate);
        }
        return this;
    }

    /*################################## blow OrderByAble method ##################################*/

    @Override
    public final Select.LimitAble<C> orderBy(SortExpression<?> groupExp) {
        Assert.state(this.sortExpList.isEmpty(), "order by clause ended.");
        this.sortExpList.add(groupExp);
        return this;
    }

    @Override
    public final Select.LimitAble<C> orderBy(Function<C, List<SortExpression<?>>> function) {
        Assert.state(this.sortExpList.isEmpty(), "order by clause ended.");
        this.sortExpList.addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public final Select.LimitAble<C> ifOrderBy(Predicate<C> predicate, SortExpression<?> groupExp) {
        if (predicate.test(this.criteria)) {
            orderBy(groupExp);
        }
        return this;
    }

    @Override
    public final Select.LimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction) {
        if (predicate.test(this.criteria)) {
            orderBy(expFunction);
        }
        return this;
    }

    /*################################## blow LimitAble method ##################################*/

    @Override
    public final Select.LockAble<C> limit(int rowCount) {
        Assert.state(this.rowCount < 0, "limit clause ended.");
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Select.LockAble<C> limit(int offset, int rowCount) {
        Assert.state(this.offset < 0, "limit clause ended.");
        Assert.state(this.rowCount < 0, "limit clause ended.");
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final Select.LockAble<C> limit(Function<C, Pair<Integer, Integer>> function) {
        Pair<Integer, Integer> pair = function.apply(this.criteria);
        int offset = -1, rowCount = -1;
        if (pair.getFirst() != null) {
            offset = pair.getFirst();
        }
        if (pair.getSecond() != null) {
            rowCount = pair.getSecond();
        }
        limit(offset, rowCount);
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(rowCount);
        }
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        if (predicate.test(this.criteria)) {
            limit(offset, rowCount);
        }
        return this;
    }

    @Override
    public final Select.LockAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        if (predicate.test(this.criteria)) {
            limit(function);
        }
        return this;
    }

    /*################################## blow LockAble method ##################################*/

    @Override
    public Select lock(LockMode lockMode) {
        Assert.state(this.lockMode == null, "lock clause ended.");
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public Select lock(Function<C, LockMode> function) {
        return lock(function.apply(this.criteria));
    }

    @Override
    public Select ifLock(Predicate<C> predicate, LockMode lockMode) {
        if (predicate.test(this.criteria)) {
            lock(lockMode);
        }
        return this;
    }

    @Override
    public Select ifLock(Predicate<C> predicate, Function<C, LockMode> function) {
        if (predicate.test(this.criteria)) {
            lock(function);
        }
        return this;
    }

    /*################################## blow AbstractSQLAble method ##################################*/

    @Override
    public String debugSQL(SQLDialect sqlDialect, Visible visible) {
       /* SessionFactory sessionFactory = createSessionFactory(tableMeta.schema(), sqlDialect);
        List<SQLWrapper> sqlWrapperList = sessionFactory.dialect().update(this, visible);
        return printSQL(sqlWrapperList, sessionFactory.dialect());*/
        return "";
    }

    /*################################## blow InnerQueryAble method ##################################*/

    @Override
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final List<Selection> selectionList() {
        return this.selectionList;
    }

    @Override
    public final List<TableWrapper> tableWrapperList() {
        return this.tableWrapperList;
    }

    @Override
    public final List<IPredicate> predicateList() {
        return this.predicateList;
    }

    @Override
    public final List<SortExpression<?>> groupExpList() {
        return this.groupExpList;
    }

    @Override
    public final List<IPredicate> havingList() {
        return this.havingList;
    }

    @Override
    public final List<SortExpression<?>> sortExpList() {
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
    public final LockMode lockMode() {
        return this.lockMode;
    }

    @Override
    public final void prepare() {
        if (prepared) {
            return;
        }
        this.modifierList = Collections.unmodifiableList(this.modifierList);
        this.selectionList = Collections.unmodifiableList(this.selectionList);
        this.tableWrapperList = Collections.unmodifiableList(this.tableWrapperList);
        this.predicateList = Collections.unmodifiableList(this.predicateList);

        this.groupExpList = Collections.unmodifiableList(this.groupExpList);
        this.havingList = Collections.unmodifiableList(this.havingList);
        this.sortExpList = Collections.unmodifiableList(this.sortExpList);

        this.prepared = true;
    }

    /*################################## blow private method ##################################*/

    private OnAble<C> appendTableWrapper(TableAble tableAble, String tableAlias, JoinType joinType) {
        Assert.state(!this.tableWrapperList.isEmpty(), "no form clause.");
        this.tableWrapperList.add(new TableWrapperImpl(tableAble, tableAlias, joinType));
        return this;
    }

    private void appendSelection(TableMeta<?> tableMeta) {
        Assert.state(this.selectionList.isEmpty(), "select clause ended.");
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
            this.selectionList.add(fieldMeta.as());
        }
    }


}
