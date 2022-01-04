package io.army.criteria.impl;

import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdate<C, WR, WA, SR, OR, LR, IR> extends SingleUpdate<C, WR, WA, SR>
        implements Query.OrderByClause<C, OR>, MySQLUpdate.LimitClause<C, LR>, MySQLUpdate, _MySQLSingleUpdate
        , MySQLUpdate.SingleIndexHintClause<C, IR> {


    static <C> MySQLUpdate.SingleUpdateSpec<C> single57(@Nullable C criteria) {
        return new SingleUpdateSpecImpl<>(criteria);
    }

    static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batch57(@Nullable C criteria) {
        return new BatchSingleUpdateSpecImpl<>(criteria);
    }

    private final TableBlock tableBlock;

    private List<SortPart> orderByList;

    private List<MySQLIndexHint> indexHintList;

    private long rowCount;

    private MySQLSingleUpdate(TableBlock tableBlock, @Nullable C criteria) {
        super(criteria);
        this.tableBlock = tableBlock;
    }

    @Override
    public final OR orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = Arrays.asList(sortPart1, sortPart2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(List<SortPart> sortPartList) {
        this.orderByList = new ArrayList<>(sortPartList);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    @Override
    public final OR orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    @Override
    public final OR ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> sortPartList;
        sortPartList = supplier.get();
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = new ArrayList<>(sortPartList);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> sortPartList;
        sortPartList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = new ArrayList<>(sortPartList);
        }
        return (OR) this;
    }

    @Override
    public final LR limit(long rowCount) {
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<Long> supplier) {
        this.rowCount = supplier.get();
        return (LR) this;
    }

    @Override
    public final LR limit(Function<C, Long> function) {
        this.rowCount = function.apply(this.criteria);
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<Long> supplier) {
        final Long rowCount;
        rowCount = supplier.get();
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<C, Long> function) {
        final Long rowCount;
        rowCount = function.apply(this.criteria);
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return (LR) this;
    }

    @Override
    public final IR useIndex(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexNames);
    }

    @Override
    public final IR ignoreIndex(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexNames);
    }

    @Override
    public final IR forceIndex(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexNames);
    }

    @Override
    public final IR useIndexForOrderBy(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, true, indexNames);
    }

    @Override
    public final IR ignoreIndexForOrderBy(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, true, indexNames);
    }

    @Override
    public final IR forceIndexForOrderBy(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, true, indexNames);
    }

    @Override
    public final IR ifUseIndex(Function<C, List<String>> function) {
        final List<String> indexNames;
        indexNames = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexNames)) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexNames);
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> indexNames;
        indexNames = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexNames)) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexNames);
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Function<C, List<String>> function) {
        final List<String> indexNames;
        indexNames = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexNames)) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexNames);
        }
        return (IR) this;
    }

    @Override
    public final IR ifUseIndexForOrderBy(Function<C, List<String>> function) {
        final List<String> indexNames;
        indexNames = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexNames)) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, true, indexNames);
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndexForOrderBy(Function<C, List<String>> function) {
        final List<String> indexNames;
        indexNames = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexNames)) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, true, indexNames);
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndexForOrderBy(Function<C, List<String>> function) {
        final List<String> indexNames;
        indexNames = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexNames)) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, true, indexNames);
        }
        return (IR) this;
    }

    @Override
    public final TableMeta<?> table() {
        return this.tableBlock.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableBlock.alias();
    }


    @Override
    public final List<? extends _IndexHint> indexHintList() {
        return this.indexHintList;
    }

    @Override
    public final List<Hint> hintList() {
        return this.tableBlock.hintList();
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.tableBlock.modifierList();
    }

    @Override
    public final List<SortPart> sortExpList() {
        return this.orderByList;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    private IR addIndexHint(MySQLIndexHint.Command command, final boolean orderBy, final List<String> indexNames) {
        if (indexNames.size() == 0) {
            throw new CriteriaException("index list must not empty.");
        }
        List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            this.indexHintList = indexHintList;
        }
        final MySQLIndexHint.Purpose purpose;
        if (orderBy) {
            purpose = MySQLIndexHint.Purpose.FOR_ORDER_BY;
        } else {
            purpose = null;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));
        return (IR) this;
    }


    private static final class SimpleUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate.OrderBySpec<C>, //WR
            MySQLUpdate.SingleWhereAndSpec<C>, //WA
            MySQLUpdate.SingleWhereSpec<C>,//SR
            MySQLUpdate.LimitSpec<C>, //OR
            Update.UpdateSpec,          //LR
            MySQLUpdate.SingleIndexHintSpec<C>> // IR
            implements MySQLUpdate.OrderBySpec<C>
            , MySQLUpdate.SingleWhereSpec<C>, MySQLUpdate.SingleWhereAndSpec<C>
            , MySQLUpdate.SingleIndexHintSpec<C> {

        private SimpleUpdate(TableBlock tableBlock, @Nullable C criteria) {
            super(tableBlock, criteria);
        }

    } // SimpleUpdate

    private static final class BatchUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate.BatchOrderBySpec<C>,  //WR
            MySQLUpdate.BatchSingleWhereAndSpec<C>, //WA
            MySQLUpdate.BatchSingleWhereSpec<C>,    //SR
            MySQLUpdate.BatchLimitSpec<C>,         //OR
            MySQLUpdate.BatchSingleParamSpec<C>,   //LR
            MySQLUpdate.BatchSingleIndexHintSpec<C>> // IR
            implements MySQLUpdate.BatchSingleWhereAndSpec<C>, MySQLUpdate.BatchSingleWhereSpec<C>
            , MySQLUpdate.BatchSingleIndexHintSpec<C> {

        private List<ReadWrapper> wrapperList;

        private BatchUpdate(TableBlock tableBlock, @Nullable C criteria) {
            super(tableBlock, criteria);
        }

        @Override
        public UpdateSpec paramMaps(List<Map<String, Object>> mapList) {
            final List<ReadWrapper> wrapperList = new ArrayList<>(mapList.size());
            for (Map<String, Object> map : mapList) {
                wrapperList.add(ObjectAccessorFactory.forReadonlyAccess(map));
            }
            this.wrapperList = wrapperList;
            return this;
        }

        @Override
        public UpdateSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public UpdateSpec paramBeans(List<Object> beanList) {
            final List<ReadWrapper> wrapperList = new ArrayList<>(beanList.size());
            for (Object bean : beanList) {
                wrapperList.add(ObjectAccessorFactory.forReadonlyAccess(bean));
            }
            this.wrapperList = wrapperList;
            return this;
        }

        @Override
        public UpdateSpec paramBeans(Supplier<List<Object>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public UpdateSpec paramBeans(Function<C, List<Object>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }

        @Override
        void onAsUpdate() {
            final List<ReadWrapper> wrapperList = this.wrapperList;
            if (CollectionUtils.isEmpty(wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
            this.wrapperList = Collections.unmodifiableList(wrapperList);
        }

    }//BatchUpdate


    private static final class SingleUpdateSpecImpl<C> implements MySQLUpdate.SingleUpdateSpec<C> {

        private final C criteria;

        private SingleUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public SinglePartitionSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifierList
                , TableMeta<? extends IDomain> table) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new SimplePartitionSpecImpl<>(new PartitionBlock(hintList, modifierList, table), this.criteria);
        }

        @Override
        public SingleIndexHintSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifierList
                , TableMeta<? extends IDomain> table, String tableAlias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new SimpleUpdate<>(new FullTableBlock(hintList, modifierList, table, tableAlias), this.criteria);
        }

        @Override
        public SinglePartitionSpec<C> update(TableMeta<? extends IDomain> table) {
            return new SimplePartitionSpecImpl<>(new NoAliasBlock(table), this.criteria);
        }

        @Override
        public SingleIndexHintSpec<C> update(TableMeta<? extends IDomain> table, String tableAlias) {
            return new SimpleUpdate<>(new AliasBlock(table, tableAlias), this.criteria);
        }


    }// SingleUpdateSpecImpl

    private static final class BatchSingleUpdateSpecImpl<C> implements MySQLUpdate.BatchSingleUpdateSpec<C> {

        private final C criteria;

        private BatchSingleUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public BatchSinglePartitionSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifierList
                , TableMeta<? extends IDomain> table) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new BatchPartitionSpecImpl<>(new PartitionBlock(hintList, modifierList, table), this.criteria);
        }

        @Override
        public BatchSingleIndexHintSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifierList
                , TableMeta<? extends IDomain> table, String tableAlias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new BatchUpdate<>(new FullTableBlock(hintList, modifierList, table, tableAlias), this.criteria);
        }

        @Override
        public BatchSinglePartitionSpec<C> update(TableMeta<? extends IDomain> table) {
            return new BatchPartitionSpecImpl<>(new NoAliasBlock(table), this.criteria);
        }

        @Override
        public BatchSingleIndexHintSpec<C> update(TableMeta<? extends IDomain> table, String tableAlias) {
            return new BatchUpdate<>(new AliasBlock(table, tableAlias), this.criteria);
        }

    }// BatchSingleUpdateSpecImpl


    private static abstract class PartitionClauseImpl<C, PR> implements MySQLQuery.PartitionClause<C, PR> {

        final C criteria;

        List<String> partitionList;

        PartitionClauseImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public final PR partition(String partitionName) {
            this.partitionList = Collections.singletonList(partitionName);
            return (PR) this;
        }

        @Override
        public final PR partition(String partitionName1, String partitionNam2) {
            this.partitionList = Arrays.asList(partitionName1, partitionNam2);
            return (PR) this;
        }

        @Override
        public final PR partition(List<String> partitionNameList) {
            if (partitionNameList.size() == 0) {
                throw new CriteriaException("partition list must not empty.");
            }
            this.partitionList = new ArrayList<>(partitionNameList);
            return (PR) this;
        }

        @Override
        public final PR partition(Supplier<List<String>> supplier) {
            return this.partition(supplier.get());
        }

        @Override
        public final PR partition(Function<C, List<String>> function) {
            return this.partition(function.apply(this.criteria));
        }

        @Override
        public final PR ifPartition(Supplier<List<String>> supplier) {
            final List<String> list;
            list = supplier.get();
            if (!CollectionUtils.isEmpty(list)) {
                this.partition(list);
            }
            return (PR) this;
        }

        @Override
        public final PR ifPartition(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.partition(list);
            }
            return (PR) this;
        }


    }// PartitionClauseImpl


    private static final class SimplePartitionSpecImpl<C> extends PartitionClauseImpl<C, MySQLUpdate.SingleAsSpec<C>>
            implements MySQLUpdate.SinglePartitionSpec<C>, MySQLUpdate.SingleAsSpec<C> {

        private final NoAliasBlock block;

        private SimplePartitionSpecImpl(NoAliasBlock block, @Nullable C criteria) {
            super(criteria);
            this.block = block;
        }

        @Override
        public SingleIndexHintSpec<C> as(final String tableAlias) {
            final NoAliasBlock block = this.block;
            final TableBlock tableBlock;
            if (block instanceof PartitionBlock) {
                tableBlock = new FullTableBlock((PartitionBlock) block, tableAlias);
            } else {
                tableBlock = new AliasBlock(block.table, tableAlias);
            }
            return new SimpleUpdate<>(tableBlock, this.criteria);
        }

    }//SimplePartitionSpecImpl

    private static final class BatchPartitionSpecImpl<C>
            extends PartitionClauseImpl<C, MySQLUpdate.BatchSingleAsSpec<C>>
            implements MySQLUpdate.BatchSinglePartitionSpec<C>, MySQLUpdate.BatchSingleAsSpec<C> {

        private final NoAliasBlock block;

        private BatchPartitionSpecImpl(NoAliasBlock block, @Nullable C criteria) {
            super(criteria);
            this.block = block;
        }

        @Override
        public BatchSingleIndexHintSpec<C> as(String tableAlias) {
            final NoAliasBlock block = this.block;
            final TableBlock tableBlock;
            if (block instanceof PartitionBlock) {
                tableBlock = new FullTableBlock((PartitionBlock) block, tableAlias);
            } else {
                tableBlock = new AliasBlock(block.table, tableAlias);
            }
            return new BatchUpdate<>(tableBlock, this.criteria);
        }
    }// BatchPartitionSpecImpl

    private static abstract class TableBlock {

        final TableMeta<?> table;

        TableBlock(TableMeta<?> table) {
            this.table = table;
        }

        abstract String alias();

        abstract List<Hint> hintList();

        abstract List<SQLModifier> modifierList();


    }

    private static final class AliasBlock extends TableBlock {

        private final String alias;

        private AliasBlock(TableMeta<?> table, String alias) {
            super(table);
            this.alias = alias;
        }

        @Override
        String alias() {
            return this.alias;
        }

        @Override
        List<Hint> hintList() {
            return Collections.emptyList();
        }

        @Override
        List<SQLModifier> modifierList() {
            return Collections.emptyList();
        }

    }

    private static class NoAliasBlock extends TableBlock {

        NoAliasBlock(TableMeta<?> table) {
            super(table);
        }

        @Override
        String alias() {
            throw new UnsupportedOperationException();
        }

        @Override
        List<Hint> hintList() {
            return Collections.emptyList();
        }

        @Override
        List<SQLModifier> modifierList() {
            return Collections.emptyList();
        }
    }

    private static final class PartitionBlock extends NoAliasBlock {

        final List<Hint> hintList;

        final List<SQLModifier> modifierList;

        PartitionBlock(List<Hint> hintList, List<SQLModifier> modifierList, TableMeta<?> table) {
            super(table);
            this.hintList = hintList;
            this.modifierList = modifierList;
        }

        @Override
        List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        List<SQLModifier> modifierList() {
            return this.modifierList;
        }

    }


    private static class FullTableBlock extends TableBlock {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifierList;

        private final String alias;

        private FullTableBlock(List<Hint> hintList, List<SQLModifier> modifierList, TableMeta<?> table, String alias) {
            super(table);
            this.alias = alias;
            this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            this.modifierList = CollectionUtils.asUnmodifiableList(modifierList);
        }

        private FullTableBlock(PartitionBlock block, String alias) {
            super(block.table);
            this.alias = alias;
            this.hintList = block.hintList;
            this.modifierList = block.modifierList;
        }

        @Override
        String alias() {
            return this.alias;
        }

        @Override
        List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        List<SQLModifier> modifierList() {
            return this.modifierList;
        }

    }


}
