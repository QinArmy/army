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
abstract class MySQLMultiUpdate<I extends Item, WE, FT, SR, FS extends Item, JT, JS, WR, WA>
        extends JoinableUpdate.WithMultiUpdate<I, MySQLCtes, WE, TableField, SR, FT, FS, FS, JT, JS, JS, WR, WA, Object, Object, Object, Object>
        implements Update, _MySQLMultiUpdate, MySQLUpdate
        , MySQLStatement._IndexHintForJoinClause<FT> {


    static <I extends Item> _MultiWithSpec<I> simple(Function<Update, I> function) {
        return new SimpleUpdateStatement<>(null, function);
    }

    static <I extends Item> _BatchMultiWithSpec<I> batch(Function<Update, I> function) {
        return new BatchUpdateStatement<>(function);
    }

    private final Function<Update, I> function;

    List<Hint> hintList;

    List<MySQLSyntax.Modifier> modifierList;

    MySQLSupports.MySQLNoOnBlock<FT> noOnBlock;


    private MySQLMultiUpdate(@Nullable _WithClauseSpec withSpec, Function<Update, I> function) {
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
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final MySQLSupports.MySQLNoOnBlock<FT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (FT) this);
        this.noOnBlock = block;
        return block;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, modifier, tableItem, alias, this);
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mySQLCteBuilder(recursive, this.context);
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

    /*################################## blow private method ##################################*/


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


    /*################################## blow inner class  ##################################*/

    private static final class SimpleComma<I extends Item> implements MySQLUpdate._MultiComma<I> {

        private final boolean recursive;

        private final SimpleUpdateStatement<I> clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._MultiComma<I>>> function;

        private SimpleComma(boolean recursive, SimpleUpdateStatement<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _MultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithEnd().update(hints, modifiers, table, wordAs, tableAlias);
        }

        @Override
        public _MultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithEnd().update(table, wordAs, tableAlias);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            return this.endStaticWithEnd().update(hints, modifiers, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<T> supplier) {
            return this.endStaticWithEnd().update(supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Query.TabularModifier modifier, Supplier<T> supplier) {
            return this.endStaticWithEnd().update(hints, modifiers, modifier, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            return this.endStaticWithEnd().update(modifier, supplier);
        }

        @Override
        public _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            return this.endStaticWithEnd().update(hints, modifiers, cteName);
        }

        @Override
        public _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithEnd().update(hints, modifiers, cteName, wordAs, alias);
        }

        @Override
        public _MultiJoinSpec<I> update(String cteName) {
            return this.endStaticWithEnd().update(cteName);
        }

        @Override
        public _MultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithEnd().update(cteName, wordAs, alias);
        }

        @Override
        public _MultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table) {
            return this.endStaticWithEnd().update(hints, modifiers, table);
        }

        @Override
        public _MultiPartitionJoinClause<I> update(TableMeta<?> table) {
            return this.endStaticWithEnd().update(table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            return this.endStaticWithEnd().update(hints, modifiers);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update() {
            return this.endStaticWithEnd().update();
        }

        private SimpleUpdateStatement<I> endStaticWithEnd() {
            final SimpleUpdateStatement<I> clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//SimpleComma


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class SimpleUpdateStatement<I extends Item> extends MySQLMultiUpdate<
            I,
            MySQLUpdate.MultiUpdateClause<I>,
            MySQLUpdate._MultiIndexHintJoinSpec<I>,
            MySQLUpdate._MultiWhereSpec<I>,
            MySQLUpdate._MultiJoinSpec<I>,
            MySQLUpdate._MultiIndexHintOnSpec<I>,
            Statement._OnClause<MySQLUpdate._MultiJoinSpec<I>>,
            Statement._DmlUpdateSpec<I>,
            MySQLUpdate._MultiWhereAndSpec<I>>
            implements MySQLUpdate._MultiWithSpec<I>
            , MySQLUpdate._MultiIndexHintJoinSpec<I>
            , MySQLUpdate._MultiWhereSpec<I>
            , MySQLUpdate._MultiWhereAndSpec<I> {


        private SimpleUpdateStatement(@Nullable _WithClauseSpec withSpec, Function<Update, I> function) {
            super(withSpec, function);
        }

        @Override
        public _MultiWhereSpec<I> set(Consumer<ItemPairs<TableField>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public _MultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public _MultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<I>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<T> supplier) {
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<I>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, final Query.TabularModifier modifier, Supplier<T> supplier) {

            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }

            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<I>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<I>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), ""));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _MultiJoinSpec<I> update(String cteName) {
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), ""));
            return this;
        }

        @Override
        public _MultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
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
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
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
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, null, tableItem, alias, this);
        }



    }// SimpleMultiUpdate

    private static final class BatchComma<I extends Item> implements MySQLUpdate._BatchMultiComma<I> {

        private final boolean recursive;

        private final BatchUpdateStatement<I> clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._BatchMultiComma<I>>> function;

        private BatchComma(boolean recursive, BatchUpdateStatement<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithClause().update(hints, modifiers, table, wordAs, tableAlias);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithClause().update(table, wordAs, tableAlias);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            return this.endStaticWithClause().update(hints, modifiers, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<T> supplier) {
            return this.endStaticWithClause().update(supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Query.TabularModifier modifier, Supplier<T> supplier) {
            return this.endStaticWithClause().update(hints, modifiers, modifier, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            return this.endStaticWithClause().update(modifier, supplier);
        }

        @Override
        public _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            return this.endStaticWithClause().update(hints, modifiers, cteName);
        }

        @Override
        public _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithClause().update(hints, modifiers, cteName, wordAs, alias);
        }

        @Override
        public _BatchMultiJoinSpec<I> update(String cteName) {
            return this.endStaticWithClause().update(cteName);
        }

        @Override
        public _BatchMultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithClause().update(cteName, wordAs, alias);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            return this.endStaticWithClause().update(hints, modifiers, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> update(TableMeta<?> table) {
            return this.endStaticWithClause().update(table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            return this.endStaticWithClause().update(hints, modifiers);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update() {
            return this.endStaticWithClause().update();
        }

        private BatchUpdateStatement<I> endStaticWithClause() {
            final BatchUpdateStatement<I> clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
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
            MySQLUpdate._BatchMultiJoinSpec<I>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<I>,
            Statement._OnClause<_BatchMultiJoinSpec<I>>,
            Statement._BatchParamClause<_DmlUpdateSpec<I>>,
            MySQLUpdate._BatchMultiWhereAndSpec<I>>
            implements MySQLUpdate._BatchMultiWithSpec<I>
            , MySQLUpdate._BatchMultiIndexHintJoinSpec<I>
            , MySQLUpdate._BatchMultiWhereSpec<I>
            , MySQLUpdate._BatchMultiWhereAndSpec<I>
            , _BatchDml {


        private List<?> paramList;


        private BatchUpdateStatement(Function<Update, I> function) {
            super(null, function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma<>(recursive, this).function.apply(name);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<I>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<T> supplier) {
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<I>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Query.TabularModifier modifier, Supplier<T> supplier) {
            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }

            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<I>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<I>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), "");
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            assert wordAs == SQLs.AS;
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> update(String cteName) {
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), "");
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new BatchPartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<I> update(TableMeta<?> table) {
            return new BatchPartitionJoinClause<>(this, _JoinType.NONE, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
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
        public _BatchMultiWhereSpec<I> set(Consumer<BatchItemPairs<TableField>> consumer) {
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
        _BatchMultiIndexHintOnSpec<I> createTableBlock(_JoinType joinType
                , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new BatchOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<I>> createItemBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<I>> createCteBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }



    }// BatchUpdateStatement


    private static final class SimplePartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause_0<_MultiIndexHintJoinSpec<I>>
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
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleOnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._MultiIndexHintOnSpec<I>
            , MySQLUpdate._MultiJoinSpec<I>>
            implements MySQLUpdate._MultiIndexHintOnSpec<I> {

        private SimpleOnTableBlock(_JoinType joinType, TabularItem tableItem, String alias
                , MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(joinType, null, tableItem, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLUpdate._MultiJoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//OnTableBlock

    private static final class SimplePartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause_0<_MultiIndexHintOnSpec<I>>
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
            extends MySQLSupports.PartitionAsClause_0<_BatchMultiIndexHintJoinSpec<I>>
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
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchOnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._BatchMultiIndexHintOnSpec<I>
            , MySQLUpdate._BatchMultiJoinSpec<I>>
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
            extends MySQLSupports.PartitionAsClause_0<_BatchMultiIndexHintOnSpec<I>>
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
