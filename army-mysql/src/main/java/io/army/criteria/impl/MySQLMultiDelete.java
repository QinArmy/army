package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLModifier;
import io.army.criteria.mysql.MySQLQuery;
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
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class MySQLMultiDelete<C, WE, DR, DP, JT, IT, WR, WA> extends WithCteMultiDelete<C, WE, JT, JT, IT, WR, WA>
        implements _MySQLMultiDelete, MySQLDelete, MySQLQuery.MySQLJoinClause<C, JT, JT, IT>
        , MySQLDelete.MultiDeleteClause<C, DR, DP>, MySQLDelete.MultiDeleteFromClause<C, DR, DP>
        , MySQLDelete.MultiDeleteUsingClause<C, DR, DP> {


    static <C> MySQLDelete.MultiDeleteSpec<C> simple(@Nullable C criteria) {
        final SimpleDelete<C, Void> delete;
        delete = new SimpleDelete<>(criteria);
        return delete;
    }

    static <C> MySQLDelete.BatchMultiDeleteSpec<C> batch(@Nullable C criteria) {
        final BatchDelete<C, Void> delete;
        delete = new BatchDelete<>(criteria);
        return delete;
    }

    static <C> MySQLDelete.WithMultiDeleteSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndDelete<>(criteria);
    }

    static <C> MySQLDelete.BatchWithMultiDeleteSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndDelete<>(criteria);
    }

    private List<Hint> hintList;

    private List<MySQLModifier> modifierList;

    private boolean usingSyntax;

    private List<String> tableAliasList;

    private IT noActionPartitionBlock;

    private MySQLMultiDelete(@Nullable C criteria) {
        super(CriteriaContexts.multiDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }


    @Override
    public final MultiDeleteFromClause<C, DR, DP> delete(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = _CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = _CollectionUtils.asUnmodifiableList(modifiers);
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final MultiDeleteFromClause<C, DR, DP> delete(List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final MultiDeleteFromClause<C, DR, DP> delete(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers
            , List<String> tableAliasList) {
        assert hintList != null;
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = _CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = _CollectionUtils.asUnmodifiableList(modifiers);
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAliasList = _CollectionUtils.asUnmodifiableList(tableAliasList);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final DR from(TableMeta<?> table, String alias) {
        if (_CollectionUtils.isEmpty(this.tableAliasList)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(table, alias));
        return (DR) this;
    }

    @Override
    public final DP from(TableMeta<?> table) {
        if (_CollectionUtils.isEmpty(this.tableAliasList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.createPartitionJoinSpec(table);
    }


    @Override
    public final <T extends TableItem> DR from(Supplier<T> supplier, String alias) {
        if (_CollectionUtils.isEmpty(this.tableAliasList)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(supplier.get(), alias));
        return (DR) this;
    }

    @Override
    public final <T extends TableItem> DR from(Function<C, T> function, String alias) {
        if (_CollectionUtils.isEmpty(this.tableAliasList)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(function.apply(this.criteria), alias));
        return (DR) this;
    }

    @Override
    public final DR using(TableMeta<?> table, String alias) {
        return this.from(table, alias);
    }

    @Override
    public final DP using(TableMeta<?> table) {
        return this.from(table);
    }

    @Override
    public final <T extends TableItem> DR using(Supplier<T> supplier, String alias) {
        return this.from(supplier, alias);
    }

    @Override
    public final <T extends TableItem> DR using(Function<C, T> function, String alias) {
        return this.from(function, alias);
    }

    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLModifier> modifierList() {
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


    abstract IT createNoActionPartitionBlock();

    abstract DP createPartitionJoinSpec(TableMeta<?> table);


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
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (_CollectionUtils.isEmpty(this.tableAliasList)) {
            throw new CriteriaException("tableAliasList must not empty in multi-table delete clause.");
        }
        this.noActionPartitionBlock = null;
        if (this instanceof BatchDelete) {
            if (_CollectionUtils.isEmpty(((BatchDelete<C, ?>) this).wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
        }

        if (this instanceof SimpleWithAndDelete) {
            final SimpleWithAndDelete<C> delete = (SimpleWithAndDelete<C>) this;
            if (delete.cteList == null) {
                delete.cteList = Collections.emptyList();
            }
        } else if (this instanceof BatchWithAndDelete) {
            final BatchWithAndDelete<C> delete = (BatchWithAndDelete<C>) this;
            if (delete.cteList == null) {
                delete.cteList = Collections.emptyList();
            }
        }

    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        if (this instanceof BatchDelete) {
            ((BatchDelete<C, ?>) this).wrapperList = null;
        }
        if (this instanceof SimpleWithAndDelete) {
            ((SimpleWithAndDelete<C>) this).cteList = null;
        } else if (this instanceof BatchWithAndDelete) {
            ((BatchWithAndDelete<C>) this).cteList = null;
        }

    }

    @Override
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
    }

    @Override
    final IT ifJointTableBeforeAs(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = this.createBlockBeforeAs(joinType, table);
        } else {
            IT noActionBlock = this.noActionPartitionBlock;
            if (noActionBlock == null) {
                noActionBlock = this.createNoActionPartitionBlock();
                this.noActionPartitionBlock = noActionBlock;
            }
            block = noActionBlock;
        }
        return block;
    }



    /*################################## blow inner class ##################################*/


    private static class SimpleDelete<C, WE> extends MySQLMultiDelete<
            C,
            WE,
            MySQLDelete.MultiJoinSpec<C>,
            MySQLDelete.MultiPartitionJoinSpec<C>,
            MySQLDelete.MultiOnSpec<C>,
            MySQLDelete.MultiPartitionOnSpec<C>,
            Delete.DeleteSpec,
            MySQLDelete.MultiWhereAndSpec<C>>
            implements MySQLDelete.MultiWhereAndSpec<C>, MySQLDelete.MultiJoinSpec<C>
            , MySQLDelete.MultiDeleteSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        final MultiPartitionJoinSpec<C> createPartitionJoinSpec(TableMeta<?> table) {
            return new SimplePartitionJoinSpec<>(table, this);
        }

        @Override
        final MultiOnSpec<C> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new SimpleOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        final MultiOnSpec<C> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return new SimpleOnBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        final MultiPartitionOnSpec<C> createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
            return new SimplePartitionOnBlock<>(joinType, table, this);
        }

        @Override
        final MultiOnSpec<C> createNoActionTableBlock() {
            return new NoActionOnBlock<>(this);
        }

        @Override
        final MultiOnSpec<C> createNoActionOnBlock() {
            return new NoActionOnBlock<>(this);
        }

        @Override
        final MultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new NoActionPartitionBlock<>(this);
        }


    }//SimpleDelete

    private static class BatchDelete<C, WE> extends MySQLMultiDelete<
            C,
            WE,
            MySQLDelete.BatchMultiJoinSpec<C>,
            MySQLDelete.BatchMultiPartitionJoinSpec<C>,
            MySQLDelete.BatchMultiOnSpec<C>,
            MySQLDelete.BatchMultiPartitionOnSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>,
            MySQLDelete.BatchMultiWhereAndSpec<C>>
            implements MySQLDelete.BatchMultiWhereAndSpec<C>, MySQLDelete.BatchMultiJoinSpec<C>, _BatchDml
            , MySQLDelete.BatchMultiDeleteSpec<C> {


        private List<?> wrapperList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final DeleteSpec paramList(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramList(beanList);
            return this;
        }

        @Override
        public final DeleteSpec paramList(Supplier<List<?>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public final DeleteSpec paramList(Function<C, List<?>> function) {
            return this.paramList(function.apply(this.criteria));
        }

        @Override
        public final DeleteSpec paramList(Function<String, Object> function, String keyName) {
            this.wrapperList = CriteriaUtils.paramList(function, keyName);
            return this;
        }

        @Override
        final BatchMultiPartitionJoinSpec<C> createPartitionJoinSpec(TableMeta<?> table) {
            return new BatchPartitionJoinSpec<>(table, this);
        }

        @Override
        final BatchMultiOnSpec<C> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new BatchOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        final BatchMultiOnSpec<C> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
            return new BatchOnBlock<>(joinType, tableItem, alias, this);
        }

        @Override
        final BatchMultiPartitionOnSpec<C> createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
            return new BatchPartitionBlock<>(joinType, table, this);
        }

        @Override
        final BatchMultiOnSpec<C> createNoActionTableBlock() {
            return new BatchNoActionOnBlock<>(this);
        }

        @Override
        final BatchMultiOnSpec<C> createNoActionOnBlock() {
            return new BatchNoActionOnBlock<>(this);
        }

        @Override
        final BatchMultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new BatchNoActionPartitionBlock<>(this);
        }

        @Override
        public final List<?> paramList() {
            prepared();
            return this.wrapperList;
        }


    }//SimpleDelete


    private static final class SimpleWithAndDelete<C> extends SimpleDelete<C, MySQLDelete.MultiDeleteSpec<C>>
            implements MySQLDelete.WithMultiDeleteSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleWithAndDelete(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            prepared();
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

    }//SimpleWithAndDelete


    private static final class BatchWithAndDelete<C> extends BatchDelete<C, MySQLDelete.BatchMultiDeleteSpec<C>>
            implements MySQLDelete.BatchWithMultiDeleteSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private BatchWithAndDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            prepared();
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

    }//BatchWithAndDelete


    private static final class FirstBlock extends TableBlock implements _MySQLTableBlock {

        private final String alias;

        private final List<String> partitionList;

        /**
         * @param partitionList a unmodified list
         */
        private FirstBlock(TableMeta<?> table, String alias, List<String> partitionList) {
            super(_JoinType.NONE, table);
            this.alias = alias;
            this.partitionList = partitionList;
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public List<_Predicate> predicates() {
            return Collections.emptyList();
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }// FirstBlock


    /**
     * @see SimpleDelete#createPartitionJoinSpec(TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C, WE> extends MySQLPartitionClause<C, MySQLDelete.MultiAsJoinSpec<C>>
            implements MySQLDelete.MultiAsJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C> {

        private final TableMeta<?> table;

        private final SimpleDelete<C, WE> delete;

        private SimplePartitionJoinSpec(TableMeta<?> table, SimpleDelete<C, WE> delete) {
            super(delete.criteria);
            this.table = table;
            this.delete = delete;
        }

        @Override
        public MultiJoinSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = TableBlock.firstBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            this.delete.criteriaContext.onFirstBlock(block);
            return this.delete;
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchDelete#createPartitionJoinSpec(TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C, WE>
            extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsJoinSpec<C>>
            implements MySQLDelete.BatchMultiAsJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C> {


        private final TableMeta<?> table;

        private final BatchDelete<C, WE> delete;

        private BatchPartitionJoinSpec(TableMeta<?> table, BatchDelete<C, WE> delete) {
            super(delete.criteria);
            this.table = table;
            this.delete = delete;
        }

        @Override
        public BatchMultiJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = TableBlock.firstBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            this.delete.criteriaContext.onFirstBlock(block);
            return this.delete;
        }
    }// BatchPartitionJoinSpec


    /**
     * @see SimpleDelete#createOnBlock(_JoinType, TableItem, String)
     * @see SimpleDelete#createTableBlock(_JoinType, TableMeta, String)
     */
    private static class SimpleOnBlock<C, WE> extends OnClauseTableBlock<C, MySQLDelete.MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private final SimpleDelete<C, WE> delete;

        private SimpleOnBlock(_JoinType joinType, TableItem tableItem, String alias, SimpleDelete<C, WE> delete) {
            super(joinType, tableItem, alias);
            this.delete = delete;
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.delete.criteriaContext;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
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
            implements MySQLDelete.MultiPartitionOnSpec<C>, MySQLDelete.MultiAsOnSpec<C> {

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
     * @see BatchDelete#createOnBlock(_JoinType, TableItem, String)
     */
    private static class BatchOnBlock<C, WE> extends OnClauseTableBlock<C, MySQLDelete.BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {


        private final BatchDelete<C, WE> delete;

        private BatchOnBlock(_JoinType joinType, TableItem tableItem, String alias, BatchDelete<C, WE> delete) {
            super(joinType, tableItem, alias);
            this.delete = delete;
        }

        @Override
        final CriteriaContext getCriteriaContext() {
            return this.delete.criteriaContext;
        }

        @Override
        final BatchMultiJoinSpec<C> endOnClause() {
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
            implements MySQLDelete.BatchMultiAsOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C> {

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
            implements MySQLDelete.MultiAsOnSpec<C>, MySQLDelete.MultiPartitionOnSpec<C> {

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
    private static final class NoActionOnBlock<C> extends NoActionOnClause<C, MySQLDelete.MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private NoActionOnBlock(MultiJoinSpec<C> stmt) {
            super(stmt);
        }

    }

    /**
     * @see BatchDelete#createNoActionTableBlock()
     * @see BatchDelete#createNoActionOnBlock()
     */
    private static final class BatchNoActionOnBlock<C> extends NoActionOnClause<C, MySQLDelete.BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {

        private BatchNoActionOnBlock(BatchMultiJoinSpec<C> stmt) {
            super(stmt);
        }

    }// NoActionBatchOnBlock

    /**
     * @see BatchDelete#createNoActionPartitionBlock()
     */
    private static final class BatchNoActionPartitionBlock<C>
            extends MySQLNoActionPartitionClause<C, BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C> {

        private final BatchMultiOnSpec<C> onSpec;

        private BatchNoActionPartitionBlock(BatchMultiJoinSpec<C> delete) {
            this.onSpec = new BatchNoActionOnBlock<>(delete);
        }

        @Override
        public BatchMultiOnSpec<C> as(String alias) {
            return this.onSpec;
        }

    }// BatchNoActionPartitionBlock


}
