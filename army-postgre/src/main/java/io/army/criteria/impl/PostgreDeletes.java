package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchReturningDelete;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreDelete;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
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
        Statement._OptionalParensStringClause<FC>,
        PostgreStatement._PostgreUsingNestedClause<FC>,
        PostgreStatement._PostgreJoinNestedClause<Statement._OnClause<FC>>,
        PostgreStatement._PostgreCrossNestedClause<FC>,
        PostgreStatement._PostgreDynamicJoinCrossClause<FC>,
        PostgreDelete._PostgreDeleteClause<DR> {


    static _SingleWithSpec<Delete, ReturningDelete> simpleDelete() {
        return new PrimarySimpleDelete();
    }


    static <I extends Item> _SingleWithSpec<I, I> simple(ArmyStmtSpec spec,
                                                         Function<PrimaryStatement, I> function) {
        return new PrimarySimpleDeleteForMultiStmt<>(spec, function);
    }

    static _BatchSingleWithSpec<BatchDelete, BatchReturningDelete> batchDelete() {
        return new PostgreBatchDelete();
    }


    static <I extends Item> _SingleWithSpec<I, I> subSimpleDelete(CriteriaContext outerContext,
                                                                  Function<SubStatement, I> function) {
        return new SubSimpleDelete<>(outerContext, function);
    }


    private SQLsSyntax.WordOnly modifier;

    private TableMeta<?> updateTable;

    private String tableAlias;

    _TabularBock fromCrossBlock;

    private PostgreDeletes(@Nullable _Statement._WithClauseSpec withSpec, CriteriaContext context) {
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
    public final DR delete(@Nullable SQLs.WordOnly only, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
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
        this.getFromDerived().parens(first, rest);
        return (FC) this;
    }

    @Override
    public final FC parens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().parens(this.context, consumer);
        return (FC) this;
    }

    @Override
    public final FC ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromDerived().ifParens(this.context, consumer);
        return (FC) this;
    }

    @Override
    public final FC using(Function<_NestedLeftParenSpec<FC>, FC> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedUsingEnd));
    }

    @Override
    public final FC crossJoin(Function<_NestedLeftParenSpec<FC>, FC> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedUsingEnd));
    }

    @Override
    public final _OnClause<FC> leftJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _OnClause<FC> join(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _OnClause<FC> rightJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd));
    }

    @Override
    public final _OnClause<FC> fullJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd));
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
        final PostgreSupports.FromClauseTableBlock block;
        block = new PostgreSupports.FromClauseTableBlock(joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FT) this;
    }

    @Override
    final FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        final _TabularBock block;
        block = TableBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    final FC nestedUsingEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        this.blockConsumer.accept(TableBlocks.fromNestedBlock(joinType, nestedItems));
        return (FC) this;
    }

    final _OnClause<FC> nestedJoinEnd(final _JoinType joinType, final _NestedItems nestedItems) {

        final TableBlocks.JoinClauseNestedBlock<FC> block;
        block = TableBlocks.joinNestedBlock(joinType, nestedItems, (FC) this);
        this.blockConsumer.accept(block);
        return block;
    }


    @Override
    final boolean isIllegalTableModifier(@Nullable Query.TableModifier modifier) {
        return CriteriaUtils.isIllegalOnly(modifier);
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final void onEndStatement() {
        this.fromCrossBlock = null;

    }

    @Override
    final void onClear() {

    }

    private PostgreSupports.FromClauseTableBlock getFromCrossBlock() {
        final _TabularBock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof PostgreSupports.FromClauseTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (PostgreSupports.FromClauseTableBlock) block;
    }


    final TableBlocks.FromClauseAliasDerivedBlock getFromDerived() {
        final _TabularBock block = this.fromCrossBlock;
        if (!(this.context.lastBlock() == block && block instanceof TableBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TableBlocks.FromClauseAliasDerivedBlock) block;
    }


    private static abstract class PostgreSimpleDelete<I extends Item, Q extends Item>
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

        private List<_Selection> returningList;

        private PostgreSimpleDelete(@Nullable _Statement._WithClauseSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
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
                    .add((_Selection) selection2);
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
                    .add((_Selection) function2.apply(alias2));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Function<String, Selection> function, String alias,
                                                            Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add((_Selection) selection);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Selection selection, Function<String, Selection> function,
                                                            String alias) {
            this.onAddSelection(selection)
                    .add((_Selection) function.apply(alias));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3) {
            final List<_Selection> list;
            list = this.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(TableField field1, TableField field2, TableField field3,
                                                            TableField field4) {
            final List<_Selection> list;
            list = this.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            list.add((_Selection) field4);
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
                    .add((_Selection) selection2);
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
                    .add((_Selection) function2.apply(alias2));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Function<String, Selection> function, String alias,
                                                        Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add((_Selection) selection);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Selection selection, Function<String, Selection> function,
                                                        String alias) {
            this.onAddSelection(selection)
                    .add((_Selection) function.apply(alias));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3) {
            final List<_Selection> list;
            list = this.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(TableField field1, TableField field2, TableField field3,
                                                        TableField field4) {
            final List<_Selection> list;
            list = this.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            list.add((_Selection) field4);
            return this;
        }

        @Override
        public final List<? extends _Selection> returningList() {
            final List<? extends _Selection> list = this.returningList;
            if (list == null || list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!(this instanceof SubStatement && this instanceof _ReturningDml)) {
                //no bug,never here
                throw new UnsupportedOperationException();
            }
            return list;
        }

        @Override
        public final Q asReturningDelete() {
            final List<_Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endDeleteStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return this.onAsReturningDelete();
        }

        abstract Q onAsReturningDelete();

        abstract I asPostgreDelete();


        @Override
        final I onAsDelete() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.asPostgreDelete();
        }


        @Override
        final _AsClause<_ParensJoinSpec<I, Q>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final TableBlocks.FromClauseAliasDerivedBlock block;
                block = TableBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
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
        final _AsParensOnClause<_SingleJoinSpec<I, Q>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final TableBlocks.JoinClauseAliasDerivedBlock<_SingleJoinSpec<I, Q>> block;
                block = TableBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
            final TableBlocks.JoinClauseCteBlock<_SingleJoinSpec<I, Q>> block;
            block = TableBlocks.joinCteBlock(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        private List<_Selection> onAddSelection(Selection selection) {
            List<_Selection> list = this.returningList;
            if (list == null) {
                this.returningList = list = new ArrayList<>();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add((_Selection) selection);
            return list;
        }


    }//SimpleDelete

    private static final class SimpleJoinClauseTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreTableOnBlock<
            PostgreDelete._RepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            PostgreDelete._SingleJoinSpec<I, Q>>
            implements PostgreDelete._TableSampleOnSpec<I, Q>,
            PostgreDelete._RepeatableOnClause<I, Q> {

        private SimpleJoinClauseTableBlock(_JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> tableItem,
                                           String alias, PostgreDelete._SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//SimpleJoinClauseTableBlock

    private static final class PrimarySimpleDelete extends PostgreSimpleDelete<Delete, ReturningDelete>
            implements Delete {

        private PrimarySimpleDelete() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(null));
        }

        @Override
        Delete asPostgreDelete() {
            return this;
        }

        @Override
        ReturningDelete onAsReturningDelete() {
            //ReturningDelete must be wrapped
            return new PrimaryReturningDeleteWrapper(this);
        }

    }//PrimarySimpleDelete

    private static final class PrimarySimpleDeleteForMultiStmt<I extends Item>
            extends PostgreSimpleDelete<I, I>
            implements Delete {

        private final Function<PrimaryStatement, I> function;

        private PrimarySimpleDeleteForMultiStmt(ArmyStmtSpec spec, Function<PrimaryStatement, I> function) {
            super(spec, CriteriaContexts.primaryJoinableSingleDmlContext(spec));
            this.function = function;
        }


        @Override
        I asPostgreDelete() {
            return this.function.apply(this);
        }

        @Override
        I onAsReturningDelete() {
            //ReturningDelete must be wrapped
            return this.function.apply(new PrimaryReturningDeleteWrapper(this));
        }


    }//PrimarySimpleDeleteForMultiStmt

    private static final class SubSimpleDelete<I extends Item>
            extends PostgreSimpleDelete<I, I>
            implements SubStatement, _ReturningDml {

        private final Function<SubStatement, I> function;

        /**
         * @see #subSimpleDelete(CriteriaContext, Function)
         */
        private SubSimpleDelete(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, CriteriaContexts.subJoinableSingleDmlContext(outerContext));
            this.function = function;
        }

        @Override
        I asPostgreDelete() {
            return this.function.apply(this);
        }

        @Override
        I onAsReturningDelete() {
            // sub update statement don't need wrapper
            return this.function.apply(this);
        }


    }//SubSimpleDelete



    /*-------------------below batch delete class  -------------------*/


    private static final class PostgreBatchDelete
            extends PostgreDeletes<
            BatchDelete,
            PostgreDelete._BatchSingleDeleteClause<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchSingleUsingSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchTableSampleJoinSpec<BatchDelete, BatchReturningDelete>,
            Statement._AsClause<PostgreDelete._BatchParensJoinSpec<BatchDelete, BatchReturningDelete>>,
            PostgreDelete._BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchTableSampleOnSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._AsParensOnClause<PostgreDelete._BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>>,
            Statement._OnClause<PostgreDelete._BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>>,
            PostgreDelete._BatchRepeatableJoinClause<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchReturningSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchSingleWhereAndSpec<BatchDelete, BatchReturningDelete>>
            implements PostgreDelete._BatchSingleWithSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchSingleUsingSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchTableSampleJoinSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchRepeatableJoinClause<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchParensJoinSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchSingleWhereAndSpec<BatchDelete, BatchReturningDelete>,
            Statement._DqlDeleteSpec<BatchReturningDelete>,
            BatchDelete,
            _BatchDml {

        private List<_Selection> returningList;

        private List<?> paramList;

        private PostgreBatchDelete() {
            super(null, CriteriaContexts.primaryJoinableSingleDmlContext(null));
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleDeleteClause<BatchDelete, BatchReturningDelete>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public PostgreQuery._StaticCteParensSpec<_BatchSingleDeleteClause<BatchDelete, BatchReturningDelete>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _BatchReturningSpec<BatchDelete, BatchReturningDelete> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public _BatchParamClause<_DqlDeleteSpec<BatchReturningDelete>> returningAll() {
            this.returningList = PostgreSupports.EMPTY_SELECTION_LIST;
            return new BatchParamClause(this);
        }

        @Override
        public _BatchParamClause<_DqlDeleteSpec<BatchReturningDelete>> returning(Consumer<Returnings> consumer) {
            consumer.accept(CriteriaSupports.returningBuilder(this::onAddSelection));
            if (this.returningList == null) {
                throw CriteriaUtils.returningListIsEmpty(this.context);
            }
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(Selection selection) {
            this.onAddSelection(selection);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(Selection selection1, Selection selection2) {
            this.onAddSelection(selection1)
                    .add((_Selection) selection2);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(Function<String, Selection> function, String alias) {
            this.onAddSelection(function.apply(alias));
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(Function<String, Selection> function1, String alias1,
                                                                              Function<String, Selection> function2, String alias2) {
            this.onAddSelection(function1.apply(alias1))
                    .add((_Selection) function2.apply(alias2));
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(Function<String, Selection> function, String alias,
                                                                              Selection selection) {
            this.onAddSelection(function.apply(alias))
                    .add((_Selection) selection);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(Selection selection, Function<String, Selection> function,
                                                                              String alias) {
            this.onAddSelection(selection)
                    .add((_Selection) function.apply(alias));
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(TableField field1, TableField field2, TableField field3) {
            final List<_Selection> list;
            list = this.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            return new BatchParamClause(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> returning(TableField field1, TableField field2, TableField field3,
                                                                              TableField field4) {
            final List<_Selection> list;
            list = this.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            list.add((_Selection) field4);
            return new BatchParamClause(this);
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> paramList(List<P> paramList) {
            if (this.paramList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<BatchDelete> paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }


        @Override
        public List<_Selection> returningList() {
            final List<_Selection> list = this.returningList;
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
        public BatchReturningDelete asReturningDelete() {
            final List<_Selection> returningList = this.returningList;
            if (!(returningList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.endDeleteStatement();
            this.returningList = _CollectionUtils.unmodifiableList(returningList);
            return new BatchReturningDeleteWrapper(this);
        }


        @Override
        BatchDelete onAsDelete() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this;
        }

        @Override
        _AsClause<_BatchParensJoinSpec<BatchDelete, BatchReturningDelete>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final TableBlocks.FromClauseAliasDerivedBlock block;
                block = TableBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }

        @Override
        _BatchTableSampleOnSpec<BatchDelete, BatchReturningDelete> onJoinTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            final BatchJoinClauseTableBlock block;
            block = new BatchJoinClauseTableBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final TableBlocks.JoinClauseAliasDerivedBlock<_BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>> block;
                block = TableBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
            final TableBlocks.JoinClauseCteBlock<_BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>> block;
            block = TableBlocks.joinCteBlock(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        private List<_Selection> onAddSelection(final Selection selection) {
            List<_Selection> list = this.returningList;
            if (list == null) {
                list = new ArrayList<>();
                this.returningList = list;
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add((_Selection) selection);
            return list;
        }


    }//BatchDelete

    private static final class BatchParamClause implements _BatchStaticReturningCommaSpec<BatchReturningDelete> {

        private final PostgreBatchDelete statement;

        private BatchParamClause(PostgreBatchDelete statement) {
            this.statement = statement;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(Selection selection) {
            this.statement.onAddSelection(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(Selection selection1, Selection selection2) {
            this.statement.onAddSelection(selection1)
                    .add((_Selection) selection2);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(Function<String, Selection> function, String alias) {
            this.statement.onAddSelection(function.apply(alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(Function<String, Selection> function1, String alias1,
                                                                          Function<String, Selection> function2, String alias2) {
            this.statement.onAddSelection(function1.apply(alias1))
                    .add((_Selection) function2.apply(alias2));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(Function<String, Selection> function, String alias,
                                                                          Selection selection) {
            this.statement.onAddSelection(function.apply(alias))
                    .add((_Selection) selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(Selection selection, Function<String, Selection> function,
                                                                          String alias) {
            this.statement.onAddSelection(selection)
                    .add((_Selection) function.apply(alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(TableField field1, TableField field2, TableField field3) {
            final List<_Selection> list;
            list = this.statement.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<BatchReturningDelete> comma(TableField field1, TableField field2, TableField field3,
                                                                          TableField field4) {
            final List<_Selection> list;
            list = this.statement.onAddSelection(field1);
            list.add((_Selection) field2);
            list.add((_Selection) field3);
            list.add((_Selection) field4);
            return this;
        }

        @Override
        public <P> _DqlDeleteSpec<BatchReturningDelete> paramList(List<P> paramList) {
            this.statement.paramList(paramList);
            return this.statement;
        }

        @Override
        public <P> _DqlDeleteSpec<BatchReturningDelete> paramList(Supplier<List<P>> supplier) {
            this.statement.paramList(supplier.get());
            return this.statement;
        }

        @Override
        public _DqlDeleteSpec<BatchReturningDelete> paramList(Function<String, ?> function, String keyName) {
            this.statement.paramList((List<?>) function.apply(keyName));
            return this.statement;
        }


    }//BatchParamClause

    private static final class BatchJoinClauseTableBlock
            extends PostgreSupports.PostgreTableOnBlock<
            PostgreDelete._BatchRepeatableOnClause<BatchDelete, BatchReturningDelete>,
            Statement._OnClause<PostgreDelete._BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>>,
            PostgreDelete._BatchSingleJoinSpec<BatchDelete, BatchReturningDelete>>
            implements PostgreDelete._BatchTableSampleOnSpec<BatchDelete, BatchReturningDelete>,
            PostgreDelete._BatchRepeatableOnClause<BatchDelete, BatchReturningDelete> {

        private BatchJoinClauseTableBlock(_JoinType joinType, @Nullable SQLWords modifier,
                                          TableMeta<?> tableItem, String alias,
                                          PostgreDelete._BatchSingleJoinSpec<BatchDelete, BatchReturningDelete> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//BatchJoinClauseTableBlock


     static abstract class PostgreReturningDeleteWrapper extends CriteriaSupports.StatementMockSupport
             implements PostgreDelete, _PostgreDelete, _ReturningDml {

         private final boolean recursive;

         private final List<_Cte> cteList;

         private final SQLsSyntax.WordOnly only;

         private final TableMeta<?> targetTable;

        private final String tableAlias;

        private final List<_TabularBock> tableBlockList;

        private final List<_Predicate> wherePredicateList;

        private final List<? extends _Selection> returningList;

        private Boolean prepared = Boolean.TRUE;

        private PostgreReturningDeleteWrapper(PostgreDeletes<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> stmt) {
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
        public final List<_TabularBock> tableBlockList() {
            return this.tableBlockList;
        }

        @Override
        public final List<? extends _Selection> returningList() {
            return this.returningList;
        }


    }//PostgreDeleteWrapper


    private static final class PrimaryReturningDeleteWrapper extends PostgreReturningDeleteWrapper
            implements ReturningDelete {

        private PrimaryReturningDeleteWrapper(PrimarySimpleDelete stmt) {
            super(stmt);
        }

        private PrimaryReturningDeleteWrapper(PrimarySimpleDeleteForMultiStmt<?> stmt) {
            super(stmt);
        }

    }//PrimaryReturningDeleteWrapper


    private static final class BatchReturningDeleteWrapper extends PostgreReturningDeleteWrapper
            implements BatchReturningDelete, _BatchDml {

        private final List<?> paramList;

        private BatchReturningDeleteWrapper(PostgreBatchDelete stmt) {
            super(stmt);
            this.paramList = stmt.paramList;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    }//BatchReturningDeleteWrapper


}
