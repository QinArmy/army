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

abstract class PostgreDynamicJoins extends JoinableClause.DynamicJoinableBlock<
        PostgreStatement._DynamicTableSampleJoinSpec,
        Statement._AsClause<PostgreStatement._DynamicParensJoinSpec>,
        PostgreStatement._DynamicJoinSpec,
        PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._DynamicJoinSpec>,
        PostgreStatement._DynamicTableSampleOnSpec,
        Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec>,
        Statement._OnClause<PostgreStatement._DynamicJoinSpec>,
        PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>,
        PostgreStatement._DynamicJoinSpec>
        implements PostgreStatement._DynamicJoinSpec {

    static PostgreJoins joinBuilder(CriteriaContext context, _JoinType joinTyp
            , Consumer<_TabularBlock> blockConsumer) {
        return new PostgreJoinBuilder(context, joinTyp, blockConsumer);
    }

    static PostgreCrosses crossBuilder(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
        return new PostgreCrossBuilder(context, blockConsumer);
    }


    private PostgreDynamicJoins(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
        super(context, blockConsumer, joinType, modifier, tabularItem, alias);
    }

    @Override
    public final PostgreStatement._DynamicJoinSpec crossJoin(Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._DynamicJoinSpec>, PostgreStatement._DynamicJoinSpec> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final Statement._OnClause<PostgreStatement._DynamicJoinSpec> leftJoin(Function<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<PostgreStatement._DynamicJoinSpec> join(Function<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<PostgreStatement._DynamicJoinSpec> rightJoin(Function<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<PostgreStatement._DynamicJoinSpec> fullJoin(Function<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function) {
        return function.apply(PostgreNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
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
    boolean isIllegalTableModifier(@Nullable SQLs.TableModifier modifier) {
        return CriteriaUtils.isIllegalOnly(modifier);
    }

    @Override
    boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final PostgreStatement._DynamicTableSampleJoinSpec onFromTable(
            _JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String alias) {
        final DynamicTableJoinBlock block;
        block = new DynamicTableJoinBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<PostgreStatement._DynamicParensJoinSpec> onFromDerived(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final DynamicDerivedJoinBlock block;
            block = new DynamicDerivedJoinBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._DynamicJoinSpec> onFromUndoneFunc(
            final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
        return alias -> {
            final Function<PostgreUtils.DoneFunc, PostgreStatement._DynamicJoinSpec> function;
            function = doneFunc -> {
                final DynamicDoneFuncBlock block;
                block = new DynamicDoneFuncBlock(this.context, this.blockConsumer, joinType, modifier,
                        doneFunc, alias);
                this.blockConsumer.accept(block);
                return block;
            };
            return PostgreUtils.undoneFunc(func, function);
        };
    }

    @Override
    final PostgreStatement._DynamicJoinSpec onFromCte(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        final PostgreDynamicBlock block;
        block = new PostgreDynamicBlock(this.context, this.blockConsumer, joinType, modifier, cteItem, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final PostgreStatement._DynamicTableSampleOnSpec onJoinTable(
            _JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String alias) {
        final DynamicTableOnBlock block;
        block = new DynamicTableOnBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec> onJoinDerived(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final DynamicDerivedOnBlock block;
            block = new DynamicDerivedOnBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> onJoinUndoneFunc(
            final _JoinType joinType, final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
        return alias -> {
            final Function<PostgreUtils.DoneFunc, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function;
            function = doneFunc -> {
                final DynamicDoneFuncBlock block;
                block = new DynamicDoneFuncBlock(this.context, this.blockConsumer, joinType, modifier,
                        doneFunc, alias);
                this.blockConsumer.accept(block);
                return block;
            };
            return PostgreUtils.undoneFunc(func, function);
        };
    }

    @Override
    final Statement._OnClause<PostgreStatement._DynamicJoinSpec> onJoinCte(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        final PostgreDynamicBlock block;
        block = new PostgreDynamicBlock(this.context, this.blockConsumer, joinType, modifier, cteItem, alias);
        this.blockConsumer.accept(block);
        return block;
    }




    /*-------------------below private method-------------------*/

    private Statement._OnClause<PostgreStatement._DynamicJoinSpec> joinNestedEnd(final _JoinType joinType,
                                                                                 final _NestedItems nestedItems) {
        final PostgreDynamicBlock block;
        block = new PostgreDynamicBlock(this.context, this.blockConsumer, joinType, null, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }

    private PostgreStatement._DynamicJoinSpec fromNestedEnd(final _JoinType joinType,
                                                            final _NestedItems nestedItems) {
        assert joinType == _JoinType.CROSS_JOIN;
        final PostgreDynamicBlock block;
        block = new PostgreDynamicBlock(this.context, this.blockConsumer, joinType, null, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }


    private static class PostgreDynamicBlock extends PostgreDynamicJoins {

        private PostgreDynamicBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                    @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
        }


    }//PostgreDynamicBlock


    @SuppressWarnings("unchecked")
    private static abstract class DynamicTableBlock<TR, RR> extends PostgreDynamicBlock
            implements PostgreStatement._TableSampleClause<TR>,
            PostgreStatement._RepeatableClause<RR>,
            _PostgreTableBlock {

        private ArmyExpression sampleMethod;

        private ArmyExpression seed;

        private DynamicTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                  @Nullable SQLWords modifier, TableMeta<?> table, String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
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


    }//DynamicTableBlock

    private static final class DynamicTableJoinBlock extends DynamicTableBlock<
            PostgreStatement._DynamicTableRepeatableJoinSpec,
            PostgreStatement._DynamicJoinSpec>
            implements PostgreStatement._DynamicTableSampleJoinSpec,
            PostgreStatement._DynamicTableRepeatableJoinSpec {

        private DynamicTableJoinBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                      _JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table,
                                      String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
        }

    }//DynamicTableJoinBlock

    private static final class DynamicTableOnBlock extends DynamicTableBlock<
            PostgreStatement._DynamicRepeatableOnSpec,
            Statement._OnClause<PostgreStatement._DynamicJoinSpec>>
            implements PostgreStatement._DynamicTableSampleOnSpec,
            PostgreStatement._DynamicRepeatableOnSpec {

        private DynamicTableOnBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                    _JoinType joinType, @Nullable SQLWords modifier, TableMeta<?> table,
                                    String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
        }

    }//DynamicTableOnBlock


    @SuppressWarnings("unchecked")
    private static abstract class DynamicDerivedBlock<R> extends PostgreDynamicBlock
            implements Statement._OptionalParensStringClause<R>,
            _ModifierTabularBlock, _AliasDerivedBlock {

        private List<String> columnAliasList;

        private _SelectionMap selectionMap;

        private DynamicDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                    @Nullable SQLWords modifier, DerivedTable table, String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
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


    }//DynamicDerivedBlock

    private static final class DynamicDerivedJoinBlock
            extends DynamicDerivedBlock<PostgreStatement._DynamicJoinSpec>
            implements PostgreStatement._DynamicParensJoinSpec {

        private DynamicDerivedJoinBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                        _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                        String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
        }


    }//DynamicDerivedJoinBlock

    private static final class DynamicDerivedOnBlock
            extends DynamicDerivedBlock<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>
            implements Statement._ParensOnSpec<PostgreStatement._DynamicJoinSpec> {

        private DynamicDerivedOnBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                      _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                      String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
        }

    }//DynamicDerivedOnBlock


    private static final class DynamicDoneFuncBlock extends PostgreDynamicBlock implements _DoneFuncBlock {

        private final List<_FunctionField> fieldList;

        private final Map<String, _FunctionField> fieldMap;

        private DynamicDoneFuncBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                     @Nullable SQLs.DerivedModifier modifier, PostgreUtils.DoneFunc func,
                                     String alias) {
            super(context, blockConsumer, joinType, modifier, func.funcItem, alias);
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

    }//DynamicDoneFuncBlock


    private static final class PostgreJoinBuilder extends JoinableClause.DynamicBuilderSupport<
            PostgreStatement._DynamicTableSampleOnSpec,
            Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec>,
            Statement._OnClause<PostgreStatement._DynamicJoinSpec>,
            PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>>
            implements PostgreJoins {

        private PostgreJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TabularBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }


        @Override
        public Statement._OnClause<PostgreStatement._DynamicJoinSpec> space(
                Function<PostgreStatement._NestedLeftParenSpec<Statement._OnClause<PostgreStatement._DynamicJoinSpec>>, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function) {
            this.checkStart();
            return function.apply(PostgreNestedJoins.nestedItem(this.context, this.joinType, this::nestedEnd));
        }

        @Override
        boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }

        @Override
        boolean isIllegalTableModifier(@Nullable SQLs.TableModifier modifier) {
            return CriteriaUtils.isIllegalOnly(modifier);
        }

        @Override
        PostgreStatement._DynamicTableSampleOnSpec onTable(
                @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final DynamicTableOnBlock block;
            block = new DynamicTableOnBlock(this.context, this.blockConsumer, this.joinType, modifier, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsParensOnClause<PostgreStatement._DynamicJoinSpec> onDerived(
                @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final DynamicDerivedOnBlock block;
                block = new DynamicDerivedOnBlock(this.context, this.blockConsumer, this.joinType, modifier, table,
                        alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        PostgreStatement._FuncColumnDefinitionAsClause<Statement._OnClause<PostgreStatement._DynamicJoinSpec>> onUndoneFunc(
                final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
            return alias -> {
                final Function<PostgreUtils.DoneFunc, Statement._OnClause<PostgreStatement._DynamicJoinSpec>> function;
                function = doneFunc -> {
                    final DynamicDoneFuncBlock block;
                    block = new DynamicDoneFuncBlock(this.context, this.blockConsumer, this.joinType, modifier,
                            doneFunc, alias);
                    this.blockConsumer.accept(block);
                    return block;
                };
                return PostgreUtils.undoneFunc(func, function);
            };
        }

        @Override
        Statement._OnClause<PostgreStatement._DynamicJoinSpec> onCte(
                _Cte cteItem, String alias) {
            final PostgreDynamicBlock block;
            block = new PostgreDynamicBlock(this.context, this.blockConsumer, this.joinType, null,
                    cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }


        private Statement._OnClause<PostgreStatement._DynamicJoinSpec> nestedEnd(_JoinType joinType, _NestedItems items) {
            final PostgreDynamicBlock block;
            block = new PostgreDynamicBlock(this.context, this.blockConsumer, joinType, null,
                    items, "");
            this.blockConsumer.accept(block);
            return block;
        }


    }//PostgreJoinBuilder

    private static final class PostgreCrossBuilder extends JoinableClause.DynamicBuilderSupport<
            PostgreStatement._DynamicTableSampleJoinSpec,
            Statement._AsClause<PostgreStatement._DynamicJoinSpec>,
            PostgreStatement._DynamicJoinSpec,
            PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._DynamicJoinSpec>>
            implements PostgreCrosses {

        private PostgreCrossBuilder(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public PostgreStatement._DynamicJoinSpec space(
                Function<PostgreStatement._NestedLeftParenSpec<PostgreStatement._DynamicJoinSpec>, PostgreStatement._DynamicJoinSpec> function) {
            this.checkStart();
            return function.apply(PostgreNestedJoins.nestedItem(this.context, this.joinType, this::nestedEnd));
        }


        @Override
        boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }

        @Override
        boolean isIllegalTableModifier(@Nullable SQLs.TableModifier modifier) {
            return CriteriaUtils.isIllegalOnly(modifier);
        }

        @Override
        PostgreStatement._DynamicTableSampleJoinSpec onTable(
                @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final DynamicTableJoinBlock block;
            block = new DynamicTableJoinBlock(this.context, this.blockConsumer, this.joinType, modifier, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<PostgreStatement._DynamicJoinSpec> onDerived(
                @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final DynamicDerivedJoinBlock block;
                block = new DynamicDerivedJoinBlock(this.context, this.blockConsumer, this.joinType, modifier, table,
                        alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        PostgreStatement._DynamicJoinSpec onCte(_Cte cteItem, String alias) {
            final PostgreDynamicBlock block;
            block = new PostgreDynamicBlock(this.context, this.blockConsumer, this.joinType, null,
                    cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        PostgreStatement._FuncColumnDefinitionAsClause<PostgreStatement._DynamicJoinSpec> onUndoneFunc(
                final @Nullable SQLs.DerivedModifier modifier, final UndoneFunction func) {
            return alias -> {
                final Function<PostgreUtils.DoneFunc, PostgreStatement._DynamicJoinSpec> function;
                function = doneFunc -> {
                    final DynamicDoneFuncBlock block;
                    block = new DynamicDoneFuncBlock(this.context, this.blockConsumer, this.joinType, modifier,
                            doneFunc, alias);
                    this.blockConsumer.accept(block);
                    return block;
                };
                return PostgreUtils.undoneFunc(func, function);
            };
        }

        private PostgreStatement._DynamicJoinSpec nestedEnd(_JoinType joinType, _NestedItems items) {
            final PostgreDynamicBlock block;
            block = new PostgreDynamicBlock(this.context, this.blockConsumer, joinType, null,
                    items, "");
            this.blockConsumer.accept(block);
            return block;
        }


    }//PostgreCrossBuilder


}
