package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLSimpleUpdate} ,MySQL multi update api implementation</li>
 *         <li>{@link MySQLBatchUpdate} ,MySQL batch multi update api implementation</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiUpdates<I extends Item, WE extends Item, FT extends Item, SR, FS extends Item, FC extends Item, JT, JS, JC, WR, WA>
        extends JoinableUpdate.WithMultiUpdate<I, MySQLCtes, WE, TableField, SR, FT, FS, FC, Void, JT, JS, JC, Void, WR, WA>
        implements UpdateStatement,
        _MySQLMultiUpdate,
        MySQLUpdate,
        MySQLStatement._IndexHintForJoinClause<FT>,
        MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexForJoinSpec<Object>, FT>,
        Statement._OptionalParensStringClause<FC>,
        MySQLUpdate._MultiUpdateClause<FT, FS>,
        MySQLUpdate._MultiUpdateCteNestedClause<FC>,
        MySQLStatement._MySQLCrossNestedClause<FC>,
        MySQLStatement._MySQLJoinNestedClause<Statement._OnClause<FC>>,
        MySQLStatement._MySQLDynamicJoinCrossClause<FC> {


    static <I extends Item> _MultiWithSpec<I> simple(@Nullable ArmyStmtSpec spec,
                                                     Function<? super Update, I> function) {
        return new MySQLSimpleUpdate<>(spec, function);
    }

    static _BatchMultiWithSpec<BatchUpdate> batch() {
        return new MySQLBatchUpdate();
    }

    List<Hint> hintList;

    List<MySQLSyntax.Modifier> modifierList;

    _TabularBlock fromCrossBlock;


    private MySQLMultiUpdates(@Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primaryMultiDmlContext(MySQLUtils.DIALECT, spec));
    }

    @Override
    public final FT update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                           TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final FT update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final <T extends DerivedTable> FS update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                                                    Supplier<T> supplier) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromDerived(_JoinType.NONE, null, this.nonNull(supplier.get()));
    }

    @Override
    public final <T extends DerivedTable> FS update(Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.NONE, null, this.nonNull(supplier.get()));
    }

    @Override
    public final <T extends DerivedTable> FS update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                                                    @Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        if (this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromDerived(_JoinType.NONE, modifier, nonNull(supplier.get()));
    }

    @Override
    public final <T extends DerivedTable> FS update(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        if (this.isIllegalDerivedModifier(modifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        return this.onFromDerived(_JoinType.NONE, modifier, nonNull(supplier.get()));
    }

    @Override
    public final FC update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, String cteName) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, String cteName,
                           SQLs.WordAs wordAs, String alias) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final FC update(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC update(String cteName, SQLs.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final FC update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, Function<_NestedLeftParenSpec<FC>, FC> function) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final FC update(Function<_NestedLeftParenSpec<FC>, FC> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final FC crossJoin(Function<_NestedLeftParenSpec<FC>, FC> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<FC> leftJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<FC> join(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<FC> rightJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<FC> fullJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<FC> straightJoin(Function<_NestedLeftParenSpec<_OnClause<FC>>, _OnClause<FC>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final FC ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifLeftJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifRightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifFullJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC ifStraightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
        return (FC) this;
    }

    @Override
    public final FC parens(String first, String... rest) {
        this.getFromClauseDerived().parens(first, rest);
        return (FC) this;
    }

    @Override
    public final FC parens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().parens(this.context, consumer);
        return (FC) this;
    }

    @Override
    public final FC ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().ifParens(this.context, consumer);
        return (FC) this;
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
    public final FT ifUseIndex(Consumer<_IndexForJoinSpec<Object>> consumer) {
        this.getHintClause().ifUseIndex(consumer);
        return (FT) this;
    }

    @Override
    public final FT ifIgnoreIndex(Consumer<_IndexForJoinSpec<Object>> consumer) {
        this.getHintClause().ifIgnoreIndex(consumer);
        return (FT) this;
    }

    @Override
    public final FT ifForceIndex(Consumer<_IndexForJoinSpec<Object>> consumer) {
        this.getHintClause().ifForceIndex(consumer);
        return (FT) this;
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

    abstract I asMySQLUpdate();


    @Override
    final I onAsUpdate() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        return this.asMySQLUpdate();
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        final Object thisStmt = this;
        if (thisStmt instanceof MySQLBatchUpdate) {
            ((MySQLBatchUpdate) thisStmt).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }


    @Override
    final FT onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final MySQLSupports.FromClauseForJoinTableBlock<FT> block;
        block = new MySQLSupports.FromClauseForJoinTableBlock<>(joinType, table, alias, (FT) this);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FT) this;
    }

    @Override
    final FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    private FC fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final _TabularBlock block;
        block = TabularBlocks.fromNestedBlock(joinType, nestedItems);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    private _OnClause<FC> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final TabularBlocks.JoinClauseNestedBlock<FC> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, (FC) this);
        this.blockConsumer.accept(block);
        return block;
    }

    private TabularBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLSupports.FromClauseForJoinTableBlock<FT> getHintClause() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.FromClauseForJoinTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (MySQLSupports.FromClauseForJoinTableBlock<FT>) block;
    }


    /*################################## blow inner class  ##################################*/

    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class MySQLSimpleUpdate<I extends Item> extends MySQLMultiUpdates<
            I,
            _SimpleMultiUpdateClause<I>,
            _MultiIndexHintJoinSpec<I>,
            _MultiWhereSpec<I>,
            _AsClause<_ParensJoinSpec<I>>,
            _MultiJoinSpec<I>,
            _MultiIndexHintOnSpec<I>,
            _AsParensOnClause<_MultiJoinSpec<I>>,
            _OnClause<_MultiJoinSpec<I>>,
            _DmlUpdateSpec<I>,
            _MultiWhereAndSpec<I>>
            implements MySQLUpdate._MultiWithSpec<I>,
            MySQLUpdate._MultiIndexHintJoinSpec<I>,
            MySQLUpdate._ParensJoinSpec<I>,
            MySQLUpdate._MultiWhereSpec<I>,
            MySQLUpdate._MultiWhereAndSpec<I>,
            Update {

        private final Function<? super Update, I> function;

        private MySQLSimpleUpdate(@Nullable ArmyStmtSpec spec, Function<? super Update, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SimpleMultiUpdateClause<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SimpleMultiUpdateClause<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _MultiWhereSpec<I> sets(Consumer<_ItemPairs<TableField>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _MultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                                                   TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _MultiPartitionJoinClause<I> update(TableMeta<?> table) {
            return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
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
        _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                    DerivedTable table) {
            return alias -> {
                final TabularBlocks.FromClauseAliasDerivedBlock block;
                block = TabularBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }


        @Override
        _MultiIndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                             TableMeta<?> table, String alias) {
            final SimpleJoinClauseTableBlock<I> block;
            block = new SimpleJoinClauseTableBlock<>(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                           DerivedTable table) {
            return alias -> {
                final TabularBlocks.JoinClauseAliasDerivedBlock<_MultiJoinSpec<I>> block;
                block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_MultiJoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                               _Cte cteItem, String alias) {
            final TabularBlocks.JoinClauseCteBlock<_MultiJoinSpec<I>> block;
            block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        I asMySQLUpdate() {
            return this.function.apply(this);
        }


    }// MySQLSimpleUpdate


    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     */
    private static final class MySQLBatchUpdate extends MySQLMultiUpdates<
            BatchUpdate,
            _BatchMultiUpdateClause<BatchUpdate>,
            _BatchMultiIndexHintJoinSpec<BatchUpdate>,
            _BatchMultiWhereSpec<BatchUpdate>,
            _AsClause<_BatchParensJoinSpec<BatchUpdate>>,
            _BatchMultiJoinSpec<BatchUpdate>,
            _BatchMultiIndexHintOnSpec<BatchUpdate>,
            _AsParensOnClause<_BatchMultiJoinSpec<BatchUpdate>>,
            _OnClause<_BatchMultiJoinSpec<BatchUpdate>>,
            _BatchParamClause<_DmlUpdateSpec<BatchUpdate>>,
            _BatchMultiWhereAndSpec<BatchUpdate>>
            implements MySQLUpdate._BatchMultiWithSpec<BatchUpdate>,
            MySQLUpdate._BatchMultiIndexHintJoinSpec<BatchUpdate>,
            MySQLUpdate._BatchParensJoinSpec<BatchUpdate>,
            MySQLUpdate._BatchMultiWhereSpec<BatchUpdate>,
            MySQLUpdate._BatchMultiWhereAndSpec<BatchUpdate>,
            BatchUpdate,
            _BatchDml {


        private List<?> paramList;


        private MySQLBatchUpdate() {
            super(null);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchMultiUpdateClause<BatchUpdate>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchMultiUpdateClause<BatchUpdate>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _BatchMultiWhereSpec<BatchUpdate> sets(Consumer<_BatchItemPairs<TableField>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _BatchMultiPartitionJoinClause<BatchUpdate> update(Supplier<List<Hint>> hints,
                                                                  List<MySQLSyntax.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new BatchPartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<BatchUpdate> update(TableMeta<?> table) {
            return new BatchPartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchUpdate> leftJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.LEFT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchUpdate> join(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchUpdate> rightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.RIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchUpdate> fullJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.FULL_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchUpdate> straightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.STRAIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<BatchUpdate> crossJoin(TableMeta<?> table) {
            return new BatchPartitionJoinClause(this, _JoinType.CROSS_JOIN, table);
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> namedParamList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> namedParamList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<BatchUpdate> namedParamList(Function<String, ?> function, String keyName) {
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
        _AsClause<_BatchParensJoinSpec<BatchUpdate>> onFromDerived(
                _JoinType joinType, @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final TabularBlocks.FromClauseAliasDerivedBlock block;
                block = TabularBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }


        @Override
        _BatchMultiIndexHintOnSpec<BatchUpdate> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                            TableMeta<?> table, String alias) {
            final BatchJoinClauseTableBlock block;
            block = new BatchJoinClauseTableBlock(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchMultiJoinSpec<BatchUpdate>> onJoinDerived(_JoinType joinType,
                                                                          @Nullable Query.DerivedModifier modifier,
                                                                          DerivedTable table) {
            return alias -> {
                final TabularBlocks.JoinClauseAliasDerivedBlock<_BatchMultiJoinSpec<BatchUpdate>> block;
                block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<BatchUpdate>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                              _Cte cteItem, String alias) {
            final TabularBlocks.JoinClauseCteBlock<_BatchMultiJoinSpec<BatchUpdate>> block;
            block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        BatchUpdate asMySQLUpdate() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }

    }// MySQLBatchUpdate


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintJoinSpec<I>>
            implements MySQLUpdate._MultiPartitionJoinClause<I> {

        private final MySQLSimpleUpdate<I> stmt;

        private SimplePartitionJoinClause(MySQLSimpleUpdate<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLSimpleUpdate<I> stmt = this.stmt;

            final MySQLSupports.FromClauseForJoinTableBlock<_MultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.FromClauseForJoinTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleJoinClauseTableBlock<I extends Item> extends MySQLSupports.MySQLJoinClauseBlock<
            _IndexForJoinSpec<Object>,
            _MultiIndexHintOnSpec<I>,
            _MultiJoinSpec<I>>
            implements MySQLUpdate._MultiIndexHintOnSpec<I> {

        /**
         * @see MySQLSimpleUpdate#onJoinTable(_JoinType, Query.TableModifier, TableMeta, String)
         */
        private SimpleJoinClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias,
                                           MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(joinType, table, alias, stmt);
        }

        private SimpleJoinClauseTableBlock(MySQLSupports.MySQLBlockParams params,
                                           MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//SimpleJoinClauseTableBlock

    private static final class SimplePartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintOnSpec<I>>
            implements MySQLUpdate._MultiPartitionOnClause<I> {

        private final MySQLSimpleUpdate<I> stmt;

        private SimplePartitionOnClause(MySQLSimpleUpdate<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLSimpleUpdate<I> stmt = this.stmt;

            final SimpleJoinClauseTableBlock<I> block;
            block = new SimpleJoinClauseTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class BatchPartitionJoinClause
            extends MySQLSupports.PartitionAsClause<_BatchMultiIndexHintJoinSpec<BatchUpdate>>
            implements MySQLUpdate._BatchMultiPartitionJoinClause<BatchUpdate> {

        private final MySQLBatchUpdate stmt;

        private BatchPartitionJoinClause(MySQLBatchUpdate stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintJoinSpec<BatchUpdate> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLBatchUpdate stmt = this.stmt;

            final MySQLSupports.FromClauseForJoinTableBlock<_BatchMultiIndexHintJoinSpec<BatchUpdate>> block;
            block = new MySQLSupports.FromClauseForJoinTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchJoinClauseTableBlock extends MySQLSupports.MySQLJoinClauseBlock<
            _IndexForJoinSpec<Object>,
            _BatchMultiIndexHintOnSpec<BatchUpdate>,
            _BatchMultiJoinSpec<BatchUpdate>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<BatchUpdate> {

        private BatchJoinClauseTableBlock(_JoinType joinType, TableMeta<?> table, String alias,
                                          MySQLUpdate._BatchMultiJoinSpec<BatchUpdate> stmt) {
            super(joinType, table, alias, stmt);
        }

        private BatchJoinClauseTableBlock(MySQLSupports.MySQLBlockParams params,
                                          MySQLUpdate._BatchMultiJoinSpec<BatchUpdate> stmt) {
            super(params, stmt);
        }

    }//BatchJoinClauseTableBlock

    private static final class BatchPartitionOnClause
            extends MySQLSupports.PartitionAsClause<_BatchMultiIndexHintOnSpec<BatchUpdate>>
            implements MySQLUpdate._BatchMultiPartitionOnClause<BatchUpdate> {

        private final MySQLBatchUpdate stmt;

        private BatchPartitionOnClause(MySQLBatchUpdate stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintOnSpec<BatchUpdate> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLBatchUpdate stmt = this.stmt;

            final BatchJoinClauseTableBlock block;
            block = new BatchJoinClauseTableBlock(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//BatchPartitionOnClause


}
