package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class StandardDynamicJoins extends JoinableClause.DynamicJoinableBlock<
        StandardStatement._DynamicJoinSpec,
        Statement._AsClause<StandardStatement._DynamicJoinSpec>,
        Void,
        Statement._OnClause<StandardStatement._DynamicJoinSpec>,
        Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>>,
        Void,
        StandardStatement._DynamicJoinSpec>
        implements StandardStatement._DynamicJoinSpec {

    static StandardJoins joinBuilder(CriteriaContext context, _JoinType joinTyp
            , Consumer<_TabularBock> blockConsumer) {
        return new StandardJoinBuilder(context, joinTyp, blockConsumer);
    }

    static StandardCrosses crossBuilder(CriteriaContext context, Consumer<_TabularBock> blockConsumer) {
        return new StandardCrossesBuilder(context, blockConsumer);
    }


    private StandardDynamicJoins(CriteriaContext context, Consumer<_TabularBock> blockConsumer, _JoinType joinType,
                                 TabularItem tabularItem, String alias) {
        super(context, blockConsumer, joinType, null, tabularItem, alias);
    }

    @Override
    public final StandardStatement._DynamicJoinSpec crossJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._DynamicJoinSpec>, StandardStatement._DynamicJoinSpec> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> leftJoin(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> join(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> rightJoin(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<StandardStatement._DynamicJoinSpec> fullJoin(Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifLeftJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifRightJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifFullJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final StandardStatement._DynamicJoinSpec ifCrossJoin(Consumer<StandardCrosses> consumer) {
        consumer.accept(StandardDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }


    @Override
    final StandardStatement._DynamicJoinSpec onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                         TableMeta<?> table, String alias) {

        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<StandardStatement._DynamicJoinSpec> onFromDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {

        return alias -> {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }


    @Override
    final Statement._OnClause<StandardStatement._DynamicJoinSpec> onJoinTable(
            _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> onJoinDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final Void onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final Void onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    private Statement._OnClause<StandardStatement._DynamicJoinSpec> joinNestedEnd(final _JoinType joinType,
                                                                                  final _NestedItems nestedItems) {
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardStatement._DynamicJoinSpec crossNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }


    private static final class StandardDynamicBlock extends StandardDynamicJoins {

        private StandardDynamicBlock(CriteriaContext context, Consumer<_TabularBock> blockConsumer, _JoinType joinType,
                                     TabularItem table, String alias) {
            super(context, blockConsumer, joinType, table, alias);
        }


    }//StandardDynamicBlock


    private static final class StandardJoinBuilder extends JoinableClause.DynamicBuilderSupport<
            Statement._OnClause<StandardStatement._DynamicJoinSpec>,
            Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>>,
            Item>
            implements StandardJoins {


        private StandardJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TabularBock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }


        @Override
        public Statement._OnClause<StandardStatement._DynamicJoinSpec> tabular(
                Function<StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>>, Statement._OnClause<StandardStatement._DynamicJoinSpec>> function) {
            this.checkStart();
            return function.apply(StandardNestedJoins.nestedJoin(this.context, this.joinType, this::nestedEnd));
        }


        @Override
        Statement._OnClause<StandardStatement._DynamicJoinSpec> onTable(
                @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> onDerived(
                @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final StandardDynamicBlock block;
                block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, table, alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Item onCte(_Cte cteItem, String alias) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        private Statement._OnClause<StandardStatement._DynamicJoinSpec> nestedEnd(_JoinType joinType, _NestedItems items) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, items, "");
            this.blockConsumer.accept(block);
            return block;
        }

    }//StandardJoinBuilder


    private static final class StandardCrossesBuilder extends JoinableClause.DynamicBuilderSupport<
            StandardStatement._DynamicJoinSpec,
            Statement._AsClause<StandardStatement._DynamicJoinSpec>,
            Item>
            implements StandardCrosses {


        private StandardCrossesBuilder(CriteriaContext context, Consumer<_TabularBock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public StandardStatement._DynamicJoinSpec tabular(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._DynamicJoinSpec>, StandardStatement._DynamicJoinSpec> function) {
            this.checkStart();
            return function.apply(StandardNestedJoins.nestedJoin(this.context, this.joinType, this::nestedJoinEnd));
        }

        @Override
        StandardStatement._DynamicJoinSpec onTable(
                @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<StandardStatement._DynamicJoinSpec> onDerived(
                @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final StandardDynamicBlock block;
                block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, table, alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Item onCte(_Cte cteItem, String alias) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        private StandardStatement._DynamicJoinSpec nestedJoinEnd(_JoinType joinType, _NestedItems items) {
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, items, "");
            this.blockConsumer.accept(block);
            return block;
        }


    }//StandardCrossesBuilder


}
