package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
            , Consumer<_TableBlock> blockConsumer) {
        return new StandardJoinBuilder(context, joinTyp, blockConsumer);
    }

    static StandardCrosses crossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        return new StandardCrossesBuilder(context, blockConsumer);
    }


    private StandardDynamicJoins(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
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

        return this.onAddTable(joinType, modifier, table, alias);
    }

    @Override
    final Statement._AsClause<StandardStatement._DynamicJoinSpec> onFromDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {

        return alias -> this.onAddDerived(joinType, modifier, table, alias);
    }


    @Override
    final Statement._OnClause<StandardStatement._DynamicJoinSpec> onJoinTable(
            _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        return this.onAddTable(joinType, modifier, table, alias);
    }

    @Override
    final Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> onJoinDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
        return alias -> this.onAddDerived(joinType, modifier, table, alias);
    }

    @Override
    final Void onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final Void onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    private Statement._OnClause<StandardStatement._DynamicJoinSpec> joinNestedEnd(final _JoinType joinType,
                                                                                  final NestedItems nestedItems) {
        joinType.assertStandardJoinType();
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardStatement._DynamicJoinSpec crossNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        assert joinType == _JoinType.CROSS_JOIN;
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardDynamicBlock onAddTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                            TableMeta<?> table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardDynamicBlock onAddDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                              @Nullable DerivedTable table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (table == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final StandardDynamicBlock block;
        block = new StandardDynamicBlock(this.context, this.blockConsumer, joinType, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    private static final class StandardDynamicBlock extends StandardDynamicJoins {

        private StandardDynamicBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                                     TabularItem table, String alias) {
            super(context, blockConsumer, joinType, table, alias);
        }


    }//StandardDynamicBlock


    private static abstract class StandardBuilderSupport extends DynamicBuilderSupport {

        boolean started;

        private StandardBuilderSupport(CriteriaContext context, _JoinType joinType,
                                       Consumer<_TableBlock> blockConsumer) {
            super(context, joinType, blockConsumer);
        }

        final StandardDynamicBlock onAddTable(TableMeta<?> table, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, table, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        final StandardDynamicBlock onAddDerived(@Nullable DerivedTable table, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final StandardDynamicBlock block;
            block = new StandardDynamicBlock(this.context, this.blockConsumer, this.joinType, table, alias);
            this.blockConsumer.accept(block);
            return block;
        }

    }//StandardBuilderSupport

    private static final class StandardJoinBuilder extends StandardBuilderSupport
            implements StandardJoins {


        private StandardJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }

        @Override
        public Statement._OnClause<StandardStatement._DynamicJoinSpec> tabular(TableMeta<?> table, SQLs.WordAs wordAs,
                                                                               String alias) {
            return this.onAddTable(table, alias);
        }

        @Override
        public <T extends DerivedTable> Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> tabular(Supplier<T> supplier) {
            return alias -> this.onAddDerived(supplier.get(), alias);
        }


    }//StandardJoinBuilder


    private static final class StandardCrossesBuilder extends StandardBuilderSupport
            implements StandardCrosses {


        private StandardCrossesBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public StandardStatement._DynamicJoinSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            return this.onAddTable(table, alias);
        }

        @Override
        public <T extends DerivedTable> Statement._AsClause<StandardStatement._DynamicJoinSpec> tabular(Supplier<T> supplier) {
            return alias -> this.onAddDerived(supplier.get(), alias);
        }

    }//StandardCrossesBuilder


}
