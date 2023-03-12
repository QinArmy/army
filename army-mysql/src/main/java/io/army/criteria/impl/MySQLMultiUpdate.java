package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SimpleUpdateStatement} ,MySQL multi update api implementation</li>
 *         <li>{@link BatchUpdateStatement} ,MySQL batch multi update api implementation</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiUpdate<I extends Item, WE, FT, SR, FS extends Item, FC, JT, JS, JC, WR, WA>
        extends JoinableUpdate.WithMultiUpdate<I, MySQLCtes, WE, TableField, SR, FT, FS, FC, JT, JS, JC, WR, WA, Object, Object, Object, Object>
        implements UpdateStatement, _MySQLMultiUpdate, MySQLUpdate, MySQLStatement._IndexHintForJoinClause<FT> {


    static <I extends Item> _MultiWithSpec<I> simple(Function<UpdateStatement, I> function) {
        return new SimpleUpdateStatement<>(null, function);
    }

    static <I extends Item> _BatchMultiWithSpec<I> batch(Function<UpdateStatement, I> function) {
        return new BatchUpdateStatement<>(function);
    }

    private final Function<UpdateStatement, I> function;

    List<Hint> hintList;

    List<MySQLSyntax.Modifier> modifierList;

    _TableBlock fromCrossBlock;


    private MySQLMultiUpdate(@Nullable _WithClauseSpec withSpec, Function<UpdateStatement, I> function) {
        super(withSpec, CriteriaContexts.primaryMultiDmlContext());
        this.function = function;
    }


    @Override
    public final MySQLQuery._IndexForJoinSpec<FT> useIndex() {
        return this.getHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<FT> ignoreIndex() {
        return this.getHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<FT> forceIndex() {
        return this.getHintClause().forceIndex();
    }


    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        final List<MySQLs.Modifier> list = this.modifierList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }
    /*################################## blow package template method ##################################*/


    @Override
    final I onAsUpdate() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this instanceof BatchUpdateStatement && ((BatchUpdateStatement<I>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.function.apply(this);
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        if (this instanceof BatchUpdateStatement) {
            ((BatchUpdateStatement<I>) this).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mySQLCteBuilder(recursive, this.context);
    }

    @Override
    final @Nullable Query.TableModifier tableModifier(@Nullable Query.TableModifier modifier) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    @Nullable
    @Override
    final Query.DerivedModifier derivedModifier(@Nullable Query.DerivedModifier modifier) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.errorModifier(this.context, modifier);
        }
        return modifier;
    }


    @Override
    final FT onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final MySQLSupports.MySQLNoOnBlock<FT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (FT) this);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FT) this;
    }

    final FT fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FT) this;
    }

    final void derivedAliasList(final List<String> aliasList) {
        final _TableBlock block = this.fromCrossBlock;
        final TabularItem item;
        if (block != this.context.lastBlock() || !((item = block.tableItem()) instanceof DerivedTable)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        ((ArmyDerivedTable) item).setColumnAliasList(aliasList);
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLQuery._IndexHintForJoinClause<FT> getHintClause() {
        final _TableBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.MySQLNoOnBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((MySQLSupports.MySQLNoOnBlock<FT>) block).getUseIndexClause();
    }


    /*################################## blow inner class  ##################################*/

    private static final class SimpleComma<I extends Item> implements MySQLUpdate._MultiComma<I> {

        private final boolean recursive;

        private final SimpleUpdateStatement<I> clause;

        private final Function<String, _StaticCteParensSpec<_MultiComma<I>>> function;

        private SimpleComma(boolean recursive, SimpleUpdateStatement<I> clause) {
            clause.context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteParensSpec<_MultiComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _SimpleMultiUpdateClause<I> space() {
            return this.clause.endStaticWithClause(this.recursive);
        }

    }//SimpleComma


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class SimpleUpdateStatement<I extends Item> extends MySQLMultiUpdate<
            I,
            _SimpleMultiUpdateClause<I>,
            MySQLUpdate._MultiIndexHintJoinSpec<I>,
            MySQLUpdate._MultiWhereSpec<I>,
            Statement._AsClause<MySQLUpdate._ParensJoinSpec<I>>,
            MySQLUpdate._MultiJoinSpec<I>,
            MySQLUpdate._MultiIndexHintOnSpec<I>,
            Statement._AsParensOnClause<MySQLUpdate._MultiJoinSpec<I>>,
            Statement._OnClause<MySQLUpdate._MultiJoinSpec<I>>,
            Statement._DmlUpdateSpec<I>,
            MySQLUpdate._MultiWhereAndSpec<I>>
            implements MySQLUpdate._MultiWithSpec<I>,
            MySQLUpdate._MultiIndexHintJoinSpec<I>,
            MySQLUpdate._ParensJoinSpec<I>,
            MySQLUpdate._MultiWhereSpec<I>,
            MySQLUpdate._MultiWhereAndSpec<I> {


        private SimpleUpdateStatement(@Nullable _WithClauseSpec withSpec, Function<UpdateStatement, I> function) {
            super(withSpec, function);
        }

        @Override
        public _MultiWhereSpec<I> sets(Consumer<ItemPairs<TableField>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _StaticCteParensSpec<_MultiComma<I>> with(String name) {
            return new SimpleComma<>(false, this).function.apply(name);
        }

        @Override
        public _StaticCteParensSpec<_MultiComma<I>> withRecursive(String name) {
            return new SimpleComma<>(true, this).function.apply(name);
        }

        @Override
        public _MultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers,
                                                 TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
        }

        @Override
        public _MultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
        }


        @Override
        public <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(
                Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromDerived(_JoinType.NONE, null, this.nonNull(supplier.get()));
        }

        @Override
        public <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(Supplier<T> supplier) {
            return this.onFromDerived(_JoinType.NONE, null, this.nonNull(supplier.get()));
        }

        @Override
        public <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(
                Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                @Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromDerived(_JoinType.NONE, derivedModifier(modifier), nonNull(supplier.get()));
        }

        @Override
        public <T extends DerivedTable> _AsClause<_ParensJoinSpec<I>> update(@Nullable Query.DerivedModifier modifier,
                                                                             Supplier<T> supplier) {
            return this.onFromDerived(_JoinType.NONE, derivedModifier(modifier), nonNull(supplier.get()));
        }

        @Override
        public _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
        }

        @Override
        public _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers,
                                        String cteName, SQLs.WordAs wordAs, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), cteAlias(alias));
        }

        @Override
        public _MultiJoinSpec<I> update(String cteName) {
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
        }

        @Override
        public _MultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), cteAlias(alias));
        }

        @Override
        public _MultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _MultiPartitionJoinClause<I> update(TableMeta<?> table) {
            return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
        }

        @Override
        public _MultiJoinSpec<I> parens(String first, String... rest) {
            this.derivedAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
            this.derivedAliasList(CriteriaUtils.columnAliasList(true, consumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
            this.derivedAliasList(CriteriaUtils.columnAliasList(false, consumer));
            return this;
        }

        @Override
        public _MultiPartitionOnClause<I> leftJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause<>(this, _JoinType.LEFT_JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<I> join(TableMeta<?> table) {
            return new SimplePartitionOnClause<>(this, _JoinType.JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<I> rightJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause<>(this, _JoinType.RIGHT_JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<I> fullJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause<>(this, _JoinType.FULL_JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<I> straightJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause<>(this, _JoinType.STRAIGHT_JOIN, table);
        }

        @Override
        public _MultiPartitionJoinClause<I> crossJoin(TableMeta<?> table) {
            return new SimplePartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
        }


        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> leftJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> join() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> rightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> fullJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> straightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> crossJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
        }

        @Override
        public _MultiJoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
            consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                    DerivedTable table) {
            return alias -> {
                final TableBlock.NoOnModifierDerivedBlock block;
                block = new TableBlock.NoOnModifierDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }


        @Override
        _MultiJoinSpec<I> onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem,
                                    String alias) {
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        }

        @Override
        _MultiIndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                             TableMeta<?> table, String alias) {
            final SimpleOnTableBlock<I> block;
            block = new SimpleOnTableBlock<>(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                           DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_MultiJoinSpec<I>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_MultiJoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                               CteItem cteItem, String alias) {
            final OnClauseTableBlock<_MultiJoinSpec<I>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        private _OnClause<_MultiJoinSpec<I>> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
            joinType.assertMySQLJoinType();
            final OnClauseTableBlock<_MultiJoinSpec<I>> block;
            block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
            this.blockConsumer.accept(block);
            return block;
        }


    }// SimpleUpdateStatement

    private static final class BatchComma<I extends Item> implements MySQLUpdate._BatchMultiComma<I> {

        private final boolean recursive;

        private final BatchUpdateStatement<I> clause;

        private final Function<String, _StaticCteParensSpec<_BatchMultiComma<I>>> function;

        private BatchComma(boolean recursive, BatchUpdateStatement<I> clause) {
            clause.context.onBeforeWithClause(recursive);
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteParensSpec<_BatchMultiComma<I>> comma(String name) {
            return this.function.apply(name);
        }


        @Override
        public _BatchMultiUpdateClause<I> space() {
            return this.clause.endStaticWithClause(this.recursive);
        }


    }//BatchComma


    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     */
    private static final class BatchUpdateStatement<I extends Item> extends MySQLMultiUpdate<
            I,
            MySQLUpdate._BatchMultiUpdateClause<I>,
            MySQLUpdate._BatchMultiIndexHintJoinSpec<I>,
            MySQLUpdate._BatchMultiWhereSpec<I>,
            _AsClause<MySQLUpdate._BatchParensJoinSpec<I>>,
            MySQLUpdate._BatchMultiJoinSpec<I>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<I>,
            _AsParensOnClause<MySQLUpdate._BatchMultiJoinSpec<I>>,
            _OnClause<MySQLUpdate._BatchMultiJoinSpec<I>>,
            Statement._BatchParamClause<_DmlUpdateSpec<I>>,
            MySQLUpdate._BatchMultiWhereAndSpec<I>>
            implements MySQLUpdate._BatchMultiWithSpec<I>,
            MySQLUpdate._BatchMultiIndexHintJoinSpec<I>,
            MySQLUpdate._BatchParensJoinSpec<I>,
            MySQLUpdate._BatchMultiWhereSpec<I>,
            MySQLUpdate._BatchMultiWhereAndSpec<I>,
            BatchUpdate,
            _BatchDml {


        private List<?> paramList;


        private BatchUpdateStatement(Function<UpdateStatement, I> function) {
            super(null, function);
        }

        @Override
        public _StaticCteParensSpec<_BatchMultiComma<I>> with(String name) {
            return new BatchComma<>(false, this).function.apply(name);
        }

        @Override
        public _StaticCteParensSpec<_BatchMultiComma<I>> withRecursive(String name) {
            return new BatchComma<>(true, this).function.apply(name);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers,
                                                      TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
        }


        @Override
        public <T extends DerivedTable> _AsClause<_BatchParensJoinSpec<I>> update(Supplier<List<Hint>> hints,
                                                                                  List<MySQLSyntax.Modifier> modifiers,
                                                                                  Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromDerived(_JoinType.NONE, null, this.nonNull(supplier.get()));
        }

        @Override
        public <T extends DerivedTable> _AsClause<_BatchParensJoinSpec<I>> update(Supplier<T> supplier) {
            return this.onFromDerived(_JoinType.NONE, null, this.nonNull(supplier.get()));
        }

        @Override
        public <T extends DerivedTable> _AsClause<_BatchParensJoinSpec<I>> update(
                Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, Query.DerivedModifier modifier,
                Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromDerived(_JoinType.NONE, derivedModifier(modifier), this.nonNull(supplier.get()));
        }

        @Override
        public <T extends DerivedTable> _AsClause<_BatchParensJoinSpec<I>> update(Query.DerivedModifier modifier,
                                                                                  Supplier<T> supplier) {
            return this.onFromDerived(_JoinType.NONE, derivedModifier(modifier), this.nonNull(supplier.get()));
        }

        @Override
        public _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers,
                                             String cteName) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
        }

        @Override
        public _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers,
                                             String cteName, SQLs.WordAs wordAs, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), this.cteAlias(alias));
        }

        @Override
        public _BatchMultiJoinSpec<I> update(String cteName) {
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
        }

        @Override
        public _BatchMultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
            if (!_StringUtils.hasText(alias)) {
                throw ContextStack.criteriaError(this.context, _Exceptions::cteNameNotText);
            }
            return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), this.cteAlias(alias));
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints,
                                                        List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new BatchPartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> update(TableMeta<?> table) {
            return new BatchPartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints,
                                                                              List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
        }


        @Override
        public _BatchMultiJoinSpec<I> parens(String first, String... rest) {
            this.derivedAliasList(_ArrayUtils.unmodifiableListOf(first, rest));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
            this.derivedAliasList(CriteriaUtils.columnAliasList(true, consumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
            this.derivedAliasList(CriteriaUtils.columnAliasList(false, consumer));
            return this;
        }

        @Override
        public _BatchMultiPartitionOnClause<I> leftJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause<>(this, _JoinType.LEFT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<I> join(TableMeta<?> table) {
            return new BatchPartitionOnClause<>(this, _JoinType.JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<I> rightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause<>(this, _JoinType.RIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<I> fullJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause<>(this, _JoinType.FULL_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<I> straightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause<>(this, _JoinType.STRAIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> crossJoin(TableMeta<?> table) {
            return new BatchPartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> leftJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> join() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> rightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> fullJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> straightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> crossJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
        }

        @Override
        public _BatchMultiJoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
            consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiWhereSpec<I> sets(Consumer<BatchItemPairs<TableField>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<I> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<I> paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }


        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        _AsClause<_BatchParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                         @Nullable DerivedTable table) {
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.errorModifier(this.context, modifier);
            }
            return alias -> {
                final TableBlock.NoOnModifierDerivedBlock block;
                block = new TableBlock.NoOnModifierDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }

        @Override
        _BatchMultiJoinSpec<I> onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                         CteItem cteItem, String alias) {
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            this.fromCrossBlock = block;
            return this;
        }

        @Override
        _BatchMultiIndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                  TableMeta<?> table, String alias) {
            final BatchOnTableBlock<I> block;
            block = new BatchOnTableBlock<>(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchMultiJoinSpec<I>> onJoinDerived(_JoinType joinType,
                                                                @Nullable Query.DerivedModifier modifier,
                                                                @Nullable DerivedTable table) {
            if (table == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.errorModifier(this.context, modifier);
            }
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_BatchMultiJoinSpec<I>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                    CteItem cteItem, String alias) {
            final OnClauseTableBlock<_BatchMultiJoinSpec<I>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        private _OnClause<_BatchMultiJoinSpec<I>> joinNestedEnd(final _JoinType joinType,
                                                                final NestedItems nestedItems) {
            joinType.assertMySQLJoinType();
            final OnClauseTableBlock<_BatchMultiJoinSpec<I>> block;
            block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
            this.blockConsumer.accept(block);
            return block;
        }


    }// BatchUpdateStatement


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintJoinSpec<I>>
            implements MySQLUpdate._MultiPartitionJoinClause<I> {

        private final SimpleUpdateStatement<I> stmt;

        private SimplePartitionJoinClause(SimpleUpdateStatement<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateStatement<I> stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<MySQLUpdate._MultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleOnTableBlock<I extends Item> extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._MultiIndexHintOnSpec<I>,
            MySQLUpdate._MultiJoinSpec<I>>
            implements MySQLUpdate._MultiIndexHintOnSpec<I> {

        /**
         * @see SimpleUpdateStatement#onJoinTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private SimpleOnTableBlock(_JoinType joinType, TableMeta<?> table, String alias,
                                   MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//SimpleOnTableBlock

    private static final class SimplePartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintOnSpec<I>>
            implements MySQLUpdate._MultiPartitionOnClause<I> {

        private final SimpleUpdateStatement<I> stmt;

        private SimplePartitionOnClause(SimpleUpdateStatement<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateStatement<I> stmt = this.stmt;

            final SimpleOnTableBlock<I> block;
            block = new SimpleOnTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class BatchPartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_BatchMultiIndexHintJoinSpec<I>>
            implements MySQLUpdate._BatchMultiPartitionJoinClause<I> {

        private final BatchUpdateStatement<I> stmt;

        private BatchPartitionJoinClause(BatchUpdateStatement<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateStatement<I> stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<MySQLUpdate._BatchMultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchOnTableBlock<I extends Item> extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._BatchMultiIndexHintOnSpec<I>,
            MySQLUpdate._BatchMultiJoinSpec<I>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<I> {

        private BatchOnTableBlock(_JoinType joinType, TableMeta<?> table, String alias
                , MySQLUpdate._BatchMultiJoinSpec<I> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLUpdate._BatchMultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//BatchOnTableBlock

    private static final class BatchPartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_BatchMultiIndexHintOnSpec<I>>
            implements MySQLUpdate._BatchMultiPartitionOnClause<I> {

        private final BatchUpdateStatement<I> stmt;

        private BatchPartitionOnClause(BatchUpdateStatement<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateStatement<I> stmt = this.stmt;

            final BatchOnTableBlock<I> block;
            block = new BatchOnTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//PartitionAsClause


}
