package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCrosses;
import io.army.criteria.mysql.MySQLJoins;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

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
final class MySQLNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<I>
        implements MySQLQuery._NestedLeftParenSpec<I> {

    static <I extends Item> MySQLQuery._NestedLeftParenSpec<I> nestedItem(CriteriaContext context, _JoinType joinType,
                                                                          BiFunction<_JoinType, NestedItems, I> function) {
        return new MySQLNestedJoins<>(context, joinType, function);
    }


    private MySQLNestedJoins(CriteriaContext context, _JoinType joinType,
                             BiFunction<_JoinType, NestedItems, I> function) {
        super(context, joinType, function);
    }

    @Override
    public MySQLStatement._NestedIndexHintJoinSpec<I> leftParen(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        final NestedIndexHintJoinClause<I> block;
        block = new NestedIndexHintJoinClause<>(this.context, this::onAddTableBlock, _JoinType.NONE, table,
                tableAlias, this::thisNestedJoinEnd);
        this.onAddFirstBlock(block);
        return block;
    }


    @Override
    public <T extends DerivedTable> Statement._AsClause<MySQLStatement._NestedLeftParensJoinSpec<I>> leftParen(Supplier<T> supplier) {
        final DerivedTable table;
        table = supplier.get();
        if (table == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return alias -> {
            final MySQLNestedJoinBlock<I> block;
            block = new MySQLNestedJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null, table,
                    alias, this::thisNestedJoinEnd);
            this.onAddFirstBlock(block);
            return block;
        };
    }

    @Override
    public <T extends DerivedTable> Statement._AsClause<MySQLStatement._NestedLeftParensJoinSpec<I>> leftParen(
            Query.DerivedModifier modifier, Supplier<T> supplier) {
        if (modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final DerivedTable table;
        if ((table = supplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return alias -> {
            final MySQLNestedJoinBlock<I> block;
            block = new MySQLNestedJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, modifier, table,
                    alias, this::thisNestedJoinEnd);
            this.onAddFirstBlock(block);
            return block;
        };
    }

    @Override
    public MySQLStatement._MySQLNestedJoinClause<I> leftParen(String cteName) {
        final MySQLNestedJoinBlock<I> block;
        block = new MySQLNestedJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null,
                this.context.refCte(cteName), "", this::thisNestedJoinEnd);
        this.onAddFirstBlock(block);
        return block;
    }

    @Override
    public MySQLStatement._MySQLNestedJoinClause<I> leftParen(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        final MySQLNestedJoinBlock<I> block;
        block = new MySQLNestedJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null,
                this.context.refCte(cteName), alias, this::thisNestedJoinEnd);
        this.onAddFirstBlock(block);
        return block;
    }

    @Override
    public MySQLStatement._NestedPartitionJoinSpec<I> leftParen(TableMeta<?> table) {
        return new PartitionJoinClause<>(this.context, this::onAddTableBlock, _JoinType.NONE, table,
                this::thisNestedJoinEnd);
    }

    @Override
    public MySQLStatement._NestedLeftParenSpec<MySQLStatement._MySQLNestedJoinClause<I>> leftParen() {
        return new MySQLNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd);
    }


    private MySQLQuery._MySQLNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType,
                                                                     final NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final MySQLNestedJoinBlock<I> block;
        block = new MySQLNestedJoinBlock<>(this.context, this::onAddTableBlock, joinType, null, nestedItems, "",
                this::thisNestedJoinEnd);

        this.onAddFirstBlock(block);
        return block;
    }


    private static class MySQLNestedJoinBlock<I extends Item>
            extends JoinableClause.NestedJoinableBlock<
            MySQLQuery._NestedIndexHintCrossSpec<I>,
            Statement._AsClause<MySQLStatement._NestedParenJoinSpec<I>>,
            MySQLQuery._NestedJoinSpec<I>,
            MySQLQuery._NestedIndexHintOnSpec<I>,
            Statement._AsClause<MySQLStatement._NestedParenOnSpec<I>>,
            MySQLQuery._NestedOnSpec<I>,
            MySQLQuery._NestedJoinSpec<I>>
            implements MySQLStatement._NestedOnSpec<I>,
            MySQLStatement._NestedParenOnSpec<I>,
            _MySQLTableBlock {

        private final List<String> partitionList;

        private final Supplier<I> ender;

        private MySQLNestedJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias, Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.ender = supplier;
            this.partitionList = Collections.emptyList();
        }

        private MySQLNestedJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params);
            this.ender = supplier;
            this.partitionList = params.partitionList();
        }


        @Override
        public final MySQLStatement._NestedOnSpec<I> parens(String first, String... rest) {

            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(ArrayUtils.unmodifiableListOf(first, rest));
            return this;
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> parens(Consumer<Consumer<String>> consumer) {

            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(list);
            return this;
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> ifParens(Consumer<Consumer<String>> consumer) {

            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(list);
            } else {
                ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(CriteriaUtils.EMPTY_STRING_LIST);
            }
            return this;
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> leftJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.LEFT_JOIN, table, this.ender);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> join(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.JOIN, table, this.ender);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> rightJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.RIGHT_JOIN, table, this.ender);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> fullJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.FULL_JOIN, table, this.ender);
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> straightJoin(TableMeta<?> table) {
            return new PartitionOnClause<>(this.context, this.blockConsumer, _JoinType.STRAIGHT_JOIN, table, this.ender);
        }

        @Override
        public final MySQLQuery._NestedPartitionCrossSpec<I> crossJoin(TableMeta<?> table) {
            return new PartitionCrossClause<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, table, this.ender);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> leftJoin() {
            return new MySQLNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> join() {
            return new MySQLNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> rightJoin() {
            return new MySQLNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> fullJoin() {
            return new MySQLNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedOnSpec<I>> straightJoin() {
            return new MySQLNestedJoins<>(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<MySQLQuery._NestedJoinSpec<I>> crossJoin() {
            return new MySQLNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd);
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
            return this.ender.get();
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            final List<? extends _IndexHint> list;
            if (this instanceof MySQLNestedIndexHintClause) {
                list = ((MySQLNestedIndexHintClause<?, ?>) this).endIndexHintIfNeed();
            } else {
                list = Collections.emptyList();
            }
            return list;
        }


        @Override
        final MySQLQuery._NestedIndexHintCrossSpec<I> onFromTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            if (joinType != _JoinType.CROSS_JOIN || modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final NestedIndexHintCrossClause<I> block;
            block = new NestedIndexHintCrossClause<>(this.context, this.blockConsumer, table, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<MySQLStatement._NestedParenJoinSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return alias -> {
                final MySQLNestedJoinBlock<I> block;
                block = new MySQLNestedJoinBlock<>(this.context, this.blockConsumer, joinType, null, table, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final MySQLQuery._NestedJoinSpec<I> onFromCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            return this.onJoinCte(joinType, modifier, cteItem, alias);
        }

        @Override
        final MySQLQuery._NestedIndexHintOnSpec<I> onJoinTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final NestedIndexHintOnClause<I> block;
            block = new NestedIndexHintOnClause<>(this.context, this.blockConsumer, joinType, table, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<MySQLStatement._NestedParenOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            } else if (table == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return alias -> {
                final MySQLNestedJoinBlock<I> block;
                block = new MySQLNestedJoinBlock<>(this.context, this.blockConsumer, joinType, modifier, table, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final MySQLNestedJoinBlock<I> block;
            block = new MySQLNestedJoinBlock<>(this.context, this.blockConsumer, joinType, null, cteItem, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #leftJoin()
         * @see #join()
         * @see #rightJoin()
         * @see #fullJoin()
         * @see #straightJoin()
         */
        private MySQLQuery._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
            joinType.assertMySQLJoinType();
            final MySQLNestedJoinBlock<I> block;
            block = new MySQLNestedJoinBlock<>(this.context, this.blockConsumer, joinType, null, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private MySQLQuery._NestedJoinSpec<I> crossNestedEnd(final _JoinType joinType, final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final MySQLNestedJoinBlock<I> block;
            block = new MySQLNestedJoinBlock<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, null, items,
                    "", this.ender);
            this.blockConsumer.accept(block);
            return block;
        }


    }//MySQLNestedJoinBlock


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
    private static abstract class MySQLNestedIndexHintClause<I extends Item, RR> extends MySQLNestedJoinBlock<I>
            implements MySQLQuery._QueryIndexHintClause<RR> {

        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryIndexHintClause<RR> hintClause;

        private MySQLNestedIndexHintClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                           _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, null, table, alias, ender);
        }

        private MySQLNestedIndexHintClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
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


        private MySQLQuery._QueryIndexHintClause<RR> getHintClause() {
            MySQLQuery._QueryIndexHintClause<RR> clause = this.hintClause;
            if (clause == null) {
                clause = MySQLSupports.indexHintClause(this.context, this::onAddIndexHint);
                this.hintClause = clause;
            }
            return clause;
        }

        @SuppressWarnings("unchecked")
        private RR onAddIndexHint(final @Nullable MySQLIndexHint indexHint) {
            if (indexHint == null) {
                return (RR) this;
            }
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            }
            indexHintList.add(indexHint);
            return (RR) this;
        }

        private List<MySQLIndexHint> endIndexHintIfNeed() {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null || indexHintList instanceof ArrayList) {
                indexHintList = _CollectionUtils.safeUnmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }


    }//MySQLNestedIndexHintClause


    /**
     * @see MySQLNestedJoins
     * @see PartitionJoinClause
     */
    private static final class NestedIndexHintJoinClause<I extends Item>
            extends MySQLNestedIndexHintClause<I, MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedIndexHintJoinSpec<I> {

        /**
         * @see MySQLNestedJoins#leftParen(TableMeta, SQLsSyntax.WordAs, String)
         */
        private NestedIndexHintJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                          _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, table, alias, ender);
        }

        /**
         * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedIndexHintJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
        }


    }//NestedIndexHintJoinClause

    /**
     * @see PartitionCrossClause
     */
    private static final class NestedIndexHintCrossClause<I extends Item>
            extends MySQLNestedIndexHintClause<I, MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedIndexHintCrossSpec<I> {

        /**
         * @see MySQLNestedJoinBlock#onFromTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private NestedIndexHintCrossClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                           TableMeta<?> table, String alias, Supplier<I> supplier) {
            super(context, blockConsumer, _JoinType.CROSS_JOIN, table, alias, supplier);
        }

        /**
         * @see PartitionCrossClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedIndexHintCrossClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }


    }//NestedIndexHintCrossClause


    /**
     * @see MySQLNestedJoinBlock
     * @see PartitionOnClause
     */
    private static final class NestedIndexHintOnClause<I extends Item>
            extends MySQLNestedIndexHintClause<I, MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedIndexHintOnSpec<I> {

        /**
         * @see MySQLNestedJoinBlock#createTableBlock(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private NestedIndexHintOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                        _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, table, alias, ender);
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedIndexHintOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
        }


    }//NestedIndexHintOnClause


    /**
     * @see MySQLNestedJoins
     */
    private static final class PartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedPartitionJoinSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedJoins#leftParen(TableMeta)
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
     * @see MySQLNestedJoinBlock#crossJoin(TableMeta)
     */
    private static final class PartitionCrossClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedPartitionCrossSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedJoinBlock#crossJoin(TableMeta)
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
     * @see MySQLNestedJoinBlock
     */
    private static final class PartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedPartitionOnSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedJoinBlock#leftJoin(TableMeta)
         * @see MySQLNestedJoinBlock#join(TableMeta)
         * @see MySQLNestedJoinBlock#rightJoin(TableMeta)
         * @see MySQLNestedJoinBlock#fullJoin(TableMeta)
         * @see MySQLNestedJoinBlock#straightJoin(TableMeta)
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
