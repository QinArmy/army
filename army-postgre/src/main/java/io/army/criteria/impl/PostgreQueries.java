package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._UnionRowSet;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.impl.inner.postgre._PostgreQuery;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

abstract class PostgreQueries<I extends Item, WE> extends SimpleQueries.WithCteSimpleQueries<
        I,
        PostgreCtes,
        WE,
        Postgres.Modifier,
        PostgreQuery._PostgreSelectCommaSpec<I>,
        PostgreQuery._FromSpec<I>,
        PostgreQuery._TableSampleJoinSpec<I>,
        PostgreQuery._ParensJoinSpec<I>,
        PostgreQuery._JoinSpec<I>,
        PostgreQuery._TableSampleOnSpec<I>,
        Statement._AsParensOnClause<PostgreQuery._JoinSpec<I>>,
        Statement._OnClause<PostgreQuery._JoinSpec<I>>,
        PostgreQuery._GroupBySpec<I>,
        PostgreQuery._WhereAndSpec<I>,
        PostgreQuery._HavingSpec<I>,
        PostgreQuery._WindowSpec<I>,
        PostgreQuery._LimitSpec<I>,
        PostgreQuery._OffsetSpec<I>,
        PostgreQuery._FetchSpec<I>,
        PostgreQuery._LockSpec<I>,
        PostgreQuery._QueryWithComplexSpec<I>>
        implements PostgreQuery,
        _PostgreQuery,
        PostgreQuery._PostgreSelectClause<I>,
        PostgreQuery._PostgreSelectCommaSpec<I>,
        PostgreQuery._TableSampleJoinSpec<I>,
        PostgreQuery._RepeatableJoinClause<I>,
        PostgreQuery._ParensJoinSpec<I>,
        PostgreQuery._WindowCommaSpec<I>,
        PostgreQuery._HavingSpec<I>,
        PostgreQuery._FetchSpec<I>,
        PostgreQuery._LockOfTableSpec<I> {


    static <I extends Item> PostgreQueries<I> primaryQuery(@Nullable _WithClauseSpec withSpec
            , @Nullable CriteriaContext outerContext, Function<Select, I> function) {
        return new SimpleSelect<>(withSpec, outerContext, function);
    }


    static <I extends Item> PostgreQueries<I> subQuery(@Nullable _WithClauseSpec withSpec
            , CriteriaContext outerContext, Function<SubQuery, I> function) {
        return new SimpleSubQuery<>(withSpec, outerContext, function);
    }


    static <I extends Item> Function<String, _StaticCteParensSpec<I>> complexCte(CriteriaContext context, I comma) {
        return new StaticComplexCommand<>(context, comma)::nextCte;
    }

    static <I extends Item> PostgreQuery._DynamicSubMaterializedSpec<I> dynamicCteQuery(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        return new DynamicCteSimpleSubQuery<>(outerContext, function);
    }


    private List<_Window> windowList;

    private PostgreLockMode lockMode;

    private List<String> ofTaleList;

    private LockWaitOption lockWaitOption;

    private _TableBlock fromCrossBlock;


    private PostgreQueries(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }


    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> from() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(final @Nullable Expression method) {
        if (method == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.getLastTableBlock().setSampleMethod((ArmyExpression) method);
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(String methodName, Expression argument) {
        return this.tableSample(FunctionUtils.oneArgVoidFunc(methodName, argument));
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
        final List<Expression> list = new ArrayList<>();
        consumer.accept(list::add);
        return this.tableSample(FunctionUtils.multiArgVoidFunc(methodName, list));
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
            BiFunction<MappingType, Object, Expression> valueOperator, Object argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
            BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final <E> _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
            BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.tableSample(method.apply(valueOperator, supplier.get()));
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
            BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
    }

    @Override
    public final _RepeatableJoinClause<I> ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
        final List<Expression> list = new ArrayList<>();
        consumer.accept(list::add);
        if (list.size() > 0) {
            this.tableSample(FunctionUtils.multiArgVoidFunc(methodName, list));
        }
        return this;
    }

    @Override
    public final <E> _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
            BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
            BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(final @Nullable Expression seed) {
        if (seed == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.getLastTableBlock().setSeed((ArmyExpression) seed);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(Supplier<Expression> supplier) {
        return this.repeatable(supplier.get());
    }

    @Override
    public final _JoinSpec<I> repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
        return this.repeatable(valueOperator.apply(seedValue));
    }

    @Override
    public final <E extends Number> _JoinSpec<I> repeatable(Function<E, Expression> valueOperator,
                                                            Supplier<E> supplier) {
        return this.repeatable(valueOperator.apply(supplier.get()));
    }

    @Override
    public final _JoinSpec<I> repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                         String keyName) {
        return this.repeatable(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.repeatable(expression);
        }
        return this;
    }

    @Override
    public final <E extends Number> _JoinSpec<I> ifRepeatable(Function<E, Expression> valueOperator,
                                                              Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                           String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(String first, String... rest) {
        this.getLastDerived().setColumnAliasList(ArrayUtils.unmodifiableListOf(first, rest));
        return this;
    }

    @Override
    public final _JoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getLastDerived().setColumnAliasList(CriteriaUtils.stringList(this.context, true, consumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
        this.getLastDerived().setColumnAliasList(CriteriaUtils.stringList(this.context, false, consumer));
        return this;
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> leftJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> join() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> rightJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> fullJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> crossJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
    }

    @Override
    public final _JoinSpec<I> ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }


    @Override
    public final _OrderBySpec<I> windows(Consumer<PostgreWindows> consumer) {
        this.ifWindows(consumer);
        if (this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<I> ifWindows(Consumer<PostgreWindows> consumer) {
        final PostgreWindowBuilder builder;
        builder = new PostgreWindowBuilder(this);
        consumer.accept(builder);

        final PostgreSupports.PostgreWindow window;
        window = builder.lastWindow;
        if (window != null) {
            builder.lastWindow = null;
            this.onAddWindow(window);
        }
        return this;
    }


    @Override
    public final _WindowAsClause<I> window(String name) {
        return new StaticWindowAsClause<>(name, this);
    }

    @Override
    public final _WindowAsClause<I> comma(String name) {
        return new StaticWindowAsClause<>(name, this);
    }


    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        this.lockMode = PostgreLockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForUpdate(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = PostgreLockMode.FOR_UPDATE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        this.lockMode = PostgreLockMode.FOR_SHARE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForShare(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = PostgreLockMode.FOR_SHARE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forNoKeyUpdate() {
        this.lockMode = PostgreLockMode.FOR_NO_KEY_UPDATE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forKeyShare() {
        this.lockMode = PostgreLockMode.FOR_KEY_SHARE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForNoKeyUpdate(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = PostgreLockMode.FOR_NO_KEY_UPDATE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForKeyShare(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = PostgreLockMode.FOR_KEY_SHARE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(String tableAlias) {
        if (this.lockMode == null) {
            this.ofTaleList = Collections.emptyList();
        } else {
            this.ofTaleList = Collections.singletonList(tableAlias);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(String firstTableAlias, String... restTableAlias) {
        if (this.lockMode == null) {
            this.ofTaleList = Collections.emptyList();
        } else {
            this.ofTaleList = ArrayUtils.unmodifiableListOf(firstTableAlias, restTableAlias);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<String>> consumer) {
        if (this.lockMode == null) {
            this.ofTaleList = Collections.emptyList();
        } else {
            this.ofTaleList = CriteriaUtils.stringList(this.context, true, consumer);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<String>> consumer) {
        if (this.lockMode == null) {
            this.ofTaleList = Collections.emptyList();
        } else {
            this.ofTaleList = CriteriaUtils.stringList(this.context, false, consumer);
        }
        return this;
    }

    @Override
    public final _LockSpec<I> noWait() {
        this.lockWaitOption = this.lockMode == null ? null : LockWaitOption.NOWAIT;
        return this;
    }

    @Override
    public final _LockSpec<I> skipLocked() {
        this.lockWaitOption = this.lockMode == null ? null : LockWaitOption.SKIP_LOCKED;
        return this;
    }

    @Override
    public final _LockSpec<I> ifNoWait(BooleanSupplier predicate) {
        if (this.lockMode != null && predicate.getAsBoolean()) {
            this.lockWaitOption = LockWaitOption.NOWAIT;
        } else {
            this.lockWaitOption = null;
        }
        return this;
    }

    @Override
    public final _LockSpec<I> ifSkipLocked(BooleanSupplier predicate) {
        if (this.lockMode != null && predicate.getAsBoolean()) {
            this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
        } else {
            this.lockWaitOption = null;
        }
        return this;
    }

    @Override
    final void onEndQuery() {
        if (this.windowList == null) {
            this.windowList = Collections.emptyList();
        }
        if (this.ofTaleList == null) {
            this.ofTaleList = Collections.emptyList();
        }
    }


    @Override
    final void onClear() {
        this.windowList = null;
        this.ofTaleList = null;
    }

    @Override
    final Dialect statementDialect() {
        return PostgreDialect.POSTGRE15;
    }

    @Override
    final List<Postgres.Modifier> asModifierList(@Nullable List<Postgres.Modifier> modifiers) {
        return CriteriaUtils.asModifierList(this.context, modifiers, PostgreUtils::selectModifier);
    }

    @Override
    final boolean isErrorModifier(Postgres.Modifier modifier) {
        return PostgreUtils.selectModifier(modifier) < 0;
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //postgre don't support hint
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final PostgreCtes createCteBuilder(boolean recursive) {
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
    }

    @Override
    final Query.TableModifier tableModifier(@Nullable Query.TableModifier modifier) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        return modifier;
    }

    @Override
    final Query.DerivedModifier derivedModifier(@Nullable Query.DerivedModifier modifier) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        return modifier;
    }

    @Override
    final _TableSampleJoinSpec<I> onFromTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                              String alias) {
        final PostgreSupports.PostgreNoOnTableBlock block;
        block = new PostgreSupports.PostgreNoOnTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _ParensJoinSpec<I> onFromDerived(_JoinType joinType, @Nullable DerivedModifier modifier,
                                           DerivedTable table) {
        return alias -> {
            final TableBlock.NoOnModifierDerivedBlock block;
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
    final _TableSampleOnSpec<I> onJoinTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                            String alias) {
        final TableOnBlock<I> block;
        block = new TableOnBlock<>(joinType, modifier, table, alias, this);
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
        block = new OnClauseTableBlock<>(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    /*-------------------below private method -------------------*/

    /**
     * @see #from()
     * @see #crossJoin()
     */
    private _JoinSpec<I> fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
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
     */
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertStandardJoinType();

        final OnClauseTableBlock<_JoinSpec<I>> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    private PostgreSupports.PostgreNoOnTableBlock getLastTableBlock() {
        final _TableBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof PostgreSupports.PostgreNoOnTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (PostgreSupports.PostgreNoOnTableBlock) block;
    }

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
     * @see #comma(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final ArmyWindow window) {
        window.endWindowClause();
        List<_Window> list = this.windowList;
        if (list == null) {
            list = new ArrayList<>();
            this.windowList = list;
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        list.add(window);
        return this;
    }


    private static final class StaticWindowAsClause<I extends Item> implements PostgreQuery._WindowAsClause<I> {

        private final String windowName;

        private final PostgreQueries<I, ?> stmt;

        /**
         * @see #window(String)
         * @see #comma(String)
         */
        private StaticWindowAsClause(String windowName, PostgreQueries<I, ?> stmt) {
            this.windowName = windowName;
            this.stmt = stmt;
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
        public _WindowCommaSpec<I> as(Consumer<_WindowPartitionBySpec> consumer) {
            return this.as(null, consumer);
        }

        @Override
        public _WindowCommaSpec<I> as(@Nullable String existingWindowName, Consumer<_WindowPartitionBySpec> consumer) {
            final PostgreSupports.PostgreWindow window;
            window = PostgreSupports.namedWindow(this.windowName, this.stmt.context, existingWindowName);
            consumer.accept(window);
            return this.stmt.onAddWindow(window);
        }

    }//StaticWindowAsClause

    private static final class PostgreWindowBuilder implements PostgreWindows {

        private final PostgreQueries<?, ?> stmt;

        private PostgreSupports.PostgreWindow lastWindow;

        /**
         * @see #windows(Consumer)
         * @see #ifWindows(Consumer)
         */
        private PostgreWindowBuilder(PostgreQueries<?, ?> stmt) {
            this.stmt = stmt;
        }

        @Override
        public Window._WindowAsClause<_WindowPartitionBySpec> window(final String windowName) {
            final PostgreSupports.PostgreWindow lastWindow = this.lastWindow;
            if (lastWindow != null) {
                this.lastWindow = null;
                this.stmt.onAddWindow(lastWindow);
            }
            return existingWindowName -> {
                final PostgreSupports.PostgreWindow oldWindow, window;
                window = PostgreSupports.namedWindow(windowName, this.stmt.context, existingWindowName);
                oldWindow = this.lastWindow;
                if (oldWindow != null) {
                    throw CriteriaUtils.windowNotEnd(this.stmt.context, oldWindow, window);
                }
                this.lastWindow = window;
                return window;
            };
        }


    }//PostgreWindowBuilder

    private static final class TableOnBlock<I extends Item> extends PostgreSupports.PostgreTableOnBlock<
            PostgreQuery._RepeatableOnClause<I>,
            Statement._OnClause<PostgreQuery._JoinSpec<I>>,
            PostgreQuery._JoinSpec<I>>
            implements PostgreQuery._TableSampleOnSpec<I>, PostgreQuery._RepeatableOnClause<I> {

        private TableOnBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> tableItem, String alias,
                             PostgreQuery._JoinSpec<I> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//OnTableBlock


    private enum PostgreLockMode implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE),
        FOR_SHARE(_Constant.SPACE_FOR_SHARE),
        FOR_NO_KEY_UPDATE(" FOR NO KEY UPDATE"),
        FOR_KEY_SHARE(" FOR KEY SHARE");

        final String spaceWords;

        PostgreLockMode(String spaceWords) {
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

    }//PostgreLockMode


    private static final class SimpleSelect<I extends Item> extends PostgreQueries<I>
            implements Select {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable _WithClauseSpec withSpec, @Nullable CriteriaContext outerContext
                , Function<? super Select, I> function) {
            super(withSpec, CriteriaContexts.primaryQuery(withSpec, outerContext));
            this.function = function;
        }


        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(null, this.context, this::bracketEnd);
            return new SimpleSelect<>(null, bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            UnionType.exceptType(this.context, unionType);
            return new ComplexSelect<>(this.context.getOuterContext(), unionFunc);
        }

        private I bracketEnd(Select query) {
            ContextStack.pop(this.context)
                    .endContextBeforeSelect();
            return this.function.apply(query);
        }


    }//SimpleSelect


    private static abstract class PostgreSimpleSubQuery<I extends Item> extends PostgreQueries<I>
            implements SubQuery {


        private PostgreSimpleSubQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext outerContext) {
            super(withSpec, CriteriaContexts.subQueryContext(withSpec, outerContext));
        }

        @Override
        public final _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(null, this.context, this::bracketEnd);
            return new SimpleSubQuery<>(null, bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        final _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.unionRightEnd(unionType, right);
            return new ComplexSubQuery<>(this.context.getNonNullOuterContext(), unionFunc);
        }

        abstract I bracketEnd(SubQuery query);

        abstract I unionRightEnd(UnionType unionType, RowSet right);


    }//PostgreSimpleSubQuery


    private static class SimpleSubQuery<I extends Item> extends PostgreSimpleSubQuery<I> {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext outerContext
                , Function<? super SubQuery, I> function) {
            super(withSpec, outerContext);
            this.function = function;
        }

        @Override
        final I onAsQuery() {
            return this.function.apply(this);
        }


        @Override
        I unionRightEnd(UnionType unionType, RowSet right) {
            return this.function.apply(new UnionSubQuery(this, unionType, right));
        }

        I bracketEnd(SubQuery query) {
            ContextStack.pop(this.context)
                    .endContextBeforeSelect();
            return this.function.apply(query);
        }

    }//SimpleSubQuery


    private static final class DynamicCteSimpleSubQuery<I extends Item> extends PostgreSimpleSubQuery<I>
            implements _DynamicSubMaterializedSpec<I> {

        private final Function<SubStatement, I> function;

        private PostgreSupports.MaterializedOption materializedOption;

        private DynamicCteSimpleSubQuery(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, outerContext);
            this.function = function;
        }

        @Override
        public _MinWithSpec<I> materialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            return this;
        }

        @Override
        public _MinWithSpec<I> notMaterialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            return this;
        }

        @Override
        public _MinWithSpec<I> ifMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return this;
        }

        @Override
        public _MinWithSpec<I> ifNotMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return this;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this.createResultStmt(this));
        }

        @Override
        I bracketEnd(final SubQuery query) {
            ContextStack.pop(this.context)
                    .endContextBeforeSelect();
            return this.function.apply(this.createResultStmt(query));
        }

        @Override
        I unionRightEnd(UnionType unionType, RowSet right) {
            final SubQuery query;
            query = new UnionSubQuery(this, unionType, right);
            return this.function.apply(this.createResultStmt(query));
        }

        private SubStatement createResultStmt(final SubQuery query) {
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            return option == null ? query : new PostgreSupports.PostgreSubStatement(option, query);
        }

    }//DynamicCteSimpleSubQuery


    private static abstract class PostgreBracketQueries<I extends Item>
            extends BracketRowSet<
            I,
            PostgreQuery._UnionOrderBySpec<I>,
            PostgreQuery._UnionLimitSpec<I>,
            PostgreQuery._UnionOffsetSpec<I>,
            PostgreQuery._UnionFetchSpec<I>,
            Query._AsQueryClause<I>,
            PostgreQuery._QueryWithComplexSpec<I>>
            implements PostgreQuery._UnionOrderBySpec<I>
            , PostgreQuery._UnionOffsetSpec<I>
            , PostgreQuery._UnionFetchSpec<I> {

        private PostgreBracketQueries(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext) {
            super(CriteriaContexts.bracketContext(spec, outerContext));
        }


        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    }//PostgreBracketQueries


    private static final class BracketSelect<I extends Item> extends PostgreBracketQueries<I>
            implements Select {

        private final Function<? super Select, I> function;

        private BracketSelect(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext
                , Function<? super Select, I> function) {
            super(spec, outerContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new ComplexSelect<>(this.context.getOuterContext(), unionFunc);
        }


    }//BracketSelect

    private static final class BracketSubQuery<I extends Item> extends PostgreBracketQueries<I>
            implements SubQuery {

        private final Function<? super SubQuery, I> function;

        private BracketSubQuery(@Nullable _WithClauseSpec spec, CriteriaContext outerContext
                , Function<? super SubQuery, I> function) {
            super(spec, outerContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new ComplexSubQuery<>(this.context.getNonNullOuterContext(), unionFunc);
        }


    }//BracketSubQuery


    private static abstract class PostgreComplexQuery<I extends Item> extends WithBuilderSelectClauseDispatcher<
            PostgreCtes,
            PostgreQuery._QueryComplexSpec<I>,
            Postgres.Modifier,
            _PostgreSelectCommaSpec<I>,
            _FromSpec<I>>
            implements PostgreQuery._QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private PostgreComplexQuery(@Nullable CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext);
            this.function = function;
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive, CriteriaContext withClauseContext) {
            return PostgreSupports.postgreCteBuilder(recursive, withClauseContext);
        }


    }//PostgreComplexQuery


    private static final class ComplexSelect<I extends Item> extends PostgreComplexQuery<I> {

        public ComplexSelect(@Nullable CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext, function);
        }


        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.getWithClause(), this.outerContext, this.function);
            return new ComplexSelect<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return PostgreSimpleValues.primaryValues(this.getWithClause(), this.outerContext, this::valuesEnd)
                    .values(consumer);
        }

        @Override
        public PostgreValues._PostgreValuesLeftParenClause<I> values() {
            return PostgreSimpleValues.primaryValues(this.getWithClause(), this.outerContext, this::valuesEnd)
                    .values();
        }

        @Override
        PostgreQueries<I> onSelectClause(@Nullable _WithClauseSpec spec) {
            return new SimpleSelect<>(spec, this.outerContext, this.function);
        }

        private I valuesEnd(Values values) {
            return this.function.apply(values);
        }

    }//ComplexSelect


    /**
     * @see #createQueryUnion(UnionType)
     * @see #createQueryUnion(UnionType)
     */
    private static final class ComplexSubQuery<I extends Item> extends PostgreComplexQuery<I> {

        public ComplexSubQuery(CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext, function);
        }


        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.getWithClause(), outerContext, this.function);
            return new ComplexSubQuery<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return PostgreSimpleValues.subValues(this.getWithClause(), outerContext, this::valuesEnd)
                    .values(consumer);
        }

        @Override
        public PostgreValues._PostgreValuesLeftParenClause<I> values() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return PostgreSimpleValues.subValues(this.getWithClause(), outerContext, this::valuesEnd)
                    .values();
        }

        @Override
        PostgreQueries<I> onSelectClause(@Nullable _WithClauseSpec spec) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return new SimpleSubQuery<>(spec, outerContext, this.function);
        }

        private I valuesEnd(SubValues values) {
            return this.function.apply(values);
        }

    }//UnionAndSubQueryClause


    private static final class CteComma<I extends Item>
            extends SelectClauseDispatcher<Postgres.Modifier, _PostgreSelectCommaSpec<I>, _FromSpec<I>>
            implements PostgreQuery._CteComma<I> {

        private final boolean recursive;

        private final PostgreQueries<I> clause;

        private final Function<String, _StaticCteParensSpec<_CteComma<I>>> function;


        private CteComma(boolean recursive, PostgreQueries<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteParensSpec<_CteComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            this.clause.endStaticWithClause(this.recursive);
            return this.clause;
        }


    }//CteComma


    private static final class StaticComplexCommand<I extends Item>
            extends SimpleQueries.ComplexSelectCommand<
            Postgres.Modifier,
            _PostgreSelectCommaSpec<_CteSearchClause<I>>,
            _FromSpec<_CteSearchClause<I>>,
            _StaticCteAsClause<I>>
            implements _StaticCteMaterializedSpec<I>
            , _StaticCteParensSpec<I>
            , _AsCteClause<I> {

        private final boolean recursive;

        private final I item;

        private String currentCteName;

        private List<String> columnAliasList;

        private PostgreSupports.MaterializedOption materializedOption;


        private StaticComplexCommand(CriteriaContext context, I item) {
            super(context);
            this.recursive = context.isWithRecursive();
            this.item = item;
        }


        @Override
        public _StaticCteMaterializedSpec<I> as() {
            return this;
        }


        @Override
        public _StaticCteComplexCommandSpec<I> materialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            return this;
        }

        @Override
        public _StaticCteComplexCommandSpec<I> notMaterialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            return this;
        }

        @Override
        public _StaticCteComplexCommandSpec<I> ifMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return this;
        }

        @Override
        public _StaticCteComplexCommandSpec<I> ifNotMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return this;
        }

        @Override
        public PostgreInsert._ComplexInsertIntoClause<_AsCteClause<I>, _AsCteClause<I>> literalMode(LiteralMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this::subStatementEnd)
                    .literalMode(mode);
        }

        @Override
        public PostgreInsert._StaticSubNullOptionSpec<_AsCteClause<I>> migration(boolean migration) {
            return PostgreInserts.staticSubInsert(this.context, this::subStatementEnd)
                    .migration(migration);
        }

        @Override
        public PostgreInsert._StaticSubPreferLiteralSpec<_AsCteClause<I>> nullMode(NullMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this::subStatementEnd)
                    .nullMode(mode);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, _AsCteClause<I>, _AsCteClause<I>> insertInto(TableMeta<T> table) {
            return PostgreInserts.staticSubInsert(this.context, this::subStatementEnd)
                    .insertInto(table);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_AsCteClause<I>, _AsCteClause<I>, T> update(TableMeta<T> table
                , SQLs.WordAs wordAs, String tableAlias) {
            return PostgreUpdates.staticCteUpdate(this.context, this::subStatementEnd)
                    .update(table, wordAs, tableAlias);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_AsCteClause<I>, _AsCteClause<I>, T> update(SQLs.WordOnly wordOnly
                , TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            return PostgreUpdates.staticCteUpdate(this.context, this::subStatementEnd)
                    .update(wordOnly, table, wordAs, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_AsCteClause<I>, _AsCteClause<I>> delete(TableMeta<?> table
                , SQLs.WordAs wordAs, String tableAlias) {
            return PostgreDeletes.staticCteDelete(this.context, this::subStatementEnd)
                    .delete(table, wordAs, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_AsCteClause<I>, _AsCteClause<I>> delete(SQLs.WordOnly wordOnly
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return PostgreDeletes.staticCteDelete(this.context, this::subStatementEnd)
                    .delete(wordOnly, table, wordAs, tableAlias);
        }

        @Override
        public PostgreValues._OrderBySpec<_AsCteClause<I>> values(Consumer<RowConstructor> consumer) {
            return PostgreSimpleValues.subValues(null, this.context, this::subStatementEnd)
                    .values(consumer);
        }

        @Override
        public PostgreValues._PostgreValuesLeftParenClause<_AsCteClause<I>> values() {
            return PostgreSimpleValues.subValues(null, this.context, this::subStatementEnd)
                    .values();
        }

        @Override
        public _StaticCteSelectSpec<_RightParenClause<_UnionOrderBySpec<_CteSearchClause<I>>>> leftParen() {
            final BracketSubQuery<_CteSearchClause<I>> bracket;
            bracket = new BracketSubQuery<>(null, this.context, this::queryEnd);
            return new StaticCteSelectSpec<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public I asCte() {
            return this.item;
        }

        @Override
        PostgreQueries<_CteSearchClause<I>> createSelectClause() {
            return new SimpleSubQuery<>(null, this.context, this::queryEnd);
        }


        private _StaticCteParensSpec<I> nextCte(final @Nullable String name) {
            if (this.currentCteName != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (name == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (!_StringUtils.hasText(name)) {
                throw ContextStack.criteriaError(this.context, _Exceptions::cteNameNotText);
            }
            this.context.onStartCte(name);
            this.currentCteName = name;
            this.columnAliasList = null;
            this.materializedOption = null;
            return this;
        }


        private _CteSearchClause<I> queryEnd(final SubQuery query) {
            final _CteSearchClause<I> clause;
            if (this.recursive && query instanceof _UnionRowSet) {
                clause = new PostgreSupports.PostgreCteSearchClause<>(this.context, query, this::cteSubStatement);
            } else {
                clause = new PostgreSupports.NonOperationPostgreCteSearchClause<>(this.context, this.currentCteName
                        , query, this::cteSubStatement);
            }
            return clause;
        }

        private _AsCteClause<I> subStatementEnd(final SubStatement subStatement) {
            this.cteSubStatement(subStatement);
            return this;
        }

        /**
         * @see #queryEnd(SubQuery)
         */
        private I cteSubStatement(final SubStatement subStatement) {
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            final SubStatement cteStatement;
            if (option == null) {
                cteStatement = subStatement;
            } else {
                cteStatement = new PostgreSupports.PostgreSubStatement(option, subStatement);
            }
            CriteriaUtils.createAndAddCte(this.context, this.currentCteName, this.columnAliasList, cteStatement);
            //clear for next cte
            this.currentCteName = null;
            this.columnAliasList = null;
            this.materializedOption = null;
            return this.item;
        }

        @Override
        _StaticCteAsClause<I> columnAliasClauseEnd(List<String> list) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = list;
            return this;
        }

    }//PostgreCteComplexCommand


    private static final class StaticCteSelectSpec<I extends Item> extends SimpleQueries.SelectClauseDispatcher<
            Postgres.Modifier,
            _PostgreSelectCommaSpec<I>,
            _FromSpec<I>> implements _StaticCteSelectSpec<I> {

        private final CriteriaContext context;

        private final Function<SubQuery, I> function;

        /**
         * @see StaticComplexCommand#leftParen()
         * @see #leftParen()
         */
        private StaticCteSelectSpec(CriteriaContext context, Function<SubQuery, I> function) {
            this.context = context;
            this.function = function;
        }

        @Override
        public _StaticCteSelectSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(null, this.context, this.function);
            return new StaticCteSelectSpec<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            return new SimpleSubQuery<>(null, this.context, this.function);
        }


    }//StaticCteSelectSpec


}
