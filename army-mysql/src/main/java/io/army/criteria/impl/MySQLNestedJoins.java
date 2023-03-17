package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ModifierTableBlock;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCrosses;
import io.army.criteria.mysql.MySQLJoins;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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

    static <I extends Item> MySQLQuery._NestedLeftParenSpec<I> nestedItem(
            CriteriaContext context, _JoinType joinType, BiFunction<_JoinType, NestedItems, I> function) {
        return new MySQLNestedJoins<>(context, joinType, function);
    }


    private MySQLNestedJoins(CriteriaContext context, _JoinType joinType,
                             BiFunction<_JoinType, NestedItems, I> function) {
        super(context, joinType, function);
    }

    @Override
    public MySQLStatement._NestedIndexHintJoinSpec<I> leftParen(TableMeta<?> table, SQLs.WordAs wordAs, String alias) {
        final NestedTableJoinBlock<I> block;
        block = new NestedTableJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, table,
                alias, this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }


    @Override
    public <T extends DerivedTable> Statement._AsClause<MySQLStatement._NestedLeftParensJoinSpec<I>> leftParen(Supplier<T> supplier) {
        return alias -> this.onAddDerived(null, supplier, alias);
    }

    @Override
    public <T extends DerivedTable> Statement._AsClause<MySQLStatement._NestedLeftParensJoinSpec<I>> leftParen(
            @Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        if (modifier == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return alias -> this.onAddDerived(modifier, supplier, alias);
    }

    @Override
    public MySQLStatement._MySQLNestedJoinClause<I> leftParen(String cteName) {
        final MySQLNestedBlock<I> block;
        block = new MySQLNestedBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null,
                this.context.refCte(cteName), "", this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public MySQLStatement._MySQLNestedJoinClause<I> leftParen(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        final MySQLNestedBlock<I> block;
        block = new MySQLNestedBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, null,
                this.context.refCte(cteName), alias, this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
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

    private <T extends DerivedTable> NestedDerivedJoinBlock<I> onAddDerived(@Nullable Query.DerivedModifier modifier,
                                                                            Supplier<T> supplier, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.errorModifier(this.context, modifier);
        }
        final DerivedTable table;
        table = supplier.get();
        if (table == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final NestedDerivedJoinBlock<I> block;
        block = new NestedDerivedJoinBlock<>(this.context, this::onAddTableBlock, _JoinType.NONE, modifier, table, alias,
                this::thisNestedJoinEnd);
        this.onAddTableBlock(block);
        return block;
    }


    private MySQLQuery._MySQLNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType,
                                                                     final NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final MySQLNestedBlock<I> block;
        block = new MySQLNestedBlock<>(this.context, this::onAddTableBlock, joinType, null, nestedItems, "",
                this::thisNestedJoinEnd);

        this.onAddTableBlock(block);
        return block;
    }


    private static class MySQLNestedBlock<I extends Item>
            extends JoinableClause.NestedJoinableBlock<
            MySQLQuery._NestedIndexHintCrossSpec<I>,
            Statement._AsClause<MySQLStatement._NestedParenCrossSpec<I>>,
            MySQLQuery._NestedJoinSpec<I>,
            MySQLQuery._NestedIndexHintOnSpec<I>,
            Statement._AsClause<MySQLStatement._NestedParenOnSpec<I>>,
            MySQLQuery._NestedOnSpec<I>,
            MySQLQuery._NestedJoinSpec<I>>
            implements MySQLStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private MySQLNestedBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.ender = ender;
        }

        private MySQLNestedBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params);
            this.ender = supplier;
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
        public final MySQLStatement._NestedJoinSpec<I> crossJoin(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._NestedJoinSpec<I>>, MySQLStatement._NestedJoinSpec<I>> function) {
            return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::crossNestedEnd));
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> leftJoin(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._NestedOnSpec<I>>, MySQLStatement._NestedOnSpec<I>> function) {
            return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> join(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._NestedOnSpec<I>>, MySQLStatement._NestedOnSpec<I>> function) {
            return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd));
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> rightJoin(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._NestedOnSpec<I>>, MySQLStatement._NestedOnSpec<I>> function) {
            return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> fullJoin(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._NestedOnSpec<I>>, MySQLStatement._NestedOnSpec<I>> function) {
            return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
        }

        @Override
        public final MySQLStatement._NestedOnSpec<I> straightJoin(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._NestedOnSpec<I>>, MySQLStatement._NestedOnSpec<I>> function) {
            return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
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
        final MySQLQuery._NestedIndexHintCrossSpec<I> onFromTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            if (joinType != _JoinType.CROSS_JOIN || modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final NestedTableCrossBlock<I> block;
            block = new NestedTableCrossBlock<>(this.context, this.blockConsumer, table, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<MySQLStatement._NestedParenCrossSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.errorModifier(this.context, modifier);
            }
            return alias -> {
                final NestedDerivedCrossBlock<I> block;
                block = new NestedDerivedCrossBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }


        @Override
        final MySQLQuery._NestedJoinSpec<I> onFromCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, joinType, null, cteItem, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final MySQLQuery._NestedIndexHintOnSpec<I> onJoinTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final NestedTableOnBlock<I> block;
            block = new NestedTableOnBlock<>(this.context, this.blockConsumer, joinType, table, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<MySQLStatement._NestedParenOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.errorModifier(this.context, modifier);
            } else if (table == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return alias -> {
                final NestedDerivedOnBlock<I> block;
                block = new NestedDerivedOnBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, joinType, null, cteItem, alias,
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
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, joinType, null, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private MySQLQuery._NestedJoinSpec<I> crossNestedEnd(final _JoinType joinType, final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, null, items,
                    "", this.ender);
            this.blockConsumer.accept(block);
            return block;
        }


    }//MySQLNestedJoinBlock


    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link NestedTableJoinBlock}</li>
     *         <li>{@link NestedTableOnBlock}</li>
     *         <li>{@link NestedTableCrossBlock}</li>
     *     </ul>
     * </p>
     */
    private static abstract class MySQLTableBlock<I extends Item, RR> extends MySQLNestedBlock<I>
            implements MySQLQuery._QueryIndexHintClause<RR>, _MySQLTableBlock {

        private final List<String> partitionList;
        private List<MySQLIndexHint> indexHintList;

        private MySQLQuery._QueryIndexHintClause<RR> hintClause;

        private MySQLTableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, null, table, alias, ender);
            this.partitionList = Collections.emptyList();
        }

        private MySQLTableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
            this.partitionList = params.partitionList();
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
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null || indexHintList instanceof ArrayList) {
                indexHintList = _CollectionUtils.safeUnmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
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


    }//MySQLTableBlock


    /**
     * @see MySQLNestedJoins
     * @see PartitionJoinClause
     */
    private static final class NestedTableJoinBlock<I extends Item>
            extends MySQLTableBlock<I, MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedIndexHintJoinSpec<I> {

        /**
         * @see MySQLNestedJoins#leftParen(TableMeta, SQLsSyntax.WordAs, String)
         */
        private NestedTableJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                     _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, table, alias, ender);
        }

        /**
         * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedTableJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
        }


    }//NestedTableJoinBlock

    /**
     * @see PartitionCrossClause
     */
    private static final class NestedTableCrossBlock<I extends Item>
            extends MySQLTableBlock<I, MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedIndexHintCrossSpec<I> {

        /**
         * @see MySQLNestedBlock#onFromTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private NestedTableCrossBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                      TableMeta<?> table, String alias, Supplier<I> supplier) {
            super(context, blockConsumer, _JoinType.CROSS_JOIN, table, alias, supplier);
        }

        /**
         * @see PartitionCrossClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedTableCrossBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }


    }//NestedTableCrossBlock


    /**
     * @see MySQLNestedBlock
     * @see PartitionOnClause
     */
    private static final class NestedTableOnBlock<I extends Item>
            extends MySQLTableBlock<I, MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedIndexHintOnSpec<I> {

        /**
         * @see MySQLNestedBlock#onFromTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private NestedTableOnBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                   _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, table, alias, ender);
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private NestedTableOnBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
        }


    }//NestedTableOnBlock


    @SuppressWarnings("unchecked")
    private static abstract class NestedDerivedBlock<I extends Item, R> extends MySQLNestedBlock<I>
            implements Statement._ParensStringClause<R>,
            _ModifierTableBlock {

        private NestedDerivedBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                   _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                   String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }


        @Override
        public final R parens(String first, String... rest) {
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
            return (R) this;
        }

        @Override
        public final R parens(Consumer<Consumer<String>> consumer) {

            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(list);
            return (R) this;
        }

        @Override
        public final R ifParens(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            ((ArmyDerivedTable) this.tabularItem).setColumnAliasList(CriteriaUtils.optionalStringList(list));
            return (R) this;
        }

    }//NestedDerivedBlock


    private static final class NestedDerivedJoinBlock<I extends Item>
            extends NestedDerivedBlock<I, MySQLStatement._MySQLNestedJoinClause<I>>
            implements MySQLStatement._NestedLeftParensJoinSpec<I> {

        private NestedDerivedJoinBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                       _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                       String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedJoinBlock

    private static final class NestedDerivedCrossBlock<I extends Item>
            extends NestedDerivedBlock<I, MySQLStatement._NestedJoinSpec<I>>
            implements MySQLStatement._NestedParenCrossSpec<I> {

        private NestedDerivedCrossBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                        _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                        String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedCrossBlock


    private static final class NestedDerivedOnBlock<I extends Item>
            extends NestedDerivedBlock<I, MySQLStatement._NestedOnSpec<I>>
            implements MySQLStatement._NestedParenOnSpec<I> {

        private NestedDerivedOnBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                                     _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                     String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//NestedDerivedOnBlock


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

            final NestedTableJoinBlock<I> block;
            block = new NestedTableJoinBlock<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionJoinClause

    /**
     * @see MySQLNestedBlock#crossJoin(TableMeta)
     */
    private static final class PartitionCrossClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedPartitionCrossSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedBlock#crossJoin(TableMeta)
         */
        private PartitionCrossClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintCrossSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final NestedTableCrossBlock<I> block;
            block = new NestedTableCrossBlock<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionJoinClause


    /**
     * @see MySQLNestedBlock
     */
    private static final class PartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedPartitionOnSpec<I> {

        private final Consumer<_TableBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedBlock#leftJoin(TableMeta)
         * @see MySQLNestedBlock#join(TableMeta)
         * @see MySQLNestedBlock#rightJoin(TableMeta)
         * @see MySQLNestedBlock#fullJoin(TableMeta)
         * @see MySQLNestedBlock#straightJoin(TableMeta)
         */
        private PartitionOnClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final NestedTableOnBlock<I> block;
            block = new NestedTableOnBlock<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause


}
