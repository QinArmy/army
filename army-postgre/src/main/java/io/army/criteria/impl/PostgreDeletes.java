package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchReturningDelete;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreDelete;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
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
 * This class is the implementation of Postgre DELETE syntax.
 * </p>
 *
 * @see PostgreDelete
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class PostgreDeletes<I extends Item, WE, DR, FT, FS, FC extends Item, JT, JS, JC extends Item, TR, WR, WA>
        extends JoinableDelete.WithJoinableDelete<I, PostgreCtes, WE, FT, FS, FC, JT, JS, JC, WR, WA>
        implements PostgreDelete,
        _PostgreDelete,
        PostgreStatement._StaticTableSampleClause<TR>,
        PostgreStatement._RepeatableClause<FC>,
        Statement._ParensStringClause<FC>,
        Statement._UsingNestedClause<PostgreQuery._NestedLeftParenSpec<FC>>,
        PostgreStatement._PostgreJoinNestedClause<Statement._OnClause<FC>>,
        PostgreStatement._PostgreCrossNestedClause<FC>,
        PostgreStatement._PostgreDynamicJoinCrossClause<FC>,
        PostgreDelete._PostgreDeleteClause<DR> {


    static <I extends Item, Q extends Item> _SingleWithSpec<I, Q> simple(
            Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
        return new PrimarySimpleDelete<>(dmlFunction, dqlFunction);
    }

    static <I extends Item, Q extends Item> _BatchSingleWithSpec<I, Q> batch(
            Function<BatchDelete, I> dmlFunction, Function<BatchReturningDelete, Q> dqlFunction) {
        return new PostgreBatchDelete<>(dmlFunction, dqlFunction);
    }


    static <I extends Item> _SingleWithSpec<I, I> dynamicCteDelete(CriteriaContext outerContext,
                                                                   Function<SubStatement, I> function) {
        return new SubSimpleDelete<>(outerContext, function);
    }


    static <I extends Item> _SingleWithSpec<I, I> singleForMultiStmt(_WithClauseSpec withSpec,
                                                                     Function<PrimaryStatement, I> function) {
        return new PrimarySimpleDeleteForMultiStmt<>(withSpec, function);
    }


    private SQLsSyntax.WordOnly modifier;

    private TableMeta<?> updateTable;

    private String tableAlias;

    _TableBlock fromCrossBlock;

    private PostgreDeletes(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }

    @Override
    public final DR delete(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        if (this.updateTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.modifier = null;
        this.updateTable = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    public final DR delete(SQLs.WordOnly only, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        if (this.updateTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (only != SQLs.ONLY) {
            throw CriteriaUtils.errorModifier(this.context, only);
        }
        this.modifier = only;
        this.updateTable = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    public final TR tableSample(Expression method) {
        this.getFromCrossBlock().onSampleMethod((ArmyExpression) method);
        return (TR) this;
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
    public final TR tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                                BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
        return this.tableSample(method.apply(valueOperator, argument));
    }

    @Override
    public final TR ifTableSample(Supplier<Expression> supplier) {
        final Expression value;
        value = supplier.get();
        if (value != null) {
            this.tableSample(value);
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
                                  BiFunction<MappingType, Object, Expression> valueOperator,
                                  Function<String, ?> function, String keyName) {
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
    public final _NestedLeftParenSpec<FC> using() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedUsingEnd);
    }


    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> leftJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> join() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> rightJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> fullJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<FC> crossJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedUsingEnd);
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
    final PostgreCtes createCteBuilder(boolean recursive) {
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
    }


    @Override
    final Dialect statementDialect() {
        return PostgreDialect.POSTGRE15;
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

    final FC nestedUsingEnd(final _JoinType joinType, final NestedItems nestedItems) {
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return (FC) this;
    }

    final _OnClause<FC> nestedJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {

        final OnClauseTableBlock<FC> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", (FC) this);
        this.blockConsumer.accept(block);
        return block;
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
    final void onEndStatement() {
        this.fromCrossBlock = null;

    }

    @Override
    final void onClear() {

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


    private static abstract class SimpleDelete<I extends Item, Q extends Item, S extends Statement>
            extends PostgreDeletes<
            I,
            PostgreDelete._SingleDeleteClause<I, Q>,
            PostgreDelete._SingleUsingSpec<I, Q>,
            PostgreDelete._TableSampleJoinSpec<I, Q>,
            Statement._AsClause<PostgreDelete._ParensJoinSpec<I, Q>>,
            PostgreDelete._SingleJoinSpec<I, Q>,
            PostgreDelete._TableSampleOnSpec<I, Q>,
            PostgreDelete._AsParensOnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            PostgreDelete._RepeatableJoinClause<I, Q>,
            PostgreDelete._ReturningSpec<I, Q>,
            PostgreDelete._SingleWhereAndSpec<I, Q>>
            implements PostgreDelete._SingleWithSpec<I, Q>,
            PostgreDelete._SingleUsingSpec<I, Q>,
            PostgreDelete._TableSampleJoinSpec<I, Q>,
            PostgreDelete._RepeatableJoinClause<I, Q>,
            PostgreDelete._ParensJoinSpec<I, Q>,
            PostgreDelete._SingleWhereAndSpec<I, Q>,
            PostgreDelete._StaticReturningCommaSpec<Q> {

        final Function<S, I> dmlFunction;

        private List<Selection> returningList;

        private SimpleDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context, Function<S, I> dmlFunction) {
            super(withSpec, context);
            this.dmlFunction = dmlFunction;
        }

        @Override
        public final PostgreQuery._StaticCteParensSpec<_SingleDeleteClause<I, Q>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final PostgreQuery._StaticCteParensSpec<_SingleDeleteClause<I, Q>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public final _DqlDeleteSpec<Q> returningAll() {
            this.returningList = PostgreSupports.EMPTY_SELECTION_LIST;
            return this;
        }

        @Override
        public final _DqlDeleteSpec<Q> returning(Consumer<Returnings> consumer) {
            consumer.accept(CriteriaSupports.returningBuilder(this::onAddSelection));
            if (this.returningList == null) {
                throw CriteriaUtils.returningListIsEmpty(this.context);
            }
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
        public final List<Selection> returningList() {
            final List<Selection> list = this.returningList;
            if (list == null || list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public final Q asReturningDelete() {
            final List<Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endDeleteStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return this.onAsReturningDelete();
        }

        abstract Q onAsReturningDelete();


        @Override
        final I onAsDelete() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.dmlFunction.apply((S) this);
        }


        @Override
        final _AsClause<_ParensJoinSpec<I, Q>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
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
            final SimpleOnTableBlock<I, Q> block;
            block = new SimpleOnTableBlock<>(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final _AsParensOnClause<_SingleJoinSpec<I, Q>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_SingleJoinSpec<I, Q>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            final OnClauseTableBlock<_SingleJoinSpec<I, Q>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        private List<Selection> onAddSelection(Selection selection) {
            List<Selection> list = this.returningList;
            if (list == null) {
                this.returningList = list = new ArrayList<>();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add(selection);
            return list;
        }


    }//SimpleDelete

    private static final class SimpleOnTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            PostgreDelete._RepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            PostgreDelete._SingleJoinSpec<I, Q>>
            implements PostgreDelete._TableSampleOnSpec<I, Q>,
            PostgreDelete._RepeatableOnClause<I, Q> {

        private SimpleOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> tableItem,
                                   String alias, PostgreDelete._SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//SimpleOnTableBlock

    private static final class PrimarySimpleDelete<I extends Item, Q extends Item> extends SimpleDelete<I, Q, Delete>
            implements Delete {

        private final Function<ReturningDelete, Q> dqlFunction;

        private PrimarySimpleDelete(Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
            super(null, CriteriaContexts.joinableSingleDmlContext(null), dmlFunction);
            this.dqlFunction = dqlFunction;
        }

        @Override
        Q onAsReturningDelete() {
            return this.dqlFunction.apply(new ReturningDeleteWrapper(this));
        }

    }//PrimarySimpleDelete

    private static final class PrimarySimpleDeleteForMultiStmt<I extends Item>
            extends SimpleDelete<I, I, PrimaryStatement>
            implements Delete {


        private PrimarySimpleDeleteForMultiStmt(_WithClauseSpec withSpec, Function<PrimaryStatement, I> function) {
            super(withSpec, CriteriaContexts.joinableSingleDmlContext(null), function);
        }


        @Override
        I onAsReturningDelete() {
            return this.dmlFunction.apply(new ReturningDeleteWrapper(this));
        }


    }//PrimarySimpleDeleteForMultiStmt

    private static final class SubSimpleDelete<I extends Item>
            extends SimpleDelete<I, I, SubStatement>
            implements SubStatement {


        private SubSimpleDelete(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, CriteriaContexts.joinableSingleDmlContext(outerContext), function);
        }

        @Override
        I onAsReturningDelete() {
            return this.dmlFunction.apply(this);
        }


    }//SubSimpleDelete



    /*-------------------below batch delete class  -------------------*/


    private static final class PostgreBatchDelete<I extends Item, Q extends Item>
            extends PostgreDeletes<
            I,
            PostgreDelete._BatchSingleDeleteClause<I, Q>,
            PostgreDelete._BatchSingleUsingSpec<I, Q>,
            PostgreDelete._BatchTableSampleJoinSpec<I, Q>,
            Statement._AsClause<PostgreDelete._BatchParensJoinSpec<I, Q>>,
            PostgreDelete._BatchSingleJoinSpec<I, Q>,
            PostgreDelete._BatchTableSampleOnSpec<I, Q>,
            PostgreDelete._AsParensOnClause<PostgreDelete._BatchSingleJoinSpec<I, Q>>,
            Statement._OnClause<PostgreDelete._BatchSingleJoinSpec<I, Q>>,
            PostgreDelete._BatchRepeatableJoinClause<I, Q>,
            PostgreDelete._BatchReturningSpec<I, Q>,
            PostgreDelete._BatchSingleWhereAndSpec<I, Q>>
            implements PostgreDelete._BatchSingleWithSpec<I, Q>,
            PostgreDelete._BatchSingleUsingSpec<I, Q>,
            PostgreDelete._BatchTableSampleJoinSpec<I, Q>,
            PostgreDelete._BatchRepeatableJoinClause<I, Q>,
            PostgreDelete._BatchParensJoinSpec<I, Q>,
            PostgreDelete._BatchSingleWhereAndSpec<I, Q>,
            Statement._DqlDeleteSpec<Q>,
            BatchDelete,
            _BatchDml {

        private final Function<BatchDelete, I> dmlFunction;

        private final Function<BatchReturningDelete, Q> dqlFunction;

        private List<Selection> returningList;

        private List<?> paramList;

        private PostgreBatchDelete(Function<BatchDelete, I> dmlFunction, Function<BatchReturningDelete, Q> dqlFunction) {
            super(null, CriteriaContexts.joinableSingleDmlContext(null));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleDeleteClause<I, Q>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleDeleteClause<I, Q>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _BatchReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public _BatchParamClause<_DqlDeleteSpec<Q>> returningAll() {
            this.returningList = PostgreSupports.EMPTY_SELECTION_LIST;
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchParamClause<_DqlDeleteSpec<Q>> returning(Consumer<Returnings> consumer) {
            consumer.accept(CriteriaSupports.returningBuilder(this::onAddSelection));
            if (this.returningList == null) {
                throw CriteriaUtils.returningListIsEmpty(this.context);
            }
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Selection selection) {
            this.onAddSelection(selection);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Selection selection1, Selection selection2) {
            this.onAddSelection(selection1)
                    .add(selection2);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias) {
            this.onAddSelection(function.apply(alias));
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Function<String, Selection> function1, String alias1,
                                                           Function<String, Selection> function2, String alias2) {
            this.onAddSelection(function1.apply(alias1))
                    .add(function2.apply(alias2));
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                           Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add(selection);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Selection selection, Function<String, Selection> function,
                                                           String alias) {
            this.onAddSelection(selection)
                    .add(function.apply(alias));
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3,
                                                           TableField field4) {
            final List<Selection> list;
            list = this.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            list.add(field4);
            return new BatchParamClause<>(this);
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(List<P> paramList) {
            if (this.paramList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<I> paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }


        @Override
        public List<Selection> returningList() {
            final List<Selection> list = this.returningList;
            if (list == null || list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
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
        public Q asReturningDelete() {
            final List<Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endDeleteStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return this.dqlFunction.apply(new BatchReturningDeleteWrapper(this));
        }


        @Override
        I onAsDelete() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.dmlFunction.apply(this);
        }

        @Override
        _AsClause<_BatchParensJoinSpec<I, Q>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                            DerivedTable table) {
            return super.onFromDerived(joinType, modifier, table);
        }

        @Override
        _BatchTableSampleOnSpec<I, Q> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                  TableMeta<?> table, String alias) {
            return super.onJoinTable(joinType, modifier, table, alias);
        }

        @Override
        _AsParensOnClause<_BatchSingleJoinSpec<I, Q>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return super.onJoinDerived(joinType, modifier, table);
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<I, Q>> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            return super.onJoinCte(joinType, modifier, cteItem, alias);
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


    }//BatchDelete

    private static final class BatchParamClause<Q extends Item> implements _BatchStaticReturningCommaSpec<Q> {

        private final PostgreBatchDelete<?, Q> statement;

        private BatchParamClause(PostgreBatchDelete<?, Q> statement) {
            this.statement = statement;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Selection selection) {
            this.statement.onAddSelection(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Selection selection1, Selection selection2) {
            this.statement.onAddSelection(selection1)
                    .add(selection2);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias) {
            this.statement.onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Function<String, Selection> function1, String alias1,
                                                       Function<String, Selection> function2, String alias2) {
            this.statement.onAddSelection(function1.apply(alias1))
                    .add(function2.apply(alias2));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias,
                                                       Selection selection) {
            this.statement.onAddSelection(function.apply(alias))
                    .add(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Selection selection, Function<String, Selection> function,
                                                       String alias) {
            this.statement.onAddSelection(selection)
                    .add(function.apply(alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3) {
            final List<Selection> list;
            list = this.statement.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3,
                                                       TableField field4) {
            final List<Selection> list;
            list = this.statement.onAddSelection(field1);
            list.add(field2);
            list.add(field3);
            list.add(field4);
            return this;
        }

        @Override
        public <P> _DqlDeleteSpec<Q> paramList(List<P> paramList) {
            this.statement.paramList(paramList);
            return this.statement;
        }

        @Override
        public <P> _DqlDeleteSpec<Q> paramList(Supplier<List<P>> supplier) {
            this.statement.paramList(supplier.get());
            return this.statement;
        }

        @Override
        public _DqlDeleteSpec<Q> paramList(Function<String, ?> function, String keyName) {
            this.statement.paramList((List<?>) function.apply(keyName));
            return this.statement;
        }


    }//BatchParamClause

    private static final class BatchOnTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            PostgreDelete._BatchRepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreDelete._BatchSingleJoinSpec<I, Q>>,
            PostgreDelete._BatchSingleJoinSpec<I, Q>>
            implements PostgreDelete._BatchTableSampleOnSpec<I, Q>,
            PostgreDelete._BatchRepeatableOnClause<I, Q> {

        private BatchOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                  TableMeta<?> tableItem, String alias,
                                  PostgreDelete._BatchSingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//BatchOnTableBlock


    private static abstract class PostgreDeleteWrapper extends CriteriaSupports.StatementMockSupport
            implements PostgreDelete, _PostgreDelete {

        private final boolean recursive;

        private final List<_Cte> cteList;

        private final SQLsSyntax.WordOnly only;

        private final TableMeta<?> targetTable;

        private final String tableAlias;

        private final List<_TableBlock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<Selection> returningList;

        private Boolean prepared = Boolean.TRUE;

        private PostgreDeleteWrapper(PostgreDeletes<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> stmt) {
            super(stmt.context);
            this.recursive = stmt.isRecursive();
            this.cteList = stmt.cteList();
            this.only = stmt.modifier;
            this.targetTable = stmt.updateTable;

            this.tableAlias = stmt.tableAlias;
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
        public final List<_TableBlock> tableBlockList() {
            return this.tableBlockList;
        }

        @Override
        public final List<Selection> returningList() {
            return this.returningList;
        }


    }//PostgreDeleteWrapper


    private static final class ReturningDeleteWrapper extends PostgreDeleteWrapper
            implements ReturningDelete {

        private ReturningDeleteWrapper(PrimarySimpleDelete<?, ?> stmt) {
            super(stmt);
        }

        private ReturningDeleteWrapper(PrimarySimpleDeleteForMultiStmt<?> stmt) {
            super(stmt);
        }

    }//ReturningDeleteWrapper

    private static final class BatchReturningDeleteWrapper extends PostgreDeleteWrapper
            implements BatchReturningDelete, _BatchDml {

        private final List<?> paramList;

        private BatchReturningDeleteWrapper(PostgreBatchDelete<?, ?> stmt) {
            super(stmt);
            this.paramList = stmt.paramList;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    }//BatchReturningDeleteWrapper


}
