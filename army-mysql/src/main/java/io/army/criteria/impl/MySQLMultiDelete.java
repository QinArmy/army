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

abstract class MySQLMultiDelete<C, DR, DP, JT, IT, WR, WA> extends MultiDelete<C, DR, JT, JT, WR, WA> implements _MySQLMultiDelete
        , MySQLDelete, MySQLQuery.MySQLJoinClause<C, JT, JT, IT>, MySQLDelete.MultiDeleteClause<C, DR, DP>
        , MySQLDelete.MultiDeleteFromClause<C, DR, DP>, MySQLDelete.MultiDeleteUsingClause<C, DR, DP> {


    static <C> MySQLDelete.MultiDeleteSpec<C> simple57(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> MySQLDelete.BatchMultiDeleteSpec<C> batch57(@Nullable C criteria) {
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
        super(CriteriaUtils.primaryContext(criteria));
        if (!(this instanceof _MySQLWithClause)) {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }
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
        return this.addFirstBlock(TableBlock.fromBlock(table, alias));
    }

    @Override
    public final DP from(TableMeta<?> table) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.createPartitionJoinSpec(table, this::addFirstBlock);
    }


    @Override
    public final <T extends TablePart> DR from(Supplier<T> supplier, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addFirstBlock(TableBlock.fromBlock(supplier.get(), alias));
    }

    @Override
    public final <T extends TablePart> DR from(Function<C, T> function, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addFirstBlock(TableBlock.fromBlock(function.apply(this.criteria), alias));
    }

    @Override
    public final DR using(TableMeta<?> table, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addFirstBlock(TableBlock.fromBlock(table, alias));
    }

    @Override
    public final DP using(TableMeta<?> table) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.createPartitionJoinSpec(table, this::addFirstBlock);
    }

    @Override
    public final <T extends TablePart> DR using(Supplier<T> supplier, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addFirstBlock(TableBlock.fromBlock(supplier.get(), alias));
    }

    @Override
    public final <T extends TablePart> DR using(Function<C, T> function, String alias) {
        if (CollectionUtils.isEmpty(this.tableList)) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addFirstBlock(TableBlock.fromBlock(function.apply(this.criteria), alias));
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
    public final JT straightJoin(TableMeta<?> table, String tableAlias) {
        return this.addTableBlock(JoinType.STRAIGHT_JOIN, table, tableAlias);
    }

    @Override
    public final JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String alias) {
        return this.ifAddTableBlock(predicate, JoinType.STRAIGHT_JOIN, table, alias);
    }

    @Override
    public final <T extends TablePart> JT straightJoin(Function<C, T> function, String alias) {
        return this.addOnBlock(JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JT ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifAddOnBlock(JoinType.STRAIGHT_JOIN, function.apply(this.criteria), alias);
    }

    @Override
    public final <T extends TablePart> JT straightJoin(Supplier<T> supplier, String alias) {
        return this.addOnBlock(JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final <T extends TablePart> JT ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddOnBlock(JoinType.STRAIGHT_JOIN, supplier.get(), alias);
    }

    @Override
    public final IT leftJoin(TableMeta<?> table) {
        return this.addPartitionBlock(JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddPartitionBlock(predicate, JoinType.LEFT_JOIN, table);
    }

    @Override
    public final IT join(TableMeta<?> table) {
        return this.addPartitionBlock(JoinType.JOIN, table);
    }

    @Override
    public final IT ifJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddPartitionBlock(predicate, JoinType.JOIN, table);
    }

    @Override
    public final IT rightJoin(TableMeta<?> table) {
        return this.addPartitionBlock(JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT ifRightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddPartitionBlock(predicate, JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final IT straightJoin(TableMeta<?> table) {
        return this.addPartitionBlock(JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddPartitionBlock(predicate, JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final IT fullJoin(TableMeta<?> table) {
        return this.addPartitionBlock(JoinType.FULL_JOIN, table);
    }

    @Override
    public final IT ifFullJoin(Predicate<C> predicate, TableMeta<?> table) {
        return this.ifAddPartitionBlock(predicate, JoinType.FULL_JOIN, table);
    }

    abstract IT createPartitionBlock(JoinType joinType, TableMeta<?> table);

    abstract IT createNoActionPartitionBlock();

    abstract DP createPartitionJoinSpec(TableMeta<?> table, Function<TableBlock, DR> function);


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

    private IT ifAddPartitionBlock(Predicate<C> predicate, JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = this.addPartitionBlock(joinType, table);
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

    private IT addPartitionBlock(JoinType joinType, TableMeta<?> table) {
        final IT block;
        block = createPartitionBlock(joinType, table);
        this.addOtherBlock((_TableBlock) block);
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
        final MultiPartitionJoinSpec<C> createPartitionJoinSpec(TableMeta<?> table
                , Function<TableBlock, MultiJoinSpec<C>> function) {
            return new SimplePartitionJoinSpec<>(table, function, this.criteria);
        }

        @Override
        final MultiOnSpec<C> createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new SimpleOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        final MultiOnSpec<C> createOnBlock(JoinType joinType, TablePart tablePart, String alias) {
            return new SimpleOnBlock<>(joinType, tablePart, alias, this);
        }

        @Override
        final MultiPartitionOnSpec<C> createPartitionBlock(JoinType joinType, TableMeta<?> table) {
            return new SimplePartitionBlock<>(joinType, table, this);
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
        public final DeleteSpec paramBeans(List<Object> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public final DeleteSpec paramBeans(Supplier<List<Object>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public final DeleteSpec paramBeans(Function<C, List<Object>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }

        @Override
        final BatchMultiPartitionJoinSpec<C> createPartitionJoinSpec(TableMeta<?> table
                , Function<TableBlock, BatchMultiJoinSpec<C>> function) {
            return new BatchPartitionJoinSpec<>(table, function, this.criteria);
        }

        @Override
        final BatchMultiOnSpec<C> createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new BatchOnBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        final BatchMultiOnSpec<C> createOnBlock(JoinType joinType, TablePart tablePart, String alias) {
            return new BatchOnBlock<>(joinType, tablePart, alias, this);
        }

        @Override
        final BatchMultiPartitionOnSpec<C> createPartitionBlock(JoinType joinType, TableMeta<?> table) {
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
            super(JoinType.NONE, table);
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
     * @see SimpleDelete#createPartitionJoinSpec(TableMeta, Function)
     */
    private static final class SimplePartitionJoinSpec<C> extends MySQLPartitionClause<C, MySQLDelete.MultiAsJoinSpec<C>>
            implements MySQLDelete.MultiAsJoinSpec<C>, MySQLDelete.MultiPartitionJoinSpec<C> {

        private final Function<TableBlock, MultiJoinSpec<C>> function;

        private final TableMeta<?> table;

        private SimplePartitionJoinSpec(TableMeta<?> table, Function<TableBlock, MultiJoinSpec<C>> function
                , @Nullable C criteria) {
            super(criteria);
            this.table = table;
            this.function = function;
        }

        @Override
        public MultiJoinSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = TableBlock.fromBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            return this.function.apply(block);
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchDelete#createPartitionJoinSpec(TableMeta, Function)
     */
    private static final class BatchPartitionJoinSpec<C>
            extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsJoinSpec<C>>
            implements MySQLDelete.BatchMultiAsJoinSpec<C>, MySQLDelete.BatchMultiPartitionJoinSpec<C> {


        private final TableMeta<?> table;

        private final Function<TableBlock, BatchMultiJoinSpec<C>> function;

        private BatchPartitionJoinSpec(TableMeta<?> table, Function<TableBlock, BatchMultiJoinSpec<C>> function
                , @Nullable C criteria) {
            super(criteria);
            this.function = function;
            this.table = table;
        }

        @Override
        public BatchMultiJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = TableBlock.fromBlock(this.table, alias);
            } else {
                block = new FirstBlock(this.table, alias, partitionList);
            }
            return this.function.apply(block);
        }
    }// BatchPartitionJoinSpec


    /**
     * @see SimpleDelete#addOnBlock(JoinType, TablePart, String)
     * @see SimpleDelete#addTableBlock(JoinType, TableMeta, String)
     */
    private static final class SimpleOnBlock<C> extends OnClauseTableBlock<C, MySQLDelete.MultiJoinSpec<C>>
            implements MySQLDelete.MultiOnSpec<C> {

        private final SimpleDelete<C> delete;

        private SimpleOnBlock(JoinType joinType, TablePart tablePart, String alias, SimpleDelete<C> delete) {
            super(joinType, tablePart, alias);
            this.delete = delete;
        }

        @Override
        C getCriteria() {
            return this.delete.criteria;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            return this.delete;
        }


    }// SimpleMultiOnBlock

    /**
     * @see SimpleDelete#addPartitionBlock(JoinType, TableMeta)
     */
    private static final class SimplePartitionBlock<C>
            extends MySQLPartitionClause<C, MySQLDelete.MultiAsOnSpec<C>>
            implements MySQLDelete.MultiPartitionOnSpec<C>, MySQLDelete.MultiAsOnSpec<C>, _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleDelete<C> delete;

        private SimpleOnBlock<C> onBlock;

        private SimplePartitionBlock(JoinType joinType, TableMeta<?> tablePart, SimpleDelete<C> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = tablePart;
            this.delete = delete;
        }

        @Override
        public MultiOnSpec<C> as(String alias) {
            if (this.onBlock != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            final SimpleOnBlock<C> onBlock;
            onBlock = new SimpleOnBlock<>(this.joinType, this.table, alias, this.delete);
            this.onBlock = onBlock;
            return onBlock;
        }

        @Override
        public TablePart table() {
            return this.table;
        }

        @Override
        public SQLModifier jointType() {
            return this.joinType;
        }

        @Override
        public String alias() {
            final SimpleOnBlock<C> onBlock;
            onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.alias;
        }

        @Override
        public List<_Predicate> predicates() {
            final SimpleOnBlock<C> onBlock;
            onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.predicates();
        }

        @Override
        public List<String> partitionList() {
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            return partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }// SimpleMultiPartitionBlock


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
     * @see BatchDelete#addTableBlock(JoinType, TableMeta, String)
     * @see BatchDelete#addOnBlock(JoinType, TablePart, String)
     */
    private static final class BatchOnBlock<C> extends OnClauseTableBlock<C, MySQLDelete.BatchMultiJoinSpec<C>>
            implements MySQLDelete.BatchMultiOnSpec<C> {


        private final BatchDelete<C> delete;

        private BatchOnBlock(JoinType joinType, TablePart tablePart, String alias, BatchDelete<C> delete) {
            super(joinType, tablePart, alias);
            this.delete = delete;
        }

        @Override
        C getCriteria() {
            return this.delete.criteria;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            return this.delete;
        }


    }// BatchMultiOnBlock

    /**
     * @see BatchDelete#addPartitionBlock(JoinType, TableMeta)
     */
    private static final class BatchPartitionBlock<C> extends MySQLPartitionClause<C, MySQLDelete.BatchMultiAsOnSpec<C>>
            implements MySQLDelete.BatchMultiAsOnSpec<C>, MySQLDelete.BatchMultiPartitionOnSpec<C>, _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final BatchDelete<C> delete;

        private BatchOnBlock<C> onBlock;

        private BatchPartitionBlock(JoinType joinType, TableMeta<?> table, BatchDelete<C> delete) {
            super(delete.criteria);
            this.joinType = joinType;
            this.table = table;
            this.delete = delete;
        }

        @Override
        public BatchMultiOnSpec<C> as(final String alias) {
            if (this.onBlock != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            final BatchOnBlock<C> onBlock;
            onBlock = new BatchOnBlock<>(this.joinType, this.table, alias, this.delete);
            this.onBlock = onBlock;
            return onBlock;
        }

        @Override
        public TablePart table() {
            return this.table;
        }

        @Override
        public String alias() {
            final BatchOnBlock<C> onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.alias;
        }

        @Override
        public SQLModifier jointType() {
            return this.joinType;
        }

        @Override
        public List<_Predicate> predicates() {
            final BatchOnBlock<C> onBlock = this.onBlock;
            assert onBlock != null;
            return onBlock.predicates();
        }

        @Override
        public List<String> partitionList() {
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            return partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }// BatchPartitionBlock


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
