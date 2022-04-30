package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._LateralSubQuery;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQL80Query;
import io.army.criteria.mysql.MySQL80Query;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.Database;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.*;


/**
 * <p>
 * This class is base class all the implementation of MySQL 8.0 SELECT syntax.
 * </p>
 *
 * @param <C> java type of criteria object for dynamic statement
 * @param <Q> {@link Select} or {@link SubQuery} or {@link ScalarExpression}
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/select.html">SELECT Statement</a>
 * @see MySQL80UnionQuery
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQL80SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        MySQL80Query._Select80Clause<C, Q>,      //WE
        MySQL80Query._FromSpec<C, Q>,// SR
        MySQL80Query._IndexHintJoinSpec<C, Q>, //FT
        MySQL80Query._JoinSpec<C, Q>,          //FS
        MySQL80Query._PartitionJoinClause<C, Q>, //FP
        MySQL80Query._IndexPurposeJoin80Clause<C, Q>,//IR
        MySQL80Query._IndexHintOnSpec<C, Q>,    //JT
        Statement._OnClause<C, MySQL80Query._JoinSpec<C, Q>>, //JS
        MySQL80Query._PartitionOn80Clause<C, Q>,   //JP
        MySQL80Query._LeftBracket80Clause<C, Q>,  //JE
        MySQL80Query._GroupBySpec<C, Q>,       //WR
        MySQL80Query._WhereAndSpec<C, Q>,      //AR
        MySQL80Query._GroupByWithRollupSpec<C, Q>,//GR
        MySQL80Query._WindowSpec<C, Q>,     //HR
        MySQL80Query._OrderByWithRollupSpec<C, Q>,     //OR
        MySQL80Query._LockSpec<C, Q>,       //LR
        MySQL80Query._UnionOrderBySpec<C, Q>, //UR
        MySQL80Query._WithSpec<C, Q>>       //SP
        implements _MySQL80Query, MySQL80Query._WithSpec<C, Q>, MySQL80Query._FromSpec<C, Q>
        , MySQL80Query._IndexHintJoinSpec<C, Q>, MySQL80Query._IndexPurposeJoin80Clause<C, Q>
        , MySQL80Query._JoinSpec<C, Q>, MySQL80Query._WhereAndSpec<C, Q>, MySQL80Query._HavingSpec<C, Q>
        , MySQL80Query._GroupByWithRollupSpec<C, Q>, MySQL80Query._OrderByWithRollupSpec<C, Q>
        , MySQL80Query._LockOfSpec<C, Q>, MySQL80Query._LockLockOptionSpec<C, Q>
        , MySQL80Query._LeftBracket80Clause<C, Q>, MySQL80Query._WindowCommaSpec<C, Q> {


    static <C> _WithSpec<C, Select> simpleSelect(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> _WithSpec<C, SubQuery> subQuery(final boolean lateral, final @Nullable C criteria) {
        final _WithSpec<C, SubQuery> with80Spec;
        if (lateral) {
            with80Spec = new LateralSimpleSubQuery<>(criteria);
        } else {
            with80Spec = new SimpleSubQuery<>(criteria);
        }
        return with80Spec;
    }


    static <C> _WithSpec<C, ScalarExpression> scalarSubQuery(final boolean lateral, final @Nullable C criteria) {
        final _WithSpec<C, ScalarExpression> with80Spec;
        if (lateral) {
            with80Spec = new LateralSimpleScalarQuery<>(criteria);
        } else {
            with80Spec = new SimpleScalarQuery<>(criteria);
        }
        return with80Spec;
    }


    static <C, Q extends Query> _WithSpec<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        final _WithSpec<C, ?> with80Spec;
        if (left instanceof Select) {
            with80Spec = new UnionAndSelect<>((Select) left, unionType);
        } else if (left instanceof ScalarSubQuery) {
            with80Spec = new UnionAndScalarSubQuery<>((ScalarExpression) left, unionType);
        } else if (left instanceof SubQuery) {
            with80Spec = new UnionAndSubQuery<>((SubQuery) left, unionType);
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (_WithSpec<C, Q>) with80Spec;
    }

    private boolean recursive;

    private List<Cte> cteList;

    private Boolean groupByWithRollup;

    private List<Window> windowList;

    private boolean orderByWithRollup;

    private MySQLLock lockMode;

    private List<String> ofTableList;

    private MySQLLockOption lockOption;


    private MySQL80SimpleQuery(@Nullable C criteria) {
        super(CriteriaContexts.queryContext(criteria));
    }

    /**
     * @see #onOrderBy()
     */
    @Override
    public final _HavingSpec<C, Q> withRollup() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = this.hasGroupBy();
        } else {
            this.orderByWithRollup = this.hasOrderBy();
        }
        return this;
    }

    @Override
    public final _HavingSpec<C, Q> ifWithRollup(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.withRollup();
        }
        return this;
    }

    @Override
    public final Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>> window(final String windowName) {
        if (!_StringUtils.hasText(windowName)) {
            throw _Exceptions.namedWindowNoText();
        }
        final Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>> window;
        window = SimpleWindow.standard(windowName, this);

        List<Window> windowList = this.windowList;
        if (windowList == null) {
            windowList = new ArrayList<>();
            this.windowList = windowList;
        }
        windowList.add((Window) window);
        return window;
    }

    @Override
    public final _OrderBySpec<C, Q> window(Function<WindowBuilder<C>, List<Window>> function) {
        final List<Window> windowList;
        windowList = function.apply(this::createWindowClause);
        if (windowList == null || windowList.size() == 0) {
            throw _Exceptions.windowListIsEmpty();
        }
        this.windowList = _CollectionUtils.asUnmodifiableList(windowList);
        return this;
    }

    @Override
    public final _OrderBySpec<C, Q> window(BiFunction<C, WindowBuilder<C>, List<Window>> function) {
        final List<Window> windowList;
        windowList = function.apply(this.criteria, this::createWindowClause);
        if (windowList == null || windowList.size() == 0) {
            throw _Exceptions.windowListIsEmpty();
        }
        this.windowList = _CollectionUtils.asUnmodifiableList(windowList);
        return this;
    }

    @Override
    public final _OrderBySpec<C, Q> ifWindow(Function<WindowBuilder<C>, List<Window>> function) {
        final List<Window> windowList;
        windowList = function.apply(this::createWindowClause);
        if (windowList != null && windowList.size() > 0) {
            this.windowList = _CollectionUtils.asUnmodifiableList(windowList);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<C, Q> ifWindow(BiFunction<C, WindowBuilder<C>, List<Window>> function) {
        final List<Window> windowList;
        windowList = function.apply(this.criteria, this::createWindowClause);
        if (windowList != null && windowList.size() > 0) {
            this.windowList = _CollectionUtils.asUnmodifiableList(windowList);
        }
        return this;
    }

    @Override
    public final Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>> comma(final String windowName) {
        return this.window(windowName);
    }

    @Override
    public final _LockOfSpec<C, Q> forUpdate() {
        this.lockMode = MySQLLock.FOR_UPDATE;
        return this;
    }

    @Override
    public final _LockOfSpec<C, Q> forShare() {
        this.lockMode = MySQLLock.SHARE;
        return this;
    }

    @Override
    public final _LockOfSpec<C, Q> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQLLock.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final _LockOfSpec<C, Q> ifForShare(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQLLock.SHARE;
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> lockInShareMode() {
        this.lockMode = MySQLLock.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQLLock.LOCK_IN_SHARE_MODE;
        }
        return this;
    }

    @Override
    public final _LockLockOptionSpec<C, Q> of(String tableAlias) {
        if (this.lockMode == null) {
            return this;
        }
        return this.asOfTableAliasList(true, Collections.singletonList(tableAlias));
    }

    @Override
    public final _LockLockOptionSpec<C, Q> of(String tableAlias1, String tableAlias2) {
        if (this.lockMode == null) {
            return this;
        }
        return this.asOfTableAliasList(true, ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2));
    }

    @Override
    public final _LockLockOptionSpec<C, Q> of(String tableAlias1, String tableAlias2, String tableAlias3) {
        if (this.lockMode == null) {
            return this;
        }
        return this.asOfTableAliasList(true, ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2, tableAlias3));
    }

    @Override
    public final _LockLockOptionSpec<C, Q> of(Supplier<List<String>> supplier) {
        if (this.lockMode == null) {
            return this;
        }
        return this.asOfTableAliasList(false, supplier.get());
    }

    @Override
    public final _LockLockOptionSpec<C, Q> of(Function<C, List<String>> function) {
        if (this.lockMode == null) {
            return this;
        }
        return this.asOfTableAliasList(false, function.apply(this.criteria));
    }

    @Override
    public final _LockLockOptionSpec<C, Q> of(Consumer<List<String>> consumer) {
        if (this.lockMode == null) {
            return this;
        }
        final List<String> tableAliasList = new ArrayList<>();
        consumer.accept(tableAliasList);
        return this.asOfTableAliasList(true, _CollectionUtils.unmodifiableList(tableAliasList));
    }

    @Override
    public final _LockLockOptionSpec<C, Q> ifOf(Supplier<List<String>> supplier) {
        if (this.lockMode == null) {
            return this;
        }
        final List<String> tableAliasList;
        tableAliasList = supplier.get();
        if (tableAliasList != null && tableAliasList.size() > 0) {
            this.asOfTableAliasList(false, tableAliasList);
        }
        return this;
    }

    @Override
    public final _LockLockOptionSpec<C, Q> ifOf(Function<C, List<String>> function) {
        if (this.lockMode == null) {
            return this;
        }
        final List<String> tableAliasList;
        tableAliasList = function.apply(this.criteria);
        if (tableAliasList != null && tableAliasList.size() > 0) {
            this.asOfTableAliasList(false, tableAliasList);
        }
        return this;
    }


    @Override
    public final _UnionSpec<C, Q> nowait() {
        return this.lockOption(MySQLLockOption.NOWAIT);
    }

    @Override
    public final _UnionSpec<C, Q> skipLocked() {
        return this.lockOption(MySQLLockOption.SKIP_LOCKED);
    }

    @Override
    public final _UnionSpec<C, Q> ifNowait(Predicate<C> predicate) {
        if (this.lockMode != null && predicate.test(this.criteria)) {
            this.lockOption(MySQLLockOption.NOWAIT);
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifSkipLocked(Predicate<C> predicate) {
        if (this.lockMode != null && predicate.test(this.criteria)) {
            this.lockOption(MySQLLockOption.SKIP_LOCKED);
        }
        return this;
    }


    @Override
    final void onOrderBy() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = Boolean.FALSE;
        }
    }

    @Override
    final _WithSpec<C, Q> asUnionAndRowSet(UnionType unionType) {
        return MySQL80SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final Q onAsQuery(final boolean fromAsQueryMethod) {
        super.onAsQuery(fromAsQueryMethod);
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.windowList == null) {
            this.windowList = Collections.emptyList();
        }
        if (this.ofTableList == null) {
            this.ofTableList = Collections.emptyList();
        }
        return this.finallyAsQuery(fromAsQueryMethod);
    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.windowList = null;
        this.lockMode = null;
        this.ofTableList = null;
        this.lockOption = null;
    }


    @Override
    final _IndexHintOnSpec<C, Q> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new IndexHintOnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL80;
    }

    @Override
    final void validateDialect(Dialect mode) {
        if (mode.database() != Database.MySQL || mode.version() < 80) {
            throw _Exceptions.stmtDontSupportDialect(mode);
        }
    }

    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return MySQL80UnionQuery.bracketQuery(rowSet);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return MySQL80UnionQuery.unionQuery((Q) left, unionType, right);
    }

    @Override
    final _TableBlock createTableBlockWithoutOnClause(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new MySQLNoOnBlock(joinType, table, tableAlias);
    }

    @Override
    final _OnClause<C, _JoinSpec<C, Q>> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }

    @Override
    final _PartitionJoinClause<C, Q> createNextClauseWithoutOnClause(_JoinType joinType, TableMeta<?> table) {
        return new PartitionJoinImpl<>(joinType, table, this);
    }


    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        if (cteList.size() == 0) {
            throw _Exceptions.cteListIsEmpty();
        }
        this.recursive = recursive;
        this.cteList = cteList;
    }


    /*################################## blow _MySQL80Query method ##################################*/

    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<Cte> cteList() {
        this.prepared();
        return this.cteList;
    }

    @Override
    public final boolean groupByWithRollUp() {
        final Boolean withRollup = this.groupByWithRollup;
        return withRollup != null && withRollup;
    }

    @Override
    public final List<Window> windowList() {
        this.prepared();
        return this.windowList;
    }

    @Override
    public final boolean orderByWithRollup() {
        return this.orderByWithRollup;
    }

    @Override
    public final SQLWords lockMode() {
        return this.lockMode;
    }

    @Override
    public final List<String> ofTableList() {
        this.prepared();
        return this.ofTableList;
    }

    @Override
    public final SQLWords lockOption() {
        return this.lockOption;
    }

    /*################################## blow private method ##################################*/

    private Window._SimpleAsClause<C, Window> createWindowClause(String windowName) {
        return SimpleWindow.standard(windowName, this.criteriaContext);
    }

    private _LockLockOptionSpec<C, Q> asOfTableAliasList(final boolean unmodified
            , final @Nullable List<String> aliasList) {
        switch (this.lockMode) {
            case SHARE:
            case FOR_UPDATE:
                break;
            case LOCK_IN_SHARE_MODE:
                throw _Exceptions.castCriteriaApi();
            default:
                throw _Exceptions.unexpectedEnum(lockMode);
        }
        if (aliasList == null || aliasList.size() == 0) {
            throw MySQLUtils.lockOfTableAliasListIsEmpty();
        }
        final List<String> tableAliasList;
        if (unmodified) {
            tableAliasList = null;
        } else {
            tableAliasList = new ArrayList<>(aliasList.size());
        }
        for (String alias : aliasList) {
            if (!this.criteriaContext.containTableAlias(alias)) {
                String m = String.format("unknown table alias[%s] in this query block.", alias);
                throw new CriteriaException(m);
            }
            if (tableAliasList != null) {
                tableAliasList.add(alias);
            }
        }
        if (tableAliasList == null) {
            this.ofTableList = aliasList;
        } else {
            this.ofTableList = _CollectionUtils.unmodifiableList(tableAliasList);
        }
        return this;
    }


    private _UnionSpec<C, Q> lockOption(MySQLLockOption lockOption) {
        final MySQLLock lock = this.lockMode;
        if (lock == null) {
            return this;
        }
        switch (lock) {
            case FOR_UPDATE:
            case SHARE:
                this.lockOption = lockOption;
                break;
            case LOCK_IN_SHARE_MODE:
                throw _Exceptions.castCriteriaApi();
            default:
                throw _Exceptions.unexpectedEnum(lock);
        }
        return this;
    }



    /*################################## blow inner class ##################################*/

    private static final class SimpleSelect<C> extends MySQL80SimpleQuery<C, Select> implements Select {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }// SimpleSelect

    private static class SimpleSubQuery<C, Q extends SubQuery> extends MySQL80SimpleQuery<C, Q> implements SubQuery
            , _SelfDescribed {

        private Map<String, Selection> selectionMap;

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = CriteriaUtils.createSelectionMap(this.selectItemList());
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(derivedFieldName);
        }

    }//SimpleSubQuery

    private static final class LateralSimpleSubQuery<C> extends SimpleSubQuery<C, SubQuery>
            implements _LateralSubQuery {

        private LateralSimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//LateralSimpleSubQuery


    private static class SimpleScalarQuery<C> extends SimpleSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private SimpleScalarQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleScalarQuery

    private static final class LateralSimpleScalarQuery<C> extends SimpleScalarQuery<C>
            implements _LateralSubQuery {

        private LateralSimpleScalarQuery(@Nullable C criteria) {
            super(criteria);
        }

    }//LateralSimpleScalarQuery


    private static abstract class UnionAndQuery<C, Q extends Query> extends MySQL80SimpleQuery<C, Q>
            implements UnionAndRowSet {

        private final Q left;

        private final UnionType unionType;

        private UnionAndQuery(Q left, UnionType unionType) {
            super(CriteriaUtils.getCriteria(left));
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        public final RowSet leftRowSet() {
            return this.left;
        }

        @Override
        public final UnionType unionType() {
            return this.unionType;
        }

    }//UnionAndQuery


    private static final class UnionAndSelect<C> extends UnionAndQuery<C, Select> implements Select {

        private UnionAndSelect(Select left, UnionType unionType) {
            super(left, unionType);
        }

    }//UnionAndSelect

    private static class UnionAndSubQuery<C, Q extends SubQuery> extends UnionAndQuery<C, Q>
            implements SubQuery, _SelfDescribed {

        private Map<String, Selection> selectionMap;

        private UnionAndSubQuery(Q left, UnionType unionType) {
            super(left, unionType);
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = CriteriaUtils.createSelectionMap(this.selectItemList());
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(derivedFieldName);
        }

    }//UnionAndSubQuery


    private static final class UnionAndScalarSubQuery<C> extends UnionAndSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionAndScalarSubQuery(ScalarExpression left, UnionType unionType) {
            super(left, unionType);
        }


    }//UnionAndScalarSubQuery


    private static final class PartitionJoinImpl<C, Q extends Query>
            extends MySQLPartitionClause<C, _AsJoinClause<C, Q>>
            implements _PartitionJoinClause<C, Q>, _AsJoinClause<C, Q> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final MySQL80SimpleQuery<C, Q> query;

        private PartitionJoinImpl(_JoinType joinType, TableMeta<?> table, MySQL80SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.joinType = joinType;
            this.table = table;
            this.query = query;
        }

        @Override
        public _IndexHintJoinSpec<C, Q> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final MySQLNoOnBlock block;
            if (partitionList == null) {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias, partitionList);
            }
            this.query.criteriaContext.onBlockWithoutOnClause(block);
            return this.query;
        }

    }//PartitionJoinImpl


    private static final class IndexHintOnBlock<C, Q extends Query> extends MySQLIndexHintOnBlock<
            C,
            _IndexPurposeOnClause<C, Q>,
            _IndexHintOnSpec<C, Q>,
            _JoinSpec<C, Q>> implements _IndexHintOnSpec<C, Q>
            , _IndexPurposeOnClause<C, Q> {

        private final List<String> partitionList;

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, MySQL80SimpleQuery<C, Q> query) {
            super(joinType, table, alias, query);
            this.partitionList = Collections.emptyList();
        }

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, List<String> partitionList
                , MySQL80SimpleQuery<C, Q> query) {
            super(joinType, table, alias, query);
            this.partitionList = partitionList;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

    }//IndexHintOnBlock


    private static final class PartitionOnBlock<C, Q extends Query>
            extends MySQLPartitionClause<C, _AsOnClause<C, Q>>
            implements _AsOnClause<C, Q>, _PartitionOn80Clause<C, Q> {

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
        public _IndexHintOnSpec<C, Q> as(final String alias) {
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


}
