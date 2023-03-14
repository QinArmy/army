package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Statement;
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
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of the implementation of {@link MySQLQuery}:
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLQueries<I extends Item, WE> extends SimpleQueries.WithCteSimpleQueries<
        I,
        MySQLCtes,
        WE,
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
        MySQLQuery._MySQLSelectClause<I>,
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


    static <I extends Item> MySQLSimpleQuery<I> primaryQuery(@Nullable _WithClauseSpec spec
            , @Nullable CriteriaContext outerContext, Function<Select, I> function) {
        return new SimpleSelect<>(spec, outerContext, function, null);
    }


    static <I extends Item> MySQLSimpleQuery<I> subQuery(@Nullable _WithClauseSpec spec, CriteriaContext outerContext
            , Function<SubQuery, I> function) {
        return new SimpleSubQuery<>(spec, outerContext, function, null);
    }

    static <I extends Item> Function<String, _StaticCteParensSpec<I>> complexCte(CriteriaContext context,
                                                                                 I cteComma) {
        return new StaticCteParensClause<>(context, cteComma)::nextCte;
    }

    /**
     * @see #onFromTable(_JoinType, TableModifier, TableMeta, String)
     * @see #onFromDerived(_JoinType, DerivedModifier, DerivedTable)
     * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
     * @see #getIndexHintClause()
     * @see #getLastDerived()
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

    MySQLQueries(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }


    @Override
    public final _PartitionJoinSpec<I> from(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> from() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
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
    public final _JoinSpec<I> parens(String first, String... rest) {
        this.getLastDerived().setColumnAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getLastDerived().setColumnAliasList(CriteriaUtils.columnAliasList(true, consumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
        this.getLastDerived().setColumnAliasList(CriteriaUtils.columnAliasList(false, consumer));
        return this;
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
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> join() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> rightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> fullJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> straightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> crossJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
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
    final TableModifier tableModifier(final @Nullable TableModifier modifier) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    @Override
    final DerivedModifier derivedModifier(final @Nullable DerivedModifier modifier) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.errorModifier(this.context, modifier);
        }
        return modifier;
    }

    @Override
    final _IndexHintJoinSpec<I> onFromTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                            String alias) {
        final MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, this);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable DerivedModifier modifier,
                                                      DerivedTable table) {
        return alias -> {
            final _TableBlock block;
            block = new TableBlock.NoOnModifierDerivedBlock(joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        };
    }


    @Override
    final _JoinSpec<I> onFromCte(_JoinType joinType, @Nullable DerivedModifier modifier, CteItem cteItem,
                                 String alias) {
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _IndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                          String alias) {
        final IndexHintOnBlock<I> block;
        block = new IndexHintOnBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsParensOnClause<_JoinSpec<I>> onJoinDerived(_JoinType joinType, @Nullable DerivedModifier modifier,
                                                        DerivedTable table) {
        return alias -> {
            final OnClauseTableBlock.OnModifierParensBlock<_JoinSpec<I>> block;
            block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _OnClause<_JoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable DerivedModifier modifier, CteItem cteItem,
                                            String alias) {
        final OnClauseTableBlock<_JoinSpec<I>> block;
        block = new OnClauseTableBlock<>(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
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
    private _JoinSpec<I> fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    /**
     * @see #leftJoin()
     * @see #join()
     * @see #rightJoin()
     * @see #fullJoin()
     * @see #straightJoin()
     */
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
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
    @SuppressWarnings("unchecked")
    private _QueryIndexHintClause<_IndexHintJoinSpec<I>> getIndexHintClause() {
        final _TableBlock block = this.fromCrossBlock;
        if (this.context.lastBlock() != block || !(block instanceof MySQLSupports.MySQLNoOnBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>>) block).getUseIndexClause();
    }

    /**
     * @see #onFromDerived(_JoinType, DerivedModifier, DerivedTable)
     * @see #parens(String, String...)
     * @see #parens(Consumer)
     * @see #ifParens(Consumer)
     */
    private ArmyDerivedTable getLastDerived() {
        final _TableBlock lastBlock = this.fromCrossBlock;
        if (!(lastBlock instanceof TableBlock.NoOnModifierDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (this.context.lastBlock() != lastBlock) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (ArmyDerivedTable) ((TableBlock.NoOnModifierDerivedBlock) lastBlock).tableItem;
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


    private static abstract class MySQLSimpleQuery<I extends Item> extends MySQLQueries<I, MySQLQuery._SelectSpec<I>>
            implements MySQLQuery._WithSpec<I> {

        private MySQLSimpleQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
        }


        @Override
        public final _StaticCteParensSpec<_CteComma<I>> with(String name) {
            return new MySQLCteComma<>(false, this).function.apply(name);

        }

        @Override
        public final _StaticCteParensSpec<_CteComma<I>> withRecursive(String name) {
            return new MySQLCteComma<>(true, this).function.apply(name);
        }


    }//MySQLSimpleQuery


    private static final class SimpleSelect<I extends Item> extends MySQLSimpleQuery<I> implements Select {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext,
                             Function<? super Select, I> function, @Nullable CriteriaContext leftContext) {
            super(spec, CriteriaContexts.primaryQuery(spec, outerContext, leftContext));
            this.function = function;
        }


        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endStmtBeforeCommand();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.getWithClause(), this.context.getOuterContext(), this.function, null);
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


    }//SimpleSelect

    private static final class SimpleSubQuery<I extends Item> extends MySQLSimpleQuery<I> implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext outerContext,
                               Function<? super SubQuery, I> function, @Nullable CriteriaContext leftContext) {
            super(withSpec, CriteriaContexts.subQueryContext(withSpec, outerContext, leftContext));
            this.function = function;
        }

        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endStmtBeforeCommand();
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.getWithClause(), this.context.getNonNullOuterContext(), this.function,
                    null);
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

        private final MySQLQueries<I, ?> stmt;

        private final String windowName;

        /**
         * @see #window(String)
         * @see #comma(String)
         */
        private NamedWindowAsClause(MySQLQueries<I, ?> stmt, String windowName) {
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

        private final MySQLQueries<?, ?> stmt;

        private ArmyWindow lastWindow;

        private MySQLWindowBuilderImpl(MySQLQueries<?, ?> stmt) {
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

        private final MySQLQueries<I, ?> stmt;

        private PartitionJoinClause(MySQLQueries<I, ?> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _IndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLQueries<I, ?> stmt = this.stmt;

            MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//PartitionJoinClause


    private static final class IndexHintOnBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<_IndexHintOnSpec<I>, _JoinSpec<I>>
            implements _IndexHintOnSpec<I> {

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, _JoinSpec<I> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private IndexHintOnBlock(MySQLSupports.MySQLBlockParams params, _JoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//OnTableBlock


    private static final class PartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_IndexHintOnSpec<I>>
            implements MySQLQuery._PartitionOnSpec<I> {

        private final MySQLQueries<I, ?> stmt;

        private PartitionOnClause(MySQLQueries<I, ?> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _IndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLQueries<I, ?> stmt = this.stmt;
            final IndexHintOnBlock<I> block;
            block = new IndexHintOnBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause

    private static final class MySQLCteComma<I extends Item> implements MySQLQuery._CteComma<I> {

        private final boolean recursive;

        private final MySQLSimpleQuery<I> stmt;


        private final Function<String, _StaticCteParensSpec<_CteComma<I>>> function;

        /**
         * @see MySQLSimpleQuery#with(String)
         * @see MySQLSimpleQuery#withRecursive(String)
         */
        public MySQLCteComma(final boolean recursive, MySQLSimpleQuery<I> stmt) {
            stmt.context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.stmt = stmt;
            this.function = MySQLQueries.complexCte(stmt.context, this);
        }

        @Override
        public _StaticCteParensSpec<_CteComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _SelectSpec<I> space() {
            return this.stmt.endStaticWithClause(this.recursive);
        }


    }//MySQLCteComma

    private static final class MySQLComplexCteComma<I extends Item> implements MySQLQuery._ComplexCteComma<I> {

        private final boolean recursive;

        private final MySQLComplexQuery<I> stmt;


        private final Function<String, _StaticCteParensSpec<_ComplexCteComma<I>>> function;

        /**
         * @see MySQLComplexQuery#with(String)
         * @see MySQLComplexQuery#withRecursive(String)
         */
        public MySQLComplexCteComma(final boolean recursive, MySQLComplexQuery<I> stmt) {
            stmt.context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.stmt = stmt;
            this.function = MySQLQueries.complexCte(stmt.context, this);
        }

        @Override
        public _StaticCteParensSpec<_ComplexCteComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _QueryComplexSpec<I> space() {
            return this.stmt.endStaticWithClause(this.recursive);
        }


    }//MySQLCteComma


    private static final class StaticCteParensClause<I extends Item>
            implements _StaticCteParensSpec<I>, _AsCteClause<I> {

        private final CriteriaContext context;

        private final I cteComma;

        private String name;

        private List<String> columnAliasList;

        /**
         * @see #complexCte(CriteriaContext, Item)
         */
        private StaticCteParensClause(CriteriaContext context, I cteComma) {
            this.context = context;
            this.cteComma = cteComma;
        }

        @Override
        public _StaticCteAsClause<I> parens(String first, String... rest) {
            return this.columnAliasListEnd(_ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public _StaticCteAsClause<I> parens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw CriteriaUtils.columnAliasIsEmpty(this.context);
            }
            return this.columnAliasListEnd(_CollectionUtils.unmodifiableList(list));
        }

        @Override
        public _StaticCteAsClause<I> ifParens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.columnAliasListEnd(_CollectionUtils.unmodifiableList(list));
            } else {
                this.columnAliasList = null;
            }
            return this;
        }


        @Override
        public I as(Function<_SelectSpec<_AsCteClause<I>>, I> function) {
            return function.apply(new SimpleSubQuery<>(null, this.context, this::queryEnd, null));
        }

        @Override
        public I asCte() {
            return this.cteComma;
        }

        /**
         * @param columnAliasList unmodified list
         */
        private _StaticCteAsClause<I> columnAliasListEnd(final List<String> columnAliasList) {
            this.context.onCteColumnAlias(this.name, columnAliasList);
            this.columnAliasList = columnAliasList;
            return this;
        }

        private _StaticCteParensSpec<I> nextCte(final String name) {
            if (this.name != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.context.onStartCte(name);
            this.name = name;
            this.columnAliasList = null;
            return this;
        }

        private _AsCteClause<I> queryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, query);
            this.name = null;// clear for next cte
            this.columnAliasList = null;// clear for next cte
            return this;
        }


    }//StaticCteColumnAliasClause


    static abstract class MySQLBracketQuery<I extends Item>
            extends BracketRowSet<
            I,
            _UnionOrderBySpec<I>,
            _UnionLimitSpec<I>,
            _AsQueryClause<I>,
            Object,
            Object,
            _QueryWithComplexSpec<I>>
            implements _UnionOrderBySpec<I>,
            _Statement._WithClauseSpec {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private MySQLBracketQuery(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext,
                                  @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.bracketContext(spec, outerContext, leftContext));
            if (spec == null) {
                this.recursive = false;
                this.cteList = Collections.emptyList();
            } else {
                this.recursive = spec.isRecursive();
                this.cteList = spec.cteList();
                assert this.context.getCteList() == this.cteList;
            }
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            return this.cteList;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLBracketQuery

    private static final class BracketSelect<I extends Item> extends MySQLBracketQuery<I> implements Select {

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
            implements ArmySubQuery {

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


    private static abstract class MySQLComplexQuery<I extends Item> extends MySQLQueries<I, _QueryComplexSpec<I>>
            implements MySQLQuery._QueryWithComplexSpec<I> {

        final CriteriaContext leftContext;

        final Function<RowSet, I> function;

        private MySQLComplexQuery(CriteriaContext context, Function<RowSet, I> function,
                                  @Nullable CriteriaContext leftContext) {
            super(null, context);
            this.leftContext = leftContext;
            this.function = function;
        }


        @Override
        public final _StaticCteParensSpec<_ComplexCteComma<I>> with(String name) {
            return new MySQLComplexCteComma<>(false, this).function.apply(name);
        }

        @Override
        public final _StaticCteParensSpec<_ComplexCteComma<I>> withRecursive(String name) {
            return new MySQLComplexCteComma<>(true, this).function.apply(name);
        }


        @Override
        final I onAsQuery() {
            return this.function.apply(this);
        }


    }//ComplexQuery


    private static final class ComplexSubQuery<I extends Item> extends MySQLComplexQuery<I>
            implements ArmySubQuery {

        private ComplexSubQuery(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(CriteriaContexts.subQueryContext(null, leftContext.getNonNullOuterContext(), leftContext),
                    function, leftContext);
        }

        private ComplexSubQuery(Function<RowSet, I> function, CriteriaContext outerContext) {
            super(CriteriaContexts.subQueryContext(null, outerContext, null), function, null);
        }


        @Override
        public MySQLValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            this.endStmtBeforeCommand();
            assert this.getWithClause() == null;

            return MySQLSimpleValues.subValues(this.context.getNonNullOuterContext(), this::valuesEnd)
                    .values(consumer);
        }

        @Override
        public MySQLValues._ValuesLeftParenClause<I> values() {
            this.endStmtBeforeCommand();
            assert this.getWithClause() == null;

            return MySQLSimpleValues.subValues(this.context.getNonNullOuterContext(), this::valuesEnd)
                    .values();
        }

        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endStmtBeforeCommand();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.getWithClause(), this.context.getNonNullOuterContext(), this.function,
                    this.leftContext);
            return new ComplexSubQuery<>(bracket::parenRowSetEnd, bracket.context);
        }

        @Override
        public <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> parens(final Supplier<S> supplier) {
            this.endStmtBeforeCommand();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.getWithClause(), this.context.getNonNullOuterContext(), this.function,
                    this.leftContext);

            final RowSet rowSet;
            rowSet = ContextStack.unionQuerySupplier(supplier);
            if (rowSet instanceof SubValues) {
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
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));

            return new ComplexSubQuery<>(this.context, unionFunc);
        }


        private I valuesEnd(SubValues values) {
            return this.function.apply(values);
        }

    }//ComplexSubQuery


    private static final class ComplexSelect<I extends Item> extends MySQLComplexQuery<I> implements Select {


        private ComplexSelect(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(CriteriaContexts.primaryQuery(null, leftContext.getOuterContext(), leftContext), function, leftContext);
        }

        private ComplexSelect(Function<RowSet, I> function, CriteriaContext outerContext) {
            super(CriteriaContexts.primaryQuery(null, outerContext, null), function, null);
        }

        @Override
        public MySQLValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            this.endStmtBeforeCommand();
            assert this.getWithClause() == null;

            return MySQLSimpleValues.primaryValues(this.context.getOuterContext(), this::valuesEnd)
                    .values(consumer);
        }

        @Override
        public MySQLValues._ValuesLeftParenClause<I> values() {
            this.endStmtBeforeCommand();
            assert this.getWithClause() == null;

            return MySQLSimpleValues.primaryValues(this.context.getOuterContext(), this::valuesEnd)
                    .values();
        }


        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endStmtBeforeCommand();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.getWithClause(), this.context.getOuterContext(), this.function,
                    this.leftContext);
            return new ComplexSelect<>(bracket::parenRowSetEnd, bracket.context);
        }

        @Override
        public <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> parens(Supplier<S> supplier) {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.getWithClause(), this.context.getOuterContext(), this.function,
                    this.leftContext);

            final RowSet rowSet;
            rowSet = ContextStack.unionQuerySupplier(supplier);
            if (rowSet instanceof Values) {
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
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));
            return new ComplexSelect<>(this.context, unionFunc);
        }


        private I valuesEnd(Values values) {
            return this.function.apply(values);
        }


    }//ComplexSelect


}
