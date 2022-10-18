package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._TableItemGroup;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLNestedJoins {

    private MySQLNestedJoins() {
        throw new UnsupportedOperationException();
    }


    private static final class NestedLeftParenClause<I extends Item>
            implements MySQLQuery._NestedLeftParenSpec<I>, _TableItemGroup
            , NestedItems {

        private final CriteriaContext context;

        private final Function<NestedItems, I> function;

        private List<_TableBlock> blockList = new ArrayList<>();

        private NestedLeftParenClause(CriteriaContext context, Function<NestedItems, I> function) {
            this.context = context;
            this.function = function;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<MySQLQuery._MySQLNestedJoinClause<I>> leftParen() {
            return new NestedLeftParenClause<>(this.context, this::nestedNestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedIndexHintJoinSpec<I> leftParen(TableMeta<?> table, StandardSyntax.WordAs wordAs
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
        public <T extends TabularItem> Statement._AsClause<MySQLQuery._MySQLNestedJoinClause<I>> leftParen(final Query.TabularModifier modifier
                , final Supplier<T> supplier) {
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
            return null;
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

        private MySQLQuery._MySQLNestedJoinClause<I> nestedNestedJoinEnd(final NestedItems nestedItems) {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() == 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add((_TableBlock) nestedItems);

            return new MySQLNestedJoinClause<>(this.context, blockList::add
                    , _JoinType.NONE, null, nestedItems, "", this::thisNestedJoinEnd);
        }

        private I thisNestedJoinEnd() {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() > 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.blockList = Collections.unmodifiableList(blockList);
            return this.function.apply(this);
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
            implements MySQLQuery._NestedOnSpec<I> {

        private final Supplier<I> supplier;

        private MySQLNestedJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias, Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
            this.supplier = supplier;
        }

        private MySQLNestedJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , MySQLSupports.MySQLBlockParams params, Supplier<I> supplier) {
            super(context, blockConsumer, params);
            this.supplier = supplier;
        }


        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> leftJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> join(TableMeta<?> table) {
            return null;
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> rightJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> fullJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public final MySQLQuery._NestedPartitionCrossSpec<I> crossJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public final MySQLQuery._NestedPartitionOnSpec<I> straightJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<I> leftJoin() {
            return null;
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<I> join() {
            return null;
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<I> rightJoin() {
            return null;
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<I> fullJoin() {
            return null;
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<I> crossJoin() {
            return null;
        }

        @Override
        public final MySQLQuery._NestedLeftParenSpec<I> straightJoin() {
            return null;
        }

        @Override
        public final I rightParen() {
            return this.supplier.get();
        }

        @Override
        final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String alias) {
            return null;
        }

        @Override
        final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        final MySQLQuery._NestedIndexHintOnSpec<I> createTableBlock(_JoinType joinType
                , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            return null;
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        final MySQLQuery._NestedOnSpec<I> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            return null;
        }


    }//MySQLNestedJoinClause

    private static final class NestedIndexHintJoinClause<I extends Item> extends MySQLNestedJoinClause<I>
            implements MySQLQuery._NestedIndexHintJoinSpec<I> {

        private NestedIndexHintJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, @Nullable SQLWords modifier
                , TabularItem tabularItem, String alias
                , Supplier<I> supplier) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias, supplier);
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<MySQLQuery._NestedIndexHintJoinSpec<I>> useIndex() {
            return null;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<MySQLQuery._NestedIndexHintJoinSpec<I>> ignoreIndex() {
            return null;
        }

        @Override
        public MySQLQuery._IndexPurposeBySpec<MySQLQuery._NestedIndexHintJoinSpec<I>> forceIndex() {
            return null;
        }


    }//NestedIndexHintJoinClause


}
