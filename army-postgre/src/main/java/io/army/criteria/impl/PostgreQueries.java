package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.impl.inner.postgre._PostgreQuery;
import io.army.criteria.postgre.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

abstract class PostgreQueries<I extends Item> extends SimpleQueries.WithCteSimpleQueries<
        I,
        PostgreCteBuilder,
        PostgreQuery._PostgreSelectClause<I>,
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
        PostgreQuery._UnionAndQuerySpec<I>>
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


    static <Q extends Item> PostgreQuery._WithSpec<Q> primaryQuery() {
        throw new UnsupportedOperationException();
    }

    static <Q extends Item> PostgreQuery._WithSpec<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        throw new UnsupportedOperationException();
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


    private PostgreQueries(CriteriaContext context) {
        super(context);
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
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForUpdate(BooleanSupplier predicate) {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForShare(BooleanSupplier predicate) {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> forNoKeyUpdate() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> forKeyShare() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForNoKeyUpdate(BooleanSupplier supplier) {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForKeyShare(BooleanSupplier supplier) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<TableMeta<?>>> consumer) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
        return null;
    }

    @Override
    public final _LockSpec<I> noWait() {
        return null;
    }

    @Override
    public final _LockSpec<I> skipLocked() {
        return null;
    }

    @Override
    public final _LockSpec<I> ifNoWait(BooleanSupplier predicate) {
        return null;
    }

    @Override
    public final _LockSpec<I> ifSkipLocked(BooleanSupplier predicate) {
        return null;
    }

    @Override
    final void onEndQuery() {

    }


    @Override
    final void onClear() {

    }

    @Override
    final List<PostgreSyntax.Modifier> asModifierList(@Nullable List<PostgreSyntax.Modifier> modifiers) {
        return null;
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        return null;
    }

    @Override
    final _UnionAndQuerySpec<I> createQueryUnion(UnionType unionType) {
        return null;
    }


    @Override
    final PostgreCteBuilder createCteBuilder(boolean recursive) {
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
    }


    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String alias) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final PostgreSupports.PostgreNoOnTableBlock block;
        block = new PostgreSupports.PostgreNoOnTableBlock(joinType, table, modifier, alias);
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
        return new OnTableBlock<>(joinType, table, modifier, tableAlias, this);
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

        private OnTableBlock(_JoinType joinType, TableMeta<?> tableItem
                , @Nullable SQLWords modifier, String alias
                , PostgreQuery._JoinSpec<I> stmt) {
            super(joinType, tableItem, modifier, alias, stmt);
        }


    }//OnTableBlock


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
