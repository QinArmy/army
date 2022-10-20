package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.mysql.*;
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
abstract class MySQLMultiUpdate<WE, UT, PS extends Update._ItemPairBuilder, SR, FS, JT, JS, WR, WA>
        extends MultiUpdate.WithMultiUpdate<Update, Item, MySQLCteBuilder, WE, TableField, PS, SR, UT, FS, FS, JT, JS, JS, WR, WA, Object, Object>
        implements Update, _MySQLMultiUpdate, MySQLUpdate
        , MySQLQuery._IndexHintForJoinClause<UT> {


    static _MultiWithSpec simple() {
        return new SimpleUpdateStatement();
    }

    static _BatchMultiWithSpec batch() {
        return new BatchUpdateStatement();
    }

    List<Hint> hintList;

    List<MySQLSyntax.Modifier> modifierList;

    MySQLSupports.MySQLNoOnBlock<UT> noOnBlock;

    private MySQLMultiUpdate() {
        super(CriteriaContexts.primaryMultiDmlContext());
    }


    @Override
    public final MySQLQuery._IndexForJoinSpec<UT> useIndex() {
        return this.getHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<UT> ignoreIndex() {
        return this.getHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<UT> forceIndex() {
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
    /*################################## blow package template method ##################################*/


    @Override
    final Update onAsUpdate() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this instanceof BatchUpdateStatement && ((BatchUpdateStatement) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this;
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        if (this instanceof BatchUpdateStatement) {
            ((BatchUpdateStatement) this).paramList = null;
        }
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier, TableMeta<?> table, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final MySQLSupports.MySQLNoOnBlock<UT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (UT) this);
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
    final MySQLCteBuilder createCteBuilder(boolean recursive) {
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
    private MySQLQuery._IndexHintForJoinClause<UT> getHintClause() {
        final MySQLSupports.MySQLNoOnBlock<UT> noOnBlock = this.noOnBlock;
        if (noOnBlock != this.context.lastBlock()) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return noOnBlock.getUseIndexClause();
    }


    /*################################## blow inner class  ##################################*/

    private static final class SimpleComma implements MySQLUpdate._MultiComma {

        private final boolean recursive;

        private final SimpleUpdateStatement clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._MultiComma>> function;

        private SimpleComma(boolean recursive, SimpleUpdateStatement clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _MultiIndexHintJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithEnd().update(hints, modifiers, table, wordAs, tableAlias);
        }

        @Override
        public _MultiIndexHintJoinSpec<Update> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithEnd().update(table, wordAs, tableAlias);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            return this.endStaticWithEnd().update(hints, modifiers, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<T> supplier) {
            return this.endStaticWithEnd().update(supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Query.TabularModifier modifier, Supplier<T> supplier) {
            return this.endStaticWithEnd().update(hints, modifiers, modifier, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            return this.endStaticWithEnd().update(modifier, supplier);
        }

        @Override
        public _MultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            return this.endStaticWithEnd().update(hints, modifiers, cteName);
        }

        @Override
        public _MultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithEnd().update(hints, modifiers, cteName, wordAs, alias);
        }

        @Override
        public _MultiJoinSpec<Update> update(String cteName) {
            return this.endStaticWithEnd().update(cteName);
        }

        @Override
        public _MultiJoinSpec<Update> update(String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithEnd().update(cteName, wordAs, alias);
        }

        @Override
        public _MultiPartitionJoinClause<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table) {
            return this.endStaticWithEnd().update(hints, modifiers, table);
        }

        @Override
        public _MultiPartitionJoinClause<Update> update(TableMeta<?> table) {
            return this.endStaticWithEnd().update(table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            return this.endStaticWithEnd().update(hints, modifiers);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> update() {
            return this.endStaticWithEnd().update();
        }

        private SimpleUpdateStatement endStaticWithEnd() {
            final SimpleUpdateStatement clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//SimpleComma


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class SimpleUpdateStatement extends MySQLMultiUpdate<
            MySQLUpdate.MultiUpdateClause,
            MySQLUpdate._MultiIndexHintJoinSpec<Update>,
            ItemPairs<TableField>,
            MySQLUpdate._MultiWhereSpec<Update>,
            MySQLUpdate._MultiJoinSpec<Update>,
            MySQLUpdate._MultiIndexHintOnSpec<Update>,
            Statement._OnClause<MySQLUpdate._MultiJoinSpec<Update>>,
            Statement._DmlUpdateSpec<Update>,
            MySQLUpdate._MultiWhereAndSpec<Update>>
            implements MySQLUpdate._MultiWithSpec
            , MySQLUpdate._MultiIndexHintJoinSpec<Update>
            , MySQLUpdate._MultiWhereSpec<Update>
            , MySQLUpdate._MultiWhereAndSpec<Update> {


        private SimpleUpdateStatement() {
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma(recursive, this).function.apply(name);
        }

        @Override
        public _MultiIndexHintJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public _MultiIndexHintJoinSpec<Update> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<T> supplier) {
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
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
            final _AsClause<_MultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_MultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public _MultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), ""));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
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
        public _MultiJoinSpec<Update> update(String cteName) {
            this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), ""));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> update(String cteName, SQLs.WordAs wordAs, String alias) {
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _MultiPartitionJoinClause<Update> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new SimplePartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public _MultiPartitionJoinClause<Update> update(TableMeta<?> table) {
            return new SimplePartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> update() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
        }

        @Override
        public _MultiPartitionOnClause<Update> leftJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause(this, _JoinType.LEFT_JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<Update> join(TableMeta<?> table) {
            return new SimplePartitionOnClause(this, _JoinType.JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<Update> rightJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause(this, _JoinType.RIGHT_JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<Update> fullJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause(this, _JoinType.FULL_JOIN, table);
        }

        @Override
        public _MultiPartitionOnClause<Update> straightJoin(TableMeta<?> table) {
            return new SimplePartitionOnClause(this, _JoinType.STRAIGHT_JOIN, table);
        }

        @Override
        public _MultiPartitionJoinClause<Update> crossJoin(TableMeta<?> table) {
            return new SimplePartitionJoinClause(this, _JoinType.CROSS_JOIN, table);
        }


        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<Update>>> leftJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<Update>>> join() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<Update>>> rightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<Update>>> fullJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<Update>>> straightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> crossJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNoneCrossEnd);
        }

        @Override
        public _MultiJoinSpec<Update> ifLeftJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> ifJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> ifRightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> ifFullJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> ifStraightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _MultiJoinSpec<Update> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
            consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }


        @Override
        _MultiIndexHintOnSpec<Update> createTableBlock(_JoinType joinType, @Nullable Query.TableModifier modifier
                , TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new SimpleOnTableBlock(joinType, table, tableAlias, this);
        }

        @Override
        _OnClause<_MultiJoinSpec<Update>> createItemBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_MultiJoinSpec<Update>> createCteBlock(_JoinType joinType, @Nullable Query.TabularModifier modifier
                , TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, null, tableItem, alias, this);
        }

        @Override
        ItemPairs<TableField> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.itemPairs(consumer);
        }


    }// SimpleMultiUpdate

    private static final class BatchComma implements MySQLUpdate._BatchMultiComma {

        private final boolean recursive;

        private final BatchUpdateStatement clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._BatchMultiComma>> function;

        private BatchComma(boolean recursive, BatchUpdateStatement clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<Update> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithClause().update(hints, modifiers, table, wordAs, tableAlias);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<Update> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithClause().update(table, wordAs, tableAlias);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            return this.endStaticWithClause().update(hints, modifiers, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<T> supplier) {
            return this.endStaticWithClause().update(supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Query.TabularModifier modifier, Supplier<T> supplier) {
            return this.endStaticWithClause().update(hints, modifiers, modifier, supplier);
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            return this.endStaticWithClause().update(modifier, supplier);
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            return this.endStaticWithClause().update(hints, modifiers, cteName);
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithClause().update(hints, modifiers, cteName, wordAs, alias);
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(String cteName) {
            return this.endStaticWithClause().update(cteName);
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(String cteName, SQLs.WordAs wordAs, String alias) {
            return this.endStaticWithClause().update(cteName, wordAs, alias);
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            return this.endStaticWithClause().update(hints, modifiers, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> update(TableMeta<?> table) {
            return this.endStaticWithClause().update(table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            return this.endStaticWithClause().update(hints, modifiers);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> update() {
            return this.endStaticWithClause().update();
        }

        private BatchUpdateStatement endStaticWithClause() {
            final BatchUpdateStatement clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//BatchComma


    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     */
    private static final class BatchUpdateStatement extends MySQLMultiUpdate<
            MySQLUpdate._BatchMultiUpdateClause,
            MySQLUpdate._BatchMultiIndexHintJoinSpec<Update>,
            BatchItemPairs<TableField>,
            MySQLUpdate._BatchMultiWhereSpec<Update>,
            MySQLUpdate._BatchMultiJoinSpec<Update>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<Update>,
            Statement._OnClause<_BatchMultiJoinSpec<Update>>,
            Statement._BatchParamClause<_DmlUpdateSpec<Update>>,
            MySQLUpdate._BatchMultiWhereAndSpec<Update>>
            implements MySQLUpdate._BatchMultiWithSpec
            , MySQLUpdate._BatchMultiIndexHintJoinSpec<Update>
            , MySQLUpdate._BatchMultiWhereSpec<Update>
            , MySQLUpdate._BatchMultiWhereAndSpec<Update>
            , _BatchDml {


        private List<?> paramList;


        private BatchUpdateStatement() {
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma(recursive, this).function.apply(name);
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<Update> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            assert wordAs == SQLs.AS;
            this.blockConsumer.accept(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
            return this;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<T> supplier) {
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                this.blockConsumer.accept(new TableBlock.NoOnTableBlock(_JoinType.NONE, tabularItem, alias));
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
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
            final _AsClause<_BatchMultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            if (modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            final TabularItem tabularItem;
            tabularItem = supplier.get();
            if (tabularItem == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final _AsClause<_BatchMultiJoinSpec<Update>> asClause;
            asClause = alias -> {
                final TableBlock.NoOnModifierTableBlock block;
                block = new TableBlock.NoOnModifierTableBlock(_JoinType.NONE, modifier, tabularItem, alias);
                this.blockConsumer.accept(block);
                return this;
            };
            return asClause;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);

            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), "");
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
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
        public _BatchMultiJoinSpec<Update> update(String cteName) {
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), "");
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(String cteName, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            final TableBlock.NoOnTableBlock block;
            block = new TableBlock.NoOnTableBlock(_JoinType.NONE, this.context.refCte(cteName), alias);
            this.blockConsumer.accept(block);
            return this;
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return new BatchPartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> update(TableMeta<?> table) {
            return new BatchPartitionJoinClause(this, _JoinType.NONE, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> update() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNoneCrossEnd);
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> leftJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.LEFT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> join(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> rightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.RIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> fullJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.FULL_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> straightJoin(TableMeta<?> table) {
            return new BatchPartitionOnClause(this, _JoinType.STRAIGHT_JOIN, table);
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> crossJoin(TableMeta<?> table) {
            return new BatchPartitionJoinClause(this, _JoinType.CROSS_JOIN, table);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> leftJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> join() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> rightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> fullJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> straightJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.STRAIGHT_JOIN, this::nestedJoinEnd);
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> crossJoin() {
            return MySQLNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNoneCrossEnd);
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifLeftJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifRightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifFullJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifStraightJoin(Consumer<MySQLJoins> consumer) {
            consumer.accept(MySQLDynamicJoins.joinBuilder(this.context, _JoinType.STRAIGHT_JOIN, this.blockConsumer));
            return this;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
            consumer.accept(MySQLDynamicJoins.crossBuilder(this.context, this.blockConsumer));
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<Update> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<Update> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<Update> paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }


        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        _BatchMultiIndexHintOnSpec<Update> createTableBlock(_JoinType joinType
                , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new BatchOnTableBlock(joinType, table, tableAlias, this);
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<Update>> createItemBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null && modifier != SQLs.LATERAL) {
                throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
            }
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<Update>> createCteBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            if (modifier != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        BatchItemPairs<TableField> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.batchItemPairs(consumer);
        }


    }// BatchUpdateStatement


    private static final class SimplePartitionJoinClause
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._MultiIndexHintJoinSpec<Update>>
            implements MySQLUpdate._MultiPartitionJoinClause<Update> {

        private final SimpleUpdateStatement stmt;

        private SimplePartitionJoinClause(SimpleUpdateStatement stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintJoinSpec<Update> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateStatement stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<MySQLUpdate._MultiIndexHintJoinSpec<Update>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//SimplePartitionJoinClause

    private static final class SimpleOnTableBlock
            extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._MultiIndexHintOnSpec<Update>
            , MySQLUpdate._MultiJoinSpec<Update>>
            implements MySQLUpdate._MultiIndexHintOnSpec<Update> {

        private SimpleOnTableBlock(_JoinType joinType, TabularItem tableItem, String alias
                , MySQLUpdate._MultiJoinSpec<Update> stmt) {
            super(joinType, null, tableItem, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLUpdate._MultiJoinSpec<Update> stmt) {
            super(params, stmt);
        }


    }//OnTableBlock

    private static final class SimplePartitionOnClause
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._MultiIndexHintOnSpec<Update>>
            implements MySQLUpdate._MultiPartitionOnClause<Update> {

        private final SimpleUpdateStatement stmt;

        private SimplePartitionOnClause(SimpleUpdateStatement stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<Update> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateStatement stmt = this.stmt;

            final SimpleOnTableBlock block;
            block = new SimpleOnTableBlock(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//SimplePartitionOnClause


    private static final class BatchPartitionJoinClause
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._BatchMultiIndexHintJoinSpec<Update>>
            implements MySQLUpdate._BatchMultiPartitionJoinClause<Update> {

        private final BatchUpdateStatement stmt;

        private BatchPartitionJoinClause(BatchUpdateStatement stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintJoinSpec<Update> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateStatement stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<MySQLUpdate._BatchMultiIndexHintJoinSpec<Update>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.blockConsumer.accept(block);
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//BatchPartitionJoinClause

    private static final class BatchOnTableBlock
            extends MySQLSupports.MySQLOnBlock<
            MySQLUpdate._BatchMultiIndexHintOnSpec<Update>
            , MySQLUpdate._BatchMultiJoinSpec<Update>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<Update> {

        private BatchOnTableBlock(_JoinType joinType, TableMeta<?> table, String alias
                , MySQLUpdate._BatchMultiJoinSpec<Update> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLUpdate._BatchMultiJoinSpec<Update> stmt) {
            super(params, stmt);
        }


    }//BatchOnTableBlock

    private static final class BatchPartitionOnClause
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._BatchMultiIndexHintOnSpec<Update>>
            implements MySQLUpdate._BatchMultiPartitionOnClause<Update> {

        private final BatchUpdateStatement stmt;

        private BatchPartitionOnClause(BatchUpdateStatement stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintOnSpec<Update> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateStatement stmt = this.stmt;

            final BatchOnTableBlock block;
            block = new BatchOnTableBlock(params, stmt);
            stmt.blockConsumer.accept(block);
            return block;
        }


    }//PartitionAsClause


}
