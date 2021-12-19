package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SortPart;
import io.army.criteria.impl.inner.mysql._MySQL57SubQuery;
import io.army.criteria.mysql.*;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class MySQL57SubQueries<Q extends MySQL57SubQuery, C> extends AbstractMySQL57Query<Q, C>
        implements _MySQL57SubQuery, MySQL57SubQuery {


    static <C> MySQL57SubQueries<MySQL57SubQuery, C> build(C criteria) {
        if (criteria != CriteriaContextStack.pop()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new MySQL57SubQueryImpl<>(criteria);
    }

    static <C> MySQL57SubQueries<MySQL57RowSubQuery, C> buildRowSubQuery(C criteria) {
        if (criteria != CriteriaContextStack.pop()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new MySQL57RowSubQueryImpl<>(criteria);
    }


    static <E, C> MySQL57ColumnSubQuery.MySQLColumnSelectionSpec<E, C> buildColumnSubQuery(Class<E> columnType, C criteria) {
        if (criteria != CriteriaContextStack.pop()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new MySQL57ColumnSubQueryImpl<>(criteria, columnType);
    }

    static <E, C> MySQL57ScalarSubQueryAdaptor<E, C> buildScalarSubQuery(Class<E> javaType, MappingType mappingType
            , C criteria) {
        if (criteria != CriteriaContextStack.pop()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new MySQL57ScalarSubQueryAdaptor<>(javaType, mappingType, criteria);
    }

    private Map<String, Selection> selectionMap;

    private MySQL57SubQueries(C criteria) {
        super(criteria);
    }

    @Override
    public String toString() {
        return "#MySQL57SubQuery@" + System.identityHashCode(this);
    }

    @Override
    public final Selection selection(String derivedFieldName) {
        if (this.selectionMap == null) {
            this.selectionMap = CriteriaUtils.createSelectionMap(selectPartList());
        }
        Selection s = this.selectionMap.get(derivedFieldName);
        if (s == null) {
            throw new CriteriaException(ErrorCode.NO_SELECTION
                    , "not found Selection[%s] from SubQuery.", derivedFieldName);
        }
        return s;
    }

    @Override
    public final void appendSql(_SqlContext context) {
        context.dialect().subQuery(this, context);
    }

    @Override
    final void onMySQLAddTable(TableMeta<?> table, String tableAlias) {
        CriteriaContextStack.pop()
                .onAddTable(table, tableAlias);
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextStack.pop()
                .onAddSubQuery(subQuery, subQueryAlias);
    }

    @Override
    final void internalAsSelect() {
        // no-op
    }

    @Override
    final void internalClear() {
        this.selectionMap = null;
    }

    /*################################## blow private static inner class ##################################*/

    private static final class MySQL57SubQueryImpl<C> extends MySQL57SubQueries<MySQL57SubQuery, C> {

        private MySQL57SubQueryImpl(C criteria) {
            super(criteria);
        }
    }

    private static final class MySQL57RowSubQueryImpl<C> extends MySQL57SubQueries<MySQL57RowSubQuery, C>
            implements MySQL57RowSubQuery {

        private MySQL57RowSubQueryImpl(C criteria) {
            super(criteria);
        }

    }

    private static final class MySQL57ColumnSubQueryImpl<E, C> extends MySQL57SubQueries<MySQL57ColumnSubQuery<E>, C>
            implements MySQL57ColumnSubQuery<E>, MySQL57ColumnSubQuery.MySQLColumnSelectionSpec<E, C> {


        private MySQL57ColumnSubQueryImpl(C criteria, Class<E> elementClass) {
            super(criteria);
        }

        @Override
        public final MySQLFromSpec<MySQL57ColumnSubQuery<E>, C> select(Distinct distinct, Selection selection) {
            doSelectClause(distinct, selection);
            return this;
        }

        @Override
        public final MySQLFromSpec<MySQL57ColumnSubQuery<E>, C> select(Selection selection) {
            doSelectClause((Distinct) null, selection);
            return this;
        }


    }


    /**
     * this class is a implementation of {@link MySQL57ScalarSubQuery}.
     *
     * @param <E> {@link MySQL57ScalarSubQuery}'s result Java Type.
     * @param <C> custom object for Dynamic SQL.
     */
    private static final class MySQL57ScalarSubQueryAdaptor<E, C> extends AbstractExpression<E>
            implements MySQL57ScalarSubQuery<E>, MySQLSelectPartSpec<MySQL57ScalarSubQuery<E>, C>
            , MySQLFromSpec<MySQL57ScalarSubQuery<E>, C>, MySQLTableRouteJoinSpec<MySQL57ScalarSubQuery<E>, C>
            , MySQLWhereAndSpec<MySQL57ScalarSubQuery<E>, C>, MySQLHavingSpec<MySQL57ScalarSubQuery<E>, C>
            , MySQL57ScalarSubQuery.MySQLScalarSelectionSpec<E, C>
            , MySQL57Query.MySQLWithRollUpSpec<MySQL57ScalarSubQuery<E>, C>
            , _MySQL57SubQuery {


        private final MappingType mappingType;

        private final MySQL57ColumnSubQueryImpl<E, C> actualSelect;

        private final MySQLTableRouteOnSpecImpl<E, C> tableRouteSpec;

        private MySQL57ScalarSubQueryAdaptor(Class<E> javaType, MappingType mappingType, C criteria) {
            Assert.isAssignable(javaType, mappingType.javaType(), "javaType and paramMeta not match.");
            this.mappingType = mappingType;
            this.actualSelect = new MySQL57ColumnSubQueryImpl<>(criteria, javaType);
            this.tableRouteSpec = new MySQLTableRouteOnSpecImpl<>(this);
        }


        /*################################## blow AbstractExpression method ##################################*/

        @Override
        public final void appendSql(_SqlContext context) {
            this.actualSelect.appendSql(context);
        }

        @Override
        public final String toString() {
            return "#MySQL57ScalarSubQuery:" + System.identityHashCode(this);
        }

        @Override
        public final MappingType mappingType() {
            return mappingType;
        }

        @Override
        public final boolean containsSubQuery() {
            // always true.
            return true;
        }

        /*################################## blow MySQL57ScalarSubQuery<E> method ##################################*/

        @Override
        public final List<SelectPart> selectPartList() {
            return this.actualSelect.selectPartList();
        }


        @Override
        public final Selection selection(String derivedFieldName) {
            return this.actualSelect.selection(derivedFieldName);
        }

        @Override
        public final boolean requiredBrackets() {
            return this.actualSelect.requiredBrackets();
        }

        @Override
        public final <S extends SelectPart> MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Distinct distinct
                , Function<C, List<S>> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <S extends SelectPart> MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Function<C, List<S>> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Distinct distinct, SelectPart selectPart) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(SelectPart selectPart) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <S extends SelectPart> MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Distinct distinct
                , List<S> selectPartList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <S extends SelectPart> MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(List<S> selectPartList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Distinct distinct, Selection selection) {
            this.actualSelect.doSelectClause(distinct, selection);
            return this;
        }

        @Override
        public final MySQLFromSpec<MySQL57ScalarSubQuery<E>, C> select(Selection selection) {
            this.actualSelect.doSelectClause((Distinct) null, selection);
            return this;
        }

        @Override
        public final MySQLTableRouteJoinSpec<MySQL57ScalarSubQuery<E>, C> from(TableMeta<?> tableMeta, String tableAlias) {
            this.actualSelect.from(tableMeta, tableAlias);
            return this;
        }

        @Override
        public final MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> from(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.from(function, subQueryAlia);
            return this;
        }

        @Override
        public final MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> route(int databaseIndex, int tableIndex) {
            this.actualSelect.route(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public final MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> route(int tableIndex) {
            this.actualSelect.route(-1, tableIndex);
            return this;
        }

        @Override
        public final MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> ifIndexHintList(
                Function<C, List<MySQL57IndexHint>> function) {
            this.actualSelect.ifIndexHintList(function);
            return this;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> leftJoin(TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.leftJoin(tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> leftJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.leftJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> ifLeftJoin(Predicate<C> predicate
                , TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.ifLeftJoin(predicate, tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> ifLeftJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.ifLeftJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> join(TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.join(tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> join(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.join(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> ifJoin(Predicate<C> predicate
                , TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.ifJoin(predicate, tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> ifJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.ifJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> rightJoin(TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.rightJoin(tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> rightJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.rightJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> ifRightJoin(Predicate<C> predicate
                , TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.ifRightJoin(predicate, tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> ifRightJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.ifRightJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> straightJoin(TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.straightJoin(tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> straightJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.straightJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> ifStraightJoin(Predicate<C> predicate
                , TableMeta<?> tableMeta, String tableAlias) {
            this.actualSelect.ifStraightJoin(predicate, tableMeta, tableAlias);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> ifStraightJoin(Function<C, SubQuery> function
                , String subQueryAlia) {
            this.actualSelect.ifStraightJoin(function, subQueryAlia);
            return this.tableRouteSpec;
        }

        @Override
        public final MySQLGroupBySpec<MySQL57ScalarSubQuery<E>, C> where(List<IPredicate> predicateList) {
            this.actualSelect.where(predicateList);
            return this;
        }

        @Override
        public final MySQLGroupBySpec<MySQL57ScalarSubQuery<E>, C> ifWhere(Function<C, List<IPredicate>> function) {
            this.actualSelect.ifWhere(function);
            return this;
        }

        @Override
        public final MySQLWhereAndSpec<MySQL57ScalarSubQuery<E>, C> where(IPredicate predicate) {
            this.actualSelect.where(predicate);
            return this;
        }

        @Override
        public final MySQLWhereAndSpec<MySQL57ScalarSubQuery<E>, C> and(IPredicate predicate) {
            this.actualSelect.and(predicate);
            return this;
        }

        @Override
        public final MySQLWhereAndSpec<MySQL57ScalarSubQuery<E>, C> ifAnd(@Nullable IPredicate predicate) {
            this.actualSelect.ifAnd(predicate);
            return this;
        }

        @Override
        public final MySQLWhereAndSpec<MySQL57ScalarSubQuery<E>, C> ifAnd(Function<C, IPredicate> function) {
            this.actualSelect.ifAnd(function);
            return this;
        }

        @Override
        public final MySQLWithRollUpSpec<MySQL57ScalarSubQuery<E>, C> groupBy(SortPart sortPart) {
            this.actualSelect.groupBy(sortPart);
            return this;
        }

        @Override
        public final MySQLWithRollUpSpec<MySQL57ScalarSubQuery<E>, C> groupBy(SortPart sortPart1, SortPart sortPart2) {
            this.actualSelect.groupBy(sortPart1, sortPart2);
            return this;
        }

        @Override
        public final MySQLWithRollUpSpec<MySQL57ScalarSubQuery<E>, C> groupBy(List<SortPart> sortPartList) {
            this.actualSelect.groupBy(sortPartList);
            return this;
        }

        @Override
        public final MySQLWithRollUpSpec<MySQL57ScalarSubQuery<E>, C> groupBy(Function<C, List<SortPart>> function) {
            this.actualSelect.groupBy(function);
            return this;
        }

        @Override
        public final MySQLWithRollUpSpec<MySQL57ScalarSubQuery<E>, C> ifGroupBy(Function<C, List<SortPart>> function) {
            this.actualSelect.ifGroupBy(function);
            return this;
        }

        @Override
        public final MySQLHavingSpec<MySQL57ScalarSubQuery<E>, C> withRollUp() {
            this.actualSelect.withRollUp();
            return this;
        }

        @Override
        public final MySQLHavingSpec<MySQL57ScalarSubQuery<E>, C> withRollUp(Predicate<C> predicate) {
            this.actualSelect.withRollUp(predicate);
            return this;
        }

        @Override
        public final MySQLOrderBySpec<MySQL57ScalarSubQuery<E>, C> having(IPredicate predicate) {
            this.actualSelect.having(predicate);
            return this;
        }

        @Override
        public final MySQLOrderBySpec<MySQL57ScalarSubQuery<E>, C> having(List<IPredicate> predicateList) {
            this.actualSelect.having(predicateList);
            return this;
        }

        @Override
        public final MySQLOrderBySpec<MySQL57ScalarSubQuery<E>, C> having(Function<C, List<IPredicate>> function) {
            this.actualSelect.having(function);
            return this;
        }

        @Override
        public final MySQLOrderBySpec<MySQL57ScalarSubQuery<E>, C> ifHaving(Function<C, List<IPredicate>> function) {
            this.actualSelect.ifHaving(function);
            return this;
        }

        @Override
        public final MySQLLimitSpec<MySQL57ScalarSubQuery<E>, C> orderBy(SortPart sortPart) {
            this.actualSelect.orderBy(sortPart);
            return this;
        }

        @Override
        public final MySQLLimitSpec<MySQL57ScalarSubQuery<E>, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
            this.actualSelect.orderBy(sortPart1, sortPart2);
            return this;
        }

        @Override
        public final MySQLLimitSpec<MySQL57ScalarSubQuery<E>, C> orderBy(List<SortPart> sortPartList) {
            this.actualSelect.orderBy(sortPartList);
            return this;
        }

        @Override
        public final MySQLLimitSpec<MySQL57ScalarSubQuery<E>, C> orderBy(Function<C, List<SortPart>> function) {
            this.actualSelect.orderBy(function);
            return this;
        }

        @Override
        public final MySQLLimitSpec<MySQL57ScalarSubQuery<E>, C> ifOrderBy(Function<C, List<SortPart>> function) {
            this.actualSelect.ifOrderBy(function);
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> limit(int rowCount) {
            this.actualSelect.limit(rowCount);
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> limit(int offset, int rowCount) {
            this.actualSelect.limit(offset, rowCount);
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> ifLimit(Function<C, LimitOption> function) {
            this.actualSelect.ifLimit(function);
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> ifLimit(Predicate<C> predicate, int rowCount) {
            this.actualSelect.ifLimit(predicate, rowCount);
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
            this.actualSelect.ifLimit(predicate, offset, rowCount);
            return this;
        }


        @Override
        public final QuerySpec<MySQL57ScalarSubQuery<E>> forUpdate() {
            this.actualSelect.forUpdate();
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> ifForUpdate(Predicate<C> predicate) {
            this.actualSelect.ifForUpdate(predicate);
            return this;
        }

        @Override
        public final QuerySpec<MySQL57ScalarSubQuery<E>> lockInShareMode() {
            this.actualSelect.lockInShareMode();
            return this;
        }

        @Override
        public final MySQLLockSpec<MySQL57ScalarSubQuery<E>, C> ifLockInShareMode(Predicate<C> predicate) {
            this.actualSelect.ifLockInShareMode(predicate);
            return this;
        }

        @Override
        public final UnionSpec<MySQL57ScalarSubQuery<E>, C> bracketsQuery() {
            return ComposeQueries.brackets(this.actualSelect.criteria, asQuery());
        }

        @Override
        public final UnionSpec<MySQL57ScalarSubQuery<E>, C> union(Function<C, MySQL57ScalarSubQuery<E>> function) {
            return ComposeQueries.compose(this.actualSelect.criteria, asQuery(), UnionType.UNION, function);
        }

        @Override
        public final UnionSpec<MySQL57ScalarSubQuery<E>, C> unionAll(Function<C, MySQL57ScalarSubQuery<E>> function) {
            return ComposeQueries.compose(this.actualSelect.criteria, asQuery(), UnionType.UNION_ALL, function);
        }

        @Override
        public final UnionSpec<MySQL57ScalarSubQuery<E>, C> unionDistinct(Function<C, MySQL57ScalarSubQuery<E>> function) {
            return ComposeQueries.compose(this.actualSelect.criteria, asQuery(), UnionType.UNION_DISTINCT, function);
        }

        @Override
        public final MySQL57ScalarSubQuery<E> asQuery() {
            this.actualSelect.asQuery();
            return this;
        }

        @Override
        public final void prepared() {
            this.actualSelect.prepared();
        }

        @Override
        public final void clear() {
            this.actualSelect.clear();
        }

        @Nullable
        @Override
        public final SQLModifier lockMode() {
            return this.actualSelect.lockMode();
        }

        @Override
        public final List<_Predicate> predicateList() {
            return this.actualSelect.predicateList();
        }

        @Override
        public final List<_SortPart> groupPartList() {
            return this.actualSelect.groupPartList();
        }

        @Override
        public final List<_Predicate> havingList() {
            return this.actualSelect.havingList();
        }

        @Override
        public final List<_SortPart> orderPartList() {
            return this.actualSelect.orderPartList();
        }

        @Override
        public final int offset() {
            return this.actualSelect.offset();
        }

        @Override
        public final int rowCount() {
            return this.actualSelect.rowCount();
        }

        @Override
        public final List<SQLModifier> modifierList() {
            return this.actualSelect.modifierList();
        }

        @Override
        public final List<? extends TableWrapper> tableWrapperList() {
            return this.actualSelect.tableWrapperList();
        }

        @Override
        public final boolean groupByWithRollUp() {
            return this.actualSelect.groupByWithRollUp();
        }


    }


    /**
     * Design this inner class for {@link #route(int)} and {@link #route(int, int)} don't crash with
     * ScalarSubQueryAdaptor class
     */
    private static final class MySQLTableRouteOnSpecImpl<E, C> implements MySQL57Query
            , MySQLTableRouteOnSpec<MySQL57ScalarSubQuery<E>, C> {

        private final MySQL57ScalarSubQueryAdaptor<E, C> mysql57Query;

        private MySQLTableRouteOnSpecImpl(MySQL57ScalarSubQueryAdaptor<E, C> mysql57Query) {
            this.mysql57Query = mysql57Query;
        }

        @Override
        public boolean requiredBrackets() {
            return this.mysql57Query.requiredBrackets();
        }

        @Override
        public MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> on(List<IPredicate> predicateList) {
            this.mysql57Query.actualSelect.tableRouteOnSpec.on(predicateList);
            return this.mysql57Query;
        }

        @Override
        public MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> on(IPredicate predicate) {
            this.mysql57Query.actualSelect.tableRouteOnSpec.on(predicate);
            return this.mysql57Query;
        }

        @Override
        public MySQLJoinSpec<MySQL57ScalarSubQuery<E>, C> on(Function<C, List<IPredicate>> function) {
            this.mysql57Query.actualSelect.tableRouteOnSpec.on(function);
            return this.mysql57Query;
        }

        @Override
        public MySQLIndexHintOnSpec<MySQL57ScalarSubQuery<E>, C> route(int databaseIndex, int tableIndex) {
            this.mysql57Query.actualSelect.tableRouteOnSpec.route(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public MySQLIndexHintOnSpec<MySQL57ScalarSubQuery<E>, C> route(int tableIndex) {
            this.mysql57Query.actualSelect.tableRouteOnSpec.route(-1, tableIndex);
            return this;
        }

        @Override
        public MySQLOnSpec<MySQL57ScalarSubQuery<E>, C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function) {
            this.mysql57Query.actualSelect.tableRouteOnSpec.ifIndexHintList(function);
            return this;
        }

        @Override
        public void prepared() {
            this.mysql57Query.actualSelect.prepared();
        }
    }

}
