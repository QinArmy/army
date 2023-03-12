package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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


    static <I extends Item, Q extends Item> PostgreUpdate._SingleWithSpec<I, Q> simple(
            @Nullable _Statement._WithClauseSpec spec, Function<UpdateStatement, I> dmlFunction,
            Function<ReturningUpdate, Q> dqlFunction) {
        return new PrimarySimpleUpdateClause<>(spec, dmlFunction, dqlFunction);
    }

    static <I extends Item> PostgreUpdate._SingleWithSpec<I, I> subSimple(CriteriaContext outerContext,
                                                                          Function<SubStatement, I> function) {
        return new SubSimpleUpdateClause<>(outerContext, function);
    }

    static <I extends Item, Q extends Item> PostgreUpdate._BatchSingleWithSpec<I, Q> batch(
            Function<UpdateStatement, I> dmlFunction, Function<ReturningUpdate, Q> dqlFunction) {
        return new BatchUpdateClause<>(dmlFunction, dqlFunction);
    }


    private final SQLs.WordOnly modifier;

    private final TableMeta<?> updateTable;

    private final String tableAlias;

    _TableBlock fromCrossBlock;

    private PostgreUpdates(PostgreUpdateClause<?> clause) {
        super(clause.context);
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
        if (this instanceof BatchUpdate && ((BatchUpdate<?, ?, ?>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }

    @Override
    final void onClear() {
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<?, ?, ?>) this).paramList = null;
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
            _AsClause<PostgreUpdate._ParensJoinSpec<I, Q>>,
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
            this.returningList = PostgreSupports.RETURNING_ALL;
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

    private static final class PrimarySimpleUpdate<I extends Item, Q extends Item, T>
            extends SimpleUpdate<I, Q, T>
            implements UpdateStatement, ReturningUpdate {

        private final Function<UpdateStatement, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private PrimarySimpleUpdate(PrimarySimpleUpdateClause<I, Q> clause) {
            super(clause);
            this.dmlFunction = clause.dmlFunction;
            this.dqlFunction = clause.dqlFunction;
        }

        @Override
        I onAsPostgreUpdate() {
            return this.dmlFunction.apply(this);
        }

        @Override
        Q onAsReturningUpdate() {
            return this.dqlFunction.apply(this);
        }

    }//PrimarySimpleUpdate

    private static final class PrimarySimpleUpdateForMultiStmt<I extends Item, T>
            extends SimpleUpdate<I, I, T>
            implements UpdateStatement, ReturningUpdate {

        private final Function<PrimaryStatement, I> function;

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
            return this.function.apply(this);
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


    private static final class BatchUpdate<I extends Item, Q extends Item, T>
            extends PostgreUpdates<
            I,
            T,
            PostgreUpdate._BatchSingleSetFromSpec<I, Q, T>,
            PostgreUpdate._BatchTableSampleJoinSpec<I, Q>,
            Statement._AsClause<PostgreUpdate._BatchParensJoinSpec<I, Q>>,
            PostgreUpdate._BatchSingleJoinSpec<I, Q>,
            PostgreUpdate._BatchTableSampleOnSpec<I, Q>,
            Statement._AsParensOnClause<PostgreUpdate._BatchSingleJoinSpec<I, Q>>,
            Statement._OnClause<PostgreUpdate._BatchSingleJoinSpec<I, Q>>,
            PostgreUpdate._BatchRepeatableJoinClause<I, Q>,
            PostgreUpdate._BatchSingleJoinSpec<I, Q>,
            PostgreUpdate._BatchReturningSpec<I, Q>,
            PostgreUpdate._BatchSingleWhereAndSpec<I, Q>>
            implements _BatchSingleSetFromSpec<I, Q, T>,
            PostgreUpdate._BatchTableSampleJoinSpec<I, Q>,
            PostgreUpdate._BatchRepeatableJoinClause<I, Q>,
            PostgreUpdate._BatchParensJoinSpec<I, Q>,
            PostgreUpdate._BatchSingleWhereAndSpec<I, Q>,
            _DqlUpdateSpec<Q>,
            UpdateStatement,
            ReturningUpdate,
            _BatchDml {

        private final Function<UpdateStatement, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private _TableBlock fromCrossBlock;

        private List<Selection> returningList;
        private List<?> paramList;

        private BatchUpdate(BatchUpdateClause<I, Q> clause) {
            super(clause);
            this.dmlFunction = clause.dmlFunction;
            this.dqlFunction = clause.dqlFunction;

        }

        @Override
        public _BatchSingleFromClause<I, Q> sets(Consumer<BatchRowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.batchRowPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _NestedLeftParenSpec<_BatchSingleJoinSpec<I, Q>> from() {
            return PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
        }

        @Override
        public _NestedLeftParenSpec<_BatchSingleJoinSpec<I, Q>> crossJoin() {
            return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
        }

        @Override
        public _NestedLeftParenSpec<_OnClause<_BatchSingleJoinSpec<I, Q>>> leftJoin() {
            return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
        }

        @Override
        public _NestedLeftParenSpec<_OnClause<_BatchSingleJoinSpec<I, Q>>> join() {
            return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
        }

        @Override
        public _NestedLeftParenSpec<_OnClause<_BatchSingleJoinSpec<I, Q>>> rightJoin() {
            return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public _NestedLeftParenSpec<_OnClause<_BatchSingleJoinSpec<I, Q>>> fullJoin() {
            return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> ifLeftJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> ifJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> ifRightJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> ifFullJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
            consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> parens(String first, String... rest) {
            this.lastDerivedBlock().onColumnAlias(_ArrayUtils.unmodifiableListOf(first, rest));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> parens(Consumer<Consumer<String>> consumer) {
            this.lastDerivedBlock().onColumnAlias(CriteriaUtils.stringList(this.context, true, consumer));
            return this;
        }

        @Override
        public _BatchSingleJoinSpec<I, Q> ifParens(Consumer<Consumer<String>> consumer) {
            this.lastDerivedBlock().onColumnAlias(CriteriaUtils.stringList(this.context, false, consumer));
            return this;
        }

        @Override
        public _BatchReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public _BatchParamClause<_DqlUpdateSpec<Q>> returningAll() {
            this.returningList = PostgreSupports.RETURNING_ALL;
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchParamClause<_DqlUpdateSpec<Q>> returning(Consumer<Returnings> consumer) {
            this.returningList = CriteriaUtils.selectionList(this.context, consumer);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression expression) {
            this.onAddSelection(expression);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.onAddSelection(ArmySelections.forExp(expression, alias));
            return new BatchParamClause<>(this);
        }

        @Override
        public _AsClause<_BatchStaticReturningCommaSpec<Q>> returning(Supplier<Expression> supplier) {
            return alias -> {
                this.onAddSelection(ArmySelections.forExp(supplier.get(), alias));
                return new BatchParamClause<>(this);
            };
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            this.onAddSelection(exp1)
                    .add(exp2);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2,
                                                           NamedExpression exp3) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2,
                                                           NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
            return new BatchParamClause<>(this);
        }

        @Override
        public <P> _DmlUpdateSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<I> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<I> paramList(Function<String, ?> function, String keyName) {
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
        public Q asReturningUpdate() {
            final List<Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endUpdateStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return this.dqlFunction.apply(this);
        }

        @Override
        _BatchTableSampleJoinSpec<I, Q> onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                    TableMeta<?> table, String alias) {
            final PostgreSupports.PostgreNoOnTableBlock block;
            block = new PostgreSupports.PostgreNoOnTableBlock(joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        }

        @Override
        _AsClause<_BatchParensJoinSpec<I, Q>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
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
        _BatchSingleJoinSpec<I, Q> onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                             CteItem cteItem, String alias) {
            final TableBlock.NoOnModifierTableBlock block;
            block = new TableBlock.NoOnModifierTableBlock(joinType, modifier, cteItem, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        }

        @Override
        _BatchTableSampleOnSpec<I, Q> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                  TableMeta<?> table, String alias) {
            final BatchTableBlock<I, Q> block;
            block = new BatchTableBlock<>(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchSingleJoinSpec<I, Q>> onJoinDerived(_JoinType joinType,
                                                                    @Nullable Query.DerivedModifier modifier,
                                                                    DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_BatchSingleJoinSpec<I, Q>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<I, Q>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                        CteItem cteItem, String alias) {
            final OnClauseTableBlock<_BatchSingleJoinSpec<I, Q>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        I onAsUpdate() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.dmlFunction.apply(this);
        }

        @Override
        _TableBlock lastBlock() {
            return this.fromCrossBlock;
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

        private _OnClause<_BatchSingleJoinSpec<I, Q>> joinNestedEnd(final _JoinType joinType, final NestedItems items) {
            joinType.assertStandardJoinType();
            final OnClauseTableBlock<_BatchSingleJoinSpec<I, Q>> block;
            block = new OnClauseTableBlock<>(joinType, items, "", this);
            this.blockConsumer.accept(block);
            return block;
        }

    }//BatchUpdate


    private static final class BatchParamClause<Q extends Item>
            implements _BatchStaticReturningCommaSpec<Q> {

        private final BatchUpdate<?, Q, ?> statement;

        private BatchParamClause(BatchUpdate<?, Q, ?> statement) {
            this.statement = statement;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(NamedExpression exp) {
            this.statement.onAddSelection(exp);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.statement.onAddSelection(ArmySelections.forExp(expression, alias));
            return this;
        }

        @Override
        public _AsClause<_BatchStaticReturningCommaSpec<Q>> comma(Supplier<Expression> supplier) {

            return alias -> {
                this.statement.onAddSelection(ArmySelections.forExp(supplier.get(), alias));
                return this;
            };
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2) {
            this.statement.onAddSelection(exp1)
                    .add(exp2);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            final List<Selection> list;
            list = this.statement.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.statement.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
            return this;
        }

        @Override
        public <P> _DqlUpdateSpec<Q> paramList(List<P> paramList) {
            this.statement.paramList(paramList);
            return this.statement;
        }

        @Override
        public <P> _DqlUpdateSpec<Q> paramList(Supplier<List<P>> supplier) {
            this.statement.paramList(supplier.get());
            return this.statement;
        }

        @Override
        public _DqlUpdateSpec<Q> paramList(Function<String, ?> function, String keyName) {
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

    private static final class PrimarySimpleUpdateClause<I extends Item, Q extends Item>
            extends SimpleUpdateClause<I, Q> {

        private final Function<UpdateStatement, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private PrimarySimpleUpdateClause(@Nullable _Statement._WithClauseSpec spec,
                                          Function<UpdateStatement, I> dmlFunction,
                                          Function<ReturningUpdate, Q> dqlFunction) {
            super(spec, CriteriaContexts.joinableSingleDmlContext(null));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs as, String tableAlias) {
            this.modifier = null;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table, SQLs.WordAs as,
                                                    String tableAlias) {
            this.modifier = only;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdate<>(this);
        }

    }//PrimarySimpleUpdateClause

    private static final class PrimarySimpleUpdateClauseForMultiStmt<I extends Item>
            extends SimpleUpdateClause<I, I> {

        private final Function<PrimaryStatement, I> function;

        private PrimarySimpleUpdateClauseForMultiStmt(_WithClauseSpec spec, Function<PrimaryStatement, I> function) {
            super(spec, CriteriaContexts.joinableSingleDmlContext(null));
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
            super(null, CriteriaContexts.joinableSingleDmlContext(outerContext));
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


    private static final class BatchUpdateClause<I extends Item, Q extends Item>
            extends PostgreUpdateClause<PostgreUpdate._BatchSingleUpdateClause<I, Q>>
            implements PostgreUpdate._BatchSingleWithSpec<I, Q> {

        private final Function<UpdateStatement, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private BatchUpdateClause(Function<UpdateStatement, I> dmlFunction, Function<ReturningUpdate, Q> dqlFunction) {
            super(null, CriteriaContexts.joinableSingleDmlContext(null));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleUpdateClause<I, Q>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleUpdateClause<I, Q>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> _BatchSingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }

        @Override
        public <T> _BatchSingleSetClause<I, Q, T> update(@Nullable SQLs.WordOnly only, TableMeta<T> table, SQLs.WordAs as,
                                                         String tableAlias) {
            if (only != null && only != SQLs.ONLY) {
                throw CriteriaUtils.errorModifier(this.context, only);
            }
            this.modifier = only;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
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


}
