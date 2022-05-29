package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of {@link _MySQLSingleUpdate}. This class extends {@link SingleUpdate}
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdate<C, WE, UR, UP, IR, SR, WR, WA, OR, LR>
        extends WithCteSingleUpdate<C, SubQuery, WE, TableField, SR, WR, WA>
        implements Statement._OrderByClause<C, OR>, MySQLUpdate._RowCountLimitClause<C, LR>
        , _MySQLSingleUpdate, MySQLUpdate._SingleUpdateClause<C, UR, UP>, MySQLQuery._IndexHintClause<C, IR, UR>
        , MySQLQuery._IndexForOrderByClause<C, UR>, _MySQLWithClause {

    static <C> _SingleWithAndUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> _BatchSingleWithAndUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }


    private boolean recursive;

    private List<Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLWords> modifierList;

    private SingleTableMeta<?> table;

    private String tableAlias;

    private List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;

    private MySQLIndexHint.Command command;

    private List<ArmySortItem> orderByList;

    private long rowCount;

    private MySQLSingleUpdate(@Nullable C criteria) {
        super(CriteriaContexts.primarySingleDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, SingleTableMeta<?> table) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.table = table;
        return this.createPartitionClause();
    }

    @Override
    public final UP update(Function<C, List<Hint>> hints, List<MySQLWords> modifiers, SingleTableMeta<?> table) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.table = table;
        return this.createPartitionClause();
    }

    @Override
    public final UR update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , SingleTableMeta<?> table, String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.table = table;
        this.tableAlias = tableAlias;
        return (UR) this;
    }

    @Override
    public final UR update(Function<C, List<Hint>> hints, List<MySQLWords> modifiers, SingleTableMeta<?> table
            , String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.table = table;
        this.tableAlias = tableAlias;
        return (UR) this;
    }

    @Override
    public final UP update(SingleTableMeta<?> table) {
        this.table = table;
        return this.createPartitionClause();
    }

    @Override
    public final UR update(SingleTableMeta<?> table, String tableAlias) {
        this.table = table;
        this.tableAlias = tableAlias;
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
            this.command = MySQLIndexHint.Command.USER_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
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
        if (indexList != null && indexList.size() > 0) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (indexList != null && indexList.size() > 0) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
        }
        return (UR) this;
    }

    @Override
    public final UR ifForceIndex(Function<C, List<String>> function) {
        final List<String> indexList;
        indexList = function.apply(this.criteria);
        if (indexList != null && indexList.size() > 0) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
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
        final MySQLIndexHint.Command command = this.command;
        if (command != null) {
            this.command = null;
            final List<String> list;
            list = function.apply(this.criteria);
            if (list != null && list.size() > 0) {
                this.addIndexHint(command, true, list);
            }
        }
        return (UR) this;
    }



    /*################################## blow OrderByClause method ##################################*/

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
    /*################################## blow LimitClause method ##################################*/

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

    abstract UP createPartitionClause();

    final UR afterAs(@Nullable String tableAlias, @Nullable List<String> partitionList) {
        assert tableAlias != null;
        if (this.table == null || this.tableAlias != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAlias = tableAlias;
        this.partitionList = partitionList;
        return (UR) this;
    }

    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        if (this.cteList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.recursive = recursive;
        this.cteList = cteList;
    }


    @Override
    final void onAsUpdate() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.command = null;
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this.partitionList == null) {
            this.partitionList = Collections.emptyList();
        }
        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null || indexHintList.size() == 0) {
            this.indexHintList = Collections.emptyList();
        } else {
            this.indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
        }
        if (this.orderByList == null) {
            this.orderByList = Collections.emptyList();
        }

        if (this instanceof BatchUpdate && ((BatchUpdate<C>) this).paramList == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;

        this.indexHintList = null;
        this.orderByList = null;

        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C>) this).paramList = null;
        }
    }



    /*################################## blow _MySQLSingleUpdate method ##################################*/

    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public List<Cte> cteList() {
        return this.cteList;
    }

    @Override
    public final SingleTableMeta<?> table() {
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
    public final List<MySQLWords> modifierList() {
        return this.modifierList;
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
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
    }

    @Override
    final Dialect dialect() {
        return Dialect.MySQL80;
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


    private static final class SimpleUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate._SingleUpdate57Clause<C>,       // WE
            MySQLUpdate._SingleIndexHintSpec<C>,        //UR
            MySQLUpdate._SinglePartitionClause<C>,      //UP
            MySQLUpdate._IndexForOrderBy57Clause<C>,    //IR
            MySQLUpdate._SingleWhereSpec<C>,            //SR
            MySQLUpdate._OrderBySpec<C>,                //WR
            MySQLUpdate._SingleWhereAndSpec<C>,         //WA
            MySQLUpdate._LimitSpec<C>,                  //OR
            _UpdateSpec>                                //LR
            implements _SingleWithAndUpdateSpec<C>, _SingleIndexHintSpec<C>
            , _SingleWhereSpec<C>, _SingleWhereAndSpec<C>
            , _OrderBySpec<C>, _IndexForOrderBy57Clause<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        _SinglePartitionClause<C> createPartitionClause() {
            return new SimplePartitionClause<>(this);
        }

    } // SimpleUpdate

    private static final class BatchUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate._BatchSingleUpdateClause<C>,        // WE
            MySQLUpdate._BatchSingleIndexHintSpec<C>,       //UR
            MySQLUpdate._BatchSinglePartitionClause<C>,     //UP
            MySQLUpdate._BatchIndexForOrderByClause<C>,     //IR
            MySQLUpdate._BatchSingleWhereSpec<C>,           //SR
            MySQLUpdate._BatchOrderBySpec<C>,               //WR
            MySQLUpdate._BatchSingleWhereAndSpec<C>,        //WA
            MySQLUpdate._BatchLimitSpec<C>,                 //OR
            Statement._BatchParamClause<C, _UpdateSpec>>    //LR
            implements _BatchSingleWithAndUpdateSpec<C>, _BatchSingleIndexHintSpec<C>
            , _BatchIndexForOrderByClause<C>, _BatchSingleWhereSpec<C>, _BatchSingleWhereAndSpec<C>
            , _BatchOrderBySpec<C>, _BatchParamClause<C, _UpdateSpec>, _BatchDml {

        private List<?> paramList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _UpdateSpec paramList(List<P> paramList) {
            this.paramList = MySQLUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = MySQLUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Function<C, List<P>> function) {
            this.paramList = MySQLUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _UpdateSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = MySQLUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

        @Override
        _BatchSinglePartitionClause<C> createPartitionClause() {
            return new BatchPartitionClause<>(this);
        }

    }//BatchUpdate


    private static final class SimplePartitionClause<C>
            extends MySQLPartitionClause<C, _AsClause<_SingleIndexHintSpec<C>>>
            implements _AsClause<_SingleIndexHintSpec<C>>, MySQLUpdate._SinglePartitionClause<C> {

        private final SimpleUpdate<C> stmt;

        public SimplePartitionClause(SimpleUpdate<C> stmt) {
            super(stmt.criteria);
            this.stmt = stmt;
        }

        @Override
        public _SingleIndexHintSpec<C> as(final String alias) {
            return this.stmt.afterAs(alias, this.partitionList);
        }

    }//SimplePartitionClause

    private static final class BatchPartitionClause<C>
            extends MySQLPartitionClause<C, _AsClause<_BatchSingleIndexHintSpec<C>>>
            implements _AsClause<_BatchSingleIndexHintSpec<C>>, MySQLUpdate._BatchSinglePartitionClause<C> {

        private final BatchUpdate<C> stmt;

        private BatchPartitionClause(BatchUpdate<C> stmt) {
            super(stmt.criteria);
            this.stmt = stmt;
        }

        @Override
        public _BatchSingleIndexHintSpec<C> as(final String alias) {
            return this.stmt.afterAs(alias, this.partitionList);
        }

    }//BatchPartitionClause


}
