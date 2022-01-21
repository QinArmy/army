package io.army.criteria.impl;

import io.army.Dialect;
import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql._MySQL80Query;
import io.army.criteria.mysql.MySQL80Query;
import io.army.dialect.Database;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MySQL80SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        MySQL80Query.From80Spec<C, Q>,// SR
        MySQL80Query.IndexHintJoin80Spec<C, Q>, //FT
        MySQL80Query.Join80Spec<C, Q>,          //FS
        MySQL80Query.PartitionJoin80Spec<C, Q>, //FP
        MySQL80Query.IndexPurposeJoin80Spec<C, Q>,//IR
        MySQL80Query.IndexHintOn80Spec<C, Q>,    //JR
        MySQL80Query.On80Spec<C, Q>,            //JS
        MySQL80Query.PartitionOn80Spec<C, Q>,   //IT
        MySQL80Query.GroupBy80Spec<C, Q>,       //WR
        MySQL80Query.WhereAnd80Spec<C, Q>,      //AR
        MySQL80Query.GroupByWithRollup80Spec<C, Q>,//GR
        MySQL80Query.Window80Spec<C, Q>,     //HR
        MySQL80Query.OrderByWithRollup80Spec<C, Q>,     //OR
        MySQL80Query.Lock80Spec<C, Q>,       //LR
        MySQL80Query.UnionOrderBy80Spec<C, Q>, //UR
        MySQL80Query.With80Spec<C, Q>>       //SP
        implements MySQL80Query, MySQL80Query.With80Spec<C, Q>, MySQL80Query.From80Spec<C, Q>
        , MySQL80Query.IndexHintJoin80Spec<C, Q>, MySQL80Query.IndexPurposeJoin80Spec<C, Q>
        , MySQL80Query.Join80Spec<C, Q>, MySQL80Query.WhereAnd80Spec<C, Q>, MySQL80Query.Having80Spec<C, Q>
        , _MySQL80Query, MySQL80Query.GroupByWithRollup80Spec<C, Q>, MySQL80Query.OrderByWithRollup80Spec<C, Q>
        , MySQL80Query.Lock80LockOfOptionSpec<C, Q>, MySQL80Query.Lock80LockOptionSpec<C, Q> {


    static <C> With80Spec<C, Select> simpleSelect(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> With80Spec<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C> With80Spec<C, RowSubQuery> rowSubQuery(@Nullable C criteria) {
        return new SimpleRowSubQuery<>(criteria);
    }

    static <C> With80Spec<C, ColumnSubQuery> columnSubQuery(@Nullable C criteria) {
        return new SimpleColumnSubQuery<>(criteria);
    }

    static <C, E> With80Spec<C, ScalarQueryExpression<E>> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarQuery<>(criteria);
    }


    static <C, Q extends Query> With80Spec<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        final With80Spec<C, ?> with80Spec;
        if (left instanceof Select) {
            with80Spec = new UnionAndSelect<>((Select) left, unionType);
        } else if (left instanceof ScalarSubQuery) {
            with80Spec = new UnionAndScalarSubQuery<>((ScalarQueryExpression<?>) left, unionType);
        } else if (left instanceof ColumnSubQuery) {
            with80Spec = new UnionAndColumnSubQuery<>((ColumnSubQuery) left, unionType);
        } else if (left instanceof RowSubQuery) {
            with80Spec = new UnionAndRowSubQuery<>((RowSubQuery) left, unionType);
        } else if (left instanceof SubQuery) {
            with80Spec = new UnionAndSubQuery<>((SubQuery) left, unionType);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (With80Spec<C, Q>) with80Spec;
    }

    private boolean recursive;

    private List<Cte> cteList;

    private Boolean groupByWithRollup;

    private boolean orderByWithRollup;

    private MySQLLock lock;

    private List<TableMeta<?>> ofTableList;

    private MySQLLockOption lockOption;


    private MySQL80SimpleQuery(@Nullable C criteria) {
        super(CriteriaUtils.primaryContext(criteria));
    }

    @Override
    public final Select80Spec<C, Q> with(String cteName, Supplier<SubQuery> supplier) {
        this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
        return this;
    }

    @Override
    public final Select80Spec<C, Q> with(String cteName, Function<C, SubQuery> function) {
        this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
        return this;
    }

    @Override
    public final Select80Spec<C, Q> with(Supplier<List<Cte>> supplier) {
        return this.doWithCte(supplier.get());
    }


    @Override
    public final Select80Spec<C, Q> with(Function<C, List<Cte>> function) {
        return this.doWithCte(function.apply(this.criteria));
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(String cteName, Supplier<SubQuery> supplier) {
        this.recursive = true;
        return this.with(cteName, supplier);
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(String cteName, Function<C, SubQuery> function) {
        this.recursive = true;
        return this.with(cteName, function);
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(Supplier<List<Cte>> supplier) {
        this.recursive = true;
        return this.doWithCte(supplier.get());
    }

    @Override
    public final Select80Spec<C, Q> withRecursive(Function<C, List<Cte>> function) {
        this.recursive = true;
        return this.doWithCte(function.apply(this.criteria));
    }

    /**
     * @see #afterOrderBy()
     */
    @Override
    public final Having80Spec<C, Q> withRollup() {
        if (this.hasOrderBy()) {
            this.orderByWithRollup = true;
        } else if (this.hasGroupBy()) {
            if (this.groupByWithRollup == null) {
                //@see this.afterOrderBy()
                this.groupByWithRollup = Boolean.TRUE;
            }
        } else {
            this.groupByWithRollup = Boolean.FALSE;
        }
        return this;
    }

    @Override
    public final Having80Spec<C, Q> ifWithRollup(Predicate<C> predicate) {
        if ((this.hasOrderBy() || this.hasGroupBy()) && predicate.test(this.criteria)) {
            this.withRollup();
        }
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(String name, Expression<?> partition) {
        //TODO
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(String name, Expression<?> partition, SortPart order) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(NamedWindow namedWindow) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(NamedWindow namedWindow1, NamedWindow namedWindow2) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(Supplier<List<NamedWindow>> supplier) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> window(Function<C, List<NamedWindow>> function) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> ifWindow(Supplier<List<NamedWindow>> supplier) {
        return this;
    }

    @Override
    public final OrderBy80Spec<C, Q> ifWindow(Function<C, List<NamedWindow>> function) {
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> forUpdate() {
        this.lock = MySQLLock.FOR_UPDATE;
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> forShare() {
        this.lock = MySQLLock.SHARE;
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lock = MySQLLock.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final Lock80LockOfOptionSpec<C, Q> ifForShare(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lock = MySQLLock.SHARE;
        }
        return this;
    }

    @Override
    public final Union80Spec<C, Q> lockInShareMode() {
        this.lock = MySQLLock.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final Union80Spec<C, Q> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lock = MySQLLock.LOCK_IN_SHARE_MODE;
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> of(TableMeta<?> table) {
        if (this.lock != null) {
            this.of(Collections.singletonList(table));
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> of(TableMeta<?> table1, TableMeta<?> table2) {
        if (this.lock != null) {
            this.of(Arrays.asList(table1, table2));
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> of(List<TableMeta<?>> tableList) {
        final MySQLLock lock = this.lock;
        if (lock != null) {
            switch (lock) {
                case FOR_UPDATE:
                case SHARE:
                    this.ofTableList = CollectionUtils.asUnmodifiableList(tableList);
                    break;
                case LOCK_IN_SHARE_MODE:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(lock);
            }
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> ifOf(Function<C, List<TableMeta<?>>> function) {
        if (this.lock != null) {
            final List<TableMeta<?>> list;
            list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.of(list);
            }
        }
        return this;
    }

    @Override
    public final Lock80LockOptionSpec<C, Q> ifOf(Supplier<List<TableMeta<?>>> supplier) {
        if (this.lock != null) {
            final List<TableMeta<?>> list;
            list = supplier.get();
            if (!CollectionUtils.isEmpty(list)) {
                this.of(list);
            }
        }
        return this;
    }

    @Override
    public final Union80Spec<C, Q> nowait() {
        return this.lockOption(MySQLLockOption.NOWAIT);
    }

    @Override
    public final Union80Spec<C, Q> skipLocked() {
        return this.lockOption(MySQLLockOption.SKIP_LOCKED);
    }

    @Override
    public final Union80Spec<C, Q> ifNowait(Predicate<C> predicate) {
        if (this.lock != null && predicate.test(this.criteria)) {
            this.nowait();
        }
        return this;
    }

    @Override
    public final Union80Spec<C, Q> ifSkipLocked(Predicate<C> predicate) {
        if (this.lock != null && predicate.test(this.criteria)) {
            this.skipLocked();
        }
        return this;
    }

    @Override
    public final UnionOrderBy80Spec<C, Q> bracketsQuery() {
        final UnionOrderBy80Spec<C, Q> unionSpec;
        if (this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> andQuery = (AbstractUnionAndQuery<C, Q>) this;
            final Q thisQuery = this.asQueryAndQuery();
            if (this instanceof ScalarSubQuery) {
                if (!(thisQuery instanceof ScalarSubQueryExpression)
                        || ((ScalarSubQueryExpression<?>) thisQuery).subQuery != this) {
                    throw asQueryMethodError();
                }
            } else if (thisQuery != this) {
                throw asQueryMethodError();
            }
            final Q right;
            right = MySQL80UnionQuery.bracketQuery(thisQuery)
                    .asQuery();
            unionSpec = MySQL80UnionQuery.unionQuery(andQuery.left, andQuery.unionType, right);
        } else {
            unionSpec = MySQL80UnionQuery.bracketQuery(this.asQuery());
        }
        return unionSpec;
    }

    @Override
    final void afterOrderBy() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = Boolean.FALSE;
        }
    }

    @Override
    final UnionOrderBy80Spec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return MySQL80UnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final With80Spec<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL80SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final Q onAsQuery(final boolean outer) {
        final Q thisQuery, resultQuery;
        if (this instanceof ScalarSubQuery) {
            thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
        } else {
            thisQuery = (Q) this;
        }
        if (outer && this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> unionAndQuery = (AbstractUnionAndQuery<C, Q>) this;
            resultQuery = MySQL80UnionQuery.unionQuery(unionAndQuery.left, unionAndQuery.unionType, thisQuery)
                    .asQuery();
        } else {
            resultQuery = thisQuery;
        }
        return resultQuery;
    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.lock = null;
        this.ofTableList = null;
        this.lockOption = null;
    }

    @Override
    final PartitionOn80Spec<C, Q> createPartitionOnBlock(_JoinType joinType, TableMeta<?> table) {
        return new PartitionOnBlock<>(joinType, table, this);
    }

    @Override
    final IndexHintOn80Spec<C, Q> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new IndexHintOnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final On80Spec<C, Q> createOnBlock(_JoinType joinType, TablePart tablePart, String alias) {
        Objects.requireNonNull(tablePart);
        return new OnBlock<>(joinType, tablePart, alias, this);
    }

    @Override
    final PartitionJoin80Spec<C, Q> createFirstPartitionBlock(TableMeta<?> table) {
        return new PartitionJoinImpl<>(table, this);
    }

    @Override
    final IndexHintOn80Spec<C, Q> createNoActionTableBlock() {
        return new NoActionIndexHintOnBlock<>(this);
    }

    @Override
    final On80Spec<C, Q> createNoActionOnBlock() {
        return new NoActionOnBlock<>(this);
    }

    @Override
    final PartitionOn80Spec<C, Q> createNoActionPartitionBlock() {
        return new NoActionPartitionOnBlock<>(this);
    }

    @Override
    final Dialect defaultDialect() {
        return Dialect.MYSQL80;
    }

    @Override
    final void validateDialect(Dialect mode) {
        if (mode.database() != Database.MySQL || mode.version() < 80) {
            throw _Exceptions.stmtDontSupportDialect(mode);
        }
    }


    @Override
    public final boolean groupByWithRollUp() {
        final Boolean withRollup = this.groupByWithRollup;
        return withRollup != null && withRollup;
    }

    @Override
    public final boolean orderByWithRollup() {
        return this.orderByWithRollup;
    }



    /*################################## blow private method ##################################*/


    private Union80Spec<C, Q> lockOption(MySQLLockOption lockOption) {
        final MySQLLock lock = this.lock;
        if (lock != null) {
            switch (lock) {
                case FOR_UPDATE:
                case SHARE:
                    this.lockOption = lockOption;
                    break;
                case LOCK_IN_SHARE_MODE:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(lock);
            }
        }
        return this;
    }

    private Select80Spec<C, Q> doWithCte(List<Cte> withCteList) {
        final List<Cte> cetList = new ArrayList<>(withCteList.size());
        for (Cte cte : withCteList) {
            _MySQLCounselor.assertCet(cte);
            cetList.add(cte);
        }
        this.cteList = cetList;
        return this;
    }


    /*################################## blow inner class ##################################*/

    private static final class SimpleSelect<C> extends MySQL80SimpleQuery<C, Select> implements Select {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }// SimpleSelect

    private static class SimpleSubQuery<C, Q extends SubQuery> extends MySQL80SimpleQuery<C, Q> implements SubQuery {

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleSubQuery

    private static final class SimpleRowSubQuery<C> extends SimpleSubQuery<C, RowSubQuery> implements RowSubQuery {

        private SimpleRowSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleRowSubQuery

    private static final class SimpleColumnSubQuery<C> extends SimpleSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private SimpleColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleColumnSubQuery

    private static final class SimpleScalarQuery<C, E> extends SimpleSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private SimpleScalarQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }

    }//SimpleScalarQuery


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends MySQL80SimpleQuery<C, Q> {

        private final Q left;

        private final UnionType unionType;

        private AbstractUnionAndQuery(Q left, UnionType unionType) {
            super(CriteriaUtils.getCriteria(left));
            this.left = left;
            this.unionType = unionType;
        }


    }//AbstractUnionAndQuery


    private static final class UnionAndSelect<C> extends AbstractUnionAndQuery<C, Select> implements Select {

        private UnionAndSelect(Select left, UnionType unionType) {
            super(left, unionType);
        }

    }//UnionAndSelect

    private static class UnionAndSubQuery<C, Q extends SubQuery> extends AbstractUnionAndQuery<C, Q> implements SubQuery {

        private UnionAndSubQuery(Q left, UnionType unionType) {
            super(left, unionType);
        }

    }//UnionAndSubQuery

    private static final class UnionAndRowSubQuery<C> extends UnionAndSubQuery<C, RowSubQuery> implements RowSubQuery {

        private UnionAndRowSubQuery(RowSubQuery left, UnionType unionType) {
            super(left, unionType);
        }
    }//UnionAndRowSubQuery

    private static final class UnionAndColumnSubQuery<C> extends UnionAndSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private UnionAndColumnSubQuery(ColumnSubQuery left, UnionType unionType) {
            super(left, unionType);
        }

    }//UnionAndColumnSubQuery

    private static final class UnionAndScalarSubQuery<C, E> extends UnionAndSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private UnionAndScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType) {
            super(left, unionType);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }

    }//UnionAndScalarSubQuery


    private static final class PartitionJoinImpl<C, Q extends Query>
            extends MySQLPartitionClause<C, MySQL80Query.AsJoin80Spec<C, Q>>
            implements MySQL80Query.PartitionJoin80Spec<C, Q>, MySQL80Query.AsJoin80Spec<C, Q> {

        private final TableMeta<?> table;

        private final MySQL80SimpleQuery<C, Q> query;

        private PartitionJoinImpl(TableMeta<?> table, MySQL80SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.table = table;
            this.query = query;
        }

        @Override
        public IndexHintJoin80Spec<C, Q> as(String alias) {
            Objects.requireNonNull(alias);
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            this.query.criteriaContext.onFirstBlock(new MySQLFirstBlock<>(this.table, alias, partitionList, this.query));
            return this.query;
        }

    }//PartitionJoinImpl


    private static class OnBlock<C, Q extends Query> extends OnClauseTableBlock<C, MySQL80Query.Join80Spec<C, Q>>
            implements MySQL80Query.On80Spec<C, Q> {


        private final MySQL80SimpleQuery<C, Q> query;

        private OnBlock(_JoinType joinType, TablePart tablePart, String alias, MySQL80SimpleQuery<C, Q> query) {
            super(joinType, tablePart, alias);
            this.query = query;
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.query.criteriaContext;
        }

        @Override
        Join80Spec<C, Q> endOnClause() {
            return this.query;
        }

    }//OnBlock

    private static final class IndexHintOnBlock<C, Q extends Query> extends MySQLIndexHintOnBlock<
            C,
            MySQL80Query.IndexPurposeOn80Spec<C, Q>,
            MySQL80Query.IndexHintOn80Spec<C, Q>,
            MySQL80Query.Join80Spec<C, Q>> implements MySQL80Query.IndexHintOn80Spec<C, Q>
            , MySQL80Query.IndexPurposeOn80Spec<C, Q> {

        private final List<String> partitionList;

        private final MySQL80SimpleQuery<C, Q> query;

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, MySQL80SimpleQuery<C, Q> query) {
            super(joinType, table, alias);
            this.query = query;
            this.partitionList = Collections.emptyList();
        }

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, List<String> partitionList
                , MySQL80SimpleQuery<C, Q> query) {
            super(joinType, table, alias);
            this.query = query;
            this.partitionList = CollectionUtils.unmodifiableList(partitionList);
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.query.criteriaContext;
        }

        @Override
        Join80Spec<C, Q> endOnClause() {
            return this.query;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

    }//IndexHintOnBlock

    /**
     * @see #createPartitionOnBlock(_JoinType, TableMeta)
     */
    private static final class PartitionOnBlock<C, Q extends Query>
            extends MySQLPartitionClause<C, MySQL80Query.AsOn80Spec<C, Q>>
            implements MySQL80Query.AsOn80Spec<C, Q>, MySQL80Query.PartitionOn80Spec<C, Q> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final MySQL80SimpleQuery<C, Q> query;

        private PartitionOnBlock(_JoinType joinType, TableMeta<?> table, MySQL80SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.joinType = joinType;
            this.table = table;
            this.query = query;
        }

        @Override
        public IndexHintOn80Spec<C, Q> as(final String alias) {
            Objects.requireNonNull(alias);
            final IndexHintOnBlock<C, Q> hintOnBlock;
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                hintOnBlock = new IndexHintOnBlock<>(this.joinType, this.table, alias, this.query);
            } else {
                hintOnBlock = new IndexHintOnBlock<>(this.joinType, this.table, alias, partitionList, this.query);
            }
            this.query.criteriaContext.onAddBlock(hintOnBlock);
            return hintOnBlock;
        }


    }//PartitionOnBlock


    private static final class NoActionOnBlock<C, Q extends Query>
            extends NoActionOnClause<C, MySQL80Query.Join80Spec<C, Q>>
            implements MySQL80Query.On80Spec<C, Q> {

        private NoActionOnBlock(Join80Spec<C, Q> stmt) {
            super(stmt);
        }


    }//NoActionOnBlock

    private static final class NoActionIndexHintOnBlock<C, Q extends Query> extends MySQLNoActionIndexHintOnBlock<
            C,
            MySQL80Query.IndexPurposeOn80Spec<C, Q>,
            MySQL80Query.IndexHintOn80Spec<C, Q>,
            MySQL80Query.Join80Spec<C, Q>> implements MySQL80Query.IndexPurposeOn80Spec<C, Q>
            , MySQL80Query.IndexHintOn80Spec<C, Q> {

        private NoActionIndexHintOnBlock(Join80Spec<C, Q> stmt) {
            super(stmt);
        }

    }// NoActionIndexHintOnBlock


    private static final class NoActionPartitionOnBlock<C, Q extends Query>
            extends MySQLNoActionPartitionClause<C, MySQL80Query.AsOn80Spec<C, Q>>
            implements MySQL80Query.PartitionOn80Spec<C, Q>, MySQL80Query.AsOn80Spec<C, Q> {

        private final NoActionIndexHintOnBlock<C, Q> hintOnBlock;

        private NoActionPartitionOnBlock(Join80Spec<C, Q> stmt) {
            this.hintOnBlock = new NoActionIndexHintOnBlock<>(stmt);
        }

        @Override
        public IndexHintOn80Spec<C, Q> as(String alias) {
            return this.hintOnBlock;
        }
    }//NoActionPartitionOnBlock


}
