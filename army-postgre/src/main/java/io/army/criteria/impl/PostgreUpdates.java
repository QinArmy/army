package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchReturningUpdate;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a abstract implementation of {@link PostgreUpdate}.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreUpdates<I extends Item, Q extends Item, T>
        extends JoinableUpdate<
        I,
        PostgreCtes,
        PostgreUpdate._SingleUpdateClause<I, Q>,
        FieldMeta<T>,
        PostgreUpdate._SingleSetFromSpec<I, Q, T>,
        PostgreUpdate._TableSampleJoinSpec<I, Q>,
        Statement._AsClause<PostgreUpdate._ParensJoinSpec<I, Q>>,
        PostgreUpdate._SingleJoinSpec<I, Q>,
        PostgreStatement._FuncColumnDefinitionAsClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
        PostgreUpdate._TableSampleOnSpec<I, Q>,
        Statement._AsParensOnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
        Statement._OnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
        PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreUpdate._SingleJoinSpec<I, Q>>>,
        PostgreUpdate._ReturningSpec<I, Q>,
        PostgreUpdate._SingleWhereAndSpec<I, Q>>
        implements PostgreUpdate,
        _PostgreUpdate,
        PostgreStatement._StaticTableSampleClause<PostgreUpdate._RepeatableJoinClause<I, Q>>,
        PostgreUpdate._SingleSetFromSpec<I, Q, T>,
        PostgreUpdate._TableSampleJoinSpec<I, Q>,
        PostgreUpdate._RepeatableJoinClause<I, Q>,
        PostgreUpdate._ParensJoinSpec<I, Q>,
        PostgreUpdate._SingleWhereAndSpec<I, Q>,
        PostgreUpdate._StaticReturningCommaSpec<Q> {


    /**
     * <p>
     * create new simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     */
    static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> simple() {
        return new PrimarySimpleUpdateClause();
    }


    /**
     * <p>
     * create new simple(non-batch) single-table UPDATE statement that is sub statement in with clause.
     * </p>
     */
    static <I extends Item> PostgreUpdate._SingleWithSpec<I, I> subSimpleUpdate(CriteriaContext outerContext,
                                                                                Function<SubStatement, I> function) {
        return new SubSimpleUpdateClause<>(outerContext, function);
    }

    /**
     * <p>
     * create new batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    static PostgreUpdate._SingleWithSpec<_BatchUpdateParamSpec, _BatchReturningUpdateParamSpec> batchUpdate() {
        return new PrimaryBatchUpdateClause();
    }


    private final boolean recursive;

    private final List<_Cte> cteList;

    private final SQLs.WordOnly onlyModifier;

    private final TableMeta<T> targetTable;

    private final SQLs.SymbolAsterisk asterisk;

    private final String targetTableAlias;

    private _TabularBlock fromCrossBlock;

    private List<_SelectItem> returningList;

    private PostgreUpdates(PostgreUpdateClause<I, Q> clause, TableMeta<T> updateTable) {
        super(clause, clause.context);
        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();
        this.onlyModifier = clause.onlyModifier;
        this.targetTable = updateTable;

        this.asterisk = clause.starModifier;
        this.targetTableAlias = clause.targetTableAlias;
        if (!_StringUtils.hasText(this.targetTableAlias)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::tableAliasIsEmpty);
        }
    }

    @Override
    public final _SingleFromSpec<I, Q> sets(Consumer<UpdateStatement._BatchRowPairs<FieldMeta<T>>> consumer) {
        consumer.accept(CriteriaSupports.batchRowPairs(this::onAddItemPair));
        return this;
    }


    @Override
    public final PostgreUpdate._RepeatableJoinClause<I, Q> tableSample(Expression method) {
        this.getFromCrossBlock().onSampleMethod((ArmyExpression) method);
        return this;
    }

    @Override
    public final PostgreUpdate._RepeatableJoinClause<I, Q> tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                                                                       BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final <E> PostgreUpdate._RepeatableJoinClause<I, Q> tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                                                                           BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.tableSample(method.apply(valueOperator, supplier.get()));
    }

    @Override
    public final PostgreUpdate._RepeatableJoinClause<I, Q> tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                                                       BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                                                       String keyName) {
        return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
    }

    @Override
    public final PostgreUpdate._RepeatableJoinClause<I, Q> ifTableSample(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.tableSample(expression);
        }
        return this;
    }

    @Override
    public final <E> PostgreUpdate._RepeatableJoinClause<I, Q> ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                                                                             BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final PostgreUpdate._RepeatableJoinClause<I, Q> ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                                                         BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                                                         String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Expression seed) {
        this.getFromCrossBlock().onSeed((ArmyExpression) seed);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Supplier<Expression> supplier) {
        return this.repeatable(supplier.get());
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
        return this.repeatable(valueOperator.apply(seedValue));
    }

    @Override
    public final <E extends Number> _SingleJoinSpec<I, Q> repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.repeatable(valueOperator.apply(supplier.get()));
    }

    @Override
    public final _SingleJoinSpec<I, Q> repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                                  String keyName) {
        return this.repeatable(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifRepeatable(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.repeatable(expression);
        }
        return this;
    }

    @Override
    public final <E extends Number> _SingleJoinSpec<I, Q> ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                                    String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> from(Function<_NestedLeftParenSpec<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final _SingleJoinSpec<I, Q> crossJoin(Function<_NestedLeftParenSpec<_SingleJoinSpec<I, Q>>, _SingleJoinSpec<I, Q>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> join(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_SingleJoinSpec<I, Q>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<_SingleJoinSpec<I, Q>>>, _OnClause<_SingleJoinSpec<I, Q>>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> parens(String first, String... rest) {
        this.getFromDerived().parens(first, rest);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> parens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().parens(this.context, consumer);
        return this;
    }

    @Override
    public final _SingleJoinSpec<I, Q> ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().ifParens(this.context, consumer);
        return this;
    }


    @Override
    public final _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
        this.where(new PostgreCursorPredicate(cursorName));
        return this;
    }


    @Override
    public final _DqlUpdateSpec<Q> returningAll() {
        this.returningList = PostgreSupports.EMPTY_SELECT_ITEM_LIST;
        return this;
    }

    @Override
    public final _DqlUpdateSpec<Q> returning(Consumer<Returnings> consumer) {
        this.returningList = CriteriaUtils.selectionList(this.context, consumer);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Selection selection) {
        this.onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Selection selection1, Selection selection2) {
        this.onAddSelection(selection1)
                .onAddSelection(selection2);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias) {
        this.onAddSelection(function.apply(alias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function1, String alias1,
                                                        Function<String, Selection> function2, String alias2) {
        this.onAddSelection(function1.apply(alias1))
                .onAddSelection(function2.apply(alias2));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                        Selection selection) {
        this.onAddSelection(function.apply(alias))
                .onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(Selection selection, Function<String, Selection> function,
                                                        String alias) {
        this.onAddSelection(selection)
                .onAddSelection(function.apply(alias));
        return this;
    }


    @Override
    public final _StaticReturningCommaSpec<Q> returning(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk star) {
        this.onAddSelection(SelectionGroups.derivedGroup(this.context.getNonNullDerived(derivedAlias), derivedAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        this.onAddSelection(SelectionGroups.singleGroup(table, tableAlias));
        return this;
    }

    @Override
    public final <P> _StaticReturningCommaSpec<Q> returning(
            String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
            String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
        }
        this.onAddSelection(SelectionGroups.singleGroup(parent, parenAlias))
                .onAddSelection(SelectionGroups.groupWithoutId(child, childAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3,
                                                        TableField field4) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3)
                .onAddSelection(field4);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Selection selection) {
        this.onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Selection selection1, Selection selection2) {
        this.onAddSelection(selection1)
                .onAddSelection(selection2);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias) {
        this.onAddSelection(function.apply(alias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function1, String alias1,
                                                    Function<String, Selection> function2, String alias2) {
        this.onAddSelection(function1.apply(alias1))
                .onAddSelection(function2.apply(alias2));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias,
                                                    Selection selection) {
        this.onAddSelection(function.apply(alias))
                .onAddSelection(selection);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(Selection selection, Function<String, Selection> function,
                                                    String alias) {
        this.onAddSelection(selection)
                .onAddSelection(function.apply(alias));
        return this;
    }


    @Override
    public final _StaticReturningCommaSpec<Q> comma(String derivedAlias, SQLs.SymbolPeriod period,
                                                    SQLs.SymbolAsterisk star) {
        this.onAddSelection(SelectionGroups.derivedGroup(this.context.getNonNullDerived(derivedAlias), derivedAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(String tableAlias, SQLs.SymbolPeriod period,
                                                    TableMeta<?> table) {
        this.onAddSelection(SelectionGroups.singleGroup(table, tableAlias));
        return this;
    }

    @Override
    public final <P> _StaticReturningCommaSpec<Q> comma(
            String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
            String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child) {
        if (child.parentMeta() != parent) {
            throw CriteriaUtils.childParentNotMatch(this.context, parent, child);
        }
        this.onAddSelection(SelectionGroups.singleGroup(parent, parenAlias))
                .onAddSelection(SelectionGroups.groupWithoutId(child, childAlias));
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3);
        return this;
    }

    @Override
    public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3,
                                                    TableField field4) {
        this.onAddSelection(field1)
                .onAddSelection(field2)
                .onAddSelection(field3)
                .onAddSelection(field4);
        return this;
    }


    @Override
    public final List<? extends _SelectItem> returningList() {
        // use wrapper,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public final Q asReturningUpdate() {
        final List<_SelectItem> returningList = this.returningList;
        if (!(returningList instanceof ArrayList || returningList == PostgreSupports.EMPTY_SELECT_ITEM_LIST)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.endUpdateStatement();
        if (returningList instanceof ArrayList) {
            this.returningList = _Collections.unmodifiableList(returningList);
        } else {
            this.returningList = CriteriaUtils.returningAll(this.targetTable, this.targetTableAlias, this.tableBlockList());
        }
        return this.onAsReturningUpdate();
    }

    @Override
    public final SQLWords modifier() {
        return this.onlyModifier;
    }

    @Override
    public final TableMeta<?> table() {
        return this.targetTable;
    }

    @Override
    public final SQLs.SymbolAsterisk asterisk() {
        return this.asterisk;
    }

    @Override
    public final String tableAlias() {
        return this.targetTableAlias;
    }


    @Override
    final I onAsUpdate() {
        if (this.returningList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.returningList = Collections.emptyList();
        return this.onAsPostgreUpdate();
    }

    @Override
    final Dialect statementDialect() {
        return PostgreUtils.DIALECT;
    }


    @Override
    final void onBeforeContextEnd() {
        //no-op
    }

    @Override
    final void onClear() {
        this.returningList = null;
        if (this instanceof PostgreUpdates.PostgreBatchUpdate) {
            ((PostgreBatchUpdate<?>) this).paramList = null;
        }
    }

    abstract I onAsPostgreUpdate();

    abstract Q onAsReturningUpdate();


    @Override
    final boolean isIllegalTableModifier(@Nullable Query.TableModifier modifier) {
        return CriteriaUtils.isIllegalOnly(modifier);
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final PostgreCtes createCteBuilder(boolean recursive) {
        // user cast criteria api
        throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
    }

    @Override
    final _TableSampleJoinSpec<I, Q> onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final PostgreSupports.FromClauseTableBlock block;
        block = new PostgreSupports.FromClauseTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final _SingleJoinSpec<I, Q> onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    final _SingleJoinSpec<I, Q> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        this.blockConsumer.accept(TabularBlocks.fromNestedBlock(joinType, nestedItems));
        return this;
    }


    @Override
    final _AsClause<_ParensJoinSpec<I, Q>> onFromDerived(_JoinType joinType,
                                                         @Nullable Query.DerivedModifier modifier,
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
    final _FuncColumnDefinitionAsClause<_SingleJoinSpec<I, Q>> onFromUndoneFunc(
            final _JoinType joinType, final @Nullable DerivedModifier modifier, final UndoneFunction func) {
        return alias -> PostgreBlocks.fromUndoneFunc(joinType, modifier, func, alias, this, this.blockConsumer);
    }


    @Override
    final _TableSampleOnSpec<I, Q> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                               TableMeta<?> table, String alias) {
        final SimpleJoinClauseTableBlock<I, Q> block;
        block = new SimpleJoinClauseTableBlock<>(joinType, modifier, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }


    @Override
    final _AsParensOnClause<_SingleJoinSpec<I, Q>> onJoinDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                                 DerivedTable table) {
        return alias -> {
            final TabularBlocks.JoinClauseAliasDerivedBlock<_SingleJoinSpec<I, Q>> block;
            block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _OnClause<_SingleJoinSpec<I, Q>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                     _Cte cteItem, String alias) {
        final TabularBlocks.JoinClauseCteBlock<_SingleJoinSpec<I, Q>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _FuncColumnDefinitionAsClause<_OnClause<_SingleJoinSpec<I, Q>>> onJoinUndoneFunc(
            final _JoinType joinType, final @Nullable DerivedModifier modifier, final UndoneFunction func) {
        return alias -> PostgreBlocks.joinUndoneFunc(joinType, modifier, func, alias, this, this.blockConsumer);
    }


    final _OnClause<_SingleJoinSpec<I, Q>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {

        final TabularBlocks.JoinClauseNestedBlock<_SingleJoinSpec<I, Q>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }


    private PostgreSupports.FromClauseTableBlock getFromCrossBlock() {
        final _TabularBlock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof PostgreSupports.FromClauseTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (PostgreSupports.FromClauseTableBlock) block;
    }


    final TabularBlocks.FromClauseAliasDerivedBlock getFromDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }

    private PostgreUpdates<I, Q, T> onAddSelection(final @Nullable SelectItem selectItem) {
        if (selectItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_SelectItem> list = this.returningList;
        if (list == null) {
            list = _Collections.arrayList();
            this.returningList = list;
        } else if (!(list instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (selectItem instanceof _SelectionGroup._TableFieldGroup) {
            final String tableAlias;
            tableAlias = ((_SelectionGroup._TableFieldGroup) selectItem).tableAlias();
            final TableMeta<?> groupTable;
            if (this.targetTableAlias.equals(tableAlias)) {
                groupTable = this.targetTable;
            } else {
                groupTable = this.context.getTable(tableAlias);
            }
            if (!((_SelectionGroup._TableFieldGroup) selectItem).isLegalGroup(groupTable)) {
                throw CriteriaUtils.unknownTableFieldGroup(this.context, (_SelectionGroup._TableFieldGroup) selectItem);
            }

        }
        list.add((_SelectItem) selectItem);
        return this;
    }


    private static final class PostgreSimpleUpdate<T> extends PostgreUpdates<Update, ReturningUpdate, T>
            implements Update {

        private PostgreSimpleUpdate(PrimarySimpleUpdateClause clause, TableMeta<T> updateTable) {
            super(clause, updateTable);
        }


        @Override
        Update onAsPostgreUpdate() {
            return this;
        }

        @Override
        ReturningUpdate onAsReturningUpdate() {
            return new ReturningUpdateWrapper(this);
        }


    }//PostgreSimpleUpdate

    private static final class PostgreBatchUpdate<T> extends PostgreUpdates<
            _BatchUpdateParamSpec,
            _BatchReturningUpdateParamSpec,
            T> implements _BatchUpdateParamSpec, BatchUpdate, _BatchStatement {

        private List<?> paramList;

        private PostgreBatchUpdate(PrimaryBatchUpdateClause clause, TableMeta<T> updateTable) {
            super(clause, updateTable);
        }


        @Override
        public BatchUpdate namedParamList(final List<?> paramList) {
            if (this.paramList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return list;
        }

        @Override
        _BatchUpdateParamSpec onAsPostgreUpdate() {
            return this;
        }

        @Override
        _BatchReturningUpdateParamSpec onAsReturningUpdate() {
            return this::createBatchReturningUpdate;
        }

        private <P> BatchReturningUpdate createBatchReturningUpdate(List<P> paramList) {
            return new PostgreBatchReturningUpdate(this, paramList);
        }

    }//PostgreBatchUpdate


    private static final class PostgreSubUpdate<I extends Item, T> extends PostgreUpdates<I, I, T>
            implements SubStatement, _ReturningDml {

        private final Function<SubStatement, I> function;

        private PostgreSubUpdate(SubSimpleUpdateClause<I> clause, TableMeta<T> updateTable) {
            super(clause, updateTable);
            this.function = clause.function;
        }


        @Override
        I onAsPostgreUpdate() {
            return this.function.apply(this);
        }

        @Override
        I onAsReturningUpdate() {
            return this.function.apply(this);
        }


    }//PostgreSubUpdate


    private static abstract class PostgreUpdateClause<I extends Item, Q extends Item>
            extends CriteriaSupports.WithClause<PostgreCtes, PostgreUpdate._SingleUpdateClause<I, Q>>
            implements PostgreUpdate._SingleWithSpec<I, Q>, ArmyStmtSpec {


        private SQLs.WordOnly onlyModifier;

        private SQLs.SymbolAsterisk starModifier;

        private String targetTableAlias;

        private PostgreUpdateClause(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
            super(spec, context);
        }

        @Override
        public final PostgreQuery._StaticCteParensSpec<_SingleUpdateClause<I, Q>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final PostgreQuery._StaticCteParensSpec<_SingleUpdateClause<I, Q>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs as, String tableAlias) {
            return this.doUpdate(null, table, null, as, tableAlias);
        }

        @Override
        public final <T> _SingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table,
                                                          SQLs.WordAs as, String tableAlias) {
            return this.doUpdate(only, table, null, as, tableAlias);
        }

        @Override
        public final <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, @Nullable SQLs.SymbolAsterisk star,
                                                          SQLs.WordAs as, String tableAlias) {
            return this.doUpdate(null, table, star, as, tableAlias);
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


        private <T> _SingleSetClause<I, Q, T> doUpdate(@Nullable SQLs.WordOnly only, @Nullable TableMeta<T> table,
                                                       @Nullable SQLs.SymbolAsterisk star, SQLs.WordAs as,
                                                       @Nullable String tableAlias) {
            if (only != null && only != SQLs.ONLY) {
                throw CriteriaUtils.errorModifier(this.context, only);
            } else if (star != null && star != SQLs.ASTERISK) {
                throw CriteriaUtils.errorModifier(this.context, star);
            } else if (as != SQLs.AS) {
                throw CriteriaUtils.errorModifier(this.context, as);
            } else if (table == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (tableAlias == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.onlyModifier = only;
            this.starModifier = star;
            this.targetTableAlias = tableAlias;
            return this.createUpdateStmt(table);
        }

        abstract <T> _SingleSetClause<I, Q, T> createUpdateStmt(TableMeta<T> table);


    }//SimpleUpdateClause

    private static final class PrimarySimpleUpdateClause extends PostgreUpdateClause<Update, ReturningUpdate> {


        private PrimarySimpleUpdateClause() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(PostgreUtils.DIALECT, null));
        }

        @Override
        <T> _SingleSetClause<Update, ReturningUpdate, T> createUpdateStmt(TableMeta<T> table) {
            return new PostgreSimpleUpdate<>(this, table);
        }


    }//PrimarySimpleUpdateClause

    private static final class PrimaryBatchUpdateClause extends PostgreUpdateClause<
            _BatchUpdateParamSpec,
            _BatchReturningUpdateParamSpec> {

        private PrimaryBatchUpdateClause() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(PostgreUtils.DIALECT, null));
        }

        @Override
        <T> _SingleSetClause<_BatchUpdateParamSpec, _BatchReturningUpdateParamSpec, T> createUpdateStmt(TableMeta<T> table) {
            return new PostgreBatchUpdate<>(this, table);
        }

    }//PrimaryBatchUpdateClause


    private static final class SubSimpleUpdateClause<I extends Item> extends PostgreUpdateClause<I, I> {

        private final Function<SubStatement, I> function;

        private SubSimpleUpdateClause(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, CriteriaContexts.subJoinableSingleDmlContext(PostgreUtils.DIALECT, outerContext));
            this.function = function;
        }

        @Override
        <T> _SingleSetClause<I, I, T> createUpdateStmt(TableMeta<T> table) {
            return new PostgreSubUpdate<>(this, table);
        }


    } //SubSimpleUpdateClause


    private static final class SimpleJoinClauseTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            _RepeatableOnClause<I, Q>,
            _OnClause<_SingleJoinSpec<I, Q>>,
            _SingleJoinSpec<I, Q>>
            implements _TableSampleOnSpec<I, Q>, _RepeatableOnClause<I, Q> {

        private SimpleJoinClauseTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias,
                                           _SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, table, alias, stmt);
        }


    }//SimpleJoinClauseTableBlock


    static abstract class PostgreUpdateWrapper extends CriteriaSupports.StatementMockSupport
            implements PostgreUpdate, _PostgreUpdate {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final SQLs.WordOnly only;

        private final TableMeta<?> targetTable;

        private final SQLs.SymbolAsterisk asterisk;

        private final String tableAlias;

        private final List<_ItemPair> itemPairList;

        private final List<_TabularBlock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<? extends _SelectItem> returningList;

        private Boolean prepared = Boolean.TRUE;

        private PostgreUpdateWrapper(PostgreUpdates<?, ?, ?> stmt) {
            super(stmt.context);

            this.recursive = stmt.recursive;
            this.cteList = stmt.cteList;
            this.only = stmt.onlyModifier;
            this.targetTable = stmt.targetTable;

            this.asterisk = stmt.asterisk;
            this.tableAlias = stmt.targetTableAlias;
            this.itemPairList = stmt.itemPairList();
            this.tableBlockList = stmt.tableBlockList();

            this.wherePredicateList = stmt.wherePredicateList();
            this.returningList = _Collections.safeUnmodifiableList(stmt.returningList);
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
        public final SQLWords modifier() {
            return this.only;
        }

        @Override
        public final TableMeta<?> table() {
            return this.targetTable;
        }


        @Override
        public final SQLs.SymbolAsterisk asterisk() {
            return this.asterisk;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final List<_ItemPair> itemPairList() {
            return this.itemPairList;
        }

        @Override
        public final List<_TabularBlock> tableBlockList() {
            return this.tableBlockList;
        }

        @Override
        public final List<_Predicate> wherePredicateList() {
            return this.wherePredicateList;
        }


        @Override
        public final List<? extends _SelectItem> returningList() {
            return this.returningList;
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public final void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = null;
        }

        @Override
        final Dialect statementDialect() {
            return PostgreUtils.DIALECT;
        }


    }//PostgreUpdateWrapper


    private static final class ReturningUpdateWrapper extends PostgreUpdateWrapper
            implements ReturningUpdate, _ReturningDml {

        private ReturningUpdateWrapper(PostgreSimpleUpdate<?> stmt) {
            super(stmt);
        }


    }//ReturningUpdateWrapper


    private static final class PostgreBatchReturningUpdate extends PostgreUpdateWrapper
            implements BatchReturningUpdate, _BatchStatement, _ReturningDml {

        private final List<?> paramList;

        private PostgreBatchReturningUpdate(PostgreBatchUpdate<?> stmt, List<?> paramList) {
            super(stmt);
            this.paramList = paramList;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }


    }//PostgreBatchReturningUpdate


}
