package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class MySQLMultiDelete<C, WE, DS, DP, JS, JP, WR, WA>
        extends WithCteMultiDelete<C, SubQuery, WE, DS, DS, DP, JS, JS, JP, WR, WA>
        implements _MySQLMultiDelete, MySQLDelete._MultiDeleteClause<C, DS, DP>
        , MySQLDelete._MultiDeleteFromClause<C, DS, DP>, MySQLDelete._MultiDeleteUsingClause<C, DS, DP>
        , _MySQLWithClause {


    static <C> _WithAndMultiDeleteSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndDelete<>(criteria);
    }

    static <C> _BatchWithAndMultiDeleteSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndDelete<>(criteria);
    }

    private boolean recursive;

    private List<Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLWords> modifierList;

    private boolean usingSyntax;

    private List<String> tableAliasList;

    private DP noActionPartitionJoinClause;

    private JP noActionPartitionOnClause;

    private MySQLMultiDelete(@Nullable C criteria) {
        super(CriteriaContexts.multiDmlContext(criteria));
    }


    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(Function<C, List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(Function<C, List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final DS from(TableMeta<?> table, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, table, alias);
    }

    @Override
    public final DP from(TableMeta<?> table) {
        return (DP) this.createClause(_JoinType.NONE, table);
    }


    @Override
    public final <T extends TableItem> DS from(Supplier<T> supplier, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> DS from(Function<C, T> function, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
    }

    @Override
    public final DS from(String cteName) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, cteName, "");
    }

    @Override
    public final DS from(String cteName, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, cteName, alias);
    }

    @Override
    public final DS using(TableMeta<?> table, String alias) {
        return this.from(table, alias);
    }

    @Override
    public final DP using(TableMeta<?> table) {
        return this.from(table);
    }

    @Override
    public final <T extends TableItem> DS using(Supplier<T> supplier, String alias) {
        return this.from(supplier, alias);
    }

    @Override
    public final <T extends TableItem> DS using(Function<C, T> function, String alias) {
        return this.from(function, alias);
    }

    @Override
    public final DS using(String cteName) {
        return this.from(cteName);
    }

    @Override
    public final DS using(String cteName, String alias) {
        return this.from(cteName, alias);
    }


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
    public final boolean usingSyntax() {
        return this.usingSyntax;
    }

    @Override
    public final List<String> tableAliasList() {
        return this.tableAliasList;
    }


    @Override
    final void validateBeforeClearContext() {
        final List<String> tableAliasList = this.tableAliasList;
        if (tableAliasList == null) {
            throw _Exceptions.castCriteriaApi();
        }
        for (String tableAlias : tableAliasList) {
            if (!this.criteriaContext.containsTable(tableAlias)) {
                throw _Exceptions.unknownTableAlias(tableAlias);
            }
        }
    }

    @Override
    final void onAsDelete() {
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (_CollectionUtils.isEmpty(this.tableAliasList)) {
            throw new CriteriaException("tableAliasList must not empty in multi-table delete clause.");
        }
        this.noActionPartitionOnClause = null;
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).paramLisst == null) {
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
            ((BatchDelete<C>) this).paramLisst = null;
        }

    }

    @Override
    final void crossJoinEvent(boolean success) {
        //no-op
    }

    @Override
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
    }


    private CriteriaException tableAliasListIsEmpty() {
        return new CriteriaException("table alias list must not empty");
    }



    /*################################## blow inner class ##################################*/


    private static final class SimpleDelete<C> extends MySQLMultiDelete<
            C,
            _MultiDelete57Clause<C>,
            MySQLDelete._MultiJoinSpec<C>,
            MySQLDelete._MultiPartitionJoinClause<C>,
            Statement._OnClause<C, MySQLDelete._MultiJoinSpec<C>>,
            MySQLDelete._MultiPartitionOnClause<C>,
            _DeleteSpec,
            MySQLDelete._MultiWhereAndSpec<C>>
            implements MySQLDelete._WithAndMultiDeleteSpec<C>, MySQLDelete._MultiJoinSpec<C>
            , MySQLDelete._MultiWhereAndSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public _TableBlock createAndAddBlock(_JoinType joinType, Object item, String alias) {
            return null;
        }

        @Override
        public Object createClause(_JoinType joinType, TableMeta<?> table) {
            return null;
        }

        @Override
        public Object getNoActionClause(_JoinType joinType) {
            return null;
        }

        @Override
        public Object getNoActionClauseBeforeAs(_JoinType joinType) {
            return null;
        }
    }//SimpleDelete

    private static final class BatchDelete<C> extends MySQLMultiDelete<
            C,
            MySQLDelete._BatchMultiDeleteClause<C>,
            MySQLDelete._BatchMultiJoinSpec<C>,
            MySQLDelete._BatchMultiPartitionJoinClause<C>,
            Statement._OnClause<C, MySQLDelete._BatchMultiJoinSpec<C>>,
            MySQLDelete._BatchMultiPartitionOnClause<C>,
            Statement._BatchParamClause<C, Delete._DeleteSpec>,
            MySQLDelete._BatchMultiWhereAndSpec<C>>
            implements MySQLDelete._BatchWithAndMultiDeleteSpec<C>, MySQLDelete._BatchMultiJoinSpec<C>
            , MySQLDelete._BatchMultiWhereAndSpec<C>, Statement._BatchParamClause<C, Delete._DeleteSpec>, _BatchDml {


        private List<?> paramLisst;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _DeleteSpec paramList(List<P> paramList) {
            return null;
        }

        @Override
        public <P> _DeleteSpec paramList(Supplier<List<P>> supplier) {
            return null;
        }

        @Override
        public <P> _DeleteSpec paramList(Function<C, List<P>> function) {
            return null;
        }

        @Override
        public _DeleteSpec paramList(Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public List<?> paramList() {
            return this.paramLisst;
        }


    }//SimpleDelete


    /**
     * @see SimpleDelete#createPartitionJoinClause(TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C, WE> extends MySQLPartitionClause<C, MySQLDelete.MultiAsJoinSpec<C>>
            implements MySQLDelete.MultiAsJoinSpec<C>, _MultiPartitionJoinClause<C> {

        private final TableMeta<?> table;

        private final SimpleDelete<C, WE> delete;

        private SimplePartitionJoinSpec(TableMeta<?> table, SimpleDelete<C, WE> delete) {
            super(delete.criteria);
            this.table = table;
            this.delete = delete;
        }

        @Override
        public _MultiJoinSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = TableBlock.noneBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            this.delete.criteriaContext.onBlockWithoutOnClause(block);
            return this.delete;
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchDelete#createPartitionJoinClause(TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C, WE>
            extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsJoinSpec<C>>
            implements MySQLDelete.BatchMultiAsJoinSpec<C>, _BatchMultiPartitionJoinClause<C> {


        private final TableMeta<?> table;

        private final BatchDelete<C, WE> delete;

        private BatchPartitionJoinSpec(TableMeta<?> table, BatchDelete<C, WE> delete) {
            super(delete.criteria);
            this.table = table;
            this.delete = delete;
        }

        @Override
        public _BatchMultiJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = TableBlock.noneBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            this.delete.criteriaContext.onBlockWithoutOnClause(block);
            return this.delete;
        }
    }// BatchPartitionJoinSpec


    /**
     * @see SimpleDelete#createItemBlock(_JoinType, TableItem, String)
     * @see SimpleDelete#createTableBlock(_JoinType, TableMeta, String)
     */
    private static class SimpleOnBlock<C, WE> extends OnClauseTableBlock<C, _MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private final SimpleDelete<C, WE> delete;

        private SimpleOnBlock(_JoinType joinType, TableItem tableItem, String alias, SimpleDelete<C, WE> delete) {
            super(joinType, tableItem, alias);
            this.delete = delete;
        }

        @Override
        C getCriteria() {
            return this.delete.criteria;
        }

        @Override
        _MultiJoinSpec<C> endOnClause() {
            return this.delete;
        }


    }// SimpleMultiOnBlock

    /**
     * @see SimplePartitionOnBlock#as(String)
     */
    private static final class SimpleOnBlockWithPartition<C, WE> extends SimpleOnBlock<C, WE> implements _MySQLTableBlock {

        private final List<String> partitionList;

        private SimpleOnBlockWithPartition(_JoinType joinType, TableItem tableItem
                , String alias, SimpleDelete<C, WE> delete, List<String> partitionList) {
            super(joinType, tableItem, alias, delete);
            switch (partitionList.size()) {
                case 0:
                case 1:
                    this.partitionList = partitionList;
                    break;
                default:
                    this.partitionList = Collections.unmodifiableList(partitionList);
            }
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }//SimpleOnBlockWithPartition


    /**
     * @see SimpleDelete#createBlockBeforeAs(_JoinType, TableMeta)
     */
    private static final class SimplePartitionOnBlock<C, WE>
            extends MySQLPartitionClause<C, MySQLDelete.MultiAsOnSpec<C>>
            implements _MultiPartitionOnClause<C>, MySQLDelete.MultiAsOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleDelete<C, WE> delete;

        private SimplePartitionOnBlock(_JoinType joinType, TableMeta<?> tablePart, SimpleDelete<C, WE> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = tablePart;
            this.delete = delete;
        }

        @Override
        public MultiOnSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final SimpleOnBlock<C, WE> onBlock;
            if (partitionList == null) {
                onBlock = new SimpleOnBlock<>(this.joinType, this.table, alias, this.delete);
            } else {
                onBlock = new SimpleOnBlockWithPartition<>(this.joinType, this.table, alias, this.delete, partitionList);
            }
            this.delete.criteriaContext.onAddBlock(onBlock);
            return onBlock;
        }


    }// SimpleMultiPartitionBlock


    /**
     * @see BatchDelete#createTableBlock(_JoinType, TableMeta, String)
     * @see BatchDelete#createItemBlock(_JoinType, TableItem, String)
     */
    private static class BatchOnBlock<C, WE> extends OnClauseTableBlock<C, _BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {


        private final BatchDelete<C, WE> delete;

        private BatchOnBlock(_JoinType joinType, TableItem tableItem, String alias, BatchDelete<C, WE> delete) {
            super(joinType, tableItem, alias);
            this.delete = delete;
        }

        @Override
        final C getCriteria() {
            return this.delete.criteria;
        }

        @Override
        final _BatchMultiJoinSpec<C> endOnClause() {
            return this.delete;
        }


    }// BatchMultiOnBlock

    private static final class BatchOnBlockWithPartition<C, WE> extends BatchOnBlock<C, WE> implements _MySQLTableBlock {

        private final List<String> partitionList;

        private BatchOnBlockWithPartition(_JoinType joinType, TableItem tableItem, String alias
                , BatchDelete<C, WE> delete, List<String> partitionList) {
            super(joinType, tableItem, alias, delete);

            switch (partitionList.size()) {
                case 0:
                case 1:
                    this.partitionList = partitionList;
                    break;
                default:
                    this.partitionList = Collections.unmodifiableList(partitionList);
            }
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }//BatchOnBlockWithPartition

    /**
     * @see BatchDelete#createBlockBeforeAs(_JoinType, TableMeta)
     */
    private static final class BatchPartitionBlock<C, WE> extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, _BatchMultiPartitionOnClause<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchDelete<C, WE> delete;

        private BatchPartitionBlock(_JoinType joinType, TableMeta<?> table, BatchDelete<C, WE> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = table;
            this.delete = delete;
        }

        @Override
        public BatchMultiOnSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);

            final List<String> tempList = this.partitionList;
            final BatchOnBlock<C, WE> onBlock;
            if (tempList == null) {
                onBlock = new BatchOnBlock<>(this.joinType, this.table, alias, this.delete);
            } else {
                onBlock = new BatchOnBlockWithPartition<>(this.joinType, this.table, alias, this.delete, tempList);
            }
            this.delete.criteriaContext.onAddBlock(onBlock);
            return onBlock;
        }


    }// BatchPartitionBlock


    /**
     * @see SimpleDelete#createNoActionPartitionBlock()
     */
    private static final class NoActionPartitionBlock<C, WE>
            extends MySQLNoActionPartitionClause<C, MultiAsOnSpec<C>>
            implements MySQLDelete.MultiAsOnSpec<C>, _MultiPartitionOnClause<C> {

        private final MultiOnSpec<C> onSpec;

        private NoActionPartitionBlock(SimpleDelete<C, WE> delete) {
            this.onSpec = new NoActionOnBlock<>(delete);
        }

        @Override
        public MultiOnSpec<C> as(String alias) {
            return this.onSpec;
        }

    }// NoActionPartitionBlock

    /**
     * @see SimpleDelete#createNoActionTableBlock()
     * @see SimpleDelete#createNoActionOnBlock()
     */
    private static final class NoActionOnBlock<C> extends NoActionOnClause<C, _MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private NoActionOnBlock(_MultiJoinSpec<C> stmt) {
            super(stmt);
        }

    }

    /**
     * @see BatchDelete#createNoActionTableBlock()
     * @see BatchDelete#createNoActionOnBlock()
     */
    private static final class BatchNoActionOnBlock<C> extends NoActionOnClause<C, _BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {

        private BatchNoActionOnBlock(_BatchMultiJoinSpec<C> stmt) {
            super(stmt);
        }

    }// NoActionBatchOnBlock

    /**
     * @see BatchDelete#createNoActionPartitionBlock()
     */
    private static final class BatchNoActionPartitionBlock<C>
            extends MySQLNoActionPartitionClause<C, BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, _BatchMultiPartitionOnClause<C> {

        private final BatchMultiOnSpec<C> onSpec;

        private BatchNoActionPartitionBlock(_BatchMultiJoinSpec<C> delete) {
            this.onSpec = new BatchNoActionOnBlock<>(delete);
        }

        @Override
        public BatchMultiOnSpec<C> as(String alias) {
            return this.onSpec;
        }

    }// BatchNoActionPartitionBlock


}
