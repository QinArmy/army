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

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLCrosses;
import io.army.criteria.mysql.MySQLJoins;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLDynamicJoins extends JoinableClause.DynamicJoinableBlock<
        MySQLStatement._DynamicIndexHintJoinClause,
        Statement._AsClause<MySQLStatement._DynamicJoinSpec>,
        MySQLStatement._DynamicJoinSpec,
        Void,
        MySQLStatement._DynamicIndexHintOnClause,
        Statement._AsParensOnClause<MySQLStatement._DynamicJoinSpec>,
        Statement._OnClause<MySQLStatement._DynamicJoinSpec>,
        Void,
        MySQLStatement._DynamicJoinSpec>
        implements MySQLStatement._DynamicJoinSpec {

    static MySQLJoins joinBuilder(CriteriaContext context, _JoinType joinTyp, Consumer<_TabularBlock> blockConsumer) {
        return new MySQLJoinBuilder(context, joinTyp, blockConsumer);
    }

    static MySQLCrosses crossBuilder(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
        return new MySQLCrossJoinBuilder(context, blockConsumer);
    }


    private MySQLDynamicJoins(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                              _JoinType joinType, @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
        super(context, blockConsumer, joinType, modifier, tabularItem, alias);
    }

    private MySQLDynamicJoins(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                              TabularBlocks.BlockParams params) {
        super(context, blockConsumer, params);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause leftJoin(TableMeta<?> table) {
        return new PartitionOnClause(this.context, this.blockConsumer, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause join(TableMeta<?> table) {
        return new PartitionOnClause(this.context, this.blockConsumer, _JoinType.JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause rightJoin(TableMeta<?> table) {
        return new PartitionOnClause(this.context, this.blockConsumer, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause fullJoin(TableMeta<?> table) {
        return new PartitionOnClause(this.context, this.blockConsumer, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionOnClause straightJoin(TableMeta<?> table) {
        return new PartitionOnClause(this.context, this.blockConsumer, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final MySQLQuery._DynamicPartitionJoinClause crossJoin(TableMeta<?> table) {
        return new PartitionJoinClause(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, table);
    }

    @Override
    public final MySQLStatement._DynamicJoinSpec crossJoin(Function<MySQLStatement._NestedLeftParenSpec<MySQLStatement._DynamicJoinSpec>, MySQLStatement._DynamicJoinSpec> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::crossNested));
    }

    @Override
    public final Statement._OnClause<MySQLStatement._DynamicJoinSpec> leftJoin(Function<MySQLStatement._NestedLeftParenSpec<Statement._OnClause<MySQLStatement._DynamicJoinSpec>>, Statement._OnClause<MySQLStatement._DynamicJoinSpec>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<MySQLStatement._DynamicJoinSpec> join(Function<MySQLStatement._NestedLeftParenSpec<Statement._OnClause<MySQLStatement._DynamicJoinSpec>>, Statement._OnClause<MySQLStatement._DynamicJoinSpec>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<MySQLStatement._DynamicJoinSpec> rightJoin(Function<MySQLStatement._NestedLeftParenSpec<Statement._OnClause<MySQLStatement._DynamicJoinSpec>>, Statement._OnClause<MySQLStatement._DynamicJoinSpec>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<MySQLStatement._DynamicJoinSpec> fullJoin(Function<MySQLStatement._NestedLeftParenSpec<Statement._OnClause<MySQLStatement._DynamicJoinSpec>>, Statement._OnClause<MySQLStatement._DynamicJoinSpec>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final Statement._OnClause<MySQLStatement._DynamicJoinSpec> straightJoin(Function<MySQLStatement._NestedLeftParenSpec<Statement._OnClause<MySQLStatement._DynamicJoinSpec>>, Statement._OnClause<MySQLStatement._DynamicJoinSpec>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
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
    final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final MySQLQuery._DynamicIndexHintJoinClause onFromTable(
            _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final FromClauseTableBlock block;
        block = new FromClauseTableBlock(this.context, this.blockConsumer, joinType, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsClause<MySQLQuery._DynamicJoinSpec> onFromDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final DynamicDerivedBlock block;
            block = new DynamicDerivedBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }


    @Override
    final MySQLQuery._DynamicJoinSpec onFromCte(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        final DynamicCteBlock block;
        block = new DynamicCteBlock(this.context, this.blockConsumer, joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final MySQLQuery._DynamicIndexHintOnClause onJoinTable(
            _JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final JoinClauseTableBlock block;
        block = new JoinClauseTableBlock(this.context, this.blockConsumer, joinType, table, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Statement._AsParensOnClause<MySQLStatement._DynamicJoinSpec> onJoinDerived(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final DynamicDerivedBlock block;
            block = new DynamicDerivedBlock(this.context, this.blockConsumer, joinType, modifier, table, alias);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final Statement._OnClause<MySQLQuery._DynamicJoinSpec> onJoinCte(
            _JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        final DynamicCteBlock block;
        block = new DynamicCteBlock(this.context, this.blockConsumer, joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        return block;
    }

    private Statement._OnClause<MySQLQuery._DynamicJoinSpec> joinNestedEnd(final _JoinType joinType,
                                                                           final _NestedItems nestedItems) {
        final DynamicNestedBlock block;
        block = new DynamicNestedBlock(this.context, this.blockConsumer, joinType, nestedItems);
        this.blockConsumer.accept(block);
        return block;
    }

    private MySQLQuery._DynamicJoinSpec crossNested(final _JoinType joinType,
                                                    final _NestedItems nestedItems) {
        final DynamicNestedBlock block;
        block = new DynamicNestedBlock(this.context, this.blockConsumer, joinType, nestedItems);
        this.blockConsumer.accept(block);
        return this;
    }


    private static final class DynamicNestedBlock extends MySQLDynamicJoins {

        private DynamicNestedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                   _NestedItems items) {
            super(context, blockConsumer, joinType, null, items, "");
        }


    }//DynamicNestedBlock


    private static final class DynamicCteBlock extends MySQLDynamicJoins {

        private DynamicCteBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                _JoinType joinType, _Cte cteItem, String alias) {
            super(context, blockConsumer, joinType, null, cteItem, alias);
        }

    }//DynamicCteBlock


    private static final class DynamicDerivedBlock extends MySQLDynamicJoins implements _ModifierTabularBlock,
            Statement._ParensOnSpec<MySQLStatement._DynamicJoinSpec>,
            _AliasDerivedBlock {

        private List<String> columnAliasList;

        private _SelectionMap selectionMap;

        private DynamicDerivedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                    _JoinType joinType, @Nullable SQLWords modifier, DerivedTable table, String alias) {
            super(context, blockConsumer, joinType, modifier, table, alias);
            this.selectionMap = (_DerivedTable) table;
        }

        @Override
        public Statement._OnClause<MySQLStatement._DynamicJoinSpec> parens(String first, String... rest) {
            return this.onColumnAlias(ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public Statement._OnClause<MySQLStatement._DynamicJoinSpec> parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.context, true, consumer));
        }

        @Override
        public Statement._OnClause<MySQLStatement._DynamicJoinSpec> ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAlias(CriteriaUtils.stringList(this.context, false, consumer));
        }

        @Override
        public Selection refSelection(String name) {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refSelection(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return this.selectionMap.refAllSelection();
        }

        @Override
        public List<String> columnAliasList() {
            List<String> list = this.columnAliasList;
            if (list == null) {
                list = Collections.emptyList();
                this.columnAliasList = list;
            }
            return list;
        }


        private Statement._OnClause<MySQLStatement._DynamicJoinSpec> onColumnAlias(final List<String> columnAliasList) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = columnAliasList;
            this.selectionMap = CriteriaUtils.createAliasSelectionMap(columnAliasList,
                    ((_DerivedTable) this.tabularItem).refAllSelection(),
                    this.alias);
            return this;
        }


    }//DynamicDerivedBlock

    @SuppressWarnings("unchecked")
    private static abstract class DynamicTableBlock<R extends Item> extends MySQLDynamicJoins
            implements _MySQLTableBlock,
            MySQLStatement._QueryIndexHintSpec<R>,
            MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexPurposeBySpec<Object>, R> {

        private final List<String> partitionList;

        private List<_IndexHint> indexHintList;

        private DynamicTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                  _JoinType joinType, TableMeta<?> table, String alias) {
            super(context, blockConsumer, joinType, null, table, alias);
            this.partitionList = Collections.emptyList();
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */

        private DynamicTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                  MySQLSupports.MySQLBlockParams params) {
            super(context, blockConsumer, params);
            this.partitionList = params.partitionList();
        }

        @Override
        public final MySQLStatement._IndexPurposeBySpec<R> useIndex() {
            return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                    this::indexHintEnd);
        }

        @Override
        public final MySQLStatement._IndexPurposeBySpec<R> ignoreIndex() {
            return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                    this::indexHintEnd);
        }

        @Override
        public final MySQLStatement._IndexPurposeBySpec<R> forceIndex() {
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
            List<_IndexHint> list = this.indexHintList;
            if (list == null || list instanceof ArrayList) {
                list = _Collections.safeUnmodifiableList(list);
                this.indexHintList = list;
            }
            return list;
        }


        @SuppressWarnings("unchecked")
        private R indexHintEnd(final _IndexHint indexHint) {
            List<_IndexHint> list = this.indexHintList;
            if (list == null) {
                list = _Collections.arrayList();
                this.indexHintList = list;
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            list.add(indexHint);
            return (R) this;
        }

        private Object indexHintEndAndReturnObject(final _IndexHint indexHint) {
            this.indexHintEnd(indexHint);
            return Collections.EMPTY_LIST;
        }


    }//DynamicTableBlock

    private static final class FromClauseTableBlock extends DynamicTableBlock<MySQLQuery._DynamicIndexHintJoinClause>
            implements MySQLQuery._DynamicIndexHintJoinClause {


        /**
         * @see MySQLJoinBuilder#space(TableMeta, SQLs.WordAs, String)
         */
        private FromClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     _JoinType joinType, TableMeta<?> table, String alias) {
            super(context, blockConsumer, joinType, table, alias);
        }

        /**
         * @see PartitionJoinClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private FromClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     MySQLSupports.MySQLBlockParams params) {
            super(context, blockConsumer, params);
        }


    }//FromClauseTableBlock


    private static final class JoinClauseTableBlock extends DynamicTableBlock<MySQLQuery._DynamicIndexHintOnClause>
            implements MySQLQuery._DynamicIndexHintOnClause {


        /**
         * @see MySQLJoinBuilder#space(TableMeta, SQLs.WordAs, String)
         */
        private JoinClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     _JoinType joinType, TableMeta<?> table, String alias) {
            super(context, blockConsumer, joinType, table, alias);
        }

        /**
         * @see PartitionOnClause#asEnd(MySQLSupports.MySQLBlockParams)
         */
        private JoinClauseTableBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer,
                                     MySQLSupports.MySQLBlockParams params) {
            super(context, blockConsumer, params);
        }


    }//JoinClauseTableBlock


    private static final class MySQLJoinBuilder extends JoinableClause.DynamicBuilderSupport<
            MySQLQuery._DynamicIndexHintOnClause,
            Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>>,
            Statement._OnClause<MySQLQuery._DynamicJoinSpec>,
            Void> implements MySQLJoins {


        private MySQLJoinBuilder(CriteriaContext context, _JoinType joinType, Consumer<_TabularBlock> blockConsumer) {
            super(context, joinType, blockConsumer);
        }

        @Override
        public Statement._OnClause<MySQLQuery._DynamicJoinSpec> space(
                Function<MySQLStatement._NestedLeftParenSpec<Statement._OnClause<MySQLQuery._DynamicJoinSpec>>, Statement._OnClause<MySQLQuery._DynamicJoinSpec>> function) {
            this.checkStart();
            return function.apply(MySQLNestedJoins.nestedItem(this.context, this.joinType, this::nestedEnd));
        }

        @Override
        public MySQLQuery._DynamicPartitionOnClause space(TableMeta<?> table) {
            this.checkStart();
            return new PartitionOnClause(this.context, this.blockConsumer, this.joinType, table);
        }


        @Override
        boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }

        @Override
        MySQLQuery._DynamicIndexHintOnClause onTable(
                @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final JoinClauseTableBlock block;
            block = new JoinClauseTableBlock(this.context, this.blockConsumer, this.joinType, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<Statement._OnClause<MySQLQuery._DynamicJoinSpec>> onDerived(
                @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final DynamicDerivedBlock block;
                block = new DynamicDerivedBlock(this.context, this.blockConsumer, this.joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Statement._OnClause<MySQLQuery._DynamicJoinSpec> onCte(_Cte cteItem, String alias) {
            final DynamicCteBlock block;
            block = new DynamicCteBlock(this.context, this.blockConsumer, this.joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        private Statement._OnClause<MySQLQuery._DynamicJoinSpec> nestedEnd(_JoinType joinType, _NestedItems items) {
            final DynamicNestedBlock block;
            block = new DynamicNestedBlock(this.context, this.blockConsumer, joinType, items);
            this.blockConsumer.accept(block);
            return block;
        }


    }//MySQLJoinBuilder


    private static final class MySQLCrossJoinBuilder extends JoinableClause.DynamicBuilderSupport<
            MySQLQuery._DynamicIndexHintJoinClause,
            Statement._AsClause<MySQLQuery._DynamicJoinSpec>,
            MySQLQuery._DynamicJoinSpec,
            Void> implements MySQLCrosses {

        private MySQLCrossJoinBuilder(CriteriaContext context, Consumer<_TabularBlock> blockConsumer) {
            super(context, _JoinType.CROSS_JOIN, blockConsumer);
        }

        @Override
        public MySQLQuery._DynamicJoinSpec space(
                Function<MySQLStatement._NestedLeftParenSpec<MySQLQuery._DynamicJoinSpec>, MySQLQuery._DynamicJoinSpec> function) {
            this.checkStart();
            return function.apply(MySQLNestedJoins.nestedItem(this.context, this.joinType, this::nestedEnd));
        }

        @Override
        public MySQLQuery._DynamicPartitionJoinClause space(TableMeta<?> table) {
            this.checkStart();
            return new PartitionJoinClause(this.context, this.blockConsumer, this.joinType, table);
        }

        @Override
        boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
            return CriteriaUtils.isIllegalLateral(modifier);
        }


        @Override
        MySQLQuery._DynamicIndexHintJoinClause onTable(
                @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            final FromClauseTableBlock block;
            block = new FromClauseTableBlock(this.context, this.blockConsumer, this.joinType, table, tableAlias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<MySQLQuery._DynamicJoinSpec> onDerived(
                @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final DynamicDerivedBlock block;
                block = new DynamicDerivedBlock(this.context, this.blockConsumer, this.joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        MySQLQuery._DynamicJoinSpec onCte(_Cte cteItem, String alias) {
            final DynamicCteBlock block;
            block = new DynamicCteBlock(this.context, this.blockConsumer, this.joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        private MySQLQuery._DynamicJoinSpec nestedEnd(_JoinType joinType, _NestedItems items) {
            final DynamicNestedBlock block;
            block = new DynamicNestedBlock(this.context, this.blockConsumer, joinType, items);
            this.blockConsumer.accept(block);
            return block;
        }


    }//MySQLCrossJoinBuilder


    /**
     * @see MySQLDynamicJoins
     * @see MySQLCrossJoinBuilder
     */
    private static final class PartitionJoinClause
            extends MySQLSupports.PartitionAsClause<MySQLQuery._DynamicIndexHintJoinClause>
            implements MySQLQuery._DynamicPartitionJoinClause {


        private final Consumer<_TabularBlock> blockConsumer;

        /**
         * @see MySQLDynamicJoins#crossJoin(TableMeta)
         * @see MySQLCrossJoinBuilder#space(TableMeta)
         */
        private PartitionJoinClause(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                    TableMeta<?> table) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;

        }

        @Override
        MySQLQuery._DynamicIndexHintJoinClause asEnd(final MySQLSupports.MySQLBlockParams params) {

            final FromClauseTableBlock block;
            block = new FromClauseTableBlock(this.context, this.blockConsumer, params);
            this.blockConsumer.accept(block);
            return block;
        }

    }//PartitionJoinClause


    private static final class PartitionOnClause
            extends MySQLSupports.PartitionAsClause<MySQLQuery._DynamicIndexHintOnClause>
            implements MySQLQuery._DynamicPartitionOnClause {

        private final Consumer<_TabularBlock> blockConsumer;

        /**
         * @see MySQLDynamicJoins#leftJoin(TableMeta)
         * @see MySQLDynamicJoins#join(TableMeta)
         * @see MySQLDynamicJoins#rightJoin(TableMeta)
         * @see MySQLDynamicJoins#fullJoin(TableMeta)
         * @see MySQLDynamicJoins#straightJoin(TableMeta)
         */
        private PartitionOnClause(CriteriaContext context, Consumer<_TabularBlock> blockConsumer, _JoinType joinType,
                                  TableMeta<?> table) {
            super(context, joinType, table);
            this.blockConsumer = blockConsumer;
        }

        @Override
        MySQLQuery._DynamicIndexHintOnClause asEnd(final MySQLSupports.MySQLBlockParams params) {
            final JoinClauseTableBlock block;
            block = new JoinClauseTableBlock(this.context, this.blockConsumer, params);
            this.blockConsumer.accept(block);
            return block;
        }


    }//PartitionOnClause


}
