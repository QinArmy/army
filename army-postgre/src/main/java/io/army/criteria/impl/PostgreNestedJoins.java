package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreTableBlock;
import io.army.criteria.postgre.PostgreCrosses;
import io.army.criteria.postgre.PostgreJoins;
import io.army.criteria.postgre.PostgreStatement;
import io.army.lang.Nullable;
import io.army.mapping.BigDecimalType;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class PostgreNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<I>
        implements PostgreStatement._NestedLeftParenSpec<I> {


    static <I extends Item> PostgreStatement._NestedLeftParenSpec<I> nestedItem(CriteriaContext context
            , _JoinType joinType, BiFunction<_JoinType, NestedItems, I> function) {
        return new PostgreNestedJoins<>(context, joinType, function);
    }


    private PostgreNestedJoins(CriteriaContext context, _JoinType joinType
            , BiFunction<_JoinType, NestedItems, I> function) {
        super(context, joinType, function);
    }

    @Override
    public PostgreStatement._NestedTableSampleJoinSpec<I> leftParen(TableMeta<?> table, StandardSyntax.WordAs wordAs
            , String tableAlias) {
        assert wordAs == SQLs.AS;
        final NestedTableSampleJoinClause<I> block;
        block = new NestedTableSampleJoinClause<>(this.context, this::onAddTableBlock
                , _JoinType.NONE, null, table, tableAlias, this::thisNestedJoinEnd);

        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public <T extends TabularItem> Statement._AsClause<PostgreStatement._PostgreNestedJoinClause<I>> leftParen(
            Supplier<T> supplier) {
        final TabularItem tabularItem;
        if ((tabularItem = supplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Statement._AsClause<PostgreStatement._PostgreNestedJoinClause<I>> asClause;
        asClause = alias -> {
            final PostgreNestedJoinClause<I> block;
            block = new PostgreNestedJoinClause<>(this.context, this::onAddTableBlock
                    , _JoinType.NONE, null, tabularItem, alias, this::thisNestedJoinEnd);
            this.onAddTableBlock(block);
            return block;
        };
        return asClause;
    }

    @Override
    public <T extends TabularItem> Statement._AsClause<PostgreStatement._PostgreNestedJoinClause<I>> leftParen(
            final Query.TabularModifier modifier, Supplier<T> supplier) {
        if (modifier != SQLs.LATERAL) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final TabularItem tabularItem;
        if ((tabularItem = supplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Statement._AsClause<PostgreStatement._PostgreNestedJoinClause<I>> asClause;
        asClause = alias -> {
            final PostgreNestedJoinClause<I> block;
            block = new PostgreNestedJoinClause<>(this.context, this::onAddTableBlock
                    , _JoinType.NONE, modifier, tabularItem, alias, this::thisNestedJoinEnd);
            this.onAddTableBlock(block);
            return block;
        };
        return asClause;
    }

    @Override
    public PostgreStatement._NestedTableSampleJoinSpec<I> leftParen(final Query.TableModifier modifier
            , TableMeta<?> table, StandardSyntax.WordAs wordAs, String tableAlias) {
        assert wordAs == SQLs.AS;
        if (modifier != SQLs.ONLY) {
            throw PostgreUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final NestedTableSampleJoinClause<I> block;
        block = new NestedTableSampleJoinClause<>(this.context, this::onAddTableBlock
                , _JoinType.NONE, modifier, table, tableAlias, this::thisNestedJoinEnd);

        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public PostgreStatement._PostgreNestedJoinClause<I> leftParen(String cteName) {
        final CriteriaContext context = this.context;
        final PostgreNestedJoinClause<I> block;
        block = new PostgreNestedJoinClause<>(context, this::onAddTableBlock
                , _JoinType.NONE, null, context.refCte(cteName), "", this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public PostgreStatement._PostgreNestedJoinClause<I> leftParen(String cteName, StandardSyntax.WordAs wordAs
            , String alias) {
        assert wordAs == SQLs.AS;

        final CriteriaContext context = this.context;
        if (!_StringUtils.hasText(alias)) {
            throw ContextStack.criteriaError(context, _Exceptions::cteNameNotText);
        }

        final PostgreNestedJoinClause<I> block;
        block = new PostgreNestedJoinClause<>(context, this::onAddTableBlock
                , _JoinType.NONE, null, context.refCte(cteName), alias, this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public PostgreStatement._NestedLeftParenSpec<PostgreStatement._PostgreNestedJoinClause<I>> leftParen() {
        return new PostgreNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd);
    }

    private PostgreStatement._PostgreNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final PostgreNestedJoinClause<I> clause;
        clause = new PostgreNestedJoinClause<>(this.context, this::onAddTableBlock
                , joinType, null, nestedItems, "", this::thisNestedJoinEnd);
        this.onAddNestedNested(clause);
        return clause;
    }


    private static class PostgreNestedJoinClause<I extends Item> extends JoinableClause.NestedJoinClause<
            PostgreStatement._NestedTableSampleCrossSpec<I>,
            PostgreStatement._NestedJoinSpec<I>,
            PostgreStatement._NestedJoinSpec<I>,
            PostgreStatement._NestedTableSampleOnSpec<I>,
            PostgreStatement._NestedOnSpec<I>,
            PostgreStatement._NestedOnSpec<I>,
            PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedOnSpec<I> {

        private final Supplier<I> supplier;

        private PostgreNestedJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.supplier = supplier;
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> leftJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> join() {
            return new PostgreNestedJoins<>(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> rightJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> fullJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedJoinSpec<I>> crossJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::nestedCrossJoinEnd);
        }

        @Override
        public final PostgreStatement._NestedJoinSpec<I> ifLeftJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final PostgreStatement._NestedJoinSpec<I> ifJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final PostgreStatement._NestedJoinSpec<I> ifRightJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final PostgreStatement._NestedJoinSpec<I> ifFullJoin(Consumer<PostgreJoins> consumer) {
            consumer.accept(PostgreDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final PostgreStatement._NestedJoinSpec<I> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
            consumer.accept(PostgreDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        public final I rightParen() {
            return this.supplier.get();
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
        final PostgreStatement._NestedTableSampleOnSpec<I> createTableBlock(_JoinType joinType
                , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            return null;
        }

        @Override
        final PostgreStatement._NestedOnSpec<I> createItemBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        final PostgreStatement._NestedOnSpec<I> createCteBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            return null;
        }

        /**
         * @see #leftJoin()
         * @see #join()
         * @see #rightJoin()
         * @see #fullJoin()
         */
        private PostgreStatement._NestedOnSpec<I> nestedJoinEnd(final _JoinType joinType
                , final NestedItems nestedItems) {
            joinType.assertStandardJoinType();
            final PostgreNestedJoinClause<I> block;
            block = new PostgreNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, null, nestedItems, "", this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private PostgreStatement._NestedJoinSpec<I> nestedCrossJoinEnd(final _JoinType joinType
                , final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final PostgreNestedJoinClause<I> block;
            block = new PostgreNestedJoinClause<>(this.context, this.blockConsumer
                    , _JoinType.CROSS_JOIN, null, items, "", this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PostgreNestedJoinClause

    @SuppressWarnings("unchecked")

    private static abstract class NestedTableSampleClause<I extends Item, TR, RR> extends PostgreNestedJoinClause<I>
            implements PostgreStatement._TableSampleClause<TR>
            , PostgreStatement._RepeatableClause<RR>
            , _PostgreTableBlock {

        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        private NestedTableSampleClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }

        @Override
        public final TR tableSample(final @Nullable Expression method) {
            if (method == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.sampleMethod = (ArmyExpression) method;
            return (TR) this;
        }

        @Override
        public final TR tableSample(String methodName, Expression argument) {
            this.sampleMethod = PostgreSupports.sampleMethod(methodName, Collections.singletonList(argument));
            return (TR) this;
        }

        @Override
        public final TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
            final List<Expression> expList = new ArrayList<>();
            consumer.accept(expList::add);
            this.sampleMethod = PostgreSupports.sampleMethod(methodName, expList);
            return (TR) this;
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, T argument) {
            return this.tableSample(method.apply(valueOperator, argument));
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            return this.tableSample(method.apply(valueOperator, supplier.get()));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public final TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
            final List<Expression> expList = new ArrayList<>();
            consumer.accept(expList::add);
            if (expList.size() > 0) {
                this.sampleMethod = PostgreSupports.sampleMethod(methodName, expList);
            }
            return (TR) this;
        }

        @Override
        public final <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, @Nullable T argument) {
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method
                , BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            final T argument;
            argument = supplier.get();
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method
                , BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object argument;
            argument = function.apply(keyName);
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final RR repeatable(final @Nullable Expression seed) {
            if (seed == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.seed = (ArmyExpression) seed;
            return (RR) this;
        }

        @Override
        public final RR repeatable(Supplier<Expression> supplier) {
            return this.repeatable(supplier.get());
        }

        @Override
        public final RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator, Number seedValue) {
            return this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
        }

        @Override
        public final RR repeatable(BiFunction<MappingType, Number, Expression> valueOperator
                , Supplier<Number> supplier) {
            return this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, supplier.get()));
        }

        @Override
        public final RR repeatable(BiFunction<MappingType, Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, function.apply(keyName)));
        }

        @Override
        public final RR ifRepeatable(Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.seed = (ArmyExpression) expression;
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator
                , @Nullable Number seedValue) {
            if (seedValue != null) {
                this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(BiFunction<MappingType, Number, Expression> valueOperator
                , Supplier<Number> supplier) {
            final Number seedValue;
            if ((seedValue = supplier.get()) != null) {
                this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(BiFunction<MappingType, Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            final Object seedValue;
            if ((seedValue = function.apply(keyName)) != null) {
                this.repeatable(valueOperator.apply(BigDecimalType.INSTANCE, seedValue));
            }
            return (RR) this;
        }

        @Override
        public final _Expression sampleMethod() {
            return this.sampleMethod;
        }

        @Override
        public final _Expression seed() {
            return this.seed;
        }


    }//NestedTableSampleClause


    private static final class NestedTableSampleJoinClause<I extends Item> extends NestedTableSampleClause<
            I,
            PostgreStatement._NestedRepeatableJoinClause<I>,
            PostgreStatement._PostgreNestedJoinClause<I>>
            implements PostgreStatement._NestedTableSampleJoinSpec<I> {

        private NestedTableSampleJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }


    }//NestedTableSampleJoinClause

    private static final class NestedTableSampleCrossClause<I extends Item> extends NestedTableSampleClause<
            I,
            PostgreStatement._NestedRepeatableCrossClause<I>,
            PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedTableSampleCrossSpec<I> {

        private NestedTableSampleCrossClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }


    }//NestedTableSampleCrossClause

    private static final class NestedTableSampleOnClause<I extends Item> extends NestedTableSampleClause<
            I,
            PostgreStatement._NestedRepeatableOnClause<I>,
            PostgreStatement._NestedOnSpec<I>>
            implements PostgreStatement._NestedTableSampleOnSpec<I> {

        private NestedTableSampleOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }


    }//NestedTableSampleOnClause


}
