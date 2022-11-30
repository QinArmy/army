package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class StandardDynamicJoins extends JoinableClause.DynamicJoinableBlock<
        StandardStatement._DynamicJoinSpec,
        Statement._AsClause<StandardStatement._DynamicJoinSpec>,
        Void,
        Statement._OnClause<StandardStatement._DynamicJoinSpec>,
        Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>>,
        Void>
        implements StandardStatement._DynamicJoinSpec {

    static StandardJoins joinBuilder(CriteriaContext context, _JoinType joinTyp
            , Consumer<_TableBlock> blockConsumer) {
        return new StandardJoinBuilder(context, joinTyp, blockConsumer);
    }

    static StandardCrosses crossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        return new StandardCrossesBuilder(context, blockConsumer);
    }


    private StandardDynamicJoins(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
        super(context, joinTyp, blockConsumer);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>> leftJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>> join() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>> rightJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<Statement._OnClause<StandardStatement._DynamicJoinSpec>> fullJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<StandardStatement._DynamicJoinSpec> crossJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd);
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
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.blockConsumer.accept(new TableBlock.NoOnTableBlock(joinType, table, alias));
        return this;
    }

    @Override
    final Statement._AsClause<StandardStatement._DynamicJoinSpec> onFromDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (table == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return alias -> {
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(joinType, table, alias));
            return this;
        };
    }


    @Override
    final Statement._OnClause<StandardStatement._DynamicJoinSpec> onJoinTable(
            _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
        block = new OnClauseTableBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> onJoinDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
        if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return alias -> {
            final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
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
        final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardStatement._DynamicJoinSpec crossNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        assert joinType == _JoinType.CROSS_JOIN;
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return this;
    }


    private static final class StandardJoinBuilder extends StandardDynamicJoins
            implements StandardJoins {

        private boolean started;

        private StandardJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }

        @Override
        public Statement._OnClause<StandardStatement._DynamicJoinSpec> tabular(TableMeta<?> table, SQLs.WordAs wordAs,
                                                                               String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public <T extends DerivedTable> Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final DerivedTable tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return alias -> {
                final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
                block = new OnClauseTableBlock<>(this.joinType, tabularItem, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }


    }//StandardJoinBuilder


    private static final class StandardCrossesBuilder extends StandardDynamicJoins
            implements StandardCrosses {

        private boolean started;

        private StandardCrossesBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }


        @Override
        public StandardStatement._DynamicJoinSpec tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(this.joinType, table, alias));
            return this;
        }

        @Override
        public <T extends DerivedTable> Statement._AsClause<StandardStatement._DynamicJoinSpec> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final DerivedTable tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final Statement._AsClause<StandardStatement._DynamicJoinSpec> asClause;
            asClause = alias -> {
                final TableBlock.NoOnTableBlock block;
                block = new TableBlock.NoOnTableBlock(this.joinType, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

    }//StandardCrossesBuilder


}
