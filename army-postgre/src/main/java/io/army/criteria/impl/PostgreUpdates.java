package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchReturningUpdate;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Assert;
import io.army.util._CollectionUtils;

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
@SuppressWarnings("unchecked")
abstract class PostgreUpdates<I extends Item, T, SR, FT, FS, FC extends Item, JT, JS, JC, TR, WR, WA>
        extends JoinableUpdate<I, FieldMeta<T>, SR, FT, FS, FC, JT, JS, JC, WR, WA, Object, Object, Object, Object>
        implements PostgreUpdate,
        _PostgreUpdate,
        PostgreStatement._StaticTableSampleClause<TR>,
        PostgreStatement._RepeatableClause<FC>,
        Statement._ParensStringClause<FC>,
        PostgreStatement._PostgreFromNestedClause<FC>,
        PostgreStatement._PostgreJoinNestedClause<Statement._OnClause<FC>>,
        PostgreStatement._PostgreCrossNestedClause<FC>,
        PostgreStatement._PostgreDynamicJoinCrossClause<FC>,
        DialectStatement._WhereCurrentOfClause<WR> {


    /**
     * <p>
     * create new simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     */
    static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> simpleUpdate() {
        return new PrimarySimpleUpdateClause();
    }


    /**
     * <p>
     * create new simple(non-batch) single-table UPDATE statement that is primary statement for multi-statement.
     * </p>
     */
    static <I extends Item> PostgreUpdate._SingleUpdateClause<I, I> simple(
            MultiStmtSpec spec, Function<PrimaryStatement, I> function) {
        return new PrimarySimpleUpdateClauseForMultiStmt<>(spec, function);
    }

    /**
     * <p>
     * create new simple(non-batch) single-table UPDATE statement that is sub statement in with clause.
     * </p>
     */
    static <I extends Item> PostgreUpdate._SingleWithSpec<I, I> cteSimple(CriteriaContext outerContext,
                                                                          Function<SubStatement, I> function) {
        return new SubSimpleUpdateClause<>(outerContext, function);
    }

    /**
     * <p>
     * create new batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    static PostgreUpdate._BatchSingleWithSpec<BatchUpdate, BatchReturningUpdate> batchUpdate() {
        return new BatchUpdateClause();
    }


    private final boolean recursive;

    private final List<_Cte> cteList;

    private final SQLs.WordOnly modifier;

    private final TableMeta<?> updateTable;

    private final String tableAlias;

    _TableBlock fromCrossBlock;

    private PostgreUpdates(PostgreUpdateClause<?> clause) {
        super(clause.context);
        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();
        this.modifier = clause.modifier;
        if (this.modifier != null && this.modifier != SQLs.ONLY) {
            throw CriteriaUtils.errorModifier(this.context, this.modifier);
        }
        this.updateTable = clause.updateTable;

        this.tableAlias = clause.tableAlias;
        assert this.updateTable != null && this.tableAlias != null;
    }


    @Override
    public final TR tableSample(Expression method) {
        this.getFromCrossBlock().onSampleMethod((ArmyExpression) method);
        return (TR) this;
    }

    @Override
    public final TR tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                                BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final <E> TR tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                                    BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.tableSample(method.apply(valueOperator, supplier.get()));
    }

    @Override
    public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                String keyName) {
        return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
    }

    @Override
    public final TR ifTableSample(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.tableSample(expression);
        }
        return (TR) this;
    }

    @Override
    public final <E> TR ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method,
                                      BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return (TR) this;
    }

    @Override
    public final TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                  BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                  String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.tableSample(method.apply(valueOperator, value));
        }
        return (TR) this;
    }

    @Override
    public final FC repeatable(Expression seed) {
        this.getFromCrossBlock().onSeed((ArmyExpression) seed);
        return (FC) this;
    }

    @Override
    public final FC repeatable(Supplier<Expression> supplier) {
        return this.repeatable(supplier.get());
    }

    @Override
    public final FC repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
        return this.repeatable(valueOperator.apply(seedValue));
    }

    @Override
    public final <E extends Number> FC repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.repeatable(valueOperator.apply(supplier.get()));
    }

    @Override
    public final FC repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                               String keyName) {
        return this.repeatable(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final FC ifRepeatable(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.repeatable(expression);
        }
        return (FC) this;
    }

    @Override
    public final <E extends Number> FC ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final FC ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                 String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.repeatable(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final _NestedLeftParenSpec<FC> from() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<FC> crossJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> leftJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> join() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> rightJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> fullJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
    }

    @Override
    public final FC ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC parens(String first, String... rest) {
        this.getFromDerived().onColumnAlias(_ArrayUtils.unmodifiableListOf(first, rest));
        return (FC) this;
    }

    @Override
    public final FC parens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().onColumnAlias(CriteriaUtils.stringList(this.context, true, consumer));
        return (FC) this;
    }

    @Override
    public final FC ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().onColumnAlias(CriteriaUtils.stringList(this.context, false, consumer));
        return (FC) this;
    }


    @Override
    public final WR whereCurrentOf(String cursorName) {
        this.where(new PostgreCursorPredicate(cursorName));
        return (WR) this;
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
        return this.modifier;
    }

    @Override
    public final TableMeta<?> table() {
        return this.updateTable;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    @Override
    final Dialect statementDialect() {
        return PostgreDialect.POSTGRE15;
    }


    @Override
    final void onStatementEnd() {
        final Object thisStmt = this;
        if (thisStmt instanceof PostgreUpdates.PrimaryBatchUpdate && ((PrimaryBatchUpdate<?>) thisStmt).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }

    @Override
    final void onClear() {
        final Object thisStmt = this;
        if (thisStmt instanceof PostgreUpdates.PrimaryBatchUpdate) {
            ((PrimaryBatchUpdate<?>) thisStmt).paramList = null;
        }
    }

    @Override
    final Query.TableModifier tableModifier(@Nullable Query.TableModifier modifier) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return modifier;
    }

    @Override
    final Query.DerivedModifier derivedModifier(@Nullable Query.DerivedModifier modifier) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return modifier;
    }

    @Override
    final FT onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final PostgreSupports.PostgreNoOnTableBlock block;
        block = new PostgreSupports.PostgreNoOnTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FT) this;
    }

    @Override
    final FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    final FC fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return (FC) this;
    }


    final _OnClause<FC> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {

        final OnClauseTableBlock<FC> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", (FC) this);
        this.blockConsumer.accept(block);
        return block;
    }


    private PostgreSupports.PostgreNoOnTableBlock getFromCrossBlock() {
        final _TableBlock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof PostgreSupports.PostgreNoOnTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (PostgreSupports.PostgreNoOnTableBlock) block;
    }


    final TableBlock.ParensDerivedJoinBlock getFromDerived() {
        final _TableBlock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof TableBlock.ParensDerivedJoinBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TableBlock.ParensDerivedJoinBlock) block;
    }


    private static abstract class SimpleUpdate<I extends Item, Q extends Item, T>
            extends PostgreUpdates<
            I,
            T,
            PostgreUpdate._SingleSetFromSpec<I, Q, T>,
            PostgreUpdate._TableSampleJoinSpec<I, Q>,
            Statement._AsClause<PostgreUpdate._ParensJoinSpec<I, Q>>,
            PostgreUpdate._SingleJoinSpec<I, Q>,
            PostgreUpdate._TableSampleOnSpec<I, Q>,
            Statement._AsParensOnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
            Statement._OnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
            PostgreUpdate._RepeatableJoinClause<I, Q>,
            PostgreUpdate._ReturningSpec<I, Q>,
            PostgreUpdate._SingleWhereAndSpec<I, Q>>
            implements PostgreUpdate._SingleSetFromSpec<I, Q, T>,
            PostgreUpdate._TableSampleJoinSpec<I, Q>,
            PostgreUpdate._RepeatableJoinClause<I, Q>,
            PostgreUpdate._ParensJoinSpec<I, Q>,
            PostgreUpdate._SingleWhereAndSpec<I, Q>,
            PostgreUpdate._StaticReturningCommaSpec<Q> {

        private List<Selection> returningList;

        private SimpleUpdate(PostgreUpdateClause<?> clause) {
            super(clause);

        }

        @Override
        public final _SingleFromSpec<I, Q> sets(Consumer<RowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.rowPairs(this::onAddItemPair));
            return this;
        }


        @Override
        public final _DqlUpdateSpec<Q> returningAll() {
            this.returningList = PostgreSupports.EMPTY_SELECTION_LIST;
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
                    .add(selection2);
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
                    .add(function2.apply(alias2));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                            Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add(selection);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Selection selection, Function<String, Selection> function,
                                                            String alias) {
            this.onAddSelection(selection)
                    .add(function.apply(alias));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3,
                                                            TableField field4) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            list.add(field4);
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
                    .add(selection2);
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
                    .add(function2.apply(alias2));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias,
                                                        Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add(selection);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Selection selection, Function<String, Selection> function,
                                                        String alias) {
            this.onAddSelection(selection)
                    .add(function.apply(alias));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3,
                                                        TableField field4) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            list.add(field4);
            return this;
        }


        @Override
        public final List<? extends Selection> returningList() {
            // use wrapper,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public final Q asReturningUpdate() {
            final List<Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endUpdateStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return this.onAsReturningUpdate();
        }

        @Override
        final I onAsUpdate() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.onAsPostgreUpdate();
        }

        abstract Q onAsReturningUpdate();

        abstract I onAsPostgreUpdate();


        @Override
        final _AsClause<_ParensJoinSpec<I, Q>> onFromDerived(_JoinType joinType,
                                                             @Nullable Query.DerivedModifier modifier,
                                                             DerivedTable table) {
            return alias -> {
                final TableBlock.ParensDerivedJoinBlock block;
                block = new TableBlock.ParensDerivedJoinBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }

        @Override
        final _TableSampleOnSpec<I, Q> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                   TableMeta<?> table, String alias) {
            final SimpleTableBlock<I, Q> block;
            block = new SimpleTableBlock<>(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }


        @Override
        final _AsParensOnClause<_SingleJoinSpec<I, Q>> onJoinDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                                     DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_SingleJoinSpec<I, Q>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                         CteItem cteItem, String alias) {
            final OnClauseTableBlock<_SingleJoinSpec<I, Q>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        private List<Selection> onAddSelection(Selection selection) {
            List<Selection> list = this.returningList;
            if (list == null) {
                list = new ArrayList<>();
                this.returningList = list;
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add(selection);
            return list;
        }

    }//SimpleUpdate

    private static final class PrimarySimpleUpdate<T>
            extends SimpleUpdate<Update, ReturningUpdate, T>
            implements Update {

        private PrimarySimpleUpdate(PrimarySimpleUpdateClause clause) {
            super(clause);
        }

        @Override
        Update onAsPostgreUpdate() {
            return this;
        }

        @Override
        ReturningUpdate onAsReturningUpdate() {
            return new ReturningUpdateWrapper(this);
        }

    }//PrimarySimpleUpdate

    private static final class PrimarySimpleUpdateForMultiStmt<I extends Item, T>
            extends SimpleUpdate<I, I, T>
            implements Update {

        private final Function<PrimaryStatement, I> function;

        /**
         * @see #simple(MultiStmtSpec, Function)
         */
        private PrimarySimpleUpdateForMultiStmt(PrimarySimpleUpdateClauseForMultiStmt<I> clause) {
            super(clause);
            this.function = clause.function;
        }

        @Override
        I onAsReturningUpdate() {
            return this.function.apply(this);
        }

        @Override
        I onAsPostgreUpdate() {
            return this.function.apply(new ReturningUpdateWrapper(this));
        }


    }//PrimarySimpleUpdateForMultiStmt

    private static final class SubSimpleUpdate<I extends Item, T>
            extends SimpleUpdate<I, I, T>
            implements SubStatement {

        private final Function<SubStatement, I> function;

        private SubSimpleUpdate(SubSimpleUpdateClause<I> clause) {
            super(clause);
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

    }//SubSimpleUpdate


    private static final class PrimaryBatchUpdate<T>
            extends PostgreUpdates<
            BatchUpdate,
            T,
            PostgreUpdate._BatchSingleSetFromSpec<BatchUpdate, BatchReturningUpdate, T>,
            PostgreUpdate._BatchTableSampleJoinSpec<BatchUpdate, BatchReturningUpdate>,
            Statement._AsClause<PostgreUpdate._BatchParensJoinSpec<BatchUpdate, BatchReturningUpdate>>,
            PostgreUpdate._BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>,
            PostgreUpdate._BatchTableSampleOnSpec<BatchUpdate, BatchReturningUpdate>,
            Statement._AsParensOnClause<PostgreUpdate._BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>>,
            Statement._OnClause<PostgreUpdate._BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>>,
            PostgreUpdate._BatchRepeatableJoinClause<BatchUpdate, BatchReturningUpdate>,
            PostgreUpdate._BatchReturningSpec<BatchUpdate, BatchReturningUpdate>,
            PostgreUpdate._BatchSingleWhereAndSpec<BatchUpdate, BatchReturningUpdate>>
            implements _BatchSingleSetFromSpec<BatchUpdate, BatchReturningUpdate, T>,
            PostgreUpdate._BatchTableSampleJoinSpec<BatchUpdate, BatchReturningUpdate>,
            PostgreUpdate._BatchRepeatableJoinClause<BatchUpdate, BatchReturningUpdate>,
            PostgreUpdate._BatchParensJoinSpec<BatchUpdate, BatchReturningUpdate>,
            PostgreUpdate._BatchSingleWhereAndSpec<BatchUpdate, BatchReturningUpdate>,
            _DqlUpdateSpec<BatchReturningUpdate>,
            BatchUpdate,
            _BatchDml {


        private List<Selection> returningList;
        private List<?> paramList;

        private PrimaryBatchUpdate(BatchUpdateClause clause) {
            super(clause);

        }

        @Override
        public _BatchSingleFromClause<BatchUpdate, BatchReturningUpdate> sets(Consumer<BatchRowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.batchRowPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _BatchParamClause<_DqlUpdateSpec<BatchReturningUpdate>> returningAll() {
            this.returningList = PostgreSupports.EMPTY_SELECTION_LIST;
            return new BatchParamClause(this);
        }

        @Override
        public _BatchParamClause<_DqlUpdateSpec<BatchReturningUpdate>> returning(Consumer<Returnings> consumer) {
            this.returningList = CriteriaUtils.selectionList(this.context, consumer);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(Selection selection) {
            this.onAddSelection(selection);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(Selection selection1, Selection selection2) {
            this.onAddSelection(selection1)
                    .add(selection2);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(Function<String, Selection> function, String alias) {
            this.onAddSelection(function.apply(alias));
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(Function<String, Selection> function1, String alias1,
                                                                              Function<String, Selection> function2, String alias2) {
            this.onAddSelection(function1.apply(alias1))
                    .add(function2.apply(alias2));
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(Function<String, Selection> function, String alias,
                                                                              Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add(selection);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(Selection selection, Function<String, Selection> function,
                                                                              String alias) {
            this.onAddSelection(selection)
                    .add(function.apply(alias));
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(TableField field1, TableField field2, TableField field3) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> returning(TableField field1, TableField field2, TableField field3,
                                                                              TableField field4) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            list.add(field4);
            return new BatchParamClause(this);
        }

        @Override
        public List<Selection> returningList() {
            //use wrapper,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<BatchUpdate> paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public BatchReturningUpdate asReturningUpdate() {
            final List<Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList) || this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endUpdateStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return new BatchReturningUpdateWrapper(this);
        }

        @Override
        final BatchUpdate onAsUpdate() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this;
        }


        @Override
        _AsClause<_BatchParensJoinSpec<BatchUpdate, BatchReturningUpdate>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                                                         DerivedTable table) {
            return alias -> {
                final TableBlock.ParensDerivedJoinBlock block;
                block = new TableBlock.ParensDerivedJoinBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }


        @Override
        _BatchTableSampleOnSpec<BatchUpdate, BatchReturningUpdate> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                                               TableMeta<?> table, String alias) {
            final BatchTableBlock<BatchUpdate, BatchReturningUpdate> block;
            block = new BatchTableBlock<>(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>> onJoinDerived(_JoinType joinType,
                                                                                                 @Nullable Query.DerivedModifier modifier,
                                                                                                 DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                                                     CteItem cteItem, String alias) {
            final OnClauseTableBlock<_BatchSingleJoinSpec<BatchUpdate, BatchReturningUpdate>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }


        private List<Selection> onAddSelection(Selection selection) {
            List<Selection> list = this.returningList;
            if (list == null) {
                list = new ArrayList<>();
                this.returningList = list;
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add(selection);
            return list;
        }

    }//PrimaryBatchUpdate


    private static final class BatchParamClause
            implements _BatchStaticReturningCommaSpec<BatchReturningUpdate> {

        private final PrimaryBatchUpdate<?> statement;

        private BatchParamClause(PrimaryBatchUpdate<?> statement) {
            this.statement = statement;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(Selection selection) {
            this.statement.onAddSelection(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(Selection selection1, Selection selection2) {
            this.statement.onAddSelection(selection1)
                    .add(selection2);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(Function<String, Selection> function, String alias) {
            this.statement.onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(Function<String, Selection> function1, String alias1,
                                                                          Function<String, Selection> function2, String alias2) {
            this.statement.onAddSelection(function1.apply(alias1))
                    .add(function2.apply(alias2));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(Function<String, Selection> function, String alias,
                                                                          Selection selection) {
            this.statement.onAddSelection(function.apply(alias))
                    .add(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(Selection selection, Function<String, Selection> function,
                                                                          String alias) {
            this.statement.onAddSelection(selection)
                    .add(function.apply(alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(TableField field1, TableField field2, TableField field3) {
            final List<Selection> list;
            list = this.statement.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningUpdate> comma(TableField field1, TableField field2, TableField field3,
                                                                          TableField field4) {
            final List<Selection> list;
            list = this.statement.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            list.add(field4);
            return this;
        }

        @Override
        public <P> _DqlUpdateSpec<BatchReturningUpdate> paramList(List<P> paramList) {
            this.statement.paramList(paramList);
            return this.statement;
        }

        @Override
        public <P> _DqlUpdateSpec<BatchReturningUpdate> paramList(Supplier<List<P>> supplier) {
            this.statement.paramList(supplier.get());
            return this.statement;
        }

        @Override
        public _DqlUpdateSpec<BatchReturningUpdate> paramList(Function<String, ?> function, String keyName) {
            this.statement.paramList((List<?>) function.apply(keyName));
            return this.statement;
        }

    }//BatchParamClause


    private static abstract class PostgreUpdateClause<WE>
            extends CriteriaSupports.WithClause<PostgreCtes, WE> {

        SQLsSyntax.WordOnly modifier;

        TableMeta<?> updateTable;

        String tableAlias;


        private PostgreUpdateClause(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
            super(spec, context);
            ContextStack.push(this.context);
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PostgreUpdateClause


    private static abstract class SimpleUpdateClause<I extends Item, Q extends Item>
            extends PostgreUpdateClause<PostgreUpdate._SingleUpdateClause<I, Q>>
            implements PostgreUpdate._SingleWithSpec<I, Q> {

        private SimpleUpdateClause(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
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


    }//SimpleUpdateClause

    private static final class PrimarySimpleUpdateClause
            extends SimpleUpdateClause<Update, ReturningUpdate> {


        private PrimarySimpleUpdateClause() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(null));

        }

        @Override
        public <T> _SingleSetClause<Update, ReturningUpdate, T> update(TableMeta<T> table, SQLs.WordAs as,
                                                                       String tableAlias) {
            this.modifier = null;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleSetClause<Update, ReturningUpdate, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table,
                                                                       SQLs.WordAs as, String tableAlias) {
            this.modifier = only;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdate<>(this);
        }

    }//PrimarySimpleUpdateClause

    private static final class PrimarySimpleUpdateClauseForMultiStmt<I extends Item>
            extends SimpleUpdateClause<I, I> {

        private final Function<PrimaryStatement, I> function;

        private PrimarySimpleUpdateClauseForMultiStmt(MultiStmtSpec spec, Function<PrimaryStatement, I> function) {
            super(spec, CriteriaContexts.primaryJoinableSingleDmlContext(spec));
            this.function = function;
        }

        @Override
        public <T> _SingleSetClause<I, I, T> update(TableMeta<T> table, SQLsSyntax.WordAs as, String tableAlias) {
            this.modifier = null;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdateForMultiStmt<>(this);
        }

        @Override
        public <T> _SingleSetClause<I, I, T> update(@Nullable SQLsSyntax.WordOnly wordOnly, TableMeta<T> table,
                                                    SQLs.WordAs as, String tableAlias) {
            this.modifier = wordOnly;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdateForMultiStmt<>(this);
        }


    }//PrimarySimpleUpdateClauseForMultiStmt

    private static final class SubSimpleUpdateClause<I extends Item>
            extends SimpleUpdateClause<I, I> {

        private final Function<SubStatement, I> function;

        private SubSimpleUpdateClause(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, CriteriaContexts.subJoinableSingleDmlContext(outerContext));
            this.function = function;
        }

        @Override
        public <T> _SingleSetClause<I, I, T> update(TableMeta<T> table, SQLsSyntax.WordAs as, String tableAlias) {
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new SubSimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleSetClause<I, I, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table, SQLs.WordAs as,
                                                    String tableAlias) {
            if (only != null && only != SQLs.ONLY) {
                throw CriteriaUtils.errorModifier(this.context, only);
            }
            this.modifier = only;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new SubSimpleUpdate<>(this);
        }

    } //SubSimpleUpdateClause


    private static final class BatchUpdateClause
            extends PostgreUpdateClause<PostgreUpdate._BatchSingleUpdateClause<BatchUpdate, BatchReturningUpdate>>
            implements PostgreUpdate._BatchSingleWithSpec<BatchUpdate, BatchReturningUpdate> {

        private BatchUpdateClause() {
            super(null, CriteriaContexts.subJoinableSingleDmlContext(null));
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleUpdateClause<BatchUpdate, BatchReturningUpdate>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleUpdateClause<BatchUpdate, BatchReturningUpdate>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> _BatchSingleSetClause<BatchUpdate, BatchReturningUpdate, T> update(TableMeta<T> table, SQLs.WordAs as, String tableAlias) {
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimaryBatchUpdate<>(this);
        }

        @Override
        public <T> _BatchSingleSetClause<BatchUpdate, BatchReturningUpdate, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table,
                                                                                      SQLs.WordAs as, String tableAlias) {
            this.modifier = only;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimaryBatchUpdate<>(this);
        }


    }//BatchUpdateClause


    private static final class SimpleTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            _RepeatableOnClause<I, Q>,
            _OnClause<_SingleJoinSpec<I, Q>>,
            _SingleJoinSpec<I, Q>>
            implements _TableSampleOnSpec<I, Q>, _RepeatableOnClause<I, Q> {

        private SimpleTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias,
                                 _SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, table, alias, stmt);
        }


    }//SimpleTableBlock

    private static final class BatchTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            _BatchRepeatableOnClause<I, Q>,
            _OnClause<_BatchSingleJoinSpec<I, Q>>,
            _BatchSingleJoinSpec<I, Q>>
            implements _BatchTableSampleOnSpec<I, Q>, _BatchRepeatableOnClause<I, Q> {

        private BatchTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table, String alias,
                                _BatchSingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, table, alias, stmt);
        }


    }//BatchTableBlock


    private static abstract class PostgreReturningUpdateWrapper extends CriteriaSupports.StatementMockSupport
            implements PostgreUpdate, _PostgreUpdate, _ReturningDml {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final SQLsSyntax.WordOnly only;

        private final TableMeta<?> targetTable;

        private final String tableAlias;

        private final List<_ItemPair> itemPairList;

        private final List<_TableBlock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<? extends Selection> returningList;

        private Boolean prepared = Boolean.TRUE;

        private PostgreReturningUpdateWrapper(PostgreUpdates<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> stmt) {
            super(stmt.context);
            this.recursive = stmt.recursive;
            this.cteList = stmt.cteList;
            this.only = stmt.modifier;
            this.targetTable = stmt.updateTable;

            this.tableAlias = stmt.tableAlias;
            this.itemPairList = stmt.itemPairList();
            this.tableBlockList = stmt.tableBlockList();
            this.wherePredicateList = stmt.wherePredicateList();

            this.returningList = stmt.returningList();
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
            return PostgreDialect.POSTGRE15;
        }

        @Override
        public final TableMeta<?> table() {
            return this.targetTable;
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
        public final List<_TableBlock> tableBlockList() {
            return this.tableBlockList;
        }

        @Override
        public final List<_Predicate> wherePredicateList() {
            return this.wherePredicateList;
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
        public final List<? extends Selection> returningList() {
            return this.returningList;
        }


    }//PostgreUpdateWrapper


    private static final class ReturningUpdateWrapper extends PostgreReturningUpdateWrapper
            implements ReturningUpdate {

        private ReturningUpdateWrapper(PrimarySimpleUpdate<?> stmt) {
            super(stmt);
        }

        private ReturningUpdateWrapper(PrimarySimpleUpdateForMultiStmt<?, ?> stmt) {
            super(stmt);
        }

    }//ReturningUpdateWrapper

    private static final class BatchReturningUpdateWrapper extends PostgreReturningUpdateWrapper
            implements BatchReturningUpdate, _BatchDml {

        private final List<?> paramList;

        private BatchReturningUpdateWrapper(PrimaryBatchUpdate<?> stmt) {
            super(stmt);
            this.paramList = stmt.paramList;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    }//BatchReturningUpdateWrapper


}
