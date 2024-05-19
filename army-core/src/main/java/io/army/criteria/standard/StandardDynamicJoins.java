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
import java.util.function.Consumer;
import java.util.function.Function;

abstract class StandardDynamicJoins extends JoinableClause.DynamicJoinableBlock<
        StandardStatement._DynamicJoinSpec,
        Statement._AsClause<StandardStatement._DynamicJoinSpec>,
        Void,
        Void,
        Statement._OnClause<StandardStatement._DynamicJoinSpec>,
        Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>>,
        Void,
        Void,
        StandardStatement._DynamicJoinSpec>
        implements StandardStatement._DynamicJoinSpec {

    static StandardJoins joinBuilder(CriteriaContext context, _JoinType joinTyp,
                                     Consumer<_TabularBlock> blockConsumer) {
        return new StandardJoinBuilder(context, joinTyp, blockConsumer);
    }

    static StandardCrosses crossBuilder(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
        return new StandardCrossesBuilder(context, blockConsumer);
    }


    private StandardDynamicJoins(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                 @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
        super(context, blockConsumer, joinType, modifier, tabularItem, alias);
    }

    @Override
    public final StandardStatement._DynamicJoinSpec crossJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._DynamicJoinSpec>, StandardStatement._DynamicJoinSpec> function) {
        return ClauseUtils.invokeFunction(function, StandardNestedJoins.nestedJoin(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> leftJoin(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return ClauseUtils.invokeFunction(function, StandardNestedJoins.nestedJoin(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> join(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return ClauseUtils.invokeFunction(function, StandardNestedJoins.nestedJoin(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> rightJoin(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return ClauseUtils.invokeFunction(function, StandardNestedJoins.nestedJoin(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> fullJoin(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return ClauseUtils.invokeFunction(function, StandardNestedJoins.nestedJoin(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifLeftJoin(Consumer<StandardJoins> consumer) {
        ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer), consumer);
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifJoin(Consumer<StandardJoins> consumer) {
        ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer), consumer);
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifRightJoin(Consumer<StandardJoins> consumer) {
        ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer), consumer);
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifFullJoin(Consumer<StandardJoins> consumer) {
        ClauseUtils.invokeConsumer(StandardDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer), consumer);
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifCrossJoin(Consumer<StandardCrosses> consumer) {
        ClauseUtils.invokeConsumer(StandardDynamicJoins.crossBuilder(this.context, this.blockConsumer), consumer);
        return this;
    }


    @Override
    final StandardStatement._DynamicJoinSpec onFromTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier,
                                                         TableMeta<?> table, String alias) {

        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<StandardStatement._DynamicJoinSpec> onFromDerived(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {

        return alias -> {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }


    @Override
    final Statement._OnClause<StandardStatement._DynamicJoinSpec> onJoinTable(
            _JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String alias) {
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> onJoinDerived(
            _JoinType joinType, @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final Void onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        throw ContextStack.clearStackAndCastCriteriaApi();
    }


    @Override
    final Void onJoinCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        throw ContextStack.clearStackAndCastCriteriaApi();
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }


    private Statement._OnClause<StandardStatement._DynamicJoinSpec> joinNestedEnd(final _JoinType joinType,
                                                                                  final _NestedItems nestedItems) {
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, null, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardStatement._DynamicJoinSpec crossNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, null, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }


    private static final class StandardDynamicBlock extends StandardDynamicJoins {

        private StandardDynamicBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                     @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
        }

    } // StandardDynamicBlock


    private static final class StandardJoinBuilder extends JoinableClause.DynamicBuilderSupport<
            Statement._OnClause<StandardStatement._DynamicJoinSpec>,
            Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>>,
            Item,
            Void>
            implements StandardJoins {


        private StandardJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TabularBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }


        @Override
        public Statement._OnClause<StandardStatement._DynamicJoinSpec> space(
                Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
            this.checkStart();
            return ClauseUtils.invokeFunction(function, StandardNestedJoins.nestedJoin(this.context, this.joinType, this::nestedEnd));
        }


        @Override
        Statement._OnClause<StandardStatement._DynamicJoinSpec> onTable(
                @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, modifier, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> onDerived(
                @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final StandardDynamicBlock block;
                block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Item onCte(_Cte cteItem, String alias) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        private Statement._OnClause<StandardStatement._DynamicJoinSpec> nestedEnd(_JoinType joinType, _NestedItems items) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, null, items, "");
            this.blockConsumer.accept(block);
            return block;
        }


        @Override
        boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }


    } //StandardJoinBuilder


    private static final class StandardCrossesBuilder extends JoinableClause.DynamicBuilderSupport<
            StandardStatement._DynamicJoinSpec,
            Statement._AsClause<StandardStatement._DynamicJoinSpec>,
            Item,
            Void>
            implements StandardCrosses {


        private StandardCrossesBuilder(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public StandardStatement._DynamicJoinSpec space(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._DynamicJoinSpec>, StandardStatement._DynamicJoinSpec> function) {
            this.checkStart();
            return function.apply(StandardNestedJoins.nestedJoin(this.context, this.joinType, this::nestedJoinEnd));
        }

        @Override
        StandardStatement._DynamicJoinSpec onTable(
                @Nullable SQLs.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, modifier, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<StandardStatement._DynamicJoinSpec> onDerived(
                @Nullable SQLs.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final StandardDynamicBlock block;
                block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Item onCte(_Cte cteItem, String alias) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        private StandardStatement._DynamicJoinSpec nestedJoinEnd(_JoinType joinType, _NestedItems items) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, null, items, "");
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }


    } // StandardCrossesBuilder


}
