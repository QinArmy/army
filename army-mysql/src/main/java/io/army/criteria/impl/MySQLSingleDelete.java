package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLModifier;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLSingleDelete.SimpleDelete}</li>
 *         <li>{@link MySQLSingleDelete.BatchDelete}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleDelete<C, WE, DR, PR, WR, WA, OR, LR> extends WithCteSingleDelete<C, WE, WR, WA>
        implements Query.OrderByClause<C, OR>, MySQLUpdate.LimitClause<C, LR>, MySQLQuery.PartitionClause<C, PR>
        , _MySQLSingleDelete, MySQLDelete.SingleDeleteClause<C, DR>, MySQLDelete.SingleDeleteFromClause<DR> {

    static <C> MySQLDelete.SingleDeleteSpec<C> simple57(@Nullable C criteria) {
        final SimpleDelete<C, Void> delete;
        delete = new SimpleDelete<>(criteria);
        return delete;
    }

    static <C> MySQLDelete.BatchSingleDeleteSpec<C> batch57(@Nullable C criteria) {
        final BatchDelete<C, Void> delete;
        delete = new BatchDelete<>(criteria);
        return delete;
    }

    static <C> MySQLDelete.SingleDelete80Spec<C> simple80(@Nullable C criteria) {
        return new SimpleDelete80<>(criteria);
    }

    static <C> MySQLDelete.BatchSingleDelete80Spec<C> batch80(@Nullable C criteria) {
        return new BatchDelete80<>(criteria);
    }

    private List<Hint> hintList;

    private List<MySQLModifier> modifierList;

    private SingleTableMeta<? extends IDomain> table;

    private List<String> partitionList;

    private List<SortItem> orderByList;

    private long rowCount = -1L;


    private MySQLSingleDelete(@Nullable C criteria) {
        super(CriteriaContexts.singleDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }


    @Override
    public final MySQLDelete.SingleDeleteFromClause<DR> delete(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers) {
        this.hintList = _CollectionUtils.asUnmodifiableList(hints.get());
        this.modifierList = _CollectionUtils.asUnmodifiableList(modifiers);
        return this;
    }

    @Override
    public final MySQLDelete.SingleDeleteFromClause<DR> delete(Function<C, List<Hint>> hints, List<MySQLModifier> modifiers) {
        this.hintList = _CollectionUtils.asUnmodifiableList(hints.apply(this.criteria));
        this.modifierList = _CollectionUtils.asUnmodifiableList(modifiers);
        return this;
    }

    @Override
    public final DR deleteFrom(SingleTableMeta<? extends IDomain> table) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        return (DR) this;
    }

    @Override
    public final DR from(SingleTableMeta<? extends IDomain> table) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        return (DR) this;
    }

    @Override
    public final PR partition(String partitionName) {
        this.partitionList = Collections.singletonList(partitionName);
        return (PR) this;
    }

    @Override
    public final PR partition(String partitionName1, String partitionNam2) {
        this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2);
        return (PR) this;
    }

    @Override
    public final PR partition(List<String> partitionNameList) {
        if (_CollectionUtils.isEmpty(partitionNameList)) {
            throw new CriteriaException("partitionNameList must not empty.");
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

    /*################################## blow _MySQLSingleDelete method ##################################*/

    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLModifier> modifierList() {
        return this.modifierList;
    }

    @Override
    public final SingleTableMeta<?> table() {
        final SingleTableMeta<?> table = this.table;
        assert table != null;
        return table;
    }

    @Override
    public final String tableAlias() {
        return "t";
    }


    @Override
    public final List<SortItem> orderByList() {
        prepared();
        return this.orderByList;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    @Override
    final void onAsDelete() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this.table == null) {
            throw _Exceptions.castCriteriaApi();
        }
        final List<String> partitionList = this.partitionList;
        if (_CollectionUtils.isEmpty(partitionList)) {
            this.partitionList = Collections.emptyList();
        } else {
            this.partitionList = partitionList;
        }

        final List<SortItem> orderByList = this.orderByList;
        if (_CollectionUtils.isEmpty(orderByList)) {
            this.orderByList = Collections.emptyList();
        } else {
            this.orderByList = orderByList;
        }

        if (this instanceof BatchDelete) {
            final List<?> wrapperList = ((BatchDelete<C, ?>) this).wrapperList;
            if (_CollectionUtils.isEmpty(wrapperList)) {
                throw _Exceptions.batchParamEmpty();
            }
        }


    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.partitionList = null;
        this.orderByList = null;
        this.rowCount = -1L;

        if (this instanceof BatchDelete) {
            ((BatchDelete<C, ?>) this).wrapperList = null;
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

    /*################################## blow inner class ##################################*/

    private static class SimpleDelete<C, WE> extends MySQLSingleDelete<
            C,
            WE,
            MySQLDelete.SinglePartitionSpec<C>,
            MySQLDelete.SingleWhereSpec<C>,
            MySQLDelete.OrderBySpec<C>,
            MySQLDelete.SingleWhereAndSpec<C>,
            MySQLDelete.LimitSpec<C>,
            Delete.DeleteSpec>
            implements MySQLDelete.SinglePartitionSpec<C>, MySQLDelete.SingleWhereAndSpec<C>
            , MySQLDelete.SingleDeleteSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);

        }

    }//SimpleDelete

    private static class BatchDelete<C, WE> extends MySQLSingleDelete<
            C,
            WE,
            MySQLDelete.BatchSinglePartitionSpec<C>,
            MySQLDelete.BatchSingleWhereSpec<C>,
            MySQLDelete.BatchOrderBySpec<C>,
            MySQLDelete.BatchSingleWhereAndSpec<C>,
            MySQLDelete.BatchLimitSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>>
            implements MySQLDelete.BatchSinglePartitionSpec<C>, MySQLDelete.BatchSingleWhereAndSpec<C>
            , MySQLDelete.BatchSingleDeleteSpec<C>, _BatchDml {

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
        public final List<?> paramList() {
            return this.wrapperList;
        }

    }//BatchDelete


    private static final class SimpleDelete80<C> extends SimpleDelete<C, MySQLDelete.SingleDeleteSpec<C>>
            implements _MySQLWithClause, MySQLDelete.SingleDelete80Spec<C> {

        private boolean recursive;

        private List<Cte> cteList;

        private SimpleDelete80(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

    }//SimpleDelete80


    private static final class BatchDelete80<C> extends BatchDelete<C, MySQLDelete.BatchSingleDeleteSpec<C>>
            implements _MySQLWithClause, MySQLDelete.BatchSingleDelete80Spec<C> {

        private boolean recursive;

        private List<Cte> cteList;

        private BatchDelete80(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

    }//BatchDelete80


}
