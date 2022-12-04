package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.dialect.SubDelete;
import io.army.criteria.dialect.SubReturningDelete;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreDelete;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This class is the implementation of Postgre DELETE synatx.
 * </p>
 *
 * @see PostgreDelete
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class PostgreDeletes<I extends Item, WE, DR, FT, FS extends Item, JT, JS, TR, WR, WA>
        extends JoinableDelete.WithJoinableDelete<I, PostgreCtes, WE, FT, FS, FS, JT, JS, JS, WR, WA>
        implements PostgreDelete, _PostgreDelete
        , PostgreStatement._StaticTableSampleClause<TR>
        , PostgreStatement._RepeatableClause<FS>
        , Statement._UsingNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._JoinNestedClause<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<FS>>>
        , PostgreStatement._CrossJoinNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._PostgreDynamicJoinCrossClause<FS>
        , PostgreStatement._PostgreDynamicCrossJoinClause<FS>
        , PostgreDelete._PostgreDeleteClause<DR> {


    static <I extends Item, Q extends Item> _SingleWithSpec<I, Q> primarySingle(
            Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
        return new PrimarySimpleDelete<>(dmlFunction, dqlFunction);
    }

    static <I extends Item, Q extends Item> _BatchSingleWithSpec<I, Q> batch(
            Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
        return new BatchDelete<>(dmlFunction, dqlFunction);
    }


    static <I extends Item> _DynamicSubMaterializedSpec<I> dynamicCteDelete(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        return new DynamicSubSimpleDelete<>(outerContext, function);
    }

    static <I extends Item> _StaticSubMaterializedSpec<I> staticCteDelete(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        return new StaticSubSimpleDelete<>(outerContext, function);
    }

    static <I extends Item> _SingleWithSpec<I, I> singleForMultiStmt(_WithClauseSpec withSpec
            , Function<PrimaryStatement, I> function) {
        return new PrimarySimpleDeleteForMultiStmt<>(withSpec, function);
    }


    private SQLWords modifier;

    private TableMeta<?> updateTable;

    private String tableAlias;

    private PostgreSupports.PostgreNoOnTableBlock noOnBlock;

    private PostgreDeletes(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
    }

    @Override
    public final DR delete(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        if (this.updateTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        assert wordAs == SQLs.AS;
        this.modifier = null;
        this.updateTable = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    public final DR delete(SQLs.WordOnly wordOnly, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        if (this.updateTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (wordOnly != SQLs.ONLY) {
            throw CriteriaUtils.errorTabularModifier(this.context, wordOnly);
        }
        assert wordAs == SQLs.AS;
        this.modifier = wordOnly;
        this.updateTable = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    public final TR tableSample(Expression method) {
        this.getNoOnBlock().tableSample(method);
        return (TR) this;
    }

    @Override
    public final TR tableSample(String methodName, Expression argument) {
        this.getNoOnBlock().tableSample(methodName, argument);
        return (TR) this;
    }

    @Override
    public final TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
        this.getNoOnBlock().tableSample(methodName, consumer);
        return (TR) this;
    }

    @Override
    public final <E> TR tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
            , BiFunction<MappingType, E, Expression> valueOperator, E argument) {
        this.getNoOnBlock().tableSample(method, valueOperator, argument);
        return (TR) this;
    }

    @Override
    public final <E> TR tableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
            , BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        this.getNoOnBlock().tableSample(method, valueOperator, supplier);
        return (TR) this;
    }

    @Override
    public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
            , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        this.getNoOnBlock().tableSample(method, valueOperator, function, keyName);
        return (TR) this;
    }

    @Override
    public final TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
        this.getNoOnBlock().ifTableSample(methodName, consumer);
        return (TR) this;
    }

    @Override
    public final <E> TR ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
            , BiFunction<MappingType, E, Expression> valueOperator, @Nullable E argument) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, argument);
        return (TR) this;
    }

    @Override
    public final <E> TR ifTableSample(BiFunction<BiFunction<MappingType, E, Expression>, E, Expression> method
            , BiFunction<MappingType, E, Expression> valueOperator, Supplier<E> supplier) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, supplier);
        return (TR) this;
    }

    @Override
    public final TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
            , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, function, keyName);
        return (TR) this;
    }

    @Override
    public final FS repeatable(Expression seed) {
        this.getNoOnBlock().repeatable(seed);
        return (FS) this;
    }

    @Override
    public final FS repeatable(Supplier<Expression> supplier) {
        this.getNoOnBlock().repeatable(supplier);
        return (FS) this;
    }

    @Override
    public final FS repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue) {
        this.getNoOnBlock().repeatable(valueOperator, seedValue);
        return (FS) this;
    }

    @Override
    public final FS repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier) {
        this.getNoOnBlock().repeatable(valueOperator, supplier);
        return (FS) this;
    }

    @Override
    public final FS repeatable(BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
            , String keyName) {
        this.getNoOnBlock().repeatable(valueOperator, function, keyName);
        return (FS) this;
    }

    @Override
    public final FS ifRepeatable(Supplier<Expression> supplier) {
        this.getNoOnBlock().ifRepeatable(supplier);
        return (FS) this;
    }

    @Override
    public final FS ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, @Nullable Number seedValue) {
        this.getNoOnBlock().ifRepeatable(valueOperator, seedValue);
        return (FS) this;
    }

    @Override
    public final FS ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator, Supplier<Number> supplier) {
        this.getNoOnBlock().ifRepeatable(valueOperator, supplier);
        return (FS) this;
    }

    @Override
    public final FS ifRepeatable(BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
            , String keyName) {
        this.getNoOnBlock().ifRepeatable(valueOperator, function, keyName);
        return (FS) this;
    }


    @Override
    public final _NestedLeftParenSpec<FS> using() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNonCrossEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FS>> leftJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FS>> join() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FS>> rightJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FS>> fullJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<FS> crossJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNonCrossEnd);
    }

    @Override
    public final FS ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return (FS) this;
    }

    @Override
    public final FS ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return (FS) this;
    }

    @Override
    public final FS ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return (FS) this;
    }

    @Override
    public final FS ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return (FS) this;
    }

    @Override
    public final FS ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return (FS) this;
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
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        final PostgreSupports.PostgreNoOnTableBlock block;
        block = new PostgreSupports.PostgreNoOnTableBlock(joinType, modifier, table, alias);
        this.noOnBlock = block;
        return block;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.DerivedModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        return new TableBlock.NoOnModifierTableBlock(joinType, modifier, tableItem, alias);
    }

    final FS nestedNonCrossEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    final _OnClause<FS> nestedJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertStandardJoinType();

        final OnClauseTableBlock<FS> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", (FS) this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final void onEndStatement() {
        this.noOnBlock = null;

    }

    @Override
    final void onClear() {

    }


    private PostgreSupports.PostgreNoOnTableBlock getNoOnBlock() {
        final PostgreSupports.PostgreNoOnTableBlock block = this.noOnBlock;
        if (this.context.lastBlock() != block) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return block;
    }


    private static final class SimpleComma<I extends Item, Q extends Item> implements _CteComma<I, Q> {

        private final boolean recursive;

        private final SimpleDelete<I, Q, ?> statement;

        private final Function<String, _StaticCteLeftParenSpec<_CteComma<I, Q>>> function;

        private SimpleComma(boolean recursive, SimpleDelete<I, Q, ?> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = PostgreQueries.complexCte(statement.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I, Q>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _SingleUsingSpec<I, Q> delete(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.statement.endStaticWithClause(this.recursive)
                    .delete(table, wordAs, tableAlias);
        }

        @Override
        public _SingleUsingSpec<I, Q> delete(SQLs.WordOnly wordOnly, TableMeta<?> table, SQLs.WordAs wordAs
                , String tableAlias) {
            return this.statement.endStaticWithClause(this.recursive)
                    .delete(wordOnly, table, wordAs, tableAlias);
        }


    }//SimpleComma


    private static abstract class SimpleDelete<I extends Item, Q extends Item, S extends Statement>
            extends PostgreDeletes<
            I,
            PostgreDelete._SingleDeleteClause<I, Q>,
            PostgreDelete._SingleUsingSpec<I, Q>,
            PostgreDelete._TableSampleJoinSpec<I, Q>,
            PostgreDelete._SingleJoinSpec<I, Q>,
            PostgreDelete._TableSampleOnSpec<I, Q>,
            Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            PostgreDelete._RepeatableJoinClause<I, Q>,
            PostgreDelete._ReturningSpec<I, Q>,
            PostgreDelete._SingleWhereAndSpec<I, Q>>
            implements PostgreDelete._SingleWithSpec<I, Q>
            , PostgreDelete._SingleUsingSpec<I, Q>
            , PostgreDelete._TableSampleJoinSpec<I, Q>
            , PostgreDelete._RepeatableJoinClause<I, Q>
            , PostgreDelete._SingleWhereAndSpec<I, Q>
            , PostgreDelete._StaticReturningCommaSpec<Q> {

        final Function<S, I> dmlFunction;

        private List<Selection> returningList;

        private SimpleDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context, Function<S, I> dmlFunction) {
            super(withSpec, context);
            this.dmlFunction = dmlFunction;
        }

        @Override
        public final _StaticCteLeftParenSpec<_CteComma<I, Q>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public final _StaticCteLeftParenSpec<_CteComma<I, Q>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public final _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public final _DqlDeleteSpec<Q> returningAll() {
            this.returningList = PostgreSupports.RETURNING_ALL;
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
        public final _StaticReturningCommaSpec<Q> returning(Expression expression, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.onAddSelection(ArmySelections.forExp(expression, alias));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Supplier<Selection> supplier) {
            this.onAddSelection(supplier.get());
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            this.onAddSelection(exp1)
                    .add(exp2);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Selection selection) {
            this.onAddSelection(selection);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.onAddSelection(ArmySelections.forExp(expression, alias));
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Supplier<Selection> supplier) {
            this.onAddSelection(supplier.get());
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2) {
            this.onAddSelection(exp1)
                    .add(exp2);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
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
        final _TableSampleOnSpec<I, Q> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null && modifier != SQLs.ONLY) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            return new SimpleOnTableBlock<>(joinType, modifier, table, tableAlias, this);
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> createItemBlock(_JoinType joinType
                , @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> createCteBlock(_JoinType joinType
                , @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }


        @Override
        final I onAsDelete() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.dmlFunction.apply((S) this);
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
            extends PostgreSupports.PostgreOnTableBlock<
            PostgreDelete._RepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreDelete._SingleJoinSpec<I, Q>>,
            PostgreDelete._SingleJoinSpec<I, Q>>
            implements PostgreDelete._TableSampleOnSpec<I, Q>
            , PostgreDelete._RepeatableOnClause<I, Q> {

        private SimpleOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TableMeta<?> tableItem, String alias
                , PostgreDelete._SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//SimpleOnTableBlock

    private static final class PrimarySimpleDelete<I extends Item, Q extends Item>
            extends SimpleDelete<I, Q, Delete>
            implements Delete, ReturningDelete {

        private final Function<Delete, I> dmlFunction;

        private final Function<ReturningDelete, Q> dqlFunction;

        private PrimarySimpleDelete(Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
            super(null, CriteriaContexts.joinableSingleDmlContext(null), dmlFunction);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        Q onAsReturningDelete() {
            return this.dqlFunction.apply(this);
        }


    }//PrimarySimpleDelete

    private static final class PrimarySimpleDeleteForMultiStmt<I extends Item>
            extends SimpleDelete<I, I, PrimaryStatement>
            implements Delete, ReturningDelete {


        private PrimarySimpleDeleteForMultiStmt(_WithClauseSpec withSpec, Function<PrimaryStatement, I> function) {
            super(withSpec, CriteriaContexts.joinableSingleDmlContext(null), function);
        }


        @Override
        I onAsReturningDelete() {
            return this.dmlFunction.apply(this);
        }


    }//PrimarySimpleDeleteForMultiStmt

    private static abstract class SubSimpleDelete<I extends Item, MR>
            extends SimpleDelete<I, I, SubStatement>
            implements SubDelete, SubReturningDelete, _CteMaterializedClause<MR> {

        private PostgreSupports.MaterializedOption materializedOption;

        private SubSimpleDelete(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(null, CriteriaContexts.joinableSingleDmlContext(outerContext), function);
        }


        @Override
        public final MR materialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            return (MR) this;
        }

        @Override
        public final MR notMaterialized() {
            this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            return (MR) this;
        }

        @Override
        public final MR ifMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return (MR) this;
        }

        @Override
        public final MR ifNotMaterialized(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.materializedOption = PostgreSupports.MaterializedOption.NOT_MATERIALIZED;
            } else {
                this.materializedOption = null;
            }
            return (MR) this;
        }


        @Override
        final I onAsReturningDelete() {
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            return this.dmlFunction.apply(option == null ? this : new PostgreSupports.PostgreSubStatement(option, this));
        }


    }//SubSimpleDelete

    private static final class StaticSubSimpleDelete<I extends Item>
            extends SubSimpleDelete<I, _SingleDeleteClause<I, I>>
            implements _StaticSubMaterializedSpec<I> {

        private StaticSubSimpleDelete(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(outerContext, function);
        }


    }//StaticSubSimpleDelete

    /**
     * @see #dynamicCteDelete(CriteriaContext, Function)
     */
    private static final class DynamicSubSimpleDelete<I extends Item>
            extends SubSimpleDelete<I, _SingleMinWithSpec<I, I>>
            implements _DynamicSubMaterializedSpec<I> {

        private DynamicSubSimpleDelete(CriteriaContext outerContext, Function<SubStatement, I> function) {
            super(outerContext, function);
        }


    }//StaticSubSimpleDelete


    /*-------------------below batch delete class  -------------------*/

    private static final class BatchComma<I extends Item, Q extends Item> implements _BatchCteComma<I, Q> {

        private final boolean recursive;

        private final BatchDelete<I, Q> statement;

        private final Function<String, _StaticCteLeftParenSpec<_BatchCteComma<I, Q>>> function;

        private BatchComma(boolean recursive, BatchDelete<I, Q> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = PostgreQueries.complexCte(statement.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_BatchCteComma<I, Q>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _BatchSingleUsingSpec<I, Q> delete(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.statement.endStaticWithClause(this.recursive)
                    .delete(table, wordAs, tableAlias);
        }

        @Override
        public _BatchSingleUsingSpec<I, Q> delete(SQLs.WordOnly wordOnly, TableMeta<?> table, SQLs.WordAs wordAs
                , String tableAlias) {
            return this.statement.endStaticWithClause(this.recursive)
                    .delete(wordOnly, table, wordAs, tableAlias);
        }

    }//BatchComma

    private static final class BatchDelete<I extends Item, Q extends Item>
            extends PostgreDeletes<
            I,
            PostgreDelete._BatchSingleDeleteClause<I, Q>,
            PostgreDelete._BatchSingleUsingSpec<I, Q>,
            PostgreDelete._BatchTableSampleJoinSpec<I, Q>,
            PostgreDelete._BatchSingleJoinSpec<I, Q>,
            PostgreDelete._BatchTableSampleOnSpec<I, Q>,
            _OnClause<PostgreDelete._BatchSingleJoinSpec<I, Q>>,
            PostgreDelete._BatchRepeatableJoinClause<I, Q>,
            PostgreDelete._BatchReturningSpec<I, Q>,
            PostgreDelete._BatchSingleWhereAndSpec<I, Q>>
            implements PostgreDelete._BatchSingleWithSpec<I, Q>
            , PostgreDelete._BatchSingleUsingSpec<I, Q>
            , PostgreDelete._BatchTableSampleJoinSpec<I, Q>
            , PostgreDelete._BatchRepeatableJoinClause<I, Q>
            , PostgreDelete._BatchSingleWhereAndSpec<I, Q>
            , _DqlDeleteSpec<Q>
            , Delete, ReturningDelete, _BatchDml {

        private final Function<Delete, I> dmlFunction;

        private final Function<ReturningDelete, Q> dqlFunction;

        private List<Selection> returningList;

        private List<?> paramList;

        private BatchDelete(Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
            super(null, CriteriaContexts.joinableSingleDmlContext(null));
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public _StaticCteLeftParenSpec<_BatchCteComma<I, Q>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma<>(recursive, this).function.apply(name);
        }

        @Override
        public _StaticCteLeftParenSpec<_BatchCteComma<I, Q>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma<>(recursive, this).function.apply(name);
        }

        @Override
        public _BatchReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public _BatchParamClause<_DqlDeleteSpec<Q>> returningAll() {
            this.returningList = PostgreSupports.RETURNING_ALL;
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
        public _BatchStaticReturningCommaSpec<Q> returning(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.onAddSelection(ArmySelections.forExp(expression, alias));
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Supplier<Selection> supplier) {
            this.onAddSelection(supplier.get());
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            this.onAddSelection(exp1)
                    .add(exp2);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2
                , NamedExpression exp3, NamedExpression exp4) {
            final List<Selection> list;
            list = this.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
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
            return this.dqlFunction.apply(this);
        }

        @Override
        _BatchTableSampleOnSpec<I, Q> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null && modifier != SQLs.ONLY) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            return new BatchOnTableBlock<>(joinType, modifier, table, tableAlias, this);
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<I, Q>> createItemBlock(_JoinType joinType
                , @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<I, Q>> createCteBlock(_JoinType joinType
                , @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        I onAsDelete() {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.emptyList();
            return this.dmlFunction.apply(this);
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

        private final BatchDelete<?, Q> statement;

        private BatchParamClause(BatchDelete<?, Q> statement) {
            this.statement = statement;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Selection selection) {
            this.statement.onAddSelection(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.statement.onAddSelection(ArmySelections.forExp(expression, alias));
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Supplier<Selection> supplier) {
            this.statement.onAddSelection(supplier.get());
            return this;
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
        public _BatchStaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3
                , NamedExpression exp4) {
            final List<Selection> list;
            list = this.statement.onAddSelection(exp1);
            list.add(exp2);
            list.add(exp3);
            list.add(exp4);
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
            extends PostgreSupports.PostgreOnTableBlock<
            PostgreDelete._BatchRepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreDelete._BatchSingleJoinSpec<I, Q>>,
            PostgreDelete._BatchSingleJoinSpec<I, Q>>
            implements PostgreDelete._BatchTableSampleOnSpec<I, Q>
            , PostgreDelete._BatchRepeatableOnClause<I, Q> {

        private BatchOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TableMeta<?> tableItem, String alias
                , PostgreDelete._BatchSingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//BatchOnTableBlock


}
