package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLModifier;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of {@link _MySQLSingleUpdate}. This class extends {@link SingleUpdate}
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdate<C, WE, UR, UP, PR, IR, WR, WA, SR, OR, LR> extends WithCteSingleUpdate<C, WE, WR, WA, SR>
        implements Statement._OrderByClause<C, OR>, MySQLUpdate.LimitClause<C, LR>, MySQLUpdate, _MySQLSingleUpdate
        , MySQLUpdate.SingleUpdateClause<UR, UP>, MySQLQuery._PartitionClause<C, PR>
        , MySQLQuery._IndexHintClause<C, IR, UR>, MySQLQuery._IndexOrderByClause<C, UR>
        , Statement._AsClause<UR> {


    static <C> MySQLUpdate.SingleUpdateSpec<C> simple57(@Nullable C criteria) {
        final SimpleUpdate<C, Void> update;
        update = new SimpleUpdate<>(criteria);
        return update;
    }

    static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batch57(@Nullable C criteria) {
        final BatchUpdate<C, Void> update;
        update = new BatchUpdate<>(criteria);
        return update;
    }

    static <C> MySQLUpdate.SingleWithAndUpdateSpec<C> simple80(@Nullable C criteria) {
        return new SimpleWithAndUpdate<>(criteria);
    }

    static <C> MySQLUpdate.BatchSingleWithAndUpdateSpec<C> batch80(@Nullable C criteria) {
        return new BatchWithAndUpdate<>(criteria);
    }

    private List<_MySQLHint> hintList;

    private Set<MySQLModifier> modifierSet;

    private TableMeta<?> table;

    private String tableAlias;

    private List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;

    private MySQLIndexHint.Command command;

    private List<SortItem> orderByList;

    private long rowCount;

    private MySQLSingleUpdate(@Nullable C criteria) {
        super(CriteriaContexts.singleDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, EnumSet<MySQLModifier> modifiers
            , TableMeta<?> table) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get());
        this.modifierSet = MySQLUtils.asUpdateModifierSet(modifiers);
        this.table = table;
        return (UP) this;
    }

    @Override
    public final UR update(Supplier<List<Hint>> hints, EnumSet<MySQLModifier> modifiers
            , TableMeta<?> table, String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get());
        this.modifierSet = MySQLUtils.asUpdateModifierSet(modifiers);
        this.table = table;
        this.tableAlias = tableAlias;
        return (UR) this;
    }

    @Override
    public final UP update(TableMeta<?> table) {
        this.table = table;
        return (UP) this;
    }

    @Override
    public final UR update(TableMeta<?> table, String tableAlias) {
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
        if (this.table == null || this.partitionList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
        return (PR) this;
    }

    @Override
    public final PR partition(List<String> partitionNameList) {
        if (this.table == null || this.partitionList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.partitionList = _CollectionUtils.asUnmodifiableList(partitionNameList);
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
        if (!_CollectionUtils.isEmpty(list)) {
            this.partition(list);
        }
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(list)) {
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
        if (!_CollectionUtils.isEmpty(indexList)) {
            this.useIndex(indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(indexList)) {
            this.ignoreIndex(indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR ifForceIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(indexList)) {
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
            if (!_CollectionUtils.isEmpty(list)) {
                this.forOrderBy(list);
            }
        }
        return (UR) this;
    }



    /*################################## blow OrderByClause method ##################################*/

    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList(sortItem);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(sortItem1, sortItem2);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(sortItem1, sortItem2, sortItem3);
        return (OR) this;
    }

    @Override
    public final OR orderBy(List<SortItem> sortItemList) {
        this.orderByList = _CollectionUtils.asUnmodifiableList(sortItemList);
        return (OR) this;
    }

    @Override
    public final OR orderBy(Function<C, List<SortItem>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    @Override
    public final OR orderBy(Supplier<List<SortItem>> supplier) {
        return this.orderBy(supplier.get());
    }

    @Override
    public final OR ifOrderBy(@Nullable SortItem sortItem) {
        if (sortItem != null) {
            this.orderByList = Collections.singletonList(sortItem);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Supplier<List<SortItem>> supplier) {
        final List<SortItem> sortItemList;
        sortItemList = supplier.get();
        if (!_CollectionUtils.isEmpty(sortItemList)) {
            this.orderByList = _CollectionUtils.asUnmodifiableList(sortItemList);
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<C, List<SortItem>> function) {
        final List<SortItem> sortItemList;
        sortItemList = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(sortItemList)) {
            this.orderByList = _CollectionUtils.asUnmodifiableList(sortItemList);
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
    public final LR limit(Supplier<Number> supplier) {
        final Number rowCount;
        rowCount = supplier.get();
        if (rowCount instanceof Long) {
            this.rowCount = (Long) rowCount;
        } else if (rowCount instanceof Integer) {
            this.rowCount = rowCount.longValue();
        } else {
            throw MySQLUtils.limitParamError();
        }
        return (LR) this;
    }

    @Override
    public final LR limit(Function<C, Long> function) {
        this.rowCount = function.apply(this.criteria);
        return (LR) this;
    }

    @Override
    public final LR limit(Function<String, ?> function, String keyName) {
        final Object rowCount;
        rowCount = function.apply(keyName);
        if (rowCount instanceof Long) {
            this.rowCount = (Long) rowCount;
        } else if (rowCount instanceof Integer) {
            this.rowCount = ((Integer) rowCount).longValue();
        } else {
            throw MySQLUtils.limitParamError();
        }
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<Number> supplier) {
        final Number rowCount;
        rowCount = supplier.get();
        if (rowCount instanceof Long) {
            this.rowCount = (Long) rowCount;
        } else if (rowCount instanceof Integer) {
            this.rowCount = rowCount.longValue();
        } else if (rowCount != null) {
            throw MySQLUtils.limitParamError();
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
    public final LR ifLimit(Function<String, ?> function, String keyName) {
        final Object rowCount;
        rowCount = function.apply(keyName);
        if (rowCount instanceof Long) {
            this.rowCount = (Long) rowCount;
        } else if (rowCount instanceof Integer) {
            this.rowCount = ((Integer) rowCount).longValue();
        } else if (rowCount != null) {
            throw MySQLUtils.limitParamError();
        }
        return (LR) this;
    }

    @Override
    final void onAsUpdate() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierSet == null) {
            this.modifierSet = Collections.emptySet();
        }
        if (this.partitionList == null) {
            this.partitionList = Collections.emptyList();
        }
        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (_CollectionUtils.isEmpty(indexHintList)) {
            this.indexHintList = Collections.emptyList();
        } else {
            this.indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
        }
        this.command = null;
        if (_CollectionUtils.isEmpty(this.orderByList)) {
            this.orderByList = Collections.emptyList();
        }

        if (this instanceof BatchUpdate) {
            if (_CollectionUtils.isEmpty(((BatchUpdate<C, ?>) this).wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }

        }
        if (this instanceof SimpleWithAndUpdate) {
            final SimpleWithAndUpdate<C> update = (SimpleWithAndUpdate<C>) this;
            if (update.cteList == null) {
                update.cteList = Collections.emptyList();
            }
        } else if (this instanceof BatchWithAndUpdate) {
            final BatchWithAndUpdate<C> update = (BatchWithAndUpdate<C>) this;
            if (update.cteList == null) {
                update.cteList = Collections.emptyList();
            }
        }

    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierSet = null;
        this.partitionList = null;
        this.indexHintList = null;
        this.orderByList = null;

        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C, ?>) this).wrapperList = null;
        }

        if (this instanceof SimpleWithAndUpdate) {
            ((SimpleWithAndUpdate<C>) this).cteList = null;
        } else if (this instanceof BatchWithAndUpdate) {
            ((BatchWithAndUpdate<C>) this).cteList = null;
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
    public final List<_MySQLHint> hintList() {
        return this.hintList;
    }

    @Override
    public final Set<MySQLModifier> modifierSet() {
        return this.modifierSet;
    }

    @Override
    public final List<SortItem> orderByList() {
        return this.orderByList;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }


    @Override
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
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


    private static class SimpleUpdate<C, WE> extends MySQLSingleUpdate<
            C,
            WE,// WE
            MySQLUpdate.SingleIndexHintSpec<C>,//UR
            MySQLUpdate.SinglePartitionSpec<C>,//UP
            _AsClause<SingleIndexHintSpec<C>>,//PR
            MySQLUpdate.IndexOrderBySpec<C>,//IR
            MySQLUpdate.OrderBySpec<C>, //WR
            MySQLUpdate.SingleWhereAndSpec<C>, //WA
            MySQLUpdate.SingleWhereSpec<C>,//SR
            MySQLUpdate.LimitSpec<C>, //OR
            Update.UpdateSpec>         //LR
            implements MySQLUpdate.SingleUpdateSpec<C>, MySQLUpdate.SinglePartitionSpec<C>
            , MySQLUpdate.SingleIndexHintSpec<C>, MySQLUpdate.SingleWhereSpec<C>, MySQLUpdate.SingleWhereAndSpec<C>
            , MySQLUpdate.OrderBySpec<C>, MySQLUpdate.IndexOrderBySpec<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }


    } // SimpleUpdate

    private static class BatchUpdate<C, WE> extends MySQLSingleUpdate<
            C,
            WE,// WE
            MySQLUpdate.BatchSingleIndexHintSpec<C>,//UR
            MySQLUpdate.BatchSinglePartitionSpec<C>,//UP
            _AsClause<BatchSingleIndexHintSpec<C>>,//PR
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

        private List<?> wrapperList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final UpdateSpec paramList(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramList(beanList);
            return this;
        }

        @Override
        public final UpdateSpec paramList(Supplier<List<?>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public final UpdateSpec paramList(Function<C, List<?>> function) {
            return this.paramList(function.apply(this.criteria));
        }

        @Override
        public final UpdateSpec paramList(Function<String, Object> function, String keyName) {
            this.wrapperList = CriteriaUtils.paramList(function, keyName);
            return this;
        }

        @Override
        public final List<?> paramList() {
            return this.wrapperList;
        }

    }//BatchUpdate


    private static final class SimpleWithAndUpdate<C> extends SimpleUpdate<C, SingleUpdateSpec<C>>
            implements SingleWithAndUpdateSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleWithAndUpdate(@Nullable C criteria) {
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

    }//SimpleWithAndUpdate

    private static final class BatchWithAndUpdate<C> extends BatchUpdate<C, BatchSingleUpdateSpec<C>>
            implements MySQLUpdate.BatchSingleWithAndUpdateSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private BatchWithAndUpdate(@Nullable C criteria) {
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

    }// BatchWithAndUpdate


}
