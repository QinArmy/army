package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
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

    private IT noActionPartitionBlock;

    private MySQLMultiUpdate(final FirstBlock firstBlock, @Nullable C criteria) {
        super(firstBlock, criteria);
        this.firstBlock = firstBlock;
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
    public final <T extends TablePart> JS straightJoin(Supplier<T> supplier, String alias) {
        return this.innerAddTablePartBlock(JoinType.STRAIGHT_JOIN, supplier, alias);
    }

    @Override
    public final <T extends TablePart> JS ifStraightJoin(Supplier<T> supplier, String alias) {
        return this.ifAddTablePartBlock(JoinType.STRAIGHT_JOIN, supplier, alias);
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
    final void doOnAsUpdate() {
        final List<MySQLIndexHint> indexHintList = this.firstBlock.indexHintList;
        if (CollectionUtils.isEmpty(indexHintList)) {
            this.firstBlock.indexHintList = Collections.emptyList();
        } else {
            this.firstBlock.indexHintList = Collections.unmodifiableList(indexHintList);
        }

        this.noActionPartitionBlock = null;
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
        if (this.blockSize() != 1) {
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
            super(JoinType.NONE, tablePart);
            this.alias = alias;
            this.hintList = Collections.emptyList();
            this.modifierList = Collections.emptyList();
            this.partitionList = Collections.emptyList();
        }

        private FirstBlock(List<Hint> hintList, List<SQLModifier> modifierList, TablePart tablePart
                , String alias) {
            super(JoinType.NONE, tablePart);
            this.hintList = CollectionUtils.asUnmodifiableList(hintList);
            this.modifierList = CollectionUtils.asUnmodifiableList(modifierList);
            this.alias = alias;
            this.partitionList = Collections.emptyList();
        }


        private FirstBlock(List<Hint> hintList, List<SQLModifier> modifierList, TablePart tablePart
                , String alias, List<String> partitionList) {
            super(JoinType.NONE, tablePart);
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
        MultiIndexHintOnSpec<C> createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new SimpleIndexHintBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        MultiOnSpec<C> createTablePartBlock(JoinType joinType, TablePart tablePart, String alias) {
            return new SimpleOnBlock<>(joinType, tablePart, alias, this);
        }

        @Override
        MultiPartitionOnSpec<C> addPartitionBlock(JoinType joinType, TableMeta<?> table) {
            final SimplePartitionBlock<C> block = new SimplePartitionBlock<>(joinType, table, this);
            this.addOtherBlock(block);
            return block;
        }


        @Override
        MultiIndexHintOnSpec<C> createNoActionTableBlock() {
            return new SimpleNoActionIndexHintBlock<>(this);
        }

        @Override
        MultiOnSpec<C> createNoActionTablePartBlock() {
            return new SimpleNoActionOnBlock<>(this);
        }

        @Override
        MultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new SimpleNoActionPartitionBlock<>(this);
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
        BatchMultiIndexHintOnSpec<C> createTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            return new BatchIndexHintBlock<>(joinType, table, tableAlias, this);
        }

        @Override
        BatchMultiOnSpec<C> createTablePartBlock(JoinType joinType, TablePart tablePart, String alias) {
            return new BatchOnBlock<>(joinType, tablePart, alias, this);
        }

        @Override
        BatchMultiPartitionOnSpec<C> addPartitionBlock(JoinType joinType, TableMeta<?> table) {
            final BatchPartitionBlock<C> block = new BatchPartitionBlock<>(joinType, table, this);
            this.addOtherBlock(block);
            return block;
        }

        @Override
        BatchMultiIndexHintOnSpec<C> createNoActionTableBlock() {
            return new BatchNoActionIndexHintBlock<>(this);
        }

        @Override
        BatchMultiOnSpec<C> createNoActionTablePartBlock() {
            return new BatchNoActionOnSpec<>(this);
        }

        @Override
        BatchMultiPartitionOnSpec<C> createNoActionPartitionBlock() {
            return new BatchNoActionPartitionBlock<>(this);
        }


    }// BatchMultiUpdate


    /**
     * @see MultiUpdateSpecImpl#update(TableMeta)
     * @see MultiUpdateSpecImpl#update(Supplier, List, TableMeta)
     */
    private static final class SimplePartitionJoinSpec<C> extends MySQLPartitionClause<C, MultiAsJoinSpec<C>>
            implements MultiAsJoinSpec<C>, MultiPartitionJoinSpec<C> {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifies;

        private final TableMeta<?> table;


        private SimplePartitionJoinSpec(List<Hint> hintList, List<SQLModifier> modifies
                , TableMeta<?> table, @Nullable C criteria) {
            super(criteria);
            this.hintList = hintList;
            this.modifies = modifies;
            this.table = table;
        }


        private SimplePartitionJoinSpec(TableMeta<?> table, @Nullable C criteria) {
            super(criteria);
            this.hintList = Collections.emptyList();
            this.modifies = Collections.emptyList();
            this.table = table;
        }

        @Override
        public MultiIndexHintJoinSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            final FirstBlock block;
            block = new FirstBlock(this.hintList, this.modifies, this.table, tableAlias, partitionList);

            return new SimpleMultiUpdate<>(block, this.criteria);
        }

    }// SimplePartitionJoinSpec

    /**
     * @see BatchMultiUpdateSpecImpl#update(TableMeta)
     * @see BatchMultiUpdateSpecImpl#update(Supplier, List, TableMeta)
     */
    private static final class BatchPartitionJoinSpec<C> extends MySQLPartitionClause<C, BatchMultiAsJoinSpec<C>>
            implements BatchMultiAsJoinSpec<C>, BatchMultiPartitionJoinSpec<C> {

        private final List<Hint> hintList;

        private final List<SQLModifier> modifies;

        private final TableMeta<?> table;

        private BatchPartitionJoinSpec(List<Hint> hintList, List<SQLModifier> modifies
                , TableMeta<?> table, @Nullable C criteria) {
            super(criteria);
            this.hintList = hintList;
            this.modifies = modifies;
            this.table = table;
        }

        private BatchPartitionJoinSpec(TableMeta<?> table, @Nullable C criteria) {
            super(criteria);
            this.hintList = Collections.emptyList();
            this.modifies = Collections.emptyList();
            this.table = table;
        }

        @Override
        public BatchMultiIndexHintJoinSpec<C> as(String tableAlias) {
            Objects.requireNonNull(tableAlias);
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            final FirstBlock block;
            block = new FirstBlock(this.hintList, this.modifies, this.table, tableAlias, partitionList);
            return new BatchMultiUpdate<>(block, this.criteria);
        }
    }// BatchPartitionJoinSpec


    private static abstract class IndexHintBlock<C, IR, OR> extends OnClauseTableBlock<C, OR> implements
            MySQLUpdate.MultiIndexHintClause<C, IR>, _MySQLTableBlock {

        final String tableAlias;

        private List<MySQLIndexHint> indexHintList;

        IndexHintBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
            super(joinType, table);
            this.tableAlias = tableAlias;
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

        private IR addIndexHint(final MySQLIndexHint.Command command, final boolean forJoin
                , final List<String> indexNames) {
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


    /**
     * @see SimpleMultiUpdate#addPartitionBlock(JoinType, TableMeta)
     */
    private static final class SimplePartitionBlock<C> extends MySQLPartitionClause<C, MySQLUpdate.MultiAsOnSpec<C>>
            implements MySQLUpdate.MultiAsOnSpec<C>, MySQLUpdate.MultiPartitionOnSpec<C>, _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleMultiUpdate<C> update;

        private SimpleIndexHintBlock<C> indexHintOnSpec;

        private SimplePartitionBlock(JoinType joinType, TableMeta<?> table, SimpleMultiUpdate<C> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public MultiIndexHintOnSpec<C> as(final String alias) {
            if (this.indexHintOnSpec != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            final SimpleIndexHintBlock<C> indexHintOnSpec;
            indexHintOnSpec = new SimpleIndexHintBlock<>(this.joinType, this.table, alias, this.update);
            this.indexHintOnSpec = indexHintOnSpec;
            return indexHintOnSpec;
        }

        @Override
        public TablePart table() {
            return this.table;
        }

        @Override
        public String alias() {
            final SimpleIndexHintBlock<C> indexHintOnSpec = this.indexHintOnSpec;
            assert indexHintOnSpec != null;
            return indexHintOnSpec.tableAlias;
        }

        @Override
        public SQLModifier jointType() {
            return this.joinType;
        }

        @Override
        public List<_Predicate> predicates() {
            final SimpleIndexHintBlock<C> indexHintOnSpec = this.indexHintOnSpec;
            assert indexHintOnSpec != null;
            return indexHintOnSpec.predicates();
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
            final SimpleIndexHintBlock<C> indexHintOnSpec = this.indexHintOnSpec;
            assert indexHintOnSpec != null;
            return indexHintOnSpec.indexHintList();
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
     * @see BatchMultiUpdate#addPartitionBlock(JoinType, TableMeta)
     */
    private static final class BatchPartitionBlock<C> extends MySQLPartitionClause<C, MySQLUpdate.BatchMultiAsOnSpec<C>>
            implements MySQLUpdate.BatchMultiAsOnSpec<C>, MySQLUpdate.BatchMultiPartitionOnSpec<C>
            , _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final BatchMultiUpdate<C> update;

        private BatchIndexHintBlock<C> indexHintBlock;

        private BatchPartitionBlock(JoinType joinType, TableMeta<?> table, BatchMultiUpdate<C> update) {
            super(update.criteria);
            this.update = update;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public BatchMultiIndexHintOnSpec<C> as(final String tableAlias) {
            if (this.indexHintBlock != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(tableAlias);
            final BatchIndexHintBlock<C> indexHintBlock;
            indexHintBlock = new BatchIndexHintBlock<>(this.joinType, this.table, tableAlias, this.update);
            this.indexHintBlock = indexHintBlock;
            return indexHintBlock;
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
        public List<_Predicate> predicates() {
            final BatchIndexHintBlock<C> indexHintBlock = this.indexHintBlock;
            assert indexHintBlock != null;
            return indexHintBlock.predicates();
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
            final BatchIndexHintBlock<C> indexHintBlock = this.indexHintBlock;
            assert indexHintBlock != null;
            return indexHintBlock.indexHintList();
        }


        @Override
        public String alias() {
            final BatchIndexHintBlock<C> indexHintBlock = this.indexHintBlock;
            assert indexHintBlock != null;
            return indexHintBlock.tableAlias;
        }

    }// BatchPartitionBlock


    /**
     * @see SimpleMultiUpdate#addTableBlock(JoinType, TableMeta, String)
     */
    private static final class SimpleIndexHintBlock<C> extends IndexHintBlock<
            C,
            MySQLUpdate.MultiIndexHintOnSpec<C>,
            MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C> {

        private final SimpleMultiUpdate<C> update;

        private SimpleIndexHintBlock(JoinType joinType, TableMeta<?> table, String tableAlias, SimpleMultiUpdate<C> update) {
            super(joinType, table, tableAlias);
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
            return this.tableAlias;
        }

        @Override
        public List<String> partitionList() {
            return Collections.emptyList();
        }

    }// SimpleIndexHintBlock

    /**
     * @see BatchMultiUpdate#addTableBlock(JoinType, TableMeta, String)
     */
    private static final class BatchIndexHintBlock<C> extends IndexHintBlock<
            C,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate.BatchMultiJoinSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C> {

        private final BatchMultiUpdate<C> update;

        private BatchIndexHintBlock(JoinType joinType, TableMeta<?> table, String tableAlias, BatchMultiUpdate<C> update) {
            super(joinType, table, tableAlias);
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
            return this.tableAlias;
        }

        @Override
        public List<String> partitionList() {
            return Collections.emptyList();
        }

    }//BatchIndexHintBlock


    /**
     * @see SimpleMultiUpdate#createNoActionTablePartBlock()
     */
    private static class SimpleNoActionOnBlock<C> extends NoActionOnClause<C, MySQLUpdate.MultiJoinSpec<C>>
            implements MySQLUpdate.MultiOnSpec<C> {

        SimpleNoActionOnBlock(SimpleMultiUpdate<C> update) {
            super(update);
        }

    }// SimpleNoActionOnSpec


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


    /**
     * @see SimpleMultiUpdate#createNoActionPartitionBlock()
     */
    private static final class SimpleNoActionPartitionBlock<C> extends NoActionMySQLPartitionClause<
            C, MySQLUpdate.MultiAsOnSpec<C>>
            implements MySQLUpdate.MultiPartitionOnSpec<C>, MySQLUpdate.MultiAsOnSpec<C> {


        private final MultiIndexHintOnSpec<C> hintOnSpec;

        private SimpleNoActionPartitionBlock(MultiJoinSpec<C> update) {
            this.hintOnSpec = new SimpleNoActionIndexHintBlock<>(update);
        }

        @Override
        public MultiIndexHintOnSpec<C> as(String alias) {
            return this.hintOnSpec;
        }

    }// SimpleNoActionPartitionBlock

    /**
     * @see SimpleMultiUpdate#createNoActionTableBlock()
     */
    private static final class SimpleNoActionIndexHintBlock<C> extends NoActionIndexHintOnSpec<
            C,
            MySQLUpdate.MultiJoinSpec<C>,
            MySQLUpdate.MultiIndexHintOnSpec<C>>
            implements MySQLUpdate.MultiIndexHintOnSpec<C> {

        private SimpleNoActionIndexHintBlock(MultiJoinSpec<C> update) {
            super(update);
        }

    }// SimpleNoActionIndexHintOnSpec


    /**
     * @see BatchMultiUpdate#createNoActionPartitionBlock()
     */
    private static final class BatchNoActionPartitionBlock<C> extends NoActionMySQLPartitionClause<
            C, MySQLUpdate.BatchMultiAsOnSpec<C>>
            implements MySQLUpdate.BatchMultiPartitionOnSpec<C>, MySQLUpdate.BatchMultiAsOnSpec<C> {

        private final BatchMultiIndexHintOnSpec<C> indexHintOnSpec;

        private BatchNoActionPartitionBlock(BatchMultiUpdate<C> update) {
            this.indexHintOnSpec = new BatchNoActionIndexHintBlock<>(update);
        }

        @Override
        public BatchMultiIndexHintOnSpec<C> as(String tableAlias) {
            return this.indexHintOnSpec;
        }
    }//BatchNoActionPartitionOnSpec

    /**
     * @see BatchMultiUpdate#createNoActionTableBlock()
     */
    private static final class BatchNoActionIndexHintBlock<C> extends NoActionIndexHintOnSpec<
            C,
            MySQLUpdate.BatchMultiJoinSpec<C>,
            MySQLUpdate.BatchMultiIndexHintOnSpec<C>>
            implements MySQLUpdate.BatchMultiIndexHintOnSpec<C> {

        private BatchNoActionIndexHintBlock(BatchMultiJoinSpec<C> update) {
            super(update);
        }

    }// BatchNoActionIndexHintOnSpec


}
