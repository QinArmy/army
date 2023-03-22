package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;


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
        Statement._AsClause<MySQLQuery._ParensJoinSpec<I>>,
        MySQLQuery._JoinSpec<I>,
        MySQLQuery._IndexHintOnSpec<I>,
        Statement._AsParensOnClause<MySQLQuery._JoinSpec<I>>,
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
        MySQLQuery._ParensJoinSpec<I>,
        MySQLQuery._WhereAndSpec<I>,
        MySQLQuery._GroupByWithRollupSpec<I>,
        MySQLQuery._HavingSpec<I>,
        MySQLQuery._WindowCommaSpec<I>,
        MySQLQuery._OrderByWithRollupSpec<I>,
        MySQLQuery._LockOfTableSpec<I>,
        OrderByClause.OrderByEventListener {

    static _WithSpec<Select> simpleQuery() {
        return new SimpleSelect<>(null, null, SQLs._SELECT_IDENTITY);
    }

    static <I extends Item> MySQLQueries<I> fromDispatcher(ArmyStmtSpec spec,
                                                           Function<? super Select, I> function) {
        return new SimpleSelect<>(spec, null, function);
    }

    static <I extends Item> _WithSpec<I> subQuery(CriteriaContext outerContext,
                                                  Function<? super SubQuery, I> function) {
        return new SimpleSubQuery<>(null, outerContext, function);
    }

    static <I extends Item> MySQLQueries<I> fromSubDispatcher(ArmyStmtSpec spec,
                                                              Function<? super SubQuery, I> function) {
        return new SimpleSubQuery<>(spec, null, function);
    }

    static <I extends Item> _CteComma<I> staticCteComma(CriteriaContext context, boolean recursive,
                                                        Function<Boolean, I> function) {
        return new StaticCteComma<>(context, recursive, function);
    }

    /**
     * @see #onFromTable(_JoinType, TableModifier, TableMeta, String)
     * @see #onFromDerived(_JoinType, DerivedModifier, DerivedTable)
     * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
     * @see #getIndexHintClause()
     * @see #getFromClauseDerived()
     */
    private _TableBlock fromCrossBlock;

    /**
     * @see #onOrderByEvent()
     */
    private Boolean groupByWithRollup;

    private List<_Window> windowList;

    private boolean orderByWithRollup;

    private MySQLLockMode lockMode;

    private List<String> ofTableList;

    private LockWaitOption lockWaitOption;

    private List<String> intoVarList;

    MySQLQueries(@Nullable ArmyStmtSpec spec, CriteriaContext context) {
        super(spec, context);
    }

    @Override
    public final _StaticCteParensSpec<_SelectSpec<I>> with(String name) {
        return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _StaticCteParensSpec<_SelectSpec<I>> withRecursive(String name) {
        return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _JoinSpec<I> from(Function<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final _JoinSpec<I> crossJoin(Function<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> join(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> straightJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _PartitionJoinSpec<I> from(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _PartitionJoinSpec<I> crossJoin(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
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
    public final _JoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
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
    public final _IndexHintJoinSpec<I> ifUseIndex(Consumer<_IndexPurposeBySpec<Object>> consumer) {
        this.getIndexHintClause().ifUseIndex(consumer);
        return this;
    }

    @Override
    public final _IndexHintJoinSpec<I> ifIgnoreIndex(Consumer<_IndexPurposeBySpec<Object>> consumer) {
        this.getIndexHintClause().ifIgnoreIndex(consumer);
        return this;
    }

    @Override
    public final _IndexHintJoinSpec<I> ifForceIndex(Consumer<_IndexPurposeBySpec<Object>> consumer) {
        this.getIndexHintClause().ifForceIndex(consumer);
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(String first, String... rest) {
        this.getFromClauseDerived().parens(first, rest);
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().parens(this.context, consumer);
        return this;
    }

    @Override
    public final _JoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().ifParens(this.context, consumer);
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
    public final _OrderBySpec<I> windows(Consumer<MySQLWindows> consumer) {
        return this.dynamicWindow(true, consumer);
    }

    @Override
    public final _OrderBySpec<I> ifWindows(Consumer<MySQLWindows> consumer) {
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
    public final _LockWaitOptionSpec<I> of(String tableAlias) {
        if (this.lockMode == null) {
            this.ofTableList = Collections.emptyList();
        } else {
            this.ofTableList = Collections.singletonList(tableAlias);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(String firstTableAlias, String... restTableAlias) {
        if (this.lockMode == null) {
            this.ofTableList = Collections.emptyList();
        } else {
            this.ofTableList = _ArrayUtils.unmodifiableListOf(firstTableAlias, restTableAlias);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<String>> consumer) {
        if (this.lockMode == null) {
            this.ofTableList = Collections.emptyList();
        } else {
            this.ofTableList = CriteriaUtils.stringList(this.context, true, consumer);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<String>> consumer) {
        if (this.lockMode == null) {
            this.ofTableList = Collections.emptyList();
        } else {
            this.ofTableList = CriteriaUtils.stringList(this.context, false, consumer);
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
        this.intoVarList = _ArrayUtils.unmodifiableListOf(firstVarName, rest);
        return this;
    }

    @Override
    public final _AsQueryClause<I> into(Consumer<Consumer<String>> consumer) {
        this.intoVarList = CriteriaUtils.stringList(this.context, true, consumer);
        return this;
    }

    @Override
    public final _AsQueryClause<I> ifInto(Consumer<Consumer<String>> consumer) {
        this.intoVarList = CriteriaUtils.stringList(this.context, false, consumer);
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
        final List<String> list = this.ofTableList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
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
    final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final _IndexHintJoinSpec<I> onFromTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                            String alias) {
        final MySQLSupports.FromClausePurposeTableBlock<_IndexHintJoinSpec<I>> block;
        block = new MySQLSupports.FromClausePurposeTableBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable DerivedModifier modifier,
                                                      DerivedTable table) {
        return alias -> {
            final TableBlocks.FromClauseAliasDerivedBlock block;
            block = TableBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        };
    }


    @Override
    final _JoinSpec<I> onFromCte(_JoinType joinType, @Nullable DerivedModifier modifier, CteItem cteItem,
                                 String alias) {
        final _TableBlock block;
        block = TableBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _IndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                          String alias) {
        final JoinClauseTableBlock<I> block;
        block = new JoinClauseTableBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsParensOnClause<_JoinSpec<I>> onJoinDerived(_JoinType joinType, @Nullable DerivedModifier modifier,
                                                        DerivedTable table) {
        return alias -> {
            final TableBlocks.JoinClauseAliasDerivedBlock<_JoinSpec<I>> block;
            block = TableBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _OnClause<_JoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable DerivedModifier modifier, CteItem cteItem,
                                            String alias) {
        final TableBlocks.JoinClauseCteBlock<_JoinSpec<I>> block;
        block = TableBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #from(Function)
     * @see #crossJoin(Function)
     */
    private _JoinSpec<I> fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        final _TableBlock block;
        block = TableBlocks.fromNestedBlock(joinType, nestedItems);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    /**
     * @see #leftJoin(Function)
     * @see #join(Function)
     * @see #rightJoin(Function)
     * @see #fullJoin(Function)
     * @see #straightJoin(Function)
     */
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {

        final TableBlocks.JoinClauseNestedBlock<_JoinSpec<I>> block;
        block = TableBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }

    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    @SuppressWarnings("unchecked")
    private MySQLSupports.FromClausePurposeTableBlock<_IndexHintJoinSpec<I>> getIndexHintClause() {
        final _TableBlock block = this.fromCrossBlock;
        if (this.context.lastBlock() != block || !(block instanceof MySQLSupports.FromClausePurposeTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (MySQLSupports.FromClausePurposeTableBlock<_IndexHintJoinSpec<I>>) block;
    }


    /**
     * @see #onFromDerived(_JoinType, DerivedModifier, DerivedTable)
     * @see #parens(String, String...)
     * @see #parens(Consumer)
     * @see #ifParens(Consumer)
     */
    private TableBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TableBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TableBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TableBlocks.FromClauseAliasDerivedBlock) block;
    }

    /**
     * @see #windows(Consumer)
     * @see #window(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final ArmyWindow window) {
        window.endWindowClause();
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
     * @see #windows(Consumer)
     * @see #ifWindows(Consumer)
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


    private static final class SimpleSelect<I extends Item> extends MySQLQueries<I> implements Select {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerBracketContext,
                             Function<? super Select, I> function) {
            super(spec, CriteriaContexts.primaryQueryContext(spec, outerBracketContext, null));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);

            return function.apply(new SimpleSelect<>(null, bracket.context, bracket::parensEnd));
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));
            return new SelectDispatcher<>(this.context, unionFunc);
        }


    }//SimpleSelect

    private static final class SimpleSubQuery<I extends Item> extends MySQLQueries<I> implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                               Function<? super SubQuery, I> function) {
            super(spec, CriteriaContexts.subQueryContext(spec, outerContext, null));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);

            return function.apply(MySQLQueries.subQuery(bracket.context, bracket::parensEnd));
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubQuery(this, unionType, rowSet));
            return new SubQueryDispatcher<>(this.context, unionFunc);
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
     * @see #windows(Consumer)
     * @see #ifWindows(Consumer)
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

            MySQLSupports.FromClausePurposeTableBlock<_IndexHintJoinSpec<I>> block;
            block = new MySQLSupports.FromClausePurposeTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//PartitionJoinClause


    private static final class JoinClauseTableBlock<I extends Item>
            extends MySQLSupports.MySQLJoinClauseBlock<
            _IndexPurposeBySpec<Object>,
            _IndexHintOnSpec<I>,
            _JoinSpec<I>>
            implements _IndexHintOnSpec<I> {

        private JoinClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias, _JoinSpec<I> stmt) {
            super(joinType, table, alias, stmt);
        }

        private JoinClauseTableBlock(MySQLSupports.MySQLBlockParams params, _JoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//JoinClauseIndexHintBlock


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
            final JoinClauseTableBlock<I> block;
            block = new JoinClauseTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause


    private static final class StaticCteComma<I extends Item> implements MySQLQuery._CteComma<I> {

        private final CriteriaContext context;

        private final boolean recursive;

        private final Function<Boolean, I> function;

        /**
         * @see #staticCteComma(CriteriaContext, boolean, Function)
         */
        private StaticCteComma(CriteriaContext context, final boolean recursive, Function<Boolean, I> function) {
            context.onBeforeWithClause(recursive);
            this.context = context;
            this.recursive = recursive;
            this.function = function;
        }

        @Override
        public _StaticCteParensSpec<I> comma(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.context.onStartCte(name);
            return new StaticCteParensClause<>(this, name);
        }

        @Override
        public I space() {
            return this.function.apply(this.recursive);
        }


    }//StaticCteComma


    private static final class StaticCteParensClause<I extends Item>
            implements _StaticCteParensSpec<I> {

        private final StaticCteComma<I> comma;

        private final String name;

        private List<String> columnAliasList;

        /**
         * @see StaticCteComma#comma(String)
         */
        private StaticCteParensClause(StaticCteComma<I> comma, String name) {
            this.comma = comma;
            this.name = name;
        }

        @Override
        public _StaticCteAsClause<I> parens(String first, String... rest) {
            return this.onColumnAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public _StaticCteAsClause<I> parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAliasList(CriteriaUtils.stringList(this.comma.context, true, consumer));
        }

        @Override
        public _StaticCteAsClause<I> ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAliasList(CriteriaUtils.stringList(this.comma.context, false, consumer));
        }

        @Override
        public _CteComma<I> as(Function<_SelectSpec<_CteComma<I>>, _CteComma<I>> function) {
            return function.apply(MySQLQueries.subQuery(this.comma.context, this::subQueryEnd));
        }

        private _StaticCteAsClause<I> onColumnAliasList(final List<String> list) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.comma.context);
            }
            this.columnAliasList = list;
            if (list.size() > 0) {
                this.comma.context.onCteColumnAlias(this.name, list);
            }
            return this;
        }

        private _CteComma<I> subQueryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.comma.context, this.name, this.columnAliasList, query);
            return this.comma;
        }


    }//StaticCteParensClause


    static abstract class MySQLBracketQuery<I extends Item>
            extends BracketRowSet<
            I,
            _UnionOrderBySpec<I>,
            _UnionLimitSpec<I>,
            _AsQueryClause<I>,
            Object,
            Object,
            _QueryWithComplexSpec<I>>
            implements _UnionOrderBySpec<I> {


        private MySQLBracketQuery(ArmyStmtSpec spec) {
            super(spec);

        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLBracketQuery

    private static final class BracketSelect<I extends Item> extends MySQLBracketQuery<I> implements Select {

        private final Function<? super Select, I> function;

        private BracketSelect(ArmyStmtSpec spec, Function<? super Select, I> function) {
            super(spec);
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
            return new SelectDispatcher<>(this.context, unionFunc);
        }


    }//BracketSelect


    private static final class BracketSubQuery<I extends Item> extends MySQLBracketQuery<I>
            implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private BracketSubQuery(ArmyStmtSpec spec, Function<? super SubQuery, I> function) {
            super(spec);
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
            return new SubQueryDispatcher<>(this.context, unionFunc);
        }


    }//BracketSubQuery


    private static abstract class MySQLQueryDispatcher<I extends Item>
            extends WithBuilderSelectClauseDispatcher<
            MySQLCtes,
            MySQLQuery._QueryComplexSpec<I>,
            MySQLSyntax.Modifier,
            MySQLQuery._MySQLSelectCommaSpec<I>,
            MySQLQuery._FromSpec<I>>
            implements MySQLQuery._QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private MySQLQueryDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext.getOuterContext(), leftContext);
            this.function = function;
        }

        private MySQLQueryDispatcher(MySQLBracketQuery<?> bracket, Function<RowSet, I> function) {
            super(bracket.context, null);
            this.function = function;
        }


        @Override
        public final _StaticCteParensSpec<_QueryComplexSpec<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final _StaticCteParensSpec<_QueryComplexSpec<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }


        @Override
        final MySQLCtes createCteBuilder(boolean recursive, CriteriaContext context) {
            return MySQLSupports.mySQLCteBuilder(recursive, context);
        }


    }//MySQLQueryDispatcher


    private static final class SelectDispatcher<I extends Item> extends MySQLQueryDispatcher<I> {

        private SelectDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private SelectDispatcher(BracketSelect<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);

            return function.apply(new SelectDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public MySQLValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            this.endDispatcher();

            return MySQLSimpleValues.fromDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public MySQLValues._ValuesLeftParenClause<I> values() {
            this.endDispatcher();

            return MySQLSimpleValues.fromDispatcher(this, this.function)
                    .values();
        }

        @Override
        MySQLQueries<I> createSelectClause() {
            this.endDispatcher();

            return MySQLQueries.fromDispatcher(this, this.function);
        }


    }//SelectDispatcher


    private static final class SubQueryDispatcher<I extends Item> extends MySQLQueryDispatcher<I> {

        private SubQueryDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private SubQueryDispatcher(BracketSubQuery<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);

            return function.apply(new SubQueryDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public MySQLValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            this.endDispatcher();

            return MySQLSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public MySQLValues._ValuesLeftParenClause<I> values() {
            this.endDispatcher();

            return MySQLSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

        @Override
        MySQLQueries<I> createSelectClause() {
            this.endDispatcher();

            return MySQLQueries.fromSubDispatcher(this, this.function);
        }


    }//SubQueryDispatcher


}
