package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MySQLMultiDelete<C, DR, DP, JT, IT, WR, WA> extends MultiDelete<C, JT, JT, WR, WA> implements _MySQLMultiDelete
        , MySQLDelete, MySQLQuery.MySQLJoinClause<C, JT, JT, IT>, MySQLDelete.MultiDeleteClause<C, DR, DP>
        , MySQLDelete.MultiDeleteFromClause<C, DR, DP>, MySQLDelete.MultiDeleteUsingClause<C, DR, DP> {


    static <C> MySQLDelete.MultiDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> MySQLDelete.BatchMultiDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }

    static <C> MySQLDelete.WithMultiDeleteSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndDelete<>(criteria);
    }

    static <C> MySQLDelete.BatchWithMultiDeleteSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndDelete<>(criteria);
    }

    private List<Hint> hintList;

    private List<SQLModifier> modifierList;

    private boolean usingSyntax;

    private List<TableMeta<?>> tableList;

    private IT noActionPartitionBlock;

    private MySQLMultiDelete(@Nullable C criteria) {
        super(CriteriaContexts.multiDeleteContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }


    @Override
    public final MultiDeleteFromClause<C, DR, DP> delete(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , List<TableMeta<?>> tableList) {
        final List<Hint> hintList;
        hintList = hints.get();
        assert hintList != null;
        if (this.hintList != null || this.modifierList != null || this.tableList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = hintList;
        this.modifierList = modifiers;
        this.tableList = CollectionUtils.asUnmodifiableList(tableList);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final MultiDeleteFromClause<C, DR, DP> delete(List<TableMeta<?>> tableList) {
        if (this.tableList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableList = CollectionUtils.asUnmodifiableList(tableList);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , List<TableMeta<?>> tableList) {
        final List<Hint> hintList;
        hintList = hints.get();
        assert hintList != null;
        if (this.hintList != null || this.modifierList != null || this.tableList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = hintList;
        this.modifierList = modifiers;
        this.tableList = CollectionUtils.asUnmodifiableList(tableList);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final MultiDeleteUsingClause<C, DR, DP> deleteFrom(List<TableMeta<?>> tableList) {
        if (this.tableList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableList = CollectionUtils.asUnmodifiableList(tableList);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final DR from(TableMeta<?> table, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(table, alias));
        return (DR) this;
    }

    @Override
    public final DP from(TableMeta<?> table) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.createPartitionJoinSpec(table);
    }


    @Override
    public final <T extends TableItem> DR from(Supplier<T> supplier, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(supplier.get(), alias));
        return (DR) this;
    }

    @Override
    public final <T extends TableItem> DR from(Function<C, T> function, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
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
    public final List<SQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final boolean usingSyntax() {
        return this.usingSyntax;
    }

    @Override
    public final List<TableMeta<?>> tableList() {
        return this.tableList;
    }

    @Override
    public final IT straightJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        final JT block;
        block = this.createTableBlock(_JoinType.STRAIGHT_JOIN, table, tableAlias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JT straightJoin(Function<C, T> function, String alias) {
        final JT block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final <T extends TableItem> JT straightJoin(Supplier<T> supplier, String alias) {
        final JT block;
        block = this.createOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
        this.criteriaContext.onAddBlock((_TableBlock) block);
        return block;
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.ifAddTableBlock(predicate, _JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TableItem> JT ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(_JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> JT ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(_JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT leftJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT join(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.JOIN, table);
    }

    @Override
    public final IT ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.JOIN, table);
    }

    @Override
    public final IT rightJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.RIGHT_JOIN, table);
    }


    @Override
    public final IT fullJoin(TableMeta<?> table) {
        return this.createPartitionOnBlock(_JoinType.FULL_JOIN, table);
    }

    @Override
    public final IT ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifCreatePartitionOnBlock(predicate, _JoinType.FULL_JOIN, table);
    }

    abstract IT createPartitionOnBlock(_JoinType joinType, TableMeta<?> table);

    abstract IT createNoActionPartitionBlock();

    abstract DP createPartitionJoinSpec(TableMeta<?> table);


    @Override
    final void onAsDelete() {
        if (CollectionUtils.isEmpty(this.hintList)) {
            this.hintList = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(this.modifierList)) {
            this.modifierList = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw new CriteriaException("tableList must not empty in multi-table delete clause.");
        }
        this.noActionPartitionBlock = null;
        if (this instanceof BatchDelete) {
            if (CollectionUtils.isEmpty(((BatchDelete<C>) this).wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
        }

    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.tableList = null;
        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).wrapperList = null;
        }
    }

    private IT ifCreatePartitionOnBlock(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = this.createPartitionOnBlock(joinType, table);
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


    private static class SimpleDelete<C> extends MySQLMultiDelete<
            C,
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
        final MultiPartitionOnSpec<C> createPartitionOnBlock(_JoinType joinType, TableMeta<?> table) {
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

    private static class BatchDelete<C> extends MySQLMultiDelete<
            C,
            MySQLDelete.BatchMultiJoinSpec<C>,
            MySQLDelete.BatchMultiPartitionJoinSpec<C>,
            MySQLDelete.BatchMultiOnSpec<C>,
            MySQLDelete.BatchMultiPartitionOnSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>,
            MySQLDelete.BatchMultiWhereAndSpec<C>>
            implements MySQLDelete.BatchMultiWhereAndSpec<C>, MySQLDelete.BatchMultiJoinSpec<C>, _BatchDml
            , MySQLDelete.BatchMultiDeleteSpec<C> {


        private List<ReadWrapper> wrapperList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final DeleteSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public final DeleteSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public final DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public final DeleteSpec paramBeans(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public final DeleteSpec paramBeans(Supplier<List<?>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public final DeleteSpec paramBeans(Function<C, List<?>> function) {
            return this.paramBeans(function.apply(this.criteria));
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
        final BatchMultiPartitionOnSpec<C> createPartitionOnBlock(_JoinType joinType, TableMeta<?> table) {
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
        public final List<ReadWrapper> wrapperList() {
            prepared();
            return this.wrapperList;
        }


    }//SimpleDelete


    private static final class SimpleWithAndDelete<C> extends SimpleDelete<C>
            implements MySQLDelete.WithMultiDeleteSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleWithAndDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public MultiDeleteSpec<C> with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return this;
        }

        @Override
        public MultiDeleteSpec<C> with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return this;
        }

        @Override
        public MultiDeleteSpec<C> with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return this;
        }

        @Override
        public MultiDeleteSpec<C> with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return this;
        }

        @Override
        public MultiDeleteSpec<C> withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public MultiDeleteSpec<C> withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public MultiDeleteSpec<C> withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public MultiDeleteSpec<C> withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
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

    }//SimpleWithAndDelete


    private static final class BatchWithAndDelete<C> extends BatchDelete<C>
            implements MySQLDelete.BatchWithMultiDeleteSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private BatchWithAndDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public BatchMultiDeleteSpec<C> with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return this;
        }

        @Override
        public BatchMultiDeleteSpec<C> with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return this;
        }

        @Override
        public BatchMultiDeleteSpec<C> with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return this;
        }

        @Override
        public BatchMultiDeleteSpec<C> with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return this;
        }

        @Override
        public BatchMultiDeleteSpec<C> withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public BatchMultiDeleteSpec<C> withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public BatchMultiDeleteSpec<C> withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public BatchMultiDeleteSpec<C> withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
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
    }//BatchWithAndDelete


    private static final class FirstBlock extends TableBlock implements _MySQLTableBlock {

        private final String alias;

        private final List<String> partitionList;

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
    private static final class SimplePartitionJoinSpec<C> extends MySQLPartitionClause<C, MySQLDelete.MultiAsJoinSpec<C>>
            implements MySQLDelete.MultiAsJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C> {

        private final TableMeta<?> table;

        private final SimpleDelete<C> delete;

        private SimplePartitionJoinSpec(TableMeta<?> table, SimpleDelete<C> delete) {
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
    private static final class BatchPartitionJoinSpec<C>
            extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsJoinSpec<C>>
            implements MySQLDelete.BatchMultiAsJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C> {


        private final TableMeta<?> table;

        private final BatchDelete<C> delete;

        private BatchPartitionJoinSpec(TableMeta<?> table, BatchDelete<C> delete) {
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
    private static class SimpleOnBlock<C> extends OnClauseTableBlock<C, MySQLDelete.MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private final SimpleDelete<C> delete;

        private SimpleOnBlock(_JoinType joinType, TableItem tableItem, String alias, SimpleDelete<C> delete) {
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
    private static final class SimpleOnBlockWithPartition<C> extends SimpleOnBlock<C> implements _MySQLTableBlock {

        private final List<String> partitionList;

        private SimpleOnBlockWithPartition(_JoinType joinType, TableItem tableItem
                , String alias, SimpleDelete<C> delete, List<String> partitionList) {
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
     * @see SimpleDelete#createPartitionOnBlock(_JoinType, TableMeta)
     */
    private static final class SimplePartitionOnBlock<C>
            extends MySQLPartitionClause<C, MySQLDelete.MultiAsOnSpec<C>>
            implements MySQLDelete.MultiPartitionOnSpec<C>, MySQLDelete.MultiAsOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleDelete<C> delete;

        private SimplePartitionOnBlock(_JoinType joinType, TableMeta<?> tablePart, SimpleDelete<C> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = tablePart;
            this.delete = delete;
        }

        @Override
        public MultiOnSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final SimpleOnBlock<C> onBlock;
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
    private static class BatchOnBlock<C> extends OnClauseTableBlock<C, MySQLDelete.BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {


        private final BatchDelete<C> delete;

        private BatchOnBlock(_JoinType joinType, TableItem tableItem, String alias, BatchDelete<C> delete) {
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

    private static final class BatchOnBlockWithPartition<C> extends BatchOnBlock<C> implements _MySQLTableBlock {

        private final List<String> partitionList;

        private BatchOnBlockWithPartition(_JoinType joinType, TableItem tableItem, String alias
                , BatchDelete<C> delete, List<String> partitionList) {
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
     * @see BatchDelete#createPartitionOnBlock(_JoinType, TableMeta)
     */
    private static final class BatchPartitionBlock<C> extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchDelete<C> delete;

        private BatchPartitionBlock(_JoinType joinType, TableMeta<?> table, BatchDelete<C> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = table;
            this.delete = delete;
        }

        @Override
        public BatchMultiOnSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);

            final List<String> tempList = this.partitionList;
            final BatchOnBlock<C> onBlock;
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
    private static final class NoActionPartitionBlock<C>
            extends MySQLNoActionPartitionClause<C, MultiAsOnSpec<C>>
            implements MySQLDelete.MultiAsOnSpec<C>, MySQLDelete.MultiPartitionOnSpec<C> {

        private final MultiOnSpec<C> onSpec;

        private NoActionPartitionBlock(SimpleDelete<C> delete) {
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
