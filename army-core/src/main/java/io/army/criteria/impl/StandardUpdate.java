package io.army.criteria.impl;

import io.army.criteria.StandardStatement;
import io.army.criteria.Statement;
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
abstract class StandardUpdate<C, UR, SR, WR, WA> extends SingleUpdate<C, SR, WR, WA>
        implements Update.StandardUpdateClause<UR>, _SingleUpdate, Update.UpdateSpec, StandardStatement {

    static <C> StandardUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> StandardBatchUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    private TableMeta<?> table;

    private String tableAlias;


    private StandardUpdate(@Nullable C criteria) {
        super(CriteriaContexts.singleDmlContext(criteria));
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
    void validateDialect(Dialect dialect) {
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
            Update.UpdateSpec,
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
            Statement.BatchParamClause<C, Update.UpdateSpec>,
            Update.StandardBatchWhereAndSpec<C>> implements Update.StandardBatchUpdateSpec<C>
            , Update.StandardBatchWhereSpec<C>, Update.StandardBatchWhereAndSpec<C>
            , Statement.BatchParamClause<C, Update.UpdateSpec>, _BatchDml {

        private List<?> paramList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public UpdateSpec paramList(List<?> paramList) {
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public UpdateSpec paramList(Supplier<List<?>> supplier) {
            this.paramList = CriteriaUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public UpdateSpec paramList(Function<C, List<?>> function) {
            this.paramList = CriteriaUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public UpdateSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }


    }//BatchUpdate


}

