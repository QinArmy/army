package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

@SuppressWarnings("unchecked")
abstract class PostgreUpdates<I extends Item, T, SR, FT, FS extends Item, JT, JS, TR, WR, WA>
        extends JoinableUpdate.WithMultiUpdate<I, PostgreCtes, Object, FieldMeta<T>, SR, FT, FS, FS, JT, JS, JS, WR, WA, Object, Object, Object, Object>
        implements PostgreUpdate, _PostgreUpdate
        , PostgreStatement._TableSampleClause<TR>
        , PostgreStatement._RepeatableClause<FS>
        , PostgreStatement._PostgreFromClause<FT, FS>
        , Statement._FromNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._JoinNestedClause<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<FS>>>
        , PostgreStatement._CrossJoinNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._PostgreDynamicJoinClause<FS>
        , PostgreStatement._PostgreDynamicCrossJoinClause<FS> {


    static <I extends Item, Q extends Item> PostgreUpdate._SingleWithSpec<I, Q> single(
            Function<Update, I> dmlFunction
            , Function<ReturningUpdate, Q> dqlFunction) {
        final CriteriaContext context;
        context = CriteriaContexts.joinableSingleDmlContext(null);
        return new PrimarySimpleUpdateClause<>(context, dmlFunction, dqlFunction);
    }

    static <I extends Item, Q extends Item> PostgreUpdate._BatchSingleWithSpec<I, Q> batch(
            Function<Update, I> dmlFunction
            , Function<ReturningUpdate, Q> dqlFunction) {
        final CriteriaContext context;
        context = CriteriaContexts.joinableSingleDmlContext(null);
        return new BatchUpdateClause<>(context, dmlFunction, dqlFunction);
    }


    static <I extends Item> _DynamicSubMaterializedSpec<I> dynamicCteUpdate(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        final CriteriaContext context;
        context = CriteriaContexts.joinableSingleDmlContext(outerContext);
        return new DynamicSubSimpleUpdateClause<>(context, function);
    }

    static <I extends Item> _StaticSubMaterializedSpec<I> staticCteUpdate(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        final CriteriaContext context;
        context = CriteriaContexts.joinableSingleDmlContext(outerContext);
        return new StaticSubSimpleUpdateClause<>(context, function);
    }

    private final SQLWords modifier;

    private final TableMeta<?> updateTable;

    private final String tableAlias;

    private PostgreSupports.PostgreNoOnTableBlock noOnBlock;


    private PostgreUpdates(PostgreUpdateClause<?> clause) {
        super(clause, clause.context);
        this.modifier = clause.modifier;
        this.updateTable = clause.updateTable;

        this.tableAlias = clause.tableAlias;
        assert this.updateTable != null && this.tableAlias != null;
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
    public final _NestedLeftParenSpec<FS> from() {
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
        //WITH clause have ended
        throw ContextStack.castCriteriaApi(this.context);
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final PostgreSupports.PostgreNoOnTableBlock block;
        block = new PostgreSupports.PostgreNoOnTableBlock(joinType, modifier, table, alias);
        this.noOnBlock = block;
        return block;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new TableBlock.NoOnModifierTableBlock(joinType, modifier, tableItem, alias);
    }

    @Override
    final Dialect statementDialect() {
        return PostgreDialect.POSTGRE15;
    }


    @Override
    final void onStatementEnd() {
        final Object THIS = this;
        this.noOnBlock = null;
        if (this instanceof BatchUpdate && ((BatchUpdate<?, ?, ?>) THIS).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
    }

    @Override
    final void onClear() {
        if (this instanceof BatchUpdate) {
            final Object THIS = this;
            ((BatchUpdate<?, ?, ?>) THIS).paramList = null;
        }
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

    private PostgreSupports.PostgreNoOnTableBlock getNoOnBlock() {
        final PostgreSupports.PostgreNoOnTableBlock block = this.noOnBlock;
        if (this.context.lastBlock() != block) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return block;
    }


    private static abstract class SimpleUpdate<I extends Item, Q extends Item, T>
            extends PostgreUpdates<
            I,
            T,
            PostgreUpdate._SingleFromSpec<I, Q, T>,
            PostgreUpdate._TableSampleJoinSpec<I, Q>,
            PostgreUpdate._SingleJoinSpec<I, Q>,
            PostgreUpdate._TableSampleOnSpec<I, Q>,
            Statement._OnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
            PostgreUpdate._RepeatableJoinClause<I, Q>,
            PostgreUpdate._ReturningSpec<I, Q>,
            PostgreUpdate._SingleWhereAndSpec<I, Q>>
            implements PostgreUpdate._SingleFromSpec<I, Q, T>
            , PostgreUpdate._TableSampleJoinSpec<I, Q>
            , PostgreUpdate._RepeatableJoinClause<I, Q>
            , PostgreUpdate._SingleWhereAndSpec<I, Q>
            , PostgreUpdate._StaticReturningCommaSpec<Q> {

        private List<Selection> returningList;

        private SimpleUpdate(PostgreUpdateClause<?> clause) {
            super(clause);

        }

        @Override
        public final _SingleFromSpec<I, Q, T> set(Consumer<RowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.rowPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public final _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            this.where(new PostgreCursorPredicate(cursorName));
            return this;
        }

        @Override
        public final _DqlUpdateSpec<Q> returningAll() {
            this.returningList = PostgreSupports.RETURNING_ALL;
            return this;
        }

        @Override
        public final _DqlUpdateSpec<Q> returning(Consumer<ReturningBuilder> consumer) {
            final List<Selection> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.returningBuilder(list::add));
            if (list.size() == 0) {
                throw CriteriaUtils.returningListIsEmpty(this.context);
            }
            this.returningList = _CollectionUtils.unmodifiableList(list);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Selection selection) {
            this.onAddSelection(selection);
            return this;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.onAddSelection(Selections.forExp(expression, alias));
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
            this.onAddSelection(Selections.forExp(expression, alias));
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
        final _TableSampleOnSpec<I, Q> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null && modifier != SQLs.ONLY) {
                throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new SimpleOnTableBlock<>(joinType, modifier, table, tableAlias, this);
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> createItemBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> createCteBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
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
            implements Update, ReturningUpdate {

        private final Function<Update, I> dmlFunction;

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

    private static final class SubSimpleUpdate<I extends Item, T>
            extends SimpleUpdate<I, I, T>
            implements SubUpdate, SubReturningUpdate {

        private final Function<SubStatement, I> function;

        private final PostgreSupports.MaterializedOption materializedOption;

        private SubSimpleUpdate(SubSimpleUpdateClause<I, ?> clause) {
            super(clause);
            this.function = clause.function;
            this.materializedOption = clause.materializedOption;
        }

        @Override
        I onAsPostgreUpdate() {
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            return this.function.apply(option == null ? this : new PostgreSupports.PostgreSubStatement(option, this));
        }

        @Override
        I onAsReturningUpdate() {
            final PostgreSupports.MaterializedOption option = this.materializedOption;
            return this.function.apply(option == null ? this : new PostgreSupports.PostgreSubStatement(option, this));
        }

    }//SubSimpleUpdate


    private static final class SimpleOnTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreOnTableBlock<
            PostgreUpdate._RepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
            PostgreUpdate._SingleJoinSpec<I, Q>>
            implements PostgreUpdate._TableSampleOnSpec<I, Q>
            , PostgreUpdate._RepeatableOnClause<I, Q> {

        private SimpleOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TableMeta<?> tableItem, String alias
                , PostgreUpdate._SingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//SimpleOnTableBlock


    private static final class BatchUpdate<I extends Item, Q extends Item, T>
            extends PostgreUpdates<
            I,
            T,
            PostgreUpdate._BatchSingleFromSpec<I, Q, T>,
            PostgreUpdate._BatchTableSampleJoinSpec<I, Q>,
            PostgreUpdate._BatchSingleJoinSpec<I, Q>,
            PostgreUpdate._BatchTableSampleOnSpec<I, Q>,
            Statement._OnClause<PostgreUpdate._BatchSingleJoinSpec<I, Q>>,
            PostgreUpdate._BatchRepeatableJoinClause<I, Q>,
            PostgreUpdate._BatchReturningSpec<I, Q>,
            PostgreUpdate._BatchSingleWhereAndSpec<I, Q>>
            implements PostgreUpdate._BatchSingleFromSpec<I, Q, T>
            , PostgreUpdate._BatchTableSampleJoinSpec<I, Q>
            , PostgreUpdate._BatchRepeatableJoinClause<I, Q>
            , PostgreUpdate._BatchSingleWhereAndSpec<I, Q>
            , _DqlUpdateSpec<Q>
            , Update, ReturningUpdate, _BatchDml {

        private final Function<Update, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private List<Selection> returningList;
        private List<?> paramList;

        private BatchUpdate(BatchUpdateClause<I, Q> clause) {
            super(clause);
            this.dmlFunction = clause.dmlFunction;
            this.dqlFunction = clause.dqlFunction;

        }

        @Override
        public _BatchSingleFromClause<I, Q> set(Consumer<BatchRowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.batchRowPairs(this::onAddItemPair));
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
        public _BatchParamClause<_DqlUpdateSpec<Q>> returning(Consumer<ReturningBuilder> consumer) {
            final List<Selection> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.returningBuilder(list::add));
            if (list.size() == 0) {
                throw CriteriaUtils.returningListIsEmpty(this.context);
            }
            this.returningList = _CollectionUtils.unmodifiableList(list);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Selection selection) {
            this.onAddSelection(selection);
            return new BatchParamClause<>(this);
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> returning(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.onAddSelection(Selections.forExp(expression, alias));
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
        _BatchTableSampleOnSpec<I, Q> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null && modifier != SQLs.ONLY) {
                throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new BatchOnTableBlock<>(joinType, modifier, table, tableAlias, this);
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<I, Q>> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_BatchSingleJoinSpec<I, Q>> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        I onAsUpdate() {
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

    }//BatchUpdate


    private static final class BatchParamClause<Q extends Item>
            implements _BatchStaticReturningCommaSpec<Q> {

        private final BatchUpdate<?, Q, ?> statement;

        private BatchParamClause(BatchUpdate<?, Q, ?> statement) {
            this.statement = statement;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Selection selection) {
            this.statement.onAddSelection(selection);
            return this;
        }

        @Override
        public _BatchStaticReturningCommaSpec<Q> comma(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.statement.onAddSelection(Selections.forExp(expression, alias));
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

    private static final class BatchOnTableBlock<I extends Item, Q extends Item>
            extends PostgreSupports.PostgreOnTableBlock<
            PostgreUpdate._BatchRepeatableOnClause<I, Q>,
            Statement._OnClause<PostgreUpdate._BatchSingleJoinSpec<I, Q>>,
            PostgreUpdate._BatchSingleJoinSpec<I, Q>>
            implements PostgreUpdate._BatchTableSampleOnSpec<I, Q>
            , PostgreUpdate._BatchRepeatableOnClause<I, Q> {

        private BatchOnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TableMeta<?> tableItem, String alias
                , PostgreUpdate._BatchSingleJoinSpec<I, Q> stmt) {
            super(joinType, modifier, tableItem, alias, stmt);
        }


    }//BatchOnTableBlock


    private static abstract class PostgreUpdateClause<WE>
            extends CriteriaSupports.WithClause<PostgreCtes, WE> {
        SQLWords modifier;

        TableMeta<?> updateTable;

        String tableAlias;


        private PostgreUpdateClause(CriteriaContext context) {
            super(context);
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PostgreUpdateClause

    private static final class SimpleComma<I extends Item, Q extends Item> implements _CteComma<I, Q> {

        private final boolean recursive;

        private final SimpleUpdateClause<I, Q> clause;

        private final Function<String, _StaticCteLeftParenSpec<_CteComma<I, Q>>> function;

        private SimpleComma(boolean recursive, SimpleUpdateClause<I, Q> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I, Q>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.clause.endStaticWithClause(this.recursive)
                    .update(table, wordAs, tableAlias);
        }

        @Override
        public <T> _SingleSetClause<I, Q, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table, SQLs.WordAs wordAs
                , String tableAlias) {
            return this.clause.endStaticWithClause(this.recursive)
                    .update(wordOnly, table, wordAs, tableAlias);
        }


    }//SimpleComma


    private static abstract class SimpleUpdateClause<I extends Item, Q extends Item>
            extends PostgreUpdateClause<PostgreUpdate._SingleUpdateClause<I, Q>>
            implements PostgreUpdate._SingleWithSpec<I, Q> {

        private SimpleUpdateClause(CriteriaContext context) {
            super(context);
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


    }//SimpleUpdateClause

    private static final class PrimarySimpleUpdateClause<I extends Item, Q extends Item>
            extends SimpleUpdateClause<I, Q> {

        private final Function<Update, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private PrimarySimpleUpdateClause(CriteriaContext context
                , Function<Update, I> dmlFunction
                , Function<ReturningUpdate, Q> dqlFunction) {
            super(context);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        public <T> _SingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleSetClause<I, Q, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table
                , SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            if (wordOnly != SQLs.ONLY) {
                throw CriteriaUtils.dontSupportTabularModifier(this.context, wordOnly);
            }
            this.modifier = wordOnly;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new PrimarySimpleUpdate<>(this);
        }


    }//PrimarySimpleUpdateClause

    private static abstract class SubSimpleUpdateClause<I extends Item, MR>
            extends SimpleUpdateClause<I, I>
            implements _CteMaterializedClause<MR> {

        private final Function<SubStatement, I> function;

        private PostgreSupports.MaterializedOption materializedOption;

        private SubSimpleUpdateClause(CriteriaContext context, Function<SubStatement, I> function) {
            super(context);
            this.function = function;
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
        public final <T> _SingleSetClause<I, I, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new SubSimpleUpdate<>(this);
        }

        @Override
        public final <T> _SingleSetClause<I, I, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table, SQLs.WordAs wordAs
                , String tableAlias) {
            assert wordAs == SQLs.AS;
            if (wordOnly != SQLs.ONLY) {
                throw CriteriaUtils.dontSupportTabularModifier(this.context, wordOnly);
            }
            this.modifier = wordOnly;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new SubSimpleUpdate<>(this);
        }


    } //SubSimpleUpdateClause

    private static final class DynamicSubSimpleUpdateClause<I extends Item>
            extends SubSimpleUpdateClause<I, _SingleMinWithSpec<I, I>>
            implements _DynamicSubMaterializedSpec<I> {

        private DynamicSubSimpleUpdateClause(CriteriaContext context, Function<SubStatement, I> function) {
            super(context, function);
        }


    }//DynamicSubSimpleUpdateClause

    private static final class StaticSubSimpleUpdateClause<I extends Item>
            extends SubSimpleUpdateClause<I, _SingleUpdateClause<I, I>>
            implements _StaticSubMaterializedSpec<I> {

        private StaticSubSimpleUpdateClause(CriteriaContext context, Function<SubStatement, I> function) {
            super(context, function);
        }


    }//StaticSubSimpleUpdateClause


    private static final class BatchComma<I extends Item, Q extends Item> implements _BatchCteComma<I, Q> {

        private final boolean recursive;

        private final BatchUpdateClause<I, Q> clause;

        private final Function<String, _StaticCteLeftParenSpec<_BatchCteComma<I, Q>>> function;

        private BatchComma(boolean recursive, BatchUpdateClause<I, Q> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_BatchCteComma<I, Q>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> _BatchSingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.clause.endStaticWithClause(this.recursive)
                    .update(table, wordAs, tableAlias);
        }

        @Override
        public <T> _BatchSingleSetClause<I, Q, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table, SQLs.WordAs wordAs
                , String tableAlias) {
            return this.clause.endStaticWithClause(this.recursive)
                    .update(wordOnly, table, wordAs, tableAlias);
        }


    }//BatchComma


    private static final class BatchUpdateClause<I extends Item, Q extends Item>
            extends PostgreUpdateClause<PostgreUpdate._BatchSingleUpdateClause<I, Q>>
            implements PostgreUpdate._BatchSingleWithSpec<I, Q> {

        private final Function<Update, I> dmlFunction;

        private final Function<ReturningUpdate, Q> dqlFunction;

        private BatchUpdateClause(CriteriaContext context
                , Function<Update, I> dmlFunction
                , Function<ReturningUpdate, Q> dqlFunction) {
            super(context);
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
        public <T> _BatchSingleSetClause<I, Q, T> update(TableMeta<T> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }

        @Override
        public <T> _BatchSingleSetClause<I, Q, T> update(SQLs.WordOnly wordOnly, TableMeta<T> table, SQLs.WordAs wordAs
                , String tableAlias) {
            assert wordAs == SQLs.AS;
            if (wordOnly != SQLs.ONLY) {
                throw CriteriaUtils.dontSupportTabularModifier(this.context, wordOnly);
            }
            this.modifier = wordOnly;
            this.updateTable = table;
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }


    }//BatchUpdateClause


}
