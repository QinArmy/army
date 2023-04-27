package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCrosses;
import io.army.criteria.mysql.MySQLJoins;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Collections;
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
final class MySQLNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<
        I,
        MySQLStatement._NestedIndexHintJoinSpec<I>,
        Statement._AsClause<MySQLStatement._NestedLeftParensJoinSpec<I>>,
        MySQLStatement._MySQLNestedJoinClause<I>,
        Void>
        implements MySQLQuery._NestedLeftParenSpec<I> {

    static <I extends Item> MySQLQuery._NestedLeftParenSpec<I> nestedItem(
            CriteriaContext context, _JoinType joinType, BiFunction<_JoinType, _NestedItems, I> function) {
        return new MySQLNestedJoins<>(context, joinType, function);
    }


    private MySQLNestedJoins(CriteriaContext context, _JoinType joinType,
                             BiFunction<_JoinType, _NestedItems, I> function) {
        super(context, joinType, function);
    }


    @Override
    public MySQLStatement._NestedPartitionJoinSpec<I> leftParen(TableMeta<?> table) {
        return new PartitionJoinClause<>(this.context, this::onAddTabularBlock, _JoinType.NONE, table,
                this::thisNestedJoinEnd);
    }

    @Override
    public MySQLStatement._MySQLNestedJoinClause<I> leftParen(
            Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._MySQLNestedJoinClause<I>>, MySQLStatement._MySQLNestedJoinClause<I>> function) {
        return function.apply(new MySQLNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd));
    }

    @Override
    boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    MySQLStatement._NestedIndexHintJoinSpec<I> onLeftTable(
            @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        final FromClauseTableBlock<I> block;
        block = new FromClauseTableBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, table,
                tableAlias, this::thisNestedJoinEnd);
        this.onAddTabularBlock(block);
        return block;
    }

    @Override
    Statement._AsClause<MySQLStatement._NestedLeftParensJoinSpec<I>> onLeftDerived(
            @Nullable Query.DerivedModifier modifier, DerivedTable table) {


        return alias -> {
            final FromClauseDerivedBlock<I> block;
            block = new FromClauseDerivedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, modifier, table, alias,
                    this::thisNestedJoinEnd);
            this.onAddTabularBlock(block);
            return block;
        };
    }

    @Override
    MySQLStatement._MySQLNestedJoinClause<I> onLeftCte(_Cte cteItem, String alias) {
        final MySQLNestedBlock<I> block;
        block = new MySQLNestedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, null,
                cteItem, alias, this::thisNestedJoinEnd);
        this.onAddTabularBlock(block);
        return block;
    }

    private MySQLQuery._MySQLNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType,
                                                                     final _NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final MySQLNestedBlock<I> block;
        block = new MySQLNestedBlock<>(this.context, this::onAddTabularBlock, joinType, null, nestedItems, "",
                this::thisNestedJoinEnd);

        this.onAddTabularBlock(block);
        return block;
    }


    private static class MySQLNestedBlock<I extends Item>
            extends JoinableClause.NestedJoinableBlock<
            MySQLQuery._NestedIndexHintCrossSpec<I>,
            Statement._AsClause<MySQLStatement._NestedParenCrossSpec<I>>,
            MySQLQuery._NestedJoinSpec<I>,
            Void,
            MySQLQuery._NestedIndexHintOnSpec<I>,
            Statement._AsClause<MySQLStatement._NestedParenOnSpec<I>>,
            MySQLQuery._NestedOnSpec<I>,
            Void,
            MySQLQuery._NestedJoinSpec<I>>
            implements MySQLStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private MySQLNestedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.ender = ender;
        }

        private MySQLNestedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
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
        final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }

        @Override
        final MySQLQuery._NestedIndexHintCrossSpec<I> onFromTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            final CrossClauseTableBlock<I> block;
            block = new CrossClauseTableBlock<>(this.context, this.blockConsumer, table, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<MySQLStatement._NestedParenCrossSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final CrossClauseDerivedBlock<I> block;
                block = new CrossClauseDerivedBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }


        @Override
        final MySQLQuery._NestedJoinSpec<I> onFromCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, joinType, null, cteItem, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final MySQLQuery._NestedIndexHintOnSpec<I> onJoinTable(
                _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
            final JoinClauseTableBlock<I> block;
            block = new JoinClauseTableBlock<>(this.context, this.blockConsumer, joinType, table, alias, this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        final Statement._AsClause<MySQLStatement._NestedParenOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final JoinClauseDerivedBlock<I> block;
                block = new JoinClauseDerivedBlock<>(this.context, this.blockConsumer, joinType, modifier, table,
                        alias, this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> onJoinCte(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, joinType, null, cteItem, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #leftJoin(Function)
         * @see #join(Function)
         * @see #rightJoin(Function)
         * @see #fullJoin(Function)
         * @see #straightJoin(Function)
         */
        private MySQLQuery._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
            final MySQLNestedBlock<I> block;
            block = new MySQLNestedBlock<>(this.context, this.blockConsumer, joinType, null, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin(Function)
         */
        private MySQLQuery._NestedJoinSpec<I> crossNestedEnd(final _JoinType joinType, final _NestedItems items) {
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
     *         <li>{@link FromClauseTableBlock}</li>
     *         <li>{@link JoinClauseTableBlock}</li>
     *         <li>{@link CrossClauseTableBlock}</li>
     *     </ul>
     * </p>
     */
    @SuppressWarnings("unchecked")
    private static abstract class MySQLNestedTableBlock<I extends Item, R extends Item> extends MySQLNestedBlock<I>
            implements MySQLStatement._QueryIndexHintSpec<R>,
            MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexPurposeBySpec<Object>, R>,
            _MySQLTableBlock {

        private final List<String> partitionList;
        private List<_IndexHint> indexHintList;


        private MySQLNestedTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                      _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, null, table, alias, ender);
            this.partitionList = Collections.emptyList();
        }

        private MySQLNestedTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
            this.partitionList = params.partitionList();
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<R> useIndex() {
            return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                    this::indexHintEnd);
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<R> ignoreIndex() {
            return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                    this::indexHintEnd);
        }

        @Override
        public final MySQLQuery._IndexPurposeBySpec<R> forceIndex() {
            return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.FORCE_INDEX,
                    this::indexHintEnd);
        }

        @Override
        public final R ifUseIndex(Consumer<MySQLStatement._IndexPurposeBySpec<Object>> consumer) {
            consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                    this::indexHintEndAndReturnObject));
            return (R) this;
        }

        @Override
        public final R ifIgnoreIndex(Consumer<MySQLStatement._IndexPurposeBySpec<Object>> consumer) {
            consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                    this::indexHintEndAndReturnObject));
            return (R) this;
        }

        @Override
        public final R ifForceIndex(Consumer<MySQLStatement._IndexPurposeBySpec<Object>> consumer) {
            consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.FORCE_INDEX,
                    this::indexHintEndAndReturnObject));
            return (R) this;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            List<_IndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null || indexHintList instanceof ArrayList) {
                indexHintList = _Collections.safeUnmodifiableList(indexHintList);
                this.indexHintList = indexHintList;
            }
            return indexHintList;
        }


        private R indexHintEnd(final _IndexHint indexHint) {

            List<_IndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            }
            indexHintList.add(indexHint);
            return (R) this;
        }

        private Object indexHintEndAndReturnObject(final _IndexHint indexHint) {
            this.indexHintEnd(indexHint);
            return Collections.EMPTY_LIST;
        }


    }//MySQLTableBlock


    /**
     * @see MySQLNestedJoins
     * @see PartitionJoinClause
     */
    private static final class FromClauseTableBlock<I extends Item>
            extends MySQLNestedTableBlock<I, MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedIndexHintJoinSpec<I> {

        /**
         * @see MySQLNestedJoins#leftParen(TableMeta, SQLsSyntax.WordAs, String)
         */
        private FromClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, table, alias, ender);
        }

        /**
         * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private FromClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
        }


    }//FromClauseTableBlock

    /**
     * @see PartitionCrossClause
     */
    private static final class CrossClauseTableBlock<I extends Item>
            extends MySQLNestedTableBlock<I, MySQLQuery._NestedIndexHintCrossSpec<I>>
            implements MySQLQuery._NestedIndexHintCrossSpec<I> {

        /**
         * @see MySQLNestedBlock#onFromTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private CrossClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                      TableMeta<?> table, String alias, Supplier<I> supplier) {
            super(context, blockConsumer, _JoinType.CROSS_JOIN, table, alias, supplier);
        }

        /**
         * @see PartitionCrossClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private CrossClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                      MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params, supplier);
        }


    }//CrossClauseTableBlock


    /**
     * @see MySQLNestedBlock
     * @see PartitionOnClause
     */
    private static final class JoinClauseTableBlock<I extends Item>
            extends MySQLNestedTableBlock<I, MySQLQuery._NestedIndexHintOnSpec<I>>
            implements MySQLQuery._NestedIndexHintOnSpec<I> {

        /**
         * @see MySQLNestedBlock#onJoinTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private JoinClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     _JoinType joinType, TableMeta<?> table, String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, table, alias, ender);
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private JoinClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> ender) {
            super(context, blockConsumer, params, ender);
        }


    }//JoinClauseTableBlock


    @SuppressWarnings("unchecked")
    private static abstract class NestedDerivedBlock<I extends Item, R> extends MySQLNestedBlock<I>
            implements Statement._OptionalParensStringClause<R>,
            _ModifierTabularBlock, _AliasDerivedBlock {

        private List<String> columnAliasList;

        private _SelectionMap selectionMap;

        private NestedDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                   _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                   String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
            this.selectionMap = (_DerivedTable) table;
        }


        @Override
        public final R parens(String first, String... rest) {
            return this.onColumnAlias(_ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public final R parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.context, true, consumer));
        }

        @Override
        public final R ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.context, false, consumer));
        }

        @Override
        public final Selection refSelection(String name) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refSelection(name);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refAllSelection();
        }

        @Override
        public final List<String> columnAliasList() {
            List<String> list = this.columnAliasList;
            if (list == null) {
                list = Collections.emptyList();
                this.columnAliasList = list;
            }
            return list;
        }


        private R onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.columnAliasList = columnAliasList;
            this.selectionMap = CriteriaUtils.createAliasSelectionMap(columnAliasList, (_DerivedTable) this.tabularItem);
            return (R) this;
        }

    }//NestedDerivedBlock


    private static final class FromClauseDerivedBlock<I extends Item>
            extends NestedDerivedBlock<I, MySQLStatement._MySQLNestedJoinClause<I>>
            implements MySQLStatement._NestedLeftParensJoinSpec<I> {

        private FromClauseDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                       _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                       String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }


    }//FromClauseDerivedBlock

    private static final class CrossClauseDerivedBlock<I extends Item>
            extends NestedDerivedBlock<I, MySQLStatement._NestedJoinSpec<I>>
            implements MySQLStatement._NestedParenCrossSpec<I> {

        private CrossClauseDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                        _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                        String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//CrossClauseDerivedBlock


    private static final class JoinClauseDerivedBlock<I extends Item>
            extends NestedDerivedBlock<I, MySQLStatement._NestedOnSpec<I>>
            implements MySQLStatement._NestedParenOnSpec<I> {

        private JoinClauseDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                       _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table,
                                       String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, modifier, table, alias, ender);
        }

    }//JoinClauseDerivedBlock


    /**
     * @see MySQLNestedJoins
     */
    private static final class PartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLQuery._NestedIndexHintJoinSpec<I>>
            implements MySQLQuery._NestedPartitionJoinSpec<I> {

        private final Consumer<_TabularBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedJoins#leftParen(TableMeta)
         */
        private PartitionJoinClause(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
                , _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final FromClauseTableBlock<I> block;
            block = new FromClauseTableBlock<>(this.context, this.blockConsumer, params, this.supplier);
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

        private final Consumer<_TabularBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedBlock#crossJoin(TableMeta)
         */
        private PartitionCrossClause(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintCrossSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final CrossClauseTableBlock<I> block;
            block = new CrossClauseTableBlock<>(this.context, this.blockConsumer, params, this.supplier);
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

        private final Consumer<_TabularBlock> blockConsumer;

        private final Supplier<I> supplier;

        /**
         * @see MySQLNestedBlock#leftJoin(TableMeta)
         * @see MySQLNestedBlock#join(TableMeta)
         * @see MySQLNestedBlock#rightJoin(TableMeta)
         * @see MySQLNestedBlock#fullJoin(TableMeta)
         * @see MySQLNestedBlock#straightJoin(TableMeta)
         */
        private PartitionOnClause(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                  _JoinType joinType, TableMeta<?> table, Supplier<I> supplier) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
            this.supplier = supplier;
        }

        @Override
        MySQLQuery._NestedIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {

            final JoinClauseTableBlock<I> block;
            block = new JoinClauseTableBlock<>(this.context, this.blockConsumer, params, this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause


}
