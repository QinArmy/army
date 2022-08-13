package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.function.*;

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
abstract class MySQLSingleDelete<C, WE, DT, PR, WR, WA, OR, LR>
        extends WithCteSingleDelete<C, SubQuery, WE, WR, WA, Delete>
        implements Statement._OrderByClause<C, OR>, MySQLUpdate._RowCountLimitClause<C, LR>
        , MySQLQuery._PartitionClause<C, PR>, _MySQLSingleDelete, MySQLDelete._MySQLSingleDeleteClause<C, DT>
        , MySQLDelete._SingleDeleteFromClause<DT>, _MySQLWithClause, MySQLDelete, Delete._DeleteSpec {


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
    public final _SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::deleteModifier);
        return this;
    }

    @Override
    public final _SingleDeleteFromClause<DT> delete(Function<C, List<Hint>> hints, List<MySQLWords> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.criteriaContext, hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::deleteModifier);
        return this;
    }

    @Override
    public final DT deleteFrom(SingleTableMeta<?> table, String alias) {
        this.table = table;
        this.alias = alias;
        return (DT) this;
    }

    @Override
    public final DT from(SingleTableMeta<?> table, String alias) {
        this.table = table;
        this.alias = alias;
        return (DT) this;
    }

    @Override
    public final _LeftParenStringQuadraOptionalSpec<C, PR> partition() {
        return CriteriaSupports.stringQuadra(this.criteriaContext, this::partitionEnd);
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
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .orderBy(consumer);
    }

    @Override
    public final OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .orderBy(consumer);
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Supplier<?> operand) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, operand);
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Function<String, ?> operand, String operandKey) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, operand, operandKey);
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Supplier<?> firstOperand, Supplier<?> secondOperator) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, firstOperand, secondOperator);
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Function<String, ?> operand, String firstKey, String secondKey) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, operand, firstKey, secondKey);
    }

    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(consumer);
    }

    @Override
    public final OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(consumer);
    }

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
    public final List<String> partitionList() {
        return this.partitionList;
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

    /**
     * @see #partition()
     */
    private PR partitionEnd(List<String> partitionList) {
        if (this.partitionList == null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.partitionList = partitionList;
        return (PR) this;
    }


    private OR orderByEnd(List<ArmySortItem> itemList) {
        if (this.orderByList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.orderByList = itemList;
        return (OR) this;
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
            , MySQLDelete._SingleWhereAndSpec<C> {

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
            , MySQLDelete._BatchSingleWhereAndSpec<C>, _BatchDml {

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
