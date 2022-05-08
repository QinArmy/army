package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.StandardStatement;
import io.army.criteria.impl.inner._BatchDml;
import io.army.dialect.Dialect;
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
 * @param <C> criteria object java type
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class StandardDelete<C, DR, WR, WA> extends SingleDelete<C, WR, WA>
        implements Delete.StandardDeleteClause<DR>, StandardStatement {

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
    }

    @Override
    public final DR deleteFrom(TableMeta<?> table, String tableAlias) {
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
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).paramList == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    @Override
    void onClear() {
        this.tableAlias = null;
        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).paramList = null;
        }
    }

    @Override
    public final TableMeta<?> table() {
        this.prepared();
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
            _DeleteSpec,
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
            _BatchParamClause<C, _DeleteSpec>,
            Delete.StandardBatchWhereAndSpec<C>>
            implements Delete.StandardBatchWhereAndSpec<C>, Delete.StandardBatchWhereSpec<C>
            , _BatchParamClause<C, _DeleteSpec>, Delete.StandardBatchDeleteSpec<C>, _BatchDml {

        private List<?> paramList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public _DeleteSpec paramList(List<?> paramList) {
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public _DeleteSpec paramList(Supplier<List<?>> supplier) {
            this.paramList = CriteriaUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public _DeleteSpec paramList(Function<C, List<?>> function) {
            this.paramList = CriteriaUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _DeleteSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    }// BatchDelete


}
