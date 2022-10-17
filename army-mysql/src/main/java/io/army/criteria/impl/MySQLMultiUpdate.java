package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SimpleUpdate} ,MySQL multi update api implementation</li>
 *         <li>{@link BatchUpdate} ,MySQL batch multi update api implementation</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiUpdate<C, WE, SR, UT, US, UP, JT, JS, JP, WR, WA>
        extends WithCteMultiUpdate<C, SubQuery, WE, TableField, SR, UT, US, UP, US, JT, JS, JP, WR, WA, Update>
        implements MySQLUpdate.MultiUpdateClause<C, UT, US, UP>, MySQLQuery._IndexHintForJoinClause<C, UT>
        , _MySQLMultiUpdate, _MySQLWithClause, MySQLUpdate, Update._UpdateSpec {


    static <C> _WithAndMultiUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> _BatchWithAndMultiUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    private boolean recursive;

    private List<_Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLSyntax._MySQLModifier> modifierList;

    private MySQLSupports.MySQLNoOnBlock<C, UT> noOnBlock;


    private MySQLMultiUpdate(@Nullable C criteria) {
        super(CriteriaContexts.primaryMultiDmlContext(criteria));
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers
            , TableMeta<?> table) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);
        return this.createNoOnTableClause(_JoinType.NONE, null, table);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers
            , TableMeta<?> table, String tableAlias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);

        this.criteriaContext.onAddBlock(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
        return (UT) this;
    }

    @Override
    public final UP update(TableMeta<?> table) {
        return this.createNoOnTableClause(_JoinType.NONE, null, table);
    }

    @Override
    public final UT update(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddBlock(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
        return (UT) this;
    }

    @Override
    public final <T extends TabularItem> US update(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers
            , Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);

        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US update(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers
            , Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);


        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US update(Supplier<T> supplier, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US update(Function<C, T> function, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US updateLateral(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers, Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);

        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US updateLateral(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers, Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);


        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US updateLateral(Supplier<T> supplier, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TabularItem> US updateLateral(Function<C, T> function, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<C, UT> useIndex() {
        return this.getIndexHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<C, UT> ignoreIndex() {
        return this.getIndexHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<C, UT> forceIndex() {
        return this.getIndexHintClause().forceIndex();
    }

    /*################################## blow IndexHintClause method ##################################*/


    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<_Cte> cteList() {
        return this.cteList;
    }


    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLSyntax._MySQLModifier> modifierList() {
        return this.modifierList;
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
    final void doOnAsUpdate() {
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        this.noOnBlock = null;

        if (this instanceof BatchUpdate && ((BatchUpdate<C>) this).paramList == null) {
            throw _Exceptions.batchParamEmpty();
        }

    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.hintList = null;
        this.modifierList = null;
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C>) this).paramList = null;
        }

    }

    @Override
    final boolean isSupportRowLeftItem() {
        //false ,MySQL 8.0 don't support row left item
        return false;
    }

    @Override
    final MySQLDialect dialect() {
        return MySQLDialect.MySQL80;
    }


    @Override
    final void doWithCte(boolean recursive, List<_Cte> cteList) {
        this.recursive = recursive;
        this.cteList = cteList;
    }

    @Override
    public final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        final MySQLSupports.MySQLNoOnBlock<C, UT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (UT) this);
        this.noOnBlock = block; // update current noOnBlock
        return block;
    }

    @Override
    public final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        MySQLUtils.assertItemWord(this.criteriaContext, itemWord, tableItem);
        return new TableBlock.DialectNoOnTableBlock(joinType, itemWord, tableItem, alias);
    }

    @Override
    public final _TableBlock createDynamicBlock(_JoinType joinType, DynamicBlock<?> block) {
        return MySQLSupports.createDynamicBlock(joinType, block);
    }



    /*################################## blow private method ##################################*/


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLQuery._IndexHintForJoinClause<C, UT> getIndexHintClause() {
        final MySQLSupports.MySQLNoOnBlock<C, UT> noOnBlock = this.noOnBlock;
        if (noOnBlock == null || this.criteriaContext.lastBlock() != noOnBlock) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        return noOnBlock.getUseIndexClause();
    }

    final void updateNoOnBlock(final MySQLSupports.MySQLNoOnBlock<C, UT> block) {
        if (this.noOnBlock != null) {
            throw ContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.noOnBlock = block;
    }


    /*################################## blow inner class  ##################################*/


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class SimpleUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate._MultiUpdate57Clause<C>,                    //WE
            MySQLUpdate._MultiWhereSpec<C>,                         //SR
            MySQLUpdate._MultiIndexHintJoinSpec<C>,                 //UT
            MySQLUpdate._MultiJoinSpec<C>,                          //US
            MySQLUpdate._MultiPartitionJoinClause<C>,               //UP
            MySQLUpdate._MultiIndexHintOnSpec<C>,                   //JT
            Statement._OnClause<C, MySQLUpdate._MultiJoinSpec<C>>,  //JS
            MySQLUpdate._MultiPartitionOnClause<C>,                 //JP
            Update._UpdateSpec,                                     //WR
            MySQLUpdate._MultiWhereAndSpec<C>>                      //WA
            implements MySQLUpdate._MultiJoinSpec<C>, MySQLUpdate._MultiWhereSpec<C>
            , MySQLUpdate._MultiWhereAndSpec<C>, MySQLUpdate._MultiIndexHintJoinSpec<C>
            , MySQLUpdate._WithAndMultiUpdateSpec<C> {


        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public _MultiPartitionJoinClause<C> createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new SimplePartitionJoinClause<>(joinType, table, this);
        }

        @Override
        public _MultiPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new SimplePartitionOnClause<>(joinType, table, this);
        }

        @Override
        public _MultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new SimpleOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        public _OnClause<C, _MultiJoinSpec<C>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
            MySQLUtils.assertItemWord(this.criteriaContext, itemWord, tableItem);
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, itemWord, tableItem, alias, this);
        }


    }// SimpleMultiUpdate

    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     *
     * @see #batch(Object)
     */
    private static final class BatchUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate._BatchMultiUpdateClause<C>,             //WE
            MySQLUpdate._BatchMultiWhereSpec<C>,                //SR
            MySQLUpdate._BatchMultiIndexHintJoinSpec<C>,        //UT
            MySQLUpdate._BatchMultiJoinSpec<C>,                 //US
            MySQLUpdate._BatchMultiPartitionJoinClause<C>,      //UP
            MySQLUpdate._BatchMultiIndexHintOnSpec<C>,          //JT
            Statement._OnClause<C, _BatchMultiJoinSpec<C>>,     //JS
            MySQLUpdate._BatchMultiPartitionOnClause<C>,        //JP
            MySQLUpdate._BatchParamClause<C, _UpdateSpec>,      //WR
            MySQLUpdate._BatchMultiWhereAndSpec<C>>             //WA
            implements MySQLUpdate._BatchMultiJoinSpec<C>, MySQLUpdate._BatchMultiWhereSpec<C>
            , MySQLUpdate._BatchMultiWhereAndSpec<C>, MySQLUpdate._BatchMultiIndexHintJoinSpec<C>
            , MySQLUpdate._BatchWithAndMultiUpdateSpec<C>, Statement._BatchParamClause<C, _UpdateSpec>
            , _BatchDml {


        private List<?> paramList;


        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _UpdateSpec paramList(List<P> paramList) {
            this.paramList = MySQLUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = MySQLUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Function<C, List<P>> function) {
            this.paramList = MySQLUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _UpdateSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = MySQLUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

        @Override
        public _BatchMultiPartitionJoinClause<C> createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new BatchPartitionJoinClause<>(joinType, table, this);
        }

        @Override
        public _BatchMultiPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new BatchPartitionOnClause<>(joinType, table, this);
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            return new BatchOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        public _OnClause<C, _BatchMultiJoinSpec<C>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
            MySQLUtils.assertItemWord(this.criteriaContext, itemWord, tableItem);
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, itemWord, tableItem, alias, this);
        }


    }// BatchUpdate


    private static final class SimplePartitionJoinClause<C> extends
            MySQLSupports.PartitionAsClause<C, MySQLUpdate._MultiIndexHintJoinSpec<C>>
            implements MySQLUpdate._MultiPartitionJoinClause<C> {
        private final SimpleUpdate<C> stmt;

        private SimplePartitionJoinClause(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C> stmt) {
            super(stmt.criteriaContext, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintJoinSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdate<C> stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<C, _MultiIndexHintJoinSpec<C>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);
            stmt.updateNoOnBlock(block);
            stmt.criteriaContext.onAddBlock(block);
            return stmt;
        }


    }// SimplePartitionJoinClause


    private static final class SimplePartitionOnClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLUpdate._MultiIndexHintOnSpec<C>>
            implements MySQLUpdate._MultiPartitionOnClause<C> {


        private final SimpleUpdate<C> stmt;

        private SimplePartitionOnClause(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C> stmt) {
            super(stmt.criteriaContext, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdate<C> stmt = this.stmt;
            final SimpleOnTableBlock<C> block;
            block = new SimpleOnTableBlock<>(params, stmt);
            stmt.criteriaContext.onAddBlock(block);
            return block;
        }


    }// SimplePartitionOnClause


    private static final class SimpleOnTableBlock<C>
            extends MySQLSupports.MySQLOnBlock<C, _MultiIndexHintOnSpec<C>, _MultiJoinSpec<C>>
            implements MySQLUpdate._MultiIndexHintOnSpec<C> {

        private SimpleOnTableBlock(_JoinType joinType, TableMeta<?> table
                , String alias, _MultiJoinSpec<C> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, _MultiJoinSpec<C> stmt) {
            super(params, stmt);
        }

    } //SimpleOnTableBlock


    private static final class BatchPartitionJoinClause<C>
            extends MySQLSupports.PartitionAsClause<C, _BatchMultiIndexHintJoinSpec<C>>
            implements MySQLUpdate._BatchMultiPartitionJoinClause<C> {


        private final BatchUpdate<C> stmt;

        private BatchPartitionJoinClause(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> stmt) {
            super(stmt.criteriaContext, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintJoinSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchUpdate<C> stmt = this.stmt;

            final MySQLSupports.MySQLNoOnBlock<C, _BatchMultiIndexHintJoinSpec<C>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);
            stmt.updateNoOnBlock(block);
            stmt.criteriaContext.onAddBlock(block);
            return stmt;
        }


    }// BatchPartitionJoinClause


    private static final class BatchPartitionOnClause<C>
            extends MySQLSupports.PartitionAsClause<C, _BatchMultiIndexHintOnSpec<C>>
            implements _BatchMultiPartitionOnClause<C> {


        private final BatchUpdate<C> stmt;

        private BatchPartitionOnClause(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> stmt) {
            super(stmt.criteriaContext, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintOnSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchUpdate<C> stmt = this.stmt;
            final BatchOnTableBlock<C> block;
            block = new BatchOnTableBlock<>(params, stmt);
            stmt.criteriaContext.onAddBlock(block);
            return block;
        }


    }// BatchPartitionOnBlock

    private static final class BatchOnTableBlock<C>
            extends MySQLSupports.MySQLOnBlock<C, _BatchMultiIndexHintOnSpec<C>, _BatchMultiJoinSpec<C>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<C> {

        private BatchOnTableBlock(_JoinType joinType, TableMeta<?> table
                , String alias, _BatchMultiJoinSpec<C> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params, _BatchMultiJoinSpec<C> stmt) {
            super(params, stmt);
        }


    } //SimpleOnTableBlock



}
