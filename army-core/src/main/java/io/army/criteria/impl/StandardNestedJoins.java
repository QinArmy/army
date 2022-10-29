package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardStatement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementation of standard nested join
 * </p>
 *
 * @see StandardQueries
 * @since 1.0
 */
final class StandardNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<I>
        implements StandardStatement._NestedLeftParenSpec<I> {

    static <I extends Item> StandardStatement._NestedLeftParenSpec<I> nestedItem(CriteriaContext context
            , _JoinType joinType, BiFunction<_JoinType, NestedItems, I> function) {
        return new StandardNestedJoins<>(context, joinType, function);
    }

    private StandardNestedJoins(CriteriaContext context, _JoinType joinType
            , BiFunction<_JoinType, NestedItems, I> function) {
        super(context, joinType, function);
    }

    @Override
    public StandardStatement._StandardNestedJoinClause<I> leftParen(TableMeta<?> table, SQLs.WordAs wordAs
            , String tableAlias) {
        assert wordAs == SQLs.AS;
        final StandardNestedJoinClause<I> block;
        block = new StandardNestedJoinClause<>(this.context, this::onAddTableBlock
                , _JoinType.NONE, table, tableAlias, this::thisNestedJoinEnd);

        this.onAddFirstBlock(block);
        return block;
    }

    @Override
    public <T extends TabularItem> Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>> leftParen(Supplier<T> supplier) {
        final TabularItem tabularItem;
        if ((tabularItem = supplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>> asClause;
        asClause = alias -> {
            final StandardNestedJoinClause<I> block;
            block = new StandardNestedJoinClause<>(this.context, this::onAddTableBlock
                    , _JoinType.NONE, tabularItem, alias, this::thisNestedJoinEnd);
            this.onAddFirstBlock(block);
            return block;
        };
        return asClause;
    }


    @Override
    public StandardStatement._NestedLeftParenSpec<StandardStatement._StandardNestedJoinClause<I>> leftParen() {
        return new StandardNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd);
    }

    private StandardStatement._StandardNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType
            , final NestedItems nestedItems) {
        if (joinType != _JoinType.NONE) {
            throw _Exceptions.unexpectedEnum(joinType);
        }
        final StandardNestedJoinClause<I> clause;
        clause = new StandardNestedJoinClause<>(this.context, this::onAddTableBlock
                , joinType, nestedItems, "", this::thisNestedJoinEnd);
        this.onAddFirstBlock(clause);
        return clause;
    }


    private static final class StandardNestedJoinClause<I extends Item> extends JoinableClause.NestedJoinClause<
            StandardStatement._NestedJoinSpec<I>,
            StandardStatement._NestedJoinSpec<I>,
            StandardStatement._NestedJoinSpec<I>,
            StandardStatement._NestedOnSpec<I>,
            StandardStatement._NestedOnSpec<I>,
            StandardStatement._NestedOnSpec<I>,
            StandardStatement._NestedJoinSpec<I>>
            implements StandardStatement._NestedOnSpec<I> {

        private final Supplier<I> supplier;

        private StandardNestedJoinClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TabularItem tabularItem
                , String alias, Supplier<I> supplier) {
            super(context, blockConsumer, joinType, null, tabularItem, alias);
            this.supplier = supplier;
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> leftJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> join() {
            return new StandardNestedJoins<>(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> rightJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> fullJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedJoinSpec<I>> crossJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::nestedCrossJoinEnd);
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifLeftJoin(Consumer<StandardJoins> consumer) {
            consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifJoin(Consumer<StandardJoins> consumer) {
            consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifRightJoin(Consumer<StandardJoins> consumer) {
            consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifFullJoin(Consumer<StandardJoins> consumer) {
            consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> ifCrossJoin(Consumer<StandardCrosses> consumer) {
            consumer.accept(StandardDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        public I rightParen() {
            return this.supplier.get();
        }

        @Override
        _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new TableBlock.NoOnTableBlock(joinType, table, alias);
        }

        @Override
        _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
        }

        @Override
        StandardStatement._NestedOnSpec<I> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new StandardNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, table, tableAlias, this.supplier);
        }

        @Override
        StandardStatement._NestedOnSpec<I> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new StandardNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, tableItem, alias, this.supplier);
        }

        @Override
        StandardStatement._NestedOnSpec<I> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            //standard query don't support cte
            throw ContextStack.castCriteriaApi(this.context);
        }


        /**
         * @see #leftJoin()
         * @see #join()
         * @see #rightJoin()
         * @see #fullJoin()
         */
        private StandardStatement._NestedOnSpec<I> nestedJoinEnd(final _JoinType joinType
                , final NestedItems nestedItems) {
            joinType.assertStandardJoinType();
            final StandardNestedJoinClause<I> block;
            block = new StandardNestedJoinClause<>(this.context, this.blockConsumer
                    , joinType, nestedItems, "", this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private StandardStatement._NestedJoinSpec<I> nestedCrossJoinEnd(final _JoinType joinType
                , final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final StandardNestedJoinClause<I> block;
            block = new StandardNestedJoinClause<>(this.context, this.blockConsumer
                    , _JoinType.CROSS_JOIN, items, "", this.supplier);
            this.blockConsumer.accept(block);
            return block;
        }


    }//StandardNestedJoinClause


}
