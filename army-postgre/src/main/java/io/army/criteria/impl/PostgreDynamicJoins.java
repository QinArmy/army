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

abstract class PostgreDynamicJoins extends JoinableClause.DynamicJoinableBlock<
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

    PostgreSupports.PostgreNoOnTableBlock noOnBlock;

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
        this.getNoOnBlock().tableSample(method);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(String methodName, Expression argument) {
        this.getNoOnBlock().tableSample(methodName, argument);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(String methodName
            , Consumer<Consumer<Expression>> consumer) {
        this.getNoOnBlock().tableSample(methodName, consumer);
        return this;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, T argument) {
        this.getNoOnBlock().tableSample(method, valueOperator, argument);
        return this;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec tableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
        this.getNoOnBlock().tableSample(method, valueOperator, supplier);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec tableSample
            (BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                    , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                    , String keyName) {
        this.getNoOnBlock().tableSample(method, valueOperator, function, keyName);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(String methodName
            , Consumer<Consumer<Expression>> consumer) {
        this.getNoOnBlock().ifTableSample(methodName, consumer);
        return this;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, @Nullable T argument) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, argument);
        return this;
    }

    @Override
    public final <T> PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(
            BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
            , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, supplier);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicTableRepeatableJoinSpec ifTableSample(
            BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
            , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        this.getNoOnBlock().ifTableSample(method, valueOperator, function, keyName);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(Expression seed) {
        this.getNoOnBlock().repeatable(seed);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(Supplier<Expression> supplier) {
        this.getNoOnBlock().repeatable(supplier);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , Number seedValue) {
        this.getNoOnBlock().repeatable(valueOperator, seedValue);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(BiFunction<MappingType, Number, Expression> valueOperator
            , Supplier<Number> supplier) {
        this.getNoOnBlock().repeatable(valueOperator, supplier);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec repeatable(BiFunction<MappingType, Object, Expression> valueOperator
            , Function<String, ?> function, String keyName) {
        this.getNoOnBlock().repeatable(valueOperator, function, keyName);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(Supplier<Expression> supplier) {
        this.getNoOnBlock().ifRepeatable(supplier);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(BiFunction<MappingType, Number
            , Expression> valueOperator, @Nullable Number seedValue) {
        this.getNoOnBlock().ifRepeatable(valueOperator, seedValue);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(BiFunction<MappingType, Number
            , Expression> valueOperator, Supplier<Number> supplier) {
        this.getNoOnBlock().ifRepeatable(valueOperator, supplier);
        return this;
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec ifRepeatable(BiFunction<MappingType, Object
            , Expression> valueOperator, Function<String, ?> function, String keyName) {
        this.getNoOnBlock().ifRepeatable(valueOperator, function, keyName);
        return this;
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

    @Override
    final PostgreStatement._DynamicTableSampleOnSpec createTableBlock(_JoinType joinType
            , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        if (modifier != null && modifier != SQLs.ONLY) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        return new OnTableBlock(joinType, modifier, table, tableAlias, this);
    }

    @Override
    final Statement._OnClause<PostgreStatement._DynamicJoinSpec> createItemBlock(_JoinType joinType
            , @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
    }

    @Override
    final Statement._OnClause<PostgreStatement._DynamicJoinSpec> createCteBlock(_JoinType joinType
            , @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
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

    private PostgreSupports.PostgreNoOnTableBlock getNoOnBlock() {
        final PostgreSupports.PostgreNoOnTableBlock block = this.noOnBlock;
        if (block == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return block;
    }

    private static final class OnTableBlock extends PostgreSupports.PostgreOnTableBlock<
            PostgreStatement._DynamicRepeatableOnSpec,
            Statement._OnClause<PostgreStatement._DynamicJoinSpec>,
            PostgreStatement._DynamicJoinSpec>
            implements PostgreStatement._DynamicTableSampleOnSpec
            , PostgreStatement._DynamicRepeatableOnSpec {

        private OnTableBlock(_JoinType joinType, @Nullable SQLWords modifier
                , TableMeta<?> table, String alias
                , PostgreStatement._DynamicJoinSpec stmt) {
            super(joinType, modifier, table, alias, stmt);
        }


    }//OnTableBlock


    private static final class PostgreJoinBuilder extends PostgreDynamicJoins
            implements PostgreJoins {

        private boolean started;

        private PostgreJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }

        @Override
        public PostgreStatement._DynamicTableSampleOnSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            assert wordAs == SQLs.AS;
            this.started = true;
            final OnTableBlock block;
            block = new OnTableBlock(this.joinType, null, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public PostgreStatement._DynamicTableSampleOnSpec tabular(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            assert wordAs == SQLs.AS;
            if (modifier != SQLs.ONLY) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            final OnTableBlock block;
            block = new OnTableBlock(this.joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }

            final Statement._AsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> asClause;
            asClause = alias -> {
                final OnClauseTableBlock<PostgreStatement._DynamicJoinSpec> block;
                block = new OnClauseTableBlock<>(this.joinType, tabularItem, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> tabular(
                @Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }

            final Statement._AsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> asClause;
            asClause = alias -> {
                final OnClauseTableBlock.OnItemTableBlock<PostgreStatement._DynamicJoinSpec> block;
                block = new OnClauseTableBlock.OnItemTableBlock<>(this.joinType, modifier, tabularItem, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
            return asClause;
        }

        @Override
        public Statement._OnClause<PostgreStatement._DynamicJoinSpec> tabular(String cteName) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final OnClauseTableBlock<PostgreStatement._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(this.joinType, this.context.refCte(cteName), "", this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public Statement._OnClause<PostgreStatement._DynamicJoinSpec> tabular(String cteName, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final OnClauseTableBlock<PostgreStatement._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(this.joinType, this.context.refCte(cteName), alias, this);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PostgreJoinBuilder

    private static final class PostgreCrossBuilder extends PostgreDynamicJoins
            implements PostgreCrosses {

        private boolean started;

        private PostgreCrossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public PostgreStatement._DynamicTableSampleJoinSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            assert wordAs == SQLs.AS;

            final PostgreSupports.PostgreNoOnTableBlock block;
            block = new PostgreSupports.PostgreNoOnTableBlock(this.joinType, null, table, alias);
            this.blockConsumer.accept(block);
            this.noOnBlock = block;
            return this;
        }

        @Override
        public PostgreStatement._DynamicTableSampleJoinSpec tabular(Query.TableModifier modifier, TableMeta<?> table
                , SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            assert wordAs == SQLs.AS;
            if (modifier != SQLs.ONLY) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            final PostgreSupports.PostgreNoOnTableBlock block;
            block = new PostgreSupports.PostgreNoOnTableBlock(this.joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            this.noOnBlock = block;
            return this;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<PostgreStatement._DynamicJoinSpec> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final Statement._AsClause<PostgreStatement._DynamicJoinSpec> asClause;
            asClause = alias -> {
                final TableBlock.NoOnTableBlock block;
                block = new TableBlock.NoOnTableBlock(this.joinType, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<PostgreStatement._DynamicJoinSpec> tabular(Query.DerivedModifier modifier, Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            if (modifier != SQLs.LATERAL) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            final Statement._AsClause<PostgreStatement._DynamicJoinSpec> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(this.joinType, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public PostgreStatement._DynamicJoinSpec tabular(String cteName) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(this.joinType, this.context.refCte(cteName), "");
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public PostgreStatement._DynamicJoinSpec tabular(String cteName, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TableBlock.NoOnModifierTableBlock block;
            block = new TableBlock.NoOnModifierTableBlock(this.joinType, null, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
        }


    }//PostgreCrossBuilder


}
