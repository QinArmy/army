package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreDelete;
import io.army.criteria.postgre.*;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.*;

abstract class PostgreDeletes<I extends Item, Q extends Item, WE, FT, FS extends Item, JT, JS, TR, WR, WA>
        extends MultiDelete.WithMultiDelete<I, Q, PostgreCteBuilder, WE, FT, FS, FS, JT, JS, JS, WR, WA>
        implements PostgreDelete, _PostgreDelete
        , PostgreStatement._TableSampleClause<TR>
        , PostgreStatement._RepeatableClause<FS>
        , Statement._UsingNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._JoinNestedClause<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<FS>>>
        , PostgreStatement._CrossJoinNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._PostgreDynamicJoinClause<FS>
        , PostgreStatement._PostgreDynamicCrossJoinClause<FS> {


    static <I extends Item> PostgreDelete._DynamicSubMaterializedSpec<I> dynamicCteDelete(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }

    private SQLWords modifier;

    private TableMeta<?> updateTable;

    private String tableAlias;

    private PostgreSupports.PostgreNoOnTableBlock noOnBlock;

    private PostgreDeletes(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
        super(withSpec, context);
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
    final PostgreCteBuilder createCteBuilder(boolean recursive) {
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
    final void onClear() {

    }

    @Override
    public final List<_Pair<String, TableMeta<?>>> deleteTableList() {
        throw ContextStack.castCriteriaApi(this.context);
    }

    private PostgreSupports.PostgreNoOnTableBlock getNoOnBlock() {
        final PostgreSupports.PostgreNoOnTableBlock block = this.noOnBlock;
        if (this.context.lastBlock() != block) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return block;
    }


    private static abstract class SimpleDelete<I extends Item, Q extends Item>
            extends PostgreDeletes<
            I,
            Q,
            PostgreDelete._SingleDeleteClause<I, Q>,
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

        private SimpleDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
        }

        @Override
        public final _StaticCteLeftParenSpec<_CteComma<I, Q>> with(String name) {
            return null;
        }

        @Override
        public final _StaticCteLeftParenSpec<_CteComma<I, Q>> withRecursive(String name) {
            return null;
        }

        @Override
        public final _SingleUsingSpec<I, Q> delete(TableMeta<?> table, StandardSyntax.WordAs wordAs, String tableAlias) {
            return null;
        }

        @Override
        public final _SingleUsingSpec<I, Q> delete(StandardSyntax.WordOnly wordOnly, TableMeta<?> table, StandardSyntax.WordAs wordAs, String tableAlias) {
            return null;
        }

        @Override
        public final _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            return null;
        }

        @Override
        public final _DqlDeleteSpec<Q> returningAll() {
            return null;
        }

        @Override
        public final _DqlDeleteSpec<Q> returning(Consumer<ReturningBuilder> consumer) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Selection selection) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Expression expression, StandardSyntax.WordAs wordAs, String alias) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(Supplier<Selection> supplier) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> returning(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Selection selection) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Expression expression, StandardSyntax.WordAs wordAs, String alias) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(Supplier<Selection> supplier) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
            return null;
        }

        @Override
        public final _StaticReturningCommaSpec<Q> comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
            return null;
        }

        @Override
        final _TableSampleOnSpec<I, Q> createTableBlock(_JoinType joinType, Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            return null;
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> createItemBlock(_JoinType joinType, Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        final _OnClause<_SingleJoinSpec<I, Q>> createCteBlock(_JoinType joinType, Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            return null;
        }


    }//SimpleDelete

    private static final class PrimarySimpleDelete<I extends Item, Q extends Item>
            extends SimpleDelete<I, Q>
            implements Delete, ReturningDelete {

        private final Function<Delete, I> dmlFunction;

        private final Function<ReturningDelete, Q> dqlFunction;

        private PrimarySimpleDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context
                , Function<Delete, I> dmlFunction, Function<ReturningDelete, Q> dqlFunction) {
            super(withSpec, context);
            this.dmlFunction = dmlFunction;
            this.dqlFunction = dqlFunction;
        }

        @Override
        I onAsDelete() {
            return this.dmlFunction.apply(this);
        }

        @Override
        Q onAsReturningDelete() {
            return this.dqlFunction.apply(this);
        }


    }//PrimarySimpleDelete

    private static abstract class SubSimpleDelete<I extends Item, MR> extends SimpleDelete<I, I>
            implements SubDelete, SubReturningDelete, _CteMaterializedClause<MR> {

        private final Function<SubStatement, I> function;

        private PostgreSupports.MaterializedOption materializedOption;

        private SubSimpleDelete(@Nullable _WithClauseSpec withSpec, CriteriaContext context
                , Function<SubStatement, I> function) {
            super(withSpec, context);
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
        I onAsDelete() {
            return this.function.apply(this);
        }

        @Override
        I onAsReturningDelete() {
            return this.function.apply(this);
        }

    }//SubSimpleDelete


}
