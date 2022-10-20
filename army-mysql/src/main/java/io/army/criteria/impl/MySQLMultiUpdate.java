package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.mysql.*;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

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

    /*################################## blow package template method ##################################*/


    @Override
    final Update onAsUpdate() {

        return this;
    }

    @Override
    final void onClear() {

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

        private List<Hint> hintList;

        private List<MySQLSyntax.Modifier> modifierList;


        private SimpleUpdateStatement() {
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma> with(String name) {
            return null;
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_MultiComma> withRecursive(String name) {
            return null;
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
        public List<Hint> hintList() {
            final List<Hint> list = this.hintList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public List<MySQLs.Modifier> modifierList() {
            final List<MySQLs.Modifier> list = this.modifierList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
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

        private List<Hint> hintList;

        private List<MySQLSyntax.Modifier> modifierList;

        private List<?> paramList;


        private BatchUpdateStatement() {
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma> with(String name) {
            return null;
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma> withRecursive(String name) {
            return null;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return null;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<Update> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return null;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, Query.TabularModifier modifier, Supplier<T> supplier) {
            return null;
        }

        @Override
        public <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<Update>> update(Query.TabularModifier modifier
                , Supplier<T> supplier) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , String cteName, SQLs.WordAs wordAs, String alias) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(String cteName) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> update(String cteName, SQLs.WordAs wordAs, String alias) {
            return null;
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers, TableMeta<?> table) {
            return null;
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> update(TableMeta<?> table) {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> update(Supplier<List<Hint>> hints
                , List<MySQLs.Modifier> modifiers) {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> update() {
            return null;
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> leftJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> join(TableMeta<?> table) {
            return null;
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> rightJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> fullJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public _BatchMultiPartitionOnClause<Update> straightJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public _BatchMultiPartitionJoinClause<Update> crossJoin(TableMeta<?> table) {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> leftJoin() {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> join() {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> rightJoin() {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> fullJoin() {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<Update>>> straightJoin() {
            return null;
        }

        @Override
        public MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<Update>> crossJoin() {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifLeftJoin(Consumer<MySQLJoins> consumer) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifJoin(Consumer<MySQLJoins> consumer) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifRightJoin(Consumer<MySQLJoins> consumer) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifFullJoin(Consumer<MySQLJoins> consumer) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifStraightJoin(Consumer<MySQLJoins> consumer) {
            return null;
        }

        @Override
        public _BatchMultiJoinSpec<Update> ifCrossJoin(Consumer<MySQLCrosses> consumer) {
            return null;
        }

        @Override
        public <P> _DmlUpdateSpec<Update> paramList(List<P> paramList) {
            return null;
        }

        @Override
        public <P> _DmlUpdateSpec<Update> paramList(Supplier<List<P>> supplier) {
            return null;
        }

        @Override
        public _DmlUpdateSpec<Update> paramList(Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public List<Hint> hintList() {
            final List<Hint> list = this.hintList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public List<MySQLs.Modifier> modifierList() {
            final List<MySQLs.Modifier> list = this.modifierList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        public List<?> paramList() {
            return null;
        }

        @Override
        _BatchMultiIndexHintOnSpec<Update> createTableBlock(_JoinType joinType
                , @Nullable Query.TableModifier modifier, TableMeta<?> table, String tableAlias) {
            return null;
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<Update>> createItemBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            return null;
        }

        @Override
        _OnClause<_BatchMultiJoinSpec<Update>> createCteBlock(_JoinType joinType
                , @Nullable Query.TabularModifier modifier, TabularItem tableItem, String alias) {
            return null;
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


}
