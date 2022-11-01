package io.army.criteria.impl;

import io.army.criteria.*;
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
        PostgreQuery._QueryWithComplexSpec<I>>
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


    static <I extends Item> PostgreQueries<I> primaryQuery(@Nullable _WithClauseSpec withSpec
            , @Nullable CriteriaContext outerContext, Function<Select, I> function) {
        return new SimpleSelect<>(withSpec, outerContext, function);
    }


    static <I extends Item> PostgreQueries<I> subQuery(@Nullable _WithClauseSpec withSpec
            , CriteriaContext outerContext, Function<SubQuery, I> function) {
        return new SimpleSubQuery<>(withSpec, outerContext, function);
    }


    static <I extends Item> Function<String, _StaticCteLeftParenSpec<I>> complexCte(CriteriaContext context, I comma) {
        return new PostgreCteComplexCommand<>(context, comma)::nextCte;
    }

    static <I extends Item> PostgreQuery._DynamicSubMaterializedSpec<I> dynamicCteQuery(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        return new DynamicCteSimpleSubQuery<>(outerContext, function);
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
            unionFunc = right -> this.function.apply(new UnionSelect(PostgreDialect.POSTGRE15, this, unionType, right));
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
            unionFunc = right -> this.function.apply(new UnionSelect(PostgreDialect.POSTGRE15, this, unionType, right));
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
            PostgreQuery._FromSpec<I>>
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
            extends SelectClauseDispatcher<PostgreSyntax.Modifier, PostgreQuery._FromSpec<I>>
            implements PostgreQuery._CteComma<I> {

        private final boolean recursive;

        private final PostgreQueries<I> clause;

        private final Function<String, _StaticCteLeftParenSpec<_CteComma<I>>> function;


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
        PostgreQueries<I> createSelectClause() {
            this.clause.endStaticWithClause(this.recursive);
            return this.clause;
        }


    }//CteComma


    private static final class PostgreCteComplexCommand<I extends Item>
            extends SimpleQueries.SelectClauseDispatcher<
            Postgres.Modifier,
            _FromSpec<_CteSearchSpec<I>>>
            implements _StaticCteMaterializedSpec<I>
            , _StaticCteLeftParenSpec<I>
            , _AsCteClause<I> {

        private final boolean recursive;

        private final CriteriaContext context;

        private final I item;

        private String currentCteName;

        private List<String> columnAliasList;

        private PostgreSupports.MaterializedOption materializedOption;

        private Statement._LeftParenStringQuadraOptionalSpec<_StaticCteAsClause<I>> columnAliasClause;

        private PostgreCteComplexCommand(CriteriaContext context, I item) {
            this.recursive = context.isWithRecursive();
            this.context = context;
            this.item = item;
        }


        @Override
        public _RightParenClause<_StaticCteAsClause<I>> leftParen(String string) {
            return this.getColumnAliasClause()
                    .leftParen(string);
        }

        @Override
        public _CommaStringDualSpec<_StaticCteAsClause<I>> leftParen(String string1, String string2) {
            return this.getColumnAliasClause()
                    .leftParen(string1, string2);
        }

        @Override
        public _RightParenClause<_StaticCteAsClause<I>> leftParen(Consumer<Consumer<String>> consumer) {
            return this.getColumnAliasClause()
                    .leftParen(consumer);
        }

        @Override
        public _CommaStringQuadraSpec<_StaticCteAsClause<I>> leftParen(String string1, String string2, String string3
                , String string4) {
            return this.getColumnAliasClause()
                    .leftParen(string1, string2, string3, string4);
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
        public PostgreInsert._StaticSubPreferLiteralSpec<_AsCteClause<I>> nullHandle(NullHandleMode mode) {
            return PostgreInserts.staticSubInsert(this.context, this::subStatementEnd)
                    .nullHandle(mode);
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
        public _StaticCteSelectSpec<_RightParenClause<_UnionOrderBySpec<_CteSearchSpec<I>>>> leftParen() {
            final BracketSubQuery<_CteSearchSpec<I>> bracket;
            bracket = new BracketSubQuery<>(null, this.context, this::queryEnd);
            return new StaticCteSelectSpec<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public I asCte() {
            return this.item;
        }

        @Override
        PostgreQueries<_CteSearchSpec<I>> createSelectClause() {
            return new SimpleSubQuery<>(null, this.context, this::queryEnd);
        }


        private _StaticCteLeftParenSpec<I> nextCte(final @Nullable String name) {
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


        private _CteSearchSpec<I> queryEnd(final SubQuery query) {
            final _CteSearchSpec<I> clause;
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

        private Statement._LeftParenStringQuadraOptionalSpec<_StaticCteAsClause<I>> getColumnAliasClause() {
            Statement._LeftParenStringQuadraOptionalSpec<_StaticCteAsClause<I>> columnAliasClause;
            columnAliasClause = this.columnAliasClause;
            if (columnAliasClause == null) {
                columnAliasClause = CriteriaSupports.stringQuadra(this.context, this::columnAliasClauseEnd);
                this.columnAliasClause = columnAliasClause;
            }
            return columnAliasClause;
        }

        private _StaticCteAsClause<I> columnAliasClauseEnd(final List<String> list) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = list;
            return this;
        }


    }//PostgreCteComplexCommand


    private static final class StaticCteSelectSpec<I extends Item> extends SimpleQueries.SelectClauseDispatcher<
            Postgres.Modifier,
            _FromSpec<I>> implements _StaticCteSelectSpec<I> {

        private final CriteriaContext context;

        private final Function<SubQuery, I> function;

        /**
         * @see PostgreCteComplexCommand#leftParen()
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
