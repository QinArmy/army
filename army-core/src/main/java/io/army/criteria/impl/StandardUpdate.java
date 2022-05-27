package io.army.criteria.impl;

import io.army.criteria.StandardStatement;
import io.army.criteria.TableField;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

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
@SuppressWarnings("unchecked")
abstract class StandardUpdate<C, UR, SR, WR, WA> extends SingleUpdate<C, TableField, SR, WR, WA>
        implements Update.StandardUpdateClause<UR>, _SingleUpdate, Update._UpdateSpec, StandardStatement {

    static <C> StandardUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> StandardBatchUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    private TableMeta<?> table;

    private String tableAlias;


    private StandardUpdate(@Nullable C criteria) {
        super(CriteriaContexts.primarySingleDmlContext(criteria));
    }

    @Override
    public final UR update(TableMeta<?> table, String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        this.tableAlias = tableAlias;
        return (UR) this;
    }

    @Override
    final void onAsUpdate() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        if (this instanceof BatchUpdate && ((BatchUpdate<C>) this).paramList == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    @Override
    final void onClear() {
        this.tableAlias = null;
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C>) this).paramList = null;
        }

    }


    @Override
    Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect dialect) {
        //no-op
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
    private static final class SimpleUpdate<C> extends StandardUpdate<
            C,
            Update.StandardSetSpec<C>,
            Update.StandardWhereSpec<C>,
            _UpdateSpec,
            Update.StandardWhereAndSpec<C>> implements Update.StandardUpdateSpec<C>, Update.StandardWhereAndSpec<C>
            , Update.StandardWhereSpec<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }


    }//SimpleUpdate


    /**
     * <p>
     * This class is standard batch update implementation.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    private static final class BatchUpdate<C> extends StandardUpdate<
            C,
            Update.StandardBatchSetSpec<C>,
            Update.StandardBatchWhereSpec<C>,
            _BatchParamClause<C, _UpdateSpec>,
            Update.StandardBatchWhereAndSpec<C>> implements Update.StandardBatchUpdateSpec<C>
            , Update.StandardBatchWhereSpec<C>, Update.StandardBatchWhereAndSpec<C>
            , _BatchParamClause<C, _UpdateSpec>, _BatchDml {

        private List<?> paramList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _UpdateSpec paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Function<C, List<P>> function) {
            this.paramList = CriteriaUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _UpdateSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }


    }//BatchUpdate


}

