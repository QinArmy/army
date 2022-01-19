package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
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

@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdate<C, UR, UP, PR, IR, WR, WA, SR, OR, LR> extends SingleUpdate<C, WR, WA, SR>
        implements Query.OrderByClause<C, OR>, MySQLUpdate.LimitClause<C, LR>, MySQLUpdate, _MySQLSingleUpdate
        , MySQLUpdate.SingleUpdateClause<UR, UP>, MySQLQuery.PartitionClause<C, PR>
        , MySQLQuery.IndexHintClause<C, IR, UR>, MySQLQuery.IndexOrderByClause<C, UR>
        , Statement.AsClause<UR> {


    static <C> MySQLUpdate.SingleUpdateSpec<C> simple57(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batch57(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    static <C> MySQLUpdate.SingleWithAndUpdateSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndUpdate<>(criteria);
    }

    static <C> MySQLUpdate.BatchSingleWithAndUpdateSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndUpdate<>(criteria);
    }

    private List<Hint> hintList;

    private List<SQLModifier> modifierList;

    private TableMeta<?> table;

    private String tableAlias;

    private List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;

    private MySQLIndexHint.Command command;

    private List<SortPart> orderByList;

    private long rowCount;

    private MySQLSingleUpdate(@Nullable C criteria) {
        super(CriteriaUtils.primaryContext(criteria));

    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , TableMeta<? extends IDomain> table) {
        if (this.hintList != null || this.modifierList != null || this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final List<Hint> hintList;
        hintList = hints.get();
        assert hintList != null;
        this.hintList = CollectionUtils.asUnmodifiableList(hintList);
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        this.table = table;
        return (UP) this;
    }

    @Override
    public final UR update(Supplier<List<Hint>> hints, List<SQLModifier> modifiers
            , TableMeta<? extends IDomain> table, String tableAlias) {
        if (this.hintList != null || this.modifierList != null || this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final List<Hint> hintList;
        hintList = hints.get();
        assert hintList != null;

        this.hintList = CollectionUtils.asUnmodifiableList(hintList);
        this.modifierList = CollectionUtils.asUnmodifiableList(modifiers);
        this.table = table;
        this.tableAlias = tableAlias;
        return (UR) this;
    }

    @Override
    public final UP update(TableMeta<? extends IDomain> table) {
        this.table = table;
        return (UP) this;
    }

    @Override
    public final UR update(TableMeta<? extends IDomain> table, String tableAlias) {
        this.table = table;
        this.tableAlias = tableAlias;
        return (UR) this;
    }

    @Override
    public final PR partition(String partitionName) {
        if (this.table == null || this.partitionList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.partitionList = Collections.singletonList(partitionName);
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2) {
        return this.partition(Arrays.asList(partitionName1, partitionNam2));
    }

    @Override
    public final PR partition(List<String> partitionNameList) {
        if (this.table == null || this.partitionList != null) {
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

    @Override
    public final UR as(String alias) {
        if (this.table == null || this.tableAlias != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAlias = alias;
        return (UR) this;
    }

    /*################################## blow IndexHintClause method ##################################*/

    @Override
    public final IR useIndex() {
        this.command = MySQLIndexHint.Command.USER_INDEX;
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        this.command = MySQLIndexHint.Command.FORCE_INDEX;
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.useIndex();
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.ignoreIndex();
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.forceIndex();
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final UR useIndex(List<String> indexList) {
        if (this.command != null) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
    }

    @Override
    public final UR ignoreIndex(List<String> indexList) {
        if (this.command != null) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
    }

    @Override
    public final UR forceIndex(List<String> indexList) {
        if (this.command != null) {
            throw _Exceptions.castCriteriaApi();
        }
        return this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
    }

    @Override
    public final UR ifUseIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexList)) {
            this.useIndex(indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexList)) {
            this.ignoreIndex(indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR ifForceIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(indexList)) {
            this.forceIndex(indexList);
        }
        return (UR) this;
    }


    /*################################## blow IndexOrderByClause method ##################################*/

    @Override
    public final UR forOrderBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.command;
        if (command != null) {
            this.command = null;
            this.addIndexHint(command, true, indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR forOrderBy(Function<C, List<String>> function) {
        if (this.command != null) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (!CollectionUtils.isEmpty(list)) {
                this.forOrderBy(list);
            }
        }
        return (UR) this;
    }



    /*################################## blow OrderByClause method ##################################*/

    @Override
    public final OR orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(sortPart1, sortPart2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(List<SortPart> sortPartList) {
        this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
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
            this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> sortPartList;
        sortPartList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = CollectionUtils.asUnmodifiableList(sortPartList);
        }
        return (OR) this;
    }

    /*################################## blow LimitClause method ##################################*/

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
    final void onAsUpdate() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        if (CollectionUtils.isEmpty(this.hintList)) {
            this.hintList = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(this.modifierList)) {
            this.modifierList = Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(this.partitionList)) {
            this.partitionList = Collections.emptyList();
        }
        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (CollectionUtils.isEmpty(indexHintList)) {
            this.indexHintList = Collections.emptyList();
        } else {
            this.indexHintList = CollectionUtils.unmodifiableList(indexHintList);
        }
        this.command = null;
        if (CollectionUtils.isEmpty(this.orderByList)) {
            this.orderByList = Collections.emptyList();
        }

        if (this instanceof BatchUpdate) {
            if (CollectionUtils.isEmpty(((BatchUpdate<C>) this).wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }

        }

        if (this instanceof _MySQLWithClause) {
            if (this instanceof SimpleWithAndUpdate) {
                if (CollectionUtils.isEmpty(((SimpleWithAndUpdate<C>) this).cteList)) {
                    ((SimpleWithAndUpdate<C>) this).cteList = Collections.emptyList();
                }
            }
            if (this instanceof BatchWithAndUpdate) {
                if (CollectionUtils.isEmpty(((BatchWithAndUpdate<C>) this).cteList)) {
                    ((BatchWithAndUpdate<C>) this).cteList = Collections.emptyList();
                }
            }
        }

    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;
        this.indexHintList = null;
        this.orderByList = null;

        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C>) this).wrapperList = null;
        }

        if (this instanceof _MySQLWithClause) {
            if (this instanceof SimpleWithAndUpdate) {
                ((SimpleWithAndUpdate<C>) this).cteList = null;
            }
            if (this instanceof BatchWithAndUpdate) {
                ((BatchWithAndUpdate<C>) this).cteList = null;
            }
        }


    }

    /*################################## blow _MySQLSingleUpdate method ##################################*/

    @Override
    public final TableMeta<?> table() {
        prepared();
        return this.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final List<String> partitionList() {
        return this.partitionList;
    }

    @Override
    public final List<? extends _IndexHint> indexHintList() {
        prepared();
        return this.indexHintList;
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
    public final List<SortPart> orderByList() {
        return this.orderByList;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    private UR addIndexHint(MySQLIndexHint.Command command, final boolean orderBy, final List<String> indexNames) {
        if (indexNames.size() == 0) {
            throw MySQLUtils.indexListIsEmpty();
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
        return (UR) this;
    }


    private static class SimpleUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate.SingleIndexHintSpec<C>,//UR
            MySQLUpdate.SinglePartitionSpec<C>,//UP
            Statement.AsClause<MySQLUpdate.SingleIndexHintSpec<C>>,//PR
            MySQLUpdate.IndexOrderBySpec<C>,//IR
            MySQLUpdate.OrderBySpec<C>, //WR
            MySQLUpdate.SingleWhereAndSpec<C>, //WA
            MySQLUpdate.SingleWhereSpec<C>,//SR
            MySQLUpdate.LimitSpec<C>, //OR
            Update.UpdateSpec>         //LR
            implements MySQLUpdate.SingleUpdateSpec<C>, MySQLUpdate.SinglePartitionSpec<C>
            , MySQLUpdate.SingleIndexHintSpec<C>, MySQLUpdate.SingleWhereSpec<C>, MySQLUpdate.SingleWhereAndSpec<C>
            , MySQLUpdate.OrderBySpec<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }


    } // SimpleUpdate

    private static class BatchUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate.BatchSingleIndexHintSpec<C>,//UR
            MySQLUpdate.BatchSinglePartitionSpec<C>,//UP
            Statement.AsClause<MySQLUpdate.BatchSingleIndexHintSpec<C>>,//PR
            MySQLUpdate.BatchIndexOrderBySpec<C>,   //IR
            MySQLUpdate.BatchOrderBySpec<C>,        //WR
            MySQLUpdate.BatchSingleWhereAndSpec<C>, //WA
            MySQLUpdate.BatchSingleWhereSpec<C>,    //SR
            MySQLUpdate.BatchLimitSpec<C>,         //OR
            Statement.BatchParamClause<C, UpdateSpec>> //LR
            implements MySQLUpdate.BatchSingleUpdateSpec<C>, MySQLUpdate.BatchSinglePartitionSpec<C>
            , MySQLUpdate.BatchSingleIndexHintSpec<C>, MySQLUpdate.BatchIndexOrderBySpec<C>
            , MySQLUpdate.BatchSingleWhereSpec<C>, MySQLUpdate.BatchSingleWhereAndSpec<C>
            , MySQLUpdate.BatchOrderBySpec<C>, Statement.BatchParamClause<C, UpdateSpec>, _BatchDml {

        private List<ReadWrapper> wrapperList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final UpdateSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public final UpdateSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public final UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public final UpdateSpec paramBeans(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public final UpdateSpec paramBeans(Supplier<List<?>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public final UpdateSpec paramBeans(Function<C, List<?>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }

        @Override
        public final List<ReadWrapper> wrapperList() {
            return this.wrapperList;
        }

    }//BatchUpdate


    private static final class SimpleWithAndUpdate<C> extends SimpleUpdate<C>
            implements SingleWithAndUpdateSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleWithAndUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public SingleUpdateSpec<C> with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return this;
        }

        @Override
        public SingleUpdateSpec<C> with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return this;
        }

        @Override
        public SingleUpdateSpec<C> with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return this;
        }

        @Override
        public SingleUpdateSpec<C> with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return this;
        }

        @Override
        public SingleUpdateSpec<C> withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public SingleUpdateSpec<C> withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public SingleUpdateSpec<C> withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public SingleUpdateSpec<C> withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

    }//SimpleWithAndUpdate

    private static final class BatchWithAndUpdate<C> extends BatchUpdate<C>
            implements MySQLUpdate.BatchSingleWithAndUpdateSpec<C>, _MySQLWithClause {


        private boolean recursive;

        private List<Cte> cteList;

        private BatchWithAndUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public BatchSingleUpdateSpec<C> with(String cteName, Supplier<SubQuery> supplier) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, supplier.get()));
            return this;
        }

        @Override
        public BatchSingleUpdateSpec<C> with(String cteName, Function<C, SubQuery> function) {
            this.cteList = Collections.singletonList(CteImpl.create(cteName, function.apply(this.criteria)));
            return this;
        }

        @Override
        public BatchSingleUpdateSpec<C> with(Supplier<List<Cte>> supplier) {
            this.cteList = CollectionUtils.asUnmodifiableList(supplier.get());
            return this;
        }

        @Override
        public BatchSingleUpdateSpec<C> with(Function<C, List<Cte>> function) {
            this.cteList = CollectionUtils.asUnmodifiableList(function.apply(this.criteria));
            return this;
        }

        @Override
        public BatchSingleUpdateSpec<C> withRecursive(String cteName, Supplier<SubQuery> supplier) {
            this.recursive = true;
            return this.with(cteName, supplier);
        }

        @Override
        public BatchSingleUpdateSpec<C> withRecursive(String cteName, Function<C, SubQuery> function) {
            this.recursive = true;
            return this.with(cteName, function);
        }

        @Override
        public BatchSingleUpdateSpec<C> withRecursive(Supplier<List<Cte>> supplier) {
            this.recursive = true;
            return this.with(supplier);
        }

        @Override
        public BatchSingleUpdateSpec<C> withRecursive(Function<C, List<Cte>> function) {
            this.recursive = true;
            return this.with(function);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

    }// BatchWithAndUpdate


}
