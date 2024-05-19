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

package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.meta.TableMeta;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementation of standard nested join
 *
 * @see StandardQueries
 * @since 0.6.0
 */
final class StandardNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<
        I,
        StandardStatement._StandardNestedJoinClause<I>,
        Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>>,
        Object,
        Void>
        implements StandardStatement._NestedLeftParenSpec<I> {

    static <I extends Item> StandardStatement._NestedLeftParenSpec<I> nestedJoin(CriteriaContext context
            , _JoinType joinType, BiFunction<_JoinType, _NestedItems, I> function) {
        return new StandardNestedJoins<>(context, joinType, function);
    }

    private StandardNestedJoins(CriteriaContext context, _JoinType joinType, BiFunction<_JoinType, _NestedItems, I> function) {
        super(context, joinType, function);
    }


    @Override
    protected StandardStatement._StandardNestedJoinClause<I> onLeftTable(
            @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        final StandardNestedBlock<I> block;
        block = new StandardNestedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, modifier, table, tableAlias,
                this::thisNestedJoinEnd);
        this.onAddTabularBlock(block);
        return block;
    }

    @Override
    protected Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>> onLeftDerived(
            @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, modifier, table, alias,
                    this::thisNestedJoinEnd);
            this.onAddTabularBlock(block);
            return block;
        };
    }

    @Override
    protected Object onLeftCte(_Cte cteItem, String alias) {
        throw ContextStack.clearStackAndCastCriteriaApi();
    }


    @Override
    public StandardStatement._StandardNestedJoinClause<I> leftParen(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._StandardNestedJoinClause<I>>, StandardStatement._StandardNestedJoinClause<I>> function) {
        return ClauseUtils.invokeFunction(function, new StandardNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd));
    }


    @Override
    protected boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }


    private StandardStatement._StandardNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType,
                                                                               final _NestedItems nestedItems) {
        final StandardNestedBlock<I> clause;
        clause = new StandardNestedBlock<>(this.context, this::onAddTabularBlock, joinType, null, nestedItems, "",
                this::thisNestedJoinEnd);
        this.onAddTabularBlock(clause);
        return clause;
    }


    private static final class StandardNestedBlock<I extends Item> extends JoinableClause.NestedJoinableBlock<
            StandardStatement._NestedJoinSpec<I>,
            Statement._AsClause<StandardStatement._NestedJoinSpec<I>>,
            Void,
            Void,
            StandardStatement._NestedOnSpec<I>,
            Statement._AsClause<StandardStatement._NestedOnSpec<I>>,
            Void,
            Void,
            StandardStatement._NestedJoinSpec<I>>
            implements StandardStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private StandardNestedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                    @Nullable SQLWords modifier, TabularItem tabularItem, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.ender = ender;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> crossJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedJoinSpec<I>>, StandardStatement._NestedJoinSpec<I>> function) {
            return ClauseUtils.invokeFunction(function, new StandardNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> leftJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return ClauseUtils.invokeFunction(function, new StandardNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> join(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return ClauseUtils.invokeFunction(function, new StandardNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> rightJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return ClauseUtils.invokeFunction(function, new StandardNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> fullJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return ClauseUtils.invokeFunction(function, new StandardNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifLeftJoin(Consumer<StandardJoins> consumer) {
            ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer), consumer);
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifJoin(Consumer<StandardJoins> consumer) {
            ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer), consumer);
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifRightJoin(Consumer<StandardJoins> consumer) {
            ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer), consumer);
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifFullJoin(Consumer<StandardJoins> consumer) {
            ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer), consumer);
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifCrossJoin(Consumer<StandardCrosses> consumer) {
            ClauseUtils.invokeConsumer(StandardDynamicJoins.crossBuilder(this.context, this.blockConsumer), consumer);
            return this;
        }

        @Override
        public I rightParen() {
            return this.ender.get();
        }


        @Override
        protected StandardStatement._NestedJoinSpec<I> onFromTable(final _JoinType joinType, @Nullable SQLs.TableModifier modifier,
                                                                   final TableMeta<?> table, final String alias) {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        protected Statement._AsClause<StandardStatement._NestedJoinSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final StandardNestedBlock<I> block;
                block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        protected Void onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        @Override
        protected StandardStatement._NestedOnSpec<I> onJoinTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier,
                                                                 TableMeta<?> table, String alias) {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        protected Statement._AsClause<StandardStatement._NestedOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable item) {
            return alias -> {
                final StandardNestedBlock<I> block;
                block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, modifier, item, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        protected Void onJoinCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        /**
         * @see #leftJoin(Function)
         * @see #join(Function)
         * @see #rightJoin(Function)
         * @see #fullJoin(Function)
         */
        private StandardStatement._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, null, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin(Function)
         */
        private StandardStatement._NestedJoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, null, items, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }


    } // StandardNestedJoinClause


}
