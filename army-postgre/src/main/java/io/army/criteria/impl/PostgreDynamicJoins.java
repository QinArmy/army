package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.postgre.PostgreCrosses;
import io.army.criteria.postgre.PostgreJoins;
import io.army.criteria.postgre.PostgreStatement;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class PostgreDynamicJoins extends JoinableClause.DynamicJoinClause<
        PostgreStatement._DynamicTableSampleJoinSpec,
        PostgreStatement._DynamicJoinSpec,
        PostgreStatement._DynamicJoinSpec,
        PostgreStatement._DynamicTableSampleOnSpec,
        Statement._OnClause<PostgreStatement._DynamicJoinSpec>,
        Statement._OnClause<PostgreStatement._DynamicJoinSpec>>
        implements PostgreStatement._DynamicTableSampleJoinSpec
        , PostgreStatement._DynamicTableRepeatableJoinSpec {

    static PostgreJoins joinBuilder(CriteriaContext context, _JoinType joinTyp
            , Consumer<_TableBlock> blockConsumer) {
        return new PostgreJoinBuilder(context, joinTyp, blockConsumer);
    }

    static PostgreCrosses crossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        return new PostgreCrossBuilder(context, blockConsumer);
    }

    private PostgreDynamicJoins(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
        super(context, joinTyp, blockConsumer);
    }

    @Override
    public final PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> leftJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> join() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> rightJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> fullJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._DynamicJoinSpec> crossJoin() {
        return PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedCrossEnd);
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifLeftJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRightJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifFullJoin(Consumer<PostgreJoins> consumer) {
        consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }


    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(final @Nullable Expression method) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(String methodName, Expression argument) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(String methodName
            , Consumer<Consumer<Expression>> consumer) {
        return null;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, T argument) {
        return null;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample
            (BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                    , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                    , String keyName) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(String methodName
            , Consumer<Consumer<Expression>> consumer) {
        return null;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, @Nullable T argument) {
        return null;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
            , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(Expression seed) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(Supplier<Expression> supplier) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , Number seedValue) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , Supplier<Number> supplier) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(BiFunction<MappingType, Object, Expression> valueOperator
            , Function<String, ?> function, String keyName) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(Supplier<Expression> supplier) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(BiFunction<MappingType, Number
            , Expression> valueOperator, @Nullable Number seedValue) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(BiFunction<MappingType, Number
            , Expression> valueOperator, Supplier<Number> supplier) {
        return null;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(BiFunction<MappingType, Object
            , Expression> valueOperator, Function<String, ?> function, String keyName) {
        return null;
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {
        return null;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , TabularItem tableItem, String alias) {
        return null;
    }

    @Override
    final PostgreStatement._DynamicTableSampleOnSpec createTableBlock(_JoinType joinType
            , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        return null;
    }

    @Override
    final Statement._OnClause<PostgreStatement._DynamicJoinSpec> createItemBlock(_JoinType joinType
            , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        return null;
    }

    @Override
    final Statement._OnClause<PostgreStatement._DynamicJoinSpec> createCteBlock(_JoinType joinType
            , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        return null;
    }

    /*-------------------below private method-------------------*/

    private Statement._OnClause<PostgreStatement._DynamicJoinSpec> nestedJoinEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        joinType.assertStandardJoinType();
        final OnClauseTableBlock<PostgreStatement._DynamicJoinSpec> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    private PostgreStatement._DynamicJoinSpec nestedCrossEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        assert joinType == _JoinType.CROSS_JOIN;
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return this;
    }


    private static final class PostgreJoinBuilder extends PostgreDynamicJoins
            implements PostgreJoins {

        private PostgreJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }

        @Override
        public PostgreStatement._DynamicTableSampleOnSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            return null;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> tabular(Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> tabular(Query.TabularModifier modifier, Supplier<T> supplier) {
            return null;
        }

        @Override
        public Statement._OnClause<PostgreStatement._DynamicJoinSpec> tabular(String cteName) {
            return null;
        }

        @Override
        public Statement._OnClause<PostgreStatement._DynamicJoinSpec> tabular(String cteName, SQLs.WordAs wordAs, String alias) {
            return null;
        }
    }//PostgreJoinBuilder

    private static final class PostgreCrossBuilder extends PostgreDynamicJoins
            implements PostgreCrosses {

        private PostgreCrossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public PostgreStatement._DynamicTableSampleJoinSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            return null;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<PostgreStatement._DynamicJoinSpec> tabular(Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<PostgreStatement._DynamicJoinSpec> tabular(Query.TabularModifier modifier, Supplier<T> supplier) {
            return null;
        }

        @Override
        public PostgreStatement._DynamicJoinSpec tabular(String cteName) {
            return null;
        }

        @Override
        public PostgreStatement._DynamicJoinSpec tabular(String cteName, SQLs.WordAs wordAs, String alias) {
            return null;
        }


    }//PostgreCrossBuilder


}
