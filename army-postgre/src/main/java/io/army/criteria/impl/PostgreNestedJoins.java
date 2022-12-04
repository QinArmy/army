package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ModifierTableBlock;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreTableBlock;
import io.army.criteria.postgre.PostgreCrosses;
import io.army.criteria.postgre.PostgreJoins;
import io.army.criteria.postgre.PostgreStatement;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class hold the implementation of Postgre nested join.
 * </p>
 *
 * @since 1.0
 */

final class PostgreNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<I>
        implements PostgreStatement._NestedLeftParenSpec<I> {


    static <I extends Item> PostgreStatement._NestedLeftParenSpec<I> nestedItem(
            CriteriaContext context, _JoinType joinType, BiFunction<_JoinType, NestedItems, I> function) {
        return new PostgreNestedJoins<>(context, joinType, function);
    }


    private PostgreNestedJoins(CriteriaContext context, _JoinType joinType,
                               BiFunction<_JoinType, NestedItems, I> function) {
        super(context, joinType, function);
    }

    @Override
    public PostgreStatement._NestedTableSampleJoinSpec<I> leftParen(TableMeta<?> table, SQLsSyntax.WordAs wordAs,
                                                                    String tableAlias) {
        final NestedTableJoinBlock<I> block;
        block = new NestedTableJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null, table, tableAlias,
                this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public PostgreStatement._NestedTableSampleJoinSpec<I> leftParen(final Query.TableModifier modifier,
                                                                    TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        if (modifier != SQLs.ONLY) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        final NestedTableJoinBlock<I> block;
        block = new NestedTableJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, modifier, table,
                tableAlias, this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public <T extends DerivedTable> Statement._AsClause<PostgreStatement._NestedParensJoinSpec<I>> leftParen(Supplier<T> supplier) {
        return this.onAddDerived(null, supplier.get());
    }

    @Override
    public <T extends DerivedTable> Statement._AsClause<PostgreStatement._NestedParensJoinSpec<I>> leftParen(
            Query.DerivedModifier modifier, Supplier<T> supplier) {
        if (modifier != SQLs.LATERAL) {
            throw PostgreUtils.errorTabularModifier(this.context, modifier);
        }
        return this.onAddDerived(modifier, supplier.get());
    }


    @Override
    public PostgreStatement._PostgreNestedJoinClause<I> leftParen(String cteName) {
        final CriteriaContext context = this.context;
        final PostgreNestedBlock<I> block;
        block = new PostgreNestedBlock<>(context, this::onAddTableBlock, _JoinType.NONE, null, context.refCte(cteName),
                "", this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public PostgreStatement._PostgreNestedJoinClause<I> leftParen(String cteName, SQLsSyntax.WordAs wordAs, String alias) {

        if (!_StringUtils.hasText(alias)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::cteNameNotText);
        }
        final PostgreNestedBlock<I> block;
        block = new PostgreNestedBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null,
                this.context.refCte(cteName), alias, this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public PostgreStatement._NestedLeftParenSpec<PostgreStatement._PostgreNestedJoinClause<I>> leftParen() {
        return new PostgreNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd);
    }


    private Statement._AsClause<PostgreStatement._NestedParensJoinSpec<I>> onAddDerived(
            @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
        if (table == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return alias -> {
            final NestedDerivedJoinBlock<I> block;
            block = new NestedDerivedJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, modifier,
                    table, alias, this::thisNestedJoinEnd);
            this.onAddTableBlock(block);
            return block;
        };
    }

    private PostgreStatement._PostgreNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final PostgreNestedBlock<I> clause;
        clause = new PostgreNestedBlock<>(this.context, this::onAddTableBlock, joinType, null, nestedItems, "",
                this::thisNestedJoinEnd);
        this.onAddTableBlock(clause);
        return clause;
    }


    private static class PostgreNestedBlock<I extends Item> extends JoinableClause.NestedJoinableBlock<
            PostgreStatement._NestedTableSampleCrossSpec<I>,
            Statement._AsClause<PostgreStatement._NestedParensCrossSpec<I>>,
            PostgreStatement._NestedJoinSpec<I>,
            PostgreStatement._NestedTableSampleOnSpec<I>,
            Statement._AsClause<PostgreStatement._NestedParensOnSpec<I>>,
            PostgreStatement._NestedOnSpec<I>,
            PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private PostgreNestedBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                   @Nullable SQLWords modifier, TabularItem tabularItem, String alias,
                                   Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.ender = ender;
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> leftJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> join() {
            return new PostgreNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> rightJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>> fullJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
        }

        @Override
        public final PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedJoinSpec<I>> crossJoin() {
            return new PostgreNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd);
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
            return this.ender.get();
        }


        @Override
        final Query.TableModifier tableModifier(@Nullable Query.TableModifier modifier) {
            if (modifier != null && modifier != SQLs.ONLY) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            return modifier;
        }

        @Override
        final Query.DerivedModifier derivedModifier(@Nullable Query.DerivedModifier modifier) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw PostgreUtils.errorTabularModifier(this.context, modifier);
            }
            return modifier;
        }

        @Override
        final PostgreStatement._NestedTableSampleCrossSpec<I> onFromTable(_JoinType joinType,
                                                                          @Nullable Query.TableModifier modifier,
                                                                          TableMeta<?> table, String alias) {
            final NestedTableCrossBlock<I> block;
            block = new NestedTableCrossBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<PostgreStatement._NestedParensCrossSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final NestedDerivedCrossBlock<I> block;
                block = new NestedDerivedCrossBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final PostgreStatement._NestedJoinSpec<I> onFromCte(_JoinType joinType,
                                                            @Nullable Query.DerivedModifier modifier,
                                                            CteItem cteItem, String alias) {
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, cteItem, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final PostgreStatement._NestedTableSampleOnSpec<I> onJoinTable(_JoinType joinType,
                                                                       @Nullable Query.TableModifier modifier,
                                                                       TableMeta<?> table, String alias) {
            final NestedTableOnBlock<I> block;
            block = new NestedTableOnBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<PostgreStatement._NestedParensOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final NestedDerivedOnBlock<I> block;
                block = new NestedDerivedOnBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final PostgreStatement._NestedOnSpec<I> onJoinCte(_JoinType joinType,
                                                          @Nullable Query.DerivedModifier modifier,
                                                          CteItem cteItem, String alias) {
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, cteItem, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #leftJoin()
         * @see #join()
         * @see #rightJoin()
         * @see #fullJoin()
         */
        private PostgreStatement._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType,
                                                                final NestedItems nestedItems) {
            joinType.assertStandardJoinType();
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, joinType, null, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private PostgreStatement._NestedJoinSpec<I> crossNestedEnd(final _JoinType joinType,
                                                                   final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, null, items, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PostgreNestedBlock

    @SuppressWarnings("unchecked")

    private static abstract class NestedTableBlock<I extends Item, TR, RR> extends PostgreNestedBlock<I>
            implements PostgreStatement._TableSampleClause<TR>,
            PostgreStatement._RepeatableClause<RR>,
            _PostgreTableBlock {

        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        private NestedTableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                 @Nullable SQLWords modifier, TableMeta<?> table, String alias,
                                 Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

        @Override
        public final TR tableSample(final @Nullable Expression method) {
            if (this.sampleMethod != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (method == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.sampleMethod = (ArmyExpression) method;
            return (TR) this;
        }

        @Override
        public final TR tableSample(String methodName, Expression argument) {
            return this.tableSample(FunctionUtils.oneArgVoidFunc(methodName, argument));
        }

        @Override
        public final TR tableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
            final List<Expression> list = new ArrayList<>();
            consumer.accept(list::add);
            return this.tableSample(FunctionUtils.multiArgVoidFunc(methodName, list));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                    BiFunction<MappingType, Object, Expression> valueOperator, Object argument) {
            return this.tableSample(method.apply(valueOperator, argument));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Expression, Expression>, Expression, Expression> method,
                                    BiFunction<MappingType, Expression, Expression> valueOperator, Expression argument) {
            return this.tableSample(method.apply(valueOperator, argument));
        }

        @Override
        public final <T> TR tableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method,
                                        BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            return this.tableSample(method.apply(valueOperator, supplier.get()));
        }

        @Override
        public final TR tableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                    BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                    String keyName) {
            return this.tableSample(method.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public final TR ifTableSample(String methodName, Consumer<Consumer<Expression>> consumer) {
            final List<Expression> expList = new ArrayList<>();
            consumer.accept(expList::add);
            if (expList.size() > 0) {
                this.tableSample(FunctionUtils.multiArgVoidFunc(methodName, expList));
            }
            return (TR) this;
        }


        @Override
        public final <T> TR ifTableSample(BiFunction<BiFunction<MappingType, T, Expression>, T, Expression> method,
                                          BiFunction<MappingType, T, Expression> valueOperator, Supplier<T> supplier) {
            final T argument;
            argument = supplier.get();
            if (argument != null) {
                this.tableSample(method.apply(valueOperator, argument));
            }
            return (TR) this;
        }

        @Override
        public final TR ifTableSample(BiFunction<BiFunction<MappingType, Object, Expression>, Object, Expression> method,
                                      BiFunction<MappingType, Object, Expression> valueOperator, Function<String, ?> function,
                                      String keyName) {
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
        public final RR repeatable(Function<Number, Expression> valueOperator, Number seedValue) {
            return this.repeatable(valueOperator.apply(seedValue));
        }

        @Override
        public final <E extends Number> RR repeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
            return this.repeatable(valueOperator.apply(supplier.get()));
        }

        @Override
        public final RR repeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                   String keyName) {
            return this.repeatable(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public final RR ifRepeatable(Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.repeatable(expression);
            }
            return (RR) this;
        }

        @Override
        public final <E extends Number> RR ifRepeatable(Function<E, Expression> valueOperator, Supplier<E> supplier) {
            final E seedValue;
            if ((seedValue = supplier.get()) != null) {
                this.repeatable(valueOperator.apply(seedValue));
            }
            return (RR) this;
        }

        @Override
        public final RR ifRepeatable(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                     String keyName) {
            final Object seedValue;
            if ((seedValue = function.apply(keyName)) != null) {
                this.repeatable(valueOperator.apply(seedValue));
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


    }//NestedTableBlock


    private static final class NestedTableJoinBlock<I extends Item> extends NestedTableBlock<
            I,
            PostgreStatement._NestedRepeatableJoinClause<I>,
            PostgreStatement._PostgreNestedJoinClause<I>>
            implements PostgreStatement._NestedTableSampleJoinSpec<I> {

        private NestedTableJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                     @Nullable SQLWords modifier, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }


    }//NestedTableJoinBlock

    private static final class NestedTableCrossBlock<I extends Item> extends NestedTableBlock<
            I,
            PostgreStatement._NestedRepeatableCrossClause<I>,
            PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedTableSampleCrossSpec<I> {

        private NestedTableCrossBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                      @Nullable SQLWords modifier, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }


    }//NestedTableCrossBlock

    private static final class NestedTableOnBlock<I extends Item> extends NestedTableBlock<
            I,
            PostgreStatement._NestedRepeatableOnClause<I>,
            PostgreStatement._NestedOnSpec<I>>
            implements PostgreStatement._NestedTableSampleOnSpec<I> {

        private NestedTableOnBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                   @Nullable SQLWords modifier, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedTableOnBlock


    @SuppressWarnings("unchecked")
    private static abstract class NestedDerivedBlock<I extends Item, R> extends PostgreNestedBlock<I>
            implements Statement._ParensStringClause<R>, _ModifierTableBlock {

        private NestedDerivedBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                   @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

        @Override
        public final R parens(String first, String... rest) {
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(ArrayUtils.unmodifiableListOf(first, rest));
            return (R) this;
        }

        @Override
        public final R parens(Consumer<Consumer<String>> consumer) {
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(CriteriaUtils.columnAliasList(true, consumer));
            return (R) this;
        }

        @Override
        public final R ifParens(Consumer<Consumer<String>> consumer) {
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(CriteriaUtils.columnAliasList(false, consumer));
            return (R) this;
        }


    }//NestedDerivedBlock

    private static final class NestedDerivedJoinBlock<I extends Item>
            extends NestedDerivedBlock<I, PostgreStatement._PostgreNestedJoinClause<I>>
            implements PostgreStatement._NestedParensJoinSpec<I> {

        private NestedDerivedJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                       @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedJoinBlock

    private static final class NestedDerivedCrossBlock<I extends Item>
            extends NestedDerivedBlock<I, PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedParensCrossSpec<I> {

        private NestedDerivedCrossBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                        @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedCrossBlock

    private static final class NestedDerivedOnBlock<I extends Item>
            extends NestedDerivedBlock<I, PostgreStatement._NestedOnSpec<I>>
            implements PostgreStatement._NestedParensOnSpec<I> {

        private NestedDerivedOnBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                     @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedOnBlock


}
