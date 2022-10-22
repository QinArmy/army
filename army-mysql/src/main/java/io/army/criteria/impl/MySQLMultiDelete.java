package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.mysql.*;
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
abstract class MySQLMultiDelete<I extends Item, WE, DH, DT, FU, FT, FS extends Item, JT, JS, WR, WA>
        extends MultiDelete.WithMultiDelete<I, Item, MySQLCteBuilder, WE, FT, FS, FS, JT, JS, JS, WR, WA>
        implements MySQLDelete, _MySQLMultiDelete
        , MySQLDelete._MultiDeleteFromAliasClause<FU>
        , MySQLDelete._MultiDeleteHintClause<DH>
        , MySQLDelete._MultiDeleteAliasClause<DT>
        , MySQLQuery._MySQLFromClause<FT, FS>
        , MySQLQuery._IndexHintForJoinClause<FT>
        , Statement._FromNestedClause<MySQLQuery._NestedLeftParenSpec<FS>>
        , Delete {


    static <I extends Item> _MultiWithSpec<I> simple(Function<Delete, I> function) {
        return new SimpleMultiDelete<>(function);
    }

    static <I extends Item> _BatchMultiWithSpec<I> batch(Function<Delete, I> function) {
        return new BatchMultiDelete<>(function);
    }

    private final Function<Delete, I> function;

    private List<Hint> hintList;

    private List<MySQLs.Modifier> modifierList;

    boolean usingSyntax;

    private List<String> tableAliasList;

    private List<_Pair<String, TableMeta<?>>> deleteTablePairList;


    MySQLSupports.MySQLNoOnBlock<FT> noOnBlock;

    private MySQLMultiDelete(Function<Delete, I> function) {
        super(CriteriaContexts.primaryMultiDmlContext());
        this.function = function;
    }

    @Override
    public final DH delete(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return (DH) this;
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
    public final FT from(TableMeta<?> table, StandardSyntax.WordAs wordAs, String tableAlias) {
        final _TableBlock block;
        block = this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias);
        this.blockConsumer.accept(block);
        return (FT) this;
    }

    @Override
    public final <T extends TabularItem> _AsClause<FS> from(Supplier<T> supplier) {
        final TabularItem tabularItem;
        tabularItem = supplier.get();
        if (tabularItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final _AsClause<FS> asClause;
        asClause = alias -> {
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
            return (FS) this;
        };
        return asClause;
    }

    @Override
    public final <T extends TabularItem> _AsClause<FS> from(Query.TabularModifier modifier, Supplier<T> supplier) {
        if (modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        final TabularItem tabularItem;
        tabularItem = supplier.get();
        if (tabularItem == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final _AsClause<FS> asClause;
        asClause = alias -> {
            final TableBlock.NoOnModifierTableBlock block;
            block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
            this.blockConsumer.accept(block);
            return (FS) this;
        };
        return asClause;
    }


    @Override
    public final FS from(String cteName) {
        this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), ""));
        return (FS) this;
    }

    @Override
    public final FS from(String cteName, StandardSyntax.WordAs wordAs, String alias) {
        assert wordAs == SQLs.AS;
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    @Override
    public final MySQLQuery._NestedLeftParenSpec<FS> from() {
        return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
    }

    @Override
    public final FU from(String alias1, String alias2) {
        this.tableAliasList = Arrays.asList(alias1, alias2);
        return (FU) this;
    }

    @Override
    public final FU from(String alias1, String alias2, String alias3) {
        this.tableAliasList = Arrays.asList(alias1, alias2, alias3);
        return (FU) this;
    }

    @Override
    public final FU from(String alias1, String alias2, String alias3, String alias4) {
        this.tableAliasList = Arrays.asList(alias1, alias2, alias3, alias4);
        return (FU) this;
    }

    @Override
    public final FU from(List<String> aliasList) {
        this.tableAliasList = aliasList;
        return (FU) this;
    }

    @Override
    public final FU from(Consumer<Consumer<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        this.tableAliasList = list;
        return (FU) this;
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
    public final String toString() {
        final String s;
        if (this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
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


    @Override
    final I onAsDelete() {
        if (this.deleteTablePairList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.function.apply(this);
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.deleteTablePairList = null;
    }

    @Override
    final MySQLCteBuilder createCteBuilder(boolean recursive) {
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
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table
            , String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final MySQLSupports.MySQLNoOnBlock<FT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (FT) this);
        this.noOnBlock = block;
        return block;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier, TabularItem tableItem
            , String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, modifier, tableItem, alias, this);
    }


    final FS nestedNoneCrossEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return (FS) this;
    }

    final _OnClause<FS> nestedJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertMySQLJoinType();
        final OnClauseTableBlock<FS> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", (FS) this);
        this.blockConsumer.accept(block);
        return block;
    }


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLQuery._IndexHintForJoinClause<FT> getHintClause() {
        final MySQLSupports.MySQLNoOnBlock<FT> noOnBlock = this.noOnBlock;
        if (noOnBlock != this.context.lastBlock()) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return noOnBlock.getUseIndexClause();
    }


    private static final class MultiComma<I extends Item> implements MySQLDelete._MultiComma<I> {

        private final boolean recursive;

        private final SimpleMultiDelete<I> statement;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLDelete._MultiComma<I>>> function;

        private MultiComma(boolean recursive, SimpleMultiDelete<I> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = MySQLQueries.complexCte(statement.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _SimpleMultiDeleteFromAliasClause<I> delete(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers) {
            return this.endStaticWithClause().delete(hints, modifiers);
        }

        @Override
        public _MultiDeleteFromTableClause<I> delete(String alias1, String alias2) {
            return this.endStaticWithClause().delete(alias1, alias2);
        }

        @Override
        public _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3) {
            return this.endStaticWithClause().delete(alias1, alias2, alias3);
        }

        @Override
        public _MultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3, String alias4) {
            return this.endStaticWithClause().delete(alias1, alias2, alias3, alias4);
        }

        @Override
        public _MultiDeleteFromTableClause<I> delete(List<String> aliasList) {
            return this.endStaticWithClause().delete(aliasList);
        }

        @Override
        public _MultiDeleteFromTableClause<I> delete(Consumer<Consumer<String>> consumer) {
            return this.endStaticWithClause().delete(consumer);
        }

        private SimpleMultiDelete<I> endStaticWithClause() {
            final SimpleMultiDelete<I> statement = this.statement;
            statement.endStaticWithClause(this.recursive);
            return statement;
        }

    }//MultiComma


    private static final class SimpleMultiDelete<I extends Item> extends MySQLMultiDelete<
            I,
            MySQLDelete._SimpleMultiDeleteClause<I>,
            MySQLDelete._SimpleMultiDeleteFromAliasClause<I>,
            MySQLDelete._MultiDeleteFromTableClause<I>,
            MySQLDelete._MultiDeleteUsingTableClause<I>,
            MySQLDelete._MultiIndexHintJoinSpec<I>,
            MySQLDelete._MultiJoinSpec<I>,
            MySQLDelete._MultiIndexHintOnSpec<I>,
            Statement._OnClause<_MultiJoinSpec<I>>,
            Statement._DmlDeleteSpec<I>,
            MySQLDelete._MultiWhereAndSpec<I>>
            implements MySQLDelete._MultiWithSpec<I>
            , MySQLDelete._SimpleMultiDeleteFromAliasClause<I>
            , MySQLDelete._MultiDeleteFromTableClause<I>
            , MySQLDelete._MultiDeleteUsingTableClause<I>
            , MySQLDelete._MultiIndexHintJoinSpec<I>
            , MySQLDelete._MultiWhereAndSpec<I> {

        private SimpleMultiDelete(Function<Delete, I> function) {
            super(function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new MultiComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new MultiComma<>(recursive, this).function.apply(name);
        }

        @Override
        public _MultiPartitionJoinClause<I> from(TableMeta<?> table) {
            return new SimplePartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _MultiIndexHintJoinSpec<I> using(TableMeta<?> table, StandardSyntax.WordAs wordAs, String tableAlias) {
            this.usingSyntax = true;
            return this.from(table, wordAs, tableAlias);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> using(Supplier<T> supplier) {
            this.usingSyntax = true;
            return this.from(supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> using(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            this.usingSyntax = true;
            return this.from(modifier, supplier);
        }

        @Override
        public _MultiJoinSpec<I> using(String cteName) {
            this.usingSyntax = true;
            return this.from(cteName);
        }

        @Override
        public _MultiJoinSpec<I> using(String cteName, StandardSyntax.WordAs wordAs, String alias) {
            this.usingSyntax = true;
            return this.from(cteName, wordAs, alias);
        }

        @Override
        public _MultiPartitionJoinClause<I> using(TableMeta<?> table) {
            this.usingSyntax = true;
            return this.from(table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> using() {
            this.usingSyntax = true;
            return this.from();
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
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> join() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> rightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> fullJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>> straightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> crossJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNoneCrossEnd);
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
        _MultiIndexHintOnSpec<I> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new SimpleOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        _OnClause<_MultiJoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_MultiJoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }


    }//SimpleMultiDelete


    private static final class BatchMultiComma<I extends Item> implements MySQLDelete._BatchMultiComma<I> {

        private final boolean recursive;

        private final BatchMultiDelete<I> statement;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLDelete._BatchMultiComma<I>>> function;

        private BatchMultiComma(boolean recursive, BatchMultiDelete<I> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = MySQLQueries.complexCte(statement.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _BatchMultiDeleteFromAliasClause<I> delete(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers) {
            return this.endStaticWithClause().delete(hints, modifiers);
        }

        @Override
        public _BatchMultiDeleteFromTableClause<I> delete(String alias1, String alias2) {
            return this.endStaticWithClause().delete(alias1, alias2);
        }

        @Override
        public _BatchMultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3) {
            return this.endStaticWithClause().delete(alias1, alias2, alias3);
        }

        @Override
        public _BatchMultiDeleteFromTableClause<I> delete(String alias1, String alias2, String alias3, String alias4) {
            return this.endStaticWithClause().delete(alias1, alias2, alias3, alias4);
        }

        @Override
        public _BatchMultiDeleteFromTableClause<I> delete(List<String> aliasList) {
            return this.endStaticWithClause().delete(aliasList);
        }

        @Override
        public _BatchMultiDeleteFromTableClause<I> delete(Consumer<Consumer<String>> consumer) {
            return this.endStaticWithClause().delete(consumer);
        }

        private BatchMultiDelete<I> endStaticWithClause() {
            final BatchMultiDelete<I> statement = this.statement;
            statement.endStaticWithClause(this.recursive);
            return statement;
        }

    }//BatchMultiComma


    private static final class BatchMultiDelete<I extends Item> extends MySQLMultiDelete<
            I,
            MySQLDelete._BatchMultiDeleteClause<I>,
            MySQLDelete._BatchMultiDeleteFromAliasClause<I>,
            MySQLDelete._BatchMultiDeleteFromTableClause<I>,
            MySQLDelete._BatchMultiDeleteUsingTableClause<I>,
            MySQLDelete._BatchMultiIndexHintJoinSpec<I>,
            MySQLDelete._BatchMultiJoinSpec<I>,
            MySQLDelete._BatchMultiIndexHintOnSpec<I>,
            Statement._OnClause<_BatchMultiJoinSpec<I>>,
            Statement._BatchParamClause<_DmlDeleteSpec<I>>,
            MySQLDelete._BatchMultiWhereAndSpec<I>>
            implements MySQLDelete._BatchMultiWithSpec<I>
            , MySQLDelete._BatchMultiDeleteFromAliasClause<I>
            , MySQLDelete._BatchMultiDeleteFromTableClause<I>
            , MySQLDelete._BatchMultiDeleteUsingTableClause<I>
            , MySQLDelete._BatchMultiIndexHintJoinSpec<I>
            , MySQLDelete._BatchMultiWhereAndSpec<I>
            , _BatchDml {

        private List<?> paramList;

        private BatchMultiDelete(Function<Delete, I> function) {
            super(function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchMultiComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchMultiComma<>(recursive, this).function.apply(name);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> from(TableMeta<?> table) {
            return new BatchPartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> using(TableMeta<?> table, StandardSyntax.WordAs wordAs
                , String tableAlias) {
            this.usingSyntax = true;
            return this.from(table, wordAs, tableAlias);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> using(Supplier<T> supplier) {
            this.usingSyntax = true;
            return this.from(supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> using(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            this.usingSyntax = true;
            return this.from(modifier, supplier);
        }

        @Override
        public _BatchMultiJoinSpec<I> using(String cteName) {
            this.usingSyntax = true;
            return this.from(cteName);
        }

        @Override
        public _BatchMultiJoinSpec<I> using(String cteName, StandardSyntax.WordAs wordAs, String alias) {
            this.usingSyntax = true;
            return this.from(cteName, wordAs, alias);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> using(TableMeta<?> table) {
            this.usingSyntax = true;
            return this.from(table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> using() {
            this.usingSyntax = true;
            return this.from();
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
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> join() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> rightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> fullJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>> straightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> crossJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNoneCrossEnd);
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
        public <P> _DmlDeleteSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<I> paramList(Function<String, ?> function, String keyName) {
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
        _BatchMultiIndexHintOnSpec<I> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new BatchOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }


    }//BatchMultiDelete


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLDelete._MultiIndexHintJoinSpec<I>>
            implements MySQLDelete._MultiPartitionJoinClause<I> {

        private final SimpleMultiDelete<I> stmt;

        private SimplePartitionJoinClause(SimpleMultiDelete<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleMultiDelete<I> stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<MySQLDelete._MultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleOnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<
            MySQLDelete._MultiIndexHintOnSpec<I>
            , MySQLDelete._MultiJoinSpec<I>>
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
            extends MySQLSupports.PartitionAsClause<MySQLDelete._MultiIndexHintOnSpec<I>>
            implements MySQLDelete._MultiPartitionOnClause<I> {

        private final SimpleMultiDelete<I> stmt;

        private SimplePartitionOnClause(SimpleMultiDelete<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._MultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleMultiDelete<I> stmt = this.stmt;

            final SimpleOnTableBlock<I> block;
            block = new SimpleOnTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class BatchPartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLDelete._BatchMultiIndexHintJoinSpec<I>>
            implements MySQLDelete._BatchMultiPartitionJoinClause<I> {

        private final BatchMultiDelete<I> stmt;

        private BatchPartitionJoinClause(BatchMultiDelete<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._BatchMultiIndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchMultiDelete<I> stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<MySQLDelete._BatchMultiIndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchOnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<
            MySQLDelete._BatchMultiIndexHintOnSpec<I>
            , MySQLDelete._BatchMultiJoinSpec<I>>
            implements MySQLDelete._BatchMultiIndexHintOnSpec<I> {

        private BatchOnTableBlock(_JoinType joinType, TabularItem tableItem, String alias
                , MySQLDelete._BatchMultiJoinSpec<I> stmt) {
            super(joinType, null, tableItem, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._BatchMultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//BatchOnTableBlock

    private static final class BatchPartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<MySQLDelete._BatchMultiIndexHintOnSpec<I>>
            implements MySQLDelete._BatchMultiPartitionOnClause<I> {

        private final BatchMultiDelete<I> stmt;

        private BatchPartitionOnClause(BatchMultiDelete<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        MySQLDelete._BatchMultiIndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchMultiDelete<I> stmt = this.stmt;

            final BatchOnTableBlock<I> block;
            block = new BatchOnTableBlock<>(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//BatchPartitionOnClause


}
