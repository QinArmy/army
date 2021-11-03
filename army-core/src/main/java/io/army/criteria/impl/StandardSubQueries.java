package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerStandardSubQuery;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class StandardSubQueries<Q extends Query, C> extends AbstractStandardQuery<Q, C>
        implements InnerStandardSubQuery, SubQuery {

    static <C> StandardSubQueries<SubQuery, C> build(C criteria) {
        if (criteria != CriteriaContextHolder.getContext()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new StandardSubQuery<>(criteria);
    }

    static <C> StandardSubQueries<RowSubQuery, C> buildRowSubQuery(C criteria) {
        if (criteria != CriteriaContextHolder.getContext()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new StandardRowSubQuery<>(criteria);
    }


    static <E, C> StandardColumnSubQuery<E, C> buildColumnSubQuery(Class<E> columnType, C criteria) {
        if (criteria != CriteriaContextHolder.getContext()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new StandardColumnSubQuery<>(criteria, columnType);
    }

    static <E, C> ScalarSubQueryAdaptor<E, C> buildScalarSubQuery(Class<E> javaType, MappingType mappingType
            , C criteria) {
        if (criteria != CriteriaContextHolder.getContext()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new ScalarSubQueryAdaptor<>(javaType, mappingType, criteria);
    }

    static void assertStandardSubQuery(InnerStandardSubQuery subQuery) {
        if (subQuery instanceof ScalarSubQuery) {
            if (!(subQuery instanceof ScalarSubQueryAdaptor)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                        , ScalarSubQueryAdaptor.class.getName()));
            }
        } else if (subQuery instanceof ColumnSubQuery) {
            if (!(subQuery instanceof StandardColumnSubQuery)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                        , StandardColumnSubQuery.class.getName()));
            }
        } else if (subQuery instanceof RowSubQuery) {
            if (!(subQuery instanceof StandardRowSubQuery)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                        , StandardRowSubQuery.class.getName()));
            }
        } else if (!(subQuery instanceof StandardSubQuery)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                    , StandardSubQuery.class.getName()));
        }
    }


    private Map<String, Selection> selectionMap;

    private StandardSubQueries(C criteria) {
        super(criteria);
    }


    @Override
    public final String toString() {
        String subQueryType;
        if (this instanceof ScalarSubQuery) {
            subQueryType = "#ScalarSubQuery@";
        } else if (this instanceof RowSubQuery) {
            subQueryType = "#RowSubQuery@";
        } else if (this instanceof ColumnSubQuery) {
            subQueryType = "#ColumnSubQuery@";
        } else {
            subQueryType = "#SubQuery@";
        }

        return subQueryType + System.identityHashCode(this);
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
    public final void appendSQL(SQLContext context) {
        context.dql().subQuery(this, context);
    }


    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        CriteriaContextHolder.getContext()
                .onAddTable(table, tableAlias);
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        CriteriaContextHolder.getContext()
                .onAddSubQuery(subQuery, subQueryAlias);
    }



    /*################################## blow package method ##################################*/

    @Override
    final void internalAsSelect() {
        // no-op
    }

    @Override
    final void internalClear() {
        this.selectionMap = null;
    }


    /*################################## blow private static inner class ##################################*/

    private static final class StandardSubQuery<C> extends StandardSubQueries<SubQuery, C> {

        private StandardSubQuery(C criteria) {
            super(criteria);
        }
    }

    private static final class StandardRowSubQuery<C> extends StandardSubQueries<RowSubQuery, C>
            implements RowSubQuery {

        private StandardRowSubQuery(C criteria) {
            super(criteria);
        }

    }

    private static final class StandardColumnSubQuery<E, C> extends StandardSubQueries<ColumnSubQuery<E>, C>
            implements ColumnSubQuery<E>, ColumnSubQuery.ColumnSelectionSpec<E, C> {


        private StandardColumnSubQuery(C criteria, Class<E> elementClass) {
            super(criteria);
        }

        @Override
        public final FromSpec<ColumnSubQuery<E>, C> select(Distinct distinct, Selection selection) {
            doSelectClause(distinct, selection);
            return this;
        }

        @Override
        public final FromSpec<ColumnSubQuery<E>, C> select(Selection selection) {
            doSelectClause((Distinct) null, selection);
            return this;
        }


    }


    /**
     * this class is a implementation of {@link ScalarSubQuery}.
     *
     * @param <E> {@link ScalarSubQuery}'s result Java Type.
     * @param <C> custom object for Dynamic SQL.
     */
    private static final class ScalarSubQueryAdaptor<E, C> extends AbstractExpression<E> implements ScalarSubQuery<E>
            , SelectPartSpec<ScalarSubQuery<E>, C>, FromSpec<ScalarSubQuery<E>, C>
            , TableRouteJoinSpec<ScalarSubQuery<E>, C>, WhereAndSpec<ScalarSubQuery<E>, C>
            , HavingSpec<ScalarSubQuery<E>, C>, ScalarSubQuery.ScalarSelectionSpec<E, C>
            , InnerStandardSubQuery {


        private final MappingType mappingType;

        private final StandardColumnSubQuery<E, C> actualSelect;

        private final ScalarOnClauseImpl<E, C> onClauseImpl;

        private ScalarSubQueryAdaptor(Class<E> javaType, MappingType mappingType, C criteria) {
            Assert.isAssignable(javaType, mappingType.javaType(), "javaType and paramMeta not match.");
            this.mappingType = mappingType;
            this.actualSelect = new StandardColumnSubQuery<>(criteria, javaType);
            this.onClauseImpl = new ScalarOnClauseImpl<>(this);
        }


        /*################################## blow AbstractExpression method ##################################*/

        @Override
        public final void appendSQL(SQLContext context) {
            this.actualSelect.appendSQL(context);
        }

        @Override
        public final String toString() {
            return "#ScalarSubQuery:" + System.identityHashCode(this);
        }

        @Override
        public final MappingType mappingMeta() {
            return mappingType;
        }

        @Override
        public final boolean containsSubQuery() {
            // always true.
            return true;
        }

        /*################################## blow ScalarSubQuery<E> method ##################################*/

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
        public final <S extends SelectPart> FromSpec<ScalarSubQuery<E>, C> select(Distinct distinct
                , Function<C, List<S>> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <S extends SelectPart> FromSpec<ScalarSubQuery<E>, C> select(Function<C, List<S>> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final FromSpec<ScalarSubQuery<E>, C> select(Distinct distinct, SelectPart selectPart) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final FromSpec<ScalarSubQuery<E>, C> select(SelectPart selectPart) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <S extends SelectPart> FromSpec<ScalarSubQuery<E>, C> select(Distinct distinct
                , List<S> selectPartList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final <S extends SelectPart> FromSpec<ScalarSubQuery<E>, C> select(List<S> selectPartList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final FromSpec<ScalarSubQuery<E>, C> select(Distinct distinct, Selection selection) {
            this.actualSelect.doSelectClause(distinct, selection);
            return this;
        }

        @Override
        public final FromSpec<ScalarSubQuery<E>, C> select(Selection selection) {
            this.actualSelect.doSelectClause((Distinct) null, selection);
            return this;
        }

        @Override
        public final TableRouteJoinSpec<ScalarSubQuery<E>, C> from(TableMeta<?> tableMeta, String tableAlias) {
            this.actualSelect.from(tableMeta, tableAlias);
            return this;
        }

        @Override
        public final JoinSpec<ScalarSubQuery<E>, C> from(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.from(function, subQueryAlia);
            return this;
        }

        @Override
        public final TableRouteOnSpec<ScalarSubQuery<E>, C> leftJoin(TableMeta<?> tableMeta, String tableAlias) {
            this.actualSelect.leftJoin(tableMeta, tableAlias);
            return this.onClauseImpl;
        }

        @Override
        public final OnSpec<ScalarSubQuery<E>, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.leftJoin(function, subQueryAlia);
            return this.onClauseImpl;
        }

        @Override
        public final TableRouteOnSpec<ScalarSubQuery<E>, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.ifLeftJoin(predicate, tableMeta, tableAlias);
            return this.onClauseImpl;
        }

        @Override
        public final OnSpec<ScalarSubQuery<E>, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.ifLeftJoin(function, subQueryAlia);
            return this.onClauseImpl;
        }

        @Override
        public final TableRouteOnSpec<ScalarSubQuery<E>, C> join(TableMeta<?> tableMeta, String tableAlias) {
            this.actualSelect.join(tableMeta, tableAlias);
            return this.onClauseImpl;
        }

        @Override
        public final OnSpec<ScalarSubQuery<E>, C> join(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.join(function, subQueryAlia);
            return this.onClauseImpl;
        }

        @Override
        public final TableRouteOnSpec<ScalarSubQuery<E>, C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.ifJoin(predicate, tableMeta, tableAlias);
            return this.onClauseImpl;
        }

        @Override
        public final OnSpec<ScalarSubQuery<E>, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.ifJoin(function, subQueryAlia);
            return this.onClauseImpl;
        }

        @Override
        public final TableRouteOnSpec<ScalarSubQuery<E>, C> rightJoin(TableMeta<?> tableMeta, String tableAlias) {
            this.actualSelect.rightJoin(tableMeta, tableAlias);
            return this.onClauseImpl;
        }

        @Override
        public final OnSpec<ScalarSubQuery<E>, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.rightJoin(function, subQueryAlia);
            return this.onClauseImpl;
        }

        @Override
        public final TableRouteOnSpec<ScalarSubQuery<E>, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta
                , String tableAlias) {
            this.actualSelect.ifRightJoin(predicate, tableMeta, tableAlias);
            return this.onClauseImpl;
        }

        @Override
        public final OnSpec<ScalarSubQuery<E>, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia) {
            this.actualSelect.ifRightJoin(function, subQueryAlia);
            return this.onClauseImpl;
        }

        @Override
        public final JoinSpec<ScalarSubQuery<E>, C> route(int databaseIndex, int tableIndex) {
            this.actualSelect.route(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public final JoinSpec<ScalarSubQuery<E>, C> route(int tableIndex) {
            this.actualSelect.route(-1, tableIndex);
            return this;
        }

        @Override
        public final GroupBySpec<ScalarSubQuery<E>, C> where(List<IPredicate> predicateList) {
            this.actualSelect.where(predicateList);
            return this;
        }

        @Override
        public final GroupBySpec<ScalarSubQuery<E>, C> where(Function<C, List<IPredicate>> function) {
            this.actualSelect.where(function);
            return this;
        }

        @Override
        public final WhereAndSpec<ScalarSubQuery<E>, C> where(IPredicate predicate) {
            this.actualSelect.where(predicate);
            return this;
        }

        @Override
        public final WhereAndSpec<ScalarSubQuery<E>, C> and(IPredicate predicate) {
            this.actualSelect.and(predicate);
            return this;
        }

        @Override
        public final WhereAndSpec<ScalarSubQuery<E>, C> ifAnd(@Nullable IPredicate predicate) {
            this.actualSelect.ifAnd(predicate);
            return this;
        }

        @Override
        public final WhereAndSpec<ScalarSubQuery<E>, C> ifAnd(Function<C, IPredicate> function) {
            this.actualSelect.ifAnd(function);
            return this;
        }

        @Override
        public final HavingSpec<ScalarSubQuery<E>, C> groupBy(SortPart sortPart) {
            this.actualSelect.groupBy(sortPart);
            return this;
        }

        @Override
        public final HavingSpec<ScalarSubQuery<E>, C> groupBy(SortPart sortPart1, SortPart sortPart2) {
            this.actualSelect.groupBy(sortPart1, sortPart2);
            return this;
        }

        @Override
        public final HavingSpec<ScalarSubQuery<E>, C> groupBy(List<SortPart> sortPartList) {
            this.actualSelect.groupBy(sortPartList);
            return this;
        }

        @Override
        public final HavingSpec<ScalarSubQuery<E>, C> groupBy(Function<C, List<SortPart>> function) {
            this.actualSelect.groupBy(function);
            return this;
        }

        @Override
        public final HavingSpec<ScalarSubQuery<E>, C> ifGroupBy(Function<C, List<SortPart>> function) {
            this.actualSelect.ifGroupBy(function);
            return this;
        }

        @Override
        public final OrderBySpec<ScalarSubQuery<E>, C> having(IPredicate predicate) {
            this.actualSelect.having(predicate);
            return this;
        }

        @Override
        public final OrderBySpec<ScalarSubQuery<E>, C> having(List<IPredicate> predicateList) {
            this.actualSelect.having(predicateList);
            return this;
        }

        @Override
        public final OrderBySpec<ScalarSubQuery<E>, C> having(Function<C, List<IPredicate>> function) {
            this.actualSelect.having(function);
            return this;
        }

        @Override
        public final OrderBySpec<ScalarSubQuery<E>, C> ifHaving(Function<C, List<IPredicate>> function) {
            this.actualSelect.ifHaving(function);
            return this;
        }

        @Override
        public final LimitClause<ScalarSubQuery<E>, C> orderBy(SortPart sortPart) {
            this.actualSelect.orderBy(sortPart);
            return this;
        }

        @Override
        public final LimitClause<ScalarSubQuery<E>, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
            this.actualSelect.orderBy(sortPart1, sortPart2);
            return this;
        }

        @Override
        public final LimitSpec<ScalarSubQuery<E>, C> orderBy(List<SortPart> sortPartList) {
            this.actualSelect.orderBy(sortPartList);
            return this;
        }

        @Override
        public final LimitClause<ScalarSubQuery<E>, C> orderBy(Function<C, List<SortPart>> function) {
            this.actualSelect.orderBy(function);
            return this;
        }

        @Override
        public final LimitSpec<ScalarSubQuery<E>, C> ifOrderBy(Function<C, List<SortPart>> function) {
            this.actualSelect.ifOrderBy(function);
            return this;
        }

        @Override
        public final LockSpec<ScalarSubQuery<E>, C> limit(int rowCount) {
            this.actualSelect.limit(rowCount);
            return this;
        }

        @Override
        public final LockSpec<ScalarSubQuery<E>, C> limit(int offset, int rowCount) {
            this.actualSelect.limit(offset, rowCount);
            return this;
        }

        @Override
        public final LockSpec<ScalarSubQuery<E>, C> ifLimit(Function<C, LimitOption> function) {
            this.actualSelect.ifLimit(function);
            return this;
        }

        @Override
        public final LockSpec<ScalarSubQuery<E>, C> ifLimit(Predicate<C> predicate, int rowCount) {
            this.actualSelect.ifLimit(predicate, rowCount);
            return this;
        }

        @Override
        public final LockSpec<ScalarSubQuery<E>, C> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
            this.actualSelect.ifLimit(predicate, offset, rowCount);
            return this;
        }

        @Override
        public final QuerySpec<ScalarSubQuery<E>> lock(LockMode lockMode) {
            this.actualSelect.lock(lockMode);
            return this;
        }

        @Override
        public final QuerySpec<ScalarSubQuery<E>> ifLock(Function<C, LockMode> function) {
            this.actualSelect.ifLock(function);
            return this;
        }


        @Override
        public final UnionSpec<ScalarSubQuery<E>, C> bracketsQuery() {
            return ComposeQueries.brackets(this.actualSelect.criteria, asQuery());
        }

        @Override
        public final UnionSpec<ScalarSubQuery<E>, C> union(Function<C, ScalarSubQuery<E>> function) {
            return ComposeQueries.compose(this.actualSelect.criteria, asQuery(), UnionType.UNION, function);
        }

        @Override
        public final UnionSpec<ScalarSubQuery<E>, C> unionAll(Function<C, ScalarSubQuery<E>> function) {
            return ComposeQueries.compose(this.actualSelect.criteria, asQuery(), UnionType.UNION_ALL, function);
        }

        @Override
        public final UnionSpec<ScalarSubQuery<E>, C> unionDistinct(Function<C, ScalarSubQuery<E>> function) {
            return ComposeQueries.compose(this.actualSelect.criteria, asQuery(), UnionType.UNION_DISTINCT, function);
        }

        @Override
        public final ScalarSubQuery<E> asQuery() {
            this.actualSelect.asQuery();
            return this;
        }

        @Override
        public final boolean prepared() {
            return this.actualSelect.prepared();
        }

        @Override
        public final void clear() {
            this.actualSelect.clear();
        }

        @Nullable
        @Override
        public final LockMode lockMode() {
            return this.actualSelect.lockMode();
        }

        @Override
        public final List<IPredicate> predicateList() {
            return this.actualSelect.predicateList();
        }

        @Override
        public final List<SortPart> groupPartList() {
            return this.actualSelect.groupPartList();
        }

        @Override
        public final List<IPredicate> havingList() {
            return this.actualSelect.havingList();
        }

        @Override
        public final List<SortPart> orderPartList() {
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


    }


    /**
     * Design this inner class for {@link #route(int)} and {@link #route(int, int)} don't crash with
     * ScalarSubQueryAdaptor class
     */
    private static final class ScalarOnClauseImpl<E, C> implements Query, TableRouteOnSpec<ScalarSubQuery<E>, C> {

        private final ScalarSubQueryAdaptor<E, C> standardQuery;

        private ScalarOnClauseImpl(ScalarSubQueryAdaptor<E, C> standardQuery) {
            this.standardQuery = standardQuery;
        }

        @Override
        public boolean requiredBrackets() {
            return this.standardQuery.requiredBrackets();
        }

        @Override
        public JoinSpec<ScalarSubQuery<E>, C> on(List<IPredicate> predicateList) {
            this.standardQuery.actualSelect.tableRouteOnSpec.on(predicateList);
            return this.standardQuery;
        }

        @Override
        public JoinSpec<ScalarSubQuery<E>, C> on(IPredicate predicate) {
            this.standardQuery.actualSelect.tableRouteOnSpec.on(predicate);
            return this.standardQuery;
        }

        @Override
        public JoinSpec<ScalarSubQuery<E>, C> on(Function<C, List<IPredicate>> function) {
            this.standardQuery.actualSelect.tableRouteOnSpec.on(function);
            return this.standardQuery;
        }

        @Override
        public OnSpec<ScalarSubQuery<E>, C> route(int databaseIndex, int tableIndex) {
            this.standardQuery.actualSelect.tableRouteOnSpec.route(databaseIndex, tableIndex);
            return this;
        }

        @Override
        public OnSpec<ScalarSubQuery<E>, C> route(int tableIndex) {
            this.standardQuery.actualSelect.tableRouteOnSpec.route(-1, tableIndex);
            return this;
        }

        @Override
        public boolean prepared() {
            return this.standardQuery.actualSelect.prepared();
        }
    }


}
