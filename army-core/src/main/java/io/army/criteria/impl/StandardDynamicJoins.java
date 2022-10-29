package io.army.criteria.impl;

import io.army.criteria.NestedItems;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class StandardDynamicJoins extends JoinableClause.DynamicJoinClause<
        StandardStatement._DynamicJoinSpec,
        StandardStatement._DynamicJoinSpec,
        StandardStatement._DynamicJoinSpec,
        Statement._OnClause<StandardStatement._DynamicJoinSpec>,
        Statement._OnClause<StandardStatement._DynamicJoinSpec>,
        Statement._OnClause<StandardStatement._DynamicJoinSpec>>
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
    public final StandardStatement._NestedLeftParenSpec<_OnClause<StandardStatement._DynamicJoinSpec>> leftJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<_OnClause<StandardStatement._DynamicJoinSpec>> join() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<_OnClause<StandardStatement._DynamicJoinSpec>> rightJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<_OnClause<StandardStatement._DynamicJoinSpec>> fullJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final StandardStatement._NestedLeftParenSpec<StandardStatement._DynamicJoinSpec> crossJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedCrossEnd);
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
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, table, alias);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
    }

    @Override
    final _OnClause<StandardStatement._DynamicJoinSpec> createTableBlock(_JoinType joinType
            , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<StandardStatement._DynamicJoinSpec> createItemBlock(_JoinType joinType
            , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }

    @Override
    final _OnClause<StandardStatement._DynamicJoinSpec> createCteBlock(_JoinType joinType
            , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        //standard query don't support cte
        throw ContextStack.castCriteriaApi(this.context);
    }


    private Statement._OnClause<StandardStatement._DynamicJoinSpec> nestedJoinEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        joinType.assertStandardJoinType();
        final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    private StandardStatement._DynamicJoinSpec nestedCrossEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
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
        public _OnClause<StandardStatement._DynamicJoinSpec> tabular(TableMeta<?> table, SQLs.WordAs wordAs
                , String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            assert wordAs == SQLs.AS;
            final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public <T extends TabularItem> _AsClause<_OnClause<StandardStatement._DynamicJoinSpec>> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }

            final Statement._AsClause<Statement._OnClause<StandardStatement._DynamicJoinSpec>> asClause;
            asClause = alias -> {
                final OnClauseTableBlock<StandardStatement._DynamicJoinSpec> block;
                block = new OnClauseTableBlock<>(this.joinType, tabularItem, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
            return asClause;
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
            assert wordAs == SQLs.AS;
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(this.joinType, table, alias));
            return this;
        }

        @Override
        public <T extends TabularItem> _AsClause<StandardStatement._DynamicJoinSpec> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
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
