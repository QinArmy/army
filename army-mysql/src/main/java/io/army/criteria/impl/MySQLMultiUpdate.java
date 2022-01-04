package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLMultiUpdate.SimpleMultiUpdate} ,MySQL multi update api implementation</li>
 *         <li>{@link MySQLMultiUpdate.BatchMultiUpdate} ,MySQL batch multi update api implementation</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiUpdate<C, JT, JS, WR, WA, SR, IR, IT> extends MultiUpdate<C, JT, JS, WR, WA, SR>
        implements MySQLUpdate.MultiIndexHintClause<C, IR>, MySQLUpdate, _MySQLMultiUpdate
        , MySQLQuery.MySQLJoinClause<C, JT, JS, IT> {


    static <C> MySQLUpdate.MultiUpdateSpec<C> simple57(@Nullable C criteria) {
        return new MultiUpdateSpecImpl<>(criteria);
    }

    static <C> MySQLUpdate.BatchMultiUpdateSpec<C> batch57(@Nullable C criteria) {
        return new BatchMultiUpdateSpecImpl<>(criteria);
    }


    private final FirstBlock firstBlock;

    List<_TableBlock> tableBlockList = new ArrayList<>();

    private IT noActionPartitionBlock;

    private MySQLMultiUpdate(final FirstBlock firstBlock, @Nullable C criteria) {
        super(criteria);
        this.firstBlock = firstBlock;
        this.tableBlockList.add(firstBlock);
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
    public final IR useIndexForJoin(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, true, indexNames);
    }

    @Override
    public final IR ignoreIndexForJoin(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, true, indexNames);
    }

    @Override
    public final IR forceIndexForJoin(List<String> indexNames) {
        return this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, true, indexNames);
    }

    @Override
    public final IR ifUseIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, list);
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, list);
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, list);
        }
        return (IR) this;
    }

    @Override
    public final IR ifUseIndexForJoin(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, true, list);
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndexForJoin(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, true, list);
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndexForJoin(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(list)) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, true, list);
        }
        return (IR) this;
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
    public final <T extends TablePart> JS straightJoin(Function<C, T> function, String alias) {
        return this.innerAddTablePartBlock(JoinType.STRAIGHT_JOIN, function, alias);
    }

    @Override
    public final <T extends TablePart> JS ifStraightJoin(Function<C, T> function, String alias) {
        return this.ifAddTablePartBlock(JoinType.STRAIGHT_JOIN, function, alias);
    }

    @Override
    public final IT ifLeftJoin(TableMeta<?> table) {
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

    @Override
    public final List<? extends _TableBlock> tableBlockList() {
        prepared();
        return this.tableBlockList;
    }

    @Override
    public final List<Hint> hintList() {
        return this.firstBlock.hintList;
    }

    @Override
    public final List<SQLModifier> modifierList() {
        return this.firstBlock.modifierList;
    }


    /*################################## blow package template method ##################################*/


    abstract IT createNoActionPartitionBlock();

    abstract IT addPartitionBlock(JoinType joinType, TableMeta<?> table);

    @Override
    final void onAsUpdate() {
        final List<MySQLIndexHint> indexHintList = this.firstBlock.indexHintList;
        if (CollectionUtils.isEmpty(indexHintList)) {
            this.firstBlock.indexHintList = Collections.emptyList();
        } else {
            this.firstBlock.indexHintList = Collections.unmodifiableList(indexHintList);
        }
        this.tableBlockList = CollectionUtils.unmodifiableList(this.tableBlockList);

        if (this instanceof BatchMultiUpdate) {
            final List<ReadWrapper> wrapperList = ((BatchMultiUpdate<C>) this).wrapperList;
            if (CollectionUtils.isEmpty(wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
            ((BatchMultiUpdate<C>) this).wrapperList = Collections.unmodifiableList(wrapperList);
        }


    }

    @Override
    final void onClear() {
        this.firstBlock.indexHintList = null;
        this.tableBlockList = null;
        if (this instanceof BatchMultiUpdate) {
            ((BatchMultiUpdate<C>) this).wrapperList = null;
        }
    }

    /*################################## blow private method ##################################*/

    private IT ifAddPartitionBlock(Predicate<C> predicate, JoinType joinType, TableMeta<?> table) {
        final IT block;
        if (predicate.test(this.criteria)) {
            block = addPartitionBlock(joinType, table);
        } else {
            IT noActionPartitionBlock = this.noActionPartitionBlock;
            if (noActionPartitionBlock == null) {
                noActionPartitionBlock = createNoActionPartitionBlock();
                this.noActionPartitionBlock = noActionPartitionBlock;
            }
            block = noActionPartitionBlock;
        }
        return block;
    }


    private IR addIndexHint(final MySQLIndexHint.Command command, final boolean forJoin, final List<String> indexNames) {
        final List<_TableBlock> tableBlockList = this.tableBlockList;
        if (tableBlockList == null
                || tableBlockList.size() != 1) {
            throw _Exceptions.castCriteriaApi();
        }
        if (CollectionUtils.isEmpty(indexNames)) {
            throw new CriteriaException("index hint clause index name list must not emtpy.");
        }

        List<MySQLIndexHint> indexHintList = this.firstBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            this.firstBlock.indexHintList = indexHintList;
        }
        final MySQLIndexHint.Purpose purpose;
        if (forJoin) {
            purpose = MySQLIndexHint.Purpose.FOR_JOIN;
        } else {
            purpose = null;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, new ArrayList<>(indexNames)));
        return (IR) this;
    }


    /*################################## blow inner class  ##################################*/

    /**
     * @see #simple57(Object)
     */
    private static final class MultiUpdateSpecImpl<C> implements MySQLUpdate.MultiUpdateSpec<C> {

        private final C criteria;

        private MultiUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public MultiPartitionJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new SimplePartitionJoinSpec<>(hintList, modifiers, table, this.criteria);
        }

        @Override
        public MultiIndexHintJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table, String tableAlias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            final FirstBlock block = new FirstBlock(hintList, modifiers, table, tableAlias);
            return new SimpleMultiUpdate<>(block, this.criteria);
        }

        @Override
        public MultiPartitionJoinSpec<C> update(TableMeta<? extends IDomain> table) {
            return new SimplePartitionJoinSpec<>(table, this.criteria);
        }

        @Override
        public MultiIndexHintJoinSpec<C> update(TableMeta<? extends IDomain> table, String tableAlias) {
            return new SimpleMultiUpdate<>(new FirstBlock(table, tableAlias), this.criteria);
        }

        @Override
        public <T extends TablePart> MultiJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , Supplier<T> supplier, String alias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;

            final TablePart tp;
            tp = supplier.get();
            assert tp != null;

            final FirstBlock block = new FirstBlock(hintList, modifiers, tp, alias);
            return new SimpleMultiUpdate<>(block, this.criteria);
        }

        @Override
        public <T extends TablePart> MultiJoinSpec<C> update(Supplier<T> tablePart, String alias) {
            final TablePart tp = tablePart.get();
            assert tp != null;
            return new SimpleMultiUpdate<>(new FirstBlock(tp, alias), this.criteria);
        }

        @Override
        public <T extends TablePart> MultiJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , Function<C, T> tablePart, String alias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;

            final TablePart tp;
            tp = tablePart.apply(this.criteria);
            assert tp != null;
            final FirstBlock block = new FirstBlock(hintList, modifiers, tp, alias);
            return new SimpleMultiUpdate<>(block, this.criteria);
        }

        @Override
        public <T extends TablePart> MultiJoinSpec<C> update(Function<C, T> tablePart, String alias) {
            final TablePart tp;
            tp = tablePart.apply(this.criteria);
            assert tp != null;
            return new SimpleMultiUpdate<>(new FirstBlock(tp, alias), this.criteria);
        }

    }//MultiUpdateSpecImpl

    /**
     * @see #batch57(Object)
     */
    private static final class BatchMultiUpdateSpecImpl<C> implements MySQLUpdate.BatchMultiUpdateSpec<C> {

        private final C criteria;

        private BatchMultiUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public BatchMultiPartitionJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            return new BatchPartitionJoinSpec<>(hintList, modifiers, table, this.criteria);
        }

        @Override
        public BatchMultiIndexHintJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , TableMeta<? extends IDomain> table, String tableAlias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;
            final FirstBlock block = new FirstBlock(hintList, modifiers, table, tableAlias);
            return new BatchMultiUpdate<>(block, this.criteria);
        }

        @Override
        public BatchMultiPartitionJoinSpec<C> update(TableMeta<? extends IDomain> table) {
            return new BatchPartitionJoinSpec<>(table, this.criteria);
        }

        @Override
        public BatchMultiIndexHintJoinSpec<C> update(TableMeta<? extends IDomain> table, String tableAlias) {
            return new BatchMultiUpdate<>(new FirstBlock(table, tableAlias), this.criteria);
        }

        @Override
        public <T extends TablePart> BatchMultiJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , Supplier<T> supplier, String alias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;

            final TablePart tp;
            tp = supplier.get();
            assert tp != null;

            final FirstBlock block = new FirstBlock(hintList, modifiers, tp, alias);
            return new BatchMultiUpdate<>(block, this.criteria);
        }

        @Override
        public <T extends TablePart> BatchMultiJoinSpec<C> update(Supplier<T> tablePart, String alias) {
            final TablePart tp = tablePart.get();
            assert tp != null;
            return new BatchMultiUpdate<>(new FirstBlock(tp, alias), this.criteria);
        }

        @Override
        public <T extends TablePart> BatchMultiJoinSpec<C> update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
                , Function<C, T> tablePart, String alias) {
            final List<Hint> hintList;
            hintList = hints.get();
            assert hintList != null;

            final TablePart tp;
            tp = tablePart.apply(this.criteria);
            assert tp != null;
            final FirstBlock block = new FirstBlock(hintList, modifiers, tp, alias);
            return new BatchMultiUpdate<>(block, this.criteria);
        }

        @Override
        public <T extends TablePart> BatchMultiJoinSpec<C> update(Function<C, T> tablePart, String alias) {
            final TablePart tp = tablePart.apply(this.criteria);
            assert tp != null;
            return new BatchMultiUpdate<>(new FirstBlock(tp, alias), this.criteria);
        }

    }//BatchMultiUpdateSpecImpl

    private static final class FirstBlock extends TableBlock implements _MySQLTableBlock {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifierList;

        private final String alias;

        private final List<String> partitionList;

        private List<MySQLIndexHint> indexHintList;


        private FirstBlock(TablePart tablePart, String alias) {
            super(tablePart, JoinType.NONE);
            this.alias = alias;
            this.hintList = Collections.emptyList();
            this.modifierList = Collections.emptyList();
            this.partitionList = Collections.emptyList();
        }

        private FirstBlock(List<Hint> hintList, List<SQLModifier> modifierList, TablePart tablePart
                , String alias) {
            super(tablePart, JoinType.NONE);
            this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            this.modifierList = CollectionUtils.asUnmodifiableList(modifierList);
            this.alias = alias;
            this.partitionList = Collections.emptyList();
        }


        private FirstBlock(List<Hint> hintList, List<SQLModifier> modifierList, TablePart tablePart
                , String alias, List<String> partitionList) {
            super(tablePart, JoinType.NONE);
            this.hintList = hintList;
            this.modifierList = modifierList;
            this.alias = alias;

            if (partitionList == Collections.EMPTY_LIST) {
                this.partitionList = partitionList;
            } else {
                this.partitionList = CollectionUtils.asUnmodifiableList(partitionList);
            }
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
            List<MySQLIndexHint> indexHintList = this.indexHintList;
            assert indexHintList != null;
            return indexHintList;
        }


    }//


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class SimpleMultiUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiOnSpec<C>,
            Update.UpdateSpec,
            MySQLUpdate.MultiWhereAndSpec<C>,
            MySQLUpdate.MultiWhereSpec<C>,
            MySQLUpdate.MultiIndexHintJoinSpec<C>,
            MySQLUpdate.MultiPartitionOnSpec<C>>
            implements MySQLUpdate.MultiWhereAndSpec<C>, MySQLUpdate.MultiWhereSpec<C>
            , MySQLUpdate.MultiIndexHintJoinSpec<C> {

        private SimpleMultiUpdate(FirstBlock firstBlock, @Nullable C criteria) {
            super(firstBlock, criteria);
        }

        @Override
        MultiIndexHintOnSpec<C> addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            final SimpleTableBlock<C> block = new SimpleTableBlock<>(joinType, table, tableAlias, this);
            this.tableBlockList.add(block);
            return block;
        }

        @Override
        MultiOnSpec<C> addTablePartBlock(JoinType joinType, TablePart tablePart, String alias) {
            final SimpleOnBlock<C> block = new SimpleOnBlock<>(joinType, tablePart, alias, this);
            this.tableBlockList.add(block);
            return block;
        }

        @Override
        MultiPartitionOnSpec<C> addPartitionBlock(JoinType joinType, TableMeta<?> table) {
            final SimplePartitionBlock<C> block = new SimplePartitionBlock<>(joinType, table, this);
            this.tableBlockList.add(block);
            return block;
        }

        @Override
        MultiIndexHintOnSpec<C> createNoActionTableBlock() {
            return new SimpleNoActionIndexHintOnSpec<>(this);
        }

        @Override
        MultiOnSpec<C> createNoActionTablePartBlock() {
            return new SimpleNoActionOnSpec<>(this);
        }

        @Override
        MultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new SimpleNoActionPartitionOnSpec<>(this);
        }


    }// SimpleMultiUpdate

    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     */
    private static final class BatchMultiUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiOnSpec<C>,
            MySQLUpdate.BatchMultiParamSpec<C>,
            MySQLUpdate.BatchMultiWhereAndSpec<C>,
            MySQLUpdate.BatchMultiWhereSpec<C>,
            MySQLUpdate.BatchMultiIndexHintJoinSpec<C>,
            MySQLUpdate.BatchMultiPartitionOnSpec<C>>
            implements MySQLUpdate.BatchMultiWhereAndSpec<C>, MySQLUpdate.BatchMultiWhereSpec<C>
            , MySQLUpdate.BatchMultiIndexHintJoinSpec<C> {

        private List<ReadWrapper> wrapperList;

        private BatchMultiUpdate(FirstBlock firstBlock, @Nullable C criteria) {
            super(firstBlock, criteria);
        }

        @Override
        public UpdateSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
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
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
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
        BatchMultiIndexHintOnSpec<C> addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            final BatchTableBlock<C> block = new BatchTableBlock<>(joinType, table, tableAlias, this);
            this.tableBlockList.add(block);
            return block;
        }

        @Override
        BatchMultiOnSpec<C> addTablePartBlock(JoinType joinType, TablePart tablePart, String alias) {
            final BatchOnBlock<C> block = new BatchOnBlock<>(joinType, tablePart, alias, this);
            this.tableBlockList.add(block);
            return block;
        }

        @Override
        BatchMultiPartitionOnSpec<C> addPartitionBlock(JoinType joinType, TableMeta<?> table) {
            final BatchPartitionBlock<C> block = new BatchPartitionBlock<>(joinType, table, this);
            this.tableBlockList.add(block);
            return block;
        }

        @Override
        BatchMultiIndexHintOnSpec<C> createNoActionTableBlock() {
            return new BatchNoActionIndexHintOnSpec<>(this);
        }

        @Override
        BatchMultiOnSpec<C> createNoActionTablePartBlock() {
            return new BatchNoActionOnSpec<>(this);
        }

        @Override
        BatchMultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new BatchNoActionPartitionOnSpec<>(this);
        }


    }// BatchMultiUpdate


    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link SimplePartitionJoinSpec}</li>
     *         <li>{@link BatchPartitionJoinSpec}</li>
     *     </ul>
     * </p>
     */
    private static abstract class PartitionJoinClause<C, PR, AR> implements MySQLQuery.PartitionClause<C, PR>
            , Statement.AsClause<AR> {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifies;

        private final TableMeta<?> table;

        final C criteria;

        private List<String> partitionList;

        private PartitionJoinClause(List<Hint> hintList, List<SQLModifier> modifies, TableMeta<?> table
                , @Nullable C criteria) {
            if (hintList == Collections.EMPTY_LIST) {
                this.hintList = hintList;
            } else {
                this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            }
            if (modifies == Collections.EMPTY_LIST) {
                this.modifies = modifies;
            } else {
                this.modifies = CollectionUtils.asUnmodifiableList(modifies);
            }
            this.table = table;
            this.criteria = criteria;
        }

        private PartitionJoinClause(TableMeta<?> table, @Nullable C criteria) {
            this.table = table;
            this.criteria = criteria;
            this.hintList = Collections.emptyList();
            this.modifies = Collections.emptyList();
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
            if (CollectionUtils.isEmpty(list)) {
                this.partition(list);
            }
            return (PR) this;
        }

        @Override
        public final PR ifPartition(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (CollectionUtils.isEmpty(list)) {
                this.partition(list);
            }
            return (PR) this;
        }

        @Override
        public final AR as(String alias) {
            Objects.requireNonNull(alias);
            List<String> partitionList = this.partitionList;
            if (CollectionUtils.isEmpty(partitionList)) {
                partitionList = Collections.emptyList();
            }
            return doAs(new FirstBlock(this.hintList, this.modifies, this.table, alias, partitionList));
        }

        abstract AR doAs(FirstBlock block);

    }// PartitionJoinClause


    /**
     * @see MultiUpdateSpecImpl#update(TableMeta)
     * @see MultiUpdateSpecImpl#update(Supplier, List, TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C>
            extends PartitionJoinClause<C, MultiAsJoinSpec<C>, MultiIndexHintJoinSpec<C>>
            implements MultiAsJoinSpec<C>, MultiPartitionJoinSpec<C> {

        private SimplePartitionJoinSpec(List<Hint> hintList, List<SQLModifier> modifies
                , TableMeta<?> table, @Nullable C criteria) {
            super(hintList, modifies, table, criteria);
        }


        private SimplePartitionJoinSpec(TableMeta<?> table, @Nullable C criteria) {
            super(table, criteria);
        }

        @Override
        MultiIndexHintJoinSpec<C> doAs(FirstBlock block) {
            return new SimpleMultiUpdate<>(block, this.criteria);
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchMultiUpdateSpecImpl#update(TableMeta)
     * @see BatchMultiUpdateSpecImpl#update(Supplier, List, TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C>
            extends PartitionJoinClause<C, BatchMultiAsJoinSpec<C>, BatchMultiIndexHintJoinSpec<C>>
            implements BatchMultiAsJoinSpec<C>, BatchMultiPartitionJoinSpec<C> {

        private BatchPartitionJoinSpec(List<Hint> hintList, List<SQLModifier> modifies
                , TableMeta<?> table, @Nullable C criteria) {
            super(hintList, modifies, table, criteria);
        }

        private BatchPartitionJoinSpec(TableMeta<?> table, @Nullable C criteria) {
            super(table, criteria);
        }

        @Override
        BatchMultiIndexHintJoinSpec<C> doAs(FirstBlock block) {
            return new BatchMultiUpdate<>(block, this.criteria);
        }


    }// BatchPartitionJoinSpec


    private static abstract class IndexHintClause<C, IR, OR> extends OnClauseTableBlock<C, OR> implements
            MySQLUpdate.MultiIndexHintClause<C, IR>, _MySQLTableBlock {

        List<MySQLIndexHint> indexHintList;

        IndexHintClause(JoinType joinType, TablePart tablePart) {
            super(joinType, tablePart);
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
        public final IR useIndexForJoin(List<String> indexNames) {
            return this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, true, indexNames);
        }

        @Override
        public final IR ignoreIndexForJoin(List<String> indexNames) {
            return this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, true, indexNames);
        }

        @Override
        public final IR forceIndexForJoin(List<String> indexNames) {
            return this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, true, indexNames);
        }

        @Override
        public final IR ifUseIndex(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, list);
            }
            return (IR) this;
        }

        @Override
        public final IR ifIgnoreIndex(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, list);
            }
            return (IR) this;
        }

        @Override
        public final IR ifForceIndex(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, list);
            }
            return (IR) this;
        }

        @Override
        public final IR ifUseIndexForJoin(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, true, list);
            }
            return (IR) this;
        }

        @Override
        public final IR ifIgnoreIndexForJoin(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, true, list);
            }
            return (IR) this;
        }

        @Override
        public final IR ifForceIndexForJoin(Function<C, List<String>> function) {
            final List<String> list;
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, true, list);
            }
            return (IR) this;
        }

        @Override
        public final List<? extends _IndexHint> indexHintList() {
            final List<MySQLIndexHint> indexHintList = this.indexHintList;
            assert indexHintList != null;
            return indexHintList;
        }

        boolean aliasIsNuLL() {
            return false;
        }


        private IR addIndexHint(final MySQLIndexHint.Command command, final boolean forJoin
                , final List<String> indexNames) {
            if (this instanceof PartitionBlock && this.aliasIsNuLL()) {
                throw _Exceptions.castCriteriaApi();
            }
            if (CollectionUtils.isEmpty(indexNames)) {
                throw new CriteriaException("index hint clause index name list must not empty.");
            }

            List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (indexHintList == null) {
                indexHintList = new ArrayList<>();
                this.indexHintList = indexHintList;
            }
            final MySQLIndexHint.Purpose purpose;
            if (forJoin) {
                purpose = MySQLIndexHint.Purpose.FOR_JOIN;
            } else {
                purpose = null;
            }
            indexHintList.add(new MySQLIndexHint(command, purpose, new ArrayList<>(indexNames)));
            return (IR) this;
        }


    }

    private static abstract class PartitionBlock<C, PR, IR, OR> extends IndexHintClause<C, IR, OR> implements
            MySQLQuery.PartitionClause<C, PR>, Statement.AsClause<IR> {

        String alias;

        private List<String> partitionList;

        PartitionBlock(JoinType joinType, TableMeta<?> table) {
            super(joinType, table);
        }

        @Override
        public final PR partition(String partitionName) {
            if (this.alias != null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.partitionList = Collections.singletonList(partitionName);
            return (PR) this;
        }

        @Override
        public final PR partition(String partitionName1, String partitionNam2) {
            if (this.alias != null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
            return (PR) this;
        }

        @Override
        public final PR partition(List<String> partitionNameList) {
            if (this.alias != null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.partitionList = CollectionUtils.asUnmodifiableList(partitionNameList);
            return (PR) this;
        }

        @Override
        public final PR partition(Supplier<List<String>> supplier) {
            return this.partition(supplier.get());
        }

        @Override
        public final PR partition(Function<C, List<String>> function) {
            return this.partition(function.apply(this.getCriteria()));
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
            list = function.apply(this.getCriteria());
            if (!CollectionUtils.isEmpty(list)) {
                this.partition(list);
            }
            return (PR) this;
        }

        @Override
        public final IR as(final String alias) {
            Objects.requireNonNull(alias);
            this.alias = alias;
            return (IR) this;
        }

        @Override
        public final List<String> partitionList() {
            final List<String> partitionList = this.partitionList;
            assert partitionList != null;
            return partitionList;
        }

        @Override
        boolean aliasIsNuLL() {
            return this.alias == null;
        }

    }// PartitionBlock


    /**
     * @see SimpleMultiUpdate#addPartitionBlock(JoinType, TableMeta)
     */
    private static final class SimplePartitionBlock<C> extends PartitionBlock<
            C,
            MySQLUpdate.MultiAsOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C>, MySQLUpdate.MultiAsOnSpec<C>
            , MySQLUpdate.MultiPartitionOnSpec<C> {

        private final SimpleMultiUpdate<C> update;


        private SimplePartitionBlock(JoinType joinType, TableMeta<?> table, SimpleMultiUpdate<C> update) {
            super(joinType, table);
            this.update = update;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            if (this.alias == null) {
                throw _Exceptions.castCriteriaApi();
            }
            return this.update;
        }

        @Override
        public String alias() {
            final String alias = this.alias;
            assert alias != null;
            return alias;
        }

    }// SimplePartitionBlock


    /**
     * @see SimpleMultiUpdate#addTablePartBlock(JoinType, TablePart, String)
     */
    private static final class SimpleOnBlock<C> extends OnClauseTableBlock<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        private final String alias;

        private final SimpleMultiUpdate<C> update;

        private SimpleOnBlock(JoinType joinType, TablePart tablePart, String alias, SimpleMultiUpdate<C> update) {
            super(joinType, tablePart);
            this.alias = alias;
            this.update = update;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        MultiJoinSpec<C> endOnClause() {
            return this.update;
        }

        @Override
        public String alias() {
            return this.alias;
        }

    } // SimpleOnBlock

    /**
     * @see BatchMultiUpdate#addTablePartBlock(JoinType, TablePart, String)
     */
    private static final class BatchOnBlock<C> extends OnClauseTableBlock<C, MySQLUpdate.BatchMultiJoinSpec<C>>
            implements BatchMultiOnSpec<C> {

        private final String alias;

        private final BatchMultiUpdate<C> update;

        private BatchOnBlock(JoinType joinType, TablePart tablePart, String alias, BatchMultiUpdate<C> update) {
            super(joinType, tablePart);
            this.alias = alias;
            this.update = update;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            return this.update;
        }

        @Override
        public String alias() {
            return this.alias;
        }


    }// BatchOnBlock


    /**
     * @see SimpleMultiUpdate#createNoActionTableBlock()
     */
    private static abstract class NoActionIndexHintOnSpec<C, OR, IR> extends NoActionOnClause<C, OR>
            implements MySQLUpdate.MultiIndexHintClause<C, IR> {

        NoActionIndexHintOnSpec(OR update) {
            super(update);
        }

        @Override
        public final IR useIndex(List<String> indexNames) {
            return (IR) this;
        }

        @Override
        public final IR ignoreIndex(List<String> indexNames) {
            return (IR) this;
        }

        @Override
        public final IR forceIndex(List<String> indexNames) {
            return (IR) this;
        }

        @Override
        public final IR useIndexForJoin(List<String> indexNames) {
            return (IR) this;
        }

        @Override
        public final IR ignoreIndexForJoin(List<String> indexNames) {
            return (IR) this;
        }

        @Override
        public final IR forceIndexForJoin(List<String> indexNames) {
            return (IR) this;
        }

        @Override
        public final IR ifUseIndex(Function<C, List<String>> function) {
            return (IR) this;
        }

        @Override
        public final IR ifIgnoreIndex(Function<C, List<String>> function) {
            return (IR) this;
        }

        @Override
        public final IR ifForceIndex(Function<C, List<String>> function) {
            return (IR) this;
        }

        @Override
        public final IR ifUseIndexForJoin(Function<C, List<String>> function) {
            return (IR) this;
        }

        @Override
        public final IR ifIgnoreIndexForJoin(Function<C, List<String>> function) {
            return (IR) this;
        }

        @Override
        public final IR ifForceIndexForJoin(Function<C, List<String>> function) {
            return (IR) this;
        }

    }// NoActionIndexHintOnSpec

    private static abstract class NoActionPartitionSpec<C, OR, IR, PR, AR> extends NoActionIndexHintOnSpec<C, OR, IR>
            implements MySQLQuery.PartitionClause<C, PR>, Statement.AsClause<AR> {

        NoActionPartitionSpec(OR update) {
            super(update);
        }

        @Override
        public final PR partition(String partitionName) {
            return (PR) this;
        }

        @Override
        public final PR partition(String partitionName1, String partitionNam2) {
            return (PR) this;
        }

        @Override
        public final PR partition(List<String> partitionNameList) {
            return (PR) this;
        }

        @Override
        public final PR partition(Supplier<List<String>> supplier) {
            return (PR) this;
        }

        @Override
        public final PR partition(Function<C, List<String>> function) {
            return (PR) this;
        }

        @Override
        public final PR ifPartition(Supplier<List<String>> supplier) {
            return (PR) this;
        }

        @Override
        public final PR ifPartition(Function<C, List<String>> function) {
            return (PR) this;
        }

        @Override
        public final AR as(String alias) {
            return (AR) this;
        }


    }// NoActionPartitionSpec

    /**
     * @see SimpleMultiUpdate#createNoActionTablePartBlock()
     */
    private static class SimpleNoActionOnSpec<C> extends NoActionOnClause<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        SimpleNoActionOnSpec(SimpleMultiUpdate<C> update) {
            super(update);
        }

    }// SimpleNoActionOnSpec

    /**
     * @see SimpleMultiUpdate#createNoActionPartitionBlock()
     */
    private static final class SimpleNoActionPartitionOnSpec<C> extends NoActionPartitionSpec<
            C,
            MySQLUpdate.MultiJoinSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiAsOnSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>>
            implements MySQLUpdate.MultiPartitionOnSpec<C>, MySQLUpdate.MultiIndexHintOnSpec<C>
            , MySQLUpdate.MultiAsOnSpec<C> {

        private SimpleNoActionPartitionOnSpec(MultiJoinSpec<C> update) {
            super(update);
        }

    }

    /**
     * @see SimpleMultiUpdate#createNoActionTableBlock()
     */
    private static final class SimpleNoActionIndexHintOnSpec<C> extends NoActionIndexHintOnSpec<
            C,
            MySQLUpdate.MultiJoinSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C> {

        private SimpleNoActionIndexHintOnSpec(MultiJoinSpec<C> update) {
            super(update);
        }

    }// SimpleNoActionIndexHintOnSpec


    /**
     * @see BatchMultiUpdate#createNoActionTablePartBlock()
     */
    private static final class BatchNoActionOnSpec<C> extends NoActionOnClause<C,
            MySQLUpdate.BatchMultiJoinSpec<C>> implements MySQLUpdate.BatchMultiOnSpec<C> {

        private BatchNoActionOnSpec(BatchMultiJoinSpec<C> stmt) {
            super(stmt);
        }
    }//BatchNoActionOnSpec


    /**
     * @see BatchMultiUpdate#addPartitionBlock(JoinType, TableMeta)
     */
    private static final class BatchPartitionBlock<C> extends PartitionBlock<
            C,
            MySQLUpdate.BatchMultiAsOnSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C>, MySQLUpdate.BatchMultiAsOnSpec<C>
            , MySQLUpdate.BatchMultiPartitionOnSpec<C> {

        private final BatchMultiUpdate<C> update;

        private BatchPartitionBlock(JoinType joinType, TableMeta<?> table, BatchMultiUpdate<C> update) {
            super(joinType, table);
            this.update = update;
        }

        @Override
        C getCriteria() {
            return this.update.criteria;
        }

        @Override
        BatchMultiJoinSpec<C> endOnClause() {
            if (this.alias == null) {
                throw _Exceptions.castCriteriaApi();
            }
            return this.update;
        }

        @Override
        public String alias() {
            final String alias = this.alias;
            assert alias != null;
            return alias;
        }

    }// BatchPartitionBlock

    /**
     * @see BatchMultiUpdate#createNoActionPartitionBlock()
     */
    private static final class BatchNoActionPartitionOnSpec<C> extends NoActionPartitionSpec<
            C,
            MySQLUpdate.BatchMultiJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiAsOnSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>>
            implements MySQLUpdate.BatchMultiPartitionOnSpec<C>, MySQLUpdate.BatchMultiIndexHintOnSpec<C>
            , MySQLUpdate.BatchMultiAsOnSpec<C> {

        private BatchNoActionPartitionOnSpec(BatchMultiJoinSpec<C> update) {
            super(update);
        }

    }//BatchNoActionPartitionOnSpec

    /**
     * @see BatchMultiUpdate#createNoActionTableBlock()
     */
    private static final class BatchNoActionIndexHintOnSpec<C> extends NoActionIndexHintOnSpec<
            C,
            MySQLUpdate.BatchMultiJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C> {

        private BatchNoActionIndexHintOnSpec(BatchMultiJoinSpec<C> update) {
            super(update);
        }

    }// BatchNoActionIndexHintOnSpec


    private static abstract class IndexHintTableBlock<C, IR, OR> extends IndexHintClause<C, IR, OR> {

        private final String tableAlias;

        final OR update;

        private IndexHintTableBlock(JoinType joinType, TablePart tablePart, String tableAlias, OR update) {
            super(joinType, tablePart);
            this.tableAlias = tableAlias;
            this.update = update;
        }

        @Override
        final OR endOnClause() {
            final List<MySQLIndexHint> indexHintList = this.indexHintList;
            if (CollectionUtils.isEmpty(indexHintList)) {
                this.indexHintList = Collections.emptyList();
            } else {
                this.indexHintList = CollectionUtils.unmodifiableList(indexHintList);
            }
            return this.update;
        }

        @Override
        public final String alias() {
            return this.tableAlias;
        }

        @Override
        public final List<String> partitionList() {
            return Collections.emptyList();
        }


    }// TableBlock

    /**
     * @see SimpleMultiUpdate#addTableBlock(JoinType, TableMeta, String)
     */
    private static final class SimpleTableBlock<C> extends IndexHintTableBlock<
            C,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C> {


        private SimpleTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias, SimpleMultiUpdate<C> update) {
            super(joinType, table, tableAlias, update);
        }


        @Override
        C getCriteria() {
            return ((SimpleMultiUpdate<C>) this.update).criteria;
        }


    }// SimpleTableBlock

    /**
     * @see BatchMultiUpdate#addTableBlock(JoinType, TableMeta, String)
     */
    private static final class BatchTableBlock<C> extends IndexHintTableBlock<
            C,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C> {

        private BatchTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias, BatchMultiUpdate<C> update) {
            super(joinType, table, tableAlias, update);
        }

        @Override
        C getCriteria() {
            return ((BatchMultiUpdate<C>) this.update).criteria;
        }

    }//BatchTableBlock


}
