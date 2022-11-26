package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.*;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of the implementation of {@link MySQLQuery}:
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLQueries<I extends Item> extends SimpleQueries.WithCteSimpleQueries<
        I,
        MySQLCtes,
        MySQLQuery._SelectSpec<I>,
        MySQLs.Modifier,
        MySQLQuery._MySQLSelectCommaSpec<I>,
        MySQLQuery._FromSpec<I>,
        MySQLQuery._IndexHintJoinSpec<I>,
        MySQLQuery._JoinSpec<I>,
        MySQLQuery._JoinSpec<I>,
        MySQLQuery._IndexHintOnSpec<I>,
        Statement._OnClause<MySQLQuery._JoinSpec<I>>,
        Statement._OnClause<MySQLQuery._JoinSpec<I>>,
        MySQLQuery._GroupBySpec<I>,
        MySQLQuery._WhereAndSpec<I>,
        MySQLQuery._GroupByWithRollupSpec<I>,
        MySQLQuery._WindowSpec<I>,
        MySQLQuery._OrderByWithRollupSpec<I>,
        MySQLQuery._LockOptionSpec<I>,
        Object,
        Object,
        MySQLQuery._QueryWithComplexSpec<I>>
        implements _MySQLQuery, MySQLQuery,
        MySQLQuery._WithSpec<I>,
        MySQLQuery._MySQLSelectCommaSpec<I>,
        MySQLQuery._IndexHintJoinSpec<I>,
        MySQLQuery._WhereAndSpec<I>,
        MySQLQuery._GroupByWithRollupSpec<I>,
        MySQLQuery._HavingSpec<I>,
        MySQLQuery._WindowCommaSpec<I>,
        MySQLQuery._OrderByWithRollupSpec<I>,
        MySQLQuery._LockOfTableSpec<I>,
        OrderByClause.OrderByEventListener {


    static <I extends Item> MySQLQueries<I> primaryQuery(@Nullable _WithClauseSpec spec
            , @Nullable CriteriaContext outerContext, Function<Select, I> function) {
        return new SimpleSelect<>(spec, outerContext, function, null);
    }


    static <I extends Item> MySQLQueries<I> subQuery(@Nullable _WithClauseSpec spec, CriteriaContext outerContext
            , Function<SubQuery, I> function) {
        return new SimpleSubQuery<>(spec, outerContext, function, null);
    }

    static <I extends Item> Function<String, MySQLQuery._StaticCteLeftParenSpec<I>> complexCte(CriteriaContext context
            , I cteComma) {
        return new StaticComplexCommand<>(context, cteComma)::nextCte;
    }

    private MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> noOnBlock;

    /**
     * @see #onOrderByEvent()
     */
    private Boolean groupByWithRollup;

    private List<_Window> windowList;

    private boolean orderByWithRollup;

    private MySQLLockMode lockMode;

    private List<TableMeta<?>> ofTableList;

    private LockWaitOption lockWaitOption;

    private List<String> intoVarList;

    MySQLQueries(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }


    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> with(String name) {

        final CriteriaContext context = this.context;
        final boolean recursive = false;
        context.onBeforeWithClause(recursive);
        return new MySQLCteComma<>(this, recursive)
                .complexCommand.nextCte(name);
    }

    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> withRecursive(String name) {
        final CriteriaContext context = this.context;
        final boolean recursive = true;
        context.onBeforeWithClause(recursive);
        return new MySQLCteComma<>(this, recursive)
                .complexCommand.nextCte(name);
    }


    @Override
    public final _PartitionJoinSpec<I> from(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> from() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedLeftParenJoinEnd);
    }

    @Override
    public final _IndexPurposeBySpec<_IndexHintJoinSpec<I>> useIndex() {
        return this.getIndexHintClause().useIndex();
    }

    @Override
    public final _IndexPurposeBySpec<_IndexHintJoinSpec<I>> ignoreIndex() {
        return this.getIndexHintClause().ignoreIndex();
    }

    @Override
    public final _IndexPurposeBySpec<_IndexHintJoinSpec<I>> forceIndex() {
        return this.getIndexHintClause().forceIndex();
    }


    @Override
    public final _PartitionOnSpec<I> leftJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> join(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> rightJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> fullJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> straightJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final _PartitionJoinSpec<I> crossJoin(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> leftJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedLeftParenOnEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> join() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedLeftParenOnEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> rightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedLeftParenOnEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> fullJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedLeftParenOnEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> straightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::nestedLeftParenOnEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> crossJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedLeftParenJoinEnd);
    }

    @Override
    public final _JoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    /**
     * @see #onOrderByEvent()
     */
    @Override
    public final _HavingSpec<I> withRollup() {
        if (this.groupByWithRollup == null) {//@see #onOrderByEvent()
            this.groupByWithRollup = Boolean.TRUE;
        } else {
            this.orderByWithRollup = true;
        }
        return this;
    }

    /**
     * @see #onOrderByEvent()
     */
    @Override
    public final _HavingSpec<I> ifWithRollup(final BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            if (this.groupByWithRollup == null) {//@see #onOrderByEvent()
                this.groupByWithRollup = Boolean.TRUE;
            } else {
                this.orderByWithRollup = true;
            }
        } else if (this.groupByWithRollup == null) {//@see #onOrderByEvent()
            this.groupByWithRollup = Boolean.FALSE;
        } else {
            this.orderByWithRollup = false;
        }
        return this;
    }

    /**
     * @see #withRollup()
     * @see #ifWithRollup(BooleanSupplier)
     */
    @Override
    public final void onOrderByEvent() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = Boolean.FALSE;
        }
    }


    @Override
    public final _OrderBySpec<I> window(Consumer<MySQLWindows> consumer) {
        return this.dynamicWindow(true, consumer);
    }

    @Override
    public final _OrderBySpec<I> ifWindow(Consumer<MySQLWindows> consumer) {
        return this.dynamicWindow(false, consumer);
    }

    @Override
    public final _WindowAsClause<I> window(String windowName) {
        return new NamedWindowAsClause<>(this, windowName);
    }

    @Override
    public final _WindowAsClause<I> comma(String windowName) {
        return new NamedWindowAsClause<>(this, windowName);
    }


    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        this.lockMode = MySQLLockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        this.lockMode = MySQLLockMode.FOR_SHARE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForUpdate(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = MySQLLockMode.FOR_UPDATE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForShare(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = MySQLLockMode.FOR_SHARE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> lockInShareMode() {
        this.lockMode = MySQLLockMode.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifLockInShareMode(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = MySQLLockMode.LOCK_IN_SHARE_MODE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table) {
        if (this.lockMode != null) {
            this.ofTableList = Collections.singletonList(table);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
        if (this.lockMode != null) {
            this.ofTableList = ArrayUtils.asUnmodifiableList(table1, table2);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
        if (this.lockMode != null) {
            this.ofTableList = ArrayUtils.asUnmodifiableList(table1, table2, table3);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<TableMeta<?>>> consumer) {
        if (this.lockMode != null) {
            final List<TableMeta<?>> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw CriteriaUtils.ofTableListIsEmpty(this.context);
            }
            this.ofTableList = _CollectionUtils.unmodifiableList(list);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
        if (this.lockMode != null) {
            final List<TableMeta<?>> list = new ArrayList<>();
            consumer.accept(list::add);
            this.ofTableList = _CollectionUtils.unmodifiableList(list);
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> noWait() {
        if (this.lockMode != null) {
            this.lockWaitOption = LockWaitOption.NOWAIT;
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> skipLocked() {
        if (this.lockMode != null) {
            this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifNoWait(BooleanSupplier predicate) {
        if (this.lockMode != null) {
            if (predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.NOWAIT;
            } else {
                this.lockWaitOption = null;
            }
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifSkipLocked(BooleanSupplier predicate) {
        if (this.lockMode != null) {
            if (predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
            } else {
                this.lockWaitOption = null;
            }
        }
        return this;
    }

    @Override
    public final _AsQueryClause<I> into(String firstVarName, String... rest) {
        this.intoVarList = ArrayUtils.unmodifiableListOf(firstVarName, rest);
        return this;
    }

    @Override
    public final _AsQueryClause<I> into(Consumer<Consumer<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        if (list.size() == 0) {
            throw ContextStack.criteriaError(this.context, "no into clause.");
        }
        this.intoVarList = _CollectionUtils.unmodifiableList(list);
        return this;
    }

    @Override
    public final _AsQueryClause<I> ifInto(Consumer<Consumer<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        this.intoVarList = _CollectionUtils.unmodifiableList(list);
        return this;
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

    @Override
    public final List<_Window> windowList() {
        final List<_Window> list = this.windowList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<String> lockOfTableList() {
        final List<TableMeta<?>> list = this.ofTableList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }//TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public final SQLWords lockMode() {
        return this.lockMode;
    }

    @Override
    public final SQLWords lockWaitOption() {
        return this.lockWaitOption;
    }

    @Override
    public final List<String> intoVarList() {
        final List<String> list = this.intoVarList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mySQLCteBuilder(recursive, this.context);
    }

    @Override
    final void onEndQuery() {
        final List<_Window> windowList = this.windowList;
        if (windowList == null) {
            this.windowList = Collections.emptyList();
        } else {
            this.windowList = _CollectionUtils.unmodifiableList(windowList);
        }

        if (this.ofTableList == null) {
            this.ofTableList = Collections.emptyList();
        }
        if (this.intoVarList == null) {
            this.intoVarList = Collections.emptyList();
        }
    }


    @Override
    final void onClear() {
        this.windowList = null;
        this.ofTableList = null;
        this.intoVarList = null;
    }

    @Override
    final List<MySQLs.Modifier> asModifierList(@Nullable List<MySQLs.Modifier> modifiers) {
        return CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::selectModifier);
    }

    @Override
    final boolean isErrorModifier(MySQLs.Modifier modifier) {
        return MySQLUtils.selectModifier(modifier) < 0;
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        return MySQLUtils.asHintList(this.context, hints, MySQLHints::castHint);
    }


    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, this);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem
            , String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, modifier, tableItem, alias, this);
    }


    @Override
    final _IndexHintOnSpec<I> createTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String tableAlias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnTableBlock<>(joinType, null, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, null, tableItem, alias, this);
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #from()
     * @see #crossJoin()
     */
    private _JoinSpec<I> nestedLeftParenJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return this;
    }

    /**
     * @see #leftJoin()
     * @see #join()
     * @see #rightJoin()
     * @see #fullJoin()
     * @see #straightJoin()
     */
    private _OnClause<_JoinSpec<I>> nestedLeftParenOnEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertMySQLJoinType();

        final OnClauseTableBlock<_JoinSpec<I>> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private _QueryIndexHintClause<_IndexHintJoinSpec<I>> getIndexHintClause() {
        final MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> noOnBlock = this.noOnBlock;
        if (noOnBlock == null || this.context.lastBlock() != noOnBlock) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return noOnBlock.getUseIndexClause();
    }

    /**
     * @see #window(Consumer)
     * @see #window(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final ArmyWindow window) {
        window.endWindowClause();
        this.context.onAddWindow(window.windowName());
        List<_Window> windowList = this.windowList;
        if (windowList == null) {
            windowList = new ArrayList<>();
            this.windowList = windowList;
        } else if (!(window instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        windowList.add(window);
        return this;
    }

    /**
     * @see #window(Consumer)
     * @see #ifWindow(Consumer)
     */
    private _OrderBySpec<I> dynamicWindow(final boolean required, final Consumer<MySQLWindows> consumer) {
        final MySQLWindowBuilderImpl builder = new MySQLWindowBuilderImpl(this);
        consumer.accept(builder);
        final ArmyWindow lastWindow = builder.lastWindow;
        if (lastWindow != null) {
            builder.lastWindow = null;
            this.onAddWindow(lastWindow);
        }
        if (required && this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }


    private static final class SimpleSelect<I extends Item> extends MySQLQueries<I>
            implements Select {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext,
                             Function<? super Select, I> function, @Nullable CriteriaContext leftContext) {
            super(spec, CriteriaContexts.primaryQuery(spec, outerContext, leftContext));
            this.function = function;
        }


        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(null, this.context, this::bracketEnd, null);
            return new SimpleSelect<>(null, bracket.context, bracket::parenRowSetEnd, null);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));
            return new ComplexSelect<>(this.context, unionFunc);
        }

        private I bracketEnd(final Select query) {
            this.endQueryBeforeSelect((BracketSelect<?>) query);
            return this.function.apply(this);
        }


    }//SimpleSelect

    private static final class SimpleSubQuery<I extends Item> extends MySQLQueries<I>
            implements SubQuery {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext outerContext,
                               Function<? super SubQuery, I> function, @Nullable CriteriaContext leftContext) {
            super(withSpec, CriteriaContexts.subQueryContext(withSpec, outerContext, leftContext));
            this.function = function;
        }

        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(null, this.context, this::bracketEnd, null);
            return new SimpleSubQuery<>(null, bracket.context, bracket::parenRowSetEnd, null);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubQuery(this, unionType, rowSet));
            return new ComplexSubQuery<>(this.context, unionFunc);
        }

        private I bracketEnd(SubQuery query) {
            ContextStack.pop(this.context.endContextBeforeSelect());
            return this.function.apply(query);
        }

    }//SimpleSubQuery


    enum MySQLLockMode implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE),
        LOCK_IN_SHARE_MODE(_Constant.SPACE_LOCK_IN_SHARE_MODE),
        FOR_SHARE(_Constant.SPACE_FOR_SHARE);

        private final String spaceWords;

        MySQLLockMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.sqlWordsToString(this);
        }

    }//MySQLLock


    private static final class NamedWindowAsClause<I extends Item> implements MySQLQuery._WindowAsClause<I> {

        private final MySQLQueries<I> stmt;

        private final String windowName;

        /**
         * @see #window(String)
         * @see #comma(String)
         */
        private NamedWindowAsClause(MySQLQueries<I> stmt, String windowName) {
            if (!_StringUtils.hasText(windowName)) {
                throw ContextStack.criteriaError(stmt.context, _Exceptions::namedWindowNoText);
            }
            this.stmt = stmt;
            this.windowName = windowName;
        }

        @Override
        public _WindowCommaSpec<I> as() {
            return this.stmt.onAddWindow(WindowClause.namedGlobalWindow(this.stmt.context, this.windowName));
        }

        @Override
        public _WindowCommaSpec<I> as(@Nullable String existingWindowName) {
            return this.stmt.onAddWindow(
                    WindowClause.namedRefWindow(this.stmt.context, this.windowName, existingWindowName)
            );
        }

        @Override
        public _WindowCommaSpec<I> as(@Nullable String existingWindowName,
                                      Consumer<Window._SimplePartitionBySpec> consumer) {
            final Window._SimplePartitionBySpec clause;
            clause = WindowClause.namedWindow(this.windowName, this.stmt.context, existingWindowName);
            consumer.accept(clause);
            return this.stmt.onAddWindow((ArmyWindow) clause);
        }

        @Override
        public _WindowCommaSpec<I> as(Consumer<Window._SimplePartitionBySpec> consumer) {
            final Window._SimplePartitionBySpec clause;
            clause = WindowClause.namedWindow(this.windowName, this.stmt.context, null);
            consumer.accept(clause);
            return this.stmt.onAddWindow((ArmyWindow) clause);
        }


    }//NamedWindowAsClause


    /**
     * @see #window(Consumer)
     * @see #ifWindow(Consumer)
     */
    private static final class MySQLWindowBuilderImpl implements MySQLWindows {

        private final MySQLQueries<?> stmt;

        private ArmyWindow lastWindow;

        private MySQLWindowBuilderImpl(MySQLQueries<?> stmt) {
            this.stmt = stmt;
        }

        @Override
        public Window._SimplePartitionBySpec window(String windowName, SQLs.WordAs as) {
            return this.window(windowName, as, null);
        }

        @Override
        public Window._SimplePartitionBySpec window(String windowName, SQLs.WordAs as
                , @Nullable String existingWindowName) {

            final ArmyWindow lastWindow = this.lastWindow;
            if (lastWindow != null) {
                this.stmt.onAddWindow(lastWindow);
            }

            final Window._SimplePartitionBySpec clause;
            clause = WindowClause.namedWindow(windowName, this.stmt.context, existingWindowName);
            this.lastWindow = (ArmyWindow) clause;
            return clause;
        }


    }//MySQLWindowBuilderImpl


    private static final class PartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_IndexHintJoinSpec<I>>
            implements MySQLQuery._PartitionJoinSpec<I> {

        private final MySQLQueries<I> stmt;

        private PartitionJoinClause(MySQLQueries<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _IndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLQueries<I> stmt = this.stmt;

            MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//PartitionJoinClause


    private static final class OnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<_IndexHintOnSpec<I>, _JoinSpec<I>>
            implements _IndexHintOnSpec<I> {

        private OnTableBlock(_JoinType joinType, @Nullable SQLWords itemWord
                , TabularItem tableItem, String alias, _JoinSpec<I> stmt) {
            super(joinType, itemWord, tableItem, alias, stmt);
        }

        private OnTableBlock(MySQLSupports.MySQLBlockParams params, _JoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//OnTableBlock

    private static final class PartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_IndexHintOnSpec<I>>
            implements MySQLQuery._PartitionOnSpec<I> {

        private final MySQLQueries<I> stmt;

        private PartitionOnClause(MySQLQueries<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _IndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLQueries<I> stmt = this.stmt;
            final OnTableBlock<I> block;
            block = new OnTableBlock<>(params, stmt);
            stmt.context.onAddBlock(block);
            return block;
        }


    }//PartitionOnClause

    private static final class MySQLCteComma<I extends Item>
            extends WithBuilderSelectClauseDispatcher<
            MySQLCtes,
            MySQLQuery._SelectSpec<I>,
            MySQLSyntax.Modifier,
            MySQLQuery._MySQLSelectCommaSpec<I>,
            MySQLQuery._FromSpec<I>>
            implements MySQLQuery._CteComma<I> {

        private final boolean recursive;
        private final MySQLQueries<I> statement;

        private final StaticComplexCommand<_CteComma<I>> complexCommand;

        private MySQLCteComma(MySQLQueries<I> statement, boolean recursive) {
            super(statement.context);
            this.statement = statement;
            this.recursive = recursive;
            this.complexCommand = new StaticComplexCommand<>(statement.context, this);

        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I>> comma(final String name) {
            return this.complexCommand.nextCte(name);
        }

        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {

            final MySQLQueries<I> statement = this.statement;
            statement.endStaticWithClause(this.recursive);
            return statement.leftParen();
        }

        @Override
        MySQLCtes createCteBuilder(boolean recursive, CriteriaContext withClauseContext) {
            return MySQLSupports.mySQLCteBuilder(recursive, withClauseContext);
        }

        @Override
        MySQLQueries<I> onSelectClause(@Nullable _WithClauseSpec spec) {
            this.statement.endStaticWithClause(this.recursive);
            return this.statement;
        }

    }//MySQLCteComma


    private static final class StaticComplexCommand<I extends Item>
            extends SimpleQueries.ComplexSelectCommand<
            MySQLs.Modifier,
            _MySQLSelectCommaSpec<_AsCteClause<I>>,
            _FromSpec<_AsCteClause<I>>,
            _StaticCteAsClause<I>>
            implements MySQLQuery._StaticCteLeftParenSpec<I>
            , _RightParenClause<_StaticCteAsClause<I>>
            , _SelectSpec<_AsCteClause<I>>
            , _AsCteClause<I> {

        private final I cteComma;

        private String cteName;

        private List<String> columnAliasList;

        private StaticComplexCommand(CriteriaContext context, I cteComma) {
            super(context);
            this.cteComma = cteComma;
        }

        @Override
        public _SelectSpec<_AsCteClause<I>> as() {
            if (this.cteName == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }

        @Override
        public I asCte() {
            return this.cteComma;
        }

        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<_AsCteClause<I>>>> leftParen() {
            final BracketSubQuery<_AsCteClause<I>> bracket;
            bracket = new BracketSubQuery<>(null, this.context, this::queryEnd, null);
            return new SimpleSubQuery<>(null, bracket.context, bracket::parenRowSetEnd, null);
        }

        @Override
        MySQLQueries<_AsCteClause<I>> createSelectClause() {
            return new SimpleSubQuery<>(null, this.context, this::queryEnd, null);
        }

        _StaticCteAsClause<I> columnAliasClauseEnd(final List<String> list) {
            this.columnAliasList = list;
            this.context.onCteColumnAlias(this.cteName, list);
            return this;
        }

        private _AsCteClause<I> queryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.context, this.cteName, this.columnAliasList, query);
            this.cteName = null;
            this.columnAliasList = null;
            return this;
        }

        private MySQLQuery._StaticCteLeftParenSpec<I> nextCte(final @Nullable String cteName) {
            if (this.cteName != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (cteName == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.context.onStartCte(cteName);
            this.cteName = cteName;
            return this;
        }


    }//StaticComplexCommand


    static abstract class MySQLBracketQuery<I extends Item>
            extends BracketRowSet<
            I,
            _UnionOrderBySpec<I>,
            _UnionLimitSpec<I>,
            _AsQueryClause<I>,
            Object,
            Object,
            _QueryWithComplexSpec<I>> implements _UnionOrderBySpec<I> {


        private MySQLBracketQuery(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext,
                                  @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.bracketContext(spec, outerContext, leftContext));
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLBracketQuery

    private static final class BracketSelect<I extends Item> extends MySQLBracketQuery<I>
            implements Select {

        private final Function<? super Select, I> function;

        private BracketSelect(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext,
                              Function<? super Select, I> function, @Nullable CriteriaContext leftContext) {
            super(spec, outerContext, leftContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));
            return new ComplexSelect<>(this.context, unionFunc);
        }


    }//BracketSelect


    private static final class BracketSubQuery<I extends Item> extends MySQLBracketQuery<I>
            implements SubQuery {

        private final Function<? super SubQuery, I> function;

        private BracketSubQuery(@Nullable _WithClauseSpec spec, CriteriaContext outerContext,
                                Function<? super SubQuery, I> function, @Nullable CriteriaContext leftContext) {
            super(spec, outerContext, leftContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubQuery(this, unionType, rowSet));
            return new ComplexSubQuery<>(this.context, unionFunc);
        }


    }//BracketSubQuery


    private static abstract class ComplexQueries<I extends Item>
            extends WithBuilderSelectClauseDispatcher<
            MySQLCtes,
            _QueryComplexSpec<I>,
            MySQLs.Modifier,
            _MySQLSelectCommaSpec<I>,
            _FromSpec<I>>
            implements MySQLQuery._QueryWithComplexSpec<I> {

        final CriteriaContext leftContext;

        final Function<RowSet, I> function;

        private ComplexQueries(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext.getOuterContext());
            this.leftContext = leftContext;
            this.function = function;
        }

        @Override
        final MySQLCtes createCteBuilder(boolean recursive, CriteriaContext withClauseContext) {
            return MySQLSupports.mySQLCteBuilder(recursive, withClauseContext);
        }


    }//ComplexQuery


    private static final class ComplexSubQuery<I extends Item> extends ComplexQueries<I> {

        private ComplexSubQuery(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        @Override
        public MySQLValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return MySQLSimpleValues.subValues(outerContext, this::valuesEnd)
                    .values(consumer);
        }

        @Override
        public MySQLValues._ValuesLeftParenClause<I> values() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return MySQLSimpleValues.subValues(outerContext, this::valuesEnd)
                    .values();
        }

        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.getWithClause(), outerContext, this.function, this.leftContext);
            return new ComplexSubQuery<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> leftParen(final Supplier<S> supplier) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.getWithClause(), outerContext, this.function, this.leftContext);

            final RowSet rowSet;
            rowSet = supplier.get();
            if (rowSet == null) {
                throw ContextStack.nullPointer(bracket.context);
            } else if (rowSet instanceof SubValues) {
                if (!(rowSet instanceof MySQLValues)) {
                    String m = String.format("%s not MySQL SubValues statement.", rowSet.getClass().getName());
                    throw ContextStack.criteriaError(bracket.context, m);
                }
            } else if (!(rowSet instanceof SubQuery
                    && (rowSet instanceof MySQLQuery || rowSet instanceof StandardQuery))) {
                String m = String.format("%s not standard SubQuery or MySQL SubQuery statement.",
                        rowSet.getClass().getName());
                throw ContextStack.criteriaError(bracket.context, m);
            }
            bracket.parenRowSetEnd(rowSet);
            return bracket;
        }

        @Override
        MySQLQueries<I> onSelectClause(@Nullable _WithClauseSpec spec) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return new SimpleSubQuery<>(spec, outerContext, this.function, leftContext);
        }

        private I valuesEnd(SubValues values) {
            return this.function.apply(values);
        }

    }//ComplexSubQuery


    private static final class ComplexSelect<I extends Item> extends ComplexQueries<I> {

        private final Function<RowSet, I> function;

        private ComplexSelect(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
            this.function = function;
        }


        @Override
        public MySQLValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return MySQLSimpleValues.primaryValues(this.outerContext, this::valuesEnd)
                    .values(consumer);
        }

        @Override
        public MySQLValues._ValuesLeftParenClause<I> values() {
            return MySQLSimpleValues.primaryValues(this.outerContext, this::valuesEnd)
                    .values();
        }

        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.getWithClause(), this.outerContext, this.function, this.leftContext);
            return new ComplexSelect<>(bracket.context, bracket::parenRowSetEnd);
        }


        @Override
        public <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> leftParen(Supplier<S> supplier) {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.getWithClause(), this.outerContext, this.function, this.leftContext);

            final RowSet rowSet;
            rowSet = supplier.get();
            if (rowSet == null) {
                throw ContextStack.nullPointer(bracket.context);
            } else if (rowSet instanceof Values) {
                if (!(rowSet instanceof MySQLValues)) {
                    String m = String.format("%s isn't MySQL Values statement.", rowSet.getClass().getName());
                    throw ContextStack.criteriaError(bracket.context, m);
                }
            } else if (!(rowSet instanceof Select
                    && (rowSet instanceof MySQLQuery || rowSet instanceof StandardQuery))) {
                String m = String.format("%s isn't standard Select or MySQL Select statement.",
                        rowSet.getClass().getName());
                throw ContextStack.criteriaError(bracket.context, m);
            }
            bracket.parenRowSetEnd(rowSet);
            return bracket;
        }

        @Override
        MySQLQueries<I> onSelectClause(final @Nullable _WithClauseSpec spec) {
            return new SimpleSelect<>(spec, this.outerContext, this.function, this.leftContext);
        }

        private I valuesEnd(Values values) {
            return this.function.apply(values);
        }


    }//ComplexSelect


}
