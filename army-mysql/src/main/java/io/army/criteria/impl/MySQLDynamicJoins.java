package io.army.criteria.impl;

import io.army.criteria.NestedItems;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.TabularItem;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.mysql.MySQLCrosses;
import io.army.criteria.mysql.MySQLJoins;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class MySQLDynamicJoins extends JoinableClause.DynamicJoinClause<
        MySQLQuery._DynamicIndexHintJoinClause,
        MySQLQuery._DynamicJoinSpec,
        MySQLQuery._DynamicJoinSpec,
        MySQLQuery._DynamicIndexHintOnClause,
        Statement._OnClause<MySQLQuery._DynamicJoinSpec>,
        Statement._OnClause<MySQLQuery._DynamicJoinSpec>>
        implements MySQLQuery._DynamicIndexHintJoinClause {

    static MySQLJoins joinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
        return new MySQLJoinBuilder(context, joinTyp, blockConsumer);
    }

    static MySQLCrosses crossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        return new MySQLCrossJoinBuilder(context, blockConsumer);
    }


    /**
     * <p>
     * Updated by below method:
     *     <ul>
     *         <li>{@link PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)}</li>
     *         <li>MySQLCrossJoinBuilder#tabular(TableMeta, SQLs.WordAs, String)</li>
     *         <li>{@link #createNoOnTableBlock(_JoinType, Query.TableModifier, TableMeta, String)}</li>
     *     </ul>
     * </p>
     */
    private MySQLSupports.MySQLNoOnBlock<MySQLQuery._DynamicIndexHintJoinClause> noOnBlock;

    private MySQLDynamicJoins(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
        super(context, joinTyp, blockConsumer);
    }


    @Override
    public final MySQLQuery._DynamicPartitionOnClause leftJoin(TableMeta<?> table) {
        return new PartitionOnClause(this, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause join(TableMeta<?> table) {
        return new PartitionOnClause(this, _JoinType.JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause rightJoin(TableMeta<?> table) {
        return new PartitionOnClause(this, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause fullJoin(TableMeta<?> table) {
        return new PartitionOnClause(this, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause straightJoin(TableMeta<?> table) {
        return new PartitionOnClause(this, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionJoinClause crossJoin(TableMeta<?> table) {
        return new PartitionJoinClause(this, _JoinType.CROSS_JOIN, table);
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> leftJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> join() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> rightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> fullJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> straightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._DynamicJoinSpec> crossJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedCrossEnd);
    }

    @Override
    public final MySQLQuery._DynamicJoinSpec ifLeftJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLQuery._DynamicJoinSpec ifJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLQuery._DynamicJoinSpec ifRightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public MySQLQuery._DynamicJoinSpec ifFullJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLQuery._DynamicJoinSpec ifStraightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLQuery._DynamicJoinSpec ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLQuery._IndexPurposeBySpec<MySQLQuery._DynamicIndexHintJoinClause> useIndex() {
        return this.getHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexPurposeBySpec<MySQLQuery._DynamicIndexHintJoinClause> ignoreIndex() {
        return this.getHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexPurposeBySpec<MySQLQuery._DynamicIndexHintJoinClause> forceIndex() {
        return this.getHintClause().forceIndex();
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
            , TableMeta<?> table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final MySQLSupports.MySQLNoOnBlock<MySQLQuery._DynamicIndexHintJoinClause> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, this);
        this.noOnBlock = block; //update noOnBlock
        return block;
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
    final MySQLQuery._DynamicIndexHintOnClause createTableBlock(_JoinType joinType
            , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new IndexHintOnBlock(joinType, table, tableAlias, this);
    }

    @Override
    final Statement._OnClause<MySQLQuery._DynamicJoinSpec> createItemBlock(_JoinType joinType
            , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        final OnClauseTableBlock<MySQLQuery._DynamicJoinSpec> block;
        if (modifier == null) {
            block = new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        } else if (modifier == SQLs.LATERAL) {
            block = new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        } else {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return block;
    }

    @Override
    final Statement._OnClause<MySQLQuery._DynamicJoinSpec> createCteBlock(_JoinType joinType
            , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        joinType.assertMySQLJoinType();
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }

    private Statement._OnClause<MySQLQuery._DynamicJoinSpec> nestedJoinEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        joinType.assertMySQLJoinType();
        final OnClauseTableBlock<MySQLQuery._DynamicJoinSpec> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }

    private MySQLQuery._DynamicJoinSpec nestedCrossEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        assert joinType == _JoinType.CROSS_JOIN;
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return this;
    }

    private MySQLQuery._QueryIndexHintClause<MySQLQuery._DynamicIndexHintJoinClause> getHintClause() {
        final MySQLSupports.MySQLNoOnBlock<MySQLQuery._DynamicIndexHintJoinClause> noOnBlock = this.noOnBlock;
        if (noOnBlock == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return noOnBlock.getUseIndexClause();
    }


    private static final class MySQLJoinBuilder extends MySQLDynamicJoins
            implements MySQLJoins {

        private boolean started;

        private MySQLJoinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TableBlock> blockConsumer) {
            super(context, joinTyp, blockConsumer);
        }

        @Override
        public MySQLQuery._DynamicIndexHintOnClause tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            assert wordAs == SQLs.AS;
            this.started = true;
            final IndexHintOnBlock block;
            block = new IndexHintOnBlock(this.joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public MySQLQuery._DynamicPartitionOnClause tabular(TableMeta<?> table) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            return new PartitionOnClause(this, this.joinType, table);
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }

            final Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> asClause;
            asClause = alias -> {
                final OnClauseTableBlock<MySQLQuery._DynamicJoinSpec> block;
                block = new OnClauseTableBlock<>(this.joinType, tabularItem, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> tabular(final @Nullable Query.TabularModifier modifier
                , Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }

            final Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> asClause;
            asClause = alias -> {
                final OnClauseTableBlock.OnItemTableBlock<MySQLQuery._DynamicJoinSpec> block;
                block = new OnClauseTableBlock.OnItemTableBlock<>(this.joinType, modifier, tabularItem, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
            return asClause;
        }

        @Override
        public Statement._OnClause<MySQLQuery._DynamicJoinSpec> tabular(String cteName) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final OnClauseTableBlock<MySQLQuery._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(this.joinType, this.context.refCte(cteName), "", this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        public Statement._OnClause<MySQLQuery._DynamicJoinSpec> tabular(String cteName, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            assert wordAs == SQLs.AS;
            this.started = true;
            final OnClauseTableBlock<MySQLQuery._DynamicJoinSpec> block;
            block = new OnClauseTableBlock<>(this.joinType, this.context.refCte(cteName), alias, this);
            this.blockConsumer.accept(block);
            return block;
        }


    }//MySQLJoinBuilder


    private static final class MySQLCrossJoinBuilder extends MySQLDynamicJoins
            implements MySQLCrosses, MySQLQuery._DynamicIndexHintJoinClause {

        private boolean started;

        private MySQLCrossJoinBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public MySQLQuery._DynamicIndexHintJoinClause tabular(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
            if (((MySQLDynamicJoins) this).noOnBlock != null || this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            assert wordAs == SQLs.AS;
            final MySQLSupports.MySQLNoOnBlock<MySQLQuery._DynamicIndexHintJoinClause> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(this.joinType, null, table, alias, this);
            this.blockConsumer.accept(block);
            ((MySQLDynamicJoins) this).noOnBlock = block; // update noOnBlock
            return this;
        }

        @Override
        public MySQLQuery._DynamicPartitionJoinClause tabular(TableMeta<?> table) {
            if (((MySQLDynamicJoins) this).noOnBlock != null || this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            return new PartitionJoinClause(this, this.joinType, table);
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<MySQLQuery._DynamicJoinSpec> tabular(Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            this.started = true;
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final Statement._AsClause<MySQLQuery._DynamicJoinSpec> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(this.joinType, null, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<MySQLQuery._DynamicJoinSpec> tabular(@Nullable Query.TabularModifier modifier
                , Supplier<T> supplier) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final Statement._AsClause<MySQLQuery._DynamicJoinSpec> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(this.joinType, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            this.started = true;
            return asClause;
        }

        @Override
        public MySQLQuery._DynamicJoinSpec tabular(String cteName) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            final TableBlock.NoOnModifierTableBlock block;
            block = new TableBlock.NoOnModifierTableBlock(this.joinType, null, this.context.refCte(cteName), "");
            this.blockConsumer.accept(block);
            this.started = true;
            return this;
        }

        @Override
        public MySQLQuery._DynamicJoinSpec tabular(String cteName, SQLs.WordAs wordAs, String alias) {
            if (this.started) {
                throw CriteriaUtils.duplicateTabularMethod(this.context);
            }
            assert wordAs == SQLs.AS;
            final TableBlock.NoOnModifierTableBlock block;
            block = new TableBlock.NoOnModifierTableBlock(this.joinType, null, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            this.started = true;
            return this;
        }


    }//MySQLCrossJoinBuilder


    /**
     * @see MySQLDynamicJoins
     * @see MySQLCrossJoinBuilder
     */
    private static final class PartitionJoinClause
            extends MySQLSupports.PartitionAsClause_0<MySQLQuery._DynamicIndexHintJoinClause>
            implements MySQLQuery._DynamicPartitionJoinClause {

        private final MySQLDynamicJoins joins;

        /**
         * @see MySQLDynamicJoins#crossJoin(TableMeta)
         * @see MySQLCrossJoinBuilder#tabular(TableMeta)
         */
        private PartitionJoinClause(MySQLDynamicJoins joins, _JoinType joinType, TableMeta<?> table) {
            super(joins.context, joinType, table);
            this.joins = joins;
        }

        @Override
        MySQLQuery._DynamicIndexHintJoinClause asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLDynamicJoins joins = this.joins;

            final MySQLSupports.MySQLNoOnBlock<MySQLQuery._DynamicIndexHintJoinClause> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, this.joins);
            joins.noOnBlock = block; // update noOnBlock
            joins.blockConsumer.accept(block);
            return joins;
        }

    }//PartitionJoinClause


    private static final class IndexHintOnBlock
            extends MySQLSupports.MySQLOnBlock<MySQLQuery._DynamicIndexHintOnClause, MySQLQuery._DynamicJoinSpec>
            implements MySQLQuery._DynamicIndexHintOnClause {

        /**
         * @see MySQLJoinBuilder#tabular(TableMeta, SQLs.WordAs, String)
         */
        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String alias, MySQLQuery._DynamicJoinSpec stmt) {
            super(joinType, null, table, alias, stmt);
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private IndexHintOnBlock(MySQLSupports.MySQLBlockParams params, MySQLQuery._DynamicJoinSpec stmt) {
            super(params, stmt);
        }


    }//IndexHintOnBlock


    private static final class PartitionOnClause
            extends MySQLSupports.PartitionAsClause_0<MySQLQuery._DynamicIndexHintOnClause>
            implements MySQLQuery._DynamicPartitionOnClause {

        private final MySQLDynamicJoins joins;

        /**
         * @see MySQLDynamicJoins#leftJoin(TableMeta)
         * @see MySQLDynamicJoins#join(TableMeta)
         * @see MySQLDynamicJoins#rightJoin(TableMeta)
         * @see MySQLDynamicJoins#fullJoin(TableMeta)
         * @see MySQLDynamicJoins#straightJoin(TableMeta)
         */
        private PartitionOnClause(MySQLDynamicJoins joins, _JoinType joinType, TableMeta<?> table) {
            super(joins.context, joinType, table);
            this.joins = joins;
        }

        @Override
        MySQLQuery._DynamicIndexHintOnClause asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLDynamicJoins joins = this.joins;

            final IndexHintOnBlock block;
            block = new IndexHintOnBlock(params, joins);
            joins.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause


}
