package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of all implementation of below:
 * <ul>
 *     <li>{@link io.army.criteria.Query}</li>
 *     <li>{@link io.army.criteria.Update}</li>
 *     <li>{@link io.army.criteria.Delete}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, OR, LR, LO, LF>
        extends WhereClause<WR, WA, OR, LR, LO, LF>
        implements Statement._JoinModifierClause<JT, JS>, Statement._CrossJoinModifierClause<FT, FS>
        , Statement._FromModifierClause<FT, FS>, Statement._FromModifierCteClause<FC>
        , Statement._UsingModifierClause<FT, FS>, Statement._UsingModifierCteClause<FC>
        , DialectStatement._JoinModifierCteClause<JC>, DialectStatement._CrossJoinModifierCteClause<FC>
        , DialectStatement._StraightJoinModifierTabularClause<JT, JS>
        , DialectStatement._StraightJoinModifierCteClause<JC> {


    final Consumer<_TableBlock> blockConsumer;

    /**
     * <p>
     * private constructor
     * </p>
     */
    private JoinableClause(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        super(context);
        this.blockConsumer = blockConsumer;
    }


    /**
     * <p>
     * package constructor for {@link  Statement}
     * </p>
     */
    JoinableClause(CriteriaContext context) {
        super(context);
        this.blockConsumer = context::onAddBlock;
    }

    @Override
    public final FT from(TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final FT from(Query.TableModifier modifier, TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, modifier, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> FS from(Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.NONE, null, supplier.get());
    }

    @Override
    public final <T extends DerivedTable> FS from(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.NONE, modifier, supplier.get());
    }

    @Override
    public final FC from(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC from(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final FC from(Query.DerivedModifier modifier, String cteName) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final FC from(Query.DerivedModifier modifier, String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), alias);
    }

    @Override
    public final FT using(TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final FT using(Query.TableModifier modifier, TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, modifier, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> FS using(Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.NONE, null, supplier.get());
    }

    @Override
    public final <T extends DerivedTable> FS using(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.NONE, modifier, supplier.get());
    }

    @Override
    public final FC using(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC using(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final FC using(Query.DerivedModifier modifier, String cteName) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final FC using(Query.DerivedModifier modifier, String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, modifier, this.context.refCte(cteName), alias);
    }



    /*################################## blow JoinSpec method ##################################*/

    @Override
    public final JT leftJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        return this.onJoinTable(_JoinType.LEFT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS leftJoin(Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.LEFT_JOIN, null, supplier.get());
    }


    @Override
    public final JC leftJoin(String cteName) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC leftJoin(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT leftJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.LEFT_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends DerivedTable> JS leftJoin(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.LEFT_JOIN, modifier, supplier.get());
    }

    @Override
    public final JC leftJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC leftJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.LEFT_JOIN, modifier, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT join(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS join(Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.JOIN, null, supplier.get());
    }

    @Override
    public final JC join(String cteName) {
        return this.onJoinCte(_JoinType.JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC join(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.JOIN, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT join(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        return this.onJoinTable(_JoinType.JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS join(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.JOIN, modifier, supplier.get());
    }

    @Override
    public final JC join(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC join(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.JOIN, modifier, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onJoinTable(_JoinType.RIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS rightJoin(Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.RIGHT_JOIN, null, supplier.get());
    }

    @Override
    public final JC rightJoin(String cteName) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC rightJoin(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT rightJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onJoinTable(_JoinType.RIGHT_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS rightJoin(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.RIGHT_JOIN, modifier, supplier.get());
    }

    @Override
    public final JC rightJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.RIGHT_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC rightJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onJoinCte(_JoinType.RIGHT_JOIN, modifier, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT fullJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onJoinTable(_JoinType.FULL_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS fullJoin(Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.FULL_JOIN, null, supplier.get());
    }

    @Override
    public final JC fullJoin(String cteName) {
        return this.onJoinCte(_JoinType.FULL_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC fullJoin(String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.FULL_JOIN, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT fullJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onJoinTable(_JoinType.FULL_JOIN, modifier, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS fullJoin(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.FULL_JOIN, modifier, supplier.get());
    }

    @Override
    public final JC fullJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.FULL_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC fullJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.FULL_JOIN, modifier, this.context.refCte(cteName), alias);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onJoinTable(_JoinType.STRAIGHT_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> JS straightJoin(Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.STRAIGHT_JOIN, null, supplier.get());
    }

    @Override
    public final JC straightJoin(String cteName) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final JC straightJoin(String cteName, SQLs.WordAs as, String alias) {
        assert as == SQLs.AS;
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final JC straightJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final JC straightJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs as, String alias) {
        return this.onJoinCte(_JoinType.STRAIGHT_JOIN, modifier, this.context.refCte(cteName), alias);
    }

    @Override
    public final <T extends DerivedTable> JS straightJoin(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onJoinDerived(_JoinType.STRAIGHT_JOIN, modifier, supplier.get());
    }

    @Override
    public final FT crossJoin(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
        assert as == SQLs.AS;
        return this.onFromTable(_JoinType.CROSS_JOIN, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> FS crossJoin(Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.CROSS_JOIN, null, supplier.get());
    }


    @Override
    public final FC crossJoin(String cteName) {
        return this.onFromCte(_JoinType.CROSS_JOIN, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC crossJoin(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.CROSS_JOIN, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final FT crossJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        assert wordAs == SQLs.AS;
        return this.onFromTable(_JoinType.CROSS_JOIN, modifier, table, tableAlias);
    }


    @Override
    public final <T extends DerivedTable> FS crossJoin(Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.CROSS_JOIN, modifier, supplier.get());
    }

    @Override
    public final FC crossJoin(Query.DerivedModifier modifier, String cteName) {
        return this.onFromCte(_JoinType.CROSS_JOIN, modifier, this.context.refCte(cteName), "");
    }

    @Override
    public final FC crossJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias) {
        assert wordAs == SQLs.AS;
        return this.onFromCte(_JoinType.CROSS_JOIN, modifier, this.context.refCte(cteName), alias);
    }

    _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        throw new UnsupportedOperationException();
    }

    _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
        throw new UnsupportedOperationException();
    }


    JT createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
        throw new UnsupportedOperationException();
    }

    JS createItemBlock(_JoinType joinType, @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
        throw new UnsupportedOperationException();
    }

    JC createCteBlock(_JoinType joinType, @Nullable Query.DerivedModifier modifier, TabularItem tableItem, String alias) {
        throw new UnsupportedOperationException();
    }


    FT onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table,
                   String alias) {

        throw new UnsupportedOperationException();
    }

    /**
     * @see #crossJoin(Supplier)
     * @see #crossJoin(Query.DerivedModifier, Supplier)
     */
    FS onFromDerived(final _JoinType joinType, final @Nullable Query.DerivedModifier modifier,
                     final @Nullable DerivedTable table) {
        throw new UnsupportedOperationException();
    }

    FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {

        throw new UnsupportedOperationException();
    }

    JT onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        throw new UnsupportedOperationException();
    }


    JS onJoinDerived(final _JoinType joinType, final @Nullable Query.DerivedModifier modifier,
                     final @Nullable DerivedTable table) {
        throw new UnsupportedOperationException();
    }

    JC onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem,
                 String alias) {
        throw new UnsupportedOperationException();
    }



    static abstract class NestedLeftParenClause<I extends Item> implements _NestedItems {

        final CriteriaContext context;

        private final _JoinType joinType;

        private final BiFunction<_JoinType, NestedItems, I> function;

        private List<_TableBlock> blockList = new ArrayList<>();

        NestedLeftParenClause(CriteriaContext context, _JoinType joinType
                , BiFunction<_JoinType, NestedItems, I> function) {
            this.context = context;
            this.joinType = joinType;
            this.function = function;
        }

        @Override
        public final List<_TableBlock> tableBlockList() {
            final List<_TableBlock> blockList = this.blockList;
            if (blockList == null || blockList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return blockList;
        }

        final void onAddTableBlock(final _TableBlock block) {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add(block);
        }

        final void onAddFirstBlock(final _TableBlock block) {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() == 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            blockList.add(block);
        }


        final I thisNestedJoinEnd() {
            final List<_TableBlock> blockList = this.blockList;
            if (!(blockList instanceof ArrayList && blockList.size() > 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.blockList = Collections.unmodifiableList(blockList);
            return this.function.apply(this.joinType, this);
        }

    }//NestedLeftParenClause


    @SuppressWarnings("unchecked")
    static abstract class JoinableBlock<FT, FS, FC, JT, JS, JC, OR>
            extends JoinableClause<FT, FS, FC, JT, JS, JC, Object, Object, Object, Object, Object, Object>
            implements Statement._OnClause<OR>, _TableBlock, _TableBlock._ModifierTableBlockSpec {

        private final _JoinType joinType;

        private final SQLWords modifier;

        final TabularItem tabularItem;

        private final String alias;

        private List<_Predicate> onPredicateList;

        JoinableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                      @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer);
            this.joinType = joinType;
            this.modifier = modifier;
            this.tabularItem = tabularItem;
            this.alias = alias;
        }

        JoinableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, TableBlock.BlockParams params) {
            super(context, blockConsumer);
            this.joinType = params.joinType();
            this.tabularItem = params.tableItem();
            this.alias = params.alias();

            if (params instanceof TableBlock.DialectBlockParams) {
                this.modifier = ((TableBlock.DialectBlockParams) params).modifier();
            } else {
                this.modifier = null;
            }

        }

        @Override
        public final OR on(IPredicate predicate) {
            this.onPredicateList = Collections.singletonList((OperationPredicate<?>) predicate);
            return (OR) this;
        }

        @Override
        public final OR on(IPredicate predicate1, IPredicate predicate2) {
            this.onPredicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate<?>) predicate1,
                    (OperationPredicate<?>) predicate2
            );
            return (OR) this;
        }

        @Override
        public final OR on(Function<Expression, IPredicate> operator, DataField operandField) {
            this.onPredicateList = Collections.singletonList((OperationPredicate<?>) operator.apply(operandField));
            return (OR) this;
        }

        @Override
        public final OR on(Function<Expression, IPredicate> operator1, DataField operandField1
                , Function<Expression, IPredicate> operator2, DataField operandField2) {
            this.onPredicateList = ArrayUtils.asUnmodifiableList(
                    (OperationPredicate<?>) operator1.apply(operandField1),
                    (OperationPredicate<?>) operator2.apply(operandField2)
            );
            return (OR) this;
        }

        @Override
        public final OR on(Consumer<Consumer<IPredicate>> consumer) {
            final List<IPredicate> list = new ArrayList<>();
            consumer.accept(list::add);
            this.onPredicateList = CriteriaUtils.asPredicateList(this.context, list);
            return (OR) this;
        }

        @Override
        public final _JoinType jointType() {
            return this.joinType;
        }

        @Override
        public final SQLWords modifier() {
            return this.modifier;
        }

        @Override
        public final TabularItem tableItem() {
            return this.tabularItem;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final List<_Predicate> onClauseList() {
            List<_Predicate> list = this.onPredicateList;
            if (list == null) {
                list = Collections.emptyList();
                this.onPredicateList = list;
            }
            return list;
        }


        @Override
        final Dialect statementDialect() {
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//JoinableBlock

    static abstract class NestedJoinableBlock<FT, FS, FC, JT, JS, JC, OR>
            extends JoinableBlock<FT, FS, FC, JT, JS, JC, OR> {

        NestedJoinableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                            @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
        }

        NestedJoinableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                            TableBlock.BlockParams params) {
            super(context, blockConsumer, params);
        }

    }//NestedJoinClause

    static abstract class DynamicJoinableBlock<FT, FS, FC, JT, JS, JC, OR>
            extends JoinableBlock<FT, FS, FC, JT, JS, JC, OR> {


        DynamicJoinableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer, _JoinType joinType,
                             @Nullable SQLWords modifier, TabularItem tabularItem, String alias) {
            super(context, blockConsumer, joinType, modifier, tabularItem, alias);
        }

        DynamicJoinableBlock(CriteriaContext context, Consumer<_TableBlock> blockConsumer,
                             TableBlock.BlockParams params) {
            super(context, blockConsumer, params);
        }


    }//DynamicJoinableBlock

    static abstract class DynamicBuilderSupport {

        final CriteriaContext context;

        final _JoinType joinType;

        final Consumer<_TableBlock> blockConsumer;


        DynamicBuilderSupport(CriteriaContext context, _JoinType joinType, Consumer<_TableBlock> blockConsumer) {
            this.context = context;
            this.joinType = joinType;
            this.blockConsumer = blockConsumer;
        }

    }//DynamicBuilderSupport


}
