package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.mysql.MySQLDialect;

import javax.annotation.Nullable;

import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * <p>
 * This class is base class of the implementation of {@link MySQLQuery}:
 * * @since 1.0
 */
abstract class MySQLQueries<I extends Item> extends SimpleQueries<
        I,
        MySQLCtes,
        MySQLQuery._SelectSpec<I>,
        MySQLs.Modifier,
        MySQLQuery._MySQLSelectCommaSpec<I>,
        MySQLQuery._FromSpec<I>,
        MySQLQuery._IndexHintJoinSpec<I>,
        Statement._AsClause<MySQLQuery._ParensJoinSpec<I>>,
        MySQLQuery._JoinSpec<I>,
        Void,
        MySQLQuery._IndexHintOnSpec<I>,
        Statement._AsParensOnClause<MySQLQuery._JoinSpec<I>>,
        Statement._OnClause<MySQLQuery._JoinSpec<I>>,
        Void,
        MySQLQuery._GroupBySpec<I>,
        MySQLQuery._WhereAndSpec<I>,
        MySQLQuery._GroupByCommaSpec<I>,
        MySQLQuery._GroupByWithRollupSpec<I>,
        MySQLQuery._HavingAndSpec<I>,
        MySQLQuery._WindowSpec<I>,
        MySQLQuery._OrderByCommaSpec<I>,
        MySQLQuery._LimitSpec<I>,
        MySQLQuery._LockSpec<I>,
        Object,
        Object,
        MySQLQuery._QueryWithComplexSpec<I>>
        implements _MySQLQuery, MySQLQuery,
        MySQLQuery._WithSpec<I>,
        MySQLQuery._MySQLSelectCommaSpec<I>,
        MySQLQuery._IndexHintJoinSpec<I>,
        MySQLQuery._ParensJoinSpec<I>,
        MySQLQuery._WhereAndSpec<I>,
        MySQLQuery._GroupByCommaSpec<I>,
        MySQLQuery._HavingSpec<I>,
        MySQLQuery._HavingAndSpec<I>,
        MySQLQuery._WindowCommaSpec<I>,
        MySQLQuery._OrderByWithRollupSpec<I>,
        MySQLQuery._OrderByCommaSpec<I>,
        OrderByClause.OrderByEventListener {

    static _WithSpec<Select> simpleQuery() {
        return new SimpleSelect<>(null, null, SQLs.SIMPLE_SELECT);
    }

    static _WithSpec<_BatchSelectParamSpec> batchQuery() {
        return new SimpleSelect<>(null, null, MySQLQueries::mapToBatchSelect);
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

    private static _BatchSelectParamSpec mapToBatchSelect(final Select select) {
        final _BatchSelectParamSpec spec;
        if (select instanceof _Query) {
            spec = ((SimpleSelect<?>) select)::wrapToBatchSelect;
        } else if (select instanceof UnionSelect) {
            spec = ((UnionSelect) select)::wrapToBatchSelect;
        } else if (select instanceof BracketSelect) {
            spec = ((BracketSelect<?>) select)::wrapToBatchSelect;
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return spec;
    }

    /**
     * @see #onFromTable(_JoinType, TableModifier, TableMeta, String)
     * @see #onFromDerived(_JoinType, DerivedModifier, DerivedTable)
     * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
     * @see #getIndexHintClause()
     * @see #getFromClauseDerived()
     */
    private _TabularBlock fromCrossBlock;

    /**
     * @see #onOrderByEvent()
     */
    private Boolean groupByWithRollup;

    private List<_Window> windowList;

    private boolean orderByWithRollup;

    private _LockBlock lockBlock;

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
    public final Window._WindowAsClause<MySQLWindow._PartitionBySpec, _WindowCommaSpec<I>> window(String windowName) {
        return new NamedWindowAsClause<>(this.context, windowName, this::onAddWindow, MySQLSupports::namedWindow);
    }

    @Override
    public final Window._WindowAsClause<MySQLWindow._PartitionBySpec, _WindowCommaSpec<I>> comma(String windowName) {
        return new NamedWindowAsClause<>(this.context, windowName, this::onAddWindow, MySQLSupports::namedWindow);
    }


    @Override
    public final _OrderBySpec<I> windows(Consumer<Window.Builder<MySQLWindow._PartitionBySpec>> consumer) {
        consumer.accept(this::createDynamicWindow);
        if (this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<I> ifWindows(Consumer<Window.Builder<MySQLWindow._PartitionBySpec>> consumer) {
        consumer.accept(this::createDynamicWindow);
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        return new StaticLockBlock<>(MySQLLockStrength.FOR_UPDATE, this);
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        return new StaticLockBlock<>(MySQLLockStrength.FOR_SHARE, this);
    }


    @Override
    public final _LockSpec<I> ifFor(Consumer<_DynamicLockStrengthClause> consumer) {
        final DynamicLockBlock block = new DynamicLockBlock(this);
        consumer.accept(block);
        if (block.lockStrength != null) {
            this.onLockClauseEnd(block);
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> lockInShareMode() {
        return this.onLockClauseEnd(LockInShareMode.LOCK_IN_SHARE_MODE);
    }

    @Override
    public final _IntoOptionSpec<I> ifLockInShareMode(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.onLockClauseEnd(LockInShareMode.LOCK_IN_SHARE_MODE);
        } else if (this.lockBlock != null) {
            throw ContextStack.castCriteriaApi(this.context);
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
    public final _LockBlock lockBlock() {
        return this.lockBlock;
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
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final void onEndQuery() {
        this.windowList = _Collections.safeUnmodifiableList(this.windowList);
        if (this.intoVarList == null) {
            this.intoVarList = Collections.emptyList();
        }
    }


    @Override
    final void onClear() {
        this.windowList = null;
        this.lockBlock = null;
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
    final MySQLs.Modifier allModifier() {
        return MySQLs.ALL;
    }

    @Override
    final MySQLs.Modifier distinctModifier() {
        return MySQLs.DISTINCT;
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
            final TabularBlocks.FromClauseAliasDerivedBlock block;
            block = TabularBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        };
    }


    @Override
    final _JoinSpec<I> onFromCte(_JoinType joinType, @Nullable DerivedModifier modifier, _Cte cteItem,
                                 String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
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
            final TabularBlocks.JoinClauseAliasDerivedBlock<_JoinSpec<I>> block;
            block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _OnClause<_JoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable DerivedModifier modifier, _Cte cteItem,
                                            String alias) {
        final TabularBlocks.JoinClauseCteBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #from(Function)
     * @see #crossJoin(Function)
     */
    private _JoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final _TabularBlock block;
        block = TabularBlocks.fromNestedBlock(joinType, nestedItems);
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
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {

        final TabularBlocks.JoinClauseNestedBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
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
        final _TabularBlock block = this.fromCrossBlock;
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
    private TabularBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }

    /**
     * @see #windows(Consumer)
     * @see #window(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final ArmyWindow window) {
        window.endWindowClause();
        List<_Window> windowList = this.windowList;
        if (windowList == null) {
            windowList = _Collections.arrayList();
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
    private Window._WindowAsClause<MySQLWindow._PartitionBySpec, Item> createDynamicWindow(String name) {
        return new NamedWindowAsClause<>(this.context, name, this::onAddWindow, MySQLSupports::namedWindow);
    }


    private MySQLQueries<I> onLockClauseEnd(final _LockBlock block) {
        if (this.lockBlock != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        if (block instanceof LockClauseBlock<?, ?>) {
            ((LockClauseBlock<?, ?>) block).endLockClause();
        }
        this.lockBlock = block;
        return this;
    }

    private enum MySQLLockStrength implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE),
        FOR_SHARE(_Constant.SPACE_FOR_SHARE);

        private final String spaceWords;

        MySQLLockStrength(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//MySQLLockStrength


    private enum LockInShareMode implements _LockBlock, SQLWords {

        LOCK_IN_SHARE_MODE(_Constant.SPACE_LOCK_IN_SHARE_MODE);

        private final String spaceWords;

        LockInShareMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final SQLWords lockStrength() {
            return this;
        }

        @Override
        public final List<String> lockTableAliasList() {
            return Collections.emptyList();
        }

        @Override
        public final SQLWords lockWaitOption() {
            //always null
            return null;
        }


        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }//LockInShareMode

    private static final class StaticLockBlock<I extends Item> extends LockClauseBlock<
            MySQLQuery._LockWaitOptionSpec<I>,
            MySQLQuery._IntoOptionSpec<I>>
            implements MySQLQuery._LockOfTableSpec<I> {

        private final MySQLLockStrength lockStrength;

        private final MySQLQueries<I> stmt;

        private StaticLockBlock(MySQLLockStrength lockStrength, MySQLQueries<I> stmt) {
            this.lockStrength = lockStrength;
            this.stmt = stmt;
        }

        @Override
        public _AsQueryClause<I> into(String firstVarName, String... rest) {
            return this.stmt.onLockClauseEnd(this)
                    .into(firstVarName, rest);
        }

        @Override
        public _AsQueryClause<I> into(Consumer<Consumer<String>> consumer) {
            return this.stmt.onLockClauseEnd(this)
                    .into(consumer);
        }

        @Override
        public _AsQueryClause<I> ifInto(Consumer<Consumer<String>> consumer) {
            return this.stmt.onLockClauseEnd(this)
                    .ifInto(consumer);
        }

        @Override
        public I asQuery() {
            return this.stmt.onLockClauseEnd(this)
                    .asQuery();
        }

        @Override
        public CriteriaContext getContext() {
            return this.stmt.context;
        }

        @Override
        public SQLWords lockStrength() {
            return this.lockStrength;
        }


    }//StaticLockBlock


    private static final class DynamicLockBlock extends LockClauseBlock<
            Query._MinLockWaitOptionClause<Item>,
            Item> implements MySQLQuery._DynamicLockStrengthClause,
            MySQLQuery._DynamicLockOfTableSpec {

        private final MySQLQueries<?> stmt;

        private MySQLLockStrength lockStrength;

        private DynamicLockBlock(MySQLQueries<?> stmt) {
            this.stmt = stmt;
        }

        @Override
        public _DynamicLockOfTableSpec update() {
            if (this.lockStrength != null) {
                throw CriteriaUtils.duplicateDynamicMethod(this.stmt.context);
            }
            this.lockStrength = MySQLLockStrength.FOR_UPDATE;
            return this;
        }

        @Override
        public _DynamicLockOfTableSpec share() {
            if (this.lockStrength != null) {
                throw CriteriaUtils.duplicateDynamicMethod(this.stmt.context);
            }
            this.lockStrength = MySQLLockStrength.FOR_SHARE;
            return this;
        }

        @Override
        public CriteriaContext getContext() {
            return this.stmt.context;
        }

        @Override
        public SQLWords lockStrength() {
            final MySQLLockStrength strength = this.lockStrength;
            if (strength == null) {
                throw ContextStack.castCriteriaApi(this.stmt.context);
            }
            return strength;
        }


    }//DynamicLockBlock


    private static final class SimpleSelect<I extends Item> extends MySQLQueries<I> implements ArmySelect {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerBracketContext,
                             Function<? super Select, I> function) {
            super(spec, CriteriaContexts.primaryQueryContext(MySQLUtils.DIALECT, spec, outerBracketContext, null));
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
        _QueryWithComplexSpec<I> createQueryUnion(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));
            return new SelectDispatcher<>(this.context, unionFunc);
        }

        private MySQLBatchSimpleSelect wrapToBatchSelect(List<?> paramList) {
            return new MySQLBatchSimpleSelect(this, CriteriaUtils.paramList(paramList));
        }


    }//SimpleSelect

    private static final class SimpleSubQuery<I extends Item> extends MySQLQueries<I> implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                               Function<? super SubQuery, I> function) {
            super(spec, CriteriaContexts.subQueryContext(MySQLUtils.DIALECT, spec, outerContext, null));
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
        _QueryWithComplexSpec<I> createQueryUnion(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubQuery(this, unionType, rowSet));
            return new SubQueryDispatcher<>(this.context, unionFunc);
        }


    }//SimpleSubQuery


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
            return this.onColumnAliasList(ArrayUtils.unmodifiableListOf(first, rest));
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
            MySQLQuery._UnionOrderBySpec<I>,
            MySQLQuery._UnionOrderByCommaSpec<I>,
            MySQLQuery._UnionLimitSpec<I>,
            Query._AsQueryClause<I>,
            Object,
            Object,
            _QueryWithComplexSpec<I>>
            implements MySQLQuery._UnionOrderBySpec<I>,
            MySQLQuery,
            MySQLQuery._UnionOrderByCommaSpec<I> {


        private MySQLBracketQuery(ArmyStmtSpec spec) {
            super(spec);

        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLBracketQuery

    private static final class BracketSelect<I extends Item> extends MySQLBracketQuery<I> implements ArmySelect {

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
        _QueryWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSelect(this, unionType, rowSet));
            return new SelectDispatcher<>(this.context, unionFunc);
        }

        BatchBracketSelect wrapToBatchSelect(List<?> paramList) {
            return new BatchBracketSelect(this, CriteriaUtils.paramList(paramList));
        }


    }//BracketSelect

    private static final class BatchBracketSelect extends BracketRowSet.ArmyBatchBracketSelect
            implements MySQLQuery {

        private BatchBracketSelect(BracketSelect<?> select, List<?> paramList) {
            super(select, paramList);
        }

        @Override
        Dialect statementDialect() {
            return MySQLUtils.DIALECT;
        }


    }//BatchBracketSelect


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
        _QueryWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
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
            super(MySQLUtils.DIALECT, leftContext.getOuterContext(), leftContext);
            this.function = function;
        }

        private MySQLQueryDispatcher(MySQLBracketQuery<?> bracket, Function<RowSet, I> function) {
            super(MySQLUtils.DIALECT, bracket.context, null);
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
            return MySQLSupports.mysqlLCteBuilder(recursive, context);
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
        public MySQLValues._OrderBySpec<I> values(Consumer<ValuesRowConstructor> consumer) {
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
        public MySQLValues._OrderBySpec<I> values(Consumer<ValuesRowConstructor> consumer) {
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


    private static final class MySQLBatchSimpleSelect extends ArmyBatchSimpleSelect
            implements MySQLQuery, _MySQLQuery {

        private final boolean groupByWithRollup;

        private final List<_Window> windowList;

        private final boolean orderByWithRollup;

        private final _LockBlock lockBlock;

        private final List<String> intoVarList;

        private MySQLBatchSimpleSelect(SimpleSelect<?> select, List<?> paramList) {
            super(select, paramList);

            this.groupByWithRollup = select.groupByWithRollUp();
            this.windowList = select.windowList();
            this.orderByWithRollup = select.orderByWithRollup();
            this.lockBlock = select.lockBlock();

            this.intoVarList = select.intoVarList();
        }


        @Override
        public List<_Window> windowList() {
            return this.windowList;
        }

        @Override
        public boolean groupByWithRollUp() {
            return this.groupByWithRollup;
        }

        @Override
        public boolean orderByWithRollup() {
            return this.orderByWithRollup;
        }

        @Override
        public _LockBlock lockBlock() {
            return this.lockBlock;
        }

        @Override
        public List<String> intoVarList() {
            return this.intoVarList;
        }


        @Override
        Dialect statementDialect() {
            return MySQLUtils.DIALECT;
        }


    }//MySQLBatchSelect


}
