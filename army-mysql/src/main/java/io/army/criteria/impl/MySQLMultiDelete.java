package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
abstract class MySQLMultiDelete<C, WE, DT, DS, DP, JT, JS, JP, WR, WA>
        extends WithCteMultiDelete<C, SubQuery, WE, DT, DS, DP, DS, JT, JS, JP, WR, WA, Delete>
        implements _MySQLMultiDelete, MySQLDelete._MultiDeleteClause<C, DT, DS, DP>
        , MySQLDelete._MultiDeleteFromClause<C, DT, DS, DP>, MySQLDelete._MultiDeleteUsingClause<C, DT, DS, DP>
        , MySQLQuery._IndexHintForJoinClause<C, DT>, _MySQLWithClause, MySQLDelete, Delete._DeleteSpec {


    static <C> _WithAndMultiDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> _BatchWithAndMultiDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }

    private boolean recursive;

    private List<_Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLSyntax._MySQLModifier> modifierList;

    private boolean usingSyntax;

    private List<String> tableAliasList;

    private List<_Pair<String, TableMeta<?>>> deleteTablePairList;


    private MySQLSupports.MySQLNoOnBlock<C, DT> noOnBlock;

    private MySQLMultiDelete(@Nullable C criteria) {
        super(CriteriaContexts.primaryMultiDmlContext(criteria));
    }


    @Override
    public final _MultiDeleteFromClause<C, DT, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers, List<String> tableAliasList) {
        this.hintList = MySQLUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        return this;
    }

    @Override
    public final _MultiDeleteFromAliasClause<C, DT, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLSyntax._MySQLModifier> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return new FromTableAliasClause<>(this::fromAliasEnd);
    }

    @Override
    public final _MultiDeleteFromClause<C, DT, DS, DP> delete(List<String> tableAliasList) {
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DT, DS, DP> delete(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DT, DS, DP> deleteFrom(List<String> tableAliasList) {
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DT, DS, DP> deleteFrom(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        return this;
    }

    @Override
    public final DT from(TableMeta<?> table, String tableAlias) {
        this.context.onAddBlock(this.createNoOnTableBlock(_JoinType.NONE, null, table, tableAlias));
        return (DT) this;
    }

    @Override
    public final <T extends TabularItem> DS from(Supplier<T> supplier, String alias) {
        this.context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, supplier.get(), alias));
        return (DS) this;
    }

    @Override
    public final <T extends TabularItem> DS from(Function<C, T> function, String alias) {
        this.context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, function.apply(this.criteria)
                , alias));
        return (DS) this;
    }

    @Override
    public final DS from(String cteName) {
        final CriteriaContext context = this.context;
        context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, context.refCte(cteName), ""));
        return (DS) this;
    }

    @Override
    public final DS from(String cteName, String alias) {
        final CriteriaContext context = this.context;
        context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, null, context.refCte(cteName), alias));
        return (DS) this;
    }

    @Override
    public final <T extends SubQuery> DS fromLateral(Supplier<T> supplier, String alias) {
        this.context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL, supplier.get()
                , alias));
        return (DS) this;
    }

    @Override
    public final <T extends SubQuery> DS fromLateral(Function<C, T> function, String alias) {
        this.context.onAddBlock(this.createNoOnItemBlock(_JoinType.NONE, ItemWord.LATERAL
                , function.apply(this.criteria), alias));
        return (DS) this;
    }

    @Override
    public final DP from(TableMeta<?> table) {
        return this.createNoOnTableClause(_JoinType.NONE, null, table);
    }

    @Override
    public final DT using(TableMeta<?> table, String alias) {
        this.usingSyntax = true;
        return this.from(table, alias);
    }

    @Override
    public final <T extends TabularItem> DS using(Supplier<T> supplier, String alias) {
        this.usingSyntax = true;
        return this.from(supplier, alias);
    }

    @Override
    public final <T extends TabularItem> DS using(Function<C, T> function, String alias) {
        this.usingSyntax = true;
        return this.from(function, alias);
    }

    @Override
    public final <T extends SubQuery> DS usingLateral(Supplier<T> supplier, String alias) {
        this.usingSyntax = true;
        return this.fromLateral(supplier, alias);
    }

    @Override
    public final <T extends SubQuery> DS usingLateral(Function<C, T> function, String alias) {
        this.usingSyntax = true;
        return this.fromLateral(function, alias);
    }

    @Override
    public final DS using(String cteName) {
        this.usingSyntax = true;
        return this.from(cteName);
    }

    @Override
    public final DS using(String cteName, String alias) {
        this.usingSyntax = true;
        return this.from(cteName, alias);
    }

    @Override
    public final DP using(TableMeta<?> table) {
        this.usingSyntax = true;
        return this.from(table);
    }


    @Override
    public final MySQLQuery._IndexForJoinSpec<C, DT> useIndex() {
        return this.getIndexHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<C, DT> ignoreIndex() {
        return this.getIndexHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForJoinSpec<C, DT> forceIndex() {
        return this.getIndexHintClause().forceIndex();
    }

    @Override
    public final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        final MySQLSupports.MySQLNoOnBlock<C, DT> block;
        block = new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, (DT) this);
        this.noOnBlock = block;
        return block;
    }

    @Override
    public final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        MySQLUtils.assertItemWord(this.context, itemWord, tableItem);
        return new TableBlock.DialectNoOnTableBlock(joinType, itemWord, tableItem, alias);
    }

    @Override
    public final _TableBlock createDynamicBlock(_JoinType joinType, DynamicBlock<?> block) {
        return MySQLSupports.createDynamicBlock(joinType, block);
    }


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
    public final boolean usingSyntax() {
        return this.usingSyntax;
    }


    @Override
    public final List<_Pair<String, TableMeta<?>>> deleteTableList() {
        final List<_Pair<String, TableMeta<?>>> pairList = this.deleteTablePairList;
        if (pairList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return pairList;
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
    final void validateBeforeClearContext() {
        final List<String> tableAliasList = this.tableAliasList;
        if (tableAliasList == null || tableAliasList.size() == 0) {
            throw ContextStack.criteriaError(this.context, "table alias list must non-empty.");
        }
        List<_Pair<String, TableMeta<?>>> pairList;
        TableMeta<?> table;
        if (tableAliasList.size() == 1) {
            final String tableAlias = tableAliasList.get(0);
            table = this.context.getTable(tableAlias);
            if (table == null) {
                throw _Exceptions.unknownTableAlias(tableAlias);
            }
            pairList = Collections.singletonList(_Pair.create(tableAlias, table));
        } else {
            pairList = new ArrayList<>(tableAliasList.size());
            for (String tableAlias : tableAliasList) {
                table = this.context.getTable(tableAlias);
                if (table == null) {
                    throw _Exceptions.unknownTableAlias(tableAlias);
                }
                pairList.add(_Pair.create(tableAlias, table));
            }
            pairList = Collections.unmodifiableList(pairList);
        }
        this.deleteTablePairList = pairList;
    }

    @Override
    final void onAsDelete() {
        if (this.deleteTablePairList == null) {
            //no bug,never here
            throw new IllegalStateException();
        }
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        if (this instanceof BatchDelete && ((BatchDelete<C>) this).paramList == null) {
            throw _Exceptions.batchParamEmpty();
        }
    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.hintList = null;
        this.modifierList = null;
        this.tableAliasList = null;

        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).paramList = null;
        }

    }


    @Override
    final void doWithCte(boolean recursive, List<_Cte> cteList) {
        this.recursive = recursive;
        this.cteList = cteList;
    }

    final void updateNoOnBlock(final MySQLSupports.MySQLNoOnBlock<C, DT> block) {
        if (this.noOnBlock != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.noOnBlock = block;
    }

    private _MultiDeleteUsingClause<C, DT, DS, DP> fromAliasEnd(List<String> tableAliasList) {
        this.tableAliasList = tableAliasList;
        return this;
    }

    private MySQLQuery._IndexHintForJoinClause<C, DT> getIndexHintClause() {
        final MySQLSupports.MySQLNoOnBlock<C, DT> block = this.noOnBlock;
        if (block == null || this.context.lastBlock() != block) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return block.getUseIndexClause();
    }



    /*################################## blow inner class ##################################*/


    private static final class SimpleDelete<C> extends MySQLMultiDelete<
            C,
            _MultiDelete57Clause<C>,
            MySQLDelete._MultiIndexHintJoinSpec<C>,
            MySQLDelete._MultiJoinSpec<C>,
            MySQLDelete._MultiPartitionJoinClause<C>,
            MySQLDelete._MultiIndexHintOnSpec<C>,
            Statement._OnClause<C, MySQLDelete._MultiJoinSpec<C>>,
            MySQLDelete._MultiPartitionOnClause<C>,
            _DeleteSpec,
            MySQLDelete._MultiWhereAndSpec<C>>
            implements MySQLDelete._WithAndMultiDeleteSpec<C>, MySQLDelete._MultiIndexHintJoinSpec<C>
            , MySQLDelete._MultiWhereAndSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public _MultiPartitionJoinClause<C> createNoOnTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new SimplePartitionJoinClause<>(joinType, table, this);
        }

        @Override
        public _MultiPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new SimplePartitionOnClause<>(joinType, table, this);
        }

        @Override
        public _MultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new SimpleOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        public _OnClause<C, _MultiJoinSpec<C>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
            MySQLUtils.assertItemWord(this.context, itemWord, tableItem);
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, itemWord, tableItem, alias, this);
        }

    }//SimpleDelete

    private static final class BatchDelete<C> extends MySQLMultiDelete<
            C,
            MySQLDelete._BatchMultiDeleteClause<C>,
            MySQLDelete._BatchMultiIndexHintJoinSpec<C>,
            MySQLDelete._BatchMultiJoinSpec<C>,
            MySQLDelete._BatchMultiPartitionJoinClause<C>,
            MySQLDelete._BatchMultiIndexHintOnSpec<C>,
            Statement._OnClause<C, MySQLDelete._BatchMultiJoinSpec<C>>,
            MySQLDelete._BatchMultiPartitionOnClause<C>,
            Statement._BatchParamClause<C, Delete._DeleteSpec>,
            MySQLDelete._BatchMultiWhereAndSpec<C>>
            implements MySQLDelete._BatchWithAndMultiDeleteSpec<C>, MySQLDelete._BatchMultiIndexHintJoinSpec<C>
            , MySQLDelete._BatchMultiWhereAndSpec<C>, Statement._BatchParamClause<C, Delete._DeleteSpec>, _BatchDml {


        private List<?> paramList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _DeleteSpec paramList(List<P> paramList) {
            this.paramList = MySQLUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = MySQLUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Function<C, List<P>> function) {
            this.paramList = MySQLUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _DeleteSpec paramList(Function<String, ?> function, String keyName) {
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
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new BatchPartitionJoinClause<>(joinType, table, this);
        }

        @Override
        public _BatchMultiPartitionOnClause<C> createTableClause(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new BatchPartitionOnClause<>(joinType, table, this);
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
            if (itemWord != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return new BatchOnTableBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        public _OnClause<C, _BatchMultiJoinSpec<C>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
            MySQLUtils.assertItemWord(this.context, itemWord, tableItem);
            return new OnClauseTableBlock.OnItemTableBlock<>(joinType, itemWord, tableItem, alias, this);
        }


    }//SimpleDelete


    private static final class SimplePartitionJoinClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLDelete._MultiIndexHintJoinSpec<C>>
            implements MySQLDelete._MultiPartitionJoinClause<C> {

        private final SimpleDelete<C> stmt;

        private SimplePartitionJoinClause(_JoinType joinType, TableMeta<?> table, SimpleDelete<C> stmt) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }


        @Override
        _MultiIndexHintJoinSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleDelete<C> stmt = this.stmt;
            final MySQLSupports.MySQLNoOnBlock<C, MySQLDelete._MultiIndexHintJoinSpec<C>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);
            stmt.updateNoOnBlock(block);
            stmt.context.onAddBlock(block);
            return stmt;
        }

    }// SimplePartitionJoinClause


    private static final class SimplePartitionOnClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLDelete._MultiIndexHintOnSpec<C>>
            implements MySQLDelete._MultiPartitionOnClause<C> {

        private final SimpleDelete<C> stmt;

        private SimplePartitionOnClause(_JoinType joinType, TableMeta<?> table, SimpleDelete<C> stmt) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _MultiIndexHintOnSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final SimpleDelete<C> stmt = this.stmt;
            final SimpleOnTableBlock<C> block;
            block = new SimpleOnTableBlock<>(params, stmt);
            stmt.context.onAddBlock(block);
            return block;
        }

    }// SimplePartitionOnClause

    private static final class SimpleOnTableBlock<C>
            extends MySQLSupports.MySQLOnBlock<C, MySQLDelete._MultiIndexHintOnSpec<C>, MySQLDelete._MultiJoinSpec<C>>
            implements MySQLDelete._MultiIndexHintOnSpec<C> {

        private SimpleOnTableBlock(_JoinType joinType, TableMeta<?> table
                , String alias, MySQLDelete._MultiJoinSpec<C> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private SimpleOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._MultiJoinSpec<C> stmt) {
            super(params, stmt);
        }

    } //SimpleOnTableBlock


    private static final class BatchPartitionJoinClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLDelete._BatchMultiIndexHintJoinSpec<C>>
            implements MySQLDelete._BatchMultiPartitionJoinClause<C> {

        private final BatchDelete<C> stmt;

        private BatchPartitionJoinClause(_JoinType joinType, TableMeta<?> table, BatchDelete<C> stmt) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintJoinSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchDelete<C> stmt = this.stmt;
            final MySQLSupports.MySQLNoOnBlock<C, MySQLDelete._BatchMultiIndexHintJoinSpec<C>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);
            stmt.updateNoOnBlock(block);
            stmt.context.onAddBlock(block);
            return stmt;
        }

    }// BatchPartitionJoinClause


    private static final class BatchPartitionOnClause<C>
            extends MySQLSupports.PartitionAsClause<C, MySQLDelete._BatchMultiIndexHintOnSpec<C>>
            implements MySQLDelete._BatchMultiPartitionOnClause<C> {

        private final BatchDelete<C> stmt;

        private BatchPartitionOnClause(_JoinType joinType, TableMeta<?> table, BatchDelete<C> stmt) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _BatchMultiIndexHintOnSpec<C> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final BatchDelete<C> stmt = this.stmt;
            final BatchOnTableBlock<C> block;
            block = new BatchOnTableBlock<>(params, stmt);
            stmt.context.onAddBlock(block);
            return block;
        }


    }// BatchPartitionBlock

    private static final class BatchOnTableBlock<C>
            extends MySQLSupports.MySQLOnBlock<C, MySQLDelete._BatchMultiIndexHintOnSpec<C>, MySQLDelete._BatchMultiJoinSpec<C>>
            implements MySQLDelete._BatchMultiIndexHintOnSpec<C> {

        private BatchOnTableBlock(_JoinType joinType, TableMeta<?> table
                , String alias, MySQLDelete._BatchMultiJoinSpec<C> stmt) {
            super(joinType, null, table, alias, stmt);
        }

        private BatchOnTableBlock(MySQLSupports.MySQLBlockParams params, MySQLDelete._BatchMultiJoinSpec<C> stmt) {
            super(params, stmt);
        }

    } //BatchOnTableBlock


    private static final class FromTableAliasClause<C, DT, DS, DP>
            implements _MultiDeleteFromAliasClause<C, DT, DS, DP> {

        private final Function<List<String>, _MultiDeleteUsingClause<C, DT, DS, DP>> function;

        private FromTableAliasClause(Function<List<String>, _MultiDeleteUsingClause<C, DT, DS, DP>> function) {
            this.function = function;
        }

        @Override
        public _MultiDeleteUsingClause<C, DT, DS, DP> from(List<String> tableAliasList) {
            return this.function.apply(_CollectionUtils.asUnmodifiableList(tableAliasList));
        }

        @Override
        public _MultiDeleteUsingClause<C, DT, DS, DP> from(String tableAlias1, String tableAlias2) {
            return this.function.apply(ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2));
        }


    }//FromTableAliasClause


}
