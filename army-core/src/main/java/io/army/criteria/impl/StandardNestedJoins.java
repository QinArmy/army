package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardStatement;

import javax.annotation.Nullable;

import io.army.meta.TableMeta;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementation of standard nested join
*
 * @see StandardQueries
 * @since 1.0
 */
final class StandardNestedJoins<I extends Item> extends JoinableClause.NestedLeftParenClause<
        I,
        StandardStatement._StandardNestedJoinClause<I>,
        Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>>,
        Object,
        Void>
        implements StandardStatement._NestedLeftParenSpec<I> {

    static <I extends Item> StandardStatement._NestedLeftParenSpec<I> nestedJoin(CriteriaContext context
            , _JoinType joinType, BiFunction<_JoinType, _NestedItems, I> function) {
        return new StandardNestedJoins<>(context, joinType, function);
    }

    private StandardNestedJoins(CriteriaContext context, _JoinType joinType
            , BiFunction<_JoinType, _NestedItems, I> function) {
        super(context, joinType, function);
    }


    @Override
    StandardStatement._StandardNestedJoinClause<I> onLeftTable(
            @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        final StandardNestedBlock<I> block;
        block = new StandardNestedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, table, tableAlias,
                this::thisNestedJoinEnd);
        this.onAddTabularBlock(block);
        return block;
    }

    @Override
    Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>> onLeftDerived(
            @Nullable Query.DerivedModifier modifier, DerivedTable table) {
        return alias -> {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this::onAddTabularBlock, _JoinType.NONE, table, alias,
                    this::thisNestedJoinEnd);
            this.onAddTabularBlock(block);
            return block;
        };
    }

    @Override
    Object onLeftCte(_Cte cteItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    public StandardStatement._StandardNestedJoinClause<I> leftParen(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._StandardNestedJoinClause<I>>, StandardStatement._StandardNestedJoinClause<I>> function) {
        return function.apply(new StandardNestedJoins<>(this.context, _JoinType.NONE, this::nestedNestedJoinEnd));
    }

    private StandardStatement._StandardNestedJoinClause<I> nestedNestedJoinEnd(final _JoinType joinType,
                                                                               final _NestedItems nestedItems) {
        final StandardNestedBlock<I> clause;
        clause = new StandardNestedBlock<>(this.context, this::onAddTabularBlock
                , joinType, nestedItems, "", this::thisNestedJoinEnd);
        this.onAddTabularBlock(clause);
        return clause;
    }


    private static final class StandardNestedBlock<I extends Item> extends JoinableClause.NestedJoinableBlock<
            StandardStatement._NestedJoinSpec<I>,
            Statement._AsClause<StandardStatement._NestedJoinSpec<I>>,
            Void,
            Void,
            StandardStatement._NestedOnSpec<I>,
            Statement._AsClause<StandardStatement._NestedOnSpec<I>>,
            Void,
            Void,
            StandardStatement._NestedJoinSpec<I>>
            implements StandardStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private StandardNestedBlock(CriteriaContext context, Consumer<_TabularBlock> blockConsumer
                , _JoinType joinType, TabularItem tabularItem
                , String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, null, tabularItem, alias);
            this.ender = ender;
        }

        @Override
        public StandardStatement._NestedJoinSpec<I> crossJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedJoinSpec<I>>, StandardStatement._NestedJoinSpec<I>> function) {
            return function.apply(new StandardNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> leftJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return function.apply(new StandardNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> join(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return function.apply(new StandardNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> rightJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return function.apply(new StandardNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
        }

        @Override
        public StandardStatement._NestedOnSpec<I> fullJoin(Function<StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>>, StandardStatement._NestedOnSpec<I>> function) {
            return function.apply(new StandardNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
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
            return this.ender.get();
        }


        @Override
        StandardStatement._NestedJoinSpec<I> onFromTable(final _JoinType joinType, @Nullable Query.TableModifier modifier,
                                                         final TableMeta<?> table, final String alias) {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<StandardStatement._NestedJoinSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final StandardNestedBlock<I> block;
                block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, table, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Void onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        @Override
        StandardStatement._NestedOnSpec<I> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                       TableMeta<?> table, String alias) {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, table, alias,
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        Statement._AsClause<StandardStatement._NestedOnSpec<I>> onJoinDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable item) {
            return alias -> {
                final StandardNestedBlock<I> block;
                block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, item, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Void onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        /**
         * @see #leftJoin(Function)
         * @see #join(Function)
         * @see #rightJoin(Function)
         * @see #fullJoin(Function)
         */
        private StandardStatement._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin(Function)
         */
        private StandardStatement._NestedJoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, items, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }


    }//StandardNestedJoinClause


}
