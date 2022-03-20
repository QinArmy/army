package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._BatchDml;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to crate dynamic delete and sub query
 */
@SuppressWarnings("unchecked")
abstract class StandardDelete<C, DR, WR, WA> extends SingleDelete<C, WR, WA>
        implements Delete.StandardDeleteClause<DR> {

    static <C> StandardDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> StandardBatchDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }


    private TableMeta<?> table;

    private String tableAlias;

    private StandardDelete(@Nullable C criteria) {
        super(CriteriaContexts.singleDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final DR deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    void onAsDelete() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).wrapperList == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    @Override
    void onClear() {
        this.tableAlias = null;
        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).wrapperList = null;
        }
    }

    @Override
    public final TableMeta<?> table() {
        prepared();
        return this.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    void validateDialect(Dialect dialect) {
        //no-op
    }

    /*################################## blow static inner class ##################################*/

    private static final class SimpleDelete<C> extends StandardDelete<
            C,
            Delete.StandardWhereSpec<C>,
            Delete.DeleteSpec,
            Delete.StandardWhereAndSpec<C>>
            implements Delete.StandardWhereSpec<C>, Delete.StandardWhereAndSpec<C>
            , Delete.StandardDeleteSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }


    }//SimpleDelete

    private static final class BatchDelete<C> extends StandardDelete<
            C,
            Delete.StandardBatchWhereSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>,
            Delete.StandardBatchWhereAndSpec<C>>
            implements Delete.StandardBatchWhereAndSpec<C>, Delete.StandardBatchWhereSpec<C>
            , Statement.BatchParamClause<C, Delete.DeleteSpec>, Delete.StandardBatchDeleteSpec<C>, _BatchDml {

        private List<?> wrapperList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public DeleteSpec paramList(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramList(beanList);
            return this;
        }

        @Override
        public DeleteSpec paramList(Supplier<List<?>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public DeleteSpec paramList(Function<C, List<?>> function) {
            return this.paramList(function.apply(this.criteria));
        }

        @Override
        public DeleteSpec paramList(Function<String, Object> function, String keyName) {
            this.wrapperList = CriteriaUtils.paramList(function, keyName);
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.wrapperList;
        }

    }// BatchDelete


}
