package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
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
abstract class MySQLMultiDeletes<I extends Item, WE extends Item, DT, FU extends Item, FT extends Item, FS extends Item, FC extends Item, JT, JS, JC, WR, WA>
        extends JoinableDelete.WithJoinableDelete<I, MySQLCtes, WE, FT, FS, FC, Void, JT, JS, JC, Void, WR, WA>
        implements MySQLDelete,
        _MySQLMultiDelete,
        MySQLDelete._MultiDeleteHintClause<FU>,
        MySQLDelete._MultiDeleteAliasClause<DT>,
        MySQLStatement._IndexHintForJoinClause<FT>,
        MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexForJoinSpec<Object>, FT>,
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


    _TabularBlock fromCrossBlock;

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
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
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

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }


    private TabularBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
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
    private MySQLSupports.FromClauseForJoinTableBlock<FT> getHintClause() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.FromClauseForJoinTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (MySQLSupports.FromClauseForJoinTableBlock<FT>) block;
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
        _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType,
                                                           @Nullable Query.DerivedModifier modifier,
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
        public <P> _DmlDeleteSpec<BatchDelete> namedParamList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<BatchDelete> namedParamList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<BatchDelete> namedParamList(Function<String, ?> function, String keyName) {
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
                final TabularBlocks.FromClauseAliasDerivedBlock block;
                block = TabularBlocks.fromAliasDerivedBlock(joinType, modifier, table, alias);
                this.blockConsumer.accept(block);
                this.fromCrossBlock = block;
                return this;
            };
        }

        @Override
        _BatchMultiIndexHintOnSpec<BatchDelete> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                                            TableMeta<?> table, String alias) {
            final BatchJoinClauseTableBlock block;
            block = new BatchJoinClauseTableBlock(joinType, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        }

        @Override
        _AsParensOnClause<_BatchMultiJoinSpec<BatchDelete>> onJoinDerived(_JoinType joinType,
                                                                          @Nullable Query.DerivedModifier modifier,
                                                                          DerivedTable table) {
            return alias -> {
                final TabularBlocks.JoinClauseAliasDerivedBlock<_BatchMultiJoinSpec<BatchDelete>> block;
                block = TabularBlocks.joinAliasDerivedBlock(joinType, modifier, table, alias, this);
                this.blockConsumer.accept(block);
                return block;
            };
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<BatchDelete>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                              _Cte cteItem, String alias) {
            final TabularBlocks.JoinClauseCteBlock<_BatchMultiJoinSpec<BatchDelete>> block;
            block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
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

            final MySQLSupports.FromClauseForJoinTableBlock<_MultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.FromClauseForJoinTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleJoinClauseTableBlock<I extends Item>
            extends MySQLSupports.MySQLJoinClauseBlock<
            _IndexForJoinSpec<Object>,
            _MultiIndexHintOnSpec<I>,
            _MultiJoinSpec<I>>
            implements MySQLDelete._MultiIndexHintOnSpec<I> {

        private SimpleJoinClauseTableBlock(_JoinType joinType, TableMeta<?> tableItem, String alias,
                                           MySQLDelete._MultiJoinSpec<I> stmt) {
            super(joinType, tableItem, alias, stmt);
        }

        private SimpleJoinClauseTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//SimpleJoinClauseTableBlock

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

            final SimpleJoinClauseTableBlock<I> block;
            block = new SimpleJoinClauseTableBlock<>(params, stmt);
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

            final MySQLSupports.FromClauseForJoinTableBlock<_BatchMultiIndexHintJoinSpec<BatchDelete>> block;
            block = new MySQLSupports.FromClauseForJoinTableBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.fromCrossBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchJoinClauseTableBlock
            extends MySQLSupports.MySQLJoinClauseBlock<
            _IndexForJoinSpec<Object>,
            _BatchMultiIndexHintOnSpec<BatchDelete>,
            _BatchMultiJoinSpec<BatchDelete>>
            implements MySQLDelete._BatchMultiIndexHintOnSpec<BatchDelete> {

        private BatchJoinClauseTableBlock(_JoinType joinType, TableMeta<?> tableItem, String alias,
                                          MySQLDelete._BatchMultiJoinSpec<BatchDelete> stmt) {
            super(joinType, tableItem, alias, stmt);
        }

        private BatchJoinClauseTableBlock(MySQLSupports.MySQLBlockParams params,
                                          MySQLDelete._BatchMultiJoinSpec<BatchDelete> stmt) {
            super(params, stmt);
        }

    }//BatchJoinClauseTableBlock

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

            final BatchJoinClauseTableBlock block;
            block = new BatchJoinClauseTableBlock(params, stmt);
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
