package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._ArrayUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is the implementation of MySQL 8.0 multi-table delete syntax.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiDeletes<I extends Item, WE, DT, FU extends Item, FT, FS extends Item, FC extends Item, JT, JS, JC, WR, WA>
        extends JoinableDelete.WithJoinableDelete<I, MySQLCtes, WE, FT, FS, FC, JT, JS, JC, WR, WA>
        implements MySQLDelete,
        _MySQLMultiDelete,
        MySQLDelete._MultiDeleteHintClause<FU>,
        MySQLDelete._MultiDeleteAliasClause<DT>,
        MySQLQuery._IndexHintForJoinClause<FT>,
        Statement._OptionalParensStringClause<FC>,
        MySQLStatement._MySQLCrossNestedClause<FC>,
        MySQLStatement._MySQLUsingNestedClause<FC>,
        MySQLStatement._MySQLFromNestedClause<FC>,
        MySQLStatement._MySQLJoinNestedClause<Statement._OnClause<FC>>,
        MySQLStatement._MySQLDynamicJoinCrossClause<FC> {


    static <I extends Item> _MultiWithSpec<I> simple(@Nullable ArmyStmtSpec spec,
                                                     Function<? super Delete, I> function) {
        return new MySQLSimpleMultiDelete<>(spec, function);
    }

    static _BatchMultiWithSpec<BatchDelete> batch() {
        return new MySQLBatchMultiDelete();
    }

    private List<Hint> hintList;

    private List<MySQLs.Modifier> modifierList;

    boolean usingSyntax;

    private List<String> tableAliasList;

    private List<_Pair<String, TableMeta<?>>> deleteTablePairList;


    _TableBlock fromCrossBlock;

    private MySQLMultiDeletes(@Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primaryMultiDmlContext(spec));
    }

    @Override
    public final MySQLDelete._MultiDeleteFromAliasClause<FU> delete(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return new MySQLFromAliasClause<>(this::fromAliasClauseEnd);
    }

    @Override
    public final DT delete(String alias) {
        this.tableAliasList = Collections.singletonList(alias);
        return (DT) this;
    }

    @Override
    public final DT delete(String alias1, String alias2) {
        this.tableAliasList = Arrays.asList(alias1, alias2);
        return (DT) this;
    }

    @Override
    public final DT delete(String alias1, String alias2, String alias3) {
        this.tableAliasList = Arrays.asList(alias1, alias2, alias3);
        return (DT) this;
    }

    @Override
    public final DT delete(String alias1, String alias2, String alias3, String alias4) {
        this.tableAliasList = Arrays.asList(alias1, alias2, alias3, alias4);
        return (DT) this;
    }

    @Override
    public final DT delete(final List<String> aliasList) {
        this.tableAliasList = aliasList;
        return (DT) this;
    }

    @Override
    public final DT delete(Consumer<Consumer<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        this.tableAliasList = list;
        return (DT) this;
    }


    @Override
    public final FC from(Function<_NestedLeftParenSpec<FC>, FC> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final FC using(Function<_NestedLeftParenSpec<FC>, FC> function) {
        this.usingSyntax = true;
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

    @Override
    public final List<_Pair<String, TableMeta<?>>> deleteTableList() {
        final List<_Pair<String, TableMeta<?>>> list = this.deleteTablePairList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final boolean isUsingSyntax() {
        return this.usingSyntax;
    }

    abstract I asMySQLDelete();


    @Override
    final I onAsDelete() {
        if (this.deleteTablePairList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.asMySQLDelete();
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.deleteTablePairList = null;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mySQLCteBuilder(recursive, this.context);
    }

    @Override
    final void onEndStatement() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        final List<String> tableAliasList = this.tableAliasList;
        final int tableAliasSize;
        if (tableAliasList == null || (tableAliasSize = tableAliasList.size()) == 0) {
            throw ContextStack.criteriaError(this.context, "table alias list must non-empty.");
        }
        List<_Pair<String, TableMeta<?>>> pairList;
        TableMeta<?> table;
        if (tableAliasSize == 1) {
            final String tableAlias = tableAliasList.get(0);
            table = this.context.getTable(tableAlias);
            if (table == null) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownTableAlias, tableAlias);
            }
            pairList = Collections.singletonList(_Pair.create(tableAlias, table));
        } else {
            pairList = new ArrayList<>(tableAliasSize);
            for (String tableAlias : tableAliasList) {
                table = this.context.getTable(tableAlias);
                if (table == null) {
                    throw ContextStack.criteriaError(this.context, _Exceptions::unknownTableAlias, tableAlias);
                }
                pairList.add(_Pair.create(tableAlias, table));
            }
            pairList = Collections.unmodifiableList(pairList);
        }
        this.tableAliasList = null;
        this.deleteTablePairList = pairList;

    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable Query.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }

    @Override
    final FT onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final MySQLSupports.MySQLFromClauseTableBlock<FT> block;
        block = new MySQLSupports.MySQLFromClauseTableBlock<>(joinType, null, table, alias, (FT) this);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FT) this;
    }

    @Override
    final FC onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, CteItem cteItem, String alias) {
        final TableBlocks.NoOnTableBlock block;
        block = new TableBlocks.NoOnTableBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return (FC) this;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }


    private TableBlocks.ParensDerivedJoinBlock getFromClauseDerived() {
        final _TableBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TableBlocks.ParensDerivedJoinBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TableBlocks.ParensDerivedJoinBlock) block;
    }

    private FC fromNestedEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlocks.NoOnTableBlock block;
        block = new TableBlocks.NoOnTableBlock(joinType, nestedItems, "");
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

    /**
     * @see #delete(Supplier, List)
     */
    private FU fromAliasClauseEnd(List<String> list) {
        this.tableAliasList = list;
        return (FU) this;
    }

    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLQuery._IndexHintForJoinClause<FT> getHintClause() {
        final _TableBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.MySQLFromClauseTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return ((MySQLSupports.MySQLFromClauseTableBlock<FT>) block).getUseIndexClause();
    }


    private static final class MySQLSimpleMultiDelete<I extends Item> extends MySQLMultiDeletes<
            I,
            MySQLDelete._SimpleMultiDeleteClause<I>,
            MySQLDelete._MultiDeleteFromTableClause<I>,
            MySQLDelete._SimpleMultiDeleteUsingClause<I>,
            MySQLDelete._MultiIndexHintJoinSpec<I>,
            Statement._AsClause<_ParensJoinSpec<I>>,
            MySQLDelete._MultiJoinSpec<I>,
            MySQLDelete._MultiIndexHintOnSpec<I>,
            Statement._AsParensOnClause<_MultiJoinSpec<I>>,
            Statement._OnClause<_MultiJoinSpec<I>>,
            Statement._DmlDeleteSpec<I>,
            MySQLDelete._MultiWhereAndSpec<I>>
            implements MySQLDelete._MultiWithSpec<I>,
            MySQLDelete._MultiDeleteFromTableClause<I>,
            MySQLDelete._SimpleMultiDeleteUsingClause<I>,
            MySQLDelete._MultiIndexHintJoinSpec<I>,
            MySQLDelete._ParensJoinSpec<I>,
            MySQLDelete._MultiWhereAndSpec<I>,
            Delete {

        private final Function<? super Delete, I> function;

        private MySQLSimpleMultiDelete(@Nullable ArmyStmtSpec spec, Function<? super Delete, I> function) {
            super(spec);
            this.function = function;
        }


        @Override
        public MySQLQuery._StaticCteParensSpec<_SimpleMultiDeleteClause<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SimpleMultiDeleteClause<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _MultiPartitionJoinClause<I> from(TableMeta<?> table) {
            return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _MultiPartitionJoinClause<I> using(TableMeta<?> table) {
            this.usingSyntax = true;
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
                final TableBlocks.ParensDerivedJoinBlock block;
                block = new TableBlocks.ParensDerivedJoinBlock(joinType, modifier, table, alias);
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
        _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType,
                                                           @Nullable Query.DerivedModifier modifier,
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
        I asMySQLDelete() {
            return this.function.apply(this);
        }


    }//MySQLSimpleMultiDelete


    private static final class MySQLBatchMultiDelete extends MySQLMultiDeletes<
            BatchDelete,
            _BatchMultiDeleteClause<BatchDelete>,
            _BatchMultiDeleteFromTableClause<BatchDelete>,
            _BatchMultiDeleteUsingTableClause<BatchDelete>,
            _BatchMultiIndexHintJoinSpec<BatchDelete>,
            _AsClause<_BatchParensJoinSpec<BatchDelete>>,
            _BatchMultiJoinSpec<BatchDelete>,
            _BatchMultiIndexHintOnSpec<BatchDelete>,
            _AsParensOnClause<_BatchMultiJoinSpec<BatchDelete>>,
            _OnClause<_BatchMultiJoinSpec<BatchDelete>>,
            _BatchParamClause<_DmlDeleteSpec<BatchDelete>>,
            _BatchMultiWhereAndSpec<BatchDelete>>
            implements MySQLDelete._BatchMultiWithSpec<BatchDelete>,
            MySQLDelete._BatchMultiDeleteFromTableClause<BatchDelete>,
            MySQLDelete._BatchMultiDeleteUsingTableClause<BatchDelete>,
            MySQLDelete._BatchMultiIndexHintJoinSpec<BatchDelete>,
            MySQLDelete._BatchParensJoinSpec<BatchDelete>,
            MySQLDelete._BatchMultiWhereAndSpec<BatchDelete>,
            BatchDelete,
            _BatchDml {

        private List<?> paramList;

        private MySQLBatchMultiDelete() {
            super(null);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchMultiDeleteClause<BatchDelete>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchMultiDeleteClause<BatchDelete>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _BatchMultiPartitionJoinClause<BatchDelete> from(TableMeta<?> table) {
            return new BatchPartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<BatchDelete> using(TableMeta<?> table) {
            this.usingSyntax = true;
            return new BatchPartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchDelete> leftJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.LEFT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchDelete> join(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchDelete> rightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.RIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchDelete> fullJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.FULL_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<BatchDelete> straightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.STRAIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<BatchDelete> crossJoin(TableMeta<?> table) {
            return new BatchPartitionJoinClause(this, _JoinType.CROSS_JOIN, table);
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<BatchDelete> paramList(Function<String, ?> function, String keyName) {
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
        _AsClause<_BatchParensJoinSpec<BatchDelete>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                                   DerivedTable table) {
            return alias -> {
                final TableBlocks.NoOnModifierDerivedBlock block;
                block = new TableBlocks.NoOnModifierDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }

        @Override
        _BatchMultiIndexHintOnSpec<BatchDelete> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                            TableMeta<?> table, String alias) {
            final BatchOnTableBlock block;
            block = new BatchOnTableBlock(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchMultiJoinSpec<BatchDelete>> onJoinDerived(_JoinType joinType,
                                                                          @Nullable Query.DerivedModifier modifier,
                                                                          DerivedTable table) {
            return alias -> {
                final OnClauseTableBlock.OnModifierParensBlock<_BatchMultiJoinSpec<BatchDelete>> block;
                block = new OnClauseTableBlock.OnModifierParensBlock<>(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<BatchDelete>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                              CteItem cteItem, String alias) {
            final OnClauseTableBlock<_BatchMultiJoinSpec<BatchDelete>> block;
            block = new OnClauseTableBlock<>(joinType, cteItem, alias);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        BatchDelete asMySQLDelete() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }


    }//MySQLBatchMultiDelete


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintJoinSpec<I>>
            implements MySQLDelete._MultiPartitionJoinClause<I> {

        private final MySQLSimpleMultiDelete<I> stmt;

        private SimplePartitionJoinClause(MySQLSimpleMultiDelete<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLSimpleMultiDelete<I> stmt = this.stmt;

            final MySQLSupports.MySQLFromClauseTableBlock<_MultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLFromClauseTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleOnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<
            MySQLDelete._MultiIndexHintOnSpec<I>,
            MySQLDelete._MultiJoinSpec<I>>
            implements MySQLDelete._MultiIndexHintOnSpec<I> {

        private SimpleOnTableBlock(_JoinType joinType, TabularItem tableItem, String alias
                , MySQLDelete._MultiJoinSpec<I> stmt) {
            super(joinType, null, tableItem, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//SimpleOnTableBlock

    private static final class SimplePartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintOnSpec<I>>
            implements MySQLDelete._MultiPartitionOnClause<I> {

        private final MySQLSimpleMultiDelete<I> stmt;

        private SimplePartitionOnClause(MySQLSimpleMultiDelete<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLSimpleMultiDelete<I> stmt = this.stmt;

            final SimpleOnTableBlock<I> block;
            block = new SimpleOnTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class BatchPartitionJoinClause
            extends MySQLSupports.PartitionAsClause<_BatchMultiIndexHintJoinSpec<BatchDelete>>
            implements MySQLDelete._BatchMultiPartitionJoinClause<BatchDelete> {

        private final MySQLBatchMultiDelete stmt;

        private BatchPartitionJoinClause(MySQLBatchMultiDelete stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._BatchMultiIndexHintJoinSpec<BatchDelete> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLBatchMultiDelete stmt = this.stmt;

            final MySQLSupports.MySQLFromClauseTableBlock<_BatchMultiIndexHintJoinSpec<BatchDelete>> block;
            block = new MySQLSupports.MySQLFromClauseTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchOnTableBlock
            extends MySQLSupports.MySQLOnBlock<
            MySQLDelete._BatchMultiIndexHintOnSpec<BatchDelete>
            , MySQLDelete._BatchMultiJoinSpec<BatchDelete>>
            implements MySQLDelete._BatchMultiIndexHintOnSpec<BatchDelete> {

        private BatchOnTableBlock(_JoinType joinType, TabularItem tableItem, String alias
                , MySQLDelete._BatchMultiJoinSpec<BatchDelete> stmt) {
            super(joinType, null, tableItem, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._BatchMultiJoinSpec<BatchDelete> stmt) {
            super(params, stmt);
        }


    }//BatchOnTableBlock

    private static final class BatchPartitionOnClause
            extends MySQLSupports.PartitionAsClause<_BatchMultiIndexHintOnSpec<BatchDelete>>
            implements MySQLDelete._BatchMultiPartitionOnClause<BatchDelete> {

        private final MySQLBatchMultiDelete stmt;

        private BatchPartitionOnClause(MySQLBatchMultiDelete stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._BatchMultiIndexHintOnSpec<BatchDelete> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLBatchMultiDelete stmt = this.stmt;

            final BatchOnTableBlock block;
            block = new BatchOnTableBlock(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//BatchPartitionOnClause


    private static final class MySQLFromAliasClause<R extends Item>
            implements MySQLDelete._MultiDeleteFromAliasClause<R> {

        private final Function<List<String>, R> function;

        private MySQLFromAliasClause(Function<List<String>, R> function) {
            this.function = function;
        }

        @Override
        public R from(String alias) {
            return this.function.apply(Collections.singletonList(alias));
        }

        @Override
        public R from(String alias1, String alias2) {
            return this.function.apply(Arrays.asList(alias1, alias2));
        }

        @Override
        public R from(String alias1, String alias2, String alias3) {
            return this.function.apply(Arrays.asList(alias1, alias2, alias3));
        }

        @Override
        public R from(String alias1, String alias2, String alias3, String alias4) {
            return this.function.apply(Arrays.asList(alias1, alias2, alias3, alias4));
        }

        @Override
        public R from(List<String> aliasList) {
            return this.function.apply(aliasList);
        }

        @Override
        public R from(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            return this.function.apply(list);
        }

    }//MySQLFromAliasClause


}
