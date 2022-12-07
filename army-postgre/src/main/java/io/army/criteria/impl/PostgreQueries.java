package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreQuery;
import io.army.criteria.postgre.*;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Exceptions;

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
        Statement._AsClause<PostgreQuery._ParensJoinSpec<I>>,
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


    static <I extends Item> PostgreQuery._WithSpec<I> primaryQuery(
            @Nullable _WithClauseSpec withSpec, @Nullable CriteriaContext outerContext, Function<Select, I> function,
            @Nullable CriteriaContext leftContext) {
        return new SimpleSelect<>(withSpec, outerContext, function, leftContext);
    }


    static <I extends Item> PostgreQuery._WithSpec<I> subQuery(
            @Nullable _WithClauseSpec withSpec, CriteriaContext outerContext, Function<SubQuery, I> function,
            @Nullable CriteriaContext leftContext) {
        return new SimpleSubQuery<>(withSpec, outerContext, function, leftContext);
    }


    static <I extends Item> Function<String, _StaticCteParensSpec<I>> complexCte(final CriteriaContext context,
                                                                                 final I comma) {
        return name -> new CteMultiCommandFunction<>(context, comma, name);
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
    public final _RepeatableJoinClause<I> ifTableSample(
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
        this.getLastDerived().setColumnAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
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
            this.ofTaleList = _ArrayUtils.unmodifiableListOf(firstTableAlias, restTableAlias);
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
        if (this instanceof StaticCteComplexCommand) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
    }

    @Override
    final Query.TableModifier tableModifier(@Nullable Query.TableModifier modifier) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.errorModifier(this.context, modifier);
        }
        return modifier;
    }

    @Override
    final Query.DerivedModifier derivedModifier(@Nullable Query.DerivedModifier modifier) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.errorModifier(this.context, modifier);
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
    final _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable DerivedModifier modifier,
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


    }//TableOnBlock


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


    private static abstract class PostgreSimpleQuery<I extends Item>
            extends PostgreQueries<I, PostgreQuery._SelectSpec<I>>
            implements PostgreQuery._WithSpec<I> {

        private PostgreSimpleQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
        }


        @Override
        public final _StaticCteParensSpec<_StaticCteComma<I>> with(String name) {
            return new CteComma<>(false, this).function.apply(name);
        }

        @Override
        public final _StaticCteParensSpec<_StaticCteComma<I>> withRecursive(String name) {
            return new CteComma<>(true, this).function.apply(name);
        }

    }//PostgreSimpleQuery


    private static final class SimpleSelect<I extends Item> extends PostgreSimpleQuery<I>
            implements Select {

        private final Function<? super Select, I> function;

        private SimpleSelect(@Nullable _WithClauseSpec withSpec, @Nullable CriteriaContext outerContext,
                             Function<? super Select, I> function, @Nullable CriteriaContext leftContext) {
            super(withSpec, CriteriaContexts.primaryQuery(withSpec, outerContext, leftContext));
            this.function = function;
        }

        private SimpleSelect(BracketSelect<?> bracket, Function<? super Select, I> function) {
            super(null, CriteriaContexts.primaryQuery(null, bracket.context, null));
            this.function = function;
        }

        @Override
        public _WithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endQueryBeforeSelect();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);
            return new SimpleSelect<>(bracket, bracket::parenRowSetEnd);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new ComplexSelect<>(this.context, unionFunc);
        }

    }//SimpleSelect


    private static final class SimpleSubQuery<I extends Item> extends PostgreSimpleQuery<I>
            implements SubQuery {

        private final Function<? super SubQuery, I> function;


        private SimpleSubQuery(@Nullable _WithClauseSpec withSpec, CriteriaContext outerContext,
                               Function<? super SubQuery, I> function, @Nullable CriteriaContext leftContext) {
            super(withSpec, CriteriaContexts.subQueryContext(withSpec, outerContext, leftContext));
            this.function = function;
        }

        private SimpleSubQuery(BracketSubQuery<?> bracket, Function<? super SubQuery, I> function) {
            super(null, CriteriaContexts.subQueryContext(null, bracket.context, null));
            this.function = function;
        }


        @Override
        public _WithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endQueryBeforeSelect();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);
            return new SimpleSubQuery<>(bracket, bracket::parenRowSetEnd);
        }


        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new ComplexSubQuery<>(this.context, unionFunc);
        }


    }//SimpleSubQuery


    private static abstract class PostgreBracketQuery<I extends Item>
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

        private PostgreBracketQuery(PostgreQueries<I, ?> migration) {
            super(CriteriaContexts.bracketContext(migration.getWithClause(), migration.context.getOuterContext(),
                    migration.context.getLeftContext()));
        }


        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    }//PostgreBracketQueries


    private static final class BracketSelect<I extends Item> extends PostgreBracketQuery<I>
            implements Select {

        private final Function<? super Select, I> function;

        private BracketSelect(PostgreQueries<I, ?> migration, Function<? super Select, I> function) {
            super(migration);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new ComplexSelect<>(this.context, unionFunc);
        }


    }//BracketSelect

    private static final class BracketSubQuery<I extends Item> extends PostgreBracketQuery<I>
            implements SubQuery {

        private final Function<? super SubQuery, I> function;

        private BracketSubQuery(PostgreQueries<I, ?> migration, Function<? super SubQuery, I> function) {
            super(migration);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new ComplexSubQuery<>(this.context, unionFunc);
        }


    }//BracketSubQuery


    private static abstract class PostgreComplexQuery<I extends Item>
            extends PostgreQueries<I, PostgreQuery._QueryComplexSpec<I>>
            implements PostgreQuery._QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private PostgreComplexQuery(CriteriaContext context, Function<RowSet, I> function) {
            super(null, context);
            this.function = function;
        }

        @Override
        public final _StaticCteParensSpec<_ComplexCteComma<I>> with(String name) {
            return new ComplexCteComma<>(false, this).function.apply(name);
        }

        @Override
        public final _StaticCteParensSpec<_ComplexCteComma<I>> withRecursive(String name) {
            return new ComplexCteComma<>(false, this).function.apply(name);
        }


    }//PostgreComplexQuery


    private static final class ComplexSelect<I extends Item> extends PostgreComplexQuery<I> {

        private ComplexSelect(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(CriteriaContexts.primaryQuery(null, leftContext.getOuterContext(), leftContext), function);
        }

        private ComplexSelect(BracketSelect<?> bracket, Function<RowSet, I> function) {
            super(CriteriaContexts.primaryQuery(null, bracket.context, null), function);
        }


        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endQueryBeforeSelect();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);
            return new ComplexSelect<>(bracket, bracket::parenRowSetEnd);
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return PostgreSimpleValues.primaryValues(this.getWithClause(), this.context.getOuterContext(),
                            this::valuesEnd, this.context.getLeftContext()
                    )
                    .values(consumer);
        }

        @Override
        public PostgreValues._PostgreValuesLeftParenClause<I> values() {
            return PostgreSimpleValues.primaryValues(this.getWithClause(), this.context.getOuterContext(),
                            this::valuesEnd, this.context.getLeftContext()
                    )
                    .values();
        }

        @Override
        public <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> leftParen(Supplier<S> supplier) {
            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);
            final RowSet rowSet;
            rowSet = ContextStack.unionQuerySupplier(supplier);
            if (!(rowSet instanceof Select || rowSet instanceof Values)) {
                throw CriteriaUtils.unknownRowSet(this.context, rowSet, Database.PostgreSQL);
            } else if (!(rowSet instanceof PostgreQuery
                    || rowSet instanceof StandardQuery
                    || rowSet instanceof UnionSelect
                    || rowSet instanceof PostgreValues
                    || rowSet instanceof SimpleValues.UnionValues)) {
                throw CriteriaUtils.unknownRowSet(this.context, rowSet, Database.PostgreSQL);
            }
            bracket.parenRowSetEnd(rowSet);
            return bracket;
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new ComplexSelect<>(this.context, unionFunc);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
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

        private ComplexSubQuery(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(CriteriaContexts.subQueryContext(null, leftContext.getNonNullOuterContext(), leftContext), function);
        }

        private ComplexSubQuery(BracketSubQuery<?> bracket, Function<RowSet, I> function) {
            super(CriteriaContexts.subQueryContext(null, bracket.context, null), function);
        }


        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);
            return new ComplexSubQuery<>(bracket, bracket::parenRowSetEnd);
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return PostgreSimpleValues.subValues(this.getWithClause(), this.context.getNonNullOuterContext(),
                            this::valuesEnd, this.context.getLeftContext()
                    )
                    .values(consumer);
        }

        @Override
        public PostgreValues._PostgreValuesLeftParenClause<I> values() {
            return PostgreSimpleValues.subValues(this.getWithClause(), this.context.getNonNullOuterContext(),
                            this::valuesEnd, this.context.getLeftContext()
                    )
                    .values();
        }

        @Override
        public <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> leftParen(Supplier<S> supplier) {
            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);
            final RowSet rowSet;
            rowSet = ContextStack.unionQuerySupplier(supplier);
            if (!(rowSet instanceof SubQuery || rowSet instanceof SubValues)) {
                throw CriteriaUtils.unknownRowSet(this.context, rowSet, Database.PostgreSQL);
            } else if (!(rowSet instanceof PostgreQuery
                    || rowSet instanceof StandardQuery
                    || rowSet instanceof UnionSubQuery
                    || rowSet instanceof PostgreValues
                    || rowSet instanceof SimpleValues.UnionSubValues)) {
                throw CriteriaUtils.unknownRowSet(this.context, rowSet, Database.PostgreSQL);
            }
            bracket.parenRowSetEnd(rowSet);
            return bracket;
        }

        @Override
        _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new ComplexSubQuery<>(this.context, unionFunc);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        private I valuesEnd(SubValues values) {
            return this.function.apply(values);
        }

    }//UnionAndSubQueryClause


    private static final class SimpleCteComma<I extends Item> implements _StaticCteComma<I> {

        private final CriteriaContext context;

        private final boolean recursive;

        private final Function<Boolean, I> function;

        private SimpleCteComma(CriteriaContext context, boolean recursive, Function<Boolean, I> function) {
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
            return new CteMultiCommandFunction<>(name, this);
        }

        @Override
        public I space() {
            return this.function.apply(this.recursive);
        }

    }//SimpleCteComma


    private static final class ComplexCteComma<I extends Item> implements PostgreQuery._ComplexCteComma<I> {

        private final boolean recursive;

        private final PostgreComplexQuery<I> clause;

        private final Function<String, _StaticCteParensSpec<_ComplexCteComma<I>>> function;

        /**
         * @see PostgreComplexQuery#with(String)
         * @see PostgreComplexQuery#withRecursive(String)
         */
        private ComplexCteComma(boolean recursive, PostgreComplexQuery<I> clause) {
            clause.context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteParensSpec<_ComplexCteComma<I>> comma(String name) {
            return this.function.apply(name);
        }


        @Override
        public _QueryComplexSpec<I> space() {
            return this.clause.endStaticWithClause(this.recursive);
        }

    }//ComplexCteComma


    private static final class CteMultiCommandFunction<I extends Item>
            implements _StaticCteParensSpec<I> {

        private final SimpleCteComma<I> comma;

        private final String name;

        private List<String> columnAliasList;

        private PostgreSyntax.WordMaterialized modifier;

        /**
         * @see #complexCte(CriteriaContext, Item)
         */
        private CteMultiCommandFunction(String name, SimpleCteComma<I> comma) {
            this.name = name;
            this.comma = comma;
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
        public <R extends _StaticCteComma<I>> R as(Function<_StaticCteComplexCommandSpec<I>, R> function) {
            return this.as(null, function);
        }

        @Override
        public <R extends _StaticCteComma<I>> R as(@Nullable PostgreSyntax.WordMaterialized modifier,
                                                   Function<_StaticCteComplexCommandSpec<I>, R> function) {
            if (modifier != null && modifier != Postgres.MATERIALIZED && modifier != Postgres.NOT_MATERIALIZED) {
                throw CriteriaUtils.errorModifier(this.comma.context, modifier);
            }
            this.modifier = modifier;
            return function.apply(new StaticCteComplexCommand<>(this.comma.context, this::subStmtEnd, this::queryEnd));
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


        private _StaticCteComma<I> subStmtEnd(final SubStatement statement) {
            final _Cte cte;
            cte = new PostgreSupports.PostgreCte(this.name, this.columnAliasList, this.modifier, statement);
            this.comma.context.onAddCte(cte);
            return this.comma;
        }


        private _StaticCteSearchSpec<I> queryEnd(final SubQuery query) {

            final _StaticCteSearchSpec<I> spec;
            final boolean supportSearch;
            if (this.comma.recursive) {
                _RowSet rowSet = (_RowSet) query;
                while (rowSet instanceof _ParensRowSet) {
                    rowSet = ((_ParensRowSet) rowSet).innerRowSet();
                }
                supportSearch = rowSet instanceof UnionSubQuery;
            } else {
                supportSearch = false;
            }

            if (supportSearch) {
                spec = new StaticCteSearchSpec<>(this, query);
            } else {
                this.subStmtEnd(query);
                spec = PostgreSupports.noOperationStaticCteSearchSpec(this.comma::comma, this.comma::space);

            }
            return spec;
        }

        private _StaticCteComma<I> searchClauseEnd(final StaticCteSearchSpec<I> clause) {

            return this.comma;
        }


    }//CteMultiCommandFunction


    private static final class StaticCteSearchSpec<I extends Item> extends PostgreSupports.PostgreCteSearchSpec<
            _StaticCteCycleSpec<I>,
            _StaticCteComma<I>>
            implements PostgreQuery._StaticCteSearchSpec<I>,
            PostgreQuery._SetSearchSeqColumnClause<PostgreQuery._StaticCteCycleSpec<I>>,
            PostgreQuery._SetCycleMarkColumnClause<PostgreQuery._StaticCteComma<I>> {

        private final CteMultiCommandFunction<I> clause;

        private final SubQuery query;

        private StaticCteSearchSpec(CteMultiCommandFunction<I> clause, SubQuery query) {
            super(clause.comma.context);
            this.clause = clause;
            this.query = query;
        }

        @Override
        public StaticCteSearchSpec<I> set(String columnName) {
            this.doSet(columnName);
            return this;
        }

        @Override
        public StaticCteSearchSpec<I> set(Supplier<String> supplier) {
            this.doSet(supplier);
            return this;
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<I> comma(String name) {
            return this.clause.searchClauseEnd(this).comma(name);
        }

        @Override
        public I space() {
            return this.clause.searchClauseEnd(this).space();
        }


    }//StaticCteSearchSpec


    private static class StaticCteSubQuery<I extends Item> extends PostgreQueries<I, Object>
            implements ArmySubQuery, _StaticCteSelectSpec<I> {

        private final Function<SubQuery, I> queryFunction;

        private StaticCteSubQuery(CriteriaContext outerContext, Function<SubQuery, I> queryFunction) {
            super(null, CriteriaContexts.subQueryContext(null, outerContext, null));
            this.queryFunction = queryFunction;
        }

        @Override
        public final _StaticCteSelectSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endQueryBeforeSelect();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.queryFunction);
            return new StaticCteSubQuery<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        final _QueryWithComplexSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = right -> this.queryFunction.apply(new UnionSubQuery(this, unionType, right));
            return new ComplexSubQuery<>(this.context, unionFunc);
        }

        @Override
        final I onAsQuery() {
            return this.queryFunction.apply(this);
        }

    }//StaticCteSubQuery


    private static final class StaticCteComplexCommand<I extends Item>
            extends StaticCteSubQuery<_StaticCteSearchSpec<I>>
            implements PostgreQuery._StaticCteComplexCommandSpec<I> {


        private final Function<SubStatement, _StaticCteComma<I>> function;

        private StaticCteComplexCommand(CriteriaContext outerContext, Function<SubStatement, _StaticCteComma<I>> function,
                                        Function<SubQuery, _StaticCteSearchSpec<I>> queryFunction) {
            super(outerContext, queryFunction);
            this.function = function;
        }


        @Override
        public PostgreValues._OrderBySpec<_StaticCteComma<I>> values(Consumer<RowConstructor> consumer) {
            return PostgreSimpleValues.subValues(null, this.context.getNonNullOuterContext(), this::valuesEnd, null)
                    .values(consumer);
        }

        @Override
        public PostgreValues._PostgreValuesLeftParenClause<_StaticCteComma<I>> values() {
            return PostgreSimpleValues.subValues(null, this.context.getNonNullOuterContext(), this::valuesEnd, null)
                    .values();
        }

        @Override
        public PostgreInsert._ComplexInsertIntoClause<_StaticCteComma<I>, _StaticCteComma<I>> literalMode(LiteralMode mode) {
            this.endQueryBeforeSelect();
            return PostgreInserts.staticSubInsert(this.context.getNonNullLeftContext(), this.function)
                    .literalMode(mode);
        }

        @Override
        public PostgreInsert._StaticSubNullOptionSpec<_StaticCteComma<I>> migration(boolean migration) {
            this.endQueryBeforeSelect();
            return PostgreInserts.staticSubInsert(this.context.getNonNullLeftContext(), this.function)
                    .migration(migration);
        }

        @Override
        public PostgreInsert._StaticSubPreferLiteralSpec<_StaticCteComma<I>> nullMode(NullMode mode) {
            this.endQueryBeforeSelect();
            return PostgreInserts.staticSubInsert(this.context.getNonNullLeftContext(), this.function)
                    .nullMode(mode);
        }

        @Override
        public <T> PostgreInsert._TableAliasSpec<T, _StaticCteComma<I>, _StaticCteComma<I>> insertInto(TableMeta<T> table) {
            this.endQueryBeforeSelect();
            return PostgreInserts.staticSubInsert(this.context.getNonNullLeftContext(), this.function)
                    .insertInto(table);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_StaticCteComma<I>, _StaticCteComma<I>, T> update(TableMeta<T> table, SQLs.WordAs as,
                                                                                                    String tableAlias) {
            this.endQueryBeforeSelect();
            return PostgreUpdates.staticCteUpdate(this.context.getNonNullOuterContext(), this.function)
                    .update(table, as, tableAlias);
        }

        @Override
        public <T> PostgreUpdate._SingleSetClause<_StaticCteComma<I>, _StaticCteComma<I>, T> update(SQLs.WordOnly only, TableMeta<T> table,
                                                                                                    SQLs.WordAs as, String tableAlias) {
            this.endQueryBeforeSelect();
            return PostgreUpdates.staticCteUpdate(this.context.getNonNullOuterContext(), this.function)
                    .update(only, table, as, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_StaticCteComma<I>, _StaticCteComma<I>> delete(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            this.endQueryBeforeSelect();
            return PostgreDeletes.staticCteDelete(this.context.getNonNullOuterContext(), this.function)
                    .delete(table, as, tableAlias);
        }

        @Override
        public PostgreDelete._SingleUsingSpec<_StaticCteComma<I>, _StaticCteComma<I>> delete(SQLs.WordOnly only, TableMeta<?> table,
                                                                                             SQLs.WordAs as, String tableAlias) {
            this.endQueryBeforeSelect();
            return PostgreDeletes.staticCteDelete(this.context.getNonNullOuterContext(), this.function)
                    .delete(only, table, as, tableAlias);
        }

        private _StaticCteComma<I> valuesEnd(final SubValues values) {
            return this.function.apply(values);
        }


    }//StaticCteComplexCommand


}
