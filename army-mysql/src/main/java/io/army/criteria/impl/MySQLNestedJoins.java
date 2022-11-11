package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._TableItemGroup;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCrosses;
import io.army.criteria.mysql.MySQLJoins;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold MySQL nested join implementations
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLNestedJoins {

    private MySQLNestedJoins() {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> MySQLQuery._NestedLeftParenSpec<I> nestedItem(CriteriaContext context
            , _JoinType joinType, BiFunction<_JoinType, NestedItems, I> function) {
        return new MySQLNestedLeftParenClause<>(context, joinType, function);
    }


    private static final class MySQLNestedLeftParenClause<I extends Item>
            implements MySQLQuery._NestedLeftParenSpec<I>, _TableItemGroup
            , NestedItems {

        private final CriteriaContext context;

        private final _JoinType joinType;

        private final BiFunction<_JoinType, NestedItems, I> function;

        private List<_TableBlock> blockList = new ArrayList<>();

        private MySQLNestedLeftParenClause(CriteriaContext context, _JoinType joinType
                , BiFunction<_JoinType, NestedItems, I> function) {
            this.context = context;
            this.joinType = joinType;
            this.function = function;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<MySQLQuery._MySQLNestedJoinClause<I>> leftParen() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedIndexHintJoinSpec<I> leftParen(TableMeta<?> table, SQLsSyntax.WordAs wordAs
                , String tableAlias) {
            assert wordAs == SQLs.AS;

            final NestedIndexHintJoinClause<I> block;
            block = new NestedIndexHintJoinClause<>(this.context, this::onAddTableBlock
                    , _JoinType.NONE, null, table, tableAlias, this::thisNestedJoinEnd);

            this.onAddTableBlock(block);
            return block;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<MySQLQuery._MySQLNestedJoinClause<I>> leftParen(final Supplier<T> supplier) {
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final Statement._AsClause<MySQLQuery._MySQLNestedJoinClause<I>> asClause;
            asClause = alias -> {
                final MySQLNestedJoinClause<I> block;
                block = new MySQLNestedJoinClause<>(this.context, this::onAddTableBlock
                        , _JoinType.NONE, null, tabularItem, alias, this::thisNestedJoinEnd);
                this.onAddTableBlock(block);
                return block;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> Statement._AsClause<MySQLQuery._MySQLNestedJoinClause<I>> leftParen(
                final Query.TabularModifier modifier, final Supplier<T> supplier) {
            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final Statement._AsClause<MySQLQuery._MySQLNestedJoinClause<I>> asClause;
            asClause = alias -> {
                final MySQLNestedJoinClause<I> block;
                block = new MySQLNestedJoinClause<>(this.context, this::onAddTableBlock
                        , _JoinType.NONE, modifier, tabularItem, alias, this::thisNestedJoinEnd);
                this.onAddTableBlock(block);
                return block;
            };
            return asClause;
        }

        @Override
        public MySQLQuery._MySQLNestedJoinClause<I> leftParen(String cteName) {
            final CriteriaContext context = this.context;
            final MySQLNestedJoinClause<I> block;
            block = new MySQLNestedJoinClause<>(context, this::onAddTableBlock
                    , _JoinType.NONE, null, context.refCte(cteName), "", this::thisNestedJoinEnd);
            this.onAddTableBlock(block);
            return block;
        }

        @Override
        public MySQLQuery._MySQLNestedJoinClause<I> leftParen(String cteName, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;

            final CriteriaContext context = this.context;
            if (!_StringUtils.hasText(alias)) {
                throw ContextStack.criteriaError(context, _Exceptions::cteNameNotText);
            }

            final MySQLNestedJoinClause<I> block;
            block = new MySQLNestedJoinClause<>(context, this::onAddTableBlock
                    , _JoinType.NONE, null, context.refCte(cteName), alias, this::thisNestedJoinEnd);
            this.onAddTableBlock(block);
            return block;
        }

        @Override
        public MySQLQuery._NestedPartitionJoinSpec<I> leftParen(TableMeta<?> table) {
            return new PartitionJoinClause<>(this.context, this::onAddTableBlock
                    , _JoinType.NONE, table, this::thisNestedJoinEnd);
        }


        @Override
        public List<? extends _TableBlock> tableGroup() {
            final List<_TableBlock> blockList = this.blockList;
            if (blockList == null || blockList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return blockList;
        }

        private void onAddTableBlock(final _TableBlock block) {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add(block);
        }

        private MySQLQuery._MySQLNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType
                , final NestedItems nestedItems) {
            if (joinType != _JoinType.NONE) {
                throw _Exceptions.unexpectedEnum(joinType);
            }
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() == 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add((_TableBlock) nestedItems);
            return new MySQLNestedJoinClause<>(this.context, this::onAddTableBlock
                    , joinType, null, nestedItems, "", this::thisNestedJoinEnd);
        }

        private I thisNestedJoinEnd() {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() > 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.blockList = Collections.unmodifiableList(blockList);
            return this.function.apply(this.joinType, this);
        }


    }//NestedLeftParenClause


    private static class MySQLNestedJoinClause<I extends Item>
            extends JoinableClause.NestedJoinClause<
            MySQLQuery._NestedIndexHintCrossSpec<I>,
            MySQLQuery._NestedJoinSpec<I>,
            MySQLQuery._NestedJoinSpec<I>,
            MySQLQuery._NestedIndexHintOnSpec<I>,
            MySQLQuery._NestedOnSpec<I>,
            MySQLQuery._NestedOnSpec<I>,
            MySQLQuery._NestedJoinSpec<I>>
            implements MySQLQuery._NestedOnSpec<I>
            , _MySQLTableBlock {

        private final List<String> partitionList;

        private final Supplier<I> supplier;

        private MySQLNestedJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias, Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.supplier = supplier;
            this.partitionList = Collections.emptyList();
        }

        private MySQLNestedJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params);
            this.supplier = supplier;
            this.partitionList = params.partitionList();
        }


        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> leftJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.LEFT_JOIN, table, this.supplier);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> join(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.JOIN, table, this.supplier);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> rightJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer
                    , _JoinType.RIGHT_JOIN, table, this.supplier);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> fullJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.FULL_JOIN, table, this.supplier);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> straightJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer
                    , _JoinType.STRAIGHT_JOIN, table, this.supplier);
        }

        @Override
        public final MySQLQuery._NestedPartitionCrossSpec<I> crossJoin(TableMeta<?> table) {
            return new PartitionCrossClause<>(this.context, this.blockConsumer
                    , _JoinType.CROSS_JOIN, table, this.supplier);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> leftJoin() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> join() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> rightJoin() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> fullJoin() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> straightJoin() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.STRAIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedJoinSpec<I>> crossJoin() {
            return new MySQLNestedLeftParenClause<>(this.context, _JoinType.CROSS_JOIN, this::nestedCrossJoinEnd);
        }

        @Override
        public final MySQLQuery._NestedJoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final MySQLQuery._NestedJoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final MySQLQuery._NestedJoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final MySQLQuery._NestedJoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final MySQLQuery._NestedJoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public final MySQLQuery._NestedJoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
            consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        public final I rightParen() {
            return this.supplier.get();
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
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
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new TableBlock.NoOnModifierTableBlock(joinType, modifier, tableItem, alias);
        }

        @Override
        final MySQLQuery._NestedIndexHintOnSpec<I> createTableBlock(_JoinType joinType
                , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new NestedIndexHintOnClause<>(this.context, this.blockConsumer
                    , joinType, table, tableAlias, this.supplier);
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new MySQLNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, modifier, tableItem, alias, this.supplier);
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new MySQLNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, null, tableItem, alias, this.supplier);
        }

        /**
         * @see #leftJoin()
         * @see #join()
         * @see #rightJoin()
         * @see #fullJoin()
         * @see #straightJoin()
         */
        private MySQLQuery._NestedOnSpec<I> nestedJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {
            joinType.assertMySQLJoinType();
            final MySQLNestedJoinClause<I> block;
            block = new MySQLNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, null, nestedItems, "", this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private MySQLQuery._NestedJoinSpec<I> nestedCrossJoinEnd(final _JoinType joinType, final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final MySQLNestedJoinClause<I> block;
            block = new MySQLNestedJoinClause<>(this.context, this.blockConsumer
                    , _JoinType.CROSS_JOIN, null, items, "", this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//MySQLNestedJoinClause


    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link NestedIndexHintJoinClause}</li>
     *         <li>{@link NestedIndexHintOnClause}</li>
     *         <li>{@link NestedIndexHintCrossClause}</li>
     *     </ul>
     * </p>
     */
    private static abstract class MySQLNestedIndexHintClause<I extends Item, RR> extends MySQLNestedJoinClause<I>
            implements MySQLQuery._QueryIndexHintClause<RR> {

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryIndexHintClause<RR> hintClause;

        private MySQLNestedIndexHintClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }

        private MySQLNestedIndexHintClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> useIndex() {
            return this.getHintClause().useIndex();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> ignoreIndex() {
            return this.getHintClause().ignoreIndex();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<RR> forceIndex() {
            return this.getHintClause().forceIndex();
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = Collections.emptyList();
                this.indexHintList = indexHintList;
            } else if (indexHintList instanceof ArrayList) {
                indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return indexHintList;
        }

        private MySQLQuery._QueryIndexHintClause<RR> getHintClause() {
            MySQLQuery._QueryIndexHintClause<RR> clause = this.hintClause;
            if (clause == null) {
                clause = MySQLSupports.indexHintClause(this.context, this::onAddIndexHint);
                this.hintClause = clause;
            }
            return clause;
        }

        @SuppressWarnings("unchecked")
        private RR onAddIndexHint(final MySQLIndexHint indexHint) {
            return (RR) this;
        }


    }//MySQLNestedIndexHintClause


    /**
     * @see MySQLNestedLeftParenClause
     * @see PartitionJoinClause
     */
    private static final class NestedIndexHintJoinClause<I extends Item>
            extends MySQLNestedIndexHintClause<I, MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedIndexHintJoinSpec<I> {

        /**
         * @see MySQLNestedLeftParenClause#leftParen(TableMeta, SQLsSyntax.WordAs, String)
         */
        private NestedIndexHintJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }

        /**
         * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedIndexHintJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }


    }//NestedIndexHintJoinClause

    /**
     * @see PartitionCrossClause
     */
    private static final class NestedIndexHintCrossClause<I extends Item>
            extends MySQLNestedIndexHintClause<I, MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedIndexHintCrossSpec<I> {

        /**
         * @see PartitionCrossClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedIndexHintCrossClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }


    }//NestedIndexHintCrossClause


    /**
     * @see MySQLNestedJoinClause
     * @see PartitionOnClause
     */
    private static final class NestedIndexHintOnClause<I extends Item>
            extends MySQLNestedIndexHintClause<I, MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedIndexHintOnSpec<I> {

        /**
         * @see MySQLNestedJoinClause#createTableBlock(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private NestedIndexHintOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table
                , String alias, Supplier<I> supplier) {
            super(context, blockConsumer, joinType, null, table, alias, supplier);
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedIndexHintOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }


    }//NestedIndexHintOnClause


    /**
     * @see MySQLNestedLeftParenClause
     */
    private static final class PartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedPartitionJoinSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedLeftParenClause#leftParen(TableMeta)
         */
        private PartitionJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final NestedIndexHintJoinClause<I> block;
            block = new NestedIndexHintJoinClause<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionJoinClause

    /**
     * @see MySQLNestedJoinClause#crossJoin(TableMeta)
     */
    private static final class PartitionCrossClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedPartitionCrossSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedJoinClause#crossJoin(TableMeta)
         */
        private PartitionCrossClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintCrossSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final NestedIndexHintCrossClause<I> block;
            block = new NestedIndexHintCrossClause<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionJoinClause


    /**
     * @see MySQLNestedJoinClause
     */
    private static final class PartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedPartitionOnSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedJoinClause#leftJoin(TableMeta)
         * @see MySQLNestedJoinClause#join(TableMeta)
         * @see MySQLNestedJoinClause#rightJoin(TableMeta)
         * @see MySQLNestedJoinClause#fullJoin(TableMeta)
         * @see MySQLNestedJoinClause#straightJoin(TableMeta)
         */
        private PartitionOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final NestedIndexHintOnClause<I> block;
            block = new NestedIndexHintOnClause<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause


}
