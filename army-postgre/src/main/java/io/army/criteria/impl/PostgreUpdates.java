package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class PostgreUpdates<I extends Item, Q extends Item, T, SR, FT, FS extends Item, JT, JS, TR, WR, WA, RE>
        extends MultiUpdate.WithMultiUpdate<I, Q, PostgreCteBuilder, Object, FieldMeta<T>, SR, FT, FS, FS, JT, JS, JS, WR, WA, Object, Object, Object, Object>
        implements PostgreUpdate, _PostgreUpdate
        , PostgreStatement._TableSampleClause<TR>
        , PostgreStatement._RepeatableClause<FS>
        , PostgreStatement._PostgreFromClause<FT, FS>
        , Statement._FromNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._JoinNestedClause<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<FS>>>
        , PostgreStatement._CrossJoinNestedClause<PostgreStatement._NestedLeftParenSpec<FS>>
        , PostgreStatement._PostgreDynamicJoinClause<FS>
        , PostgreStatement._PostgreDynamicCrossJoinClause<FS>
        , DialectStatement._StaticReturningClause<RE>
        , DialectStatement._StaticReturningCommaClause<RE>
        , DialectStatement._DynamicReturningClause<Statement._DqlUpdateSpec<Q>> {


    static <I extends Item> PostgreUpdate._DynamicSubMaterializedSpec<I> dynamicCteUpdate(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }


    private PostgreSupports.PostgreNoOnTableBlock noOnBlock;

    private List<Selection> returningList;

    private PostgreUpdates(_WithClauseSpec withSpec, CriteriaContext context) {
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
    public final _DqlUpdateSpec<Q> returningAll() {
        this.returningList = PostgreSupports.RETURNING_ALL;
        return this;
    }

    @Override
    public final _DqlUpdateSpec<Q> returning(Consumer<ReturningBuilder> consumer) {
        final List<Selection> list = new ArrayList<>();
        consumer.accept(CriteriaSupports.returningBuilder(list::add));
        if (list.size() == 0) {
            throw CriteriaUtil
        }
        return null;
    }

    @Override
    public final RE returning(Selection selection) {
        return null;
    }

    @Override
    public final RE returning(Expression expression, StandardSyntax.WordAs wordAs, String alias) {
        return null;
    }

    @Override
    public final RE returning(Supplier<Selection> supplier) {
        return null;
    }

    @Override
    public final RE returning(NamedExpression exp1, NamedExpression exp2) {
        return null;
    }

    @Override
    public final RE returning(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
        return null;
    }

    @Override
    public final RE returning(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
        return null;
    }

    @Override
    public final RE comma(Selection selection) {
        return null;
    }

    @Override
    public final RE comma(Expression expression, StandardSyntax.WordAs wordAs, String alias) {
        return null;
    }

    @Override
    public final RE comma(Supplier<Selection> supplier) {
        return null;
    }

    @Override
    public final RE comma(NamedExpression exp1, NamedExpression exp2) {
        return null;
    }

    @Override
    public final RE comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
        return null;
    }

    @Override
    public final RE comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4) {
        return null;
    }

    @Override
    final PostgreCteBuilder createCteBuilder(boolean recursive) {
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


    private static final class SimpleUpdate<I extends Item, Q extends Item, T>
            extends PostgreUpdates<
            I,
            Q,
            T,
            PostgreUpdate._SingleFromSpec<I, Q, T>,
            PostgreUpdate._TableSampleJoinSpec<I, Q>,
            PostgreUpdate._SingleJoinSpec<I, Q>,
            PostgreUpdate._TableSampleOnSpec<I, Q>,
            Statement._OnClause<PostgreUpdate._SingleJoinSpec<I, Q>>,
            PostgreUpdate._RepeatableJoinClause<I, Q>,
            PostgreUpdate._ReturningSpec<I, Q>,
            PostgreUpdate._SingleWhereAndSpec<I, Q>,
            PostgreUpdate._StaticReturningCommaSpec<Q>>
            implements PostgreUpdate._SingleFromSpec<I, Q, T>
            , PostgreUpdate._TableSampleJoinSpec<I, Q>
            , PostgreUpdate._RepeatableJoinClause<I, Q>
            , PostgreUpdate._SingleWhereAndSpec<I, Q>
            , PostgreUpdate._StaticReturningCommaSpec<Q> {

        private SimpleUpdate(_WithClauseSpec withSpec, CriteriaContext context) {
            super(withSpec, context);
        }


        @Override
        public _SingleFromSpec<I, Q, T> set(Consumer<RowPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.rowPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _ReturningSpec<I, Q> whereCurrentOf(String cursorName) {
            return this;
        }

        @Override
        _TableSampleOnSpec<I, Q> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            return null;
        }

        @Override
        _OnClause<_SingleJoinSpec<I, Q>> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        _OnClause<_SingleJoinSpec<I, Q>> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        I onAsUpdate() {
            return null;
        }


    }//SimpleUpdate


}
