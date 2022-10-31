package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
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
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

abstract class PostgreQueries<I extends Item> extends SimpleQueries.WithCteSimpleQueries<
        I,
        PostgreCtes,
        PostgreQuery._SelectSpec<I>,
        PostgreSyntax.Modifier,
        PostgreQuery._FromSpec<I>,
        PostgreQuery._TableSampleJoinSpec<I>,
        PostgreQuery._JoinSpec<I>,
        PostgreQuery._JoinSpec<I>,
        PostgreQuery._TableSampleOnSpec<I>,
        Statement._OnClause<PostgreQuery._JoinSpec<I>>,
        Statement._OnClause<PostgreQuery._JoinSpec<I>>,
        PostgreQuery._GroupBySpec<I>,
        PostgreQuery._WhereAndSpec<I>,
        PostgreQuery._HavingSpec<I>,
        PostgreQuery._WindowSpec<I>,
        PostgreQuery._LimitSpec<I>,
        PostgreQuery._OffsetSpec<I>,
        PostgreQuery._FetchSpec<I>,
        PostgreQuery._LockSpec<I>,
        PostgreQuery._MinWithSpec<I>>
        implements PostgreQuery, _PostgreQuery
        , PostgreQuery._WithSpec<I>
        , PostgreQuery._FromSpec<I>
        , PostgreQuery._TableSampleJoinSpec<I>
        , PostgreQuery._RepeatableJoinClause<I>
        , PostgreQuery._JoinSpec<I>
        , PostgreQuery._WindowCommaSpec<I>
        , PostgreQuery._HavingSpec<I>
        , PostgreQuery._FetchSpec<I>
        , PostgreQuery._LockOfTableSpec<I> {


    static <I extends Item> PostgreQuery._WithSpec<I> primaryQuery(@Nullable _WithClauseSpec withSpec
            , Function<Select, I> function) {
        return new SimpleSelect<>(withSpec, CriteriaContexts.primaryQuery(null), function);
    }


    static <I extends Item> _WithSpec<I> subQuery(CriteriaContext outerContext, Function<SubQuery, I> function) {
        return new SimpleSubQuery<>(outerContext, function);
    }


    static <I extends Item> Function<String, _StaticCteLeftParenSpec<I>> complexCte(CriteriaContext outerContext
            , I comma) {
        throw new UnsupportedOperationException();

    }

    static <I extends Item> PostgreQuery._DynamicSubMaterializedSpec<I> dynamicCteQuery(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }

    private PostgreSupports.PostgreNoOnTableBlock noOnBlock;

    private List<_Window> windowList;

    private PostgreLockMode lockMode;

    private List<TableMeta<?>> ofTaleList;

    private LockWaitOption lockWaitOption;


    private PostgreQueries(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }


    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> with(String name) {
        final boolean recursive = false;
        this.context.onBeforeWithClause(recursive);
        return new CteComma<>(recursive, this).function.apply(name);
    }

    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> withRecursive(String name) {
        final boolean recursive = true;
        this.context.onBeforeWithClause(recursive);
        return new CteComma<>(recursive, this).function.apply(name);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> from() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNonCrossEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> leftJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> join() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> rightJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> fullJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> crossJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNonCrossEnd);
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
    public final _RepeatableJoinClause<I> tableSample(Expression method) {
        this.getNoOnBlock().tableSample(method);
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(String methodName, Expression argument) {
        this.getNoOnBlock().tableSample(methodName, argument);
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
        this.getNoOnBlock().tableSample(methodName, consumer);
        return this;
    }

    @Override
    public final <T> _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, T argument) {
        this.getNoOnBlock().tableSample(method, valueOperator, argument);
        return this;
    }

    @Override
    public final <T> _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
        this.getNoOnBlock().tableSample(method, valueOperator, supplier);
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> tableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
            , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        this.getNoOnBlock().tableSample(method, valueOperator, function, keyName);
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
        this.getNoOnBlock().ifTableSample(methodName, consumer);
        return this;
    }

    @Override
    public final <T> _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, @Nullable T argument) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, argument);
        return this;
    }

    @Override
    public final <T> _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, supplier);
        return this;
    }

    @Override
    public final _RepeatableJoinClause<I> ifTableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
            , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, function, keyName);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(Expression seed) {
        this.getNoOnBlock().repeatable(seed);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(Supplier<Expression> supplier) {
        this.getNoOnBlock().repeatable(supplier);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue) {
        this.getNoOnBlock().repeatable(valueOperator, seedValue);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , Supplier<Number> supplier) {
        this.getNoOnBlock().repeatable(valueOperator, supplier);
        return this;
    }

    @Override
    public final _JoinSpec<I> repeatable(BiFunction<MappingType, Object, Expression> valueOperator
            , Function<String, ?> function, String keyName) {
        this.getNoOnBlock().repeatable(valueOperator, function, keyName);
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(Supplier<Expression> supplier) {
        this.getNoOnBlock().ifRepeatable(supplier);
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , @Nullable Number seedValue) {
        this.getNoOnBlock().ifRepeatable(valueOperator, seedValue);
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , Supplier<Number> supplier) {
        this.getNoOnBlock().ifRepeatable(valueOperator, supplier);
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRepeatable(BiFunction<MappingType, Object, Expression> valueOperator
            , Function<String, ?> function, String keyName) {
        this.getNoOnBlock().ifRepeatable(valueOperator, function, keyName);
        return this;
    }

    @Override
    public final _WindowAsClause<_WindowCommaSpec<I>> window(String windowName) {
        return PostgreSupports.postgreNamedWindow(windowName, this.context, this::onAddWindow);
    }

    @Override
    public final _OrderBySpec<I> window(Consumer<PostgreWindows> consumer) {
        consumer.accept(new PostgreWindowBuilder(this));
        if (this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<I> ifWindow(Consumer<PostgreWindows> consumer) {
        consumer.accept(new PostgreWindowBuilder(this));
        return this;
    }

    @Override
    public final _WindowAsClause<_WindowCommaSpec<I>> comma(String windowName) {
        return PostgreSupports.postgreNamedWindow(windowName, this.context, this::onAddWindow);
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
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table) {
        if (this.lockMode != null) {
            this.ofTaleList = Collections.singletonList(table);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
        if (this.lockMode != null) {
            this.ofTaleList = ArrayUtils.asUnmodifiableList(table1, table2);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
        if (this.lockMode != null) {
            this.ofTaleList = ArrayUtils.asUnmodifiableList(table1, table2, table3);
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
            this.ofTaleList = _CollectionUtils.asUnmodifiableList(list);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
        if (this.lockMode != null) {
            final List<TableMeta<?>> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.ofTaleList = _CollectionUtils.asUnmodifiableList(list);
            } else {
                this.ofTaleList = null;
            }
        }
        return this;
    }

    @Override
    public final _LockSpec<I> noWait() {
        this.lockWaitOption = this.lockWaitOption == null ? null : LockWaitOption.NOWAIT;
        return this;
    }

    @Override
    public final _LockSpec<I> skipLocked() {
        this.lockWaitOption = this.lockWaitOption == null ? null : LockWaitOption.SKIP_LOCKED;
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
        this.noOnBlock = null;
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
    final List<PostgreSyntax.Modifier> asModifierList(@Nullable List<PostgreSyntax.Modifier> modifiers) {
        return CriteriaUtils.asModifierList(this.context, modifiers, PostgreUtils::selectModifier);
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
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String alias) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final PostgreSupports.PostgreNoOnTableBlock block;
        block = new PostgreSupports.PostgreNoOnTableBlock(joinType, modifier, table, alias);
        this.noOnBlock = block;
        return block;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem
            , String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new TableBlock.NoOnModifierTableBlock(joinType, modifier, tableItem, alias);
    }

    @Override
    final _TableSampleOnSpec<I> createTableBlock(_JoinType joinType, @Nullable TableModifier modifier
            , TableMeta<?> table, String tableAlias) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new OnTableBlock<>(joinType, modifier, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }


    /*-------------------below private method -------------------*/

    /**
     * @see #from()
     * @see #crossJoin()
     */
    private _JoinSpec<I> nestedNonCrossEnd(final _JoinType joinType, final NestedItems nestedItems) {
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
    private _OnClause<_JoinSpec<I>> nestedJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertStandardJoinType();

        final OnClauseTableBlock<_JoinSpec<I>> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    private PostgreSupports.PostgreNoOnTableBlock getNoOnBlock() {
        final PostgreSupports.PostgreNoOnTableBlock block = this.noOnBlock;
        if (this.context.lastBlock() != block) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return block;
    }

    /**
     * @see #window(String)
     * @see #comma(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final _Window window) {
        List<_Window> list = this.windowList;
        if (list == null) {
            list = new ArrayList<>();
            this.windowList = list;
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        windowList.add(window);
        return this;
    }

    private static final class PostgreWindowBuilder implements PostgreWindows {

        private final PostgreQueries<?> statement;

        private PostgreWindowBuilder(PostgreQueries<?> statement) {
            this.statement = statement;
        }

        @Override
        public _WindowAsClause<PostgreWindows> window(String name) {
            return PostgreSupports.postgreNamedWindow(name, this.statement.context, this::windowEnd);
        }

        private PostgreWindows windowEnd(_Window window) {
            this.statement.onAddWindow(window);
            return this;
        }


    }//PostgreWindowBuilder

    private static final class OnTableBlock<I extends Item> extends PostgreSupports.PostgreOnTableBlock<
            PostgreQuery._RepeatableOnClause<I>,
            Statement._OnClause<PostgreQuery._JoinSpec<I>>,
            PostgreQuery._JoinSpec<I>>
            implements PostgreQuery._TableSampleOnSpec<I>
            , PostgreQuery._RepeatableOnClause<I> {

        private OnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TableMeta<?> tableItem, String alias
                , PostgreQuery._JoinSpec<I> stmt) {
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
            return _StringUtils.builder()
                    .append(PostgreLockMode.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//PostgreLockMode


    private static final class SimpleSelect<I extends Item> extends PostgreQueries<I>
            implements Select {

        private final Function<Select, I> function;

        private SimpleSelect(@Nullable _WithClauseSpec withSpec, CriteriaContext context, Function<Select, I> function) {
            super(withSpec, context);
            this.function = function;
        }

        private SimpleSelect(CriteriaContext context, Function<Select, I> function) {
            super(null, context);
            this.function = function;
        }

        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext bracketContext, queryContext;
            bracketContext = CriteriaContexts.bracketContext(this.context.endContextBeforeSelect());

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(bracketContext, this.function);

            queryContext = CriteriaContexts.primaryQuery(bracketContext);
            return new SimpleSelect<>(null, queryContext, bracket::parenRowSetEnd);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _MinWithSpec<I> createQueryUnion(UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
        }


    }//SimpleSelect


    private static final class SimpleSubQuery<I extends Item> extends PostgreQueries<I>
            implements SubQuery {

        private final Function<SubQuery, I> function;

        private SimpleSubQuery(CriteriaContext context, Function<SubQuery, I> function) {
            super(null, context);
            this.function = function;
        }

        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext bracketContext, queryContext;
            bracketContext = CriteriaContexts.bracketContext(this.context.endContextBeforeSelect());

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(bracketContext, this.function);

            queryContext = CriteriaContexts.subQueryContext(bracketContext);
            return new SimpleSubQuery<>(queryContext, bracket::parenRowSetEnd);
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _MinWithSpec<I> createQueryUnion(UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }


    }//SimpleSubQuery


    private static abstract class PostgreBracketQueries<I extends Item, Q extends Query>
            extends BracketRowSet<
            I,
            Q,
            PostgreQuery._UnionOrderBySpec<I>,
            PostgreQuery._UnionLimitSpec<I>,
            PostgreQuery._UnionOffsetSpec<I>,
            PostgreQuery._UnionFetchSpec<I>,
            PostgreQuery._UnionLockSpec<I>,
            PostgreQuery._MinWithSpec<I>,
            RowSet,
            Object> implements PostgreQuery._UnionOrderBySpec<I>
            , PostgreQuery._UnionLockOfTableSpec<I>
            , Statement._RightParenClause<PostgreQuery._UnionOrderBySpec<I>> {

        private PostgreLockMode lockMode;

        private List<TableMeta<?>> ofTaleList;

        private LockWaitOption lockWaitOption;

        private PostgreBracketQueries(CriteriaContext context) {
            super(context);
        }

        @Override
        public final _UnionLockOfTableSpec<I> forUpdate() {
            this.lockMode = PostgreLockMode.FOR_UPDATE;
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> ifForUpdate(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.lockMode = PostgreLockMode.FOR_UPDATE;
            } else {
                this.lockMode = null;
            }
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> forShare() {
            this.lockMode = PostgreLockMode.FOR_SHARE;
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> ifForShare(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.lockMode = PostgreLockMode.FOR_SHARE;
            } else {
                this.lockMode = null;
            }
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> forNoKeyUpdate() {
            this.lockMode = PostgreLockMode.FOR_NO_KEY_UPDATE;
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> forKeyShare() {
            this.lockMode = PostgreLockMode.FOR_KEY_SHARE;
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> ifForNoKeyUpdate(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.lockMode = PostgreLockMode.FOR_NO_KEY_UPDATE;
            } else {
                this.lockMode = null;
            }
            return this;
        }

        @Override
        public final _UnionLockOfTableSpec<I> ifForKeyShare(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.lockMode = PostgreLockMode.FOR_KEY_SHARE;
            } else {
                this.lockMode = null;
            }
            return this;
        }

        @Override
        public final _UnionLockWaitOptionSpec<I> of(TableMeta<?> table) {
            if (this.lockMode != null) {
                this.ofTaleList = Collections.singletonList(table);
            }
            return this;
        }

        @Override
        public final _UnionLockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
            if (this.lockMode != null) {
                this.ofTaleList = ArrayUtils.asUnmodifiableList(table1, table2);
            }
            return this;
        }

        @Override
        public final _UnionLockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
            if (this.lockMode != null) {
                this.ofTaleList = ArrayUtils.asUnmodifiableList(table1, table2, table3);
            }
            return this;
        }

        @Override
        public final _UnionLockWaitOptionSpec<I> of(Consumer<Consumer<TableMeta<?>>> consumer) {
            if (this.lockMode != null) {
                final List<TableMeta<?>> list = new ArrayList<>();
                consumer.accept(list::add);
                if (list.size() == 0) {
                    throw CriteriaUtils.ofTableListIsEmpty(this.context);
                }
                this.ofTaleList = _CollectionUtils.asUnmodifiableList(list);
            }
            return this;
        }

        @Override
        public final _UnionLockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
            if (this.lockMode != null) {
                final List<TableMeta<?>> list = new ArrayList<>();
                consumer.accept(list::add);
                if (list.size() > 0) {
                    this.ofTaleList = _CollectionUtils.asUnmodifiableList(list);
                } else {
                    this.ofTaleList = null;
                }
            }
            return this;
        }

        @Override
        public final _UnionLockSpec<I> noWait() {
            this.lockWaitOption = this.lockMode == null ? null : LockWaitOption.NOWAIT;
            return this;
        }

        @Override
        public final _UnionLockSpec<I> skipLocked() {
            this.lockWaitOption = this.lockMode == null ? null : LockWaitOption.SKIP_LOCKED;
            return this;
        }

        @Override
        public final _UnionLockSpec<I> ifNoWait(BooleanSupplier predicate) {
            if (this.lockMode != null && predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.NOWAIT;
            } else {
                this.lockWaitOption = null;
            }
            return this;
        }

        @Override
        public final _UnionLockSpec<I> ifSkipLocked(BooleanSupplier predicate) {
            if (this.lockMode != null && predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
            } else {
                this.lockWaitOption = null;
            }
            return this;
        }


        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }

        @Override
        final Object createRowSetUnion(UnionType unionType, RowSet right) {
            //standard query don't support union VALUES statement
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//PostgreBracketQueries


    private static final class BracketSelect<I extends Item>
            extends PostgreBracketQueries<I, Select>
            implements Select {

        private final Function<Select, I> function;

        private BracketSelect(CriteriaContext context, Function<Select, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._MinWithSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
        }


    }//BracketSelect

    private static final class BracketSubQuery<I extends Item>
            extends PostgreBracketQueries<I, SubQuery>
            implements SubQuery {

        private final Function<SubQuery, I> function;

        private BracketSubQuery(CriteriaContext context, Function<SubQuery, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        PostgreQuery._MinWithSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }


    }//BracketSubQuery


    private static final class UnionAndSelectClause<I extends Item>
            extends WithSelectClauseDispatcher<
            PostgreCtes,
            PostgreQuery._SelectSpec<I>,
            Postgres.Modifier,
            PostgreQuery._FromSpec<I>>
            implements PostgreQuery._MinWithSpec<I> {

        private final Select left;

        private final UnionType unionType;

        private final Function<Select, I> function;

        private UnionAndSelectClause(Select left, UnionType unionType, Function<Select, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }


        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext bracketContext, queryContext;
            bracketContext = CriteriaContexts.unionBracketContext(((CriteriaContextSpec) this.left).getContext());

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(bracketContext, this::unionRight);

            queryContext = CriteriaContexts.primaryQuery(bracketContext);
            return new SimpleSelect<>(queryContext, bracket::parenRowSetEnd);
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            context = CriteriaContexts.unionSelectContext(leftContext);
            return new SimpleSelect<>(context, this::unionRight);
        }

        private I unionRight(final Select right) {
            return this.function.apply(new UnionSelect(PostgreDialect.POSTGRE15, this.left, this.unionType, right));
        }


    }//UnionSelectClause


    /**
     * @see #createQueryUnion(UnionType)
     * @see #createQueryUnion(UnionType)
     */
    private static final class UnionAndSubQueryClause<I extends Item>
            extends WithSelectClauseDispatcher<
            PostgreCtes,
            _SelectSpec<I>,
            Postgres.Modifier,
            _FromSpec<I>>
            implements _MinWithSpec<I> {

        private final SubQuery left;

        private final UnionType unionType;

        private final Function<SubQuery, I> function;

        private UnionAndSubQueryClause(SubQuery left, UnionType unionType, Function<SubQuery, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }


        @Override
        public _MinWithSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext bracketContext, queryContext;
            bracketContext = CriteriaContexts.unionBracketContext(((CriteriaContextSpec) this.left).getContext());

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(bracketContext, this::unionRight);

            queryContext = CriteriaContexts.subQueryContext(bracketContext);
            return new SimpleSubQuery<>(queryContext, bracket::parenRowSetEnd);
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            context = CriteriaContexts.unionSubQueryContext(leftContext);
            return new SimpleSubQuery<>(context, this::unionRight);
        }

        private I unionRight(final SubQuery right) {
            return this.function.apply(new UnionSubQuery(this.left, this.unionType, right));
        }


    }//UnionAndSubQueryClause


    private static final class CteComma<I extends Item>
            extends SelectClauseDispatcher<PostgreSyntax.Modifier, PostgreQuery._FromSpec<I>>
            implements PostgreQuery._CteComma<I> {

        private final boolean recursive;

        private final PostgreQueries<I> clause;

        private final Function<String, PostgreStatement._StaticCteLeftParenSpec<PostgreQuery._CteComma<I>>> function;


        private CteComma(boolean recursive, PostgreQueries<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        _DynamicHintModifierSelectClause<PostgreSyntax.Modifier, _FromSpec<I>> createSelectClause() {
            this.clause.endStaticWithClause(this.recursive);
            return this.clause;
        }


    }//CteComma


}
