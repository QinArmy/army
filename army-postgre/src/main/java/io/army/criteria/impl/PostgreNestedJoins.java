/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreTableBlock;
import io.army.criteria.postgre.PostgreCrosses;
import io.army.criteria.postgre.PostgreJoins;
import io.army.criteria.postgre.PostgreStatement;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class hold the implementation of Postgre nested join.
 *
 * @since 0.6.0
 */

final class PostgreNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<
        I,
        PostgreStatement._NestedTableSampleJoinSpec<I>,
        Statement._AsClause<PostgreStatement._NestedParensJoinSpec<I>>,
        PostgreStatement._PostgreNestedJoinClause<I>,
        PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._PostgreNestedJoinClause<I>>>
        implements PostgreStatement._NestedLeftParenSpec<I> {


    static <I extends Item> PostgreStatement._NestedLeftParenSpec<I> nestedItem(
            CriteriaContext context, _JoinType joinType, BiFunction<_JoinType, _NestedItems, I> function) {
        return new PostgreNestedJoins<>(context, joinType, function);
    }


    private PostgreNestedJoins(CriteriaContext context, _JoinType joinType,
                               BiFunction<_JoinType, _NestedItems, I> function) {
        super(context, joinType, function);
    }

    @Override
    public PostgreStatement._PostgreNestedJoinClause<I> leftParen(
            Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._PostgreNestedJoinClause<I>>, PostgreStatement._PostgreNestedJoinClause<I>> function) {
        return function.apply(new PostgreNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd));
    }

    @Override
    boolean isIllegalTableModifier(@Nullable SQLs.TableModifier modifier) {
        return CriteriaUtils.isIllegalOnly(modifier);
    }

    @Override
    boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    PostgreStatement._NestedTableSampleJoinSpec<I> onLeftTable(
            @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        final NestedTableJoinBlock<I> block;
        block = new NestedTableJoinBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, modifier, table, tableAlias,
                this::thisNestedJoinEnd);
        this.onAddTabularBlock(block);
        return block;
    }

    @Override
    Statement._AsClause<PostgreStatement._NestedParensJoinSpec<I>> onLeftDerived(
            @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final NestedDerivedJoinBlock<I> block;
            block = new NestedDerivedJoinBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, modifier,
                    table, alias, this::thisNestedJoinEnd);
            this.onAddTabularBlock(block);
            return block;
        };
    }

    @Override
    PostgreStatement._PostgreNestedJoinClause<I> onLeftCte(_Cte cteItem, String alias) {
        final PostgreNestedBlock<I> block;
        block = new PostgreNestedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, null,
                cteItem, alias, this::thisNestedJoinEnd);
        this.onAddTabularBlock(block);
        return block;
    }

    @Override
    PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._PostgreNestedJoinClause<I>> onLeftUndoneFunc(
            final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
        return alias -> {
            final Function<PostgreUtils.DoneFunc, PostgreStatement._PostgreNestedJoinClause<I>> function;
            function = doneFunc -> {
                NestedDoneFuncBlock<I> block;
                block = new NestedDoneFuncBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, modifier,
                        doneFunc, alias, this::thisNestedJoinEnd);
                this.onAddTabularBlock(block);
                return block;
            };
            return PostgreUtils.undoneFunc(func, function);
        };
    }


    private PostgreStatement._PostgreNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType,
                                                                             final _NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final PostgreNestedBlock<I> clause;
        clause = new PostgreNestedBlock<>(this.context, this::onAddTabularBlock, joinType, null, nestedItems, "",
                this::thisNestedJoinEnd);
        this.onAddTabularBlock(clause);
        return clause;
    }


    private static class PostgreNestedBlock<I extends Item> extends JoinableClause.NestedJoinableBlock<
            PostgreStatement._NestedTableSampleCrossSpec<I>,
            Statement._AsClause<PostgreStatement._NestedParensCrossSpec<I>>,
            PostgreStatement._NestedJoinSpec<I>,
            PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._NestedJoinSpec<I>>,
            PostgreStatement._NestedTableSampleOnSpec<I>,
            Statement._AsClause<PostgreStatement._NestedParensOnSpec<I>>,
            PostgreStatement._NestedOnSpec<I>,
            PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._NestedOnSpec<I>>,
            PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private PostgreNestedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                   @Nullable SQLWords modifier, TabularItem tabularItem, String alias,
                                   Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.ender = ender;
        }

        @Override
        public final PostgreStatement._NestedJoinSpec<I> crossJoin(Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedJoinSpec<I>>, PostgreStatement._NestedJoinSpec<I>> function) {
            return function.apply(new PostgreNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd));
        }

        @Override
        public final PostgreStatement._NestedOnSpec<I> leftJoin(Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>>, PostgreStatement._NestedOnSpec<I>> function) {
            return function.apply(new PostgreNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
        }

        @Override
        public final PostgreStatement._NestedOnSpec<I> join(Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>>, PostgreStatement._NestedOnSpec<I>> function) {
            return function.apply(new PostgreNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd));
        }

        @Override
        public final PostgreStatement._NestedOnSpec<I> rightJoin(Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>>, PostgreStatement._NestedOnSpec<I>> function) {
            return function.apply(new PostgreNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
        }

        @Override
        public final PostgreStatement._NestedOnSpec<I> fullJoin(Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._NestedOnSpec<I>>, PostgreStatement._NestedOnSpec<I>> function) {
            return function.apply(new PostgreNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
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
        boolean isIllegalTableModifier(@Nullable SQLs.TableModifier modifier) {
            return CriteriaUtils.isIllegalOnly(modifier);
        }

        @Override
        boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }

        @Override
        final PostgreStatement._NestedTableSampleCrossSpec<I> onFromTable(_JoinType joinType,
                                                                          @Nullable SQLs.TableModifier modifier,
                                                                          TableMeta<?> table, String alias) {
            final NestedTableCrossBlock<I> block;
            block = new NestedTableCrossBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<PostgreStatement._NestedParensCrossSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final NestedDerivedCrossBlock<I> block;
                block = new NestedDerivedCrossBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._NestedJoinSpec<I>> onFromUndoneFunc(
                final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier,
                final UndoneFunction func) {
            return alias -> {
                final Function<PostgreUtils.DoneFunc, PostgreStatement._NestedJoinSpec<I>> function;
                function = doneFunc -> {
                    final NestedDoneFuncBlock<I> block;
                    block = new NestedDoneFuncBlock<>(this.context, this.blockConsumer, joinType, modifier,
                            doneFunc, alias, this.ender);
                    this.blockConsumer.accept(block);
                    return block;
                };
                return PostgreUtils.undoneFunc(func, function);
            };
        }

        @Override
        final PostgreStatement._NestedJoinSpec<I> onFromCte(_JoinType joinType,
                                                            @Nullable SQLs.DerivedModifier modifier,
                                                            _Cte cteItem, String alias) {
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, cteItem, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final PostgreStatement._NestedTableSampleOnSpec<I> onJoinTable(_JoinType joinType,
                                                                       @Nullable SQLs.TableModifier modifier,
                                                                       TableMeta<?> table, String alias) {
            final NestedTableOnBlock<I> block;
            block = new NestedTableOnBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<PostgreStatement._NestedParensOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
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
                                                          @Nullable SQLs.DerivedModifier modifier,
                                                          _Cte cteItem, String alias) {
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, cteItem, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._NestedOnSpec<I>> onJoinUndoneFunc(
                final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier,
                final UndoneFunction func) {
            return alias -> {
                final Function<PostgreUtils.DoneFunc, PostgreStatement._NestedOnSpec<I>> function;
                function = doneFunc -> {
                    final NestedDoneFuncBlock<I> block;
                    block = new NestedDoneFuncBlock<>(this.context, this.blockConsumer, joinType, modifier,
                            doneFunc, alias, this.ender);
                    this.blockConsumer.accept(block);
                    return block;
                };
                return PostgreUtils.undoneFunc(func, function);
            };
        }

        /**
         * @see #leftJoin(Function)
         * @see #join(Function)
         * @see #rightJoin(Function)
         * @see #fullJoin(Function)
         */
        private PostgreStatement._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType,
                                                                final _NestedItems nestedItems) {
            final PostgreNestedBlock<I> block;
            block = new PostgreNestedBlock<>(this.context, this.blockConsumer, joinType, null, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin(Function)
         */
        private PostgreStatement._NestedJoinSpec<I> crossNestedEnd(final _JoinType joinType, final _NestedItems items) {
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

        private NestedTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                 @Nullable SQLWords modifier, TableMeta<?> table, String alias,
                                 Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

        @Override
        public final TR tableSample(final @Nullable Expression method) {
            if (this.sampleMethod != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (method == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.sampleMethod = (ArmyExpression) method;
            return (TR) this;
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
        public final TR ifTableSample(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.tableSample(expression);
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
            implements PostgreStatement._NestedTableSampleJoinSpec<I>,
            PostgreStatement._NestedRepeatableJoinClause<I> {

        private NestedTableJoinBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                     @Nullable SQLWords modifier, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }


    }//NestedTableJoinBlock

    private static final class NestedTableCrossBlock<I extends Item> extends NestedTableBlock<
            I,
            PostgreStatement._NestedRepeatableCrossClause<I>,
            PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedTableSampleCrossSpec<I>,
            PostgreStatement._NestedRepeatableCrossClause<I> {

        private NestedTableCrossBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                      @Nullable SQLWords modifier, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }


    }//NestedTableCrossBlock

    private static final class NestedTableOnBlock<I extends Item> extends NestedTableBlock<
            I,
            PostgreStatement._NestedRepeatableOnClause<I>,
            PostgreStatement._NestedOnSpec<I>>
            implements PostgreStatement._NestedTableSampleOnSpec<I>,
            PostgreStatement._NestedRepeatableOnClause<I> {

        private NestedTableOnBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                   @Nullable SQLWords modifier, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedTableOnBlock


    @SuppressWarnings("unchecked")
    private static abstract class NestedDerivedBlock<I extends Item, R> extends PostgreNestedBlock<I>
            implements Statement._OptionalParensStringClause<R>,
            _ModifierTabularBlock,
            _AliasDerivedBlock {

        private List<String> columnAliasList;

        private _SelectionMap selectionMap;

        private NestedDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                   @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
            this.selectionMap = (_DerivedTable) table;
        }

        @Override
        public final R parens(String first, String... rest) {
            return this.onColumnAlias(ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public final R parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.context, true, consumer));
        }

        @Override
        public final R ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.context, false, consumer));
        }

        @Override
        public final Selection refSelection(String name) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refSelection(name);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refAllSelection();
        }

        @Override
        public final List<String> columnAliasList() {
            List<String> list = this.columnAliasList;
            if (list == null) {
                list = Collections.emptyList();
                this.columnAliasList = list;
            }
            return list;
        }

        private R onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.columnAliasList = columnAliasList;
            this.selectionMap = CriteriaUtils.createAliasSelectionMap(columnAliasList,
                    ((_DerivedTable) this.tabularItem).refAllSelection(),
                    this.alias);
            return (R) this;
        }


    }//NestedDerivedBlock

    private static final class NestedDerivedJoinBlock<I extends Item>
            extends NestedDerivedBlock<I, PostgreStatement._PostgreNestedJoinClause<I>>
            implements PostgreStatement._NestedParensJoinSpec<I> {

        private NestedDerivedJoinBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                       @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedJoinBlock

    private static final class NestedDerivedCrossBlock<I extends Item>
            extends NestedDerivedBlock<I, PostgreStatement._NestedJoinSpec<I>>
            implements PostgreStatement._NestedParensCrossSpec<I> {

        private NestedDerivedCrossBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                        @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedCrossBlock

    private static final class NestedDerivedOnBlock<I extends Item>
            extends NestedDerivedBlock<I, PostgreStatement._NestedOnSpec<I>>
            implements PostgreStatement._NestedParensOnSpec<I> {

        private NestedDerivedOnBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                     @Nullable SQLWords modifier, DerivedTable table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedOnBlock

    private static final class NestedDoneFuncBlock<I extends Item> extends PostgreNestedBlock<I>
            implements _DoneFuncBlock {

        private final List<_FunctionField> fieldList;

        private final Map<String, _FunctionField> fieldMap;

        private NestedDoneFuncBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                    @Nullable SQLs.DerivedModifier modifier, PostgreUtils.DoneFunc func,
                                    String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, func.funcItem, alias, ender);
            this.fieldList = func.fieldList;
            this.fieldMap = func.fieldMap;
        }

        @Override
        public Selection refSelection(String name) {
            return this.fieldMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.fieldList;
        }

        @Override
        public List<_FunctionField> fieldList() {
            return this.fieldList;
        }


    }//NestedDoneFuncBlock


}
