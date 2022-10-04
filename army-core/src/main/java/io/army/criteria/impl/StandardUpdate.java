package io.army.criteria.impl;

import io.army.criteria.StandardStatement;
import io.army.criteria.TableField;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard single domain update statement.
 * </p>
 *
 * @param <C> criteria object java type
 * @since 1.0
 */

abstract class StandardUpdate<C, F extends TableField, SR, WR, WA> extends SingleUpdate<C, F, SR, WR, WA, Update>
        implements Update._UpdateSpec, StandardStatement, Update {

    static <C> _StandardSingleUpdateClause<C> simpleSingle(@Nullable C criteria) {
        return new SimpleUpdateClause<>(criteria);
    }

    static <C> _StandardBatchSingleUpdateClause<C> batchSingle(@Nullable C criteria) {
        return new BatchUpdateClause<>(criteria);
    }

    static <C> _StandardDomainUpdateClause<C> simpleDomain(@Nullable C criteria) {
        return new SimpleDomainUpdateClause<>(criteria);
    }

    static <C> _StandardBatchDomainUpdateClause<C> batchDomain(@Nullable C criteria) {
        return new BatchDomainUpdateClause<>(criteria);
    }

    private final TableMeta<?> table;

    private final String tableAlias;


    private StandardUpdate(@Nullable C criteria, TableMeta<?> table, String tableAlias) {
        super(CriteriaContexts.primarySingleDmlContext(criteria));
        this.table = table;
        this.tableAlias = tableAlias;

        ContextStack.setContextStack(this.context);
    }

    @Override
    public final String toString() {
        final String s;
        if (this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    final void onAsUpdate() {
        if (this.table == null || this.tableAlias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        if (this instanceof BatchUpdate && ((BatchUpdate<C, F>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
    }

    @Override
    final void onClear() {
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C, F>) this).paramList = null;
        }

    }


    @Override
    final boolean isSupportRowLeftItem() {
        // false ,standard don't support row left item
        return false;
    }
    @Override
    final MySQLDialect dialect() {
        // no dialect
        return null;
    }

    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    /**
     * <p>
     * This class is standard update implementation.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    private static class SimpleUpdate<C, F extends TableField> extends StandardUpdate<
            C,
            F,
            _StandardWhereSpec<C, F>,
            _UpdateSpec,
            _StandardWhereAndSpec<C>>
            implements _StandardWhereAndSpec<C>, _StandardWhereSpec<C, F> {

        private SimpleUpdate(@Nullable C criteria, TableMeta<?> table, String tableAlias) {
            super(criteria, table, tableAlias);
        }


    }//SimpleUpdate

    private static class SimpleDomainUpdate<C, T> extends SimpleUpdate<C, FieldMeta<? super T>>
            implements _DomainUpdate {

        private SimpleDomainUpdate(@Nullable C criteria, TableMeta<T> table, String tableAlias) {
            super(criteria, table, tableAlias);
        }


    }//SimpleDomainUpdate


    /**
     * <p>
     * This class is standard batch update implementation.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    private static class BatchUpdate<C, F extends TableField> extends StandardUpdate<
            C,
            F,
            _StandardBatchWhereSpec<C, F>,
            _BatchParamClause<C, _UpdateSpec>,
            _StandardBatchWhereAndSpec<C>>
            implements _StandardBatchWhereSpec<C, F>, _StandardBatchWhereAndSpec<C>
            , _BatchParamClause<C, _UpdateSpec>, _BatchDml {

        private List<?> paramList;

        private BatchUpdate(@Nullable C criteria, TableMeta<?> table, String tableAlias) {
            super(criteria, table, tableAlias);
        }

        @Override
        public final <P> _UpdateSpec paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public final <P> _UpdateSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public final <P> _UpdateSpec paramList(Function<C, List<P>> function) {
            this.paramList = CriteriaUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public final _UpdateSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public final List<?> paramList() {
            return this.paramList;
        }


    }//BatchUpdate


    private static final class BatchDomainUpdate<C, T> extends BatchUpdate<C, FieldMeta<? super T>>
            implements _DomainUpdate {

        private BatchDomainUpdate(@Nullable C criteria, TableMeta<T> table, String tableAlias) {
            super(criteria, table, tableAlias);
        }

    }//BatchDomainUpdate


    private static final class SimpleUpdateClause<C> implements _StandardSingleUpdateClause<C> {

        private final C criteria;

        private SimpleUpdateClause(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T> _StandardSetClause<C, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias) {
            return new SimpleUpdate<>(this.criteria, table, tableAlias);
        }

        @Override
        public <P> _StandardSetClause<C, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias) {
            return new SimpleUpdate<>(this.criteria, table, tableAlias);
        }

    }//SimpleUpdateClause

    private static final class SimpleDomainUpdateClause<C> implements Update._StandardDomainUpdateClause<C> {

        private final C criteria;

        private SimpleDomainUpdateClause(@Nullable C criteria) {
            this.criteria = criteria;
        }


        @Override
        public <T> _StandardSetClause<C, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias) {
            return new SimpleDomainUpdate<>(this.criteria, table, tableAlias);
        }


    }//SimpleDomainUpdateClause


    private static final class BatchUpdateClause<C> implements _StandardBatchSingleUpdateClause<C> {

        private final C criteria;

        private BatchUpdateClause(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T> _StandardBatchSetClause<C, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias) {
            return new BatchUpdate<>(this.criteria, table, tableAlias);
        }

        @Override
        public <P> _StandardBatchSetClause<C, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias) {
            return new BatchUpdate<>(this.criteria, table, tableAlias);
        }

    }//BatchUpdateClause


    private static final class BatchDomainUpdateClause<C> implements Update._StandardBatchDomainUpdateClause<C> {

        private final C criteria;

        private BatchDomainUpdateClause(@Nullable C criteria) {
            this.criteria = criteria;
        }


        @Override
        public <T> _StandardBatchSetClause<C, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias) {
            return new BatchDomainUpdate<>(this.criteria, table, tableAlias);
        }


    }//BatchDomainUpdateClause


}

