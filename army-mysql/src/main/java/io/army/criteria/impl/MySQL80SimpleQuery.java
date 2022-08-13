package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQL80Query;
import io.army.criteria.mysql.MySQL80Query;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


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
        MySQL80Query._QueryUseIndexJoinSpec<C, Q>, //FT
        MySQL80Query._JoinSpec<C, Q>,          //FS
        MySQL80Query._PartitionJoinClause<C, Q>, //FP
        MySQL80Query._JoinSpec<C, Q>,          //FJ
        MySQL80Query._QueryUseIndexOnSpec<C, Q>,    //JT
        Statement._OnClause<C, MySQL80Query._JoinSpec<C, Q>>, //JS
        MySQL80Query._PartitionOnClause<C, Q>,   //JP
        MySQL80Query._GroupBySpec<C, Q>,       //WR
        MySQL80Query._WhereAndSpec<C, Q>,      //AR
        MySQL80Query._GroupByWithRollupSpec<C, Q>,//GR
        MySQL80Query._WindowSpec<C, Q>,     //HR
        MySQL80Query._OrderByWithRollupSpec<C, Q>,     //OR
        MySQL80Query._LockSpec<C, Q>,       //LR
        MySQL80Query._UnionOrderBySpec<C, Q>, //UR
        MySQL80Query._WithSpec<C, Q>>       //SP
        implements _MySQL80Query, MySQL80Query._WithSpec<C, Q>, MySQL80Query._FromSpec<C, Q>
        , MySQL80Query._QueryUseIndexJoinSpec<C, Q>, MySQL80Query._WindowCommaSpec<C, Q>
        , MySQL80Query._JoinSpec<C, Q>, MySQL80Query._WhereAndSpec<C, Q>, MySQL80Query._HavingSpec<C, Q>
        , MySQL80Query._GroupByWithRollupSpec<C, Q>, MySQL80Query._OrderByWithRollupSpec<C, Q>
        , MySQL80Query._LockOfSpec<C, Q>, MySQL80Query._LockLockOptionSpec<C, Q>
        , MySQL80Query, PartRowSet.OrderByEventListener {


    static <C> _WithSpec<C, Select> simpleSelect(@Nullable C criteria) {
        return new SimpleSelect<>(CriteriaContexts.primaryQueryContext(criteria));
    }

    static <C> _WithSpec<C, SubQuery> subQuery(final @Nullable C criteria) {
        return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(criteria));
    }


    static <C> _WithSpec<C, ScalarExpression> scalarSubQuery(final @Nullable C criteria) {
        return new SimpleScalarQuery<>(CriteriaContexts.subQueryContext(criteria));
    }


    static <C, Q extends Query> _WithSpec<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        final _WithSpec<C, ?> with80Spec;
        if (left instanceof Select) {
            with80Spec = new UnionAndSelect<>((Select) left, unionType, CriteriaContexts.primaryQueryContextFrom(left));
        } else if (left instanceof ScalarSubQuery) {
            final CriteriaContext context;
            context = CriteriaContexts.subQueryContextFrom(left);
            with80Spec = new UnionAndScalarSubQuery<>((ScalarExpression) left, unionType, context);
        } else if (left instanceof SubQuery) {
            final CriteriaContext context;
            context = CriteriaContexts.subQueryContextFrom(left);
            with80Spec = new UnionAndSubQuery<>((SubQuery) left, unionType, context);
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

    private MySQLLockMode lockMode;

    private List<String> ofTableList;

    private MySQLLockOption lockOption;

    private MySQLSupports.MySQLNoOnBlock<C, _QueryUseIndexJoinSpec<C, Q>> noOnBlock;


    private MySQL80SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final _IndexPurposeBySpec<C, _QueryUseIndexJoinSpec<C, Q>> useIndex() {
        return this.getUserIndexClause().useIndex();
    }

    @Override
    public final _IndexPurposeBySpec<C, _QueryUseIndexJoinSpec<C, Q>> ignoreIndex() {
        return this.getUserIndexClause().ignoreIndex();
    }

    @Override
    public final _IndexPurposeBySpec<C, _QueryUseIndexJoinSpec<C, Q>> forceIndex() {
        return this.getUserIndexClause().forceIndex();
    }


    /**
     * @see #orderByEvent()
     */
    @Override
    public final _HavingSpec<C, Q> withRollup() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = Boolean.TRUE;
        } else {
            this.orderByWithRollup = true;
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
    public final _OrderBySpec<C, Q> window(Supplier<List<Window>> supplier) {
        this.windowList = MySQLUtils.asWindowList(supplier.get(), this::isIllegalWindow);
        return this;
    }

    @Override
    public final _OrderBySpec<C, Q> window(Function<C, List<Window>> function) {
        this.windowList = MySQLUtils.asWindowList(function.apply(this.criteria), this::isIllegalWindow);
        return this;
    }

    @Override
    public final _OrderBySpec<C, Q> ifWindow(Supplier<List<Window>> supplier) {
        final List<Window> windowList;
        windowList = supplier.get();
        if (windowList != null && windowList.size() > 0) {
            this.windowList = MySQLUtils.asWindowList(windowList, SimpleWindow::isIllegalWindow);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<C, Q> ifWindow(Function<C, List<Window>> function) {
        final List<Window> windowList;
        windowList = function.apply(this.criteria);
        if (windowList != null && windowList.size() > 0) {
            this.windowList = MySQLUtils.asWindowList(windowList, SimpleWindow::isIllegalWindow);
        }
        return this;
    }

    @Override
    public final Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>> comma(final String windowName) {
        return this.window(windowName);
    }

    @Override
    public final _LockOfSpec<C, Q> forUpdate() {
        this.lockMode = MySQLLockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final _LockOfSpec<C, Q> forShare() {
        this.lockMode = MySQLLockMode.FOR_SHARE;
        return this;
    }

    @Override
    public final _LockOfSpec<C, Q> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQLLockMode.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final _LockOfSpec<C, Q> ifForShare(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQLLockMode.FOR_SHARE;
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> lockInShareMode() {
        this.lockMode = MySQLLockMode.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockMode = MySQLLockMode.LOCK_IN_SHARE_MODE;
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
    public final void orderByEvent() {
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
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
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
    final _UnionOrderBySpec<C, Q> getNoActionUnionRowSet(RowSet rowSet) {
        return MySQL80UnionQuery.noActionQuery(rowSet);
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

    /*################################## blow JoinableClause method ##################################*/

    @Override
    public final _PartitionJoinClause<C, Q> createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new PartitionJoinClause<>(joinType, table, this);
    }

    @Override
    public final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final MySQLSupports.MySQLNoOnBlock<C, _QueryUseIndexJoinSpec<C, Q>> noOnBlock;
        noOnBlock = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, this);
        this.noOnBlock = noOnBlock; //update current no on block
        return noOnBlock;
    }

    @Override
    public final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        MySQLUtils.assertItemWord(this.criteriaContext, itemWord, tableItem);
        return new TableBlock.DialectNoOnTableBlock(joinType, itemWord, tableItem, alias);
    }

    @Override
    public final _TableBlock createDynamicBlock(_JoinType joinType, DynamicBlock<?> block) {
        return MySQLSupports.createDynamicBlock(joinType, block);
    }


    @Override
    public final _PartitionOnClause<C, Q> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new PartitionOnClause<>(joinType, table, this);
    }

    @Override
    public final _QueryUseIndexOnSpec<C, Q> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return new OnTableBlock<>(joinType, null, table, tableAlias, this);
    }

    @Override
    public final _OnClause<C, _JoinSpec<C, Q>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        MySQLUtils.assertItemWord(this.criteriaContext, itemWord, tableItem);
        return new OnTableBlock<>(joinType, itemWord, tableItem, alias, this);
    }


    /*################################## blow private method ##################################*/

    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private _QueryIndexHintClause<C, _QueryUseIndexJoinSpec<C, Q>> getUserIndexClause() {
        final MySQLSupports.MySQLNoOnBlock<C, _QueryUseIndexJoinSpec<C, Q>> noOnBlock = this.noOnBlock;
        if (noOnBlock == null || this.criteriaContext.lastTableBlockWithoutOnClause() != noOnBlock) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return noOnBlock.getUseIndexClause();
    }

    private boolean isIllegalWindow(Window window) {
        return SimpleWindow.isIllegalWindow(window, this.criteriaContext);
    }


    private _LockLockOptionSpec<C, Q> asOfTableAliasList(final boolean unmodified
            , final @Nullable List<String> aliasList) {
        switch (this.lockMode) {
            case FOR_SHARE:
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
            if (this.criteriaContext.getTable(alias) == null) {
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
        final MySQLLockMode lock = this.lockMode;
        if (lock == null) {
            return this;
        }
        switch (lock) {
            case FOR_UPDATE:
            case FOR_SHARE:
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

        private SimpleSelect(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

    }// SimpleSelect

    private static class SimpleSubQuery<C, Q extends SubQuery> extends MySQL80SimpleQuery<C, Q> implements SubQuery {

        private Map<String, Selection> selectionMap;

        private SimpleSubQuery(CriteriaContext criteriaContext) {
            super(criteriaContext);
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


    private static class SimpleScalarQuery<C> extends SimpleSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private SimpleScalarQuery(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

    }//SimpleScalarQuery


    private static abstract class UnionAndQuery<C, Q extends Query> extends MySQL80SimpleQuery<C, Q>
            implements UnionAndRowSet {

        private final Q left;

        private final UnionType unionType;

        private UnionAndQuery(Q left, UnionType unionType, CriteriaContext criteriaContext) {
            super(criteriaContext);
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

        private UnionAndSelect(Select left, UnionType unionType, CriteriaContext criteriaContext) {
            super(left, unionType, criteriaContext);
        }

    }//UnionAndSelect

    private static class UnionAndSubQuery<C, Q extends SubQuery> extends UnionAndQuery<C, Q>
            implements SubQuery, _SelfDescribed {

        private Map<String, Selection> selectionMap;

        private UnionAndSubQuery(Q left, UnionType unionType, CriteriaContext criteriaContext) {
            super(left, unionType, criteriaContext);
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

        private UnionAndScalarSubQuery(ScalarExpression left, UnionType unionType, CriteriaContext criteriaContext) {
            super(left, unionType, criteriaContext);
        }


    }//UnionAndScalarSubQuery


    private static final class PartitionJoinClause<C, Q extends Query>
            extends MySQLSupports.PartitionAsClause<C, _QueryUseIndexJoinSpec<C, Q>>
            implements MySQL80Query._PartitionJoinClause<C, Q> {


        private final MySQL80SimpleQuery<C, Q> query;


        private PartitionJoinClause(_JoinType joinType, TableMeta<?> table, MySQL80SimpleQuery<C, Q> query) {
            super(query.criteriaContext, joinType, table);
            this.query = query;
        }

        @Override
        _QueryUseIndexJoinSpec<C, Q> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQL80SimpleQuery<C, Q> query = this.query;

            final MySQLSupports.MySQLNoOnBlock<C, _QueryUseIndexJoinSpec<C, Q>> noOnBlock;
            noOnBlock = new MySQLSupports.MySQLNoOnBlock<>(params, query);

            query.criteriaContext.onAddBlock(noOnBlock);
            query.noOnBlock = noOnBlock; //update current noOnBlock
            return query;
        }


    }//PartitionJoinClause


    private static final class PartitionOnClause<C, Q extends Query>
            extends MySQLSupports.PartitionAsClause<C, _QueryUseIndexOnSpec<C, Q>>
            implements MySQL80Query._PartitionOnClause<C, Q> {


        private final MySQL80SimpleQuery<C, Q> query;

        private PartitionOnClause(_JoinType joinType, TableMeta<?> table, MySQL80SimpleQuery<C, Q> query) {
            super(query.criteriaContext, joinType, table);
            this.query = query;
        }

        @Override
        _QueryUseIndexOnSpec<C, Q> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQL80SimpleQuery<C, Q> query = this.query;

            final OnTableBlock<C, Q> block;
            block = new OnTableBlock<>(params, query);
            query.criteriaContext.onAddBlock(block);
            return block;
        }


    }//PartitionOnClause


    private static final class OnTableBlock<C, Q extends Query>
            extends MySQLSupports.MySQLOnBlock<C, _QueryUseIndexOnSpec<C, Q>, _JoinSpec<C, Q>>
            implements _QueryUseIndexOnSpec<C, Q> {

        private OnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem
                , String alias, _JoinSpec<C, Q> stmt) {
            super(joinType, itemWord, tableItem, alias, stmt);
        }

        private OnTableBlock(MySQLSupports.MySQLBlockParams params, _JoinSpec<C, Q> stmt) {
            super(params, stmt);
        }


    }//OnTableBlock


}
