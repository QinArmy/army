package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLModifier;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of {@link _MySQLSingleUpdate}. This class extends {@link SingleUpdateClause}
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdate<C, T, UT, SR, WR, WA, OR, LR>
        extends SingleUpdate<C, FieldMeta<T>, SR, WR, WA, Update>
        implements Statement._OrderByClause<C, OR>, MySQLUpdate._RowCountLimitClause<C, LR>
        , _MySQLSingleUpdate, MySQLQuery._IndexHintForOrderByClause<C, UT>
        , _MySQLWithClause, MySQLUpdate, Update._UpdateSpec {

    static <C> _SingleWithAndUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdateClause<>(criteria);
    }

    static <C> _BatchSingleWithAndUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdateClause<>(criteria);
    }


    private final boolean recursive;

    private final List<Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLModifier> modifierList;

    private final TableMeta<?> table;

    private final String tableAlias;

    private final List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;

    private List<ArmySortItem> orderByList;

    private long rowCount;

    private MySQLQuery._IndexHintForOrderByClause<C, UT> indexHintClause;

    private MySQLSingleUpdate(SingleUpdateClause<C, ?> clause) {
        super(clause.criteriaContext);

        this.recursive = clause.recursive;
        this.cteList = _CollectionUtils.safeList(clause.cteList);
        this.hintList = _CollectionUtils.safeList(clause.hintList);
        this.modifierList = _CollectionUtils.safeList(clause.modifierList);

        this.table = clause.table;
        this.tableAlias = clause.tableAlias;
        this.partitionList = _CollectionUtils.safeList(clause.partitionList);

        if (this.table == null || this.tableAlias == null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }

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
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .orderBy(consumer);
    }

    @Override
    public final OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .orderBy(consumer);
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
    final void onAsUpdate() {

        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            this.indexHintList = Collections.emptyList();
        } else {
            this.indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
        }

        if (this.orderByList == null) {
            this.orderByList = Collections.emptyList();
        }

        if (this instanceof BatchUpdate && ((BatchUpdate<C, T>) this).paramList == null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
    }

    @Override
    final void onClear() {

        this.indexHintList = null;
        this.orderByList = null;

        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C, T>) this).paramList = null;
        }
    }

    @Override
    final boolean isSupportRowLeftItem() {
        //false, MySQL 8.0 don't support row left item
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
    public final List<MySQLModifier> modifierList() {
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
        } else if (!(indexHintList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        indexHintList.add(indexHint);
        return (UT) this;
    }

    private OR orderByEnd(final List<ArmySortItem> itemList) {
        if (this.orderByList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.orderByList = itemList;
        return (OR) this;
    }


    private static final class SimpleUpdate<C, T> extends MySQLSingleUpdate<
            C,
            T,
            MySQLUpdate._SingleIndexHintSpec<C, T>,
            MySQLUpdate._SingleWhereSpec<C, T>,
            MySQLUpdate._OrderBySpec<C>,
            MySQLUpdate._SingleWhereAndSpec<C>,
            MySQLUpdate._LimitSpec<C>,
            Update._UpdateSpec> implements MySQLUpdate._SingleIndexHintSpec<C, T>
            , MySQLUpdate._SingleWhereSpec<C, T>, MySQLUpdate._SingleWhereAndSpec<C> {

        private SimpleUpdate(SimpleUpdateClause<C> clause) {
            super(clause);
        }

    }//SimpleUpdate


    private static abstract class SingleUpdateClause<C, WE> extends WithCteClause<C, SubQuery, WE> {

        private boolean recursive;

        private List<Cte> cteList;

        private List<Hint> hintList;

        private List<MySQLModifier> modifierList;

        TableMeta<?> table;

        String tableAlias;

        List<String> partitionList;

        private SingleUpdateClause(@Nullable C criteria) {
            super(CriteriaContexts.primarySingleDmlContext(criteria));
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

        @Override
        final void doWithCte(boolean recursive, List<Cte> cteList) {
            if (this.cteList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.recursive = recursive;
            this.cteList = cteList;
        }

        final void doUpdate(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, TableMeta<?> table) {
            final CriteriaContext context = this.criteriaContext;
            this.hintList = MySQLUtils.asHintList(context, hints.get(), MySQLHints::castHint);
            this.modifierList = MySQLUtils.asModifierList(context, modifiers, MySQLUtils::updateModifier);
            this.table = table;
        }


    }//SingleUpdate


    static abstract class PartitionAndAsClause<C, AR>
            extends CriteriaSupports.ParenStringConsumerClause<C, Statement._AsClause<AR>>
            implements MySQLQuery._PartitionAndAsClause<C, AR>
            , Statement._AsClause<AR> {

        private List<String> partitionList;

        PartitionAndAsClause(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

        @Override
        public final Statement._LeftParenStringQuadraOptionalSpec<C, Statement._AsClause<AR>> partition() {
            return this;
        }

        @Override
        public final AR as(final String alias) {
            final List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (!_StringUtils.hasText(alias)) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::tableAliasIsEmpty);
            }
            return this.partitionAsEnd(partitionList, alias);
        }

        @Override
        final Statement._AsClause<AR> stringConsumerEnd(List<String> stringList) {
            if (this.partitionList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.partitionList = stringList;
            return this;
        }

        abstract AR partitionAsEnd(List<String> partitionList, String alias);


    }//PartitionAndAsClause


    private static final class SimpleUpdateClause<C> extends SingleUpdateClause<C, _SingleUpdate57Clause<C>>
            implements _SingleWithAndUpdateSpec<C> {

        private SimpleUpdateClause(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <T> _SinglePartitionClause<C, T> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, SingleTableMeta<T> table) {
            this.doUpdate(hints, modifiers, table);
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <T> _SingleIndexHintSpec<C, T> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, SingleTableMeta<T> table, String tableAlias) {
            this.doUpdate(hints, modifiers, table);
            this.tableAlias = tableAlias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<C, P> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.doUpdate(hints, modifiers, table);
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<C, P> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers
                , ComplexTableMeta<P, ?> table, String tableAlias) {
            this.doUpdate(hints, modifiers, table);
            this.tableAlias = tableAlias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<C, T> update(SingleTableMeta<T> table) {
            this.table = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <T> _SingleIndexHintSpec<C, T> update(SingleTableMeta<T> table, String tableAlias) {
            this.table = table;
            this.tableAlias = tableAlias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<C, P> update(ComplexTableMeta<P, ?> table) {
            this.table = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<C, P> update(ComplexTableMeta<P, ?> table, String tableAlias) {
            this.table = table;
            this.tableAlias = tableAlias;
            return new SimpleUpdate<>(this);
        }


    } // SimpleUpdate


    private static final class SimplePartitionClause<C, T>
            extends PartitionAndAsClause<C, _SingleIndexHintSpec<C, T>>
            implements MySQLUpdate._SinglePartitionClause<C, T> {

        private final SimpleUpdateClause<C> clause;

        private SimplePartitionClause(SimpleUpdateClause<C> clause) {
            super(clause.criteriaContext);
            this.clause = clause;
        }

        @Override
        _SingleIndexHintSpec<C, T> partitionAsEnd(List<String> partitionList, String alias) {
            final SimpleUpdateClause<C> clause = this.clause;
            clause.partitionList = partitionList;
            clause.tableAlias = alias;
            return new SimpleUpdate<>(clause);
        }


    }//SimplePartitionClause


    private static final class BatchUpdateClause<C>
            extends SingleUpdateClause<C, MySQLUpdate._BatchSingleUpdate57Clause<C>>
            implements _BatchSingleWithAndUpdateSpec<C> {

        private BatchUpdateClause(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <T> _BatchSinglePartitionClause<C, T> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, SingleTableMeta<T> table) {
            this.doUpdate(hints, modifiers, table);
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<C, T> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, SingleTableMeta<T> table, String tableAlias) {
            this.doUpdate(hints, modifiers, table);
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<C, P> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.doUpdate(hints, modifiers, table);
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<C, P> update(Supplier<List<Hint>> hints, List<MySQLModifier> modifiers, ComplexTableMeta<P, ?> table, String tableAlias) {
            this.doUpdate(hints, modifiers, table);
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<C, T> update(SingleTableMeta<T> table) {
            this.table = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<C, T> update(SingleTableMeta<T> table, String tableAlias) {
            this.table = table;
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<C, P> update(ComplexTableMeta<P, ?> table) {
            this.table = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<C, P> update(ComplexTableMeta<P, ?> table, String tableAlias) {
            this.table = table;
            this.tableAlias = tableAlias;
            return new BatchUpdate<>(this);
        }


    }//BatchUpdateClause

    private static final class BatchPartitionClause<C, T>
            extends PartitionAndAsClause<C, _BatchSingleIndexHintSpec<C, T>>
            implements MySQLUpdate._BatchSinglePartitionClause<C, T> {

        private final BatchUpdateClause<C> clause;

        private BatchPartitionClause(BatchUpdateClause<C> clause) {
            super(clause.criteriaContext);
            this.clause = clause;
        }

        @Override
        _BatchSingleIndexHintSpec<C, T> partitionAsEnd(List<String> partitionList, String alias) {
            final BatchUpdateClause<C> clause = this.clause;
            clause.partitionList = partitionList;
            clause.tableAlias = alias;
            return new BatchUpdate<>(clause);
        }

    }//BatchPartitionClause

    private static final class BatchUpdate<C, T> extends MySQLSingleUpdate<
            C,
            T,
            MySQLUpdate._BatchSingleIndexHintSpec<C, T>,       //UT
            MySQLUpdate._BatchSingleWhereSpec<C, T>,           //SR
            MySQLUpdate._BatchOrderBySpec<C>,               //WR
            MySQLUpdate._BatchSingleWhereAndSpec<C>,        //WA
            MySQLUpdate._BatchLimitSpec<C>,                 //OR
            Statement._BatchParamClause<C, _UpdateSpec>>    //LR
            implements _BatchSingleIndexHintSpec<C, T>
            , _BatchSingleWhereSpec<C, T>, _BatchSingleWhereAndSpec<C>
            , _BatchDml {


        private List<?> paramList;

        private BatchUpdate(BatchUpdateClause<C> clause) {
            super(clause);
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
