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
        final StandardNestedBlock<I> block;
        block = new StandardNestedBlock<>(this.context, this::onAddTableBlock
                , _JoinType.NONE, table, tableAlias, this::thisNestedJoinEnd);

        this.onAddTableBlock(block);
        return block;
    }

    @Override
    public <T extends DerivedTable> Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>> leftParen(Supplier<T> supplier) {
        final TabularItem tabularItem;
        if ((tabularItem = supplier.get()) == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Statement._AsClause<StandardStatement._StandardNestedJoinClause<I>> asClause;
        asClause = alias -> {
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this::onAddTableBlock
                    , _JoinType.NONE, tabularItem, alias, this::thisNestedJoinEnd);
            this.onAddTableBlock(block);
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
        final StandardNestedBlock<I> clause;
        clause = new StandardNestedBlock<>(this.context, this::onAddTableBlock
                , joinType, nestedItems, "", this::thisNestedJoinEnd);
        this.onAddTableBlock(clause);
        return clause;
    }


    private static final class StandardNestedBlock<I extends Item> extends JoinableClause.NestedJoinableBlock<
            StandardStatement._NestedJoinSpec<I>,
            Statement._AsClause<StandardStatement._NestedJoinSpec<I>>,
            Void,
            StandardStatement._NestedOnSpec<I>,
            Statement._AsClause<StandardStatement._NestedOnSpec<I>>,
            Void,
            StandardStatement._NestedJoinSpec<I>>
            implements StandardStatement._NestedOnSpec<I> {

        private final Supplier<I> ender;

        private StandardNestedBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer
                , _JoinType joinType, TabularItem tabularItem
                , String alias, Supplier<I> ender) {
            super(context, blockConsumer, joinType, null, tabularItem, alias);
            this.ender = ender;
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> leftJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> join() {
            return new StandardNestedJoins<>(this.context, _JoinType.JOIN, this::joinNestedEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> rightJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedOnSpec<I>> fullJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
        }

        @Override
        public StandardStatement._NestedLeftParenSpec<StandardStatement._NestedJoinSpec<I>> crossJoin() {
            return new StandardNestedJoins<>(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
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
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(joinType, table, alias));
            return this;
        }

        @Override
        Statement._AsClause<StandardStatement._NestedJoinSpec<I>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable table) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (table == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return alias -> {
                final StandardNestedBlock<I> block;
                block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, table, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Void onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
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
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, @Nullable DerivedTable item) {
            if (item == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return alias -> {
                final StandardNestedBlock<I> block;
                block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, item, alias,
                        this.ender);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        Void onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        /**
         * @see #leftJoin()
         * @see #join()
         * @see #rightJoin()
         * @see #fullJoin()
         */
        private StandardStatement._NestedOnSpec<I> joinNestedEnd(final _JoinType joinType
                , final NestedItems nestedItems) {
            joinType.assertStandardJoinType();
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, joinType, nestedItems, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }

        /**
         * @see #crossJoin()
         */
        private StandardStatement._NestedJoinSpec<I> fromNestedEnd(final _JoinType joinType, final NestedItems items) {
            assert joinType == _JoinType.CROSS_JOIN;
            final StandardNestedBlock<I> block;
            block = new StandardNestedBlock<>(this.context, this.blockConsumer, _JoinType.CROSS_JOIN, items, "",
                    this.ender);
            this.blockConsumer.accept(block);
            return block;
        }


    }//StandardNestedJoinClause


}
