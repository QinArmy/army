package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.ArrayList;
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
abstract class MySQLMultiDeletes<I extends Item, BI extends Item> extends JoinableDelete<
        I,
        BI,
        MySQLCtes,
        MySQLDelete._MySQLMultiDeleteClause<I>,
        MySQLDelete._MultiIndexHintJoinSpec<I>,
        Statement._AsClause<MySQLDelete._ParensJoinSpec<I>>,
        MySQLDelete._MultiJoinSpec<I>,
        Void,
        MySQLDelete._MultiIndexHintOnSpec<I>,
        Statement._AsParensOnClause<MySQLDelete._MultiJoinSpec<I>>,
        Statement._OnClause<MySQLDelete._MultiJoinSpec<I>>,
        Void,
        Statement._DmlDeleteSpec<I>,
        MySQLDelete._MultiWhereAndSpec<I>>
        implements MySQLDelete,
        _MySQLMultiDelete,
        MySQLDelete._MultiWithSpec<I>,
        MySQLDelete._MultiDeleteFromTableClause<I>,
        MySQLDelete._SimpleMultiDeleteUsingClause<I>,
        MySQLDelete._MultiIndexHintJoinSpec<I>,
        MySQLDelete._ParensJoinSpec<I>,
        MySQLDelete._MultiWhereAndSpec<I>,
        BatchDeleteSpec<BI>,
        JoinableClause.UsingClauseListener {


    static _MultiWithSpec<Delete> simple() {
        return new MySQLSimpleMultiDelete<>(null, SQLs.SIMPLE_DELETE, SQLs.ERROR_FUNC);
    }

    static _MultiWithSpec<_BatchParamClause<BatchDelete>> batch() {
        return new MySQLSimpleMultiDelete<>(null, SQLs::forBatchDelete, SQLs.BATCH_DELETE);
    }

    private final Function<? super BatchDeleteSpec<BI>, I> function;

    private final Function<? super BatchDelete, BI> batchFunc;

    private List<Hint> hintList;

    private List<MySQLs.Modifier> modifierList;

    boolean usingSyntax;

    private List<String> tableAliasList;

    private List<_Pair<String, TableMeta<?>>> deleteTablePairList;


    _TabularBlock fromCrossBlock;

    private MySQLMultiDeletes(@Nullable ArmyStmtSpec spec, Function<? super BatchDeleteSpec<BI>, I> function,
                              Function<? super BatchDelete, BI> batchFunc) {
        super(spec, CriteriaContexts.primaryMultiDmlContext(MySQLUtils.DIALECT, spec));
        this.function = function;
        this.batchFunc = batchFunc;
    }


    @Override
    public final MySQLQuery._StaticCteParensSpec<_MySQLMultiDeleteClause<I>> with(String name) {
        return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_MySQLMultiDeleteClause<I>> withRecursive(String name) {
        return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _MultiDeleteFromAliasClause<I> delete(Supplier<List<Hint>> hints,
                                                       List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return new MySQLFromAliasClause<>(this);
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias) {
        this.tableAliasList = _Collections.singletonList(alias);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias1, String alias2) {
        this.tableAliasList = ArrayUtils.of(alias1, alias2);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3) {
        this.tableAliasList = ArrayUtils.of(alias1, alias2, alias3);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3, String alias4) {
        this.tableAliasList = ArrayUtils.of(alias1, alias2, alias3, alias4);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(List<String> aliasList) {
        this.tableAliasList = _Collections.asUnmodifiableList(aliasList);
        return this;
    }

    @Override
    public final _MultiDeleteFromTableClause<I> delete(Consumer<Consumer<String>> consumer) {
        CriteriaUtils.stringList(this.context, true, consumer);
        return this;
    }

    @Override
    public final _MultiPartitionJoinClause<I> from(TableMeta<?> table) {
        return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _MultiPartitionJoinClause<I> using(TableMeta<?> table) {
        this.usingSyntax = true;
        return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> leftJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> join(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> rightJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> fullJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final _MultiPartitionOnClause<I> straightJoin(TableMeta<?> table) {
        return new SimplePartitionOnClause<>(this, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final _MultiPartitionJoinClause<I> crossJoin(TableMeta<?> table) {
        return new SimplePartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
    }


    @Override
    public final MySQLDelete._MultiJoinSpec<I> from(Function<_NestedLeftParenSpec<MySQLDelete._MultiJoinSpec<I>>, MySQLDelete._MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> using(Function<_NestedLeftParenSpec<MySQLDelete._MultiJoinSpec<I>>, MySQLDelete._MultiJoinSpec<I>> function) {
        this.usingSyntax = true;
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> crossJoin(Function<_NestedLeftParenSpec<MySQLDelete._MultiJoinSpec<I>>, MySQLDelete._MultiJoinSpec<I>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> join(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<MySQLDelete._MultiJoinSpec<I>> straightJoin(Function<_NestedLeftParenSpec<_OnClause<MySQLDelete._MultiJoinSpec<I>>>, _OnClause<MySQLDelete._MultiJoinSpec<I>>> function) {
        return function.apply(MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
        consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifLeftJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifRightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifFullJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifStraightJoin(Consumer<MySQLJoins> consumer) {
        consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> parens(String first, String... rest) {
        this.getFromClauseDerived().parens(first, rest);
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> parens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().parens(this.context, consumer);
        return this;
    }

    @Override
    public final MySQLDelete._MultiJoinSpec<I> ifParens(Consumer<Consumer<String>> consumer) {
        this.getFromClauseDerived().ifParens(this.context, consumer);
        return this;
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<MySQLDelete._MultiIndexHintJoinSpec<I>> useIndex() {
        return this.getHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<MySQLDelete._MultiIndexHintJoinSpec<I>> ignoreIndex() {
        return this.getHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<MySQLDelete._MultiIndexHintJoinSpec<I>> forceIndex() {
        return this.getHintClause().forceIndex();
    }

    @Override
    public final MySQLDelete._MultiIndexHintJoinSpec<I> ifUseIndex(Consumer<_IndexForJoinSpec<Object>> consumer) {
        this.getHintClause().ifUseIndex(consumer);
        return this;
    }

    @Override
    public final MySQLDelete._MultiIndexHintJoinSpec<I> ifIgnoreIndex(Consumer<_IndexForJoinSpec<Object>> consumer) {
        this.getHintClause().ifIgnoreIndex(consumer);
        return this;
    }

    @Override
    public final MySQLDelete._MultiIndexHintJoinSpec<I> ifForceIndex(Consumer<_IndexForJoinSpec<Object>> consumer) {
        this.getHintClause().ifForceIndex(consumer);
        return this;
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
    public final void onUsing() {
        this.usingSyntax = true;
    }

    @Override
    public final boolean isUsingSyntax() {
        return this.usingSyntax;
    }


    @Override
    final I onAsDelete() {
        if (this.deleteTablePairList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.function.apply(this);
    }

    @Override
    final BI onAsBatchUpdate(List<?> paramList) {
        return this.batchFunc.apply(new MySQLBatchDelete(this, paramList));
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
    final MySQLDelete._MultiIndexHintJoinSpec<I> onFromTable(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        final MySQLSupports.FromClauseForJoinTableBlock<MySQLDelete._MultiIndexHintJoinSpec<I>> block;
        block = new MySQLSupports.FromClauseForJoinTableBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    @Override
    final MySQLDelete._MultiJoinSpec<I> onFromCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier, _Cte cteItem, String alias) {
        final _TabularBlock block;
        block = TabularBlocks.fromCteBlock(joinType, cteItem, alias);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }


    @Override
    final _AsClause<_ParensJoinSpec<I>> onFromDerived(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
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
    final _MultiIndexHintOnSpec<I> onJoinTable(_JoinType joinType, @Nullable Query.TableModifier modifier,
                                               TableMeta<?> table, String alias) {
        final SimpleJoinClauseTableBlock<I> block;
        block = new SimpleJoinClauseTableBlock<>(joinType, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsParensOnClause<_MultiJoinSpec<I>> onJoinDerived(_JoinType joinType,
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
    final _OnClause<_MultiJoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable Query.DerivedModifier modifier,
                                                 _Cte cteItem, String alias) {
        final TabularBlocks.JoinClauseCteBlock<_MultiJoinSpec<I>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }


    private TabularBlocks.FromClauseAliasDerivedBlock getFromClauseDerived() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof TabularBlocks.FromClauseAliasDerivedBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (TabularBlocks.FromClauseAliasDerivedBlock) block;
    }

    private MySQLDelete._MultiJoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final _TabularBlock block;
        block = TabularBlocks.fromNestedBlock(joinType, nestedItems);
        this.blockConsumer.accept(block);
        this.fromCrossBlock = block;
        return this;
    }

    private _OnClause<MySQLDelete._MultiJoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final TabularBlocks.JoinClauseNestedBlock<MySQLDelete._MultiJoinSpec<I>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLSupports.FromClauseForJoinTableBlock<MySQLDelete._MultiIndexHintJoinSpec<I>> getHintClause() {
        final _TabularBlock block = this.fromCrossBlock;
        if (block != this.context.lastBlock() || !(block instanceof MySQLSupports.FromClauseForJoinTableBlock)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return (MySQLSupports.FromClauseForJoinTableBlock<MySQLDelete._MultiIndexHintJoinSpec<I>>) block;
    }


    private static final class MySQLSimpleMultiDelete<I extends Item, BI extends Item>
            extends MySQLMultiDeletes<I, BI> {

        private MySQLSimpleMultiDelete(@Nullable ArmyStmtSpec spec, Function<? super BatchDeleteSpec<BI>, I> function,
                                       Function<? super BatchDelete, BI> batchFunc) {
            super(spec, function, batchFunc);
        }


    }//MySQLSimpleMultiDelete


    private static final class SimplePartitionJoinClause<I extends Item, BI extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintJoinSpec<I>>
            implements MySQLDelete._MultiPartitionJoinClause<I> {

        private final MySQLMultiDeletes<I, BI> stmt;

        private SimplePartitionJoinClause(MySQLMultiDeletes<I, BI> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLMultiDeletes<I, BI> stmt = this.stmt;

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

    private static final class SimplePartitionOnClause<I extends Item, BI extends Item>
            extends MySQLSupports.PartitionAsClause<_MultiIndexHintOnSpec<I>>
            implements MySQLDelete._MultiPartitionOnClause<I> {

        private final MySQLMultiDeletes<I, BI> stmt;

        private SimplePartitionOnClause(MySQLMultiDeletes<I, BI> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLMultiDeletes<I, BI> stmt = this.stmt;

            final SimpleJoinClauseTableBlock<I> block;
            block = new SimpleJoinClauseTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class MySQLFromAliasClause<I extends Item>
            implements MySQLDelete._MultiDeleteFromAliasClause<I> {

        private final MySQLMultiDeletes<I, ?> stmt;

        private MySQLFromAliasClause(MySQLMultiDeletes<I, ?> stmt) {
            this.stmt = stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias) {
            this.stmt.delete(alias);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2) {
            this.stmt.delete(alias1, alias2);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2, String alias3) {
            this.stmt.delete(alias1, alias2, alias3);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(String alias1, String alias2, String alias3, String alias4) {
            this.stmt.delete(alias1, alias2, alias3, alias4);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(List<String> aliasList) {
            this.stmt.delete(aliasList);
            return this.stmt;
        }

        @Override
        public _SimpleMultiDeleteUsingClause<I> from(Consumer<Consumer<String>> consumer) {
            this.stmt.delete(consumer);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias) {
            this.stmt.delete(alias);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias1, String alias2) {
            this.stmt.delete(alias1, alias2);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias1, String alias2, String alias3) {
            this.stmt.delete(alias1, alias2, alias3);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(String alias1, String alias2, String alias3, String alias4) {
            this.stmt.delete(alias1, alias2, alias3, alias4);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(List<String> aliasList) {
            this.stmt.delete(aliasList);
            return this.stmt;
        }

        @Override
        public _MultiDeleteFromTableClause<I> space(Consumer<Consumer<String>> consumer) {
            this.stmt.delete(consumer);
            return this.stmt;
        }


    }//MySQLFromAliasClause


    private static final class MySQLBatchDelete extends ArmyBatchJoinableDelete
            implements MySQLDelete, BatchDelete, _MySQLMultiDelete {

        private final List<Hint> hintList;

        private final List<MySQLSyntax.Modifier> modifierList;

        private final List<_Pair<String, TableMeta<?>>> deleteTablePairList;

        private final boolean usingSyntax;


        private MySQLBatchDelete(MySQLMultiDeletes<?, ?> stmt, List<?> paramList) {
            super(stmt, paramList);

            this.hintList = stmt.hintList;
            this.modifierList = stmt.modifierList;
            this.deleteTablePairList = stmt.deleteTablePairList;
            this.usingSyntax = stmt.usingSyntax;

            if (this.hintList == null || this.modifierList == null || this.deleteTablePairList == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
        }


        @Override
        public List<_Pair<String, TableMeta<?>>> deleteTableList() {
            return this.deleteTablePairList;
        }

        @Override
        public List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public boolean isUsingSyntax() {
            return this.usingSyntax;
        }

        @Override
        Dialect statementDialect() {
            return MySQLUtils.DIALECT;
        }


    }//MySQLBatchDelete


}
