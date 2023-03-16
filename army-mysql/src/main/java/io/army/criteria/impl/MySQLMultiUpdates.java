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
abstract class MySQLMultiUpdates<I extends Item, WE, FT, SR, FS extends Item, FC extends Item, JT, JS, JC, WR, WA>
        extends JoinableUpdate.WithMultiUpdate<I, MySQLCtes, WE, TableField, SR, FT, FS, FC, JT, JS, JC, WR, WA, Object, Object, Object, Object>
        implements UpdateStatement,
        _MySQLMultiUpdate,
        MySQLUpdate,
        MySQLStatement._IndexHintForJoinClause<FT>,
        Statement._ParensStringClause<FC>,
        MySQLUpdate._MultiUpdateClause<FT, FS>,
        MySQLUpdate._MultiUpdateCteNestedClause<FC>,
        MySQLStatement._MySQLCrossNestedClause<FC>,
        MySQLStatement._MySQLJoinNestedClause<Statement._OnClause<FC>>,
        MySQLStatement._MySQLDynamicJoinCrossClause<FC> {


    static <I extends Item> _MultiWithSpec<I> simple(@Nullable MultiStmtSpec spec,
                                                     Function<? super Update, I> function) {
        return new MySQLSimpleUpdate<>(spec, function);
    }

    static _BatchMultiWithSpec<BatchUpdate> batch() {
        return new MySQLBatchUpdate();
    }

    List<Hint> hintList;

    List<MySQLSyntax.Modifier> modifierList;

    _TableBlock fromCrossBlock;


    private MySQLMultiUpdates(@Nullable MultiStmtSpec spec) {
        super(spec, CriteriaContexts.primaryMultiDmlContext(spec));
    }

    @Override
    public final FT update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                           TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromTable(_JoinType.NONE, null, table, tableAlias);
    }

    @Override
    public final FT update(TableMeta<?> table, SQLsSyntax.WordAs wordAs, String tableAlias) {
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
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromDerived(_JoinType.NONE, derivedModifier(modifier), nonNull(supplier.get()));
    }

    @Override
    public final <T extends DerivedTable> FS update(@Nullable Query.DerivedModifier modifier, Supplier<T> supplier) {
        return this.onFromDerived(_JoinType.NONE, derivedModifier(modifier), nonNull(supplier.get()));
    }

    @Override
    public final FC update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, String cteName) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, String cteName,
                           SQLsSyntax.WordAs wordAs, String alias) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final FC update(String cteName) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), "");
    }

    @Override
    public final FC update(String cteName, SQLsSyntax.WordAs wordAs, String alias) {
        return this.onFromCte(_JoinType.NONE, null, this.context.refCte(cteName), alias);
    }

    @Override
    public final _NestedLeftParenSpec<FC> update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<FC> update() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<FC> crossJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> leftJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> join() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> rightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> fullJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<FC>> straightJoin() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd);
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
        this.getFromClauseDerived().onColumnAlias(_ArrayUtils.unmodifiableListOf(first, rest));
        return (FC) this;
    }

    @Override
    public final FC parens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().onColumnAlias(CriteriaUtils.stringList(this.context, true, consumer));
        return (FC) this;
    }

    @Override
    public final FC ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().onColumnAlias(CriteriaUtils.stringList(this.context, false, consumer));
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

    @Override
    final FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    private FC fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    private _OnClause<FC> joinNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertMySQLJoinType();
        final OnClauseTableBlock<FC> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", (FC) this);
        this.blockConsumer.accept(block);
        return block;
    }

    private TableBlock.ParensDerivedJoinBlock getFromClauseDerived() {
        final _TableBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TableBlock.ParensDerivedJoinBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TableBlock.ParensDerivedJoinBlock) block;
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

        private MySQLSimpleUpdate(@Nullable MultiStmtSpec spec, Function<? super Update, I> function) {
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
        public _MultiWhereSpec<I> sets(Consumer<ItemPairs<TableField>> consumer) {
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
                final TableBlock.ParensDerivedJoinBlock block;
                block = new TableBlock.ParensDerivedJoinBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
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
        public _BatchMultiWhereSpec<BatchUpdate> sets(Consumer<BatchItemPairs<TableField>> consumer) {
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
        public <P> _DmlUpdateSpec<BatchUpdate> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<BatchUpdate> paramList(Function<String, ?> function, String keyName) {
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
        _AsClause<_BatchParensJoinSpec<BatchUpdate>> onFromDerived(_JoinType joinType,
                                                                   @Nullable Query.DerivedModifier modifier, DerivedTable table) {
            return alias -> {
                final TableBlock.ParensDerivedJoinBlock block;
                block = new TableBlock.ParensDerivedJoinBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }


        @Override
        _BatchMultiIndexHintOnSpec<BatchUpdate> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                            TableMeta<?> table, String alias) {
            final BatchOnTableBlock block;
            block = new BatchOnTableBlock(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchMultiJoinSpec<BatchUpdate>> onJoinDerived(_JoinType joinType,
                                                                          @Nullable Query.DerivedModifier modifier,
                                                                          DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_BatchMultiJoinSpec<BatchUpdate>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<BatchUpdate>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                              CteItem cteItem, String alias) {
            final OnClauseTableBlock<_BatchMultiJoinSpec<BatchUpdate>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias);
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
         * @see MySQLSimpleUpdate#onJoinTable(_JoinType, Query.TableModifier, TableMeta, String)
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

        private final MySQLSimpleUpdate<I> stmt;

        private SimplePartitionOnClause(MySQLSimpleUpdate<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLSimpleUpdate<I> stmt = this.stmt;

            final SimpleOnTableBlock<I> block;
            block = new SimpleOnTableBlock<>(params, stmt);
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

            final MySQLSupports.MySQLNoOnBlock<MySQLUpdate._BatchMultiIndexHintJoinSpec<BatchUpdate>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchOnTableBlock extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._BatchMultiIndexHintOnSpec<BatchUpdate>,
            MySQLUpdate._BatchMultiJoinSpec<BatchUpdate>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<BatchUpdate> {

        private BatchOnTableBlock(_JoinType joinType, TableMeta<?> table, String alias,
                                  MySQLUpdate._BatchMultiJoinSpec<BatchUpdate> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params,
                                  MySQLUpdate._BatchMultiJoinSpec<BatchUpdate> stmt) {
            super(params, stmt);
        }


    }//BatchOnTableBlock

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

            final BatchOnTableBlock block;
            block = new BatchOnTableBlock(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//BatchPartitionOnClause


}
