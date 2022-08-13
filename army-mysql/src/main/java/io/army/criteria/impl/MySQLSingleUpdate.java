package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This class is an implementation of {@link _MySQLSingleUpdate}. This class extends {@link SingleUpdate}
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdate<C, WE, UT, SR, WR, WA, OR, LR>
        extends WithCteSingleUpdate<C, SubQuery, WE, TableField, SR, WR, WA, Update>
        implements Statement._OrderByClause<C, OR>, MySQLUpdate._RowCountLimitClause<C, LR>
        , _MySQLSingleUpdate, MySQLUpdate._SingleUpdateClause<C, UT>, MySQLQuery._IndexHintForOrderByClause<C, UT>
        , _MySQLWithClause, MySQLUpdate, Update._UpdateSpec {

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

    private TableMeta<?> table;

    private String tableAlias;

    private List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;

    private List<ArmySortItem> orderByList;

    private long rowCount;

    private MySQLQuery._IndexHintForOrderByClause<C, UT> indexHintClause;

    private MySQLSingleUpdate(@Nullable C criteria) {
        super(CriteriaContexts.primarySingleDmlContext(criteria));
    }

    @Override
    public final MySQLQuery._PartitionClause<C, _AsClause<UT>> update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers, TableMeta<?> table) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);
        this.table = table;
        return MySQLSupports.partition(this.criteriaContext, this::asClauseEnd);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , TableMeta<?> table, String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::updateModifier);
        this.table = table;
        this.tableAlias = tableAlias;
        return (UT) this;
    }


    @Override
    public final MySQLQuery._PartitionClause<C, _AsClause<UT>> update(TableMeta<?> table) {
        this.table = table;
        return MySQLSupports.partition(this.criteriaContext, this::asClauseEnd);
    }

    @Override
    public final UT update(TableMeta<?> table, String tableAlias) {
        this.table = table;
        this.tableAlias = tableAlias;
        return (UT) this;
    }


    /*################################## blow IndexHintClause method ##################################*/

    @Override
    public final MySQLQuery._IndexForOrderBySpec<C, UT> useIndex() {
        return this.getIndexHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForOrderBySpec<C, UT> ignoreIndex() {
        return this.getIndexHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForOrderBySpec<C, UT> forceIndex() {
        return this.getIndexHintClause().forceIndex();
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
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addOrderByItem);
        return this.endOrderByClause(true);
    }

    @Override
    public final OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::addOrderByItem);
        return this.endOrderByClause(true);
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Supplier<?> operand) {
        final Object value;
        if ((value = operand.get()) != null) {
            this.orderByList = Collections.singletonList((ArmySortItem) operator.apply(value));
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Function<String, ?> operand, String operandKey) {
        final Object value;
        if ((value = operand.apply(operandKey)) != null) {
            this.orderByList = Collections.singletonList((ArmySortItem) operator.apply(value));
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Supplier<?> firstOperand, Supplier<?> secondOperator) {
        final Object firstValue, secondValue;
        if ((firstValue = firstOperand.get()) != null && (secondValue = secondOperator.get()) != null) {
            this.orderByList = Collections.singletonList((ArmySortItem) operator.apply(firstValue, secondValue));
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Function<String, ?> operand, String firstKey, String secondKey) {
        final Object firstValue, secondValue;
        if ((firstValue = operand.apply(firstKey)) != null && (secondValue = operand.apply(secondKey)) != null) {
            this.orderByList = Collections.singletonList((ArmySortItem) operator.apply(firstValue, secondValue));
        }
        return (OR) this;
    }

    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        consumer.accept(this::addOrderByItem);
        return this.endOrderByClause(false);
    }

    @Override
    public final OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        consumer.accept(this.criteria, this::addOrderByItem);
        return this.endOrderByClause(false);
    }


    /*################################## blow LimitClause method ##################################*/

    @Override
    public final LR limit(long rowCount) {
        this.rowCount = rowCount;
        return (LR) this;
    }

    @Override
    public final LR limit(Supplier<? extends Number> supplier) {
        this.rowCount = MySQLUtils.asLimitParam(this.criteriaContext, supplier.get());
        return (LR) this;
    }

    @Override
    public final LR limit(Function<C, ? extends Number> function) {
        this.rowCount = MySQLUtils.asLimitParam(this.criteriaContext, function.apply(this.criteria));
        return (LR) this;
    }

    @Override
    public final LR limit(Function<String, ?> function, String keyName) {
        this.rowCount = MySQLUtils.asLimitParam(this.criteriaContext, function.apply(keyName));
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Supplier<? extends Number> supplier) {
        this.rowCount = MySQLUtils.asIfLimitParam(this.criteriaContext, supplier.get());
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<C, ? extends Number> function) {
        this.rowCount = MySQLUtils.asIfLimitParam(this.criteriaContext, function.apply(this.criteria));
        return (LR) this;
    }

    @Override
    public final LR ifLimit(Function<String, ?> function, String keyName) {
        this.rowCount = MySQLUtils.asIfLimitParam(this.criteriaContext, function.apply(keyName));
        return (LR) this;
    }

    @Override
    public final String toString() {
        final String s;
        if (this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        if (this.cteList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.recursive = recursive;
        this.cteList = cteList;
    }


    @Override
    final void onAsUpdate() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
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
        if (indexHintList == null) {
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

    @Override
    final boolean isSupportRowLeftItem() {
        //false, MySQL 8.0 don't support row left item
        return false;
    }

    @Override
    final boolean isSupportMultiTableUpdate() {
        //false, this is single-table update
        return false;
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
    public final TableMeta<?> table() {
        final TableMeta<?> table = this.table;
        if (table == null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return table;
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
        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null || indexHintList instanceof ArrayList) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return indexHintList;
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
    final MySQLDialect dialect() {
        return MySQLDialect.MySQL80;
    }


    private UT asClauseEnd(List<String> partitionList, String tableAlias) {
        this.partitionList = partitionList;
        this.tableAlias = tableAlias;
        return (UT) this;
    }

    private MySQLQuery._IndexHintForOrderByClause<C, UT> getIndexHintClause() {
        MySQLQuery._IndexHintForOrderByClause<C, UT> indexHintClause = this.indexHintClause;
        if (indexHintClause == null) {
            indexHintClause = MySQLSupports.indexHintClause(this.criteriaContext, this::addIndexHint);
            this.indexHintClause = indexHintClause;
        }
        return indexHintClause;
    }

    private UT addIndexHint(final MySQLIndexHint indexHint) {
        List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            this.indexHintList = indexHintList;
        }
        indexHintList.add(indexHint);
        return (UT) this;
    }

    private void addOrderByItem(@Nullable SortItem sortItem) {
        if (sortItem == null) {
            throw CriteriaContextStack.nullPointer(this.criteriaContext);
        }
        List<ArmySortItem> itemList = this.orderByList;
        if (itemList == null) {
            this.orderByList = itemList = new ArrayList<>();
        } else if (!(itemList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        itemList.add((ArmySortItem) sortItem);
    }

    private OR endOrderByClause(final boolean required) {
        final List<ArmySortItem> itemList = this.orderByList;
        if (itemList == null) {
            if (required) {
                throw CriteriaUtils.orderByIsEmpty(this.criteriaContext);
            }
            this.orderByList = Collections.emptyList();
        } else if (itemList instanceof ArrayList) {
            this.orderByList = _CollectionUtils.unmodifiableList(itemList);
        } else {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return (OR) this;
    }


    private static final class SimpleUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate._SingleUpdateClause<C, _SingleIndexHintSpec<C>>,       // WE
            MySQLUpdate._SingleIndexHintSpec<C>,        //UR
            MySQLUpdate._SingleWhereSpec<C>,            //SR
            MySQLUpdate._OrderBySpec<C>,                //WR
            MySQLUpdate._SingleWhereAndSpec<C>,         //WA
            MySQLUpdate._LimitSpec<C>,                  //OR
            _UpdateSpec>                                //LR
            implements _SingleWithAndUpdateSpec<C>, _SingleIndexHintSpec<C>
            , _SingleWhereSpec<C>, _SingleWhereAndSpec<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }


    } // SimpleUpdate

    private static final class BatchUpdate<C> extends MySQLSingleUpdate<
            C,
            MySQLUpdate._SingleUpdateClause<C, _BatchSingleIndexHintSpec<C>>,        // WE
            MySQLUpdate._BatchSingleIndexHintSpec<C>,       //UR
            MySQLUpdate._BatchSingleWhereSpec<C>,           //SR
            MySQLUpdate._BatchOrderBySpec<C>,               //WR
            MySQLUpdate._BatchSingleWhereAndSpec<C>,        //WA
            MySQLUpdate._BatchLimitSpec<C>,                 //OR
            Statement._BatchParamClause<C, _UpdateSpec>>    //LR
            implements _BatchSingleWithAndUpdateSpec<C>, _BatchSingleIndexHintSpec<C>
            , _BatchSingleWhereSpec<C>, _BatchSingleWhereAndSpec<C>
            , _BatchDml {

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

    }//BatchUpdate



}
