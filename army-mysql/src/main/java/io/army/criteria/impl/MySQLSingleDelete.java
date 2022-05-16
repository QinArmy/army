package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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
abstract class MySQLSingleDelete<C, WE, DS, PR, WR, WA, OR, LR> extends WithCteSingleDelete<C, SubQuery, WE, WR, WA>
        implements Statement._OrderByClause<C, OR>, MySQLUpdate._RowCountLimitClause<C, LR>
        , MySQLQuery._PartitionClause<C, PR>, _MySQLSingleDelete, MySQLDelete._MySQLSingleDeleteClause<C, DS>
        , MySQLDelete._SingleDeleteFromClause<DS>, _MySQLWithClause {


    static <C> _WithAndSingleDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> _BatchWithAndSingleDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }

    private boolean recursive;

    private List<Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLWords> modifierList;

    private SingleTableMeta<?> table;

    private String alias;

    private List<String> partitionList;

    private List<ArmySortItem> orderByList;

    private long rowCount = -1L;


    private MySQLSingleDelete(@Nullable C criteria) {
        super(CriteriaContexts.primarySingleDmlContext(criteria));
    }


    @Override
    public final _SingleDeleteFromClause<DS> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isDeleteModifier);
        return this;
    }

    @Override
    public final _SingleDeleteFromClause<DS> delete(Function<C, List<Hint>> hints, List<MySQLWords> modifiers) {
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isDeleteModifier);
        return this;
    }

    @Override
    public final DS deleteFrom(SingleTableMeta<?> table, String alias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        this.alias = alias;
        return (DS) this;
    }

    @Override
    public final DS from(SingleTableMeta<?> table, String alias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        this.alias = alias;
        return (DS) this;
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
    public final PR partition(String partitionName1, String partitionNam2, String partitionNam3) {
        this.partitionList = ArrayUtils.asUnmodifiableList(partitionName1, partitionNam2, partitionNam3);
        return (PR) this;
    }

    @Override
    public final PR partition(Supplier<List<String>> supplier) {
        this.partitionList = MySQLUtils.asStringList(supplier.get(), MySQLUtils::partitionListIsEmpty);
        return (PR) this;
    }

    @Override
    public final PR partition(Function<C, List<String>> function) {
        this.partitionList = MySQLUtils.asStringList(function.apply(this.criteria), MySQLUtils::partitionListIsEmpty);
        return (PR) this;
    }

    @Override
    public final PR partition(Consumer<List<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list);
        this.partitionList = MySQLUtils.asStringList(list, MySQLUtils::partitionListIsEmpty);
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Supplier<List<String>> supplier) {
        final List<String> list;
        list = supplier.get();
        if (list != null && list.size() > 0) {
            this.partitionList = MySQLUtils.asStringList(list, MySQLUtils::partitionListIsEmpty);
        }
        return (PR) this;
    }

    @Override
    public final PR ifPartition(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (list != null && list.size() > 0) {
            this.partitionList = MySQLUtils.asStringList(list, MySQLUtils::partitionListIsEmpty);
        }
        return (PR) this;
    }


    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR orderBy(Function<C, List<S>> function) {
        this.orderByList = MySQLUtils.asSortItemList(function.apply(this.criteria));
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR orderBy(Supplier<List<S>> supplier) {
        this.orderByList = MySQLUtils.asSortItemList(supplier.get());
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<List<SortItem>> consumer) {
        final List<SortItem> sortItemList = new ArrayList<>();
        consumer.accept(sortItemList);
        this.orderByList = MySQLUtils.asSortItemList(sortItemList);
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR ifOrderBy(Supplier<List<S>> supplier) {
        final List<S> sortItemList;
        sortItemList = supplier.get();
        if (sortItemList != null && sortItemList.size() > 0) {
            this.orderByList = MySQLUtils.asSortItemList(sortItemList);
        }
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR ifOrderBy(Function<C, List<S>> function) {
        final List<S> sortItemList;
        sortItemList = function.apply(this.criteria);
        if (sortItemList != null && sortItemList.size() > 0) {
            this.orderByList = MySQLUtils.asSortItemList(sortItemList);
        }
        return (OR) this;
    }

    @Override
    public final LR limit(long rowCount) {
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<? extends Number> supplier) {
        this.rowCount = MySQLUtils.asRowCount(supplier.get());
        return (LR) this;
    }

    @Override
    public final LR limit(Function<C, ? extends Number> function) {
        this.rowCount = MySQLUtils.asRowCount(function.apply(this.criteria));
        return (LR) this;
    }

    @Override
    public final LR limit(Function<String, ?> function, String keyName) {
        this.rowCount = MySQLUtils.asRowCount(function.apply(keyName));
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<? extends Number> supplier) {
        this.rowCount = MySQLUtils.asIfRowCount(supplier.get());
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<C, ? extends Number> function) {
        this.rowCount = MySQLUtils.asIfRowCount(function.apply(this.criteria));
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<String, ?> function, String keyName) {
        this.rowCount = MySQLUtils.asIfRowCount(function.apply(keyName));
        return (LR) this;
    }

    /*################################## blow _MySQLSingleDelete method ##################################*/

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
    public final SingleTableMeta<?> table() {
        final SingleTableMeta<?> table = this.table;
        assert table != null;
        return table;
    }

    @Override
    public final String tableAlias() {
        return this.alias;
    }


    @Override
    public final List<? extends SortItem> orderByList() {
        return this.orderByList;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        this.recursive = recursive;
        this.cteList = cteList;
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
        if (this.table == null || this.alias == null) {
            throw _Exceptions.castCriteriaApi();
        }

        if (this.partitionList == null) {
            this.partitionList = Collections.emptyList();
        }
        if (this.orderByList == null) {
            this.orderByList = Collections.emptyList();
        }
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).paramList == null) {
            throw _Exceptions.batchParamEmpty();
        }


    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;

        this.orderByList = null;
        this.rowCount = -1L;

        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).paramList = null;
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

    private static final class SimpleDelete<C> extends MySQLSingleDelete<
            C,
            MySQLDelete._SingleDelete57Clause<C>,
            MySQLDelete._SinglePartitionSpec<C>,
            MySQLDelete._SingleWhereClause<C>,
            MySQLDelete._OrderBySpec<C>,
            MySQLDelete._SingleWhereAndSpec<C>,
            MySQLDelete._LimitSpec<C>,
            Delete._DeleteSpec>
            implements MySQLDelete._WithAndSingleDeleteSpec<C>, MySQLDelete._SinglePartitionSpec<C>
            , MySQLDelete._SingleWhereAndSpec<C>, MySQLDelete._OrderBySpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);

        }


    }//SimpleDelete

    private static final class BatchDelete<C> extends MySQLSingleDelete<
            C,
            MySQLDelete._BatchSingleDeleteClause<C>,
            MySQLDelete._BatchSinglePartitionSpec<C>,
            MySQLDelete._BatchSingleWhereClause<C>,
            MySQLDelete._BatchOrderBySpec<C>,
            MySQLDelete._BatchSingleWhereAndSpec<C>,
            MySQLDelete._BatchLimitSpec<C>,
            Statement._BatchParamClause<C, Delete._DeleteSpec>>
            implements MySQLDelete._BatchWithAndSingleDeleteSpec<C>, MySQLDelete._BatchSinglePartitionSpec<C>
            , MySQLDelete._BatchSingleWhereAndSpec<C>, MySQLDelete._BatchOrderBySpec<C>, _BatchDml {

        private List<?> paramList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);

        }

        @Override
        public <P> _DeleteSpec paramList(List<P> paramList) {
            this.paramList = MySQLUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = MySQLUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Function<C, List<P>> function) {
            this.paramList = MySQLUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _DeleteSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = MySQLUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }


    }//BatchDelete


}
