package io.army.criteria.impl;

import io.army.beans.ReadWrapper;
import io.army.criteria.Statement;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard single domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
abstract class StandardUpdate<C, UR, WR, WA, SR> extends SingleUpdate<C, WR, WA, SR>
        implements Update.StandardUpdateClause<UR>, _SingleUpdate, Update.UpdateSpec {

    static <C> StandardUpdateSpec<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> StandardBatchUpdateSpec<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    private TableMeta<?> table;

    private String tableAlias;


    private StandardUpdate(@Nullable C criteria) {
        super(criteria);
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

        if (this instanceof BatchUpdate && CollectionUtils.isEmpty(((BatchUpdate<C>) this).wrapperList)) {
            throw _Exceptions.castCriteriaApi();
        }

    }

    @Override
    final void onClear() {
        this.tableAlias = null;
        if (this instanceof BatchUpdate) {
            ((BatchUpdate<C>) this).wrapperList = null;
        }

    }

    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    private static final class SimpleUpdate<C> extends StandardUpdate<
            C,
            Update.StandardSetSpec<C>,
            Update.UpdateSpec,
            Update.StandardWhereAndSpec<C>,
            Update.StandardWhereSpec<C>> implements Update.StandardUpdateSpec<C>, Update.StandardWhereAndSpec<C>
            , Update.StandardWhereSpec<C> {

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }


    }//SimpleUpdate


    private static final class BatchUpdate<C> extends StandardUpdate<
            C,
            Update.StandardBatchSetSpec<C>,
            Statement.BatchParamClause<C, Update.UpdateSpec>,
            Update.StandardBatchWhereAndSpec<C>,
            Update.StandardBatchWhereSpec<C>> implements Update.StandardBatchUpdateSpec<C>
            , Update.StandardBatchWhereSpec<C>, Update.StandardBatchWhereAndSpec<C>
            , Statement.BatchParamClause<C, Update.UpdateSpec>, _BatchDml {

        private List<ReadWrapper> wrapperList;

        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public UpdateSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public UpdateSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public UpdateSpec paramBeans(List<Object> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public UpdateSpec paramBeans(Supplier<List<Object>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public UpdateSpec paramBeans(Function<C, List<Object>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }

        @Override
        public List<ReadWrapper> wrapperList() {
            return this.wrapperList;
        }


    }//BatchUpdate


}

