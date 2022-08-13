package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    private List<Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLWords> modifierList;

    private MySQLSupports.MySQLNoOnBlock<C, UT> noOnBlock;


    private MySQLMultiUpdate(@Nullable C criteria) {
        super(CriteriaContexts.primaryMultiDmlContext(criteria));
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , TableMeta<?> table) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);
        return this.createNoOnTableClause(_JoinType.NONE, null, table);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
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
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);

        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);


        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<T> supplier, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Function<C, T> function, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US updateLateral(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);

        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US updateLateral(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);


        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, function.apply(this.criteria), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US updateLateral(Supplier<T> supplier, String alias) {
        this.criteriaContext.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, supplier.get(), alias));
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US updateLateral(Function<C, T> function, String alias) {
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
    public final List<Cte> cteList() {
        return this.cteList;
    }


    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLWords> modifierList() {
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
        this.command = null;
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        this.noActionOnClause = null;

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
    final boolean isSupportMultiTableUpdate() {
        // true ,this is multi-table update
        return true;
    }

    @Override
    final MySQLDialect dialect() {
        return MySQLDialect.MySQL80;
    }


    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        this.recursive = recursive;
        this.cteList = cteList;
    }

    @Override
    public final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        final MySQLSupports.MySQLNoOnBlock<C, UT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (UT) this);
        this.noOnBlock = block; // update current noOnBlock
        return block;
    }

    @Override
    public final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
        MySQLUtils.assertItemWord(this.criteriaContext, itemWord, tableItem);
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, itemWord, tableItem, alias, (US) this);
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
        if (noOnBlock == null || this.criteriaContext.lastTableBlockWithoutOnClause() != noOnBlock) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return noOnBlock.getUseIndexClause();
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
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new SimplePartitionJoinClause<>(joinType, table, this);
        }

        @Override
        public _MultiPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new SimplePartitionOnClause<>(joinType, table, this);
        }

        @Override
        public _MultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            if (itemWord != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return new SimpleOnTableBlock<>(joinType, null, table, tableAlias, this);
        }

        @Override
        public _OnClause<C, _MultiJoinSpec<C>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
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
            return super.createNoOnTableClause(joinType, itemWord, table);
        }

        @Override
        public _BatchMultiPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            return super.createTableClause(joinType, itemWord, table);
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            return null;
        }

        @Override
        public _OnClause<C, _BatchMultiJoinSpec<C>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem, String alias) {
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
            ((MySQLMultiUpdate<C, ?, ?, _MultiIndexHintJoinSpec<C>, ?, ?, ?, ?, ?, ?, ?>) stmt).noOnBlock = block;
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

        private SimpleOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableItem tableItem
                , String alias, _MultiJoinSpec<C> stmt) {
            super(joinType, itemWord, tableItem, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, _MultiJoinSpec<C> stmt) {
            super(params, stmt);
        }

    } //SimpleOnTableBlock


    private static final class SimpleNoActionIndexHintOnClause<C> extends MySQLNoActionIndexHintOnClause<
            C,
            _IndexForJoinOnClause<C>,
            _MultiIndexHintOnSpec<C>,
            _MultiJoinSpec<C>>
            implements _MultiIndexHintOnSpec<C>, _IndexForJoinOnClause<C> {

        private SimpleNoActionIndexHintOnClause(_MultiJoinSpec<C> update) {
            super(update);
        }

    }// SimpleNoActionIndexHintOnBlock

    private static final class SimpleNoActionPartitionJoinClause<C> extends MySQLNoActionPartitionClause<
            C,
            _AsClause<_MultiIndexHintJoinSpec<C>>>
            implements MySQLUpdate._MultiPartitionJoinClause<C>, _AsClause<_MultiIndexHintJoinSpec<C>> {

        private final _MultiIndexHintJoinSpec<C> spec;

        private SimpleNoActionPartitionJoinClause(_MultiIndexHintJoinSpec<C> spec) {
            this.spec = spec;
        }

        @Override
        public _MultiIndexHintJoinSpec<C> as(String alias) {
            return this.spec;
        }

    }// SimpleNoActionPartitionJoinClause


    private static final class SimpleNoActionPartitionOnClause<C> extends MySQLNoActionPartitionClause<
            C,
            _AsClause<_MultiIndexHintOnSpec<C>>>
            implements _MultiPartitionOnClause<C>, _AsClause<_MultiIndexHintOnSpec<C>> {

        private final Supplier<?> supplier;


        private SimpleNoActionPartitionOnClause(Supplier<?> supplier) {
            this.supplier = supplier;
        }

        @Override
        public _MultiIndexHintOnSpec<C> as(String alias) {
            return (_MultiIndexHintOnSpec<C>) this.supplier.get();
        }

    }// SimpleNoActionPartitionOnClause


    private static final class BatchIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate._BatchIndexForJoinOnClause<C>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate._BatchMultiJoinSpec<C>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<C>, MySQLUpdate._BatchIndexForJoinOnClause<C> {

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String alias, _BatchMultiJoinSpec<C> stmt) {
            super(joinType, table, alias, stmt);
        }

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String alias, List<String> partitionList
                , _BatchMultiJoinSpec<C> stmt) {
            super(joinType, table, alias, partitionList, stmt);
        }

    }//BatchIndexHintOnBlock


    private static final class BatchPartitionJoinClause<C>
            extends MySQLPartitionClause2<C, _AsClause<_BatchMultiIndexHintJoinSpec<C>>>
            implements _BatchMultiPartitionJoinClause<C>, _AsClause<_BatchMultiIndexHintJoinSpec<C>> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchUpdate<C> update;

        private BatchPartitionJoinClause(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final _TableBlock block;
            if (partitionList == null) {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias, partitionList);
            }
            this.update.criteriaContext.onAddBlock(block);
            if (this.joinType == _JoinType.CROSS_JOIN) {
                this.update.crossJoinEvent(true);
            }
            return this.update;
        }
    }// BatchPartitionJoinClause


    private static final class BatchPartitionOnClause<C>
            extends MySQLPartitionClause2<C, _AsClause<_BatchMultiIndexHintOnSpec<C>>>
            implements _AsClause<_BatchMultiIndexHintOnSpec<C>>
            , _BatchMultiPartitionOnClause<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchUpdate<C> update;

        private BatchPartitionOnClause(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> update) {
            super(update.criteria);
            this.update = update;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final BatchIndexHintOnBlock<C> block;
            if (partitionList == null) {
                block = new BatchIndexHintOnBlock<>(this.joinType, this.table, tableAlias, this.update);
            } else {
                block = new BatchIndexHintOnBlock<>(this.joinType, this.table, tableAlias
                        , partitionList, this.update);
            }
            this.update.criteriaContext.onAddBlock(block);
            return block;
        }


    }// BatchPartitionOnBlock


    private static final class BatchNoActionIndexHintOnClause<C> extends MySQLNoActionIndexHintOnClause<
            C,
            MySQLUpdate._BatchIndexForJoinOnClause<C>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate._BatchMultiJoinSpec<C>>
            implements _BatchMultiIndexHintOnSpec<C>, _BatchIndexForJoinOnClause<C> {

        private BatchNoActionIndexHintOnClause(_BatchMultiJoinSpec<C> update) {
            super(update);
        }

    }// BatchNoActionIndexHintOnClause

    private static final class BatchNoActionPartitionJoinClause<C> extends MySQLNoActionPartitionClause<
            C, _AsClause<_BatchMultiIndexHintJoinSpec<C>>>
            implements _BatchMultiPartitionJoinClause<C>, _AsClause<_BatchMultiIndexHintJoinSpec<C>> {

        private final _BatchMultiIndexHintJoinSpec<C> spec;

        private BatchNoActionPartitionJoinClause(_BatchMultiIndexHintJoinSpec<C> spec) {
            this.spec = spec;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<C> as(String alias) {
            return this.spec;
        }

    }//BatchNoActionPartitionJoinClause


    private static final class BatchNoActionPartitionOnClause<C> extends MySQLNoActionPartitionClause<
            C, _AsClause<_BatchMultiIndexHintOnSpec<C>>>
            implements _BatchMultiPartitionOnClause<C>, _AsClause<_BatchMultiIndexHintOnSpec<C>> {

        private final Supplier<?> supplier;

        private BatchNoActionPartitionOnClause(Supplier<?> supplier) {
            this.supplier = supplier;
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> as(String tableAlias) {
            return (_BatchMultiIndexHintOnSpec<C>) this.supplier.get();
        }
    }//BatchNoActionPartitionOnClause


}
